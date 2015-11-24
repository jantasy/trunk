package cn.yjt.oa.app.enterprise;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;

public class InfoBasicInputUI extends BaseActivity {
	
	@Override
	protected Fragment getFragment() {
		return new BasicInputFragment();
	}
	
	@Override
	protected CharSequence initTitle() {
		return "基本信息输入";
	}
	
	public static class BasicInputFragment extends Fragment implements OnClickListener{
		public static final String EXTRA_ENTERPRISE_ADDRESS = "enterprise_address";
		public static final String EXTRA_ENTERPRISE_NAME = "enterprise_name";
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.enterprise_step_basic_input, container, false);
			initView(view);
			return view;
		}
		
		private Button btnNextStep;
		private EditText etAddress;
		private TextView name;
		public void initView(View view){
			name = (TextView) view.findViewById(R.id.tv_enterprise_name);
			name.setText(getName());
			btnNextStep = (Button) view.findViewById(R.id.enterprise_btn_next_step);
			btnNextStep.setOnClickListener(this);
			etAddress = (EditText) view.findViewById(R.id.et_enterprise_address);
		}
		
		private String getAddress(){
			return etAddress.getText().toString().trim();
		}
		
		@Override
		public void onClick(View v) {
			if(R.id.enterprise_btn_next_step == v.getId()){
				startActivity();
			}
		}
		
		private void startActivity() {
			Intent intent = new Intent(getActivity(), InfoDetailInputUI.class);
			intent.putExtra(EXTRA_ENTERPRISE_ADDRESS, getAddress());
			intent.putExtra(EXTRA_ENTERPRISE_NAME, getName());
			startActivity(intent);
		}
		
		@SuppressWarnings("unused")
		private void nextStep(){
			if(!TextUtils.isEmpty(getAddress())){
				Intent intent = new Intent(getActivity(),InfoDetailInputUI.class);
				intent.putExtra(EXTRA_ENTERPRISE_ADDRESS, getAddress());
				intent.putExtra(EXTRA_ENTERPRISE_NAME, getName());
				startActivity(intent);
			}else{
				toast("地址输入不能为空");
			}
		}
		
		private String getName() {
			return getActivity().getIntent().getStringExtra(EXTRA_ENTERPRISE_NAME);
		}

		private void toast(String msg){
			Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
		}
	}
	
	public static void startActivityForRegister(Activity activity,String name){
		Intent intent = new Intent(activity,InfoBasicInputUI.class);
		intent.putExtra(BasicInputFragment.EXTRA_ENTERPRISE_NAME, name);
		activity.startActivity(intent);
	}
	
}
