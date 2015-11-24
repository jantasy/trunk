package cn.yjt.oa.app.find;

import android.os.Bundle;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.TitleFragmentActivity;

public class FinderActivity extends TitleFragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		getSupportFragmentManager().beginTransaction().add(R.id.content,new FinderFragment()).commit();
		getLeftbutton().setImageResource(R.drawable.navigation_back);
	}
	
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}
	
}
