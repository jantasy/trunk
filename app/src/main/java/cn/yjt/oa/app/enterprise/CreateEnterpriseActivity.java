package cn.yjt.oa.app.enterprise;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.Cancelable;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.MainActivity;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.CustInfo;
import cn.yjt.oa.app.beans.CustRegisterInfo;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.AsyncRequest.Builder;

public class CreateEnterpriseActivity extends TitleFragmentActivity implements OnClickListener {
	private EditText enterpriseName;
	private TextView enterpriseNameAttention;
	private ListView enterprisesList;
	private TextView emptyView;
	private LinearLayout enterpriseListLayout;
	private RelativeLayout progress;
	private Button createEnterpriseBtn;
	private String name;
	private int from;
	private static final int MAX = 5;
	protected static final String TAG = "CreateEnterpriseActivity";
	private Cancelable enterpriseListTask;
	private List<CustInfo> mEnterpriseList;
	private List<CustInfo> mOPenEnterpriseList= new ArrayList<CustInfo>();
	private EnterpriseAdapter mAdapter;
	private String startActivity;
	private ProgressDialog progressDialog;
	

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.create_enterprise);
		initView();
		getLeftbutton().setImageResource(R.drawable.navigation_back);
	}

	private void initView() {
		startActivity=getIntent().getStringExtra("start_activity");
		enterpriseName = (EditText) findViewById(R.id.enterprise_name);
		enterpriseNameAttention = (TextView) findViewById(R.id.enterprise_name_attention);
		enterpriseListLayout = (LinearLayout) findViewById(R.id.enterprise_list_layout);
		progress = (RelativeLayout) findViewById(R.id.progress_bar);
		enterprisesList = (ListView) findViewById(R.id.enterprises_list);
		createEnterpriseBtn=(Button) findViewById(R.id.create_enterprise);
		createEnterpriseBtn.setOnClickListener(this);
		enterprisesList.setVisibility(View.GONE);
		enterpriseListLayout.setVisibility(View.GONE);
		progress.setVisibility(View.GONE);
		enterpriseNameAttention.setVisibility(View.VISIBLE);
		mAdapter=new EnterpriseAdapter(this);
		emptyView = (TextView) findViewById(R.id.empty_view);
		enterprisesList.setEmptyView(emptyView);
		enterprisesList.setAdapter(mAdapter);
		enterpriseName.addTextChangedListener(new TextWatcher() {

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
				name = s.toString().trim();
				if (enterpriseListTask != null) {
					enterpriseListTask.cancel();
					enterpriseListTask = null;
				}
				
				getEnterpriseList();

			}
		});
		
		enterprisesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CustInfo custInfo = mOPenEnterpriseList.get(position);
				EnterpriseDetailActivity.launch(CreateEnterpriseActivity.this, custInfo);
			}
		});

	}

//	@Override
//	public void onBackPressed() {
//		if(startActivity!=null && "MainActivity".equals(startActivity)){
//			super.onBackPressed();
//		}
//		
//	}
	
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	private void getEnterpriseList() {
		progress.setVisibility(View.VISIBLE);
		enterpriseListLayout.setVisibility(View.GONE);
		enterpriseNameAttention.setVisibility(View.INVISIBLE);
		Builder builder = new Builder();
		builder.setModule(AsyncRequest.MODULE_CUSTS_BYNAME);
		builder.addQueryParameter("name", name);
		builder.addPageQueryParameter(from, MAX);
		Type type = new TypeToken<Response<ListSlice<CustInfo>>>() {
		}.getType();
		builder.setResponseType(type);
		builder.setResponseListener(new Listener<Response<ListSlice<CustInfo>>>() {

			@Override
			public void onErrorResponse(InvocationError arg0) {
				progress.setVisibility(View.GONE);
				enterpriseListLayout.setVisibility(View.VISIBLE);
			}

			@Override
			public void onResponse(Response<ListSlice<CustInfo>> response) {
				if (response.getCode() == 0) {
					progress.setVisibility(View.GONE);
					enterpriseListLayout.setVisibility(View.VISIBLE);
					if(mEnterpriseList!=null && mEnterpriseList.size()>0){
						mEnterpriseList.clear();
					}
					if(response.getPayload()!=null){
						mEnterpriseList=response.getPayload().getContent();
					}
					
					if(mOPenEnterpriseList.size()>0){
						mOPenEnterpriseList.clear();
					}
					
					if(mEnterpriseList.size()>0){
						int i = 0;
						for (CustInfo custInfo : mEnterpriseList) {
							if(custInfo.getIsOpen()==1){
								mOPenEnterpriseList.add(custInfo);
								i++;
							}
							if (i == 5) {
								break;
							}
						}
						mAdapter.setEnterpriseList(mOPenEnterpriseList);
						mAdapter.notifyDataSetChanged();
					}else{
						mEnterpriseList=Collections.emptyList();
						mAdapter.setEnterpriseList(mOPenEnterpriseList);
						mAdapter.notifyDataSetChanged();
					}
				}else{
					Toast.makeText(getApplicationContext(),
							response.getDescription(),
							Toast.LENGTH_LONG).show();
				}

			}

		

		});
		enterpriseListTask = builder.build().get();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.create_enterprise:

            /*记录操作 0201*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_CENTER_ENTERPRISE);

			if(!TextUtils.isEmpty(name)){
				if(mEnterpriseList!=null && mEnterpriseList.size()>0){
					for (int i = 0; i < mEnterpriseList.size(); i++) {
						if(mEnterpriseList.get(i).getName().equals(name)){
							Toast.makeText(this, "该企业名称已被注册", Toast.LENGTH_SHORT).show();
							return;
						}
					}
				}
//				InfoBasicInputUI.startActivityForRegister(this, name);
				
				//Enterprise name is enough
				registerEnterprise();
			}else{
				Toast.makeText(this, "企业名称不能为空", Toast.LENGTH_SHORT).show();
			}
			
			break;

		default:
			break;
		}
		
	}
	
	private void registerEnterprise() {
		Type type_enterprise = new TypeToken<Response<CustRegisterInfo>>(){}.getType();
		CustRegisterInfo info = new CustRegisterInfo();
		info.setName(name);
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_CUSTS)
		.setResponseType(type_enterprise).setRequestBody(info)
		.setResponseListener(new Listener<Response<CustRegisterInfo>>() {

			@Override
			public void onErrorResponse(InvocationError arg0) {
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "提交失败", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onResponse(Response<CustRegisterInfo> response) {
				progressDialog.dismiss();
				if (response.getCode() == 0) {
					directToResultPage(response);
				} else{
					Toast.makeText(getApplicationContext(), response.getDescription(), Toast.LENGTH_SHORT).show();
				}
			}
		});		
		builder.build().post();
		progressDialog = ProgressDialog.show(this, null, "正在提交申请...");
	}
	
	private void directToResultPage(Response<CustRegisterInfo> response){
//		Intent intent = new Intent(this, InfoCommitFinishUI.class);
//		intent.putExtra(CommitFragment.EXTRA_ENTERPRISE_NAME, response.getPayload().getName());
//		startActivity(intent);
//		finish();
		Toast.makeText(getApplicationContext(), "企业创建成功", Toast.LENGTH_SHORT).show();
		MainActivity.launchClearTask(this);
	}

	public static void launch(Context context){
		Intent intent = new Intent(context, CreateEnterpriseActivity.class);
		context.startActivity(intent );
	}

}
