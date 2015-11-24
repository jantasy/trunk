package cn.yjt.oa.app.enterprise;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.TitleFragmentActivity;

public class BaseActivity extends TitleFragmentActivity{
	
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.nfc_activity_main);
		initTitleBar();
		attachFragment(savedState);
	}
	
	private void attachFragment(Bundle savedState){
		Fragment fragment = getFragment();
		if(savedState == null && fragment != null){
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.add(R.id.nfc_container,fragment);
			transaction.commit();
		}
	}
	
	protected Fragment getFragment(){
		return null;
	}
	
	protected void initTitleBar(){
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		setTitle(initTitle());
	}
	
	protected CharSequence initTitle() {
		return "";
	}

	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}
}
