package cn.yjt.oa.app.enterprise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.ApplyInfo;
import cn.yjt.oa.app.beans.MessageInfo;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.component.TitleFragmentActivity;

import com.google.gson.Gson;

public class JoinCustHandleActivity extends TitleFragmentActivity {

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		ApplyInfo applyInfo = getIntent().getParcelableExtra("applyinfo");
		UserInfo userInfo = AccountManager.getCurrent(getApplicationContext());
		
		Fragment handleFragment;
		if(userInfo.getPhone().equals(applyInfo.getPhone())){
			handleFragment = new ApplicantHandleFragment();
		}else{
			handleFragment = new AdministratorHandleFragment();
		}
		handleFragment.setArguments(getIntent().getExtras());
		getSupportFragmentManager().beginTransaction().add(R.id.content, handleFragment).commit();
		getLeftbutton().setImageResource(R.drawable.navigation_back);

	}
	
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	public static void launchWithMessageInfo(Context context, MessageInfo messageInfo) {
		ApplyInfo applyInfo = new Gson().fromJson(messageInfo.getPayload(), ApplyInfo.class);
		applyInfo.setTitle(messageInfo.getTitle());
		applyInfo.setContent(messageInfo.getContent());
		launchWithApplyInfo(context, applyInfo);
	}
	
	public static void launchWithApplyInfo(Context context,ApplyInfo applyInfo){
		Intent intent = new Intent(context, JoinCustHandleActivity.class);
		intent.putExtra("applyinfo", applyInfo);
		context.startActivity(intent );
		
	}
	public static void launchWithMessageInfo(Fragment	fragment, MessageInfo messageInfo, int requestCode) {
		ApplyInfo applyInfo = new Gson().fromJson(messageInfo.getPayload(), ApplyInfo.class);
		applyInfo.setTitle(messageInfo.getTitle());
		applyInfo.setContent(messageInfo.getContent());
		launchWithApplyInfo(fragment, applyInfo,requestCode);
	}
	
	public static void launchWithApplyInfo(Fragment fragment,ApplyInfo applyInfo, int requestCode){
		Intent intent = new Intent(fragment.getActivity(), JoinCustHandleActivity.class);
		intent.putExtra("applyinfo", applyInfo);
		fragment.startActivityForResult(intent, requestCode);
		
	}
	

}
