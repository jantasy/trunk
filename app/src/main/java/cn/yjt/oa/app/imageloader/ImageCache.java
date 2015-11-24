package cn.yjt.oa.app.imageloader;

import android.graphics.Bitmap;

/**
 * 图片缓存接口
 */
public interface ImageCache {
	
	public void putBitmap(String uri,Bitmap bitmap);
	
	public Bitmap getBitmap(String uri);
	
	public void trim();
}
