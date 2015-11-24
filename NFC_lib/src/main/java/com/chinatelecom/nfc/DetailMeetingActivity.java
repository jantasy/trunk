package com.chinatelecom.nfc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinatelecom.nfc.BroadcastReceiver.wifiConnectCallback;
import com.chinatelecom.nfc.DB.Dao.MyDataDao;
import com.chinatelecom.nfc.DB.Pojo.Meetting;
import com.chinatelecom.nfc.DB.Provider.MyDataTable;
import com.chinatelecom.nfc.DB.Provider.SettingData;
import com.chinatelecom.nfc.Utils.Constant;
import com.chinatelecom.nfc.Utils.MyUtil;

public class DetailMeetingActivity extends BaseTag {

	private Meetting mMeeting;

	private EditText etTitle;
	// ashley 0923 情景设置功能
	// private TextView tvTimeDetail;
	// private EditText etPlaceDetail;
	// private EditText etPartnerDetail;
	private LinearLayout llPhoneMode;
	private EditText etContent;

	/**
	 * false:查看页面<br>
	 * true:新建页面
	 */
	private boolean isEditOrSelect = true;
	private boolean isEdit = false;

	private LinearLayout llNew;
	private LinearLayout llEditOrSelect;
	private ImageView ivEditOrSelect;
	private TextView tvEditOrSelect;
	private Context context;

	private List<Map<String, Integer>> phoneModeMaps;

	private long startTime;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nfc_detail_meetting);
		init();
		mMyData = getMyData();
		if (mMyData != null) {
			mMeeting = mMyData.getMeeting();
			if (mMeeting != null) {
				showView();
				edit(false);
				// 查看页面
				isEditOrSelect = false;
				String strMode = mMeeting.mm;
				String[] arrMode = strMode.split(",");
				mode = new int[arrMode.length];
				for (int i = 0; i < arrMode.length; i++) {
					mode[i] = Integer.parseInt(arrMode[i]);
				}
				ssid = mMeeting.ss;
				pwd = mMeeting.pw;
			}
		}
		if (isEditOrSelect) {
			// 进入的是新建页面
			isEdit = true;
			edit(true);
			tvEditOrSelect.setText(R.string.nfc_save);
			mode = new int[] { SettingData.DEFAULT_MODE,
					SettingData.DEFAULT_MODE, SettingData.DEFAULT_MODE,
					SettingData.DEFAULT_MODE };
			ssid = "";
			pwd = "";

			llPhoneMode.removeAllViews();
			TextView tv = new TextView(this);
			tv.setText(R.string.nfc_meetting_mode_null);
			tv.setTextColor(getResources().getColor(R.color.nfc_black));
			tv.setPadding(0, 0, getResources().getDimensionPixelSize(R.dimen.nfc_otherfeatures_paddingBottom), 0);
			tv.setTextSize(16);
			llPhoneMode.addView(tv);
			Button btn = new Button(this);
			btn.setText("点击设置");
//			btn.setTextSize(getResources().getDimensionPixelSize(
//					R.dimen.nfc_otherfeatures_textSize));
			btn.setBackground(getResources().getDrawable(R.drawable.set_top_bg));
			btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(DetailMeetingActivity.this,
							DetailMeetingModeActivity.class);
					int[] _mode = new int[] { mode[0] + 1, mode[1] + 1,
							mode[2] + 1, mode[3] + 1 };
					intent.putExtra("mode", _mode);
					if (_mode[3] == 1) {
						intent.putExtra("ssid", ssid);
						intent.putExtra("pwd", pwd);
					}
					DetailMeetingActivity.this.startActivityForResult(intent,
							Constant.REQUESTCODE_MODE);
				}
			});
			llPhoneMode.addView(btn);

		}
		regWifiConnectCallback();
	}

	private void regWifiConnectCallback() {
		IntentFilter intentFilter = new IntentFilter(
				WifiManager.NETWORK_STATE_CHANGED_ACTION);
		registerReceiver(new wifiConnectCallback(), intentFilter);
		// registerReceiver(new wifiConnectCallback(),new
		// IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
	}

	@Override
	public void init() {
		super.init();
		context = this;
		startTime = System.currentTimeMillis();
		etTitle = (EditText) findViewById(R.id.title);
		// ashley 0923 削减会议设置功能
		// tvTimeDetail = (TextView) findViewById(R.id.tvTimeDetail);
		// etPlaceDetail = (EditText) findViewById(R.id.etPlaceDetail);
		// etPartnerDetail = (EditText) findViewById(R.id.etPartnerDetail);
		ivEditOrSelect = (ImageView) findViewById(R.id.ivEditOrSelect);
		llPhoneMode = (LinearLayout) findViewById(R.id.llPhoneMode);

		etContent = (EditText) findViewById(R.id.etContent);

		tvEditOrSelect = (TextView) findViewById(R.id.tvEditOrSelect);
		tvEditOrSelect.setText(R.string.nfc_edit);
		llNew = (LinearLayout) findViewById(R.id.llNew);
		llNew.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi") @Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 进入的是新建页面
				edit(true);
				tvEditOrSelect.setText(R.string.nfc_save);
				mode = new int[] { SettingData.DEFAULT_MODE,
						SettingData.DEFAULT_MODE, SettingData.DEFAULT_MODE,
						SettingData.DEFAULT_MODE };

				llPhoneMode.removeAllViews();
				TextView tv = new TextView(DetailMeetingActivity.this);
				tv.setText(R.string.nfc_meetting_mode_null);
				tv.setTextColor(getResources().getColor(R.color.nfc_black));
				tv.setTextSize(16);
				llPhoneMode.addView(tv);
				Button btn = new Button(DetailMeetingActivity.this);
				btn.setText("点击设置");
				btn.setTextSize(getResources().getDimensionPixelSize(
						R.dimen.nfc_otherfeatures_textSize));
				btn.setBackground(getResources().getDrawable(R.drawable.set_top_bg));
				btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(DetailMeetingActivity.this,
								DetailMeetingModeActivity.class);
						int[] _mode = new int[] { mode[0] + 1, mode[1] + 1,
								mode[2] + 1, mode[3] + 1 };
						intent.putExtra("mode", _mode);
						if (_mode[3] == 1) {
							intent.putExtra("ssid", ssid);
							intent.putExtra("pwd", pwd);
						}
						DetailMeetingActivity.this.startActivityForResult(intent,
								Constant.REQUESTCODE_MODE);
					}
				});
				llPhoneMode.addView(btn);
				etTitle.setText("");
				// ashley 0923 削减会议设置功能
				// tvTimeDetail.setText(R.string.nfc_meetting_startTime);
				// etPlaceDetail.setText("");
				// etPartnerDetail.setText("");
				etContent.setText("");
				isEditOrSelect = true;

			}
		});
		llEditOrSelect = (LinearLayout) findViewById(R.id.llEditOrSelect);
		llEditOrSelect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isEdit) {
					if (check()) {
						dialog();
					}
				} else {
					tvEditOrSelect.setText(R.string.nfc_save);
					edit(!isEdit);
					isEdit = !isEdit;
				}
			}
		});
		phoneModeMaps = new ArrayList<Map<String, Integer>>();

		// ashley 0923 削减会议设置功能
		// tvTimeDetail.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// Intent intent = new Intent(DetailMeetingActivity.this,
		// TimeManagerActivity2.class);
		// intent.putExtra("TITLE", "会议开始时间");
		// startActivityForResult(intent,Constant.REQUESTCODE_TIME);
		// }
		// });

		needShowBottomBar();

		findViewById(R.id.llShareTag).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (etTitle.isEnabled()) {
					MyUtil.showMessage(R.string.nfc_msg_save_first, context);
				} else {
					getJsonData(mMyData);
					showPlanDialog();
				}
			}
		});
	}

	@Override
	public void showView() {
		super.showView();
		// 手机模式
		etTitle.setText(mMeeting.n);
		// ashley 0923 削减会议设置功能
		// tvTimeDetail.setText(MyUtil.dateFormat(mMeeting.st));
		// etPlaceDetail.setText(mMeeting.pl);
		// etPartnerDetail.setText(mMeeting.p);
		etContent.setText(mMeeting.c);
		setPhoneMode(mMeeting);

	}

	private void setPhoneMode(Meetting m) {
		List<View> views = getViewsByPhoneMode(m);
		if (views != null) {
			llPhoneMode.removeAllViews();
			llPhoneMode.setOnClickListener(new OnClickListener() {
			
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(DetailMeetingActivity.this,
									DetailMeetingModeActivity.class);
							int[] _mode = new int[] { mode[0] + 1, mode[1] + 1,
									mode[2] + 1, mode[3] + 1 };
							intent.putExtra("mode", _mode);
							if (_mode[3] == 1) {
								intent.putExtra("ssid", ssid);
								intent.putExtra("pwd", pwd);
							}
							DetailMeetingActivity.this.startActivityForResult(intent,
									Constant.REQUESTCODE_MODE);
						}
					});
			for (View view : views) {
				llPhoneMode.addView(view);
			}
		}
	}

	@Override
	public void edit(boolean isEdit) {
		super.edit(isEdit);
		if(isEdit){
			etTitle.setTextColor(getResources().getColor(R.color.nfc_meetting_title));
		}else{
			etTitle.setTextColor(getResources().getColor(R.color.nfc_white));
		}
		etTitle.setEnabled(isEdit);
		// ashley 0923 削减会议设置功能
		// tvTimeDetail.setEnabled(isEdit);
		// etPlaceDetail.setEnabled(isEdit);
		// etPartnerDetail.setEnabled(isEdit);
		etContent.setEnabled(isEdit);
		llPhoneMode.setEnabled(isEdit);

		etTitle.setSelected(isEdit);
		// ashley 0923 削减会议设置功能
		// tvTimeDetail.setSelected(isEdit);
		// etPlaceDetail.setSelected(isEdit);
		// etPartnerDetail.setSelected(isEdit);
		etContent.setSelected(isEdit);

		setImageRes(ivEditOrSelect, isEdit);

	}

	@Override
	public boolean check() {
		// TODO Auto-generated method stub
		if (TextUtils.isEmpty(etTitle.getText().toString().trim())) {
			etTitle.requestFocus();
			MyUtil.showMessage(R.string.nfc_msg_null_all_title, this);
			return false;
		}
		// String startTime =
		// getResources().getString(R.string.nfc_meetting_startTime);
		// if(tvTimeDetail.getText().toString().trim().equals(startTime)){
		// MyUtil.showMessage(R.string.nfc_msg_null_meetting_startTime, this);
		// tvTimeDetail.requestFocus();
		// return false;
		// }
		// if(TextUtils.isEmpty(etPlaceDetail.getText().toString().trim())){
		// etPlaceDetail.requestFocus();
		// MyUtil.showMessage(R.string.nfc_msg_null_meetting_place, this);
		// return false;
		// }
		// if(TextUtils.isEmpty(etPartnerDetail.getText().toString().trim())){
		// etPartnerDetail.requestFocus();
		// MyUtil.showMessage(R.string.nfc_msg_null_meetting_partner, this);
		// return false;
		// }
		return true;
	}

	protected void dialog() {
		etTitle.setSelection(0);
		etTitle.requestFocus();
		Builder builder = new Builder(this);
		builder.setMessage(R.string.nfc_confirm_save);
		builder.setTitle(R.string.nfc_title_prompt);
		builder.setPositiveButton(R.string.nfc_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.setNegativeButton(R.string.nfc_ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						edit(!isEdit);
						isEdit = !isEdit;
						if (isEditOrSelect) {
							Meetting data = new Meetting(null, etTitle
									.getText().toString(),
									// ashley 0923 削减会议设置功能
									"",
									"",
									// etPartnerDetail.getText().toString(),
									// etPlaceDetail.getText().toString(),
									etContent.getText().toString(), startTime,
									MyUtil.getStringMode(mode), ssid, pwd);
							// 数据库操作：插入，查出
							long id = MyDataDao.insert(context, data,
									MyDataTable.TAG_WRITETAG);
							mMyData = MyDataDao.query(context,
									MyUtil.longToInteger(id),
									MyDataTable.MEETTING);
							if (mMyData != null) {
								mMeeting = mMyData.getMeeting();
								mydataId = mMyData.id;
							}
							isEditOrSelect = false;
						} else {
							Meetting data = new Meetting(mMeeting.id, etTitle
									.getText().toString(),
									// ashley 0923 削减会议设置功能
									"",
									"",
									// etPartnerDetail.getText().toString(),
									// etPlaceDetail.getText().toString(),
									etContent.getText().toString(), startTime,
									MyUtil.getStringMode(mode), ssid, pwd);
							// 数据库操作：更新
							// mMyData.id = mydataId;
							mMyData.n = etTitle.getText().toString();
							mMyData.setMeeting(data);
							MyDataDao.update(context, mMyData);
						}
						tvEditOrSelect.setText(R.string.nfc_edit);
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	private List<View> getViewsByPhoneMode(Meetting meetting) {
		phoneModeMaps.clear();
		String strMode = meetting.mm;
		String[] arrMode = strMode.split(",");
		int[] intMode = new int[arrMode.length];
		for (int i = 0; i < arrMode.length; i++) {
			intMode[i] = Integer.parseInt(arrMode[i]);
		}
		Integer muteMode = intMode[0];
		Integer bluetoothSwith = intMode[1];
		Integer digitalSwitch = intMode[2];
		Integer wifiSwitch = intMode[3];

		List<View> views = new ArrayList<View>();
		ImageView view;
		Map<String, Integer> map;
		if (muteMode != null) {
			if (muteMode == SettingData.OFF) {
				view = new ImageView(this);
				view.setBackgroundResource(R.drawable.nfc_mode_vibration_on);
				views.add(view);
				map = new HashMap<String, Integer>();
				map.put("muteMode", SettingData.ON);
				phoneModeMaps.add(map);
			} else if (muteMode == SettingData.ON) {
				view = new ImageView(this);
				view.setBackgroundResource(R.drawable.nfc_mode_mute);
				views.add(view);
				map = new HashMap<String, Integer>();
				map.put("muteMode", SettingData.ON);
				phoneModeMaps.add(map);
			} else {
				// 响铃
				view = new ImageView(this);
				view.setBackgroundResource(R.drawable.nfc_mode_voice_on);
				views.add(view);
				map = new HashMap<String, Integer>();
				map.put("muteMode", SettingData.OFF);
				phoneModeMaps.add(map);
			}
		}
		// if(vibrationMode != null){
		// if(vibrationMode == SettingData.OFF){
		// view = new ImageView(this);
		// view.setBackgroundResource(R.drawable.nfc_mode_vibration_off);
		// views.add(view);
		// map = new HashMap<String, Integer>();
		// map.put("vibrationMode", SettingData.OFF);
		// phoneModeMaps.add(map);
		// }else if(vibrationMode == SettingData.ON){
		//
		// }
		// }

		if (bluetoothSwith != null) {
			if (bluetoothSwith == SettingData.OFF) {
				view = new ImageView(this);
				view.setBackgroundResource(R.drawable.nfc_mode_bluetooth_off);
				views.add(view);
				map = new HashMap<String, Integer>();
				map.put("bluetoothSwith", SettingData.OFF);
				phoneModeMaps.add(map);
			} else if (bluetoothSwith == SettingData.ON) {
				view = new ImageView(this);
				view.setBackgroundResource(R.drawable.nfc_mode_bluetooth_on);
				views.add(view);
				map = new HashMap<String, Integer>();
				map.put("bluetoothSwith", SettingData.ON);
				phoneModeMaps.add(map);
			}
		}

		if (digitalSwitch != null) {
			if (digitalSwitch == SettingData.OFF) {
				view = new ImageView(this);
				view.setBackgroundResource(R.drawable.nfc_mode_digital_off);
				views.add(view);
				map = new HashMap<String, Integer>();
				map.put("digitalSwitch", SettingData.OFF);
				phoneModeMaps.add(map);
			} else if (digitalSwitch == SettingData.ON) {
				view = new ImageView(this);
				view.setBackgroundResource(R.drawable.nfc_mode_digital_on);
				views.add(view);
				map = new HashMap<String, Integer>();
				map.put("digitalSwitch", SettingData.ON);
				phoneModeMaps.add(map);
			}
		}
		if (wifiSwitch != null) {
			if (wifiSwitch == SettingData.OFF) {
				view = new ImageView(this);
				view.setBackgroundResource(R.drawable.nfc_mode_wifi_off);
				views.add(view);
				map = new HashMap<String, Integer>();
				map.put("wifiSwitch", SettingData.OFF);
				phoneModeMaps.add(map);
			} else if (wifiSwitch == SettingData.ON) {
				view = new ImageView(this);
				view.setBackgroundResource(R.drawable.nfc_mode_wifi_on);
				views.add(view);
				map = new HashMap<String, Integer>();
				map.put("wifiSwitch", SettingData.ON);
				phoneModeMaps.add(map);
			}
		}
		if (views.size() == 0) {
			TextView tv = new TextView(this);
			tv.setText(R.string.nfc_meetting_mode_null);
			tv.setTextColor(getResources().getColor(R.color.nfc_black));
			tv.setTextSize(16);
			views.add(tv);
		}
		return views;
	}

	private int mode[];
	private String ssid;
	private String pwd;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case Constant.REQUESTCODE_MODE:
			if (data != null) {
				int tmpMode[] = data.getIntArrayExtra("mode");
				mode = new int[tmpMode.length];
				for (int i = 0; i < tmpMode.length; i++) {
					mode[i] = tmpMode[i] - 1;
				}
				if (mode[3] == SettingData.ON) {
					ssid = data.getStringExtra("ssid");
					pwd = data.getStringExtra("pwd");
				}
				setPhoneMode(new Meetting((Integer) null, "", "", "", "", 1l,
						MyUtil.getStringMode(mode), ssid, pwd));

			}
			break;
		case Constant.REQUESTCODE_TIME:
			if (data != null) {
				// ashley 0923 削减会议设置功能
				// tvTimeDetail.setText(getTimeLong(
				// data.getIntExtra("myYear", 1),
				// data.getIntExtra("myMonth", 1),
				// data.getIntExtra("myDay", 1),
				// data.getIntExtra("myHour", 1),
				// data.getIntExtra("myMinute", 1)
				// ));
			}
			break;

		default:
			break;
		}
	}

	private String getTimeLong(int y, int m, int d, int h, int min) {
		Calendar cal = Calendar.getInstance();
		cal.set(y, m - 1, d, h, min);
		startTime = cal.getTimeInMillis();
		return MyUtil.dateFormat(startTime);
	}

}
