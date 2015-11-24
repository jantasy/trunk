package cn.yjt.oa.app.push;

import android.view.View;

import com.nineoldandroids.animation.ObjectAnimator;

public class TabIndicatorAnimation {

	private View mViewIndicator;
	private int mTabWidth;
	private int mCurrentTab;

	public TabIndicatorAnimation(View viewIndicator, int tabWidth) {
		mViewIndicator = viewIndicator;
		mTabWidth = tabWidth;
	}

	public void onTabChanged(int tab) {
		ObjectAnimator.ofFloat(mViewIndicator, "translationX",
						mCurrentTab * mTabWidth, tab * mTabWidth)
				.setDuration(200).start();
		mCurrentTab = tab;
	}
}
