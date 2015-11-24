package cn.yjt.oa.app.widget.listview;

/**
 * Interface definition for a callback to be invoked when list should be
 * refreshed.
 */
public interface OnRefreshListener {
    /**
     * Called when the list should be refreshed.
     * <p>
     * A call to {@link PullToRefreshListView #onRefreshComplete()} is
     * expected to indicate that the refresh has completed.
     */
    public void onRefresh();
}