package cn.yjt.oa.app.attendance;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import cn.robusoft.CTBeaconManager;
import cn.robusoft.CTRangeNotifier;
import cn.robusoft.ErrorType;
import cn.robusoft.WrapperResultInfo;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.attendance.ShakeManager.OnSensorChangedListener;
import cn.yjt.oa.app.beacon.Beacon;
import cn.yjt.oa.app.beans.BeaconInfo;
import cn.yjt.oa.app.beans.SigninInfo;
import cn.yjt.oa.app.signin.AttendanceActivity;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.utils.UserData;

public class BeaconShakeFragment extends Fragment implements CTRangeNotifier {

	private static final String TAG = "BeaconShakeFragment";

	private static final int REQUEST_CODE_ALL_BEACON = 1;
	private static final int MAX_SHAKE_TIMES = 3;

	private View root;
	private Vibrator vibrator;

	private Handler handler;
	private boolean isVibrating;
	private CTBeaconManager beaconManager;

	private List<Beacon> beacons = new ArrayList<Beacon>();
	private ImageView shakeImage;

	private int shakedTimes;

	private ShakeManager manager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler();
		ShakeSound.getInstance().load();
		new BeaconServerLoader().load();
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		ShakeSound.getInstance().release();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (root == null) {

			root = inflater.inflate(R.layout.fragment_shake, container, false);
			shakeImage = (ImageView) root.findViewById(R.id.img_shake);
			//
			// manager = (SensorManager) getActivity().getSystemService(
			// Context.SENSOR_SERVICE);
			manager = new ShakeManager(getActivity());
			vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
			beaconManager = CTBeaconManager.getInstance(getActivity().getApplicationContext());
			beaconManager.setBetweenScanPeriod(2000);
			beaconManager.setScanPeriod(3000);
			beaconManager.registerNotifier(this);
		}

		return root;
	}

	@Override
	public void onResume() {
		super.onResume();
		manager.setShakeSenseValues(4, 4, 4, true);
		manager.registerSensorListener(new OnSensorChangedListener() {

			@Override
			public void onSensorChanged() {
				vibrate();
			}

			@Override
			public boolean onChangeFinish() {
				return true;
			}
		});
		startScanBeacon();
	}

	@Override
	public void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
		release();
	}

	private void release() {
		try {
			manager.unregisterSensorListener();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			vibrator.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}
		stopScanBeacon();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ViewGroup parent = (ViewGroup) root.getParent();
		if (parent != null) {
			parent.removeView(root);
		}
		beacons.clear();
	}

	public List<Beacon> getBeacons() {
		return beacons;
	}

	private void startScanBeacon() {
		beaconManager.start();
	}

	private void stopScanBeacon() {
		beaconManager.stop();
		Log.d(TAG, "stopScanBeacon");
	}

	private void vibrate() {
		if (isVibrating) {
			return;
		}
		isVibrating = true;
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				vibrator.vibrate(new long[] { 100, 200, 100, 100 }, -1);
				playShake();
				animate();
				restoreVibrating();
				postOnShake();
			}

		}, 300);
	}

	private void postOnShake() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (getActivity() != null) {
					onShake();
				}
			}
		}, 1100);
	}

	private void onShake() {
		shakedTimes++;
		if (beacons.isEmpty()) {
			onShakedNone();
		} else if (shakedTimes <= MAX_SHAKE_TIMES && shakedTimes <= beacons.size()) {
			//onShakedBeacon(beacons.get(shakedTimes - 1));
			onShakedBeacon(beacons);
		} else {
			onShakedAllBeacons(beacons);
		}
	}

	private void onShakedBeacon(List<Beacon> beacons2) {
		for (Beacon beacon : beacons2) {
			BeaconInfo matchedBeacon = getMatchedBeacon(beacon);
			if (matchedBeacon != null) {
				playMatch();
				Toast.makeText(getActivity(), "摇到了" + matchedBeacon.getName() + "吆~", Toast.LENGTH_SHORT).show();
				attendance(matchedBeacon);
				return;
			} 
		}
		playNomatch();
		Toast.makeText(getActivity(), "没摇到匹配的吆~,再试一次", Toast.LENGTH_SHORT).show();
	}

	private void onShakedNone() {
		if (!isSupportBeacon()) {
			Toast.makeText(getActivity(), "您的设备不支持蓝牙考勤", Toast.LENGTH_SHORT).show();
		} else if (!isBluetoothOpened()) {
			Toast.makeText(getActivity(), "您还没有打开蓝牙", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getActivity(), "周围没有任何标签吆~", Toast.LENGTH_SHORT).show();
		}
		playNomatch();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public static boolean isBluetoothOpened() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			BluetoothManager bluetoothManager = (BluetoothManager) MainApplication.getAppContext().getSystemService(
					Context.BLUETOOTH_SERVICE);
			return bluetoothManager.getAdapter().isEnabled();
		}
		return false;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public static boolean isSupportBeacon() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			BluetoothManager bluetoothManager = (BluetoothManager) MainApplication.getAppContext().getSystemService(
					Context.BLUETOOTH_SERVICE);
			if (bluetoothManager != null) {
				BluetoothAdapter adapter = bluetoothManager.getAdapter();
				if (adapter != null) {
					return true;
				}
			}
		}
		return false;

	}

	private void onShakedBeacon(Beacon beacon) {
		BeaconInfo matchedBeacon = getMatchedBeacon(beacon);

		if (matchedBeacon != null) {
			playMatch();
			Toast.makeText(getActivity(), "摇到了" + matchedBeacon.getName() + "吆~", Toast.LENGTH_SHORT).show();
			attendance(matchedBeacon);
		} else {
			playNomatch();
			Toast.makeText(getActivity(), "没摇到匹配的吆~,再试一次", Toast.LENGTH_SHORT).show();
		}
	}

	private void onShakedAllBeacons(List<Beacon> beaconInfos) {
		playMatch();
		Toast.makeText(getActivity(), "摇到了全部吆~,请选择一个有效标签进行考勤", Toast.LENGTH_SHORT).show();
		BeaconListActivity.launchWithBeaconInfos(this, buildBeaconInfos(), REQUEST_CODE_ALL_BEACON);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_ALL_BEACON && resultCode == Activity.RESULT_OK) {
			BeaconInfo beaconInfo = data.getParcelableExtra("beaconInfo");
			attendance(beaconInfo);
		}
	}

	private ArrayList<BeaconInfo> buildBeaconInfos() {

		ArrayList<BeaconInfo> arrayList = new ArrayList<BeaconInfo>();

		for (Beacon beacon : beacons) {
			BeaconInfo matchedBeacon = getMatchedBeacon(beacon);
			if (matchedBeacon == null) {
				matchedBeacon = buildBeaconInfo(beacon);
			}
			arrayList.add(matchedBeacon);
		}

		return arrayList;
	}

	private BeaconInfo buildBeaconInfo(Beacon beacon) {
		BeaconInfo beaconInfo = new BeaconInfo();
		beaconInfo.setName("未知");
		beaconInfo.setUumm(beacon.getUumm());
		return beaconInfo;
	}

	private void attendance(BeaconInfo beaconInfo) {
		AttendanceActivity.launchWithSigninInfo(getActivity(), buildSigninInfo(beaconInfo.getUumm()));
	}

	private SigninInfo buildSigninInfo(String uumm) {
		SigninInfo signinInfo = new SigninInfo();
		signinInfo.setType(SigninInfo.SIGNIN_TYPE_BEACON);
		signinInfo.setActrualData(uumm);
		return signinInfo;
	}

	private BeaconInfo getMatchedBeacon(Beacon beacon) {
		List<BeaconInfo> beaconInfos = UserData.getUserAreaBeaconInfos();
		System.out.println("getMatchedBeacon:" + beaconInfos);
		if (beaconInfos != null) {

			for (BeaconInfo beaconInfo : beaconInfos) {
				if (beacon.getUumm().equalsIgnoreCase(beaconInfo.getUumm())) {
					return beaconInfo;
				}
			}
		}
		return null;
	}

	private void animate() {
		Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
		shakeImage.startAnimation(animation);
	}

	private void restoreVibrating() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				isVibrating = false;
			}
		}, 1500);
	}

	// @Override
	// public void onAccuracyChanged(Sensor sensor, int accuracy) {
	//
	// }

	private void playShake() {
		ShakeSound.getInstance().playShake();
	}

	private void playMatch() {
		ShakeSound.getInstance().playMatch();
	}

	private void playNomatch() {
		ShakeSound.getInstance().playNomatch();
	}

	@Override
	public void didRangeBeacons(List<WrapperResultInfo> arg0) {
//		beacons.clear();
		for (WrapperResultInfo wrapperResultInfo : arg0) {
			Beacon beacon = new Beacon(wrapperResultInfo.getUUID() + "-" + wrapperResultInfo.getMajor() + "-"
					+ wrapperResultInfo.getMinor());
			beacon.setDistance(wrapperResultInfo.getDistance() + "");
			beacons.add(beacon);
		}
	}

	@Override
	public void onError(ErrorType arg0) {

	}

}
