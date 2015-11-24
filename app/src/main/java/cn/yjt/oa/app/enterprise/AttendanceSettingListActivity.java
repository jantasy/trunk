package cn.yjt.oa.app.enterprise;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.Cancelable;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.CustSignCommonInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.ResponseListener;
import cn.yjt.oa.app.signin.AttendanceSetActivity;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

/**设置考勤区域*/
public class AttendanceSettingListActivity extends TitleFragmentActivity implements OnRefreshListener,
		OnItemClickListener {

	private final String TAG = "AttendanceSettingListActivity";

	/**自定义的下拉刷新和上拉加载的listview，用来展示考勤区域信息*/
	private PullToRefreshListView mLvShowAttendanceArea;

	/**适配考勤区域listview显示的适配器*/
	private AttendanceSettingAdapter adapter;

	/**请求考勤区域信息的任务*/
	private Cancelable mRequestCancelable;
	/**删除某一条考勤区域信息的任务*/
	private Cancelable mDeleteCancelable;

	/**带进度条的对话框，用于删除时显现*/
	private ProgressDialog mPdDeleting;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_attendance_setting_list);
		initParams();
		initView();
		fillData();
		setListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mLvShowAttendanceArea.setRefreshingState();
		requestCustSignCommonInfos();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mRequestCancelable != null) {
			mRequestCancelable.cancel();
		}
	}

	/*-----onCreate中执行的方法START-----*/
	/**初始化一些成员变量*/
	private void initParams() {
		adapter = new AttendanceSettingAdapter();
	}

	/**初始化控件*/
	private void initView() {
		mLvShowAttendanceArea = (PullToRefreshListView) findViewById(R.id.attendance_setting_listview);
	}

	/**向控件中填充数据*/
	private void fillData() {
		mLvShowAttendanceArea.setAdapter(adapter);
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		getRightButton().setImageResource(R.drawable.contact_add_group);
	}

	/**设置监听*/
	private void setListener() {
		mLvShowAttendanceArea.setOnRefreshListener(this);
		mLvShowAttendanceArea.enableFooterView(false);
		mLvShowAttendanceArea.setOnItemClickListener(this);
	}

	/*-----onCreate中执行的方法END-----*/

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	@Override
	public void onRightButtonClick() {
		AttendanceSetActivity.launch(this);
		super.onRightButtonClick();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		AttendanceSetActivity.launchWithCustSignCommonInfo(AttendanceSettingListActivity.this,
				(CustSignCommonInfo) parent.getItemAtPosition(position));
	}

	@Override
	public void onRefresh() {
		requestCustSignCommonInfos();
	}

	/**请求考勤区域*/
	private void requestCustSignCommonInfos() {
		Type responseType = new TypeToken<Response<List<CustSignCommonInfo>>>() {
		}.getType();
		mRequestCancelable = new AsyncRequest.Builder()
				.setModule(
						String.format(AsyncRequest.MODULE_CUSTS_SIGNCOMMON_LISTS,
								AccountManager.getCurrent(getApplicationContext()).getCustId()))
				.setResponseType(responseType).setResponseListener(new ResponseListener<List<CustSignCommonInfo>>() {

					@Override
					public void onFinish() {
						super.onFinish();
						mLvShowAttendanceArea.onRefreshComplete();
						mRequestCancelable = null;
					}

					@Override
					public void onSuccess(List<CustSignCommonInfo> payload) {
						if (payload != null) {
							//成功之后刷新listview
							adapter.data = payload;
							adapter.notifyDataSetChanged();
						}
					}

				}).build().get();
	}

	/**考勤区域展示适配器*/
	private class AttendanceSettingAdapter extends BaseAdapter {
		/**用于适配数据的list集合*/
		List<CustSignCommonInfo> data = Collections.emptyList();
		/**初始化布局填充器*/
		LayoutInflater inflater = LayoutInflater.from(AttendanceSettingListActivity.this);

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_attendancesetting_list, parent, false);
			}
			//获取当前条目在数据源集合中对应的对象
			final CustSignCommonInfo info = (CustSignCommonInfo) getItem(position);
			//将考勤区域的名称显示出来
			TextView poiName = (TextView) convertView.findViewById(R.id.poi_name);
			poiName.setText(info.getName());
			//条目右边的垃圾桶图标被点击后删除该条目的信息
			convertView.findViewById(R.id.btn_delete).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showDeleteConfirmDialog(info);
				}
			});
			return convertView;
		}
	}

	/**弹出是否确定删除考勤区域的对话框*/
	private void showDeleteConfirmDialog(final CustSignCommonInfo info) {
		//如果删除区域，则关联的所有人员自动解除关联
		AlertDialogBuilder.newBuilder(this).setMessage("如果删除区域，则关联的所有人员自动解除关联")
				.setPositiveButton("删除", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						delete(info);

						 /*记录操作 0814*/
						OperaEventUtils.recordOperation(OperaEvent.OPERA_DELETE_ATTENDANCE_AREA);
					}
				}).setNegativeButton("取消", null).show();
	}

	/**删除考勤区域对象*/
	private void delete(final CustSignCommonInfo info) {
		//删除的时候给个进度条对话框
		mPdDeleting = ProgressDialog.show(this, null, "正在删除...");
		mPdDeleting.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if (mDeleteCancelable != null) {
					mDeleteCancelable.cancel();
					mDeleteCancelable = null;
				}
			}
		});

		//开始删除
		Type responseType = new TypeToken<Response<String>>() {
		}.getType();
		mDeleteCancelable = new AsyncRequest.Builder()
				.setModule(
						String.format(AsyncRequest.MODULE_CUSTS_SIGNCOMMON,
								AccountManager.getCurrent(getApplicationContext()).getCustId()))
				.setModuleItem(String.valueOf(info.getId())).setResponseType(responseType)
				.setResponseListener(new Listener<Response<String>>() {

					@Override
					public void onErrorResponse(InvocationError arg0) {
						mDeleteCancelable = null;
						mPdDeleting.dismiss();
						mPdDeleting = null;
						Toast.makeText(getApplicationContext(), "删除失败", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onResponse(Response<String> arg0) {
						//删除成功关闭进度对话框，刷新列表
						mDeleteCancelable = null;
						mPdDeleting.dismiss();
						mPdDeleting = null;
						if (arg0.getCode() == 0) {
							adapter.data.remove(info);
							adapter.notifyDataSetChanged();
						} else {
							Toast.makeText(getApplicationContext(), arg0.getDescription(), Toast.LENGTH_SHORT).show();
						}

					}
				}).build().delete();
	}

	/**启动该界面*/
	public static void launch(Context context) {
		Intent intent = new Intent(context, AttendanceSettingListActivity.class);
		context.startActivity(intent);
	}
}
