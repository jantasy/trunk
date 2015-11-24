package cn.yjt.oa.app.personalcenter;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.app.utils.LogUtils;
import cn.yjt.oa.app.beans.ChangePasswordInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.UserLoginInfo;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.utils.CheckNetUtils;

public class ChangePassword extends TitleFragmentActivity implements OnClickListener {
	
	private EditText tvOldPwd, tvNewPwd, tvSurePwd;
	
	private Button btnSure, btnCancle;
	
	private ProgressDialog proDialog;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
        /*记录操作 0105*/
        OperaEventUtils.recordOperation(OperaEvent.OPERA_MODIFY_PASSWORD);

		setContentView(R.layout.change_pasword);
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		
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
	public void onLeftButtonClick() {
		super.onLeftButtonClick();
		this.finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.change_pwd_sure:
			changePwd();
			break;
		case R.id.change_pwd_cancle:
			this.finish();
			break;
		default:
			break;
		}
	}

	// 修改密码
	private void changePwd() {
		final UserLoginInfo info = AccountManager.getCurrentLogiInfo(this);
		String primitivePwd = "";
		if (info.getUserId()!=0 && !TextUtils.isEmpty(info.getPassword())) {
			primitivePwd = info.getPassword();
		}
		
		LogUtils.i("===oldPwd = " + primitivePwd);

		String oldPwd = tvOldPwd.getText().toString().trim();
		final String newPwd = tvNewPwd.getText().toString().trim();
		String surePwd = tvSurePwd.getText().toString().trim();
		if (!CheckNetUtils.hasNetWork(this)) {// 无网
			showLuoboDialog(R.string.connect_network_fail);
			return;
		}
		if (TextUtils.isEmpty(oldPwd) || !primitivePwd.equals(oldPwd)) {// 旧密码错误
			showLuoboDialog(R.string.change_pwd_old_pwd_wrong);
			return;
		}
		if (TextUtils.isEmpty(newPwd)) {// 新密码不能为空
			showLuoboDialog(R.string.change_pwd_new_pwd_can_not_null);
			return;
		}
		if (!newPwd.equals(surePwd)) {// 两次密码不一致
			showLuoboDialog(R.string.change_pwd_not_same_twice);
			return;
		}
		
		showWaitDialog(R.string.change_pwd_wait);

		ChangePasswordInfo cpInfo = new ChangePasswordInfo();
		cpInfo.setOldPassword(oldPwd);
		cpInfo.setNewPassword(newPwd);

		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_PERSONALINFO);
		builder.setRequestBody(cpInfo);
		Type type = new TypeToken<Response<Object>>() {
		}.getType();
		builder.setResponseType(type);
		builder.setResponseListener(new Listener<Response<Object>>() {

			public void onErrorResponse(
					io.luobo.common.http.InvocationError error) {
				proDialog.dismiss();
				showLuoboDialog(R.string.change_pwd_change_pwd_fail);
			}

			@Override
			public void onResponse(Response<Object> response) {
				proDialog.dismiss();
				if (response != null && response.getCode() == 0) {
//					showLuoboDialog(R.string.change_pwd_change_pwd_sucess);
					Toast.makeText(getApplicationContext(), R.string.change_pwd_change_pwd_sucess, Toast.LENGTH_SHORT).show();
					AccountManager.saveCurrentLogiInfo(getApplicationContext(), info.getUserId(),info.getPhone(),newPwd);
					ChangePassword.this.finish();
				} else {
					showLuoboDialog(R.string.change_pwd_change_pwd_fail);
				}
			}
		});
		builder.build().post();

	}
	
	private void showWaitDialog(int msgId) {
		if (proDialog == null) {
			proDialog = new ProgressDialog(this);
			proDialog.setCancelable(false);
			proDialog.setCanceledOnTouchOutside(false);
		}
		proDialog.setMessage(getResources().getString(msgId));
		if (!proDialog.isShowing()) {
			proDialog.show();
		}
	}

	private void showLuoboDialog(int msgId) {
		AlertDialogBuilder.newBuilder(this)
				.setTitle(getResources().getString(R.string.dialog_title))
				.setMessage(getResources().getString(msgId))
				.setPositiveButton(
						getResources().getString(R.string.dialog_sure), null)
				.show();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (proDialog != null && proDialog.isShowing()) {
			proDialog.dismiss();
		}
	}
	
}
