package cn.yjt.oa.app.signin.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import cn.yjt.oa.app.widget.dragdrop.DragSource;
import cn.yjt.oa.app.widget.dragdrop.DropTarget;

public class FunctionButton extends ImageView implements DropTarget {
	
	private Runnable dropAction;

	public FunctionButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FunctionButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FunctionButton(Context context) {
		super(context);
	}
	
	public void setOnDropAction(Runnable action) {
		dropAction = action;
	}

	@Override
	public boolean accept(Object dragItem, DragSource dragSource) {
		return true;
	}

	@Override
	public void drop(Object dragItem, int x, int y) {
		if (dropAction != null) {
			dropAction.run();
		}
	}

	@Override
	public void onDragIn(int x, int y) {
		setSelected(true);
	}

	@Override
	public void onDragOver(int x, int y) {
	}

	@Override
	public void onDragOut(int x, int y) {
		setSelected(false);
	}

}
