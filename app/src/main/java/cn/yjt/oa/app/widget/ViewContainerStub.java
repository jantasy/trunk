package cn.yjt.oa.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class ViewContainerStub extends ViewGroup {

	public ViewContainerStub(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ViewContainerStub(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ViewContainerStub(Context context) {
		super(context);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

	}

	public void setView(View view) {
		ViewGroup parent = (ViewGroup) getParent();
		LayoutParams params = getLayoutParams();
		
		view.setId(getId());
		int index = parent.indexOfChild(this);
		parent.removeViewInLayout(this);
		if (params != null)
			parent.addView(view, index, params);
		else
			parent.addView(view, index);
		
	}
}
