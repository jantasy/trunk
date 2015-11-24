package cn.yjt.oa.app.contactlist.server;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.util.List;

import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.beans.GroupInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.contactlist.db.ContactManager;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.ResponseListener;

/**群组数据加载类*/
public class GroupsServerLoader extends ServerLoader<List<GroupInfo>> {

	public GroupsServerLoader(
			ServerLoaderListener<List<GroupInfo>> serverLoaderListener) {
		super(serverLoaderListener);
	}


	@Override
	public void load() {
		System.out.println("GroupsServerLoader.load");
		requestGroups(updateGroupsAfterSuccessListener());
	}

	
	private Listener<Response<List<GroupInfo>>> updateGroupsAfterSuccessListener(){
		return new ResponseListener<List<GroupInfo>>() {

			@Override
			public void onSuccess(List<GroupInfo> payload) {
				GroupsServerLoader.this.onSuccess(payload);
				updateGroups(payload);
			}
			
			@Override
			public void onErrorResponse(InvocationError error) {
				super.onErrorResponse(error);
				onError();
			}
		};
	}
	
	private void requestGroups(Listener<Response<List<GroupInfo>>> listener) {
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_GROUPS);
		builder.setResponseListener(listener);
		TypeToken<Response<List<GroupInfo>>> typeToken = new TypeToken<Response<List<GroupInfo>>>() {
		};
		builder.setResponseType(typeToken.getType());
		builder.build().get();
	}
	
	private void updateGroups(List<GroupInfo> groups){
		ContactManager manager = ContactManager.getContactManager(MainApplication.getAppContext());
		manager.deleteAllGroups();
		manager.addGrouops(groups);
	}
}
