package cn.yjt.oa.app.industry;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.IndustryTagGroupInfo;
import cn.yjt.oa.app.beans.IndustryTagInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.AsyncRequest.Builder;

public class SettingIndustryActivity extends TitleFragmentActivity {
	private ExpandableListView industryListview;
	private IndustryAdapter industryAdapter;
	private List<IndustryTagGroupInfo> industryTagGroupInfoList;
	private ProgressDialog progress;
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_setting_industry);
		initView();
		loadData();
		
	}

	private void initView() {
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		getRightButton().setImageResource(R.drawable.contact_list_save);
		industryListview=(ExpandableListView) findViewById(R.id.industry_listview);
		//去掉箭头图标
		industryListview.setGroupIndicator(null);  
		industryAdapter=new IndustryAdapter(this);
		industryListview.setAdapter(industryAdapter);
	}
	
	private void loadData() {
		showProgressDialog("加载行业标签");
		AsyncRequest.Builder buider=new Builder();
		buider.setModule(AsyncRequest.MODULE_INDUSTRYTAGS);
		Type responseType =new TypeToken<Response<List<IndustryTagGroupInfo>>>() {
		}.getType();
		buider.setResponseType(responseType);
		buider.setResponseListener(new Listener<Response<List<IndustryTagGroupInfo>>>() {

			@Override
			public void onErrorResponse(InvocationError arg0) {
				dismissDialog();
				
			}

			@Override
			public void onResponse(Response<List<IndustryTagGroupInfo>> arg0) {
				dismissDialog();
				if(arg0.getCode()==0){
					industryTagGroupInfoList=arg0.getPayload();
					industryAdapter.setIndustryTagGroupList(industryTagGroupInfoList);
					industryAdapter.notifyDataSetChanged();
					for (int i = 0; i <industryTagGroupInfoList.size(); i++) {
						industryListview.expandGroup(i);
					}
				}else{
					Toast.makeText(getApplicationContext(),
							arg0.getDescription(),
							Toast.LENGTH_LONG).show();
				}
				
				
			}
		});
		buider.build().get();

	}
	
	private void showProgressDialog(String msg) {
		if(progress==null){
			progress=new ProgressDialog(this);
		}
		progress.setMessage(msg);
		if(!progress.isShowing()){
			progress.show();
		}
		
	}
	private void dismissDialog() {
		if(progress!=null && progress.isShowing()){
			progress.dismiss();
		}
		
	}

	@Override
	public void onLeftButtonClick() {
//		super.onLeftButtonClick();
		super.onBackPressed();
	}

	@Override
	public void onRightButtonClick() {
		super.onRightButtonClick();
		updateData();
		
	}
	
	private List<IndustryTagInfo> getSelctedIndustryTag(){
		List<IndustryTagInfo> selectedIndustryTags=new ArrayList<IndustryTagInfo>();
		List<IndustryTagGroupInfo> groupList = industryAdapter.getIndustryTagGroupList();
		for (int i = 0; i < groupList.size(); i++) {
			List<IndustryTagInfo> industryTags = groupList.get(i).getIndustryTags();
			for (int j = 0; j < industryTags.size(); j++) {
				if(industryTags.get(j).getIsUsed()==1){
					selectedIndustryTags.add(industryTags.get(j));
				}
			}
		}
		return selectedIndustryTags;
	}
	
	private void updateData() {
		
		showProgressDialog("修改行业标签");
		AsyncRequest.Builder buider=new Builder();
		buider.setModule(AsyncRequest.MODULE_INDUSTRYTAGS);
		buider.setRequestBody(getSelctedIndustryTag());
		Type responseType =new TypeToken<Response<String>>() {
		}.getType();
		buider.setResponseType(responseType);
		buider.setResponseListener(new Listener<Response<String>>() {

			@Override
			public void onErrorResponse(InvocationError arg0) {
				dismissDialog();
				
			}

			@Override
			public void onResponse(Response<String> arg0) {
				dismissDialog();
				if(arg0.getCode()==0){
					toast("行业标签修改成功");
					setResult(RESULT_OK);
					finish();
				}else{
					toast("行业标签修改失败，请重新提交");
				}
			}
		});
		buider.build().put();

	}
	private void toast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		
	}
	public static void launch(Context context) {
		Intent intent=new Intent(context,SettingIndustryActivity.class);
		context.startActivity(intent);
	}
	
	public static void launchForResult(Activity context ,int requestCode){
		Intent intent=new Intent(context,SettingIndustryActivity.class);
		context.startActivityForResult(intent, requestCode);
	}

}
