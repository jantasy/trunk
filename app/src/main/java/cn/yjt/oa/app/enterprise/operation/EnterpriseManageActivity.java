package cn.yjt.oa.app.enterprise.operation;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.Cancelable;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import cn.yjt.oa.app.LaunchActivity;
import cn.yjt.oa.app.MainActivity;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.enterprise.AttendanceReportsActivity;
import cn.yjt.oa.app.enterprise.AttendanceSettingListActivity;
import cn.yjt.oa.app.enterprise.AttendanceTagListActivity;
import cn.yjt.oa.app.enterprise.AttendanceTimeActivity;
import cn.yjt.oa.app.enterprise.BeaconSettingListActivity;
import cn.yjt.oa.app.patrol.activitys.PatrolManageActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.industry.SettingIndustryActivity;
import cn.yjt.oa.app.notifications.NotificationActivity;
import cn.yjt.oa.app.personalcenter.LoginActivity;
import cn.yjt.oa.app.utils.ViewUtil;

/**企业管理界面*/
public class EnterpriseManageActivity extends TitleFragmentActivity implements OnClickListener {

	private final String TAG = "EnterpriseManageActivity";

	/**注销企业的任务*/
	private Cancelable deleteEnterpriseCancelable;
	/**带进度条的弹窗*/
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		//判断是否是未登录就启动
		if (ViewUtil.noLoginToLaunchAndDashBoardAllowed(this)) {
			LaunchActivity.launch(this);
			finish();
		} else {
			//判断权限，权限不足直接跳入主界面
			String permission = AccountManager.getCurrent(this).getPermission();
			if (TextUtils.isEmpty(permission)) {
				MainActivity.launch(this);
				finish();
			}
            /*记录操作 0206*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_ENTERPRISE_MANAGE);

			setContentView(R.layout.activity_enterprise_manage);
			fillDate();
			setListener();

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (deleteEnterpriseCancelable != null) {
			deleteEnterpriseCancelable.cancel();
			deleteEnterpriseCancelable = null;
		}
	}

	/*-----onCreate中执行的方法START-----*/
	/**向控件中填充数据*/
	private void fillDate() {
		getLeftbutton().setImageResource(R.drawable.navigation_back);
	}

	/**设置监听*/
	private void setListener() {
		findViewById(R.id.enterprise_manage_memeber_list).setOnClickListener(this);
		findViewById(R.id.enterprise_manage_send_notice).setOnClickListener(this);
		findViewById(R.id.enterprise_manage_modify_info).setOnClickListener(this);
		findViewById(R.id.enterprise_manage_attendance_set).setOnClickListener(this);
		findViewById(R.id.enterprise_manage_nfc_tag).setOnClickListener(this);
		findViewById(R.id.enterprise_manage_beacon_tag).setOnClickListener(this);
		findViewById(R.id.enterprise_manage_setting_industry).setOnClickListener(this);
		findViewById(R.id.enterprise_manage_setting_attendanceReports).setOnClickListener(this);
		findViewById(R.id.enterprise_manage_deleteEnterprise).setOnClickListener(this);
		findViewById(R.id.enterprise_manage_attendance_time).setOnClickListener(this);
		findViewById(R.id.enterprise_manage_patrol_manage).setOnClickListener(this);
	}

	/*-----onCreate中执行的方法END-----*/

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		//点击企业成员列表
		case R.id.enterprise_manage_memeber_list:
			memeberList();
             /*记录操作 0207*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_WATCH_MEMBERLISTS);
			break;

		//点击企业通知	
		case R.id.enterprise_manage_send_notice:
			sendNotice();
			break;

		//点击修改企业信息
		case R.id.enterprise_manage_modify_info:
			modifyInfo();
            /*记录操作 0212*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_MODIFY_ENTERPRISE_INFO);
			break;

		//点击考勤区域列表
		case R.id.enterprise_manage_attendance_set:
			createAttendanceSet();
			/*记录操作 0810*/
			OperaEventUtils.recordOperation(OperaEvent.OPERA_WATCH_ATTENDANCE_AREALISTS);
			break;

		//点击考勤时间
		case R.id.enterprise_manage_attendance_time:
			attendanceTime();

			/*记录操作 0815*/
			OperaEventUtils.recordOperation(OperaEvent.OPERA_WATCH_ATTENDANCE_TIME);
			break;

		//点击管理本企业的nfc标签
		case R.id.enterprise_manage_nfc_tag:
			attendanceTagList();

			/*记录操作 0819*/
			OperaEventUtils.recordOperation(OperaEvent.OPERA_WATCH_ATTENDANCES_NFCTAGLISTS);
			break;

		//点击管理本企业的蓝牙考勤标签
		case R.id.enterprise_manage_beacon_tag:
			beaconList();

			/*记录操作 0821*/
			OperaEventUtils.recordOperation(OperaEvent.OPERA_WATCH_ATTENDANCE_BEACONTAG);
			break;

		//点击考勤报表
		case R.id.enterprise_manage_setting_attendanceReports:
			attendanceReports();
			break;

		//点击注销企业
		case R.id.enterprise_manage_deleteEnterprise:
			showDeleteEnterpriseConfirmDialog();
             /*记录操作 0213*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_LOGOUT_ENTERPRISE);

			break;

		case R.id.enterprise_manage_setting_industry:
			createSettingIndustry();
			break;

		default:
			break;
		}
	}

	/**跳转到企业成员列表页面*/
	private void memeberList() {
		EnterpriseMembersActivity.launch(this);
	}

	/**跳转到电子公告页面*/
	private void sendNotice() {
		NotificationActivity.launch(this);
	}

	/**跳转到修改企业信息的页面*/
	private void modifyInfo() {
		ModifyEnterpriseInfoActivity.launch(this);
	}

	/**跳转到考勤时间设置页面*/
	private void attendanceTime() {
		//AttendanceTimeSettingActivity.launch(this);
		AttendanceTimeActivity.launch(this);
	}

	/**跳转到考勤区域设置页面*/
	private void createAttendanceSet() {
		AttendanceSettingListActivity.launch(this);
	}

	/**跳转到这时nfc考勤标签页面*/
	private void attendanceTagList() {
		AttendanceTagListActivity.launch(this);
	}

	/**
	 * 跳转到蓝牙考勤标签页面，
	 * 首先判断手机的android版本是否达到要求，如达到要求就跳转，否贼给出土司提示
	 */
	private void beaconList() {
		if (VERSION.SDK_INT < VERSION_CODES.JELLY_BEAN_MR2) {
			Toast.makeText(getApplicationContext(), "您的手机不支持Beacon设定", Toast.LENGTH_SHORT).show();
			return;
		}
		BeaconSettingListActivity.launch(this);
	}

	/**跳转到考勤报表的页面*/
	private void attendanceReports() {
		AttendanceReportsActivity.launch(this);
	}

	/**跳转到巡更管理页面*/
	private void patrolManange() {
		PatrolManageActivity.launch(this);
	}

	/**弹出是否确定注销企业的对话框*/
	private void showDeleteEnterpriseConfirmDialog() {
		AlertDialogBuilder.newBuilder(this).setMessage("注销后企业将不能恢复，是否注销？")
				.setPositiveButton("注销", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//注销企业
						deleteEnterprise();
					}
				}).setNegativeButton("取消", null).show();
	}

	/**注销企业*/
	private void deleteEnterprise() {
		Type responseType = new TypeToken<Response<String>>() {
		}.getType();
		deleteEnterpriseCancelable = new AsyncRequest.Builder().setModule(AsyncRequest.MODULE_CUSTS_CLOSE)
				.setModuleItem(AccountManager.getCurrent(getApplicationContext()).getCustId())
				.setResponseType(responseType).setResponseListener(new Listener<Response<String>>() {

					@Override
					public void onErrorResponse(InvocationError arg0) {
						dismissDialog();
						deleteEnterpriseCancelable = null;
						Toast.makeText(getApplicationContext(), "网络请求失败，请稍后再试", Toast.LENGTH_SHORT).show();

					}

					@Override
					public void onResponse(Response<String> arg0) {
						dismissDialog();
						deleteEnterpriseCancelable = null;
						if (arg0.getCode() == 0) {
							LoginActivity.launchWithEnterpriseList(EnterpriseManageActivity.this);
						} else {
							Toast.makeText(getApplicationContext(), arg0.getDescription(), Toast.LENGTH_SHORT).show();
						}
					}
				}).build().put();
		progressDialog = ProgressDialog.show(this, null, "正在注销当前企业");
		progressDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				deleteEnterpriseCancelable.cancel();
				deleteEnterpriseCancelable = null;
			}
		});
	}

	/**关闭对话框*/
	private void dismissDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	private void createSettingIndustry() {
		SettingIndustryActivity.launch(this);
	}
}
