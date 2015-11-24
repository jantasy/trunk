package com.chinatelecom.nfc.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class SlidingLinearLayout extends LinearLayout {

	/* �����垮�ㄤ��锟�?锟斤拷濮���剁��x������ */
	private float lastMotionX = -1;

	public SlidingLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
			return false;
	}

	
	
}
