package cn.yjt.oa.app.http;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;

public class BitmapCache extends LruCache<String, Bitmap> {

	private static final BitmapCache instance = new BitmapCache((int)(Runtime.getRuntime().maxMemory() / 8));
	
	public static BitmapCache globalBitmapCache() {
		return instance;
	}
	
	public BitmapCache(int maxSize) {
		super(maxSize);
	}
	
	@Override
	protected int sizeOf(String key, Bitmap value) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
			return value.getByteCount();
		else
			return value.getRowBytes() * value.getHeight();
	}

}
