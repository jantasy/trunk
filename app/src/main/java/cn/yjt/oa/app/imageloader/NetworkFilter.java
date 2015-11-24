package cn.yjt.oa.app.imageloader;

import java.io.File;

import cn.yjt.oa.app.imageloader.Network.NetworkListener;

public interface NetworkFilter {
	
	public boolean filt(String url,NetworkListener listener);
	public void onProgress(String url,long length, long max);
	public void onSuccess(String url,File file);
	public void onError(String url,Exception e);
	public void onStart(String url);
	
}
