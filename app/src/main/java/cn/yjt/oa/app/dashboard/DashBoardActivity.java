package cn.yjt.oa.app.dashboard;

import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.TimeLineFragment;
import cn.yjt.oa.app.consume.ConsumeFragment;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.widget.TimeLineAdapter;

import com.umeng.analytics.MobclickAgent;

public class DashBoardActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment_container);
		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.container, new ConsumeFragment());
		transaction.commit();
	}
	
	class TestFragment extends TimeLineFragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			return inflater.inflate(R.layout.test, container, false);
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			TimeLineAdapter adapter = new TimeLineAdapter() {
				
				@Override
				public View getSectionView(int section, View convertView, ViewGroup parent) {
					if (convertView == null) {
						convertView = new TextView(DashBoardActivity.this);
					}
					
					((TextView)convertView).setText("section:"+BusinessConstants.getDate(getSectionDate(section)));
					return convertView;
				}
				
				@Override
				public View getItemView(int section, int position, View convertView,
						ViewGroup parent) {
					if (convertView == null) {
						convertView = new TextView(DashBoardActivity.this);
					}
					
					((TextView)convertView).setText("section:"+section+", position:"+position);
					return convertView;
				}
			};
			
			adapter.addEntry(new Date(2014, 4, 3, 2, 2), null);
			adapter.addEntry(new Date(2014, 4, 3, 2, 2), null);
			adapter.addEntry(new Date(2014, 4, 3, 4, 2), null);
			adapter.addEntry(new Date(2014, 4, 4, 2, 2), null);
			adapter.addEntry(new Date(2014, 4, 4, 2, 2), null);
			adapter.addEntry(new Date(2014, 4, 4, 5, 2), null);
			
			setListViewAdapter(adapter);
		}
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
