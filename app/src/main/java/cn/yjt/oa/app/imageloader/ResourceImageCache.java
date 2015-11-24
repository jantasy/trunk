package cn.yjt.oa.app.imageloader;

import android.graphics.Bitmap;
import android.util.SparseArray;

public class ResourceImageCache extends SparseArray<Bitmap> {
	
	@Override
	public void clear() {
		int size = size();
		for (int i = 0; i < size; i++) {
			valueAt(i).recycle();
		}
		super.clear();
	}
}
