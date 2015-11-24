package cn.yjt.oa.app.component;

import android.os.Bundle;
import cn.yjt.oa.app.R;

public class BackTitleFragmentActivity extends TitleFragmentActivity {

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		getLeftbutton().setImageResource(R.drawable.navigation_back);
	}
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}
}
