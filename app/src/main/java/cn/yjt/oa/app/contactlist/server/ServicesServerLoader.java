package cn.yjt.oa.app.contactlist.server;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.util.List;

import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.beans.CommonContactInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.contactlist.db.ContactManager;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.ResponseListener;

/**公共服务号数据加载类*/
public class ServicesServerLoader extends
		ServerLoader<List<CommonContactInfo>> {

	/**
	 * 构造方法
	 * @param serverLoaderListener
	 */
	public ServicesServerLoader(
			ServerLoaderListener<List<CommonContactInfo>> serverLoaderListener) {
		super(serverLoaderListener);
	}

	@Override
	public void load() {
		requestPublicService(updatePublicServiceAfterSuccessListener());
	}
	
	private Listener<Response<List<CommonContactInfo>>> updatePublicServiceAfterSuccessListener(){
		return new ResponseListener<List<CommonContactInfo>>() {

			@Override
			public void onSuccess(List<CommonContactInfo> payload) {
				ServicesServerLoader.this.onSuccess(payload);
				updatePublicService(payload);
			}
			@Override
			public void onErrorResponse(InvocationError error) {
				super.onErrorResponse(error);
				onError();
			}
		};
	}
	
	private void requestPublicService(Listener<Response<List<CommonContactInfo>>> listener) {
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_CONTACTS_COMMON);
		builder.setResponseListener(listener);
		TypeToken<Response<List<CommonContactInfo>>> token = new TypeToken<Response<List<CommonContactInfo>>>() {};
		builder.setResponseType(token.getType());
		builder.build().get();
	}
	
	private void updatePublicService(List<CommonContactInfo> publicServices){
		ContactManager manager = ContactManager.getContactManager(MainApplication.getAppContext());
		manager.deleteAllPublicService();
		manager.addPublicService(publicServices);
	}

}
