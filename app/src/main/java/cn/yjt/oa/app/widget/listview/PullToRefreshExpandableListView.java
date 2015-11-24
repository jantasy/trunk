package cn.yjt.oa.app.widget.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListView;
import android.widget.Scroller;
import cn.yjt.oa.app.R;

public class PullToRefreshExpandableListView extends ExpandableListView
		implements OnScrollListener {
	private int mTotalItemCount;

	private final float MOTION_MOVE_FACTOR = 0.7f;
	private HeaderView mHeaderView;
	private float mLastEventY;

	private FooterView mFooterView;
	private boolean mFooterEnabled = true;

	private final static int SCROLL_DURATION = 400; // scroll back duration
	private Scroller mHeaderScroller;
	private Scroller mFooterScroller;

	private boolean mHeaderScrolling = false;
	private boolean mFooterScrolling = false;

	public PullToRefreshExpandableListView(Context context) {
		super(context);
		init(context);
	}

	public PullToRefreshExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PullToRefreshExpandableListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mHeaderScroller = new Scroller(context, new DecelerateInterpolator());
		mFooterScroller = new Scroller(context, new DecelerateInterpolator());

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mHeaderView = (HeaderView) inflater.inflate(
				R.layout.mini_task_pull_to_refresh_header, this, false);
		addHeaderView(mHeaderView);

		mFooterView = (FooterView) inflater.inflate(
				R.layout.mini_task_pull_to_refresh_footer, this, false);
		addFooterView(mFooterView);

		setOnScrollListener(this);
	}
	
	 public void setRefreshingState() {
	        mHeaderView.setRefreshingState();
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

			if (mFooterView != null && getAdapter() != null)
				removeFooterView(mFooterView);
			mFooterEnabled = enabled;
			return;
		}

		if (enabled && !mFooterEnabled) {
			addFooterView(mFooterView);
			mFooterEnabled = enabled;
			return;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastEventY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			float delta = (ev.getY() - mLastEventY)
					* MOTION_MOVE_FACTOR;
			mLastEventY = ev.getY();
			if (!mHeaderView.isRefreshing() && getFirstVisiblePosition() == 0
					&& (mHeaderView.getHeaderHeight() > 0 || delta > 0)
					&& !mHeaderScrolling) {
				mHeaderView.onHeaderHeightMoified((int) delta
						+ mHeaderView.getHeaderHeight());
				setSelection(0);
			} else if (mFooterEnabled && !mFooterView.isLoading()
					&& getLastVisiblePosition() == mTotalItemCount - 1
					&& mFooterView.getBottom() >= getHeight()
					&& (mFooterView.getFooterHeight() > 0 || delta < 0)
					&& !mFooterScrolling) {
				mFooterView.onFooterHeightMoified(
						(int) -delta + mFooterView.getFooterHeight(),
						getHeight());
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			int[] heights = mHeaderView.bindToStableState();
			if (heights[0] != -1) {
				mHeaderScroller.startScroll(0, heights[0], 0, heights[1]
						- heights[0], SCROLL_DURATION);
				mHeaderScrolling = true;
			}

			if (!mFooterEnabled)
				break;

			int[] bottomPaddings = mFooterView.bindToStableState();
			if (bottomPaddings[0] != -1) {
				mFooterScroller.startScroll(0, bottomPaddings[0], 0,
						bottomPaddings[1] - bottomPaddings[0], SCROLL_DURATION);
				mFooterScrolling = true;
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
		if (!mHeaderScroller.isFinished()) {
			mHeaderScroller.forceFinished(true);
			mHeaderScrolling = false;
		}
		mHeaderView.resetHeaderView();
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
	}


	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mTotalItemCount = totalItemCount;
	}
}
