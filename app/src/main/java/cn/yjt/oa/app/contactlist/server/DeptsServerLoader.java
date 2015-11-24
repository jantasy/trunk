package cn.yjt.oa.app.contactlist.server;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import com.activeandroid.util.Log;

import android.widget.Toast;

import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.DeptDetailInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.contactlist.db.ContactManager;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.ResponseListener;
import cn.yjt.oa.app.utils.LogUtils;

/**组织结构数据加载类*/
public class DeptsServerLoader extends ServerLoader<List<DeptDetailInfo>> {

	private final String TAG = "DeptsServerLoader";
	/**构造方法*/
	public DeptsServerLoader(
			ServerLoaderListener<List<DeptDetailInfo>> serverLoaderListener) {
		super(serverLoaderListener);
	}

	@Override
	public void load() {
		reqiestDepts(updateDeptsAfterSuccessListener());
	}

	/**请求组织架构数据*/
	private void reqiestDepts(Listener<Response<List<DeptDetailInfo>>> listener) {
		Type responseType = new TypeToken<Response<List<DeptDetailInfo>>>() {
		}.getType();
		new AsyncRequest.Builder().setModule(buildDeptModule())
				.setResponseType(responseType).setResponseListener(listener)
				.build().get();
	}

	/**成功后升级组织架构信息*/
	private Listener<Response<List<DeptDetailInfo>>> updateDeptsAfterSuccessListener() {
		return new ResponseListener<List<DeptDetailInfo>>() {

			@Override
			public void onSuccess(List<DeptDetailInfo> payload) {
				//testDept(payload);
				DeptsServerLoader.this.onSuccess(payload);
				updateDepts(payload);
				MainApplication.isLoadingDeptContacts = false;
			}

			
			@Override
			public void onErrorResponse(InvocationError error) {
				super.onErrorResponse(error);
				onError();
			}
			
			/**测试方法*/
			private void testDept(List<DeptDetailInfo> payload) {
				for (DeptDetailInfo deptDetailInfo : payload) {
					LogUtils.i(TAG, deptDetailInfo.getOrderIndex()+"--"+deptDetailInfo.getName());
					if(deptDetailInfo.getChildren()!=null){
						testDept(deptDetailInfo.getChildren());
					}
				}
			}
		};
	}

	private String buildDeptModule() {
		return String.format(
				AsyncRequest.MODULE_CUSTS_DEPTS,
				String.valueOf(AccountManager.getCurrent(
						MainApplication.getAppContext()).getCustId()));
	}

	/**更新组织架构信息*/
	private void updateDepts(List<DeptDetailInfo> depts) {
		ContactManager manager = ContactManager
				.getContactManager(MainApplication.getAppContext());
		manager.deleteAllDept();
		manager.deleteAllDeptUser();
		manager.addDepts(depts);
	}

}
