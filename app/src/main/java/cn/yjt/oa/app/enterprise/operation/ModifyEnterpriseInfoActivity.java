package cn.yjt.oa.app.enterprise.operation;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.CustInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.AsyncRequest.Builder;
import cn.yjt.oa.app.http.BusinessConstants;

public class ModifyEnterpriseInfoActivity extends TitleFragmentActivity
		implements OnClickListener {
	private TextView enterpriseName;
	private TextView enterpriseShortName;
	private TextView enterpriseId;
	private TextView adress;
	private EditText shortName;
	private EditText email;
	private EditText telephone;
	private RadioGroup isOpen;
	private RadioButton open;
	private RadioButton notOpen;
	
	private Button saveEnterpriseInfo;
	private CustInfo custInfo;
	private boolean needCheck = false;
	private int whetherOpen = 1;
	private ProgressDialog dialog;
	
	private String lastShortName;
	private long custId;
	private TextView createTime;
	private TextView creater;
	private TextView membersNumber;
	private TextView isReview;
	private boolean notModify;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		notModify = getIntent().getBooleanExtra("notModify", false);
		custId = getIntent().getLongExtra("custId", Long.valueOf(AccountManager.getCurrent(getApplicationContext()).getCustId()));
		if(notModify){
			setContentView(R.layout.enterprise_list_detail);
			setTitle("企业详情");
			enterpriseName=(TextView) findViewById(R.id.enterprise_name);
			enterpriseShortName=(TextView) findViewById(R.id.enterprise_shortname);
			enterpriseId=(TextView) findViewById(R.id.enterprise_id);
			createTime = (TextView) findViewById(R.id.create_time);
			creater=(TextView) findViewById(R.id.creater);
			membersNumber=(TextView) findViewById(R.id.members_number);
			adress=(TextView) findViewById(R.id.adress);
			isReview=(TextView) findViewById(R.id.is_review);
		}else{
			setContentView(R.layout.modify_enterprise_info);
			initView();
		}
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		loadEnterpriseInfo();
	}

	private void initView() {
		enterpriseName = (TextView) findViewById(R.id.enterprise_name);
		adress = (EditText) findViewById(R.id.adress);
		shortName = (EditText) findViewById(R.id.short_name);
		email = (EditText) findViewById(R.id.email);
		telephone = (EditText) findViewById(R.id.telephone);
		isOpen = (RadioGroup) findViewById(R.id.is_open);
		open = (RadioButton) findViewById(R.id.open);
		notOpen = (RadioButton) findViewById(R.id.not_open);
		isOpen.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.open:
					whetherOpen = 1;
					break;
				case R.id.not_open:
					whetherOpen = 0;
					break;

				default:
					break;
				}

			}
		});
		saveEnterpriseInfo = (Button) findViewById(R.id.save_enterprise_info);
		saveEnterpriseInfo.setOnClickListener(this);
	}
	
	private void setDetail(CustInfo custInfo){
		enterpriseName.setText(custInfo.getName());
		enterpriseShortName.setText(custInfo.getShortName());
		enterpriseId.setText(custInfo.getUniqueId()+"");
		try {
			Date date = BusinessConstants.parseTime(custInfo.getCreateTime());
			createTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date));
		} catch (Exception e) {
			e.printStackTrace();
			createTime.setText(custInfo.getCreateTime());
		}
		creater.setText(custInfo.getCreateUserName());
		membersNumber.setText(custInfo.getUserCount()+"");
		adress.setText(custInfo.getAddress());
		if(custInfo.getIsNeedCheck()){
			isReview.setText("是");
		}else{
			isReview.setText("否");
		}
	}

	private void loadEnterpriseInfo() {
		if(dialog==null){
			dialog=new ProgressDialog(this);
		}
		dialog.setMessage(getString(R.string.enterprise_loading));
		dialog.show();
		Builder builder = new Builder();
		builder.setModule(String.format(AsyncRequest.MODULE_CUSTS_ID_BASEINFO,
				custId));
		Type type = new TypeToken<Response<CustInfo>>() {
		}.getType();
		builder.setResponseType(type);
		builder.setResponseListener(new Listener<Response<CustInfo>>() {

			@Override
			public void onErrorResponse(InvocationError arg0) {
				dismissDialog();
			}

			@Override
			public void onResponse(Response<CustInfo> response) {
				dismissDialog();
				if (response.getCode() == 0) {
					custInfo = response.getPayload();
					if(notModify){
						setDetail(custInfo);
					}else{
						initData(custInfo);
					}
				}

			}

		});
		builder.build().get();

	}

	private void initData(CustInfo custInfo) {
		if (custInfo != null) {
			enterpriseName.setText(custInfo.getName());
			adress.setText(custInfo.getAddress());
			shortName.setText(custInfo.getShortName());
			email.setText(custInfo.getEmail());
			telephone.setText(custInfo.getTelephone());
			whetherOpen=custInfo.getIsOpen();
			if(whetherOpen==1){
				open.setChecked(true);
			}else{
				notOpen.setChecked(true);
			}
			
			lastShortName = custInfo.getShortName();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save_enterprise_info:
			saveEnterpriseInfo();
			break;

		default:
			break;
		}

	}
	
	
	private void saveEnterpriseInfo(){
		if(dialog==null){
			dialog=new ProgressDialog(this);
		}
		dialog.setMessage(getString(R.string.enterprise_update));
		dialog.show();
		custInfo.setAddress(adress.getText().toString().trim());
		custInfo.setShortName(shortName.getText().toString().trim());
		custInfo.setEmail(email.getText().toString().trim());
		custInfo.setTelephone(telephone.getText().toString());
		custInfo.setIsOpen(whetherOpen);
//		custInfo.setIsNeedCheck(needCheck);
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		Type type = new TypeToken<Response<CustInfo>>() {
		}.getType();
		builder.setModule(String.format(AsyncRequest.MODULE_CUSTS_ID_BASEINFO, custInfo.getId()))
		.setResponseType(type)
		.setRequestBody(custInfo)
		.setResponseListener(new Listener<Response<CustInfo>>() {

			@Override
			public void onErrorResponse(InvocationError arg0) {
				dismissDialog();
				Toast.makeText(ModifyEnterpriseInfoActivity.this, "修改企业信息失败", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onResponse(Response<CustInfo> arg0) {
				dismissDialog();
				if(arg0.getCode()==0){
					Toast.makeText(ModifyEnterpriseInfoActivity.this, "修改企业信息成功", Toast.LENGTH_SHORT).show();
					onBackPressed();
					CustInfo payload = arg0.getPayload();
					if(payload != null){
						if(lastShortName == null && payload.getShortName() != null){
							AccountManager.getCurrent(getApplicationContext()).setCustShortName(payload.getShortName() );
							MainApplication.sendChangedCustShortNameBroadcast(getApplicationContext());
						}else if(lastShortName != null && !lastShortName.equals(payload.getShortName())){
							AccountManager.getCurrent(getApplicationContext()).setCustShortName(payload.getShortName());
							MainApplication.sendChangedCustShortNameBroadcast(getApplicationContext());
						}
					}
				}
				
			}
		});
		builder.build().put();
		
	}

	@Override
	public void onLeftButtonClick() {
		onBackPressed();
	}
	private void dismissDialog(){
		if(dialog!=null && dialog.isShowing()){
			dialog.dismiss();
		}
	}

	public static void launch(Context context) {
		Intent intent = new Intent(context, ModifyEnterpriseInfoActivity.class);
		context.startActivity(intent);
	}
	
	public static void launchWithNotModify(Context context,long custId){
		Intent intent = new Intent(context, ModifyEnterpriseInfoActivity.class);
		intent.putExtra("notModify", true);
		intent.putExtra("custId", custId);
		context.startActivity(intent);
	}

}
