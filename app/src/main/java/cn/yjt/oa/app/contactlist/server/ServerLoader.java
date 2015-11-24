package cn.yjt.oa.app.contactlist.server;

/***/
public abstract class ServerLoader<T> implements IServerLoader<T> {
	
	private ServerLoaderListener<T> serverLoaderListener;
	
	public ServerLoader(ServerLoaderListener<T> serverLoaderListener) {
		this.serverLoaderListener = serverLoaderListener;
	}
	
	protected void onSuccess(T result) {
		if(serverLoaderListener != null){
			serverLoaderListener.onSuccess(result);
		}
	}
	
	protected void onError() {
		if(serverLoaderListener != null){
			serverLoaderListener.onError();
		}
	}

}
