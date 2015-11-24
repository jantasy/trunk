package cn.yjt.oa.app.nfctools.operationview;

import android.content.Context;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.nfctools.NfcTagOperationView;
import cn.yjt.oa.app.nfctools.operation.NfcTagWifiOpenOperation;

public class NfcTagOperationWifiExtraView extends FrameLayout implements
		NfcTagOperationView.ExtraView {
	
	private CheckBox connectionSpecificWifi;
	private LinearLayout connectWifi;
	private EditText networkSsid;
	private Spinner safetySpinner;
	private EditText networkPwd;
	private CheckBox displayPwd;
	private View view;
	
	private int gravity;
	
	

	public NfcTagOperationWifiExtraView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public NfcTagOperationWifiExtraView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public NfcTagOperationWifiExtraView(Context context) {
		this(context, null);
	}

	private void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.wifi_setting, this);
		connectionSpecificWifi=(CheckBox) view.findViewById(R.id.connection_specific_wifi);
		connectWifi=(LinearLayout) view.findViewById(R.id.connect_wifi);
		networkSsid=(EditText) view.findViewById(R.id.network_ssid);
		networkPwd=(EditText) view.findViewById(R.id.network_pwd);
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(context,R.layout.spinner_item, context.getResources().getStringArray(R.array.safety_network));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		safetySpinner = (Spinner) view.findViewById(R.id.safety_spinner);
		safetySpinner.setAdapter(adapter);
		displayPwd=(CheckBox) view.findViewById(R.id.display_pwd);
		displayPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					networkPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
				}else{
					networkPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
				}
				
			}
		});
		connectionSpecificWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					connectWifi.setVisibility(View.VISIBLE);
				}else{
					connectWifi.setVisibility(View.GONE);
				}
			}
		});
	}


	@Override
	public View getView() {
		return this;
	}

	
	public void setClickable(boolean clickable){
		connectionSpecificWifi.setClickable(clickable);
	}

	

	@Override
	public byte[] getExtraData() {
		if(connectionSpecificWifi.isChecked()){
			return NfcTagWifiOpenOperation.generateExtraData(networkSsid.getText().toString(), networkPwd.getText().toString(), (String) safetySpinner.getSelectedItem());
		}else{
			return null;
		}
		
	}

	@Override
	public int getExtraViewGravity() {
		return gravity;
	}

	@Override
	public void setExtraViewGravity(int gravity) {
		this.gravity = gravity;
	}


}
