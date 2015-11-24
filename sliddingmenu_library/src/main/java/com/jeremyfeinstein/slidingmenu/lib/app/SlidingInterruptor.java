package com.jeremyfeinstein.slidingmenu.lib.app;

import android.view.MotionEvent;

public interface SlidingInterruptor {
	
	public boolean allowTouchInterrupted(MotionEvent ev);
	public boolean getUserVisibleHint(MotionEvent ev);

}
