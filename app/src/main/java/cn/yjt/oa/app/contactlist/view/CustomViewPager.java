package cn.yjt.oa.app.contactlist.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {
    
	private boolean isScrollable;
	public CustomViewPager(Context context) {
		super(context);
	}

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		if (!isScrollable) {
			return false;
		}
		return super.onTouchEvent(arg0);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if (!isScrollable) {
			return false;
		}
		return super.onInterceptTouchEvent(arg0);
	}
	
	public void setScrollable(boolean scrollable){
		this.isScrollable = scrollable;
	}
	
	public boolean getScrollable(){
		return isScrollable;
	}
}
