package cn.yjt.oa.app.nfctools;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.nfctools.fragment.NFCBaseFragment;

/**
 * NFC基类activity
 *
 */
public class NFCBaseActivity extends TitleFragmentActivity{
	
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.nfc_activity_main);
		initTitleBar();
		attachFragment(savedState);
	}
	
	/**绑定Fragment*/
	private void attachFragment(Bundle savedState){
		NFCBaseFragment fragment = getFragment();
		if(savedState == null && fragment != null){
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.add(R.id.nfc_container,fragment);
			transaction.commit();
		}
	}
	

	protected NFCBaseFragment getFragment(){
		return null;
	}
	
	/**初始化标题栏的操作*/
	protected void initTitleBar(){
		getLeftbutton().setImageResource(R.drawable.navigation_back);
	}
	
	/**标题栏左按钮的点击操作*/
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}
}
