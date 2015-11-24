package cn.yjt.oa.app.push;

import java.util.Collections;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter{
	private List<Fragment> fragments=Collections.emptyList();

	
	public MyFragmentPagerAdapter(FragmentManager fm,List<Fragment> frags){
		super(fm);
		fragments=frags;
		
	}
	

	@Override
	public Fragment getItem(int arg0) {
		return fragments.get(arg0);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}
	
	@Override
	public int getItemPosition(Object object) {
		if(object==null){
			return -1;
		}else{
			return fragments.indexOf(object);
		}
		
		
	}

}
