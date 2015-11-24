package cn.yjt.oa.app.lifecircle;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.ExpandableListView.OnChildClickListener;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.lifecircle.adapter.ChooseCityAdapter;
import cn.yjt.oa.app.lifecircle.model.Netable;
import cn.yjt.oa.app.lifecircle.utils.Constants;
import cn.yjt.oa.app.lifecircle.utils.PreferfenceUtils;

public class ChooseCityActivity extends TitleFragmentActivity {
	
	private static final String tag = "ChooseCityActivity";
    private ProgressBar mProgress;
    private ExpandableListView mList;
    private ArrayList<String> mProList;    
    private ArrayList<ArrayList<String>> mCityLists;
    private Handler mHandler;
    private ChooseCityAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_choose_city);
		setTitle("城市切换");
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		mProgress = (ProgressBar) findViewById(R.id.center_progress);
		mList = (ExpandableListView) findViewById(R.id.city_list);
		mList.setGroupIndicator(null);
        
		mProList = new ArrayList();
		mCityLists = new ArrayList();
        mHandler = new Handler();
        mList.setOnChildClickListener(new OnChildClickListener() {
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long j) {
//                PreferfenceUtils.setCityPreferences(ChooseCityActivity.this, (String) ((ArrayList) mCityLists.get(i)).get(i2));                
//                Constants.PRO_IP = "https://" + ((Netable) Constants.getProList().get(i)).getIp() + "/";
//                Constants.business = ((Netable) Constants.getProList().get(i)).getBussiness();
//                Constants.PRO_NAME = ((Netable) Constants.getProList().get(i)).getName();
                String city = (String) ((ArrayList) mCityLists.get(i)).get(i2);
                Log.d(tag, "Location.city:" + city);     
        		Intent data = new Intent();
        		data.putExtra(Intent.EXTRA_INTENT, city);
        		setResult(RESULT_OK, data);                
                ChooseCityActivity.this.finish();
                return false;
            }
        });
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
                if (!(Constants.getProList() == null || Constants.getProList().size() == 0)) {
                    for (int i = 0; i < Constants.getProList().size(); i++) {
                        ArrayList cityList = new ArrayList();
                        Netable proNetable = (Netable) Constants.getProList().get(i);
                        for (String city : proNetable.getCities()) {
                        	cityList.add(city);
                        }
                        mProList.add(proNetable.getName());
                        mCityLists.add(cityList);
                    }                    
                }
                mHandler.post(new Runnable() {
                    public void run() {
                    	mAdapter = new ChooseCityAdapter(ChooseCityActivity.this, mProList, mCityLists);
                        mList.setAdapter(mAdapter);
                        mProgress.setVisibility(View.GONE);
                        mList.setVisibility(View.VISIBLE);
                    }
                });
            }
        }.start();
		
	}
}
