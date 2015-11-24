package com.chinatelecom.nfc;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinatelecom.nfc.DB.Dao.MyDataDao;
import com.chinatelecom.nfc.DB.Pojo.Meetting;
import com.chinatelecom.nfc.DB.Pojo.MyData;
import com.chinatelecom.nfc.DB.Pojo.MyMode;
import com.chinatelecom.nfc.DB.Pojo.ProtocolTitle;
import com.chinatelecom.nfc.DB.Provider.MyDataTable;
import com.chinatelecom.nfc.DB.Provider.SettingData;
import com.chinatelecom.nfc.Main.MainListView;
import com.chinatelecom.nfc.Utils.Constant;
import com.chinatelecom.nfc.Utils.MyUtil;
import com.google.gson.Gson;

public class MainActivity extends BaseNfcAdapter implements OnClickListener {
	private TextView tvTitle;
	private Button btnTestData;
	private Button btnMymode;
	private ImageView btnNewTag;
	private String tempDataType;
	private String tempReadOrWrite;
	private ImageView iv_movie;
	private ImageView iv_park;
	private ImageView animation;
	private AnimationDrawable animationDrawable = null;
	private MainListView mMainListView;
//	private LinearLayout ll_switch;

	@Override
	protected void onResume() {
		super.onResume();
		if (mMainListView != null) {
			if (!TextUtils.isEmpty(tempDataType)) {
				StringBuilder sb = new StringBuilder();
				if (tempReadOrWrite == null) {
					sb.append(MyDataTable.TAG_READ_FAVORITE).append(",")
							.append(MyDataTable.TAG_READFROMNFC);
					mMainListView.update(tempDataType, sb.toString());
				} else {
					mMainListView.update(tempDataType, tempReadOrWrite);
				}

			}
		}
	}
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus&&animationDrawable!=null) {
			animationDrawable.start();
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nfc_main);

		animation = (ImageView) findViewById(R.id.animation);
		btnTestData = (Button) findViewById(R.id.btnTestData);
		btnTestData.setOnClickListener(this);
		btnNewTag = (ImageView) findViewById(R.id.btnNewTag);

		btnNewTag.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(tempDataType)) {

					Intent intent = new Intent();
					intent.putExtra(Constant.INTENT_TYPE,
							Constant.INTENT_TYPE_MAKE_TAG);
					if (tempDataType.equals(String
							.valueOf(MyDataTable.MEETTING))) {
						intent.setClass(MainActivity.this,
								DetailMeetingActivity.class);
						MainActivity.this.startActivity(intent);
					} else if (tempDataType.equals(String
							.valueOf(MyDataTable.NAMECARD))) {
						intent.setClass(MainActivity.this,
								NameCardManageActivity.class);
						intent.setAction(Intent.ACTION_INSERT);
						MainActivity.this.startActivity(intent);
					} else if (tempDataType.equals(String
							.valueOf(MyDataTable.TEXT))) {
						intent.setClass(MainActivity.this,
								DetailTextActivity.class);
						MainActivity.this.startActivity(intent);
					} else if (tempDataType.equals(String
							.valueOf(MyDataTable.WEB))) {
						intent.setClass(MainActivity.this,
								DetailWebActivity.class);
						MainActivity.this.startActivity(intent);
					}
				}
			}
		});
		btnMymode = (Button) findViewById(R.id.btnMymode);
		btnMymode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyData myMode = MyDataDao.query(MainActivity.this, null,
						MyDataTable.MYMODE);
				StringBuilder myModeContent = new StringBuilder();
				if (myMode != null) {
					String content = MyDataDao.getMyModeStr(MainActivity.this,
							myMode, new StringBuffer());
					MyMode mmmm = myMode.getMyMode();
					if (mmmm == null) {
						myModeContent.append(getResources().getString(
								R.string.nfc_meetting_restore_null));
					} else {
						myModeContent
								.append(getResources().getString(
										R.string.nfc_meetting_restore_1));

					}
					dialog(myMode.getMyMode(), myModeContent.toString());
				}
			}
		});

		tvTitle = (TextView) findViewById(R.id.tvTitle);
		Intent data = getIntent();
		tempDataType = data.getStringExtra(Constant.MYDATA_DATATYPE);
		// 杩欎釜鏍囪鏄尯鍒嗘槸璇诲彇锛岃繕鏄啓鍏ョ殑椤甸潰
		tempReadOrWrite = data.getStringExtra(Constant.MYDATA_READORWRITE);

		// by wunan 濡傛灉鏈夋暟鎹紝杩涜鍥炴樉
		if (!TextUtils.isEmpty(tempDataType)
				&& (tempReadOrWrite.contains(String
						.valueOf(MyDataTable.TAG_READFROMNFC)) || tempReadOrWrite
						.contains(String.valueOf(MyDataTable.TAG_READ_FAVORITE)))) {


			mMainListView = new MainListView(this, tempDataType,
					tempReadOrWrite);
			tvTitle.setText(data.getStringExtra(Constant.TITLE));

			if (tempDataType.equals(String.valueOf(MyDataTable.MEETTING))) {
				MyData myMode = MyDataDao.query(MainActivity.this, null,
						MyDataTable.MYMODE);
				if (myMode.getMyMode() != null) {
					btnMymode.setVisibility(View.VISIBLE);
				} else {
					btnMymode.setVisibility(View.GONE);
				}
			} else {
				btnMymode.setVisibility(View.GONE);
			}

			if (tempDataType.equals(String.valueOf(MyDataTable.NAMECARD))) {
				List<Map<String, Object>> tmp = mMainListView.getListDatas();
				if (tmp != null && tmp.size() != 0) {
					String tel = tmp.get(0).get("info").toString().trim();
					if (tel.equals("null")) {// 涓嶇煡閬撲负浠�涔堝緱鍒版槸鈥渘ull鈥濊繖涓瓧绗︿覆銆�
						MyUtil.showMessage(R.string.nfc_nc_my_nc, MainActivity.this);
					}
				} else {

				}
				if (!(tmp != null && tmp.size() != 0)) {
					Intent intent = new Intent();
					intent.setClass(this, NameCardManageActivity.class);
					intent.setAction(Intent.ACTION_INSERT);
					startActivity(intent);
					this.finish();
				}
			}
			animationDrawable = (AnimationDrawable) animation.getDrawable();
			btnNewTag.setVisibility(View.GONE);
		} else if (!TextUtils.isEmpty(tempDataType)
				&& (tempReadOrWrite.contains(String
						.valueOf(MyDataTable.TAG_WRITETAG)) || tempReadOrWrite
						.contains(String
								.valueOf(MyDataTable.TAG_WRITE_FAVORITE)))) {
			mMainListView = new MainListView(this, tempDataType,
					tempReadOrWrite);
			tvTitle.setText(data.getStringExtra("title"));
			btnNewTag.setVisibility(View.VISIBLE);

		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(MyDataTable.TAG_READ_FAVORITE).append(",")
					.append(MyDataTable.TAG_READFROMNFC);
			mMainListView = new MainListView(this, null, sb.toString());
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();

		if(id == R.id.btnTestData){
			
			MyUtil.showMessage("btnTestData", this);
			Calendar cal = Calendar.getInstance();
			cal.set(2013, 2, 14, 13, 30);

			StringBuilder sb = new StringBuilder();
			sb.append(SettingData.DEFAULT_MODE).append(",")
					.append(SettingData.ON).append(",").append(SettingData.ON)
					.append(",").append(SettingData.ON);

			Meetting _Meeting = new Meetting(
					null,
					"NFC宸alalalalla",
					"灏忓垬锛屽皬鏉庯紝lalala",
					"澶╅箙搴�",
					"鍏充簬NFC宸ュ叿闆嗙殑浼氳锛屽噯鏃跺弬鍔狅紝涓嶈兘缂哄腑銆傚叧浜嶯FC宸ュ叿闆嗙殑浼氳锛屽噯鏃跺弬鍔狅紝涓嶈兘缂哄腑銆傚叧浜嶯FC宸ュ叿闆嗙殑浼氳锛屽噯鏃跺弬鍔狅紝涓嶈兘缂哄腑銆傚叧浜嶯FC宸ュ叿闆嗙殑浼氳锛屽噯鏃跺弬鍔狅紝涓嶈兘缂哄腑銆�",
					cal.getTimeInMillis(), sb.toString(), "TP-LINK_hems",
					"hems1235");
			MyData e_meetting = new MyData(null, "NFC宸alalalalla",
					MyDataTable.MEETTING, 1, -1l, MyDataTable.TAG_READFROMNFC);
			e_meetting.setMeeting(_Meeting);
			ProtocolTitle pt = new ProtocolTitle(this);
			pt.setData(e_meetting);
			Gson gson = new Gson();
			String jsonstr = gson.toJson(pt);
			MyDataDao.readData(this, jsonstr, true);

		}
	}

	protected void dialog(final MyMode m, String content) {
		Builder builder = new Builder(this);
		builder.setMessage(content);
		builder.setTitle(R.string.nfc_meetting_restore_mode);
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
						if (m != null) {

							MyUtil.setPhoneModeOnly(MainActivity.this,
									new int[] { m.muteMode, m.bluetooth,
											m.digitalSwitch, m.wifiSwitch },
									m.wifiSSID, m.wifiPassword);
						}
						dialog.dismiss();
					}
				});
		builder.create().show();
	}


	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!TextUtils.isEmpty(tempDataType)
					&& (tempReadOrWrite.contains(String
							.valueOf(MyDataTable.TAG_READFROMNFC)) || tempReadOrWrite
							.contains(String.valueOf(MyDataTable.TAG_READ_FAVORITE)))){
				finish();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
