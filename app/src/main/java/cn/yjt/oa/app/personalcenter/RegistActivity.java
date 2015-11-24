package cn.yjt.oa.app.personalcenter;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;
import io.luobo.common.utils.MD5Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsMessage;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.BuildConfig;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.WebViewActivity;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.app.utils.LogUtils;
import cn.yjt.oa.app.beans.LoginInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.enterprise.contact.PhoneUtils;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.AsyncRequest.Builder;
import cn.yjt.oa.app.security.VerifyCodeEncryptFactory;
import cn.yjt.oa.app.utils.CheckNetUtils;
import cn.yjt.oa.app.utils.ErrorUtils;

public class RegistActivity extends TitleFragmentActivity implements
		OnClickListener {
	private EditText phoneMunberEt;
	public static final int ACTION_CODE=1;
	private EditText codeEt;
	private Button codeBt;
	private CountDownTimer timer;
	private String code;
	private EditText passwordEt;
	private EditText checkPasswordEt;
	private EditText recommendPhoneEt;
	private String from;
	private SMSReceiver smsReceiver;
	private Handler handler;

	private String encryptType;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler=new Handler(){

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case ACTION_CODE:
					codeEt.setText((String)msg.obj);
					break;

				default:
					break;
				}
			}
			
		};
		setContentView(R.layout.regist_activity_layout);
		from = getIntent().getStringExtra("case");

		phoneMunberEt = (EditText) findViewById(R.id.phone_munber);
		codeEt = (EditText) findViewById(R.id.input_code);
		passwordEt = (EditText) findViewById(R.id.password);
		checkPasswordEt = (EditText) findViewById(R.id.check_password);
		recommendPhoneEt = (EditText) findViewById(R.id.recommend_phone);
		codeBt = (Button) findViewById(R.id.get_code);
		
		

		findViewById(R.id.clear).setOnClickListener(this);
		findViewById(R.id.regist_bt).setOnClickListener(this);
		findViewById(R.id.regist_bt).setOnClickListener(this);
		codeBt.setOnClickListener(this);

		getLeftbutton().setImageResource(R.drawable.navigation_back);

		
		if ("regist".equals(from)) {
			setTitle(R.string.regist);
			userProtocalCheckBox = (CheckBox) findViewById(R.id.user_protocal_check);
			userProtocalCheckBox.setVisibility(View.VISIBLE);
			userProtocalCheckBox.setChecked(true);
			TextView userProtocal = (TextView) findViewById(R.id.user_protocal);
			userProtocal.setVisibility(View.VISIBLE);
			userProtocal.setText(Html.fromHtml("我已同意<a href=\"file:///android_asset/user_protocal.html\">《用户协议》</a>"));
			userProtocal.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					WebViewActivity.launchWithTitleAndUrl(RegistActivity.this, "用户协议", "file:///android_asset/user_protocal.html");
				}
			});
		} else {
			setTitle("忘记密码");
		}

		timer = new CountDownTimer(60000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				codeBt.setText(millisUntilFinished / 1000 + " 秒后可重发");
			}

			@Override
			public void onFinish() {
				codeBt.setText(R.string.verify_code);
				codeBt.setClickable(true);
				if(smsReceiver!=null){
					unregisterReceiver(smsReceiver);
					smsReceiver=null;
				}
			}
		};
		
		
	}

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.get_code:
			String phoneMunber =PhoneUtils.formatPhoneNumber(phoneMunberEt.getText().toString());
			if (TextUtils.isEmpty(phoneMunber) || phoneMunber.length()!=11 ||!PhoneUtils.isMobileNum(phoneMunber)) {
				showDialogRegist(R.string.phone_munber_format_error);
				return;
			}
			sendVerifyCode(phoneMunber);
			smsReceiver=new SMSReceiver();
			IntentFilter filter=new IntentFilter();
			filter.addAction("android.provider.Telephony.SMS_RECEIVED");
			registerReceiver(smsReceiver, filter);
			break;
		case R.id.regist_bt:
			if ("regist".equals(from)) {
				regist();
			} else {
				forgetPassword();
			}
			break;
		case R.id.clear:
			phoneMunberEt.setText("");
			break;
		default:
			break;
		}
	}


	private String getIcc() {
		TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		return manager.getSimSerialNumber();
	}

    private String getImei() {
        TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        return manager.getDeviceId();
    }


    ProgressDialog dialog;
	private CheckBox userProtocalCheckBox;

	private void regist() {
		
		if(userProtocalCheckBox != null&&!userProtocalCheckBox.isChecked()){
			Toast.makeText(getApplicationContext(), "请同意用户使用协议", Toast.LENGTH_SHORT).show();
			return;
		}
		final String phoneMunber = phoneMunberEt.getText().toString();
		final String inputCode = codeEt.getText().toString();
		final String password = passwordEt.getText().toString();
		String checkPassword = checkPasswordEt.getText().toString();
		String recommendPhone =PhoneUtils.formatPhoneNumber(recommendPhoneEt.getText().toString());
		
		if (TextUtils.isEmpty(inputCode) || TextUtils.isEmpty(password)
				|| TextUtils.isEmpty(checkPassword)) {
			showDialogRegist(R.string.has_not_completed_regist_info);
			return;
		}
		if (!password.equals(checkPassword)) {
			showDialogRegist(R.string.password_not_same);
			return;
		}
		if (!CheckNetUtils.hasNetWork(this)) {
			showDialogRegist(R.string.connect_network_fail);
			return;
		}
		if (!TextUtils.isEmpty(recommendPhone)) {
			if(recommendPhone.length()!=11 ||!PhoneUtils.isMobileNum(recommendPhone)){
				showDialogRegist(R.string.recommend_phone_munber_format_error);
				return;
			}
		}
		final LoginInfo info = new LoginInfo();
		info.setLoginName(phoneMunber);
		info.setVerifyCode(VerifyCodeEncryptFactory.createVerifyCodeEncrypt(encryptType).encrypt(inputCode));
		info.setPassword(password);
		info.setIccd(getIcc());
        info.setImei(getImei());
		info.setRecommendPhone(recommendPhone);
		if (dialog == null) {
			dialog = new ProgressDialog(this);
			dialog.setMessage(getString(R.string.registing));
		}
		dialog.show();
		Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_REGISTER);
		builder.setRequestBody(info);
		Type type = new TypeToken<Response<UserInfo>>() {
		}.getType();
		builder.setResponseType(type);
		builder.setResponseListener(new Listener<Response<UserInfo>>() {

			@Override
			public void onResponse(final Response<UserInfo> response) {
				dialog.dismiss();
				
				if (response.getCode() == 0) {
					UserInfo userInfo = response.getPayload();
					
					if (userInfo != null) {
						AccountManager.updateUserInfo(RegistActivity.this,
								userInfo);
						RegiestPersonalHomeActivity.launchWithApplyStatus(
								RegistActivity.this, userInfo.getCustId(),userInfo.getHasApplyCust());

                    }

					Toast.makeText(RegistActivity.this, "注册成功,请完善个人信息..", Toast.LENGTH_SHORT)
							.show();
					// add by zhil 2014.9.21待测
					// 保存用户信息
					AccountManager.saveCurrentLogiInfo(RegistActivity.this,userInfo.getId(),info.getLoginName(),info.getPassword());
					MainApplication.sendLoginBroadcast(RegistActivity.this);
					new Thread(){
						public void run() {
							// 友盟使用的setAlias
//							MainApplication.setAlias(""
//									+ response.getPayload().getId());
							//	百度推送使用的setAlias
							MainApplication.setAlias("" + response.getPayload().getId(), response.getPayload().getPhone());
							MainApplication.addTag("cust"
									+ response.getPayload().getCustId());
						};
					}.start();
					setResult(RESULT_OK);

                     /*记录操作 0102*/
                    OperaEventUtils.recordOperation(OperaEvent.OPERA_REGIEST);

					finish();
				} else {
					showDialogRegist(response.getDescription());
				}
			}

			@Override
			public void onErrorResponse(InvocationError error) {
				dialog.dismiss();
				showDialogRegist(ErrorUtils.getErrorDescription(error.getErrorType()));
			}
		});
		builder.build().post();
	}

	private void showDialogRegist(int msgId) {
		showDialogRegist(getResources().getString(msgId));
	}
	private void showDialogRegist(String message) {
		if(isFinishing()){
			return;
		}
		AlertDialogBuilder.newBuilder(this)
		.setTitle(getResources().getString(R.string.dialog_title))
		.setMessage(message)
		.setPositiveButton(
				getResources().getString(R.string.dialog_sure), null)
				.show();
	}

	private void forgetPassword() {
		String phoneMunber = phoneMunberEt.getText().toString();
		//TODO：MD5加密验证码
//		String inputCode = MD5Utils.md5(codeEt.getText().toString()+"yjt");
		String inputCode = codeEt.getText().toString();
		String newPassword = passwordEt.getText().toString();
		String oldPassword = checkPasswordEt.getText().toString();
		if (TextUtils.isEmpty(inputCode) || TextUtils.isEmpty(newPassword)
				|| TextUtils.isEmpty(oldPassword)) {
			// Toast.makeText(this, R.string.has_not_completed_regist_info, 0)
			// .show();
			
			showDialogRegist(R.string.has_not_completed_regist_info);
			return;
		}
		if (!newPassword.equals(oldPassword)) {
			// Toast.makeText(this, "两次密码不能一样", 0).show();
			showDialogRegist(R.string.password_not_same);
			return;
		}
		LoginInfo info = new LoginInfo();
		info.setLoginName(phoneMunber);
		info.setVerifyCode(inputCode);
		info.setPassword(newPassword);
		info.setIccd(getIcc());
		if (dialog == null) {
			dialog = new ProgressDialog(this);
			dialog.setMessage(getString(R.string.forget_password));
		}
		dialog.show();
		Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_REGISTER);
		builder.setRequestBody(info);
		Type type = new TypeToken<Response<UserInfo>>() {
		}.getType();
		builder.setResponseType(type);
		builder.setResponseListener(new Listener<Response<UserInfo>>() {

			@Override
			public void onResponse(Response<UserInfo> response) {
				dialog.dismiss();
				if (response.getCode() == 0) {
					if (response.getPayload() != null) {
						AccountManager.updateUserInfo(RegistActivity.this,
								response.getPayload());

                        /*记录操作 0104*/
                        OperaEventUtils.recordOperation(OperaEvent.OPERA_FORGET_PASSWORD);

					}
					
					finish();
				}else{
					if(response.getDescription()!=null){
						showDialogRegist(response.getDescription());
					}else{
						showDialogRegist("该用户尚未注册翼机通+，无法找回密码");
					}
				}
			}

			@Override
			public void onErrorResponse(InvocationError error) {
				dialog.dismiss();
				showDialogRegist("服务器繁忙");
			}
		});
		builder.build().post();
	}

	private void sendVerifyCode(final String phoneMunber) {
		if (!CheckNetUtils.hasNetWork(this)) {
			// Toast.makeText(this, "未链接网络，请先设置网络，再重试", 0).show();
			showDialogRegist(R.string.connect_network_fail);
			return;
		}
		codeBt.setClickable(false);
		timer.start();
		Builder builder = new Builder();
		builder.setModule(AsyncRequest.MODULE_REGISTER);
		builder.addQueryParameter("loginName", phoneMunber);
		builder.setResponseType(new TypeToken<Response<String>>() {
		}.getType());
		builder.setResponseListener(new Listener<Response<String>>() {

			
			@Override
			public void onResponse(Response<String> response) {
				if (response.getCode() == 0) {
					encryptType = response.getPayload();
					
					
					LogUtils.i("regist", code);

					// *
//					sendMessage(phoneMunber, code);
					/*
					 * / Toast.makeText(RegistActivity.this, code, 0).show(); //
					 */
					// Toast.makeText(RegistActivity.this,
					// R.string.send_verify_code, 0).show();
					showDialogRegist(R.string.send_verify_code);

				} else {
					// Toast.makeText(RegistActivity.this, "发送验证码失败，请重试...", 0)
					// .show();
					showDialogRegist(R.string.send_verify_code_file);
					timer.cancel();
					codeBt.post(new Runnable() {
						
						@Override
						public void run() {
							codeBt.setText(R.string.verify_code);
							codeBt.setClickable(true);
							if(smsReceiver!=null){
								unregisterReceiver(smsReceiver);
								smsReceiver=null;
							}
						}
					});
					
				}
			}

			@Override
			public void onErrorResponse(InvocationError error) {
				// Toast.makeText(RegistActivity.this, "发送验证码失败，请重试...",
				// 0).show();
				showDialogRegist(R.string.send_verify_code_file);
				if(smsReceiver!=null){
					unregisterReceiver(smsReceiver);
					smsReceiver=null;
				}
			}
		});
		builder.build().get();
	}

	private void sendMessage(String phoneMunber, String code) {
		if (!BuildConfig.DEBUG) {
			codeBt.setClickable(false);
			SmsManager manager = SmsManager.getDefault();
			ArrayList<String> list = manager.divideMessage("翼机通验证码：" + code);
			manager.sendMultipartTextMessage(phoneMunber, null, list, null,
					null);
		}else{
			Toast.makeText(getApplicationContext(), "验证码："+code, Toast.LENGTH_LONG).show();
		}
	}
	
	public class SMSReceiver extends BroadcastReceiver{

		@SuppressWarnings("deprecation")
		@Override
		public void onReceive(Context arg0, Intent intent) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Object[] pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] messages = new SmsMessage[pdus.length];
				for (int i = 0; i < pdus.length; i++) {
					messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				}
				for (SmsMessage message : messages) {
					if (message != null) {

						try {
							String strMsg = message.getDisplayMessageBody();
							if (strMsg.contains("感谢您使用\"翼机通+\"，本次操作验证码为")) {
								String replaceAll = Pattern.compile("[^0-9]")
										.matcher(strMsg).replaceAll("")
										.substring(0, 6);
								Message msg = handler.obtainMessage();
								msg.what = ACTION_CODE;
								msg.obj = replaceAll;
								handler.sendMessage(msg);
								unregisterReceiver(smsReceiver);
								smsReceiver = null;
							}
						} catch (Exception e) {
							// TODO: handle exception
							unregisterReceiver(smsReceiver);
							smsReceiver = null;
						}
						
					}

				}

			}
		}
		 
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(smsReceiver!=null){
			unregisterReceiver(smsReceiver);
			smsReceiver=null;
		}
	}
}
