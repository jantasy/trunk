package cn.yjt.oa.app.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.yjt.oa.app.R;

public class DashBoardAppFragment extends BaseFragment implements OnPageChangeListener, OnClickListener{
	private ViewPager viewPager;
	private DashBoardPagerAdapter adapter;
	private TextView businessApp;
	private TextView localApp;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.activity_fragment_app_dashboard, container, false);
		viewPager = (ViewPager) root.findViewById(R.id.app_main_view_pager);
		businessApp = (TextView) root.findViewById(R.id.business_app);
		businessApp.setOnClickListener(this);
		localApp = (TextView) root.findViewById(R.id.local_app);
		localApp.setOnClickListener(this);
		showSelect(true,false);
		adapter = new DashBoardPagerAdapter(getChildFragmentManager());
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(this);
		return root;
	}
	
	private void showSelect(boolean showTask,boolean showNotice) {
		businessApp.setSelected(showTask);
		localApp.setSelected(showNotice);
	}
	
	class DashBoardPagerAdapter extends FragmentPagerAdapter{
		public DashBoardPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int index) {
			return AppFragmentFactory.getFragmentForPager(index);
		}

		@Override
		public int getCount() {
			return AppFragmentFactory.PAGER_COUNT;
		}
		
	}

	@Override
	public void onPageScrollStateChanged(int index) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int index) {
		switch (index) {
		case 0:
			showSelect(true,false);
			break;
		case 1:
			showSelect(false,true);
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.business_app:
			viewPager.setCurrentItem(0);
			break;
		case R.id.local_app:
			viewPager.setCurrentItem(1);
			break;
		default:
			break;
		}
	}
}
