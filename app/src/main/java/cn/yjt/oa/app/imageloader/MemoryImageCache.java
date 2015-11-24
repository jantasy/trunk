package cn.yjt.oa.app.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class MemoryImageCache extends LruCache<String, Bitmap> implements
		ImageCache {
	static final int MAX_SIZE = (int) (Runtime.getRuntime().maxMemory() / 8);

	public MemoryImageCache(Context context) {
		super(MAX_SIZE);
	}

	@Override
	public Bitmap getBitmap(String key) {
		Bitmap bitmap = get(key);
		if (bitmap != null && bitmap.isRecycled()) {
			remove(key);
			return null;
		}
		return bitmap;
	}

	@Override
	public void putBitmap(String key, Bitmap value) {
		put(key, value);
	}

	@Override
	public void trim() {
		super.trimToSize(0);
	}

}
