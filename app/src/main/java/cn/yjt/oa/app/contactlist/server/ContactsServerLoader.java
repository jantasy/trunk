package cn.yjt.oa.app.contactlist.server;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.beans.ContactInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.contactlist.db.ContactManager;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.ResponseListener;
import cn.yjt.oa.app.utils.LogUtils;

/**通讯录数据加载类*/
public class ContactsServerLoader extends ServerLoader<List<ContactInfo>> {

	private static final String TAG = "ContactsServerLoader";

	/**建立一个单例的线程工厂*/
	private static ExecutorService mSingleThread = Executors.newSingleThreadExecutor();

	/**
	 * 构造方法
	 * @param serverLoaderListener 传递一个加载监听对象，有onsuccess和onerror方法
	 */
	public ContactsServerLoader(ServerLoaderListener<List<ContactInfo>> serverLoaderListener) {
		super(serverLoaderListener);
	}

	@Override
	public void load() {
		//请求所有的联系人
		requestALLContacts(loadContactsSuccessListener());
	}

	/**请求通讯录联系人的监听*/
	private Listener<Response<List<ContactInfo>>> loadContactsSuccessListener() {
		return new ResponseListener<List<ContactInfo>>() {
			@Override
			public void onSuccess(final List<ContactInfo> payload) {
				// 遍历请求到的CantactInfo对象，根据对象的userid进行判断，该联系人是否注册
				// 如果该联系人已经注册，就将他的regiest字段设置为true
				for (ContactInfo contactInfo : payload) {
					if (contactInfo.getUserId() != 0 && contactInfo.getUserId() != null) {
						contactInfo.setRegister(true);
					}
				}
				//在子线程中执行，将获取到的联系人数据添加到本地数据库
				mSingleThread.execute(new Runnable() {
					@Override
					public void run() {
						updateLocalContacts(payload);
						//然后将正在加载所有联系人的标识设置为false
						MainApplication.setLoadingALLContacts(false);
//						sendContactsUpdatedBroadcast();
					}
				});
				//执行从构造函数传进来的serverLoaderListener的onSuccess方法
				ContactsServerLoader.this.onSuccess(payload);
			}

			@Override
			public void onErrorResponse(InvocationError error) {
				super.onErrorResponse(error);
				onError();
			}
		};
	}
	private void sendContactsUpdatedBroadcast() {
		Intent intent = new Intent(IServerLoader.ACTION_CONTACTS_UPDATED);
		sendBroadcast(intent);
	}
	
	/**发送广播*/
	private void sendBroadcast(Intent intent) {
		MainApplication.getAppContext().sendBroadcast(intent);
	}
	
	/**更新本地联系人*/
	private void updateLocalContacts(final List<ContactInfo> contacts) {
		ContactManager manager = ContactManager.getContactManager(MainApplication.getAppContext());
		//清除本地数据库中所有注册了的联系人
		manager.deleteAllRegisteredContacts();
		//清除本地数据库中所有未注册的联系人
		manager.deleteAllUnregisteredContacts();
		//将新的联系人添加到本地数据库
		manager.addContacts(contacts);
	}

	/**向服务器请求所有的通讯录联系人*/
	private void requestALLContacts(Listener<Response<List<ContactInfo>>> listener) {
		requestContacts(AsyncRequest.MODULE_CONTACTS_ALL, null, listener);
	}

	/**向服务器请求联系人*/
	private void requestContacts(String module, String moduleItem, Listener<Response<List<ContactInfo>>> listener) {
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(module);
		builder.setModuleItem(moduleItem);
		TypeToken<Response<List<ContactInfo>>> typeToken = new TypeToken<Response<List<ContactInfo>>>() {
		};
		builder.setResponseType(typeToken.getType());
		builder.setResponseListener(listener);
		builder.build().get();
	}
}
