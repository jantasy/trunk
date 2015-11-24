package cn.yjt.oa.app.lifecircle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class LinearView extends LinearLayout {
    public BaseAdapter adapter;
    public OnClickListener mOnClickListener;

    public LinearView(Context context) {
        super(context);
        mOnClickListener = null;
    }

    public LinearView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mOnClickListener = null;
    }

    public void bindLinearLayout() {
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            View dropDownView = adapter.getDropDownView(i, null, null);
            dropDownView.setOnClickListener(mOnClickListener);
            addView(dropDownView, i);
        }
        Log.v("countTAG", count+"");
    }

    public BaseAdapter getAdapter() {
        return adapter;
    }

    public OnClickListener getOnClickListener() {
        return mOnClickListener;
    }

    public void setAdapter(BaseAdapter baseAdapter) {
        adapter = baseAdapter;
        bindLinearLayout();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }
}
