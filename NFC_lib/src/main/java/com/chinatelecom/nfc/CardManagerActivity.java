package com.chinatelecom.nfc;

import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.TextView;

import com.chinatelecom.nfc.DB.Provider.MyDataTable;
import com.chinatelecom.nfc.Main.MainListView;
import com.chinatelecom.nfc.Utils.Constant;
import com.chinatelecom.nfc.Utils.MyUtil;

public class CardManagerActivity extends BaseNfcAdapter {
	private TextView tvTitle;
	private String tempDataType;
	private String tempReadOrWrite;
	private MainListView mMainListView;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nfc_car_manager);

		tvTitle = (TextView) findViewById(R.id.tvTitle);
		Intent data = getIntent();
		tempDataType = data.getStringExtra(Constant.MYDATA_DATATYPE);
		tempReadOrWrite = data.getStringExtra(Constant.MYDATA_READORWRITE);

		if (!TextUtils.isEmpty(tempDataType)
				&& (tempReadOrWrite.contains(String
						.valueOf(MyDataTable.TAG_READFROMNFC)) || tempReadOrWrite
						.contains(String.valueOf(MyDataTable.TAG_READ_FAVORITE)))) {

			mMainListView = new MainListView(this, tempDataType,
					tempReadOrWrite);
			tvTitle.setText(data.getStringExtra(Constant.TITLE));

			if (tempDataType.equals(String.valueOf(MyDataTable.NAMECARD))) {
				List<Map<String, Object>> tmp = mMainListView.getListDatas();
				if (tmp != null && tmp.size() != 0) {
					String tel = tmp.get(0).get("info").toString().trim();
					if (tel.equals("null")) {
						MyUtil.showMessage(R.string.nfc_nc_my_nc,
								CardManagerActivity.this);
					}
				} 
				if (!(tmp != null && tmp.size() != 0)) {
					Intent intent = new Intent();
					intent.setClass(this, NameCardManageActivity.class);
					intent.setAction(Intent.ACTION_INSERT);
					startActivity(intent);
					this.finish();
				}
			}
		} else if (!TextUtils.isEmpty(tempDataType)
				&& (tempReadOrWrite.contains(String
						.valueOf(MyDataTable.TAG_WRITETAG)) || tempReadOrWrite
						.contains(String
								.valueOf(MyDataTable.TAG_WRITE_FAVORITE)))) {
			mMainListView = new MainListView(this, tempDataType,
					tempReadOrWrite);
			tvTitle.setText(data.getStringExtra("title"));

		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(MyDataTable.TAG_READ_FAVORITE).append(",")
					.append(MyDataTable.TAG_READFROMNFC);
			mMainListView = new MainListView(this, null, sb.toString());
		}

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!TextUtils.isEmpty(tempDataType)
					&& (tempReadOrWrite.contains(String
							.valueOf(MyDataTable.TAG_READFROMNFC)) || tempReadOrWrite
							.contains(String
									.valueOf(MyDataTable.TAG_READ_FAVORITE)))) {
				finish();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
