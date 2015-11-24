package cn.yjt.oa.app.imageloader;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.yjt.oa.app.imageloader.Network.NetworkListener;

public class NetworkFilterImpl implements NetworkFilter {

	static final String TAG = "NetworkFilterImpl";
	private Map<String, Set<WeakReference<NetworkListener>>> networkListeners = new HashMap<String, Set<WeakReference<NetworkListener>>>();

	public boolean filt(String url, NetworkListener listener) {
		checkListeners(url);
		if (networkListeners.containsKey(url)) {
			networkListeners.get(url).add(
					new WeakReference<Network.NetworkListener>(listener));
			return true;
		} else {
			Set<WeakReference<NetworkListener>> set = new HashSet<WeakReference<NetworkListener>>();
			set.add(new WeakReference<Network.NetworkListener>(listener));
			networkListeners.put(url, set);
			return false;
		}
	}

	private void checkListeners(String url) {
		Log.d(TAG, "checkListeners:"+url);
		Set<WeakReference<NetworkListener>> set = networkListeners.get(url);
		if (set == null) {
			return;
		}
		Set<WeakReference<NetworkListener>> deleted = new HashSet<WeakReference<NetworkListener>>();
		for (WeakReference<NetworkListener> weakReference : set) {
			if (weakReference.get() == null) {
				deleted.add(weakReference);
				Log.d(TAG, "weakReference is null add to deleted Set:"+url);
			}
		}
		set.removeAll(deleted);
		Log.d(TAG, "remove null weakreference form set:"+url);
		if (set.isEmpty()) {
			networkListeners.remove(url);
			Log.d(TAG, "set is empty remove from map:"+url);
		}
	}

	@Override
	public void onProgress(String url, long length, long max) {
		Log.d(TAG, "onProgress:"+url);
		Set<WeakReference<NetworkListener>> set = networkListeners.get(url);
		if (set != null && !set.isEmpty()) {
			for (WeakReference<NetworkListener> wListener : set) {
				NetworkListener networkListener = wListener.get();
				if (networkListener != null) {
					networkListener.onProgress(url, length, max);
					Log.d(TAG, "invoke networklistener onProgress :"+url);
				}else{
					Log.d(TAG, "networklistener is null :"+url);
				}
			}
		}else{
			Log.d(TAG, "set is null or empty:"+url);
		}
	}

	@Override
	public void onSuccess(String url, File file) {
		Log.d(TAG, "onSuccess:"+url);
		Set<WeakReference<NetworkListener>> set = networkListeners.get(url);
		if (set != null) {
			for (WeakReference<NetworkListener> wListener : set) {
				NetworkListener networkListener = wListener.get();
				if (networkListener != null) {
					networkListener.onSuccess(url, file);
					Log.d(TAG, "invoke networklistener onSuccess :"+url);
				}else{
					Log.d(TAG, "networklistener is null :"+url);
				}
			}
			networkListeners.remove(url);
		}
	}

	@Override
	public void onError(String url, Exception e) {
		Log.d(TAG, "onError:"+url);
		Set<WeakReference<NetworkListener>> set = networkListeners.get(url);
		if (set != null) {
			for (WeakReference<NetworkListener> wListener : set) {
				NetworkListener networkListener = wListener.get();
				if (networkListener != null) {
					networkListener.onError(url, e);
					Log.d(TAG, "invoke networklistener onError :"+url);
				}else{
					Log.d(TAG, "networklistener is null :"+url);
				}
			}
			networkListeners.remove(url);
		}
	}
	
	static class Log{
		static final boolean DEBUG = false;
		public static void d(String tag,String message){
			if(DEBUG){
				android.util.Log.d(tag, message);
			}
		}
	}

	@Override
	public void onStart(String url) {
		Set<WeakReference<NetworkListener>> set = networkListeners.get(url);
		if (set != null) {
			for (WeakReference<NetworkListener> wListener : set) {
				NetworkListener networkListener = wListener.get();
				if (networkListener != null) {
					networkListener.onStart(url);
					Log.d(TAG, "invoke networklistener onStart :"+url);
				}else{
					Log.d(TAG, "networklistener is null :"+url);
				}
			}
		}
	}
}
