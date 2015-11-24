package cn.yjt.oa.app.attendance;

import io.luobo.common.Cancelable;
import io.luobo.common.http.InvocationError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;
import cn.robusoft.CTBeaconManager;
import cn.robusoft.CTRangeNotifier;
import cn.robusoft.ErrorType;
import cn.robusoft.WrapperResultInfo;
import cn.yjt.oa.app.beans.BeaconInfo;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.SigninInfo;
import cn.yjt.oa.app.http.ApiHelper;
import cn.yjt.oa.app.http.ResponseListener;
import cn.yjt.oa.app.signin.AttendanceActivity;
import cn.yjt.oa.app.utils.ErrorUtils;

/**
 *	手动蓝牙考勤的Activity
 *
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BeaconAttendanceActivity extends Activity {

	//扫描周期
	private static final long SCAN_PERIOD = 2000;
	//蓝牙适配器
	private BluetoothAdapter mBluetoothAdapter;
	//handler对象（静态？）
	private Handler mHandler;

	private List<BeaconInfo> attendanceBeacons;

	private ProgressDialog decodeProgressDialog;
	private ProgressDialog scanProgressDialog;
	private ProgressDialog progressDialog;
	private Cancelable cancelable;
	private List<String> uumms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uumms = getIntent().getStringArrayListExtra("uumms");
		//初始化handler
		mHandler = new Handler();
		//将该activity北京设置为透明的
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		checkVersion();
		checkIfSupportBeacon();
		checkIfSupportBluetooth();
		checkIfBluetoothOpened();
		requestAvailableBeacons();
	}

	/**
	 * 检测设备版本是否达到使用beacon的要求
	 * 否则给出提示语，然后关闭该界面
	 */
	private void checkVersion() {
		if (VERSION.SDK_INT < VERSION_CODES.JELLY_BEAN_MR2) {
			Toast.makeText(this, "Beacon只支持4.3系统以上", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	/**
	 * 检测设备是否支持蓝牙
	 * 如果支持，获取蓝牙适配器
	 * 否则给出提示语，然后关闭该界面
	 */
	private void checkIfSupportBluetooth() {
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "您的手机蓝牙功能无法使用", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
	}

	/**
	 * 检测设备是否支持Beacon
	 * 否则给出提示语，然后关闭该界面
	 */
	private void checkIfSupportBeacon() {
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, "您的手机不支持蓝牙考勤", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	/**
	 * 检测设备是否打开蓝牙
	 * 否则给出提示语，然后关闭该界面 
	 */
	private void checkIfBluetoothOpened() {
		BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		if (manager != null && !manager.getAdapter().isEnabled()) {
			Toast.makeText(this, "您的蓝牙未打开，请在设置中打开", Toast.LENGTH_SHORT).show();
			finish();
		}
	}
	
	/**
	 * 请求可以使用的beacons
	 */
	private void requestAvailableBeacons() {
		if (isFinishing()) {
			return;
		}
		//请求服务器获取当前企业设置的蓝牙考勤标签
		cancelable = ApiHelper
				.getBeacons(new ResponseListener<ListSlice<BeaconInfo>>() {
					@Override
					public void onSuccess(ListSlice<BeaconInfo> payload) {
						//请求成功
						//如果企业没有设置蓝牙考勤标签，给出提示，然后关闭当前界面
						if (payload.getTotal() == 0) {
							AttendanceActivity.launchWithState(
									BeaconAttendanceActivity.this,
									AttendanceActivity.STATE_REQUEST_RESPONSE,
									"您的企业还未设置蓝牙考勤标签");
							finish();
							return;
						}
						
						//获取请求到的当前企业的beacon信息的集合
						attendanceBeacons = payload.getContent();
						String findAttendanceBeancon = findAttendanceBeancon(uumms);
						attendance(buildSigninInfo(findAttendanceBeancon));
						dismissRequestProgressDialog();
					}

					@Override
					public void onErrorResponse(InvocationError error) {
						//请求失败
						super.onErrorResponse(error);
						//给出失败的提示信息
						Toast.makeText(
								getApplicationContext(),
								ErrorUtils.getErrorDescription(error
										.getErrorType()), Toast.LENGTH_SHORT)
								.show();
						
						cancelRequestProgressDialog();
					}

					@Override
					public void onErrorResponse(
							Response<ListSlice<BeaconInfo>> response) {
						//请求失败
						super.onErrorResponse(response);
						Toast.makeText(getApplicationContext(),
								response.getDescription(), Toast.LENGTH_SHORT)
								.show();
						cancelRequestProgressDialog();
					}

					@Override
					public void onFinish() {
						super.onFinish();
						System.out.println("onFinish:"
								+ Thread.currentThread().getName());

					}

				});
		showRequestProgressDialog();
	}
	

	/**
	 * 关闭解析进度的对话框
	 */
	private void dismissDecodeProgressDialog() {
		if (decodeProgressDialog != null) {
			decodeProgressDialog.dismiss();
			decodeProgressDialog = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	/**
	 * 停止扫描，关闭扫面进度窗口
	 */
	private void stopScanLeDevice() {
		if (scanProgressDialog != null) {
			scanProgressDialog.dismiss();
		}
	}

	/**
	 * 显示扫描进度的对话框
	 */
	private void showScanProgressDialog() {
		if (isFinishing()) {
			return;
		}
		scanProgressDialog = ProgressDialog.show(this, null, "正在扫描周围蓝牙标签");
		scanProgressDialog.setCancelable(true);
		//设置取消监听事件
		scanProgressDialog
				.setOnCancelListener(new DialogInterface.OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						scanProgressDialog = null;
						stopScanLeDevice();
						finish();
					}
				});
	}
	
	/**
	 * 显示获取蓝牙标签进度的对话框
	 */
	private void showRequestProgressDialog() {
		progressDialog = ProgressDialog.show(this, null, "正在获取可用蓝牙标签...");
		progressDialog.setCancelable(true);
		progressDialog
				.setOnCancelListener(new DialogInterface.OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						if (cancelable != null) {
							cancelable.cancel();
							cancelable = null;
						}
						finish();

						System.out.println("onCancel finish()");
					}
				});
	}

	/**
	 * 关闭请求进度的对话框
	 */
	private void dismissRequestProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
			cancelable = null;
		}
	}

	/**
	 * 关闭请求进度对话框，并且结束请求任务
	 */
	private void cancelRequestProgressDialog() {
		if (progressDialog != null) {
			progressDialog.cancel();
			progressDialog = null;
			cancelable = null;
		}
	}


	private SigninInfo buildSigninInfo(String	uumm) {
		if (uumms == null) {
			return null;
		}
		SigninInfo signinInfo = new SigninInfo();
		signinInfo.setType(SigninInfo.SIGNIN_TYPE_BEACON);
		signinInfo.setActrualData(uumm);
		return signinInfo;
	}

	private void attendance(SigninInfo signinInfo) {
		if (signinInfo != null) {
			AttendanceActivity.launchWithSigninInfo(this, signinInfo);
		} else {
			AttendanceActivity.launchWithState(this,
					AttendanceActivity.STATE_REQUEST_RESPONSE, "未搜索到有效蓝牙标签");
		}
		finish();
	}

	private String findAttendanceBeancon(List<String> uumms) {
		if (attendanceBeacons != null) {
			for (String uumm : uumms) {

				for (BeaconInfo beaconInfo : attendanceBeacons) {
					if(beaconInfo.getUumm().equalsIgnoreCase(uumm)){
						return uumm;
					}
				}
			}
		}
		return null;
	}

	private boolean equals(WrapperResultInfo wrapperResultInfo, BeaconInfo beaconInfo) {
		if (wrapperResultInfo == null || beaconInfo == null) {
			return false;
		}
		String uumm = wrapperResultInfo.getUUID()+"-"+wrapperResultInfo.getMajor()+"-"+wrapperResultInfo.getMinor();
		String uumm1 = beaconInfo.getUumm();
		return TextUtils.equals(uumm, uumm1);
	}


	public static void sortBeaconListByDistance(List<WrapperResultInfo> list) {
		Collections.sort(list, new Comparator<WrapperResultInfo>() {

			@Override
			public int compare(WrapperResultInfo iBeacon1, WrapperResultInfo iBeacon2) {
				return Double.valueOf(iBeacon1.getDistance()).compareTo(
						Double.valueOf(iBeacon2.getDistance()));
			}
		});
	}

	/**
	 * 启动该activity
	 * @param context 上下文
	 */
	public static void launch(Context context,ArrayList<String> uumms) {
		Intent intent = new Intent(context, BeaconAttendanceActivity.class);
		if (!(context instanceof Activity)) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		intent.putStringArrayListExtra("uumms", uumms);
		context.startActivity(intent);
	}
}
