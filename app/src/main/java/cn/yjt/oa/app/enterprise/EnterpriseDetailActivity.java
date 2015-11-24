package cn.yjt.oa.app.enterprise;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

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
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.CustInfo;
import cn.yjt.oa.app.beans.CustJoinInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.BusinessConstants;

public class EnterpriseDetailActivity extends TitleFragmentActivity implements OnClickListener{
	protected static final String TAG = "EnterpriseDetailActivity";
	private TextView enterpriseName;
	private TextView createTime;
	private TextView creater;
	private TextView membersNumber;
	private TextView adress;
	private TextView isReview;
	private EditText joinReasons;
	private Button addEnterprise;
	private CustInfo custInfo;
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.enterprise_detail);
		initView();
		loadData();
	}

	private void loadData() {
		custInfo=(CustInfo) getIntent().getParcelableExtra("CustInfo");
		if(custInfo!=null){
			enterpriseName.setText(custInfo.getName());
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
	}

	private void initView() {
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		enterpriseName=(TextView) findViewById(R.id.enterprise_name);
		createTime=(TextView) findViewById(R.id.create_time);
		creater=(TextView) findViewById(R.id.creater);
		membersNumber=(TextView) findViewById(R.id.members_number);
		adress=(TextView) findViewById(R.id.adress);
		isReview=(TextView) findViewById(R.id.is_review);
		joinReasons=(EditText) findViewById(R.id.join_reasons);
		addEnterprise=(Button) findViewById(R.id.add_enterprise);
		addEnterprise.setOnClickListener(this);
	}
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_enterprise:

            /*记录操作 0202*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_ENTERPRISE);

			if(joinReasons.getText().toString().trim().isEmpty()){
				Toast.makeText(this, "申请加入企业的理由不能为空", Toast.LENGTH_SHORT).show();
			}else{
				applicationJoinEnterprise();
			}
			
			break;

		default:
			break;
		}
		
	}
	
	
	private void applicationJoinEnterprise(){
		if (progressDialog == null) {
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("正在申请加入企业...");
			progressDialog.show();
		}else{
			progressDialog.setMessage("正在申请加入企业...");
		}
		AsyncRequest.Builder requestBuilder = new AsyncRequest.Builder();
		requestBuilder.setModule(AsyncRequest.MODULE_CUSTS_APPLIES);
		CustJoinInfo info = new CustJoinInfo();
		info.setCustId(custInfo.getId());
		info.setMessage(joinReasons.getText().toString().trim());
		requestBuilder.setRequestBody(info);
		requestBuilder.setResponseType(new TypeToken<Response<CustJoinInfo>>() {
		}.getType());
		requestBuilder.setResponseListener(new Listener<Response<CustJoinInfo>>() {

			@Override
			public void onErrorResponse(InvocationError arg0) {
				dismissProgressDialog();
				Toast.makeText(getApplicationContext(), "申请加入企业失败", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onResponse(Response<CustJoinInfo> arg0) {
				dismissProgressDialog();
				if(arg0.getCode()==0){
					JoinEnterpriseSuccessActivity.launch(EnterpriseDetailActivity.this, custInfo);
					onBackPressed();
				}else{
					Toast.makeText(getApplicationContext(),
							arg0.getDescription(),
							Toast.LENGTH_LONG).show();
				}
				
			}

			
		});
		requestBuilder.build().post();
	}
	
	private void dismissProgressDialog() {
		if(progressDialog!=null && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
	}
	
	
	public static void launch(Context context,CustInfo custInfo){
		Intent intent=new Intent(context, EnterpriseDetailActivity.class);
		intent.putExtra("CustInfo", custInfo);
		context.startActivity(intent);
	}

}
