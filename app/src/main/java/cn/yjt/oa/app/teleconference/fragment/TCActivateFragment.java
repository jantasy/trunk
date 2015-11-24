package cn.yjt.oa.app.teleconference.fragment;

import java.util.Map;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.TCActiveCodeResponse;
import cn.yjt.oa.app.teleconference.http.TCAsyncRequest;
import cn.yjt.oa.app.teleconference.http.TCAsyncRequest.AccessTokenCallback;
import cn.yjt.oa.app.teleconference.http.TCAsyncRequest.ActivateAccountCallback;

public class TCActivateFragment extends TCBaseFragment implements
		OnClickListener, AccessTokenCallback ,ActivateAccountCallback,OnKeyListener{
	private String defaultPhoneNumber;
	private UserPresenter presenter;
	private CountDownTimer timer;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		defaultPhoneNumber = AccountManager.getCurrent(getActivity()).getPhone();
		initTimer();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_tc_activate, container,false);
		initView(root);
		return root;
	}
	
	private EditText phoneNumber;
	private EditText verificationCode;
	private ImageButton clearText;
	private Button btnVerificationCode;
	private Button activate;
	
	public void initView(View view){
		phoneNumber = (EditText) view.findViewById(R.id.input_phone_num);
		phoneNumber.setText(defaultPhoneNumber);
		verificationCode = (EditText) view.findViewById(R.id.input_verification_code);
		verificationCode.setOnKeyListener(this);
		clearText = (ImageButton) view.findViewById(R.id.ib_clear_text);
		clearText.setOnClickListener(this);
		btnVerificationCode = (Button) view.findViewById(R.id.btn_verification_code);
		btnVerificationCode.setOnClickListener(this);
		activate = (Button) view.findViewById(R.id.activate_ok);
		activate.setOnClickListener(this);
	}
	
	public void initTimer(){
		timer = new CountDownTimer(60000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				btnVerificationCode.setText(millisUntilFinished / 1000 + " 秒后可重发");
			}

			@Override
			public void onFinish() {
				btnVerificationCode.setText(R.string.verify_code);
				btnVerificationCode.setClickable(true);
			}
		};
	}
	
	@Override
	public String getTitle() {
		return "号码激活";
	}
	
	public String getPhoneNumder(){
		return phoneNumber.getText().toString().trim();
	}
	
	public String getVerificationCode(){
		return verificationCode.getText().toString().trim();
	}
	
	private String accessToken;
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_verification_code:
			sendVerifyCode();
			break;
		case R.id.activate_ok:
			activate();
			//toNextFragment("ecp_token");
			break;
		case R.id.ib_clear_text:
			phoneNumber.setText("");
			break;
		default:
			break;
		}
	}
	
	private void sendVerifyCode(){
		btnVerificationCode.setClickable(false);
		timer.start();
		Map<String,String> params = TCAsyncRequest.addAccessTokenParameters(getPhoneNumder());
		TCAsyncRequest.getAccessToken(params, this);
	}
	
	private void activate(){
		if(!TextUtils.isEmpty(getVerificationCode())&&!TextUtils.isEmpty(getPhoneNumder())){
			activateAccount(accessToken,getVerificationCode());
		}else{
			toast("手机号或者验证码为空");
		}
	}
	
	public void activateAccount(String accessToken,String verifyCode){
		if(accessToken!=null&&verifyCode!=null){
			Map<String, String> params = TCAsyncRequest.addActivateAccountParameters(accessToken, getPhoneNumder(),verifyCode);
			TCAsyncRequest.getVerificationCode(params, this);
		}else{
			toast("激活失败，请重新激活");
		}
		
	}

	@Override
	public void onResult(String accessToken) {
		this.accessToken = accessToken;
		activateAccount(accessToken,"");
	}

	public void toNextFragment(String ecp_token){
		FragmentBrige brige = (FragmentBrige) getActivity();
		TCActivatePromptFragment nextFragment = new TCActivatePromptFragment();
		presenter = new UserPresenter(nextFragment);
		presenter.addParams(getPhoneNumder(), ecp_token, accessToken);
		if(brige!=null){
			brige.toFragment(nextFragment);
		}
	}
	
	@Override
	public void onResult(TCActiveCodeResponse response) {
		if(response.getCode() == TCAsyncRequest.ACTIVATE_OK){
			if(!TextUtils.isEmpty(getVerificationCode())){
				if(!TextUtils.isEmpty(response.getEcp_token())){
					toNextFragment(response.getEcp_token());
				}else{
					toast("验证码已过期，请重新获取");
				}
			}else{
				toast("验证码已发到你手机");
			}
		}else{
			toast("激活失败，请重新激活");
			timer.cancel();
			btnVerificationCode.post(new Runnable() {
				
				@Override
				public void run() {
					
					btnVerificationCode.setText(R.string.verify_code);
					btnVerificationCode.setClickable(true);
				}
			});
		}
	}
	
	public void toast(String msg){
		FragmentActivity activity = getActivity();
		if(activity != null){
			Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if(KeyEvent.KEYCODE_ENTER == keyCode&&event.getAction() == KeyEvent.ACTION_DOWN){
			activate();
			return true;
		}
		return false;
	}
}
