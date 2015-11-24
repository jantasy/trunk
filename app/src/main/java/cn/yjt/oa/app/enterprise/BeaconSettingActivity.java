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
import java.util.Comparator;
import java.util.List;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cn.robusoft.CTBeaconManager;
import cn.robusoft.CTRangeNotifier;
import cn.robusoft.ErrorType;
import cn.robusoft.WrapperResultInfo;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beacon.Beacon;
import cn.yjt.oa.app.beans.BeaconInfo;
import cn.yjt.oa.app.beans.CustSignCommonInfo;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.component.BackTitleFragmentActivity;
import cn.yjt.oa.app.http.ApiHelper;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.ProgressDialogResponseListener;
import cn.yjt.oa.app.utils.SharedUtils;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BeaconSettingActivity extends BackTitleFragmentActivity implements
		OnRefreshListener, OnItemClickListener, OnClickListener {

	private List<WrapperResultInfo> infos = new ArrayList<WrapperResultInfo>();
	private List<Beacon> beacons = new ArrayList<Beacon>();

	private ProgressDialog scanProgressDialog;
	private Handler handler;
	private Spinner spinner;
	private Cancelable cancelable;
	private ProgressDialog progressDialog;
	private AutoCompleteTextView beaconName;

	// 所有记录在sp中的tag的名称数组
	private String[] arr_tags;
	private ArrayAdapter<String> adapter_auto;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_beacon_setting);
		handler = new Handler();

		beaconManager = CTBeaconManager.getInstance(getApplicationContext());
		beaconManager.setDebugMode(true);
		beaconManager.registerNotifier(ctRangeNotifier);
		beaconManager.setBetweenScanPeriod(5000);
		beaconManager.setScanPeriod(5000);
		beaconManager.setMaxScanBeacons(1);

		checkVersion();
		checkIfSupportBluetooth();
		checkIfSupportBeacon();
		checkIfBluetoothOpened();

		listView = (PullToRefreshListView) findViewById(R.id.beacon_list);
		listView.setOnRefreshListener(this);
		adapter = new BeaconListAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		listView.enableFooterView(false);
		scanLeDevice();

		spinner = (Spinner) findViewById(R.id.attendance_area);
		attendanceAreaAdapter = new AttendanceAreaAdapter(this);
		spinner.setAdapter(attendanceAreaAdapter);

		beaconName = (AutoCompleteTextView) findViewById(R.id.beacon_name);
		beaconName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				beaconName.showDropDown();
				initAutoCompleteTextView();
				if (beaconName.getText().toString() != null
						&& !"".equals(beaconName.getText().toString())) {
					beaconName.setText(beaconName.getText().toString());
					beaconName.setSelection(beaconName.getText().toString()
							.length());
				}
			}
		});
		beaconName.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (beaconName.getText().toString() == null
							|| "".equals(beaconName.getText().toString())) {
						beaconName.showDropDown();
					}
				}
			}
		});

		findViewById(R.id.beacon_btn).setOnClickListener(this);
		request();

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		beaconManager.setMaxScanBeacons(100);
		beaconManager.stop();
	}

	private CTRangeNotifier ctRangeNotifier = new CTRangeNotifier() {

		@Override
		public void didRangeBeacons(List<WrapperResultInfo> arg0) {
//			beaconManager.stop();
			infos.clear();
			infos.addAll(arg0);
			handler.post(new Runnable() {

				@Override
				public void run() {
//					dismissScanProgressDialog();
					adapter.notifyDataSetChanged();
					
				}
			});
		}

		@Override
		public void onError(final ErrorType arg0) {
//			beaconManager.stop();
//			handler.post(new Runnable() {
//				
//				@Override
//				public void run() {
//					dismissScanProgressDialog();
//					Toast.makeText(getApplicationContext(), arg0.toString(), Toast.LENGTH_SHORT).show();
//				}
//			});
		}
	};

	private void checkIfBluetoothOpened() {
		BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		if (manager != null && !manager.getAdapter().isEnabled()) {
			AlertDialogBuilder
					.newBuilder(this)
					.setMessage("您的蓝牙未打开，请在设置中打开")
					.setPositiveButton("设置",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											Settings.ACTION_BLUETOOTH_SETTINGS);
									intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									startActivity(intent);
								}
							}).setNegativeButton("取消", null).show();
		}
	}

	/**
	 * 初始化AutoCompleteTextView 显示出最近10条常用的标签
	 */
	private void initAutoCompleteTextView() {
		// 获取目前存储在sp中的标签
		String str_tag = SharedUtils.getBeaconNames(getApplicationContext());
		// 用","分割字符串, 不同的tag用,号分开
		arr_tags = str_tag.split(",");
		List<String> tags = new ArrayList<String>();
		for (int i = 0; i < arr_tags.length; i++) {
			if (!TextUtils.isEmpty(arr_tags[i])) {
				tags.add(arr_tags[i]);
			}
		}
		arr_tags = tags.toArray(new String[tags.size()]);
		// AutoCompleteTextView的adapter
		adapter_auto = new ArrayAdapter<String>(this,
				R.layout.autocompletetextview_item, arr_tags);
		// android.R.layout.simple_list_item_1, arr_tags);
		beaconName.setAdapter(adapter_auto);
	}

	private void request() {
		Type responseType = new TypeToken<Response<List<CustSignCommonInfo>>>() {
		}.getType();
		cancelable = new AsyncRequest.Builder()
				.setModule(
						String.format(
								AsyncRequest.MODULE_CUSTS_SIGNCOMMON_LISTS,
								AccountManager.getCurrent(
										getApplicationContext()).getCustId()))
				.setResponseType(responseType)
				.setResponseListener(
						new Listener<Response<List<CustSignCommonInfo>>>() {
							@Override
							public void onErrorResponse(InvocationError arg0) {
								progressDialog.dismiss();
								progressDialog = null;
								cancelable = null;
							}

							@Override
							public void onResponse(
									Response<List<CustSignCommonInfo>> response) {
								progressDialog.dismiss();
								progressDialog = null;
								cancelable = null;
								if (response.getCode() == Response.RESPONSE_OK) {
									attendanceAreaAdapter.setData(response
											.getPayload());
									attendanceAreaAdapter
											.notifyDataSetChanged();
								} else {
									Toast.makeText(getApplicationContext(),
											response.getDescription(),
											Toast.LENGTH_SHORT).show();
								}
							}

						}).build().get();
		progressDialog = ProgressDialog.show(this, null, "正在获取考勤区域");
		progressDialog.setCancelable(true);
		progressDialog
				.setOnCancelListener(new DialogInterface.OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						cancelable.cancel();
						cancelable = null;
					}
				});
	}

	private void checkVersion() {
		if (VERSION.SDK_INT < VERSION_CODES.JELLY_BEAN_MR2) {
			Toast.makeText(this, "Beacon只支持4.3系统以上", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	private void checkIfSupportBluetooth() {
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();

		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "您的手机蓝牙功能无法使用", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	private void checkIfSupportBeacon() {
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, "您的手机不支持Beacon", Toast.LENGTH_SHORT).show();
		}
	}

	private void scanLeDevice() {
		beacons.clear();
		beaconManager.start();
//		showScanProgressDialog();
	}

	private void stopScanLeDevice() {
		beaconManager.stop();
//		dismissScanProgressDialog();
	}


	// private void decodeBeacon() {
	// //TODO test codes.
	// // infos.clear();
	// // for (Beacon beacon : beacons) {
	// // WrapperResultInfo WrapperResultInfo = new WrapperResultInfo();
	// // WrapperResultInfo.setUumm(Utils.getUumm(beacon));
	// // WrapperResultInfo.setDistanece(String.valueOf(beacon.getDistance()));
	// // infos.add(WrapperResultInfo);
	// // }
	// // adapter.notifyDataSetChanged();
	//
	// final BeaconTask beaconTask = new BeaconTask(this, this, beacons);
	//
	// new Thread(beaconTask).start();
	// decodeProgressDialog = ProgressDialog.show(this, null, "正在解析蓝牙标签");
	// decodeProgressDialog.setCancelable(true);
	// decodeProgressDialog
	// .setOnCancelListener(new DialogInterface.OnCancelListener() {
	//
	// @Override
	// public void onCancel(DialogInterface dialog) {
	// decodeProgressDialog = null;
	// beaconTask.removeRangeNotifer();
	// }
	// });
	// }
	//
	private void showScanProgressDialog() {
		scanProgressDialog = ProgressDialog.show(this, null, "正在扫描周围最近蓝牙标签");
		scanProgressDialog.setCancelable(true);
		scanProgressDialog
				.setOnCancelListener(new DialogInterface.OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						scanProgressDialog = null;
						stopScanLeDevice();
					}
				});
	}
	private void dismissScanProgressDialog(){
		if(scanProgressDialog != null){
			scanProgressDialog.dismiss();
			scanProgressDialog = null;
		}
	}

	// private LeScanCallback mLeScanCallback = new LeScanCallback() {
	//
	// @Override
	// public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
	// {
	// Beacon beacon = beaconParser.fromScanData(scanRecord, rssi, device);
	// if (beacon != null) {
	// if (!beacons.contains(beacon)) {
	// beacons.add(beacon);
	// }
	// }
	// }
	// };
	private BeaconListAdapter adapter;
	private PullToRefreshListView listView;
	private AttendanceAreaAdapter attendanceAreaAdapter;

	public static void launch(Context context) {
		Intent intent = new Intent(context, BeaconSettingActivity.class);
		context.startActivity(intent);
	}

	private class BeaconListAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		public BeaconListAdapter() {
			inflater = LayoutInflater.from(getApplicationContext());
		}

		@Override
		public int getCount() {
			if (infos.isEmpty()) {
				return 0;
			}
			return 1;
		}

		@Override
		public Object getItem(int position) {
			return infos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_beacon_scan_list,
						parent, false);
			}
			TextView uuid = (TextView) convertView
					.findViewById(R.id.beacon_uuid);
			TextView major = (TextView) convertView
					.findViewById(R.id.beacon_major);
			TextView minor = (TextView) convertView
					.findViewById(R.id.beacon_minor);
			TextView distance = (TextView) convertView
					.findViewById(R.id.beacon_distance);
			WrapperResultInfo info = (WrapperResultInfo) getItem(position);
			uuid.setText("UUID:" + info.getUUID());
			major.setText("Major:" + info.getMajor());
			minor.setText("Minor:" + info.getMinor());
			distance.setText("距离:" + info.getDistance() + "米");
			return convertView;
		}
	}

	// private class BeaconListAdapter extends BaseAdapter {
	//
	// private LayoutInflater inflater;
	//
	// public BeaconListAdapter() {
	// inflater = LayoutInflater.from(getApplicationContext());
	// }
	//
	// @Override
	// public int getCount() {
	// if(beacons.isEmpty()){
	// return 0;
	// }
	// return 1;
	// }
	//
	// @Override
	// public Object getItem(int position) {
	// return beacons.get(position);
	// }
	//
	// @Override
	// public long getItemId(int position) {
	// return position;
	// }
	//
	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// if (convertView == null) {
	// convertView = inflater.inflate(R.layout.item_beacon_scan_list,
	// parent, false);
	// }
	// TextView uuid = (TextView) convertView.findViewById(R.id.beacon_uuid);
	// TextView major = (TextView) convertView.findViewById(R.id.beacon_major);
	// TextView minor = (TextView) convertView.findViewById(R.id.beacon_minor);
	// TextView distance = (TextView)
	// convertView.findViewById(R.id.beacon_distance);
	// Beacon info = (Beacon) getItem(position);
	// uuid.setText("UUID:"+Utils.getUUID(info.getUumm()));
	// major.setText("Major:"+Utils.getMajor(info.getUumm()));
	// minor.setText("Minor:"+Utils.getMinor(info.getUumm()));
	// distance.setText("距离:"+info.getDistance()+"米");
	// return convertView;
	// }
	// }

	private void sort(List<WrapperResultInfo> infos) {
		Collections.sort(infos, rssiComparator);
	}

	private static final Comparator<WrapperResultInfo> rssiComparator = new Comparator<WrapperResultInfo>() {

		@Override
		public int compare(WrapperResultInfo lhs, WrapperResultInfo rhs) {
			return (int) (lhs.getDistance() * 10000 - rhs.getDistance() * 10000);
		}
	};
	private CTBeaconManager beaconManager;

	@Override
	public void onRefresh() {
		scanLeDevice();
		listView.onRefreshComplete();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		WrapperResultInfo item = (WrapperResultInfo) parent.getAdapter()
				.getItem(position);
		if (checkBeaconName() && checkSelectedArea()) {
			showAddBeaconConfirmDialog(item);
		}
	}

	private boolean checkBeaconName() {
		if (TextUtils.isEmpty(beaconName.getText())) {
			Toast.makeText(getApplicationContext(), "请填写蓝牙标签名称",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private boolean checkSelectedArea() {
		CustSignCommonInfo selectedItem = (CustSignCommonInfo) spinner
				.getSelectedItem();
		if (selectedItem == null) {
			Toast.makeText(getApplicationContext(), "请选择考勤区域",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private void showAddBeaconConfirmDialog(
			final WrapperResultInfo WrapperResultInfo) {
		CustSignCommonInfo selectedItem = (CustSignCommonInfo) spinner
				.getSelectedItem();
		AlertDialogBuilder.newBuilder(this)
				.setMessage("是否确认添加该蓝牙标签到“" + selectedItem.getName() + "”")
				.setPositiveButton("添加", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						requestAddBeacon(buildBeaconInfo(WrapperResultInfo));
					}
				}).setNegativeButton("取消", null).show();
	}

	private BeaconInfo buildBeaconInfo(WrapperResultInfo wrapperResultInfo) {
		BeaconInfo beaconInfo = new BeaconInfo();
		CustSignCommonInfo selectedItem = (CustSignCommonInfo) spinner
				.getSelectedItem();
		beaconInfo.setAreaId(selectedItem.getId());
		beaconInfo.setName(beaconName.getText().toString());
		beaconInfo.setUumm(wrapperResultInfo.getUUID()+"-"+wrapperResultInfo.getMajor() +"-"+wrapperResultInfo.getMinor());
		return beaconInfo;
	}

	private void requestAddBeacon(BeaconInfo beaconInfo) {
		writeTagOnSharedPreference(beaconInfo.getName());
		ApiHelper.addBeacon(new ProgressDialogResponseListener<BeaconInfo>(
				this, "正在添加...") {

			@Override
			public void onSuccess(BeaconInfo payload) {
				Toast.makeText(getApplicationContext(), "添加成功",
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}, beaconInfo);
	}

	/**
	 * 把tag写入到sp中
	 */
	private void writeTagOnSharedPreference(String current_tagName) {
		// 记录current_tagName在已有的tag数组中的下标, 以便排序
		int index = -1;
		// 判断已有的tag中是否还有本次输入的tag名称
		for (int i = 0; i < arr_tags.length; i++) {
			if (current_tagName.equals(arr_tags[i])) {
				// 记录下坐标用于排序
				index = i;
				break;
			}
		}
		// 排序
		String[] new_tagArrs = SortTagArr(index, current_tagName);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < new_tagArrs.length; i++) {
			sb.append(new_tagArrs[i]);
			if (i != new_tagArrs.length - 1) {
				sb.append(",");
			}
		}
		// 写入到sp中
		SharedUtils.setBeaconNames(getApplicationContext(), sb.toString());
	}

	/**
	 * 对tag进行排序
	 */
	private String[] SortTagArr(int index, String current_Name) {
		if (index < 0) {
			// 表示不需要排序, 将currentName放在最前端, 直接将所有的数组元素依次后移1位
			if (arr_tags.length < 10) {
				// 不足10个
				String[] new_tagArr = new String[arr_tags.length + 1];
				new_tagArr[0] = current_Name;
				for (int i = 0; i < arr_tags.length; i++) {
					new_tagArr[i + 1] = arr_tags[i];
				}
				return new_tagArr;
			} else {
				// 已有10个tag
				for (int i = arr_tags.length - 1; i >= 1; i--) {
					arr_tags[i] = arr_tags[i - 1];
				}
				arr_tags[0] = current_Name;
				return arr_tags;
			}
		} else {
			// 需要重新排序, 将最新的放在第一个位置, 其余依次后移1位
			if (index != 0) {
				for (int i = index; i >= 1; i--) {
					arr_tags[i] = arr_tags[i - 1];
				}
				arr_tags[0] = current_Name;
				return arr_tags;
			} else {
				return arr_tags;
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.beacon_btn:
			addBeacon();
			break;

		default:
			break;
		}
	}

	private void addBeacon() {
		if (adapter.getCount() == 0) {
			return;
		}
		WrapperResultInfo item = (WrapperResultInfo) adapter.getItem(0);
		if (checkBeaconName() && checkSelectedArea()) {
			showAddBeaconConfirmDialog(item);
		}

		 /*记录操作 0822*/
		OperaEventUtils.recordOperation(OperaEvent.OPERA_BIND_ATTENDANCE_BEANCONTAG);
	}

}
