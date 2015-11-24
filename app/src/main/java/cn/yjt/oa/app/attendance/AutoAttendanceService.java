package cn.yjt.oa.app.attendance;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import cn.robusoft.CTBeaconManager;
import cn.robusoft.CTRangeNotifier;
import cn.robusoft.ErrorType;
import cn.robusoft.WrapperResultInfo;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.attendance.AttendanceService.AttendanceBinder;
import cn.yjt.oa.app.attendance.AttendanceService.AttendanceListener;
import cn.yjt.oa.app.beacon.Beacon;
import cn.yjt.oa.app.beans.BeaconInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.SigninInfo;
import cn.yjt.oa.app.signin.AttendanceActivity;
import cn.yjt.oa.app.signin.SigninActivity;
import cn.yjt.oa.app.utils.UserData;

/**
 * beacon auto attendance
 */
public class AutoAttendanceService extends Service implements CTRangeNotifier {

	private static final String TAG = "AutoAttendanceService";

	private CTBeaconManager beaconManager;
	private AttendanceBinder attendanceBinder;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			attendanceBinder.unBind(listener);
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			attendanceBinder = (AttendanceBinder) service;
			attendanceBinder.onBind(listener);
		}
	};

	private AttendanceListener listener = new AttendanceListener() {

		@Override
		public void onResponse(Response<SigninInfo> response) {
			if (response.getCode() == Response.RESPONSE_OK) {
				SigninInfo signinInfo = response.getPayload();
				if (signinInfo.getSignResult() == 1) {
					sendAttendanceSuccessNotification(signinInfo
							.getSignResultDesc());
				} else {
					sendAttendanceFailureManualNotification(signinInfo
							.getSignResultDesc());
				}
			} else {
				sendAttendanceFailureManualNotification(response
						.getDescription());
			}

			stopSelf();
		}

		@Override
		public void onRequesting() {

		}

		@Override
		public void onLocationSuccess() {

		}

		@Override
		public void onLocationFailure() {
			sendAttendanceFailureManualNotification("定位失败");
			stopSelf();
		}

		@Override
		public void onLocating() {

		}

		@Override
		public void onError() {
			sendAttendanceFailureManualNotification("网络错误");
			stopSelf();
		}
	};

	private void sendAttendanceSuccessNotification(String description) {
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification baseNF = new Notification();
		baseNF.icon = R.drawable.ic_launcher;
		baseNF.flags = Notification.FLAG_AUTO_CANCEL;
		baseNF.tickerText = "翼机通自动考勤成功";
		baseNF.defaults = Notification.DEFAULT_SOUND;
		baseNF.vibrate = new long[] { 100, 200, 100, 100 };
		Intent intent = new Intent(MainApplication.getAppContext(),
				SigninActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		PendingIntent pd = PendingIntent.getActivity(this, 1, intent, 0);
		baseNF.setLatestEventInfo(this, "翼机通自动考勤成功", description, pd);

		manager.notify(0, baseNF);
	}

	private void sendAttendanceFailureNotification(String description,
			Intent intent) {
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification baseNF = new Notification();
		baseNF.icon = R.drawable.ic_launcher;
		baseNF.flags = Notification.FLAG_AUTO_CANCEL;
		baseNF.tickerText = "翼机通自动考勤失败";
		baseNF.defaults = Notification.DEFAULT_SOUND;
		baseNF.vibrate = new long[] { 100, 200, 100, 200, 100, 200, 100 };
		PendingIntent pd = PendingIntent.getActivity(this, 1, intent, 0);
		baseNF.setLatestEventInfo(this, "翼机通自动考勤失败", description, pd);

		manager.notify(0, baseNF);
	}

	private void sendAttendanceFailureManualNotification(String description) {
		Intent intent = new Intent(MainApplication.getAppContext(),
				BeaconAttendanceActivity.class);
		sendAttendanceFailureNotification(description + ",点击进行手动考勤", intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent != null && intent.hasExtra("requestStop")) {
			stopSelf();
			Log.d(TAG, "onStartCommand.stopself");
		} else {
			boolean autoAttendance = UserData.getAutoAttendance();
			boolean supportBeacon = isSupportBeacon();
			if (autoAttendance && supportBeacon) {

				if (isBluetoothOpened()) {

					if (beaconManager == null) {
						beaconManager = CTBeaconManager
								.getInstance(getApplicationContext());
					}
					beaconManager.registerNotifier(this);
					beaconManager.setBetweenScanPeriod(5000);
					beaconManager.setScanPeriod(5000);
					beaconManager.start();
					Log.d(TAG, "onStartCommand.startScan");
				} else {
					Intent intent2 = new Intent(getApplicationContext(),
							AttendanceActivity.class);
					intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					sendAttendanceFailureNotification("您的蓝牙未打开,点击进行位置考勤",
							intent2);
				}
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public static boolean isBluetoothOpened() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			BluetoothManager bluetoothManager = (BluetoothManager) MainApplication
					.getAppContext()
					.getSystemService(Context.BLUETOOTH_SERVICE);
			return bluetoothManager.getAdapter().isEnabled();
		}
		return false;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public static boolean isSupportBeacon() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			BluetoothManager bluetoothManager = (BluetoothManager) MainApplication
					.getAppContext()
					.getSystemService(Context.BLUETOOTH_SERVICE);
			if (bluetoothManager != null) {
				BluetoothAdapter adapter = bluetoothManager.getAdapter();
				if (adapter != null) {
					return true;
				}
			}
		}
		return false;

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			unbindService(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Intent getStartIntent(Context context) {
		Log.d(TAG, "getStartIntent");
		Intent intent = new Intent(context, AutoAttendanceService.class);
		return intent;
	}

	public static Intent getStopIntent(Context context) {
		Log.d(TAG, "getStopIntent");
		Intent intent = new Intent(context, AutoAttendanceService.class);
		intent.putExtra("requestStop", true);
		return intent;
	}


	private void bindAttendanceService(BeaconInfo matchedBeacon) {
		Intent service = new Intent(this, AttendanceService.class);
		service.putExtra("SigninInfo", buildSigninInfo(matchedBeacon.getUumm()));
		bindService(service, conn, Context.BIND_AUTO_CREATE);
	}


	private SigninInfo buildSigninInfo(String uumm) {
		SigninInfo signinInfo = new SigninInfo();
		signinInfo.setType(SigninInfo.SIGNIN_TYPE_BEACON);
		signinInfo.setActrualData(uumm);
		return signinInfo;
	}

	private BeaconInfo getMatchedBeacon(WrapperResultInfo beacon) {
		List<BeaconInfo> beaconInfos = UserData
				.getFavorateUserAreaBeaconInfos();
		Log.d(TAG, "getMatchedBeacon beaconinfos = " + beaconInfos);
		if (beaconInfos != null) {
			for (BeaconInfo beaconInfo : beaconInfos) {
				if ((beacon.getUUID()+"-"+beacon.getMajor()+"-"+beacon.getMinor()).equalsIgnoreCase(beaconInfo.getUumm())) {
					Log.d(TAG, "find MatchedBeacon");
					return beaconInfo;
				}
			}
		}
		return null;
	}

	@Override
	public void didRangeBeacons(List<WrapperResultInfo> arg0) {
		for (WrapperResultInfo wrapperResultInfo : arg0) {
			
			BeaconInfo matchedBeacon = getMatchedBeacon(wrapperResultInfo);
			if (matchedBeacon != null) {
				beaconManager.stop();
				bindAttendanceService(matchedBeacon);
			}		
		}
	}

	@Override
	public void onError(ErrorType arg0) {
		// TODO Auto-generated method stub
		
	}

}
