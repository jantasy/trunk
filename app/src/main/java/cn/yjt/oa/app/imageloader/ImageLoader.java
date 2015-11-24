package cn.yjt.oa.app.imageloader;

import java.io.File;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import cn.yjt.oa.app.imageloader.Network.NetworkListener;

/**
 * 图片加载类
 *
 */
public class ImageLoader {

	private Context context;
	private Handler handler;
	private ImageCache imageCache;
	private DiskImageCache diskImageCache;

	private Network network;
	private HashMap<ImageLoaderListener, NetworkListener> listeners = new HashMap<ImageLoaderListener, NetworkListener>();

	private HashMap<String,String> urlCacheFile = new HashMap<String, String>();
	
	public ImageLoader(Context context, ImageCache imageCache) {
		this.context = context;
		this.imageCache = imageCache;
		handler = new Handler(Looper.getMainLooper());
		diskImageCache = new DiskImageCache(context);
		network = new FastNetwork(context);
	}

	public Context getContext() {
		return context;
	}

	public ImageCache getImageCache() {
		return imageCache;
	}

	public void get(final String uri, final int maxWidth, final int maxHeight,
			final ImageLoaderListener listener, boolean fitSize) {
		if (TextUtils.isEmpty(uri)) {
			return;
		}

		final ImageContainer container = new ImageContainer(uri, maxWidth,
				maxHeight);
		container.setFitSize(fitSize);
		Bitmap bitmap = imageCache.getBitmap(getCacheKey(uri, maxWidth,
				maxHeight));
		if (bitmap != null) {
			container.setBitmap(bitmap);
			listener.onSuccess(container);
		} else {
			new AsyncTask<Void, Void, Bitmap>() {

				@Override
				protected Bitmap doInBackground(Void... params) {
					try {
						String cacheFile = DiskImageCache.createCacheFile(
								diskImageCache.getCacheDir(), uri);
						urlCacheFile.put(uri, cacheFile);
						Bitmap bitmap = diskImageCache.getBitmap(uri,
								container.getMaxWidth(), container.getMaxHeight());
						if (bitmap != null) {
							onLoadedFromDisk(bitmap, container);
							postSuccess(listener, container);
						} else if (uri.startsWith("http")) {
							requestNetwork(uri, listener, container);
						} else {
							container.setE(new Exception("decode failure"));
							postError(listener, container);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
			}.execute();
		}
	}

	public void get(final String uri, final int maxWidth, final int maxHeight,
			final ImageLoaderListener listener) {
		get(uri, maxWidth, maxHeight, listener, true);
	}

	protected void onLoadedFromDisk(Bitmap originalBitmap,
			ImageContainer container) {
		if (container.isFitSize()) {
			Bitmap scaledBitmap = ImageSizeAdapter.scaledBitmap(originalBitmap,
					container.getMaxWidth(), container.getMaxHeight());
			if (scaledBitmap != originalBitmap) {
				originalBitmap.recycle();
			}
			originalBitmap = scaledBitmap;
		}
		container.setBitmap(originalBitmap);
		imageCache.putBitmap(
				getCacheKey(container.getUrl(), container.getMaxWidth(),
						container.getMaxHeight()), originalBitmap);
	}

	protected void onLoadedFromNetwork(ImageContainer container) {
		decode(container, container.getUrl());
	}

	public DiskImageCache getDiskImageCache() {
		return diskImageCache;
	}

	private Bitmap decode(final ImageContainer container, String url) {
		Bitmap bitmap = null;
		bitmap = diskImageCache.getBitmap(url, container.getMaxWidth(),
				container.getMaxHeight());
		if (bitmap != null) {
			if (container.isFitSize()) {
				Bitmap scaledBitmap = ImageSizeAdapter.scaledBitmap(bitmap,
						container.getMaxWidth(), container.getMaxHeight());
				if (scaledBitmap != bitmap) {
					bitmap.recycle();
				}
				bitmap = scaledBitmap;
			}
			imageCache.putBitmap(
					getCacheKey(url, container.getMaxWidth(),
							container.getMaxHeight()), bitmap);
		}

		final Bitmap bmp = bitmap;
		container.setBitmap(bmp);
		return bmp;
	}

	private void postSuccess(final ImageLoaderListener loaderListener,
			final ImageContainer container) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				loaderListener.onSuccess(container);
			}
		});
	}

	private void postProgress(final ImageLoaderListener loaderListener,
			final ImageContainer container) {
		// Log.d(TAG, "postProgress");
		if (loaderListener instanceof ImageLoaderProgressListener) {

			handler.post(new Runnable() {

				@Override
				public void run() {
					((ImageLoaderProgressListener) loaderListener)
							.onProgress(container);
				}
			});
		}
	}

	private void postError(final ImageLoaderListener loaderListener,
			final ImageContainer container) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				loaderListener.onError(container);
			}
		});
	}

	private void postStart(final ImageLoaderListener loaderListener,
			final ImageContainer container) {
		if (loaderListener instanceof ImageLoaderProgressListener) {

			handler.post(new Runnable() {

				@Override
				public void run() {
					((ImageLoaderProgressListener) loaderListener)
							.onStart(container);
				}
			});
		}
	}

	private void postStarted(final ImageLoaderListener loaderListener,
			final ImageContainer container) {
		if (loaderListener instanceof ImageLoaderProgressListener) {

			handler.post(new Runnable() {

				@Override
				public void run() {
					((ImageLoaderProgressListener) loaderListener)
							.onStarted(container);
				}
			});
		}
	}

	private void postWait(final ImageLoaderListener loaderListener,
			final ImageContainer container) {
		if (loaderListener instanceof ImageLoaderProgressListener) {

			handler.post(new Runnable() {

				@Override
				public void run() {
					((ImageLoaderProgressListener) loaderListener)
							.onWait(container);
				}
			});
		}
	}

	public void get(final String uri, final ImageLoaderListener listener) {
		get(uri, 0, 0, listener, false);
	}

	private void requestNetwork(String url, ImageLoaderListener listener,
			ImageContainer container) {
		String cacheFile = DiskImageCache.createCacheFile(
				diskImageCache.getCacheDir(), url);
		//TODO:
		ImageNetworkListener networkListener = null;
		if (listeners.containsKey(listener)) {
			networkListener = (ImageNetworkListener) listeners.get(listener);
			networkListener.container = container;
			networkListener.listener = listener;
		} else {
			networkListener = new ImageNetworkListener();
			networkListener.container = container;
			networkListener.listener = listener;
			listeners.put(listener, networkListener);
		}
		network.performRequest(url, new File(cacheFile), networkListener);
	}

	/** 通过url获取图片的本地缓存位置 */
	public String getCacheFileByUrl(String url){
		return urlCacheFile.get(url);
	}
	
	private class ImageNetworkListener implements NetworkListener {
		private ImageContainer container;
		private ImageLoaderListener listener;

		@Override
		public void onSuccess(String url, File file) {
			listeners.remove(listener);
			onLoadedFromNetwork(container);
			Bitmap bmp = container.getBitmap();
			if (bmp != null) {
				postSuccess(listener, container);
			} else {
				container.setE(new Exception("Decode failure"));
				postError(listener, container);
			}
		}

		@Override
		public void onWait(String url) {
			postWait(listener, container);
		}

		@Override
		public void onStart(String url) {
			postStart(listener, container);
		}

		@Override
		public void onStarted(String url, int max, int progress) {
			container.setMax(max);
			container.setProgress(progress);
			postStarted(listener, container);
		}

		@Override
		public void onProgress(String url, long length, long max) {
			container.setMax(max);
			container.setProgress(length);
			postProgress(listener, container);
		}

		@Override
		public void onError(String url, Exception e) {
			listeners.remove(listener);
			container.setE(e);
			postError(listener, container);
		}
	}

	/**
	 * Creates a cache key for use with the L1 cache.
	 * 
	 * @param url
	 *            The URL of the request.
	 * @param maxWidth
	 *            The max-width of the output.
	 * @param maxHeight
	 *            The max-height of the output.
	 */
	protected static String getCacheKey(String url, int maxWidth, int maxHeight) {
		return new StringBuilder(url.length() + 12).append("#W")
				.append(maxWidth).append("#H").append(maxHeight).append(url)
				.toString();
	}

	public static interface ImageLoaderListener {
		public void onSuccess(ImageContainer container);

		public void onError(ImageContainer container);
	}

	public static interface ImageLoaderProgressListener extends
			ImageLoaderListener {

		public void onWait(ImageContainer container);

		public void onStarted(ImageContainer container);

		public void onStart(ImageContainer container);

		public void onProgress(ImageContainer container);

	}

	public static class ImageContainer {
		private String url;
		private int maxWidth;
		private int maxHeight;
		private Bitmap bitmap;
		private Exception e;
		private long max;
		private long progress;
		private boolean isFitSize;

		public ImageContainer(String url, int maxWidth, int maxHeight) {
			this.url = url;
			this.maxWidth = maxWidth;
			this.maxHeight = maxHeight;
		}

		public String getUrl() {
			if(url == null){
				return "";
			}
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public int getMaxWidth() {
			return maxWidth;
		}

		public void setMaxWidth(int maxWidth) {
			this.maxWidth = maxWidth;
		}

		public int getMaxHeight() {
			return maxHeight;
		}

		public void setMaxHeight(int maxHeight) {
			this.maxHeight = maxHeight;
		}

		public Bitmap getBitmap() {
			return bitmap;
		}

		public void setBitmap(Bitmap bitmap) {
			this.bitmap = bitmap;
		}

		public Exception getE() {
			return e;
		}

		public void setE(Exception e) {
			this.e = e;
		}

		public long getMax() {
			return max;
		}

		public void setMax(long max) {
			this.max = max;
		}

		public long getProgress() {
			return progress;
		}

		public void setProgress(long progress) {
			this.progress = progress;
		}

		public boolean isFitSize() {
			return isFitSize;
		}

		public void setFitSize(boolean isFitSize) {
			this.isFitSize = isFitSize;
		}

	}

}
