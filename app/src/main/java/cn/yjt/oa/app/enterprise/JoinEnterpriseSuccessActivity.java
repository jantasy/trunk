package cn.yjt.oa.app.enterprise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.yjt.oa.app.MainActivity;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.CustInfo;
import cn.yjt.oa.app.component.TitleFragmentActivity;

public class JoinEnterpriseSuccessActivity extends TitleFragmentActivity implements OnClickListener{
	private Button experienceBtn;
	private TextView enterpriseName;
	private CustInfo custInfo;
	
	
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.join_enterprise_success);
		initView();
		loadData();
		
	}

	private void loadData() {
		custInfo=(CustInfo) getIntent().getParcelableExtra("CustInfo");
		if(custInfo!=null){
			enterpriseName.setText(custInfo.getName());
		}
	}

	private void initView() {
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		enterpriseName=(TextView) findViewById(R.id.enterprise_name);
		experienceBtn=(Button) findViewById(R.id.experience_btn);
		experienceBtn.setOnClickListener(this);
		
		
	}
	
	@Override
	public void onLeftButtonClick() {
		startMainActivity();
		super.onBackPressed();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			startMainActivity();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.experience_btn:
			startMainActivity();
			onBackPressed();
			break;

		default:
			break;
		}
		
	}

	private void startMainActivity() {
		Intent intent=new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}
	
	
	public static void launch(Context context,CustInfo custInfo){
		Intent intent=new Intent(context, JoinEnterpriseSuccessActivity.class);
		intent.putExtra("CustInfo", custInfo);
		context.startActivity(intent);
	}
	

}
