package cn.yjt.oa.app.signin.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import cn.yjt.oa.app.widget.WaveImageView;
import cn.yjt.oa.app.widget.dragdrop.DragSource;

public class SigninButton extends WaveImageView implements DragSource {

	public SigninButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SigninButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SigninButton(Context context) {
		super(context);
	}

	@Override
	public void drawShadow(Canvas canvas) {
		int saveCount = canvas.save();
		draw(canvas);
		canvas.restoreToCount(saveCount);
	}

	@Override
	public Object startDrag() {
		setVisibility(View.INVISIBLE);
		stopAnimate();
		return null;
	}

	@Override
	public void onDragComplete(boolean isCanceled) {
		setVisibility(View.VISIBLE);
		startAnimate();
	}

}
