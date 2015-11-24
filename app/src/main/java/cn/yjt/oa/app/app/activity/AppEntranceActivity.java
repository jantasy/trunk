package cn.yjt.oa.app.app.activity;

import android.os.Bundle;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.fragment.AppFragmentFactory;
import cn.yjt.oa.app.component.TitleFragmentActivity;

public class AppEntranceActivity extends TitleFragmentActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app);
		String action = getIntent().getAction();
		setTitle(TitleFactory.getTitle(action));
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		if(savedInstanceState == null){
			getSupportFragmentManager()
			.beginTransaction()
			.add(R.id.app_container,AppFragmentFactory.getFragmentWithType(action))
			.commit();
		}
	}
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}
}
