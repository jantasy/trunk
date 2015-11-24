package cn.yjt.oa.app.teleconferencenew;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.component.TitleFragmentActivity;

public class CallListActivity extends TitleFragmentActivity {
	private static final String tag = "CallListActivity";
	
    private ProgressBar mProgress;
    private ExpandableListView mList;
    private ArrayList<String> mProList, mTimeList, mDuringList;
    private ArrayList<ArrayList<String>> mCityLists;
    private Handler mHandler;
    private CallListAdapter mAdapter;
    
    private UserInfo mCurrent;
	
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
			setContentView(R.layout.activity_call_list);
			getLeftbutton().setImageResource(R.drawable.navigation_back);			
			setTitle("通话明细");
			mProgress = (ProgressBar) findViewById(R.id.center_progress);
			mList = (ExpandableListView) findViewById(R.id.list_detail);
			mList.setGroupIndicator(null);
			
			mProList = new ArrayList();
			mTimeList = new ArrayList();
			mDuringList = new ArrayList();
			mCityLists = new ArrayList();
	        mHandler = new Handler();
	        
	        mCurrent = AccountManager.getCurrent(this);
	        getDataAndDisplay();
	}
	
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}
	
	private void getDataAndDisplay() {
		mProgress.setVisibility(View.VISIBLE);
		mList.setVisibility(View.GONE);
        new Thread() {
            public void run() {
                super.run();
    			String date = String.valueOf(System.currentTimeMillis());
//    			String date = "20150506110523423";
    			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
    			params.add(new BasicNameValuePair("command", "Command _ConferenceDetail"));
    			params.add(new BasicNameValuePair("telno", mCurrent.getPhone()));    			
    			params.add(new BasicNameValuePair("userid", getCustId()));
    			//test
//    			params.add(new BasicNameValuePair("userid", "01000000054002"));
    			params.add(new BasicNameValuePair("starttime", ""));
    			params.add(new BasicNameValuePair("endtime", ""));
    			params.add(new BasicNameValuePair("date", date));
    	    	DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
//    	    	HttpPost httpPost = new HttpPost("http://118.85.207.187:9080/hytconfgw/servlet/HttpServer");  
    	    	HttpPost httpPost = new HttpPost(Constants.MEETING_URL + "query_details");  
    	    	
    	        try {
    	            defaultHttpClient.getParams().setParameter("http.connection.timeout", Integer.valueOf(10000));
    	            defaultHttpClient.getParams().setParameter("http.socket.timeout", Integer.valueOf(10000));
    	            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
    	            HttpResponse execute = defaultHttpClient.execute(httpPost);
    	            Log.d(tag, "网络返回 状态码为200 :" + execute.getStatusLine().getStatusCode());
    	            final String result = execute.getStatusLine().getStatusCode() == 200 ? new String(EntityUtils.toByteArray(execute.getEntity()), "utf-8") : "netwrong";
    	            Log.d(tag, "result:" + result);
                    mHandler.post(new Runnable() {
                        public void run() {
							JSONObject jOResult;
							try {
								jOResult = new JSONObject(result);							
								if(TextUtils.equals(jOResult.getString("result"), "success")) {
									JSONArray detailList = jOResult.getJSONArray("details");
									for (int i = 0; i < detailList.length(); i++) {
										ArrayList telList = new ArrayList();
										JSONObject detail = detailList.getJSONObject(i);
										mProList.add(detail.getString("admintelno"));
										mTimeList.add(detail.getString("starttime"));
										mDuringList.add(detail.getString("usetime"));
										String jointelList = detail.getString("joinnum");
										Log.d(tag, "jointelList: " + jointelList);
										String[] jointelArray = jointelList.split("\\|");
										Log.d(tag, "jointelArray length: " + jointelArray.length);
				                        for (String jointel : jointelArray) {
				                        	Log.d(tag, "jointel: " + jointel);
				                        	telList.add(jointel);
				                        }
				                        mCityLists.add(telList);
									}
									
								} else if(TextUtils.equals(jOResult.getString("result"), "failed")) {
									String reason = jOResult.getString("reason");
									String reasonDetail;
									if(TextUtils.equals(reason, "1")) {
										reasonDetail = "企业ID非法";
									} else if(TextUtils.equals(reason, "2")) {
										reasonDetail = "企业未开通";
									}else {
										reasonDetail = "其他情况";
									}
	            	            	Toast.makeText(CallListActivity.this, "查询失败 (" + reasonDetail + ")", Toast.LENGTH_SHORT).show();
								}
		                        mProgress.setVisibility(View.GONE);
		                        mList.setVisibility(View.VISIBLE);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
                    });

    	        } catch (Exception e) {
    	            e.printStackTrace();
    	        }    
                
                mHandler.post(new Runnable() {
                    public void run() {
                    	mAdapter = new CallListAdapter(CallListActivity.this, mProList, mTimeList, mDuringList, mCityLists);
                        mList.setAdapter(mAdapter);
                        mProgress.setVisibility(View.GONE);
                        mList.setVisibility(View.VISIBLE);
                    }
                });
            }
        }.start();		
	}
	
	public void setData(int i) {
		Log.d(tag, "setData i: " + i);
		String adminTel = mProList.get(i);
		ArrayList<String> joinList = mCityLists.get(i);
		Intent data = new Intent();
		data.putExtra(Intent.EXTRA_SHORTCUT_INTENT, adminTel);			
		data.putStringArrayListExtra(Intent.EXTRA_INTENT, joinList);
		setResult(RESULT_OK, data); 
		CallListActivity.this.finish();
	}

	private String getCustId(){

		if(mCurrent == null){
			return null;
		}

		if(mCurrent.getExternalCustId()!=null){
			return mCurrent.getExternalCustId();
		}

		String  custId = mCurrent.getCustId();
		while(custId.length()<8){
			custId = "0"+custId;
		}
		custId="01"+custId+"4002";

//		ToastUtils.shortToastShow(custId);
		return custId;
	}
}
