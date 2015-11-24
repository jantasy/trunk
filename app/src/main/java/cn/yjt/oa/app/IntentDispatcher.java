package cn.yjt.oa.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.SigninInfo;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.signin.AttendanceActivity;

public class IntentDispatcher extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Uri data = getIntent().getData();
		try {
			if(data != null){
				String module = data.getQueryParameter("module");
				UserInfo current = AccountManager.getCurrent(getApplicationContext());
				if(current!=null&&current.getId() > 0){
					startActivity(parseModule(module,data.getQueryParameter("ID")));
				}else{
					LaunchActivity.launch(this);
				}
				finish();
			}else{
				finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LaunchActivity.launch(this);
			finish();
		}
	}
	
	private Intent parseModule(String module,String sn){
		
		if("signin".equals(module)){
			Intent intent = new Intent(this, AttendanceActivity.class);
			SigninInfo signinInfo = new SigninInfo();
			signinInfo.setActrualData(sn);
			signinInfo.setType(SigninInfo.SIGNIN_TYPE_QR);
			intent.putExtra("SigninInfo", signinInfo);
			return intent;
		}
		Intent intent = new Intent(this, LaunchActivity.class);
		return intent;
	}
}
