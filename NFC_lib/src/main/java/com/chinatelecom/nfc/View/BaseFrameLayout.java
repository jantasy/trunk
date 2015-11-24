package com.chinatelecom.nfc.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class BaseFrameLayout extends FrameLayout{
	protected Context context;
	
	public BaseFrameLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}
	public BaseFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}
	public BaseFrameLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	private void init(Context context) {
//		setClickable(true);
//		setLongClickable(true);
//		setPressed(false);
//		setSelected(false);
		setWillNotDraw(false);
		setFocusable(true);
		setFocusableInTouchMode(true);
		this.context = context;
	}
}
