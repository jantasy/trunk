package cn.yjt.oa.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class RelativeLayout extends android.widget.RelativeLayout {

	private OnKeyListener keyListener;

	public RelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public RelativeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RelativeLayout(Context context) {
		this(context, null);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void setOnKeyListener(OnKeyListener l) {
		super.setOnKeyListener(l);
		keyListener = l;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (getKeyDispatcherState() == null) {
				return super.dispatchKeyEvent(event);
			}

			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getRepeatCount() == 0) {
				KeyEvent.DispatcherState state = getKeyDispatcherState();
				if (state != null) {
					state.startTracking(event, this);
				}
				return true;
			} else if (event.getAction() == KeyEvent.ACTION_UP) {
				KeyEvent.DispatcherState state = getKeyDispatcherState();
				if (state != null && state.isTracking(event)
						&& !event.isCanceled()) {
					if (keyListener != null) {
						keyListener.onKey(this, event.getKeyCode(), event);
					}
					return true;
				}
			}
			return super.dispatchKeyEvent(event);
		} else {
			return super.dispatchKeyEvent(event);
		}
	}

}
