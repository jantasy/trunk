package cn.yjt.oa.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class TravelerView extends FrameLayout {

	public TravelerView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public TravelerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TravelerView(Context context) {
		super(context);
	}
	
	private int y;
	
	private static final LayoutParams PARAMS = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); 
	
	public void setTravelerContainedView(View v) {
		removeAllViews();
		addView(v, PARAMS);
	}

	public void traveToPosition(int y) {
		if (this.y != y) {
			this.y = y;
			if (getChildCount() != 1) {
				throw new IllegalArgumentException("TravelerView can only have one view");
			}
			View v = getChildAt(0);
			v.offsetTopAndBottom(y - v.getTop());
		}
	}
	
	public int getTravelerViewHeight() {
		return getChildAt(0).getHeight();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (getChildCount() != 1) {
			throw new IllegalArgumentException("TravelerView can only have one view");
		}
		super.onLayout(changed, l, t, r, b);
		
		
		View v = getChildAt(0);
		v.layout(v.getLeft(), y, v.getRight(), y+v.getHeight());
	}
}
