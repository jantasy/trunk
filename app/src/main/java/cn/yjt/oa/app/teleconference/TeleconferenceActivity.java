package cn.yjt.oa.app.teleconference;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import cn.yjt.oa.app.LaunchActivity;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.component.TitleFragmentActivity;
import cn.yjt.oa.app.teleconference.fragment.FragmentBrige;
import cn.yjt.oa.app.teleconference.fragment.TCBaseFragment;
import cn.yjt.oa.app.teleconference.fragment.TariffIntroductionFragment;
import cn.yjt.oa.app.utils.ViewUtil;

public class TeleconferenceActivity extends TitleFragmentActivity implements FragmentBrige {
	
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		 if(ViewUtil.noLoginToLaunch(this)){
			 LaunchActivity.launch(this);
			 finish();
		 }else{
		getLeftbutton().setImageResource(R.drawable.navigation_back);
		
		TariffIntroductionFragment tariffFragment = new TariffIntroductionFragment();
		getSupportFragmentManager().beginTransaction().add(R.id.content, tariffFragment).commit();
		setTitle(tariffFragment.getTitle());
		 }
	}
	
	@Override
	public void onLeftButtonClick() {
		super.onBackPressed();
	}

	@Override
	public void toFragment(Fragment fragment) {
		this.fragment = fragment;
		getSupportFragmentManager().beginTransaction().replace(R.id.content,fragment).commit();
		setTitle(((TCBaseFragment)fragment).getTitle());
	}
	
	private Fragment fragment;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(fragment != null){
			fragment.onActivityResult(requestCode, resultCode, data);
		}
	}
}
