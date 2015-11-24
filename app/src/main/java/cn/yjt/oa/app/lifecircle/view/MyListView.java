package cn.yjt.oa.app.lifecircle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class MyListView extends ListView implements Runnable {
    private int mDistance;
    private float mLastDownY;
    private boolean mPositive;
    private int mStep;

    public MyListView(Context context) {
        super(context);
        mLastDownY = 0.0f;
        mDistance = 0;
        mStep = 12;
        mPositive = false;
    }

    public MyListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mLastDownY = 0.0f;
        mDistance = 0;
        mStep = 12;
        mPositive = false;
    }

    public MyListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        mLastDownY = 0.0f;
        mDistance = 0;
        mStep = 12;
        mPositive = false;
    }

    public void run() {
    }
}
