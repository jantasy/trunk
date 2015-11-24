package cn.yjt.oa.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class CheckableContainer extends LinearLayout implements Checkable {

	public CheckableContainer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CheckableContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CheckableContainer(Context context) {
		super(context);
	}
	
	private boolean checked;
	
	private void setCheckedTravasal(View parent, boolean checked) {
		if (parent != this && parent instanceof Checkable) {
			((Checkable)parent).setChecked(checked);
		}
		if (parent instanceof ViewGroup) {
			ViewGroup group = (ViewGroup)parent;
			int childCount = group.getChildCount();
			for (int i=0; i<childCount; ++i) {
				setCheckedTravasal(group.getChildAt(i), checked);
			}
		}
	}

	@Override
	public void setChecked(boolean checked) {
		if (this.checked != checked) {
			this.checked = checked;
			setCheckedTravasal(this, checked);
		}
	}

	@Override
	public boolean isChecked() {
		return checked;
	}

	@Override
	public void toggle() {
		setChecked(!checked);
	}

}
