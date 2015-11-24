package cn.yjt.oa.app.enterprise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.CustJoinInviteInfo;
import cn.yjt.oa.app.beans.MessageInfo;
import cn.yjt.oa.app.component.TitleFragmentActivity;

import com.google.gson.Gson;

public class CustJoinInviteHandleActivity extends TitleFragmentActivity {
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		
		Fragment handleFragment = new CustJoinInviteHandleFragment();
		handleFragment.setArguments(getIntent().getExtras());
		getSupportFragmentManager().beginTransaction().add(R.id.content, handleFragment).commit();
		getLeftbutton().setImageResource(R.drawable.navigation_back);
	}
	
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}
	
	public static void launchWithInviteInfo(Context context,CustJoinInviteInfo inviteInfo){
		Intent intent = new Intent(context, CustJoinInviteHandleActivity.class);
		intent.putExtra("inviteInfo", inviteInfo);
		context.startActivity(intent);
	}
	
	public static void launchWithInviteInfo(Fragment fragment,CustJoinInviteInfo inviteInfo,int requestCode){
		Intent intent = new Intent(fragment.getActivity(), CustJoinInviteHandleActivity.class);
		intent.putExtra("inviteInfo", inviteInfo);
		fragment.startActivityForResult(intent, requestCode);
	}
	
	public static void launchWithMessageInfo(Fragment	fragment, MessageInfo messageInfo, int requestCode) {
		CustJoinInviteInfo inviteInfo = new Gson().fromJson(messageInfo.getPayload(), CustJoinInviteInfo.class);
		launchWithInviteInfo(fragment, inviteInfo,requestCode);
	}
}
