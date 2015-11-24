package cn.yjt.oa.app.widget.dragdrop;

import android.graphics.Canvas;

public interface DragSource {

	void drawShadow(Canvas canvas);
	Object startDrag();
	void onDragComplete(boolean isCanceled);
}
