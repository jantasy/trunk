package cn.yjt.oa.app.component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.widget.ClockWidget;
import cn.yjt.oa.app.widget.TimeLineAdapter.DateItem;
import cn.yjt.oa.app.widget.TravelerView;

public class TimeClockFragment extends TimeLineFragment {

	private Animation showTravelerAnimation;
	private Animation hideTravelerAnimation;

	private TextView dateView;
	private TextView timeView;
	private ClockWidget clockWidget;
	
	private boolean showTraveler;
	private long showDelay = 300;
	private long hideDelay = 800;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (showTravelerAnimation == null) {
			showTravelerAnimation = AnimationUtils.loadAnimation(getActivity(),
					R.anim.clock_fade_in);
		}
		if (hideTravelerAnimation == null) {
			hideTravelerAnimation = AnimationUtils.loadAnimation(getActivity(),
					R.anim.clock_fade_out);
		}
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		dateView = (TextView) view.findViewById(R.id.date);
		timeView = (TextView) view.findViewById(R.id.time);
		clockWidget = (ClockWidget) view.findViewById(R.id.clockwidget);

	}
	@Override
	public void onTravedToPosition(int position) {
		super.onTravedToPosition(position);
		position = position - getListView().getHeaderViewsCount();
		if (position < 0)
			return;
		if (sectionAdapter == null)
			return;
		Object item = sectionAdapter.getItem(position);
		Date date = null;
		if (item instanceof Date) {
			date = (Date) item;
		} else if (item instanceof DateItem) {
			date = ((DateItem) item).getDate();
		}

		if (date != null) {
			dateView.setText(BusinessConstants.getDate(date));
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			String str = "上午";
			if (cal.get(Calendar.AM_PM) == 0) {
				str = "上午";
			} else {
				str = "下午";
			}
			SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
			timeView.setText(str + sdf.format(date));
			clockWidget.animateTo(date, true);
		}
	}

	public void setShowTravelerView(boolean show) {
		TravelerView travelerView = getTravelerView();
		if (travelerView == null)
			return;
		if (showTraveler != show) {
			showTraveler = show;
			getListView().removeCallbacks(showHideTravelerRunnable);
			if (show && travelerView.getVisibility() == View.VISIBLE)
				return;
			if (!show && travelerView.getVisibility() != View.VISIBLE)
				return;
			getListView().postDelayed(showHideTravelerRunnable, show ? showDelay
					: hideDelay);
		}
	}

	private Runnable showHideTravelerRunnable = new Runnable() {

		@Override
		public void run() {
			TravelerView travelerView = getTravelerView();
			if (travelerView != null) {
				if (showTraveler) {
					travelerView.setVisibility(View.VISIBLE);
					travelerView.startAnimation(showTravelerAnimation);
				} else {
					travelerView.setVisibility(View.GONE);
					travelerView.startAnimation(hideTravelerAnimation);
				}
			}
		}
	};
	
}
