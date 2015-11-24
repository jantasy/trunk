package cn.yjt.oa.app.component;


import java.lang.reflect.Method;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.widget.SectionAdapter;
import cn.yjt.oa.app.widget.TimeLineView;
import cn.yjt.oa.app.widget.TravelerView;

public class TimeLineFragment extends Fragment implements ListView.OnScrollListener {
	
	private ListView listView;
	private TimeLineView timelineView;
	private TravelerView travelerView;
	SectionAdapter sectionAdapter;
	
	private int scrollState = ListView.OnScrollListener.SCROLL_STATE_IDLE;
	
	private static Method computeVerticalScrollExtentMethod;
	private static Method computeVerticalScrollOffsetMethod;
	private static Method computeVerticalScrollRangeMethod;
	
	static
	{
		try {
			computeVerticalScrollExtentMethod = View.class.getDeclaredMethod("computeVerticalScrollExtent");
			computeVerticalScrollExtentMethod.setAccessible(true);
			computeVerticalScrollOffsetMethod = View.class.getDeclaredMethod("computeVerticalScrollOffset");
			computeVerticalScrollOffsetMethod.setAccessible(true);
			computeVerticalScrollRangeMethod = View.class.getDeclaredMethod("computeVerticalScrollRange");
			computeVerticalScrollRangeMethod.setAccessible(true);
		} catch (Throwable e) {
			//ignore
		}
		
	}
	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		listView = (ListView) view.findViewById(R.id.listview);
		timelineView = (TimeLineView) view.findViewById(R.id.timelineview);
		travelerView = (TravelerView) view.findViewById(R.id.travelerview);
		
		if (listView != null) {
			listView.setOnScrollListener(this);
			ListViewClickListener listener = new ListViewClickListener();
			listView.setOnItemClickListener(listener);
			listView.setOnItemLongClickListener(listener);
			if (sectionAdapter != null) {
				listView.setAdapter(sectionAdapter);
			}
		}
		
		if (travelerView != null) {
			travelerView.setVisibility(View.GONE);
		}
		if (timelineView != null) {
			timelineView.setVisibility(View.GONE);
		}
	}
	
	private class ListViewClickListener implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			int headrCount = listView.getHeaderViewsCount();
			if (position < headrCount)
				TimeLineFragment.this.onHeaderClick(parent, view, position, id);
			else
				TimeLineFragment.this.onItemClick(parent, view, position-headrCount, id);
		}

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			int headrCount = listView.getHeaderViewsCount();
			if (position < headrCount)
				return TimeLineFragment.this.onHeaderLongClick(parent, view, position, id);
			else
				return TimeLineFragment.this.onItemLongClick(parent, view, position-headrCount, id);
		}
		
	}

	public ListView getListView() {
		return listView;
	}
	
	public TravelerView getTravelerView() {
		return travelerView;
	}
	
	private DataSetObserver observer = new DataSetObserver() {

		@Override
		public void onChanged() {
			super.onChanged();
			setupTimeLine();
		}

		@Override
		public void onInvalidated() {
			super.onInvalidated();
			setupTimeLine();
		}
	};
	
	public void setListViewAdapter(SectionAdapter adapter) {
		if (sectionAdapter != null) {
			sectionAdapter.unregisterDataSetObserver(observer);
		}
		sectionAdapter = adapter;
		if (sectionAdapter != null) {
			sectionAdapter.registerDataSetObserver(observer);
		}
		if (listView != null) {
			listView.setAdapter(sectionAdapter);
		}
	}
	
	private void traveTravelerView() {
		if (travelerView != null && listView != null) {
			int size = listView.getHeight();
			int extent = computeVerticalScrollExtent();
			int range = computeVerticalScrollRange();
			int offset = computeVerticalScrollOffset();
			
			int height = Math.round((float) size * extent / range);
			int thumbOffset = Math.round((float) (size - height) * offset / (range - extent));
            
            travelerView.traveToPosition(thumbOffset);
            
            int travelerHeight = travelerView.getTravelerViewHeight();
            int centerPos = thumbOffset+travelerHeight/2;
            int childCount = listView.getChildCount();
            int fistVisible = listView.getFirstVisiblePosition();
            for (int i=0; i<childCount; ++i) {
            	View v = listView.getChildAt(i);
            	if (v.getTop() <= centerPos && v.getBottom() >= centerPos) {
            		onTravedToPosition(fistVisible+i);
            		break;
            	}
            }
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (this.scrollState == SCROLL_STATE_IDLE) {
			if (this.scrollState != scrollState) {
				setShowTravelerView(true);
			}
		} else if (scrollState == SCROLL_STATE_IDLE) {
			setShowTravelerView(false);
		}
		
		this.scrollState = scrollState;
		traveTravelerView();
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
		setupTimeLine();
		
		traveTravelerView();
	}
	
	protected void setupTimeLine() {
		if (timelineView != null && sectionAdapter != null) {
			if (sectionAdapter.getCount() > 0)
				timelineView.setVisibility(View.VISIBLE);
			else
				timelineView.setVisibility(View.GONE);
			timelineView.clearAllTimeNodes();
			timelineView.setFirstNodeShowing(false);
			int visibleItemCount = listView.getChildCount();
			int firstVisibleItem = listView.getFirstVisiblePosition();
			for (int i=0; i<visibleItemCount; ++i) {
				int pos = firstVisibleItem+i-listView.getHeaderViewsCount();
				View v = listView.getChildAt(i);
				int[] sectionInfo = SectionAdapter.getDetailPosition(sectionAdapter, pos);
				if (sectionInfo != null && sectionInfo.length == 1) {
					// is section
					timelineView.addTimeNode(v.getTop()+v.getHeight()/2);
					if (sectionInfo[0] == 0) {
						timelineView.setFirstNodeShowing(true);
					}
				}
			}
			timelineView.invalidate();
		}
	}
	
	public void setShowTravelerView(boolean show) {
		if (travelerView != null) {
			if (show)
				travelerView.setVisibility(View.VISIBLE);
			else
				travelerView.setVisibility(View.GONE);
		}
	}
	
	public void onTravedToPosition(int position) {
		
	}
	
	public void onHeaderClick(AdapterView<?> parent, View view, int headerIndex,
			long id) {
		
	}
	
	public boolean onHeaderLongClick(AdapterView<?> parent, View view, int headerIndex,
			long id) {
		return false;
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int positionInAdapter,
			long id) {
		
	}
	
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int positionInAdapter, long id) {
		return false;
	}
	
    private int computeVerticalScrollExtent() {
        try {
			return (Integer) computeVerticalScrollExtentMethod.invoke(listView);
		} catch (Throwable e) {
			//ignore
			return 0;
		}
    }

    private int computeVerticalScrollOffset() {
    	 try {
 			return (Integer) computeVerticalScrollOffsetMethod.invoke(listView);
 		} catch (Throwable e) {
 			//ignore
 			return 0;
 		}
    }

    private int computeVerticalScrollRange() {
    	 try {
 			return (Integer) computeVerticalScrollRangeMethod.invoke(listView);
 		} catch (Throwable e) {
 			//ignore
 			return 0;
 		}
    }
}
