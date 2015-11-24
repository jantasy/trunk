package cn.yjt.oa.app.lifecircle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;


public class RoundedRectListView extends ListView {
    public RoundedRectListView(Context context) {
        super(context);
    }

    public RoundedRectListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public RoundedRectListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case 0 /*0*/:
                if (pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY()) == -1) {
                    break;
                }
                break;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }
}
