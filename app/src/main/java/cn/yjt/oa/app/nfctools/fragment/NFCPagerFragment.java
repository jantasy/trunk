package cn.yjt.oa.app.nfctools.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.yjt.oa.app.R;

public class NFCPagerFragment extends NFCBaseFragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.nfc_fragment_pager, container, false);
		initView(view);
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAdapter = new NFCPagerAdapter(getActivity().getSupportFragmentManager());
	}
	
	private ViewPager mViewPager;
	private NFCPagerAdapter mAdapter;
//	
//	private TextView tvRead;
//	private TextView tvWrite;
//	private TextView tvOther;
//	
	private void initView(View view){
		mViewPager = (ViewPager) view.findViewById(R.id.nfc_pager);
		mViewPager.setAdapter(mAdapter);
		
//		tvRead = (TextView) view.findViewById(R.id.pager_read);
//		tvWrite = (TextView) view.findViewById(R.id.pager_write);
//		tvOther = (TextView) view.findViewById(R.id.pager_other);
		
	}
	
	static class NFCPagerAdapter extends FragmentPagerAdapter{
		private static final int COUNTS = 3;
		public NFCPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return FragmentFactory.createFragment(position);
		}

		@Override
		public int getCount() {
			return COUNTS;
		}
		
	}
}
