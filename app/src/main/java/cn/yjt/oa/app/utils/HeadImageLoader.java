package cn.yjt.oa.app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import cn.yjt.oa.app.imageloader.ImageCache;
import cn.yjt.oa.app.imageloader.ImageLoader;
import cn.yjt.oa.app.imageloader.ImageSizeAdapter;

public class HeadImageLoader extends ImageLoader {

	public HeadImageLoader(Context context, ImageCache imageCache) {
		super(context, imageCache);
	}

	@Override
	protected void onLoadedFromDisk(Bitmap originalBitmap,
			ImageContainer container) {
		Bitmap scaledBitmap = ImageSizeAdapter.scaledBitmap(originalBitmap, container.getMaxWidth(),container.getMaxHeight());
		if (scaledBitmap != originalBitmap) {
			originalBitmap.recycle();
		}
		Bitmap bitmap = BitmapUtils.getPersonalHeaderIcon(getContext(),
				scaledBitmap,container.getMaxWidth(),container.getMaxHeight());
		scaledBitmap.recycle();
		container.setBitmap(bitmap);
		getImageCache().putBitmap(
				getCacheKey(container.getUrl(), container.getMaxWidth(),
						container.getMaxHeight()), bitmap);
	}

	@Override
	protected void onLoadedFromNetwork(ImageContainer container) {
		Bitmap originalBitmap = null;
		originalBitmap = getDiskImageCache().getBitmap(container.getUrl(),container.getMaxWidth(),container.getMaxHeight());
		if (originalBitmap != null) {
			Bitmap scaledBitmap = ImageSizeAdapter.scaledBitmap(originalBitmap, container.getMaxWidth(),container.getMaxHeight());
			if (scaledBitmap != originalBitmap) {
				originalBitmap.recycle();
			}
			Bitmap bitmap = BitmapUtils.getPersonalHeaderIcon(getContext(),
					scaledBitmap,container.getMaxWidth(),container.getMaxHeight());
			scaledBitmap.recycle();
			getImageCache().putBitmap(
					getCacheKey(container.getUrl(), container.getMaxWidth(),
							container.getMaxHeight()), bitmap);
			container.setBitmap(bitmap);
		}
	}

}
