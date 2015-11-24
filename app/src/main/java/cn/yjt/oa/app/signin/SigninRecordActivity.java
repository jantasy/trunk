package cn.yjt.oa.app.signin;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.SigninInfo;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.signin.adapter.SigninRecordAdapter;
import cn.yjt.oa.app.widget.listview.OnRefreshListener;
import cn.yjt.oa.app.widget.listview.PullToRefreshListView;

public class SigninRecordActivity extends TitleFragmentActivity {
	private static final Type TYPE_SIGNIN_RECORD = new TypeToken<Response<ListSlice<SigninInfo>>>(){}.getType();
	private PullToRefreshListView signinRecordList;
	private SigninRecordAdapter mAdapter;
	
	private SigninRecordCallback callBack;
	
	private LinearLayout progress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signin_record);
		initView();
		callBack = new SigninRecordCallback() {
			
			@Override
			public void onSigninRecordResponse(List<SigninInfo> list) {
				if(list != null){
					mAdapter = new SigninRecordAdapter(SigninRecordActivity.this);
					updateAdapter(list);
					signinRecordList.setAdapter(mAdapter);
					mAdapter.notifyDataSetChanged();
					signinRecordList.onRefreshComplete();
				}
			}
		};
		getData(new SigninRecordCallback() {
			
			@Override
			public void onSigninRecordResponse(List<SigninInfo> list) {
				if(list != null){
					mAdapter = new SigninRecordAdapter(SigninRecordActivity.this);
					updateAdapter(list);
					signinRecordList.setAdapter(mAdapter);
				}
			}
		});
	}
	
	private void updateAdapter(List<SigninInfo> list) {
		if (list != null) {
			mAdapter.clear();
			mAdapter.addEntries(list);
			mAdapter.notifyDataSetChanged();
		}
	}
	
	public void initView(){
		progress = (LinearLayout) findViewById(R.id.signin_progress);
		
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		getRightButton().setImageResource(R.drawable.icon_search);
		
		signinRecordList = (PullToRefreshListView) findViewById(R.id.signin_record_list);
//		signinRecordList.addHeaderView(LayoutInflater.from(this).inflate(R.layout.signin_record_item, null,true));
//		signinRecordList.setHeaderDividersEnabled(true);
		signinRecordList.enableFooterView(false);
		signinRecordList.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				getData(callBack);
			}
		});
	}
	public void getData(final SigninRecordCallback callback){
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_SINGNINS)
		       .setResponseType(TYPE_SIGNIN_RECORD)
		       .addPageQueryParameter(0, 10)
				.setResponseListener(
						new Listener<Response<ListSlice<SigninInfo>>>() {

							@Override
							public void onErrorResponse(InvocationError error) {
								// TODO 数据获取失败处理
								progress.setVisibility(View.GONE);
								Toast.makeText(
										SigninRecordActivity.this,
										getResources().getString(
												R.string.load_fail),
										Toast.LENGTH_SHORT).show();
							}

							@Override
							public void onResponse(
									Response<ListSlice<SigninInfo>> response) {
								progress.setVisibility(View.GONE);
								if(response.getCode()==0){
									callback.onSigninRecordResponse(response
											.getPayload().getContent());
									
								}else{
									Toast.makeText(getApplicationContext(),
											response.getDescription(),
											Toast.LENGTH_LONG).show();
								}
							}
						})
		       .build()
		       .get();
		
	}
	public interface SigninRecordCallback{
		public void onSigninRecordResponse(List<SigninInfo> list);
	}
	
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}
	
	@Override
	public void onRightButtonClick() {
		super.onRightButtonClick();
		if (progress.getVisibility() == View.GONE) {
			progress.setVisibility(View.VISIBLE);
		}
		getData(callBack);
	}
	
	// 启动签到查询页面
	public static void startSigninActivity(Context context) {
		Intent intent = new Intent(context, SigninRecordActivity.class);
		context.startActivity(intent);
	}
	
}
