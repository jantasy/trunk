package cn.yjt.oa.app.imageloader;

import java.io.File;

public interface Network {

	public static interface NetworkListener {
		/**
		 * Request for this URL is waiting for performed.
		 * @param url
		 */
		public void onWait(String url);
		/**
		 * Request for this URL starts performing.
		 * @param url
		 */
		public void onStart(String url);
		
		/**
		 * Request for this URL has started by a previous request.
		 * @param url
		 * @param max
		 * @param progress
		 */
		public void onStarted(String url,int max,int progress);

		/**
		 * Request for this URL has a progress.
		 * @param url
		 * @param length
		 * @param max
		 */
		public void onProgress(String url,long length, long max);

		/**
		 * Request for this URL has complete successful.
		 * @param url
		 * @param data
		 */
		public void onSuccess(String url,File file);

		/**
		 * Request for this URL occurs a exception.
		 * @param url
		 * @param e
		 */
		public void onError(String url,Exception e);
	}

	/**
	 * Perform a network request with a URL and a File.
	 * @param url
	 * @param file
	 */
	public void performRequest(String url,File file,NetworkListener listener);
	
}
