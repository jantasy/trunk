package cn.yjt.oa.app.personalcenter;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.Cancelable;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsMessage;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import cn.yjt.oa.app.LaunchActivity;
import cn.yjt.oa.app.LeaveEnterpriseActivity;
import cn.yjt.oa.app.MainActivity;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.app.utils.LogUtils;
import cn.yjt.oa.app.app.utils.UpdateHelper;
import cn.yjt.oa.app.beans.LoginInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.beans.UserLoginInfo;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.component.UmengBaseActivity;
import cn.yjt.oa.app.enterprise.CreateEnterpriseActivity;
import cn.yjt.oa.app.http.ApiHelper;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.AsyncRequest.Builder;
import cn.yjt.oa.app.utils.CheckNetUtils;
import cn.yjt.oa.app.utils.IPSettings;

public class LoginActivity extends UmengBaseActivity implements OnClickListener {

	private static final int REQUEST_CODE_REGISTER = 0;

	private EditText loginNameEt;
	private EditText passwordEt;
	private List<UserLoginInfo> enterpriseList = new ArrayList<UserLoginInfo>();
	private Bitmap bgBitmap;
	Cancelable loginTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		UpdateHelper.updateOnlyOnceLaunch(this);
		bgBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.login_bg);
		setContentView(R.layout.login_activity_layout);
		findViewById(R.id.login_layout).setBackgroundDrawable(
				new BitmapDrawable(getResources(), bgBitmap));
		findViewById(R.id.login).setOnClickListener(this);
		findViewById(R.id.login_experience).setOnClickListener(this);
		findViewById(R.id.regist).setOnClickListener(this);
		findViewById(R.id.forget_password).setOnClickListener(this);
		findViewById(R.id.clear).setOnClickListener(this);
		loginNameEt = (EditText) findViewById(R.id.login_name);
		passwordEt = (EditText) findViewById(R.id.password);
		passwordEt.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					postClientlogin();
				}
				return handled;
			}
		});
		ImageView iconIv = (ImageView) findViewById(R.id.icon);
		// iconIv.setImageBitmap(BitmapUtils.getDefaultHeadIcon(this, -1,
		// R.drawable.login_default_icon));
		iconIv.setImageResource(R.drawable.contactlist_contact_icon_default);
		UserLoginInfo info = AccountManager.getCurrentLogiInfo(this);

		findViewById(R.id.icon).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					touchChangedIPButton(v);
				}
				return false;
			}
		});

		boolean isLoginWithEnterpriseList = getIntent().getBooleanExtra(
				"loginWithEnterpriseList", false);
		if (isLoginWithEnterpriseList) {
			clientlogin(
					AccountManager.getCurrentLogiInfo(getApplicationContext())
							.getPhone(),
					AccountManager.getPassword(getApplicationContext()));
		} else {
			if (info != null && !TextUtils.isEmpty(info.getPhone())) {
				loginNameEt.setText(info.getPhone());
				// passwordEt.setText(info.getPassword());
			}
		}
	}

	@Override
	public void onClick(View v) {
		hideSoftInput();
		switch (v.getId()) {
		case R.id.login:
			postClientlogin();
			break;
		case R.id.login_experience:
			postLoginExperience();

            /*记录操作 0103*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_EXPE_ACCOUNT);
			break;
		case R.id.regist:

			Intent registIntent = new Intent(this, RegistActivity.class);
			registIntent.putExtra("case", "regist");
			startActivityForResult(registIntent, REQUEST_CODE_REGISTER);


			break;
		case R.id.forget_password:
			Intent forgetIntent = new Intent(this, RegistActivity.class);
			forgetIntent.putExtra("case", "forgetpassword");
			startActivityForResult(forgetIntent, REQUEST_CODE_REGISTER);

			break;
		case R.id.clear:
			loginNameEt.setText("");
			passwordEt.setText("");
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (REQUEST_CODE_REGISTER == requestCode) {
			if (resultCode == RESULT_OK) {
				finish();
			}
		}
	}

	private void postClientlogin() {
		String loginName = loginNameEt.getText().toString();
		String password = passwordEt.getText().toString();
		if (TextUtils.isEmpty(loginName) || TextUtils.isEmpty(password)) {
			showDialogLogin(getString(R.string.login_info_fail));
			return;
		}
		if (!CheckNetUtils.hasNetWork(this)) {
			showDialogLogin(getString(R.string.connect_network_fail));
			return;
		}
		clientlogin(loginName, password);
	}

	private void postLoginExperience() {
		String loginName = "10000";
		String password = "10000";

		clientlogin(loginName, password);

	}

	private void clientlogin(final String loginName, final String password) {
		LoginInfo info = new LoginInfo();
		info.setLoginName(loginName);
		info.setPassword(password);
		info.setIccd(getIcc());
		if (dialog == null) {
			dialog = new ProgressDialog(this);
			dialog.setMessage(getString(R.string.logining));
		}
		dialog.show();
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_CLIENT_LOGIN);
		builder.setRequestBody(info);
		Type type = new TypeToken<Response<List<UserLoginInfo>>>() {
		}.getType();
		builder.setResponseType(type);
		builder.setResponseListener(new Listener<Response<List<UserLoginInfo>>>() {

			@Override
			public void onResponse(Response<List<UserLoginInfo>> response) {
				clientLoginCancelable = null;
				dialog.dismiss();
				if (response.getCode() == 0) {
					List<UserLoginInfo> payload = response.getPayload();
					if (payload != null && payload.size() > 0) {
						if (payload.size() == 1) {
							UserLoginInfo loginInfo = payload.get(0);
							loginInfo.setPassword(password);
							login(loginInfo);
						} else {
							enterpriseList.clear();
							enterpriseList.addAll(payload);
							showDialogEnterprise(enterpriseList, password);
						}

					}
				} else if (response.getCode() == Response.RESPONSE_NOT_YOUR_ICCID
						|| response.getCode() == Response.RESPONSE_OTHER_ICCID) {
					showVerifyCodeConfirmDialog(response);
				} else if(response.getCode() == Response.RESPONSE_WEAK_PASSWORD){
					AlertDialogBuilder.newBuilder(LoginActivity.this).setMessage(response.getDescription()).setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							UpdateWeakPasswordActivity.launch(LoginActivity.this, loginName);
						}
					}).setNegativeButton("取消", null).show();
				}else {
					showDialogLogin(response.getDescription());
				}
			}

			@Override
			public void onErrorResponse(InvocationError error) {
				clientLoginCancelable = null;
				dialog.dismiss();
				showDialogLogin(getString(R.string.login_fail));
			}
		});
		clientLoginCancelable = builder.build().post();
	}

	ProgressDialog dialog;

	private CountDownTimer timer;

	private void login(final UserLoginInfo userLoginInfo) {
		if (dialog == null) {
			dialog = new ProgressDialog(this);
			dialog.setMessage(getString(R.string.logining));
		}
		dialog.show();
		loginTask =ApiHelper.realLogin(new Listener<Response<UserInfo>>() {
			
			@Override
			public void onResponse(Response<UserInfo> response) {
				dialog.dismiss();
				if (response.getCode() == 0) {

					UserInfo info = response.getPayload();
					if (info != null) {
						final long userId = info.getId();
						final String custId = info.getCustId();
						// 百度推送时需要设置的phone tag
						final String phone = info.getPhone();
						new Thread() {
							public void run() {
								// 将isUserLogout设置为false,
								// isUserLogout为true表示用户登出不推送信息
								// 友盟推送使用的setAlias
								// MainApplication.setAlias("" + userId);
								// 换成百度推送使用的setAlias
								MainApplication.setAlias("" + userId, phone);
								MainApplication.addTag("cust" + custId);
							};
						}.start();
					}
					AccountManager.saveCurrentLogiInfo(LoginActivity.this,
							userLoginInfo.getUserId(),
							userLoginInfo.getPhone(),
							userLoginInfo.getPassword());

					MainActivity.userName = info.getPhone();

					LaunchActivity.LOGO_ICON = Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/yijitong/"
							+ MainActivity.userName
							+ "/company_logo.jpg";
					LaunchActivity.savaLogo(info, LoginActivity.this,
							info.getPhone(),
							getResources().getString(R.string.screen_width));
					if (TextUtils.isEmpty(info.getName())) {
						PersonalHomeActivity.launchWithApplyStatus(
								LoginActivity.this, info.getCustId(),
								info.getHasApplyCust());

					} else if ("1".equals(info.getCustId())
							&& info.getHasApplyCust() == 0) {
						CreateEnterpriseActivity.launch(LoginActivity.this);
					} else {
						Intent personalIntent = new Intent(LoginActivity.this,
								MainActivity.class);
						personalIntent.putExtra("info", info);
						startActivity(personalIntent);
					}
					AccountManager
							.updateUserInfo(getApplicationContext(), info);
					MainApplication.sendLoginBroadcast(LoginActivity.this);
					finish();
				} else if (response.getCode() == Response.RESPONSE_NOT_YOUR_ICCID||response.getCode() == Response.RESPONSE_OTHER_ICCID) {
					LeaveEnterpriseActivity.launch(response.getDescription());
				}
				else {
					showDialogLogin(response.getDescription());
				}
			}
			
			@Override
			public void onErrorResponse(InvocationError arg0) {
				dialog.dismiss();
				// Intent fail = new Intent(LoginActivity.this,
				// LoginOrRegistFail.class);
				// fail.putExtra("from", "login");
				// startActivity(fail);
				// Toast.makeText(LoginActivity.this, R.string.login_fail,
				// 0).show();
				showDialogLogin(getString(R.string.login_fail));				
			}
		}, userLoginInfo);
	}

	private String getIcc() {
		TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		return manager.getSimSerialNumber();
	}

	private void showDialogLogin(String message) {
		if (isFinishing()) {
			return;
		}
		AlertDialogBuilder
				.newBuilder(this)
				.setTitle(getResources().getString(R.string.dialog_title))
				.setMessage(message)
				.setPositiveButton(
						getResources().getString(R.string.dialog_sure), null)
				.show();
	}

	private void showVerifyCodeConfirmDialog(Response<?> response) {
		AlertDialogBuilder.newBuilder(this)
				.setMessage(response.getDescription())
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						showVerifyCodeDialog();
					}
				}).setNegativeButton("取消", null).show();
	}

	private void sendVerifyCode(final String phoneMunber, final View codeBt,
			final EditText codeEt) {
		if (!CheckNetUtils.hasNetWork(this)) {
			Toast.makeText(getApplicationContext(),
					R.string.connect_network_fail, Toast.LENGTH_SHORT).show();
			return;
		}
		codeBt.setClickable(false);
		Builder builder = new Builder();
		builder.setModule(AsyncRequest.MODULE_REGISTER);
		builder.addQueryParameter("loginName", phoneMunber);
		builder.setResponseType(new TypeToken<Response<String>>() {
		}.getType());
		builder.setResponseListener(new Listener<Response<String>>() {

			@Override
			public void onResponse(Response<String> response) {
				if (response.getCode() == 0) {
					String code = response.getPayload();
					if (MainApplication.isTestMode(getApplicationContext())) {
						codeEt.setText(code);
					}

					LogUtils.i("regist", code);

					Toast.makeText(getApplicationContext(),
							R.string.send_verify_code, Toast.LENGTH_SHORT)
							.show();

				} else {
					Toast.makeText(getApplicationContext(),
							R.string.send_verify_code_file, Toast.LENGTH_SHORT)
							.show();
				}
				codeBt.setClickable(true);
			}

			@Override
			public void onErrorResponse(InvocationError error) {
				Toast.makeText(getApplicationContext(),
						R.string.send_verify_code_file, Toast.LENGTH_SHORT)
						.show();
			}
		});
		builder.build().get();
	}

	private void showVerifyCodeDialog() {
		
		receiver = new SMSReceiver();
		View view = LayoutInflater.from(this).inflate(
				R.layout.view_verify_code,
				(ViewGroup) findViewById(R.id.login_layout), false);
		final EditText verifyCodeEditText = (EditText) view
				.findViewById(R.id.et_verify_code);

		receiver.editText = verifyCodeEditText;
		final Button btnSendCode = (Button) view.findViewById(R.id.btn_verify_code);
		btnSendCode.setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						btnSendCode.setEnabled(false);
						sendVerifyCode(loginNameEt.getText().toString(), v,
								verifyCodeEditText);
						timer.start();
					}
				});
		final AlertDialog dialog = AlertDialogBuilder
				.newBuilder(this)
				.setTitle("验证手机")
				.setView(view)
				.setPositiveButton("验证", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						if (TextUtils.isEmpty(verifyCodeEditText.getText())) {
							Toast.makeText(getApplicationContext(), "请填写验证码",
									Toast.LENGTH_SHORT).show();
						} else {
							//TODO:
							//postClientlogin();
						    postVerify(verifyCodeEditText);
						}
					}
				}).setNegativeButton("取消", null)
				.setOnCancelListener(new DialogInterface.OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						unregisterReceiver(receiver);
						if(timer != null){
							timer.cancel();
							timer = null;
						}
					}
				}).create();
		verifyCodeEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 6) {
					dialog.getButton(DialogInterface.BUTTON_POSITIVE)
							.setEnabled(true);
				} else {
					dialog.getButton(DialogInterface.BUTTON_POSITIVE)
							.setEnabled(false);
				}
			}
		});
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialog.show();
		dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(receiver, filter);
		
		
		timer = new CountDownTimer(60000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				btnSendCode.setText(millisUntilFinished / 1000 + " 秒后可重发");
			}

			@Override
			public void onFinish() {
				btnSendCode.setText(R.string.verify_code);
				btnSendCode.setEnabled(true);
			}
		};

	}
	
	
	public class SMSReceiver extends BroadcastReceiver {

		private EditText editText;

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
								editText.setText(replaceAll);
							}
						} catch (Exception e) {
						}
					}
				}
			}
		}
	}

	private void postVerify(EditText verifyCodeEditText) {
		LoginInfo requestBody = new LoginInfo();
		requestBody.setLoginName(loginNameEt.getText().toString());
		requestBody.setIccd(getIcc());
		requestBody.setVerifyCode(verifyCodeEditText.getText().toString());
		Type responseType = new TypeToken<Response<String>>() {
		}.getType();
		verifyCancelable = new AsyncRequest.Builder()
				.setModule(AsyncRequest.MODULE_CHECKICCID)
				.setRequestBody(requestBody).setResponseType(responseType)
				.setResponseListener(new Listener<Response<String>>() {

					@Override
					public void onErrorResponse(InvocationError arg0) {
						Toast.makeText(getApplicationContext(), "网络请求失败，请稍后重试",
								Toast.LENGTH_SHORT).show();
						progressDialog.dismiss();
						progressDialog = null;
						verifyCancelable = null;
					}

					@Override
					public void onResponse(Response<String> response) {
						progressDialog.dismiss();
						progressDialog = null;
						verifyCancelable = null;
						if (response.getCode() == 0) {
							Toast.makeText(getApplicationContext(),
									"验证成功，正在为您重新登录", Toast.LENGTH_SHORT).show();
							postClientlogin();
						} else {
							Toast.makeText(getApplicationContext(),
									response.getDescription(),
									Toast.LENGTH_SHORT).show();
						}

					}
				}).build().put();
		progressDialog = ProgressDialog.show(this, null, "正在验证...");
		progressDialog
				.setOnCancelListener(new DialogInterface.OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						progressDialog = null;
						verifyCancelable.cancel();
						verifyCancelable = null;
					}
				});
	}

	private void showDialogEnterprise(
			final List<UserLoginInfo> loginWithEnterpriseList,
			final String password) {
		if (isFinishing()) {
			return;
		}
		AlertDialog.Builder builder = AlertDialogBuilder.newBuilder(this);
		AlertDialog alertDialog = builder
				.setTitle("请选择您要登录的单位")
				.setAdapter(
						new LoginEnterpriseAdapter(this,
								loginWithEnterpriseList),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								UserLoginInfo info = loginWithEnterpriseList
										.get(which);
								info.setPassword(password);
								login(info);
							}
						}).create();
		View footView = View.inflate(this, R.layout.foot_view, null);
		alertDialog.setView(footView);
		alertDialog.setCancelable(false);

		alertDialog.show();
	}

	@Override
	protected void onPause() {
		hideSoftInput();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bgBitmap != null) {
			bgBitmap.recycle();
			bgBitmap = null;
		}
		if (loginTask != null) {
			loginTask.cancel();
		}
		if (clientLoginCancelable != null) {
			clientLoginCancelable.cancel();
		}
	}

	private Handler handler = new Handler();
	private int counts;
	private Runnable clearTouchCounts = new Runnable() {

		@Override
		public void run() {
			System.out.println("clear tounts");
			counts = 0;
		}
	};

	private Cancelable clientLoginCancelable;

	private ProgressDialog progressDialog;

	private Cancelable verifyCancelable;

	private SMSReceiver receiver;

	private void touchChangedIPButton(View v) {
		handler.removeCallbacks(clearTouchCounts);
		handler.postDelayed(clearTouchCounts, 500);
		if (++counts >= 5) {
			IPSettings.launch(this);
		}
	}

	public static void launch(Context context) {
		Intent intent = new Intent(context, LoginActivity.class);
		context.startActivity(intent);
	}

	public static void launchWithEnterpriseList(Context context) {
		Intent intent = new Intent(context, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("loginWithEnterpriseList", true);
		context.startActivity(intent);
	}
}
