package cn.yjt.oa.app.utils;

import java.io.File;
import java.util.HashMap;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import cn.yjt.oa.app.imageloader.FastNetwork;
import cn.yjt.oa.app.imageloader.Network;
import cn.yjt.oa.app.imageloader.Network.NetworkListener;

public class FileLoader {

	static final String TAG = "FileLoader";

	private Context context;
	private Handler handler;
	private DiskFileCache diskFileCache;

	private Network network;
	private HashMap<FileLoaderListener, NetworkListener> listeners = new HashMap<FileLoaderListener, NetworkListener>();

	public FileLoader(Context context) {
		this.context = context;
		handler = new Handler(Looper.getMainLooper());
		diskFileCache = new DiskFileCache(context);
		network = new FastNetwork(context);
	}

	public Context getContext() {
		return context;
	}

	public void get(final String url,final FileLoaderListener listener) {

		Log.d(TAG, "get:" + url);

		final FileContainer container = new FileContainer(url);
		new AsyncTask<Void, Void, File>() {

			@Override
			protected File doInBackground(Void... params) {
				File file = diskFileCache.getFile(url);
				if (file != null) {
					container.setFile(file);
					postSuccess(listener, container);
				} else {
				   requestNetwork(url, listener, container);
				}
				return null;
			}
		}.execute();
	}

	private void postSuccess(final FileLoaderListener loaderListener,
			final FileContainer container) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				loaderListener.onSuccess(container);
			}
		});
	}

	private void postProgress(final FileLoaderListener loaderListener,
			final FileContainer container) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				loaderListener.onProgress(container);
			}
		});
	}

	private void postError(final FileLoaderListener loaderListener,
			final FileContainer container) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				loaderListener.onError(container);
			}
		});
	}

	private void postStart(final FileLoaderListener loaderListener,
			final FileContainer container) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				loaderListener.onStart(container);
			}
		});
	}

	private void postStarted(final FileLoaderListener loaderListener,
			final FileContainer container) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				loaderListener.onStarted(container);
			}
		});
	}

	private void postWait(final FileLoaderListener loaderListener,
			final FileContainer container) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				loaderListener.onWait(container);
			}
		});
	}



	private void requestNetwork(final String url,
			final FileLoaderListener listener, final FileContainer container) {
		Log.d(TAG, "requestNetwork:" + url);
		String createCacheFile = DiskFileCache.createCacheFile(diskFileCache.getCacheDiskFile(), url);
		FileNetworkListener networkListener = null;
		if(listeners.containsKey(listener)){
			networkListener = (FileNetworkListener) listeners.get(listener);
			networkListener.container = container;
			networkListener.listener = listener;
		}else{
			networkListener = new FileNetworkListener();
			networkListener.container = container;
			networkListener.listener = listener;
			listeners.put(listener, networkListener);
		}
		network.performRequest(url, new File(createCacheFile),networkListener);
	}

	private class FileNetworkListener implements NetworkListener {
		private FileContainer container;
		private FileLoaderListener listener;
		@Override
		public void onSuccess(String url, File file) {
			listeners.remove(listener);
			if (file != null) {
				container.setFile(file);
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





	public static interface FileLoaderListener {

		public void onWait(FileContainer container);

		public void onStarted(FileContainer container);

		public void onStart(FileContainer container);

		public void onProgress(FileContainer container);

		public void onSuccess(FileContainer container);

		public void onError(FileContainer container);
	}
	
	


	public static class FileContainer {
		private String url;
		private File file;
		private Exception e;
		private long max;
		private long progress;

		public FileContainer(String url) {
			this.url = url;
		
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
		
		



		public File getFile() {
			return file;
		}

		public void setFile(File file) {
			this.file = file;
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

	}

}
