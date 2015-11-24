package cn.yjt.oa.app.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class TouchScrollView extends ScrollView {

	private boolean isDispatch = false;
	public TouchScrollView(Context context) {
		super(context);
	}

	public TouchScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TouchScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public void setisDispatch(boolean isDispatch){
		this.isDispatch = isDispatch;
	}
	/**
	 * 
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if(getChildCount()>0){
			getChildAt(0).measure(widthMeasureSpec, heightMeasureSpec);
		}
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(!isDispatch){
			return super.onInterceptTouchEvent(ev); 
		}
		return false;
	}
}
