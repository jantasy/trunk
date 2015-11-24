package cn.yjt.oa.app.widget.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Scroller;
import cn.yjt.oa.app.R;

public class PullToRefreshListView extends ListView implements OnScrollListener{
    private int mTotalItemCount;
    
    private final float MOTION_MOVE_FACTOR = 0.7f;
    private HeaderView mHeaderView;
    private boolean mHeaderEnabled = true;
    private float mLastEventY;
    
    private FooterView mFooterView;
    private boolean mFooterEnabled = true;
    
    private final static int SCROLL_DURATION = 400; // scroll back duration
    private Scroller mHeaderScroller;
    private Scroller mFooterScroller;
    
    private boolean mHeaderScrolling = false;
    private boolean mFooterScrolling = false;
    
    private boolean mRefreshingEnding = false;
    
    private OnScrollListener mScrollListner;
    
    public PullToRefreshListView(Context context) {
        super(context);
        init(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    
    private void init(Context context) {
        mHeaderScroller = new Scroller(context, new DecelerateInterpolator());
        mFooterScroller = new Scroller(context, new DecelerateInterpolator());
        
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mHeaderView = (HeaderView)inflater.inflate(R.layout.mini_task_pull_to_refresh_header, this, false);
        addHeaderView(mHeaderView);
        
        mFooterView = (FooterView)inflater.inflate(R.layout.mini_task_pull_to_refresh_footer, this, false);
        addFooterView(mFooterView);
        
        super.setOnScrollListener(this);
    }
    
    public FooterView getFooterView() {
    	return this.mFooterView;
    }
    
    public void setOnScrollListener(OnScrollListener listner) {
        mScrollListner = listner;
    }
    
    /**
     * 
     * @param enabled
     */
    public void enableHeaderView(boolean enabled) {
        if (!enabled && mHeaderEnabled) {
            if (!mHeaderScroller.isFinished()) {
                mHeaderScroller.abortAnimation();
                mHeaderScrolling = false;
            }
            
            mHeaderEnabled = enabled;
            return;
        }
        
        if (enabled && !mHeaderEnabled) {
            mHeaderView.resetHeaderView();
            mHeaderEnabled = enabled;
            return;
        }
    }
    
    /**
     * 
     * @param enabled
     */
    public void enableFooterView(boolean enabled) {
        if (!enabled && mFooterEnabled) {
            if (!mFooterScroller.isFinished()) {
                mFooterScroller.abortAnimation();
                mFooterScrolling = false;
            }
            
            mFooterEnabled = enabled;
            return;
        }
        
        if (enabled && !mFooterEnabled) {
            mFooterView.resetFooterView();
            mFooterEnabled = enabled;
            return;
        }
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	boolean ret = super.onInterceptTouchEvent(ev);
    	if (!ret) {
    		mLastEventY = ev.getY();
    	}
    	return ret;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mLastEventY = ev.getY();
            break;
        case MotionEvent.ACTION_MOVE:
            float delta = (ev.getY() - mLastEventY) * MOTION_MOVE_FACTOR;
            mLastEventY = ev.getY();
            if (mHeaderEnabled && !mHeaderView.isRefreshing() &&
                    getFirstVisiblePosition() == 0 &&
                    (mHeaderView.getHeaderHeight() > 0 || delta > 0) &&
                    !mHeaderScrolling) {
                mHeaderView.onHeaderHeightMoified((int)delta + mHeaderView.getHeaderHeight());
                setSelection(0);
            } else if (mFooterEnabled && !mFooterView.isLoading() &&
                    getLastVisiblePosition() == mTotalItemCount - 1 &&
                    mFooterView.getBottom() >= getHeight() &&
                    (mFooterView.getFooterHeight() > 0 || delta < 0) &&
                    !mFooterScrolling) {
                mFooterView.onFooterHeightMoified((int)-delta + mFooterView.getFooterHeight(), getHeight());
            }
            break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            if (mHeaderEnabled) {
                int[] heights = mHeaderView.bindToStableState();
                if (heights[0] != -1) {
                    mHeaderScroller.startScroll(0, heights[0], 0, heights[1] - heights[0], SCROLL_DURATION);
                    mHeaderScrolling = true;
                }
            }
            
            if (mFooterEnabled) {
                int[] bottomPaddings = mFooterView.bindToStableState();
                if (bottomPaddings[0] != -1) {
                    mFooterScroller.startScroll(0, bottomPaddings[0], 0,
                            bottomPaddings[1] - bottomPaddings[0], SCROLL_DURATION);
                    mFooterScrolling = true;
                }
            }
            
            break;
        default:
            break;
        }
        
        return super.onTouchEvent(ev);
    }
    
    @Override
    public void computeScroll() {
        super.computeScroll();
        
        if (mHeaderScroller.computeScrollOffset()) {
            int tempHeight = mHeaderScroller.getCurrY();
            mHeaderView.setHeaderHeight(tempHeight);
        } else if (mHeaderScrolling) {
            mHeaderScrolling = false;
            
            if (mRefreshingEnding) {
                mHeaderView.resetHeaderView();
                mRefreshingEnding = false;
            }
        }
        
        if (mFooterScroller.computeScrollOffset()) {
            int tempHeight = mFooterScroller.getCurrY();
            mFooterView.setFooterHeight(tempHeight);
        } else if (mFooterScrolling) {
            mFooterScrolling = false;
        }
    }

    public void setOnRefreshListener(OnRefreshListener l) {
        mHeaderView.setOnRefreshListener(l);
    }
    
    public void onRefreshComplete() {
    	
        int currentHeight = mHeaderView.getHeaderHeight();
        mHeaderScroller.startScroll(0, currentHeight, 0, -currentHeight, SCROLL_DURATION * 2 / 3);
        mHeaderScrolling = true;
        mRefreshingEnding = true;
        invalidate();
    }
    
    public void setRefreshingState() {
        mHeaderView.setRefreshingState();
    }
    
    public HeaderView getHeaderView(){
    	return mHeaderView;
    }
    
    public void setOnLoadMoreListner(OnLoadMoreListner l) {
        mFooterView.setOnLoadMoreListener(l);
    }
    
    public void onLoadMoreComplete() {
        if (!mFooterScroller.isFinished()) {
            mFooterScroller.forceFinished(true);
            mFooterScrolling = false;
        }
        mFooterView.resetFooterView();
        invalidate();
    }
    
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListner != null)
            mScrollListner.onScrollStateChanged(view, scrollState);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        if (mScrollListner != null)
            mScrollListner.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        
        mTotalItemCount = totalItemCount;
    }
    
}
