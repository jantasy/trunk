package cn.yjt.oa.app.contactlist;

import io.luobo.common.Cancelable;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.CustJoinInfo;
import cn.yjt.oa.app.beans.InviteUserInfo;
import cn.yjt.oa.app.beans.MessageInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.BackTitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class InviteUserHandleActivity extends BackTitleFragmentActivity implements
		OnClickListener, Listener<Response<CustJoinInfo>> {

	private TextView message;
	private TextView title;
	private InviteUserInfo inviteInfo;
	private ProgressDialog progressDialog;
	private Cancelable cancelable;
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_invite_user_handle);
		Button refuseBtn = (Button)  findViewById(R.id.invite_user_button_left);
		refuseBtn.setOnClickListener(this);
		Button handledBtn = (Button)  findViewById(R.id.invite_user_button_middle);
		handledBtn.setOnClickListener(this);
		Button applyBtn = (Button)  findViewById(R.id.invite_user_button_right);
		applyBtn.setOnClickListener(this);
		message = (TextView)  findViewById(R.id.invite_user_content);
		title = (TextView)  findViewById(R.id.invite_user_title);

		inviteInfo = getIntent().getParcelableExtra("inviteUserInfo");

		title.setText(inviteInfo.getTitle());
		message.setText(inviteInfo.getContent());
		if(AccountManager.getCurrent(this).getId()==inviteInfo.getInviteUserId()){
			handledBtn.setVisibility(View.VISIBLE);
			handledBtn.setText("确定");
			handledBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finish();
					
				}
			});
			refuseBtn.setVisibility(View.GONE);
			applyBtn.setVisibility(View.GONE);
			 findViewById(R.id.tv_line).setVisibility(View.GONE);
		}else{
			switchButtonText(inviteInfo.getHandleStatus(), handledBtn,
					 findViewById(R.id.tv_line), refuseBtn, applyBtn);
		}


	}



	private void switchButtonText(int status, Button button, View line,
			Button leftButton, Button rightButton) {

		switch (status) {
		case 0:
			button.setVisibility(View.GONE);
			leftButton.setVisibility(View.VISIBLE);
			rightButton.setVisibility(View.VISIBLE);
			line.setVisibility(View.VISIBLE);
			break;
		case 1:
			button.setVisibility(View.VISIBLE);
			button.setText("您已同意此邀请");
			leftButton.setVisibility(View.GONE);
			rightButton.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
			break;
		case -1:
			button.setVisibility(View.VISIBLE);
			button.setText("您已拒绝此邀请");
			leftButton.setVisibility(View.GONE);
			rightButton.setVisibility(View.GONE);
			line.setVisibility(View.GONE);
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.invite_user_button_left:
			handle("-1");
			break;
		case R.id.invite_user_button_middle:
			
			break;
		case R.id.invite_user_button_right:
			handle("1");
			break;

		default:
			break;
		}
	}

	private void handle(String operation) {
		new TypeToken<CustJoinInfo>() {
		}.getType();
		cancelable = new AsyncRequest.Builder()
				.setModule(String.format(AsyncRequest.MODULE_INVITEUSER_INVITEID, inviteInfo.getId()))
				.addQueryParameter("operation", operation)
				.setResponseType(new TypeToken<Response<InviteUserInfo>>() {
				}.getType()).setResponseListener(this).build().put();
		progressDialog = ProgressDialog.show(this, null, "正在提交...");
	}

	private void dismissDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	private void toast(String message) {
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onErrorResponse(InvocationError arg0) {
		dismissDialog();
		toast("提交失败。");
	}

	@Override
	public void onDestroy() {
		if(cancelable != null){cancelable.cancel();}
		super.onDestroy();
	}

	@Override
	public void onResponse(Response<CustJoinInfo> resp) {
		dismissDialog();
		if (resp.getCode() == 0) {
			toast("提交成功。");
				finish();
		} else if(resp.getCode() == 2){
			toast(resp.getDescription());
				finish();
		}else{
			toast(resp.getDescription());
		}
	}
	
	
	
	public static void launch(Context context,InviteUserInfo inviteInfo){
		Intent intent = new Intent(context, InviteUserHandleActivity.class);
		intent.putExtra("inviteUserInfo", inviteInfo);
		context.startActivity(intent);
	}
	
	public static void launchWithInviteUserInfo(Fragment fragment,InviteUserInfo inviteInfo,int requestCode){
		Intent intent = new Intent(fragment.getActivity(), InviteUserHandleActivity.class);
		intent.putExtra("inviteUserInfo", inviteInfo);
		fragment.startActivityForResult(intent, requestCode);
	}
	
	public static void launchWithMessageInfo(Fragment	fragment, MessageInfo messageInfo, int requestCode) {
		InviteUserInfo inviteInfo = new Gson().fromJson(messageInfo.getPayload(), InviteUserInfo.class);
		launchWithInviteUserInfo(fragment, inviteInfo,requestCode);
	}

}
