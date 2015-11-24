package cn.yjt.oa.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.contactlist.db.ContactManager;
import cn.yjt.oa.app.message.fragment.TopMessageIdManager;
import cn.yjt.oa.app.personalcenter.LoginActivity;

public class LeaveEnterpriseActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String msg = getIntent().getStringExtra("msg");
		AlertDialog.Builder builder;
		if(VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB){
			builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
		}else{
			builder = new AlertDialog.Builder(this);
		}
		AlertDialog alertDialog = builder.setTitle("企业移除").setMessage(msg).setPositiveButton("重新登录", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				LoginActivity.launch(LeaveEnterpriseActivity.this);
				MainApplication.clearContacts();
				AccountManager.clearAccountCookie();
				ContactManager.destoryContactManager();
				TopMessageIdManager.deleteTopIds();
				AccountManager.exitLogin(getApplicationContext());
				finish();
			}
		}).create();
		alertDialog.setCancelable(false);
		alertDialog.show();
	}
	
	public static void launch(String msg){
		Context context = MainApplication.getAppContext();
		Intent intent = new Intent(context, LeaveEnterpriseActivity.class);
		intent.putExtra("msg", msg);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	
}
