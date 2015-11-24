package cn.yjt.oa.app.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

public class MyViewPager extends ViewPager {

private boolean mCanScroll = true;
	
	private float	mLastX;
	private float	mLastY;
	private boolean	mHandle;

	public MyViewPager(Context context) {
		super(context);
	}

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		if (mCanScroll) {
			final int action = e.getAction();
			float fx = e.getX();
			float fy = e.getY();
			if(action == MotionEvent.ACTION_DOWN){
				disallowInterceptTouch();
				mLastX = fx;
				mLastY = fy;
				mHandle = true;
			}else if(action == MotionEvent.ACTION_MOVE){
				if(mHandle){
					float disX = fx - mLastX;
					float disY = fy - mLastY;
					if(Math.abs(disX) > Math.abs(disY)){
						if(disX > 0){
							PagerAdapter adapter = getAdapter();
							if(adapter != null){
								final int pos = getCurrentItem();
								if(pos > 0){
									disallowInterceptTouch();
								}else{
									allowInterceptTouch();
								}
							}
						}else{
							PagerAdapter adapter = getAdapter();
							if(adapter != null){
								final int pos = getCurrentItem();
								if(pos < adapter.getCount() - 1){
									disallowInterceptTouch();
								}else{
									allowInterceptTouch();
								}
							}
						}
						mHandle = false;
					}else{
						allowInterceptTouch();
						mHandle = false;
					}
				}
			}
//			if (action == MotionEvent.ACTION_DOWN) {
//				PagerAdapter adapter = getAdapter();
//				if (adapter != null) {
//					final int pos = getCurrentItem();
//					if (pos == 0) {
//						if (pos != adapter.getCount() - 1) {
//							if (fx < WindowUtils.getWindowWidth(getContext()) / 2) {
//
//							} else if (fx > WindowUtils.getWindowWidth(getContext()) / 2) {
//								ViewParent vp = getParent();
//								if (vp != null) {
//									vp.requestDisallowInterceptTouchEvent(true);
//								}
//							}
//						}
//					} else if (pos == adapter.getCount() - 1) {
//						if (fx < WindowUtils.getWindowWidth(getContext()) / 2) {
//							ViewParent vp = getParent();
//							if (vp != null) {
//								vp.requestDisallowInterceptTouchEvent(true);
//							}
//						}
//					} else {
//						ViewParent vp = getParent();
//						if (vp != null) {
//							vp.requestDisallowInterceptTouchEvent(true);
//						}
//					}
//				}
//			}
			return super.onInterceptTouchEvent(e);
		} else {
			return false;
		}
	}
	
	@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		 int height = 0;
		    //下面遍历所有child的高度
		    for (int i = 0; i < getChildCount(); i++) {
		      View child = getChildAt(i);
		      child.measure(widthMeasureSpec,
		          MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		      int h = child.getMeasuredHeight();
		      if (h > height) //采用最大的view的高度。
		        height = h;
		    }

		    heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
		        MeasureSpec.EXACTLY);

		    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	
	protected void allowInterceptTouch(){
		ViewParent vp = getParent();
		if (vp != null) {
			vp.requestDisallowInterceptTouchEvent(false);
		}
	}
	
	protected void disallowInterceptTouch(){
		ViewParent vp = getParent();
		if (vp != null) {
			vp.requestDisallowInterceptTouchEvent(true);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		if (mCanScroll) {
			try{
				return super.onTouchEvent(e);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			return false;
		} else {
			return false;
		}
	}

	public void setCanScroll(boolean bScroll) {
		mCanScroll = bScroll;
	}
}
