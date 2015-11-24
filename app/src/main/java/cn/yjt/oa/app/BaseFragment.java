package cn.yjt.oa.app;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

public abstract class BaseFragment extends Fragment {

	public abstract CharSequence getPageTitle(Context context);
	
	public abstract boolean onRightButtonClick();
	
	public abstract void configRightButton(ImageView imgView);
	
	public void onFragmentSelected(){
		
	}
}
