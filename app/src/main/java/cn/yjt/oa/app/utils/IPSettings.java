package cn.yjt.oa.app.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.BusinessConstants;

public class IPSettings extends TitleFragmentActivity implements
		OnCheckedChangeListener, OnClickListener,
		android.widget.CompoundButton.OnCheckedChangeListener {

	private String[] CONS_IP = new String[] {
			"http://42.123.76.176:9090/yjtoa/s/",
			"http://219.142.69.139:9090/yjtoa/s/",
			"http://192.168.1.181:9090/yjtoa/s/",
			"http://192.168.1.250:9090/s/" };
	private String[] IP = CONS_IP;
	private Spinner spinner;
	private AutoCompleteTextView textView;
	private RadioGroup radioGroup;
	private SharedPreferences preferences;
	private ArrayAdapter<String> adapter;
	private ArrayAdapter<String> adapter1;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		preferences = getSharedPreferences("ip_config", MODE_PRIVATE);
		loadData();
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		getRightButton().setImageResource(R.drawable.contact_list_save);
		setContentView(R.layout.activity_ip_settings);
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		radioGroup.setOnCheckedChangeListener(this);
		textView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
		spinner = (Spinner) findViewById(R.id.spinner1);
		findViewById(R.id.button1).setOnClickListener(this);
		findViewById(R.id.button2).setOnClickListener(this);
		int resource = R.layout.simple_sropdown_item_1line;
		Switch testMode = (Switch) findViewById(R.id.test_mode);
		testMode.setChecked(MainApplication.isTestMode(this));
		testMode.setOnCheckedChangeListener(this);
		

		List<String> data = asList(IP);
		adapter = new ArrayAdapter<String>(this, resource, data);
		adapter1 = new ArrayAdapter<String>(this, resource, data);
		spinner.setAdapter(adapter);
		textView.setAdapter(adapter1);
		textView.setText(BusinessConstants.getBASE_URL());
		textView.setVisibility(View.GONE);
	}

	private List<String> asList(String[] array) {
		List<String> list = new ArrayList<String>(array.length);
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
	}

	private void loadData() {
		Set<String> defValues = new HashSet<String>();
		defValues.addAll(Arrays.asList(CONS_IP));
		Set<String> stringSet = preferences.getStringSet("ip", defValues);
		String[] strings = new String[stringSet.size()];
		IP = stringSet.toArray(strings);
	}

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	@Override
	public void onRightButtonClick() {
		if (radioGroup.getCheckedRadioButtonId() == R.id.radio0) {
			BusinessConstants
					.setBASE_URL(IP[spinner.getSelectedItemPosition()]);
		} else {
			BusinessConstants.setBASE_URL(textView.getText().toString());
			Set<String> values = new HashSet<String>(Arrays.asList(IP));
			values.add(textView.getText().toString());
			preferences.edit().putStringSet("ip", values).commit();
		}
		super.onBackPressed();
	}

	public static void launch(Context context) {
//		if(true)
//		{
//			return;
//		}
		Intent intent = new Intent(context, IPSettings.class);
		context.startActivity(intent);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radio0:
			textView.setVisibility(View.GONE);
			spinner.setVisibility(View.VISIBLE);
			break;
		case R.id.radio1:
			textView.setVisibility(View.VISIBLE);
			spinner.setVisibility(View.GONE);
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			clearHistory();
			reloadData();
			break;
		case R.id.button2:
			
			new Thread(){
				public void run() {
					deleteFile(new File( Environment.getExternalStorageDirectory()
							+ "/yijitong/"));
					handler.sendEmptyMessage(1);
				};
			}.start();
			
			break;
		default:
			break;
		}
	}
	
	private void deleteFile(File file){
		if(file == null){
			return;
		}
		if(file.isDirectory()){
			File[] listFiles = file.listFiles();
			for (File file2 : listFiles) {
				deleteFile(file2);
			}
		}else{
			file.delete();
			Message msg = Message.obtain();;
			msg.what = 0;
			msg.obj = file.getAbsolutePath();
			handler.sendMessage(msg);
		}
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 0){
				if(progressDialog ==null){
					progressDialog = ProgressDialog.show(IPSettings.this, null, "正在删除 "+msg.obj);
				}else{
					progressDialog.setMessage( "正在删除 "+msg.obj);
				}
			}else{
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						if(progressDialog != null){
							progressDialog.dismiss();
							progressDialog = null;
							Toast.makeText(getApplicationContext(), "删除完成", Toast.LENGTH_SHORT).show();
						}
					}
				}, 1000);
			}
		};
	};
	ProgressDialog progressDialog;

	private void reloadData() {
		loadData();
		adapter1.clear();
		adapter1.addAll(IP);
		adapter1.notifyDataSetChanged();

		adapter.clear();
		adapter.addAll(IP);
		adapter.notifyDataSetChanged();

	}

	private void clearHistory() {
		preferences.edit().clear().commit();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		MainApplication.setTestMode(this,isChecked);
	}
}
