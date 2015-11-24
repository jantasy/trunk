package cn.yjt.oa.app.app.activity;

import android.os.Bundle;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.fragment.AppManagerFragment;
import cn.yjt.oa.app.component.BaseActivity;

public class AppManagerActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		if (savedInstanceState == null) {
			getSupportFragmentManager()
			.beginTransaction()
			.add(R.id.app_download_manager, new AppManagerFragment())
			.commit();
		}
	}
}
