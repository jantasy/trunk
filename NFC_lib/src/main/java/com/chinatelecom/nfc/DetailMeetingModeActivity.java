package com.chinatelecom.nfc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinatelecom.nfc.Utils.Constant;
import com.chinatelecom.nfc.Utils.MyUtil;
import com.chinatelecom.nfc.Utils.WifiAdmin;
import com.chinatelecom.nfc.View.MySimpleAdpter;

public class DetailMeetingModeActivity extends BaseTag {

	private ListView listView;
	private MySimpleAdpter adapter;

	private int[] icon_phone = new int[] {R.drawable.nfc_mode_voice_on,R.drawable.nfc_mode_mute ,
			R.drawable.nfc_mode_vibration_on};

//	private int[] icon_vibration = new int[] { R.drawable.nfc_mode_default,
//			R.drawable.nfc_mode_vibration_on, R.drawable.nfc_mode_vibration_off };

	private int[] icon_wifi = new int[] { R.drawable.nfc_mode_default,
			R.drawable.nfc_mode_wifi_on, R.drawable.nfc_mode_wifi_off };

	private int[] icon_digital = new int[] { R.drawable.nfc_mode_default,
			R.drawable.nfc_mode_digital_on, R.drawable.nfc_mode_digital_off };
	
	private int[] icon_bluetooth = new int[] { R.drawable.nfc_mode_default,
			R.drawable.nfc_mode_bluetooth_on, R.drawable.nfc_mode_bluetooth_off };
	private int[] switch_phone = new int[] { R.drawable.nfc_mode_ling,
			R.drawable.nfc_mode_mute_icon, R.drawable.nfc_mode_vir_icon };
	private int[] switch_iamge = new int[] { R.drawable.nfc_mode_switch_default,
			R.drawable.nfc_mode_switch_on, R.drawable.nfc_mode_switch_off };
	private String[] info_text = new String[] { "默认", "开启", "关闭" };
	private String[] info_phone = new String[] { "默认", "静音","响铃" };
	private String[] title = new String[] { "手机模式", "蓝牙设置","2G/3G设置", "无线设置" };
	private int[] icon = new int[] { 
			R.drawable.nfc_mode_mute,
			R.drawable.nfc_mode_default,R.drawable.nfc_mode_default,
			R.drawable.nfc_mode_default };
	private String[] info ;
	private int[] ivSwitch = new int[] { 
			R.drawable.nfc_mode_switch_off,
			R.drawable.nfc_mode_switch_default,R.drawable.nfc_mode_switch_default,
			R.drawable.nfc_mode_switch_default };

	private int[] count;

	
	private LinearLayout llSSID;
	private EditText edSSID;
	private EditText edPwd;
	private Button btnSearch;
	private Button btnCancel;
	private Button btnOk;
	private ProgressBar pbWifi;
	
	private ListView wifiListView;
	private MySimpleAdpter wifiAdapter;
	
	/**
	 * 时间控制
	 */
	private long wifiScanControl = 1l;
	private List<ScanResult> scanResults;
	
	private final int MSG_SCANRESULTS = 1;
	private Handler myHandle = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_SCANRESULTS:
				pbWifi.setVisibility(View.GONE);
				if(scanResults != null){
					wifiAdapter.setListData(getWifiDatas(scanResults));
					wifiAdapter.notifyDataSetChanged();
				}
				break;

			default:
				break;
			}
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nfc_detail_meetting_mode);
		setTitle(R.string.nfc_phone_manage);
		
		llSSID = (LinearLayout) findViewById(R.id.llSSID);
		edSSID = (EditText) findViewById(R.id.edSSID);
		edPwd = (EditText) findViewById(R.id.edPwd);
		pbWifi = (ProgressBar) findViewById(R.id.pbWifi);
		wifiListView = (ListView) findViewById(R.id.wifiListView);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		btnOk = (Button) findViewById(R.id.btnOk);
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean isProblem = false;
				Intent data = new Intent();
				data.putExtra("mode", count);
				if(count[3] == 1){
					String ssid = edSSID.getText().toString();
					String pwd = edPwd.getText().toString();
					if(TextUtils.isEmpty(ssid.trim())){
						isProblem = true;
					}
					if(TextUtils.isEmpty(pwd.trim())){
						isProblem = true;
					}
					data.putExtra("ssid", ssid );
					data.putExtra("pwd", pwd);
				}
				if(!isProblem){
					setResult(Constant.REQUESTCODE_MODE, data);
					finish();
				}else{
					MyUtil.showMessage(R.string.nfc_msg_null_nameorpwd, DetailMeetingModeActivity.this);
				}
			}
		});
		
		btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				long currentTimeMillis = System.currentTimeMillis() ;
				if((currentTimeMillis - wifiScanControl) > 6000){
					pbWifi.setVisibility(View.VISIBLE);
					wifiScanControl = currentTimeMillis;
					new Thread() {
						@Override
						public void run() {
							// 需要花时间方法
							WifiAdmin wifiAdmin = new WifiAdmin(DetailMeetingModeActivity.this);

							// 判断wifi是否开启
							if (!wifiAdmin.isWifiOpened()) {
								wifiAdmin.OpenWifi();
								while (!wifiAdmin.isWifiOpened()) {
									try {
										Thread.sleep(100);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
							// 扫描wifi
							wifiAdmin.StartScan();
							scanResults =  wifiAdmin.GetWifiList();
							long scanTime = System.currentTimeMillis();
							while (scanResults == null) {
								try {
									if(System.currentTimeMillis() - scanTime < 5000){
										Thread.sleep(100);
									}else{
										break;
									}

								} catch (InterruptedException e) {
									e.printStackTrace();
								}

							}
							myHandle.sendEmptyMessage(MSG_SCANRESULTS);
							
						}
					}.start();
					
				}
			}
		});
		
		Intent data = getIntent();
		int []mode = data.getIntArrayExtra("mode");
		if(mode !=null ){
			count = mode;
			if(mode[3] == 1){
				llSSID.setVisibility(View.VISIBLE);
				edPwd.setVisibility(View.VISIBLE);
				wifiListView.setVisibility(View.VISIBLE);
				edSSID.setText(data.getStringExtra("ssid"));
				edPwd.setText(data.getStringExtra("pwd"));
			}
		}else{
			initCount();
		}
		//初始化
		info = new String[] { info_phone[count[0]], info_text[count[1]], info_text[count[2]],info_text[count[3]] };
		icon = new int[] { icon_phone[count[0]], icon_bluetooth[count[1]],icon_digital[count[2]], icon_wifi[count[3]] };
		ivSwitch = new int[]{switch_phone[count[0]],switch_iamge[count[1]],switch_iamge[count[2]],switch_iamge[count[3]]};
		
		listView = (ListView) findViewById(R.id.listView);
		adapter = new MySimpleAdpter(this, getDatas(),
				R.layout.nfc_detail_meetting_mode_item, new String[] { "iv_icon",
						"tv_title", "tv_info", "iv_switch" }, new int[] {
						R.id.iv_icon, R.id.tv_title, R.id.tv_info,
						R.id.iv_switch });
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				plusCount(position);
				
				switch (position) {
				case 0:
					icon[position] = icon_phone[count[position]];
					info[position] = info_phone[count[position]];
					ivSwitch[position] = switch_phone[count[position]];
					break;
				case 1:
					icon[position] = icon_bluetooth[count[position]];
					info[position] = info_text[count[position]];
					ivSwitch[position] = switch_iamge[count[position]];
					break;
				case 2:
					icon[position] = icon_digital[count[position]];
					info[position] = info_text[count[position]];
					ivSwitch[position] = switch_iamge[count[position]];
					break;
				case 3:
					icon[position] = icon_wifi[count[position]];
					info[position] = info_text[count[position]];
					ivSwitch[position] = switch_iamge[count[position]];
					if(count[position] == 1){
						llSSID.setVisibility(View.VISIBLE);
						edPwd.setVisibility(View.VISIBLE);
						wifiListView.setVisibility(View.VISIBLE);
					}else{
						llSSID.setVisibility(View.GONE);
						edPwd.setVisibility(View.GONE);
						wifiListView.setVisibility(View.GONE);
					}
//					toWirelessSettings();
					break;
//				case 4:
//					icon[position] = icon_bluetooth[count[position]];
//					info[position] = info_text[count[position]];
//					ivSwitch[position] = switch_iamge[count[position]];
//					break;
				default:
					break;
				}
				adapter.setListData(getDatas());
				adapter.notifyDataSetChanged();
			}

		});
		
		
		wifiAdapter = new MySimpleAdpter(this, getWifiDatas(scanResults),
				R.layout.nfc_detail_meetting_mode_item_wifi,
				new String[] { "iv_checkbox","tv_ssid" }, new int[] { R.id.iv_checkbox, R.id.tv_ssid });
		wifiListView.setAdapter(wifiAdapter);
		wifiListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				for (int i = 0; i < wifiListView.getChildCount(); i++) {
					RelativeLayout rl = (RelativeLayout) wifiListView.getChildAt(i);// 获得子级
					rl.findViewById(R.id.iv_checkbox).setBackgroundResource(R.drawable.nfc_checkbox_null);
				}
				view.findViewById(R.id.iv_checkbox).setBackgroundResource(R.drawable.nfc_checkbox_select);
				edSSID.setText(((TextView)view.findViewById(R.id.tv_ssid)).getText().toString());
			}
			
		});
		
	}

	private ArrayList<HashMap<String, Object>> getDatas() {
		// 生成动态数组，并且转入数据
		ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < title.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("iv_icon", icon[i]);
			map.put("tv_title", title[i]);
			map.put("tv_info", info[i]);
			map.put("iv_switch", ivSwitch[i]);
			lstImageItem.add(map);
		}

		return lstImageItem;
	}
	private ArrayList<HashMap<String, Object>> getWifiDatas(List<ScanResult> scanResultss) {
		// 生成动态数组，并且转入数据
		ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
		if(scanResultss != null){
			for (ScanResult s : scanResultss) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("iv_checkbox", R.drawable.nfc_checkbox_null);
				map.put("tv_ssid", s.SSID);
				lstImageItem.add(map);
			}
		}
		
		return lstImageItem;
	}

	private void initCount() {
		count = new int[] { 2, 0, 0 ,0};

	}

	private void plusCount(int position) {
		if (count[position] == 2) {
			count[position] = 0;
		} else {
			count[position] += 1;
		}
	}
	

//	private void toWirelessSettings() {
//		
////		if (android.os.Build.VERSION.SDK_INT > 10) {}
//			// 3.0以上打开设置界面
////			startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
////			startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
////			startActivity(new Intent(android.provider.Settings.ACTION_WIFI_IP_SETTINGS));
//			startActivityForResult(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS),0);
//	}

}
