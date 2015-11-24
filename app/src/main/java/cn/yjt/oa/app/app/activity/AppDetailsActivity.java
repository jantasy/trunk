package cn.yjt.oa.app.app.activity;

import android.os.Bundle;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.AppRequest;
import cn.yjt.oa.app.app.fragment.ActivityDetailsFragment;
import cn.yjt.oa.app.component.TitleFragmentActivity;

public class AppDetailsActivity extends TitleFragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		setContentView(R.layout.activity_app_details);
		if(getIntent().getAction() == AppRequest.ACTION_APP_DETAILS){
			setTitle("应用详情");
		}
		if (savedInstanceState == null) {
			getSupportFragmentManager()
			.beginTransaction()
			.add(R.id.app_details_container, new ActivityDetailsFragment())
			.commit();
		}
	}
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}
}
