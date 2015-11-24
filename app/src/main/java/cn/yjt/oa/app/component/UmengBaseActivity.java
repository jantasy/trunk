package cn.yjt.oa.app.component;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.umeng.analytics.MobclickAgent;

/**
 * 基类activity，为了确保在每个Activity的 ,
 * onResume方法中调用 MobclickAgent.onResume(Context),
 * onPause方法中调用 MobclickAgent.onPause(Context)
 * 
 */
public class UmengBaseActivity extends FragmentActivity {

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

	protected void hideSoftInput() {
		InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (manager.isActive()) {
			View currentFocus = getCurrentFocus();
			if (currentFocus != null) {
				manager.hideSoftInputFromWindow(currentFocus.getWindowToken(),
						0);
			}
		}
	}

}
