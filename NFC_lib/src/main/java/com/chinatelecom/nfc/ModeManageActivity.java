package com.chinatelecom.nfc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinatelecom.nfc.DB.Provider.SettingData;
import com.chinatelecom.nfc.Utils.Constant;

public class ModeManageActivity extends Activity implements OnClickListener{
	private Button btnOk;
	private Button btnCancel;
	private RelativeLayout rl_default;
	private RelativeLayout rl_off;
	private RelativeLayout rl_on;
	private CheckBox cb_default;
	private CheckBox cb_off;
	private CheckBox cb_on;
	private TextView tvTitle;
	
	private int requestCode;
	private String title;
	
	private LinearLayout llSettingWifi;
	private EditText etWifiName;
	private EditText etWifiPassword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.nfc_mode_manage);
		requestCode = getIntent().getIntExtra("mode",  Constant.REQUESTCODE_MODEMUTE);
		title = getIntent().getStringExtra("title");
		
		rl_default = (RelativeLayout) findViewById(R.id.rl_default);
		rl_default.setOnClickListener(this);
		rl_off = (RelativeLayout) findViewById(R.id.rl_off);
		rl_off.setOnClickListener(this);
		rl_on = (RelativeLayout) findViewById(R.id.rl_on);
		rl_on.setOnClickListener(this);
		cb_default = (CheckBox) findViewById(R.id.cb_default);
		cb_default.setChecked(true);
		cb_off = (CheckBox) findViewById(R.id.cb_off);
		cb_on = (CheckBox) findViewById(R.id.cb_on);
		
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(title);
		
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);
		btnOk = (Button) findViewById(R.id.btnOk);
		btnOk.setOnClickListener(this);
		
		llSettingWifi = (LinearLayout) findViewById(R.id.llSettingWifi);
		etWifiName = (EditText) findViewById(R.id.etWifiName);
		etWifiPassword = (EditText) findViewById(R.id.etWifiPassword);
		
		llSettingWifi.setVisibility(View.GONE);
		
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id== R.id.btnCancel){
			this.finish();
			}else 
		if(id== R.id.btnOk){
			Intent intent = new Intent();
			int isChecked = SettingData.DEFAULT_MODE;
			if(cb_default.isChecked()){
				isChecked = SettingData.DEFAULT_MODE;
			}else if(cb_off.isChecked()){
				isChecked = SettingData.OFF;
			}else{
				isChecked = SettingData.ON;
			}
			intent.putExtra("modeMute", isChecked);
			if(requestCode == Constant.REQUESTCODE_WIFI){
				intent.putExtra("name", etWifiName.getText().toString());
				intent.putExtra("password", etWifiPassword.getText().toString());
			}
			setResult(requestCode, intent);
			this.finish();
			}else 
		if(id== R.id.rl_default){
			cb_default.setChecked(true);
			cb_off.setChecked(false);
			cb_on.setChecked(false);
			if(requestCode == Constant.REQUESTCODE_WIFI){
				llSettingWifi.setVisibility(View.GONE);
			}
			}else 
		if(id== R.id.rl_off){
			cb_default.setChecked(false);
			cb_off.setChecked(true);
			cb_on.setChecked(false);
			if(requestCode == Constant.REQUESTCODE_WIFI){
				llSettingWifi.setVisibility(View.GONE);
			}
			}else 
		if(id== R.id.rl_on){
			cb_default.setChecked(false);
			cb_off.setChecked(false);
			cb_on.setChecked(true);
			if(requestCode == Constant.REQUESTCODE_WIFI){
				llSettingWifi.setVisibility(View.VISIBLE);
			}
			} 

	}

}
