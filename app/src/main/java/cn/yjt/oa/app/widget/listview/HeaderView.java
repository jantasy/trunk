package cn.yjt.oa.app.widget.listview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.yjt.oa.app.R;

public class HeaderView extends LinearLayout {
    private int mHeaderOriginHeight;
    
    private LinearLayout mHeaderContainer;
    private TextView mRefreshViewText;
//    private ImageView mRefreshViewImage;
    private ProgressBar mRefreshViewProgress;
    private TextView mRefreshViewLastUpdated;
    private LinearLayout topMessageContainer;

    private RotateAnimation mFlipAnimation;
    private RotateAnimation mReverseFlipAnimation;
    
    private int mRefreshState;
    private static final int PULL_TO_REFRESH = 0;
    private static final int RELEASE_TO_REFRESH = 1;
    private static final int REFRESHING = 2;
    private int refreshing_height;
    
    private OnRefreshListener mRefreshListner;
    public HeaderView(Context context) {
        super(context);
        init(context);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public HeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        topMessageContainer = (LinearLayout) findViewById(R.id.zhiding);
        mHeaderContainer = (LinearLayout)findViewById(R.id.header_container);
        mRefreshViewText =
            (TextView) findViewById(R.id.pull_to_refresh_text);
//        mRefreshViewImage =
//            (ImageView) findViewById(R.id.pull_to_refresh_image);
        mRefreshViewProgress =
            (ProgressBar) findViewById(R.id.pull_to_refresh_progress);
        mRefreshViewLastUpdated =
            (TextView) findViewById(R.id.pull_to_refresh_updated_at);
        
        mHeaderOriginHeight = getContext().getResources().
                getDimensionPixelSize(R.dimen.pull_to_refresh_header_origin_height);
    }

    private void init(Context context) {
    	refreshing_height = context.getResources().getDimensionPixelSize(R.dimen.refreshing_scroll_height);
        // Load all of the animations we need in code rather than through XML
        mFlipAnimation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(250);
        mFlipAnimation.setFillAfter(true);
        mReverseFlipAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(250);
        mReverseFlipAnimation.setFillAfter(true);
        
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            
            @Override
            public void onGlobalLayout() {
                RelativeLayout headerContent = (RelativeLayout)findViewById(R.id.header_content);
                mHeaderOriginHeight = headerContent.getHeight();
                
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        
        mRefreshState = PULL_TO_REFRESH;
    }
    
    public void updateRefreshState(int newRefreshState) {
        if (newRefreshState == mRefreshState)
            return;
        
        switch (newRefreshState) {
        case PULL_TO_REFRESH:
            mRefreshViewText.setText(R.string.pull_to_refresh_pull_label);
            if (mRefreshState == RELEASE_TO_REFRESH) {
//                mRefreshViewImage.clearAnimation();
//                mRefreshViewImage.startAnimation(mReverseFlipAnimation);
            } else if (mRefreshState == REFRESHING) {
//                mRefreshViewImage.setVisibility(View.VISIBLE);
                mRefreshViewProgress.setVisibility(View.GONE);
            }
            break;
        case RELEASE_TO_REFRESH:
            mRefreshViewText.setText(R.string.pull_to_refresh_release_label);
//            mRefreshViewImage.clearAnimation();
//            mRefreshViewImage.startAnimation(mFlipAnimation);
            break;
        case REFRESHING:
            mRefreshViewText.setText(R.string.pull_to_refresh_refreshing_label);
//            mRefreshViewImage.clearAnimation();
//            mRefreshViewImage.setVisibility(View.GONE);
            mRefreshViewProgress.setVisibility(View.VISIBLE);
            break;
        default:
            break;
        }
        
        mRefreshState = newRefreshState;
    }
    
    public void setOnRefreshListener(OnRefreshListener l) {
        mRefreshListner = l;
    }
    
    /**
     * 由于MotionEvent.Move导致HeaderView.getBottom()发生变化，由此mRefreshState也应相应地变化
     */
    private void adjustRefreshState() {
        int newRefreshState = -1;
        final int bottom = getBottom();
        switch (mRefreshState) {
        case PULL_TO_REFRESH:
            if (bottom > mHeaderOriginHeight + 20) {
                newRefreshState = RELEASE_TO_REFRESH;
            }
            break;
        case RELEASE_TO_REFRESH:
            if (bottom <= mHeaderOriginHeight) {
                newRefreshState = PULL_TO_REFRESH;
            }
            break;
        case REFRESHING:
        default:
            break;
        }
        
        if (newRefreshState != -1)
            updateRefreshState(newRefreshState);
    }
    
    public void onHeaderHeightMoified(int height) {
        setHeaderHeight(height);
        
        adjustRefreshState();
    }
    
    public void setHeaderHeight(int height) {
        if (height < 0)
            height = 0;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)mHeaderContainer.getLayoutParams();
        params.height = height;
        AbsListView.LayoutParams pm = (android.widget.AbsListView.LayoutParams) getLayoutParams();
        pm.height = height+topMessageContainer.getHeight();
        requestLayout();
    }
    
    /**
     * reset HeaderView的高度, 以及mRefreshState
     */
    public void resetHeaderView() {
        setHeaderHeight(0);        
        updateRefreshState(PULL_TO_REFRESH);
    }
    
    public void setRefreshingState() {
        setHeaderHeight(refreshing_height);
        updateRefreshState(REFRESHING);
    }
    
    /**
     * 将mRefreshState和HeaderView的高度调整到稳定状态״̬
     * @return
     */
    public int[] bindToStableState() {
        int currentHeight = getHeaderHeight();
        switch (mRefreshState) {
        case PULL_TO_REFRESH:
            return new int[] {currentHeight, 0};
        case RELEASE_TO_REFRESH:
            switchToRefresh();
            return new int[] {currentHeight, mHeaderOriginHeight};
        case REFRESHING:
            if (currentHeight > mHeaderOriginHeight)
                return new int[] {currentHeight, mHeaderOriginHeight};
            else
                return new int[] {currentHeight, currentHeight};
        default:
            break;
        }
        return new int[] {-1, -1};
    }
    
    private void switchToRefresh() {
        updateRefreshState(REFRESHING);
        if (mRefreshListner != null)
            mRefreshListner.onRefresh();
    }
    
    public int getHeaderHeight() {
        return mHeaderContainer.getHeight();
    }
    
    public boolean isRefreshing() {
        return mRefreshState == REFRESHING;
    }
    
}
