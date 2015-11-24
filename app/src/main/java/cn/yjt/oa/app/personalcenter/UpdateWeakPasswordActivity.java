package cn.yjt.oa.app.personalcenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.BackTitleFragmentActivity;
import cn.yjt.oa.app.http.ApiHelper;
import cn.yjt.oa.app.http.ProgressDialogResponseListener;

public class UpdateWeakPasswordActivity extends BackTitleFragmentActivity
		implements OnClickListener {

	private EditText tvOldPwd;
	private EditText tvNewPwd;
	private EditText tvSurePwd;
	private Button btnSure;
	private Button btnCancle;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.change_pasword);
		initview();
	}

	private void initview() {
		tvOldPwd = (EditText) findViewById(R.id.change_pwd_old_pwd);
		tvNewPwd = (EditText) findViewById(R.id.change_pwd_new_pwd);
		tvSurePwd = (EditText) findViewById(R.id.change_pwd_sure_pwd);
		btnSure = (Button) findViewById(R.id.change_pwd_sure);
		btnCancle = (Button) findViewById(R.id.change_pwd_cancle);

		btnSure.setOnClickListener(this);
		btnCancle.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.change_pwd_sure:
			if (checkInput()) {
				updateWeakPassword();
			}
			break;
		case R.id.change_pwd_cancle:
			onBackPressed();
			break;

		default:
			break;
		}
	}

	private boolean checkInput() {
		if (TextUtils.isEmpty(tvOldPwd.getText())) {
			return false;
		}

		if (TextUtils.isEmpty(tvNewPwd.getText())) {
			return false;
		}
		if (TextUtils.isEmpty(tvSurePwd.getText())) {
			return false;
		}

		if (TextUtils.equals(tvNewPwd.getText(), tvSurePwd.getText())) {
			return false;
		}
		
		if(checkWeak(tvNewPwd.getText().toString())){
			return false;
		}
		return true;
	}
	
	private boolean checkWeak(String pwd){
		
		return false;
	}

	private void updateWeakPassword() {
		ApiHelper.updatePassword(tvOldPwd.getText().toString(), tvNewPwd
				.getText().toString(),
				new ProgressDialogResponseListener<String>(this) {

					@Override
					public void onSuccess(String payload) {
						finish();
						Toast.makeText(getApplicationContext(), "密码修改成功，请重新登录", Toast.LENGTH_SHORT).show();
					}
				});
	}
	
	public static void launch(Context context,String phone){
		Intent intent = new Intent(context, UpdateWeakPasswordActivity.class);
		intent.putExtra("phone", phone);
		context.startActivity(intent );
	}

}
