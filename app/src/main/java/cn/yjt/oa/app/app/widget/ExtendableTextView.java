package cn.yjt.oa.app.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class ExtendableTextView extends TextView{
	private OnSizeChangeListener mOnSizeChangeListener;
	public ExtendableTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ExtendableTextView(Context context) {
		super(context);
	}

	public ExtendableTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public void setOnSizeChangedListener(OnSizeChangeListener listener){
		mOnSizeChangeListener = listener;
	};
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if(mOnSizeChangeListener != null){
			mOnSizeChangeListener.onLineCountChanged(getLineCount());
		}
	}

	public interface OnSizeChangeListener {
		public void onLineCountChanged(int lineCount);
	}

}
