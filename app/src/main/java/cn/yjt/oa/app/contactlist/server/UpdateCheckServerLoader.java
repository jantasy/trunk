package cn.yjt.oa.app.contactlist.server;

import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.CommonContactInfo;
import cn.yjt.oa.app.beans.ContactInfo;
import cn.yjt.oa.app.beans.CustLastUpdateTimesInfo;
import cn.yjt.oa.app.beans.DeptDetailInfo;
import cn.yjt.oa.app.beans.GroupInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.ResponseListener;

/**检测数据更新的类*/
public class UpdateCheckServerLoader implements IServerLoader<CustLastUpdateTimesInfo> {

	private static final String TAG = "UpdateCheckServerLoader";

	private static final String PREFERENCE_CONTACTS_UPDATE_CHECK = "contacts_update_check_%s_%s";

	private static final String KEY_CONTACTS_UPDATETIME = "contacts_updatetime";
	private static final String KEY_GROUPS_UPDATETIME = "groups_updatetime";
	private static final String KEY_DEPTS_UPDATETIME = "depts_updatetime";
	private static final String KEY_SERVICES_UPDATETIME = "services_updatetime";

	@Override
	public void load() {
		//获取联系人（通讯录，组群，组织结构，公共服务联系人）的更新时间
		requestCustUpdatetime(updateDataAfterSuccessListenner());
	}

	/**获取通讯录的更新时间*/
	private void requestCustUpdatetime(Listener<Response<CustLastUpdateTimesInfo>> listener) {
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_CUSTS_UPDATETIMES);
		TypeToken<Response<CustLastUpdateTimesInfo>> typeToken = new TypeToken<Response<CustLastUpdateTimesInfo>>() {
		};
		builder.setResponseType(typeToken.getType());
		builder.setResponseListener(listener);
		builder.build().get();
	}

	/**获取通讯录更新时间的回调监听*/
	private Listener<Response<CustLastUpdateTimesInfo>> updateDataAfterSuccessListenner() {
		return new ResponseListener<CustLastUpdateTimesInfo>() {

			@Override
			public void onSuccess(CustLastUpdateTimesInfo payload) {
				checkUpdateTimes(payload);
			}
		};
	}

	/**检测更新时间*/
	private void checkUpdateTimes(CustLastUpdateTimesInfo updateTimesInfo) {
		checkContactsUpdateTime(updateTimesInfo.getUserLastUpdateTime());
		checkGroupsUpdateTime(updateTimesInfo.getGroupLastUpdateTime());
		checkServicesUpdateTime(updateTimesInfo.getCommonContactLastUpdateTime());
		checkDeptsUpdateTime(updateTimesInfo.getDeptLastUpdateTime());
	}

	/**检测通讯录的更新时间，并更具时间的变化进行相应的操作*/
	private void checkContactsUpdateTime(final String updateTime) {
		String contactsUpdateTime = getContactsUpdateTime();
		//TODO：
		if (contactsUpdateTime == null) {

		}
		//比较从网络获取的最后更新时间和保存在本地的最后更新时间是否相同，
		//如果相同不做任何操作，否则从网络获取通讯录的数据
		if (!TextUtils.equals(updateTime, contactsUpdateTime)) {
			//设置标签正在更新通讯录
			MainApplication.setLoadingALLContacts(true);
			//从网络获取通讯录联系人
			new ContactsServerLoader(new ServerLoaderSuccessListener<List<ContactInfo>>() {
				@Override
				public void onSuccess(final List<ContactInfo> result) {
					//获取成功之后将更新时间保存到sp中
					setContactsUpdateTime(updateTime);
					MainApplication.sContactInfoLists = result;
					//发送广播通知通讯录更新
					sendContactsUpdatedBroadcast();
				}
			}).load();
		}
	}

	/**检测群组的更新时间，并更具时间的变化进行相应的操作*/
	private void checkGroupsUpdateTime(final String updateTime) {
		if (!TextUtils.equals(updateTime, getGroupsUpdateTime())) {
			new GroupsServerLoader(new ServerLoaderSuccessListener<List<GroupInfo>>() {
				@Override
				public void onSuccess(List<GroupInfo> result) {
					setGroupsUpdateTime(updateTime);
					sendGroupsUpdatedBroadcast(result);
				}
			}).load();
		}
	}

	/**检测公共服务号的更新时间，并更具时间的变化进行相应的操作*/
	private void checkServicesUpdateTime(final String updateTime) {
		//		if(TextUtils.isEmpty(updateTime)&&TextUtils.isEmpty(getServicesUpdateTime())){
		//			
		//		}else{
		if (!TextUtils.equals(updateTime, getServicesUpdateTime())) {
			new ServicesServerLoader(new ServerLoaderSuccessListener<List<CommonContactInfo>>() {

				@Override
				public void onSuccess(List<CommonContactInfo> result) {
					setServicesUpdateTime(updateTime);
					sendServicesUpdatedBroadcast(result);
				}
			}).load();
			//			}
		}
	}

	/**检测组织架构的更新时间，并更具时间的变化进行相应的操作*/
	private void checkDeptsUpdateTime(final String updateTime) {

		if (!TextUtils.equals(updateTime, getDeptsUpdateTime())) {
			MainApplication.isLoadingDeptContacts = true;
			new DeptsServerLoader(new ServerLoaderSuccessListener<List<DeptDetailInfo>>() {

				@Override
				public void onSuccess(List<DeptDetailInfo> result) {
					setDeptsUpdateTime(updateTime);
					sendDeptsUpdatedBroadcast(result);
				}
			}).load();
		}
	}

	/**发送一条通讯录更新的广播*/
	private void sendContactsUpdatedBroadcast() {
		Intent intent = new Intent(IServerLoader.ACTION_CONTACTS_UPDATED);
		//		intent.putExtra(IServerLoader.EXTRA_CONTACTS,
		//				(ArrayList<ContactInfo>) result);
		sendBroadcast(intent);
	}

	/**发送一条群组更新的广播*/
	private void sendGroupsUpdatedBroadcast(List<GroupInfo> result) {
		Intent intent = new Intent(IServerLoader.ACTION_GROUPS_UPDATED);
		intent.putExtra(IServerLoader.EXTRA_GROUPS, (ArrayList<GroupInfo>) result);
		sendBroadcast(intent);
	}

	/**发送一条公共服务号更新的广播*/
	private void sendServicesUpdatedBroadcast(List<CommonContactInfo> result) {
		Intent intent = new Intent(IServerLoader.ACTION_SERVICES_UPDATED);
		intent.putParcelableArrayListExtra(IServerLoader.EXTRA_SERVICES, (ArrayList<CommonContactInfo>) result);
		sendBroadcast(intent);
	}

	/**发送一条组织架构更新的广播*/
	private void sendDeptsUpdatedBroadcast(List<DeptDetailInfo> result) {
		//		MainApplication.sDeptDetailInfoLists = result;
		Intent intent = new Intent(IServerLoader.ACTION_DEPTS_UPDATED);
		//		intent.putExtra(IServerLoader.EXTRA_DEPTS,
		//				(ArrayList<DeptDetailInfo>) result);
		sendBroadcast(intent);
	}

	/**从sp中取出保存的通讯录的上次更新时间*/
	private String getContactsUpdateTime() {
		return getPreferences().getString(KEY_CONTACTS_UPDATETIME, "");
	}

	/**往sp中设置通讯录最新的更新时间*/
	private void setContactsUpdateTime(String updateTime) {
		getPreferences().edit().putString(KEY_CONTACTS_UPDATETIME, updateTime).commit();
	}

	/**从sp中取出保存的群组的上次更新时间*/
	private String getGroupsUpdateTime() {
		return getPreferences().getString(KEY_GROUPS_UPDATETIME, "");
	}

	/**往sp中设置群组最新的更新时间*/
	private void setGroupsUpdateTime(String updateTime) {
		getPreferences().edit().putString(KEY_GROUPS_UPDATETIME, updateTime).commit();
	}

	/**从sp中取出保存的公共服务号的上次更新时间*/
	private String getServicesUpdateTime() {
		return getPreferences().getString(KEY_SERVICES_UPDATETIME, "");
	}

	/**往sp中设置公共服务号最新的更新时间*/
	private void setServicesUpdateTime(String updateTime) {
		getPreferences().edit().putString(KEY_SERVICES_UPDATETIME, updateTime).commit();
	}

	/**从sp中取出保存的组织架构的上次更新时间*/
	private String getDeptsUpdateTime() {
		return getPreferences().getString(KEY_DEPTS_UPDATETIME, "");
	}

	/**往sp中设置组织架构最新的更新时间*/
	private void setDeptsUpdateTime(String updateTime) {
		getPreferences().edit().putString(KEY_DEPTS_UPDATETIME, updateTime).commit();
	}

	/**获取sp*/
	private SharedPreferences getPreferences() {
		UserInfo current = AccountManager.getCurrent(MainApplication.getAppContext());
		return MainApplication.getAppContext().getSharedPreferences(
				String.format(PREFERENCE_CONTACTS_UPDATE_CHECK, current.getCustId(), current.getId()),
				Context.MODE_PRIVATE);
	}

	/**发送广播*/
	private void sendBroadcast(Intent intent) {
		MainApplication.getAppContext().sendBroadcast(intent);
	}

}
