package cn.yjt.oa.app.widget.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.yjt.oa.app.R;


public class FooterView extends LinearLayout {
    private final int TAP_TO_LOAD_MORE = 0;
    private final int RELEASE_TO_LOAD_MORE = 1;
    private final int LOADING_MORE = 2;
    
    private int mLoadState = TAP_TO_LOAD_MORE;
    
    private LinearLayout mFooterContainer;
    private RelativeLayout mFooterContent;
    private TextView mLoadText;
    private ProgressBar mLoadProgress;
    
    private int mFooterOriginHeight;
    
    private OnLoadMoreListner mLoadMoreListner;

    public FooterView(Context context) {
        super(context);
        init(context);
    }

    public FooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FooterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        
        mFooterContainer = (LinearLayout)findViewById(R.id.footer_container); 
        mFooterContent = (RelativeLayout)findViewById(R.id.footer_content);
        mLoadText = (TextView)findViewById(R.id.loading_more_text);
        mLoadProgress = (ProgressBar)findViewById(R.id.loading_more_progress);
    }
    
    private void init(Context context) {
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            
            @Override
            public void onGlobalLayout() {
                mFooterOriginHeight = mFooterContent.getHeight();
                
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        
//        setOnClickListener(new OnClickListener() {
//            
//            @Override
//            public void onClick(View v) {
//                switchToLoading();
//            }
//        });
    }
    
    public void onFooterHeightMoified(int height, int parentHeight) {
        setFooterHeight(height);
        
        adjustLoadState(parentHeight);
    }
    
    public void setFooterHeight(int height) {
        if (height < 0)
            height = 0;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)mFooterContainer.getLayoutParams();
        params.height = height;
        requestLayout();
    }

    /**
     * 由于MotionEvent.Move导致FooterView.getTop()发生变化，由此mLoadState也应相应地变化
     */
    private void adjustLoadState(final int parentHeight) {
        int newLoadState = -1;
        final int top = getTop();
        switch (mLoadState) {
        case TAP_TO_LOAD_MORE:
            if (top < parentHeight - mFooterOriginHeight - 20) {
                newLoadState = RELEASE_TO_LOAD_MORE;
            }
            break;
        case RELEASE_TO_LOAD_MORE:
            if (top >= parentHeight - mFooterOriginHeight) {
                newLoadState = TAP_TO_LOAD_MORE;
            }
            break;
        case LOADING_MORE:
            break;
        default:
            break;        
        }
        
        if (newLoadState != -1)
            updateLoadState(newLoadState);
    }
    
    public void updateLoadState(int newLoadState) {
        if (newLoadState == mLoadState)
            return;
        
        switch (newLoadState) {
        case TAP_TO_LOAD_MORE:
            mLoadText.setText(R.string.pull_to_load_more_tap_label);
            mLoadProgress.setVisibility(View.GONE);
            break;
        case RELEASE_TO_LOAD_MORE:
            mLoadText.setText(R.string.pull_to_load_more_release_label);
            break;
        case LOADING_MORE:
            mLoadText.setText(R.string.pull_to_load_more_loading_label);
            mLoadProgress.setVisibility(View.VISIBLE);
            break;
        default:
            break;
        }
        
        mLoadState = newLoadState;
    }
    
    /**
     * 将mLoadState和FooterView的高度调整到稳定状态״̬
     * @return
     */
    public int[] bindToStableState() {
        int currentHeight = getFooterHeight();
        switch (mLoadState) {
        case TAP_TO_LOAD_MORE:
            return new int[] {currentHeight, 0};
        case RELEASE_TO_LOAD_MORE:
            switchToLoading();
            return new int[] {currentHeight, mFooterOriginHeight};
        case LOADING_MORE:
            if (currentHeight > mFooterOriginHeight)
                return new int[] {currentHeight, mFooterOriginHeight};
            else
            return new int[] {currentHeight, currentHeight};
        }
        return new int[] {-1, -1};
    }
    
    private void switchToLoading() {
        updateLoadState(LOADING_MORE);
        if (mLoadMoreListner != null)
            mLoadMoreListner.onLoadMore();
    }
    
    public boolean isLoading() {
        return mLoadState == LOADING_MORE;
    }
    
    public int getFooterHeight() {
        return mFooterContainer.getHeight();
    }
    
    public View getFooterContainer() {
    	return this.mFooterContainer;
    }
    
    /**
     * reset FooterView的padding, 以及mLoadState
     */
    public void resetFooterView() {
        setFooterHeight(0);
        
        updateLoadState(TAP_TO_LOAD_MORE);
    }
    
    public void setOnLoadMoreListener(OnLoadMoreListner l) {
        mLoadMoreListner = l;
    }
}
