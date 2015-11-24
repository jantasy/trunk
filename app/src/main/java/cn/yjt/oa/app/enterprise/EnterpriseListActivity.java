package cn.yjt.oa.app.enterprise;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.Cancelable;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.LeaveEnterpriseActivity;
import cn.yjt.oa.app.MainActivity;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.app.utils.StorageUtils;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.beans.UserLoginInfo;
import cn.yjt.oa.app.beans.UserLoginInfoList;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.contactlist.db.ContactManager;
import cn.yjt.oa.app.contactlist.server.UpdateCheckServerLoader;
import cn.yjt.oa.app.enterprise.operation.ModifyEnterpriseInfoActivity;
import cn.yjt.oa.app.http.ApiHelper;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.utils.Config;
import cn.yjt.oa.app.widget.SectionAdapter;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

public class EnterpriseListActivity extends TitleFragmentActivity implements
	OnItemClickListener, OnClickListener, OnItemLongClickListener {

	private EnterpriseAdapter adapter;
	private ProgressDialog progressDialog;
	private Cancelable cancelable;
	private Cancelable enterpriseListCancelable;
	// private ProgressDialog enterpriseListProgress;
	private PullToRefreshListView listView;
	private EnterpriseListener enterpriseListener;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
        setContentView(R.layout.activity_enterprise_list);
		listView = (PullToRefreshListView) findViewById(R.id.enterprise_listview);
		listView.setOnItemLongClickListener(this);
		findViewById(R.id.create_enterprise).setOnClickListener(this);
		adapter = new EnterpriseAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		enterpriseListener = new EnterpriseListener();
		listView.setOnRefreshListener(enterpriseListener);
		listView.setRefreshingState();
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		requestEnterpriseList();
	}

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	public class EnterpriseListener implements OnRefreshListener {

		@Override
		public void onRefresh() {
			requestEnterpriseList();
		}

	}

	private void requestEnterpriseList() {
		// repeated request,cancel it.
		if (enterpriseListCancelable != null) {
			enterpriseListCancelable.cancel();
			enterpriseListCancelable = null;
		}
		Type responseType = new TypeToken<Response<List<UserLoginInfo>>>() {
		}.getType();
		enterpriseListCancelable = new AsyncRequest.Builder()
			.setModule(AsyncRequest.MODULE_CUSTS_CUSTLIST)
			.setResponseType(responseType)
			.setResponseListener(
				new Listener<Response<List<UserLoginInfo>>>() {

					@Override
					public void onErrorResponse(InvocationError error) {
						System.out.println("onErrorResponse:"
							+ error.getErrorType());
						//								dismissEnterpriseProgress();
						listView.onRefreshComplete();
						enterpriseListCancelable = null;
						Toast.makeText(getApplicationContext(),
							"网路连接失败，请稍后重试", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onResponse(
						Response<List<UserLoginInfo>> response) {
						System.out.println("onResponse");
						//								dismissEnterpriseProgress();
						listView.onRefreshComplete();
						enterpriseListCancelable = null;
						if (response.getCode() == 0) {
							// CustList payload = response.getPayload();
							// adapter.custList = payload;
							List<UserLoginInfo> payload = response.getPayload();
							List<UserLoginInfo> acceptList = new ArrayList<UserLoginInfo>();
							List<UserLoginInfo> applyingList = new ArrayList<UserLoginInfo>();
							List<UserLoginInfoList> loginInfoLists = new ArrayList<UserLoginInfoList>();
							for (UserLoginInfo userLoginInfo : payload) {
								if ("cust".equalsIgnoreCase(userLoginInfo.getType())) {
									acceptList.add(userLoginInfo);
								} else {
									applyingList.add(userLoginInfo);
								}
							}
							if (!acceptList.isEmpty()) {
								UserLoginInfoList object = new UserLoginInfoList();
								object.setUserLoginInfos(acceptList);
								object.setGroupName("我的单位");
								loginInfoLists.add(object);
							}
							if (!applyingList.isEmpty()) {
								UserLoginInfoList object = new UserLoginInfoList();
								object.setUserLoginInfos(applyingList);
								object.setGroupName("我申请中的企业");
								loginInfoLists.add(object);
							}
							adapter.infoLists = loginInfoLists;
							adapter.notifyDataSetChanged();
						} else {
							Toast.makeText(getApplicationContext(),
								response.getDescription(),
								Toast.LENGTH_SHORT).show();
						}
					}

					//							private void dismissEnterpriseProgress() {
					//								runOnUiThread(new Runnable() {
					//									public void run() {
					//										// enterpriseListProgress.dismiss();
					//										enterpriseListCancelable = null;
					//									}
					//								});
					//							}
				}).build().get();

		//		enterpriseListProgress = ProgressDialog.show(this, null, "正在获取企业列表...");
		//		enterpriseListProgress.setCancelable(true);
		//		enterpriseListProgress
		//				.setOnCancelListener(new DialogInterface.OnCancelListener() {
		//
		//					@Override
		//					public void onCancel(DialogInterface dialog) {
		//						if (enterpriseListCancelable != null) {
		//							enterpriseListCancelable = null;
		//						}
		//					}
		//				});

	}

	private class EnterpriseAdapter extends SectionAdapter {
		List<UserLoginInfoList> infoLists = Collections.emptyList();;

		@Override
		public int getSectionCount() {
			return infoLists.size();
		}

		@Override
		public int getItemCountAtSection(int section) {
			return infoLists.get(section).getUserLoginInfos().size();
		}

		@Override
		public Object getSection(int section) {
			return infoLists.get(section);
		}

		@Override
		public Object getItem(int section, int position) {
			return infoLists.get(section).getUserLoginInfos().get(position);
		}

		@Override
		public View getSectionView(int section, View convertView,
			ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = inflater.inflate(
					R.layout.message_center_item_title, parent, false);
			}
			TextView titleView = (TextView) convertView
				.findViewById(R.id.title_tv);
			titleView.setText(((UserLoginInfoList) getSection(section))
				.getGroupName());

			return convertView;
		}

		@Override
		public View getItemView(int section, int position, View convertView,
			ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.enterprise_list_item,
					parent, false);
			}
			TextView enterpriseName = (TextView) convertView
				.findViewById(R.id.enterprise_name);
			TextView enterpriseID = (TextView) convertView
				.findViewById(R.id.enterprise_id);
			UserLoginInfo custUser = (UserLoginInfo) getItem(section, position);

			String custName = custUser.getCustName();
			convertView.findViewById(R.id.enterprise_select).setVisibility(View.GONE);
			if ("CUST".equals(custUser.getType())) {
				if (custUser.getContentId() == Long.valueOf(AccountManager.getCurrent(getApplicationContext())
					.getCustId())) {
					convertView.findViewById(R.id.enterprise_select).setVisibility(
						View.VISIBLE);

				}
				if (custUser.getCustVCode() == 1) {
					enterpriseName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_v, 0, 0, 0);
					enterpriseName.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.dimen_5dp));

				} else {
					enterpriseName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_no_v, 0, 0, 0);
					enterpriseName.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.dimen_5dp));
				}
				enterpriseID.setText("ID:" + custUser.getCustUniqueId());
				enterpriseID.setVisibility(View.VISIBLE);
			} else {
				enterpriseID.setVisibility(View.GONE);
			}
			enterpriseName.setText(custName);

			return convertView;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
		long id) {
		position = position - listView.getHeaderViewsCount();
		int[] detailPosition = SectionAdapter.getDetailPosition(adapter,
			position);
		if (detailPosition != null && detailPosition.length == 2) {
			UserLoginInfoList section = (UserLoginInfoList) adapter
				.getSection(detailPosition[0]);
			if ("我的单位".equals(section.getGroupName())) {
				UserLoginInfo loginInfo = (UserLoginInfo) adapter.getItem(
					detailPosition[0], detailPosition[1]);
				loginInfo.setPassword(AccountManager.getPassword(this));

				if (loginInfo.getContentId() != Long.valueOf(
					AccountManager.getCurrent(getApplicationContext())
						.getCustId())) {
					//TODO：更换企业添加表示
					StorageUtils.getSystemSettings(this).edit()
						.putBoolean(Config.IS_DEPT_CHANGED, true)
						.commit();
					loginWidthUserLoginInfo(loginInfo);

                    /*记录操作 0205*/
                    OperaEventUtils.recordOperation(OperaEvent.OPERA_CHANGE_ENTERPRISE);
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (cancelable != null) {
			cancelable.cancel();
			cancelable = null;
		}
		if (enterpriseListCancelable != null) {
			enterpriseListCancelable.cancel();
			enterpriseListCancelable = null;
		}
	}

	private void loginWidthUserLoginInfo(UserLoginInfo loginInfo) {
		// repeated request,cancel it.
		if (cancelable != null) {
			cancelable.cancel();
			cancelable = null;
		}

		cancelable = ApiHelper.realLogin(new Listener<Response<UserInfo>>() {

			@Override
			public void onResponse(Response<UserInfo> response) {
				cancelable = null;
				progressDialog.dismiss();
				if (response.getCode() == 0) {
					UserInfo info = response.getPayload();
					if (info != null) {
						AccountManager.updateUserInfo(EnterpriseListActivity.this, info);
						AccountManager.updateCurrentLogiInfoId(EnterpriseListActivity.this, info.getId());
						final long userId = info.getId();
						final String custId = info.getCustId();
						// 百度推送需要的电话号码
						final String phone = info.getPhone();
						new Thread() {
							public void run() {
								// 使用友盟时使用的setTag
								//								MainApplication.setAlias("" + userId);
								// 使用百度时使用的setTag
								MainApplication.setAlias("" + userId, phone);
								MainApplication.addTag("cust" + custId);
							};
						}.start();
					}
					MainApplication
						.sendLoginBroadcast(EnterpriseListActivity.this);
					//					MainApplication.clearContacts();
					ContactManager.destoryContactManager();
					adapter.notifyDataSetChanged();
					Toast.makeText(getApplicationContext(),
						"已切换到：" + info.getCustName(),
						Toast.LENGTH_SHORT).show();
					MainActivity.launchClearTask(EnterpriseListActivity.this);
				} else if (response.getCode() == Response.RESPONSE_NOT_YOUR_ICCID||response.getCode() == Response.RESPONSE_OTHER_ICCID) {
					LeaveEnterpriseActivity.launch(response.getDescription());
				}
				else {
					Toast.makeText(getApplicationContext(),
						response.getDescription(),
						Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onErrorResponse(InvocationError arg0) {
				progressDialog.dismiss();
				cancelable = null;
			}
		}, loginInfo);

		progressDialog = ProgressDialog.show(this, null, "正在切换...");
		progressDialog
			.setOnCancelListener(new DialogInterface.OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					if (cancelable != null) {
						cancelable.cancel();
						cancelable = null;
					}
				}
			});

		//Xiong:
		new UpdateCheckServerLoader().load();
	}

	public static void launch(Context context) {
		Intent intent = new Intent(context, EnterpriseListActivity.class);
		context.startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.create_enterprise:
			CreateEnterpriseActivity.launch(this);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
		int position, long id) {
		position = position - listView.getHeaderViewsCount();
		int[] detailPosition = SectionAdapter.getDetailPosition(adapter,
			position);
		if (detailPosition != null && detailPosition.length == 2) {
			UserLoginInfoList section = (UserLoginInfoList) adapter
				.getSection(detailPosition[0]);
			if ("我的单位".equals(section.getGroupName())) {
				UserLoginInfo loginInfo = (UserLoginInfo) adapter.getItem(
					detailPosition[0], detailPosition[1]);
				ModifyEnterpriseInfoActivity.launchWithNotModify(this, loginInfo.getContentId());
				return true;
			}
		}
		return false;
	}
}
