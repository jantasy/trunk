package cn.yjt.oa.app.lifecircle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class FooterListView extends ListView implements OnScrollListener {
    private static final String tag = "FooterListView";
    private OnAdapterListener mOnAdapterListener;
    private OnPagingListener mOnPagingListener;
    private int visibleLastIndex;

    public interface OnAdapterListener {
        BaseAdapter onAdapter();
    }

    public interface OnPagingListener {
        void onPaging();
    }

    public FooterListView(Context context) {
        super(context);
        setOnScrollListener(this);
    }

    public FooterListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setOnScrollListener(this);
    }

    private BaseAdapter onAdapter() {
        return mOnAdapterListener != null ? mOnAdapterListener.onAdapter() : null;
    }

    private void onPaging() {
        if (mOnPagingListener != null) {
        	mOnPagingListener.onPaging();
        }
    }

    public BaseAdapter getAdapter(BaseAdapter baseAdapter) {
        return baseAdapter;
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        visibleLastIndex = (i + i2) - 1;
    }
    
    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if (onAdapter() != null) {
            int count = (onAdapter().getCount() - 1) + 1;
            Log.d(tag, visibleLastIndex + "," + count);
            if (i == 0 && visibleLastIndex == count) {
                onPaging();
            }
        }
    }

    public void setOnAdapterListener(OnAdapterListener onAdapterListener) {
    	mOnAdapterListener = onAdapterListener;
    }

    public void setonPagingListener(OnPagingListener onPagingListener) {
        Log.d(tag, "setonPagingListener---");
        mOnPagingListener = onPagingListener;
    }
}
