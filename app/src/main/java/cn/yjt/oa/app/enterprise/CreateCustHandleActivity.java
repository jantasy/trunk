package cn.yjt.oa.app.enterprise;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.CustRegisterInfo;
import cn.yjt.oa.app.beans.MessageInfo;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.enterprise.operation.EnterpriseManageActivity;
import cn.yjt.oa.app.utils.OperaEventUtils;

import com.google.gson.Gson;

public class CreateCustHandleActivity extends TitleFragmentActivity  {


	private CustRegisterInfo custRegisterInfo;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_create_cust_handle);
		Button handleBtn = (Button) findViewById(R.id.create_cust_handle_btn);
		TextView title = (TextView) findViewById(R.id.create_cust_handle_title);
		TextView message = (TextView) findViewById(R.id.create_cust_handle_message);
		ImageView icon = (ImageView) findViewById(R.id.create_cust_handle_icon);
		ImageView tip = (ImageView) findViewById(R.id.create_cust_handle_icon_tip);
		custRegisterInfo = getIntent().getParcelableExtra("custregisterinfo");
		title.setText(custRegisterInfo.getTitle());
		message.setText(custRegisterInfo.getContent());
		title.setText(custRegisterInfo.getTitle());
		message.setText(custRegisterInfo.getContent());
		switchCheckStatus(handleBtn,custRegisterInfo.getCheckStatus());
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		switchStatus(icon, tip, custRegisterInfo.getCheckStatus());

	}
	
	private void switchStatus(ImageView icon,ImageView tip,int status){
		switch (status) {
		case 0:
			icon.setImageBitmap(null);
			tip.setImageBitmap(null);
			break;
		case 1:
			icon.setImageResource(R.drawable.ic_apply_accept);
			tip.setImageResource(R.drawable.ic_apply_accept_tip);
			break;
		case -1:
			icon.setImageResource(R.drawable.ic_apply_refuse);
			tip.setImageResource(R.drawable.ic_apply_refuse_tip);
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	private void switchCheckStatus(Button handleBtn, final int status) {
		switch (status) {
		case 0:
			handleBtn.setText("知道了");
			break;
		case 1:
			if(custRegisterInfo.getCustId()==Long.parseLong(AccountManager.getCurrent(this).getCustId())){
				handleBtn.setText("去管理企业");
			}else{
				handleBtn.setText("查看企业列表");
			}
			
			break;
		case -1:
			handleBtn.setText("查看我的申请");
			break;

		default:
			break;
		}
		handleBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switchClick(status);
			}
		});
	}

	public static void launchWithMessageInfo(Context context, MessageInfo info) {
		String payload = info.getPayload();
		CustRegisterInfo registerInfo = new Gson().fromJson(payload, CustRegisterInfo.class);
		registerInfo.setTitle(info.getTitle());
		registerInfo.setContent(info.getContent());
		launchWithCustRegisterInfo(context, registerInfo);
	}
	
	public static void launchWithCustRegisterInfo(Context context ,CustRegisterInfo info){
		Intent intent = new Intent(context, CreateCustHandleActivity.class);
		intent.putExtra("custregisterinfo", info);
		context.startActivity(intent);
	}
	
	public static void launchWithMessageInfo(Fragment	fragment, MessageInfo messageInfo, int requestCode) {
		CustRegisterInfo custRegisterInfo = new Gson().fromJson(messageInfo.getPayload(), CustRegisterInfo.class);
		custRegisterInfo.setTitle(messageInfo.getTitle());
		custRegisterInfo.setContent(messageInfo.getContent());
		launchWithCustRegisterInfo(fragment, custRegisterInfo,requestCode);
	}
	
	public static void launchWithCustRegisterInfo(Fragment fragment,CustRegisterInfo custRegisterInfo, int requestCode){
		Intent intent = new Intent(fragment.getActivity(), CreateCustHandleActivity.class);
		intent.putExtra("custregisterinfo", custRegisterInfo);
		fragment.startActivityForResult(intent, requestCode);
		
	}

	private void switchClick(int status){
		switch (status) {
		case 0:
			finish();
			break;
		case 1:
			if (custRegisterInfo.getCustId() == Long.parseLong(AccountManager.getCurrent(this).getCustId())) {
				/*记录操作 1305*/
				OperaEventUtils.recordOperation(OperaEvent.OPERA_GOTO_WATCH_ENTERPRISELISTS);

				Intent intent=new Intent(this, EnterpriseManageActivity.class);
				startActivity(intent);
				finish();
			} else {
				/*记录操作 1306*/
				OperaEventUtils.recordOperation(OperaEvent.OPERA_GOTO_MANAGE_ENTERPRISE);

				EnterpriseListActivity.launch(this);
				finish();
			}
		
			break;
		case -1:
			viewCustRegister();
			break;

		default:
			break;
		}
	}

	private void viewCustRegister() {
		InfoCommitUI.launchForEdit(this, true, custRegisterInfo);
	}
	
}
