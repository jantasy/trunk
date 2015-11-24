package cn.yjt.oa.app.contactlist.server;

/**通讯录那一块的更新的接口*/
public interface IServerLoader<T> {
	
	public static final String ACTION_CONTACTS_UPDATED = "cn.yjt.oa.app.contactlist.server.ACTION_CONTACTS_UPDATED";
	public static final String ACTION_GROUPS_UPDATED = "cn.yjt.oa.app.contactlist.server.ACTION_GROUPS_UPDATED";
	public static final String ACTION_SERVICES_UPDATED = "cn.yjt.oa.app.contactlist.server.ACTION_SERVICES_UPDATED";
	public static final String ACTION_DEPTS_UPDATED = "cn.yjt.oa.app.contactlist.server.ACTION_DEPTS_UPDATED";
	public static final String ACTION_DEPTS_REFRESHING = "cn.yjt.oa.app.contactlist.server.ACTION_DEPTS_REFRESHING";
	
	public static final String EXTRA_CONTACTS = "contacts";
	public static final String EXTRA_GROUPS = "groups";
	public static final String EXTRA_SERVICES = "services";
	public static final String EXTRA_DEPTS = "depts";

	/**启动方法*/
	public void load();
	
	/**内部接口*/
	public static interface ServerLoaderListener<T>{
		public void onSuccess(T result);
		public void onError();
	}
	
	public static abstract class ServerLoaderSuccessListener<T> implements ServerLoaderListener<T>{
		@Override
		public void onError() {
		}
	}
}
