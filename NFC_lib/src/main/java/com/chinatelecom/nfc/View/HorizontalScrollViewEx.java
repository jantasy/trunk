package com.chinatelecom.nfc.View;

import java.lang.reflect.Field;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.OverScroller;
import android.widget.Scroller;

public class HorizontalScrollViewEx extends HorizontalScrollView {
	static final int ANIMATED_SCROLL_GAP = 250;
	private long mLastScroll;

	private Field mScrollerField;
	ScrollerEx scrollerEx = null;

	public HorizontalScrollViewEx(Context context) {
		this(context, null);

	}

	public HorizontalScrollViewEx(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setSmoothScrollingEnabled(false);
		initScroller();
	}

	public HorizontalScrollViewEx(Context context, AttributeSet attrs,
			int defStyle) {

		super(context, attrs, defStyle);
		this.setSmoothScrollingEnabled(false);
		initScroller();
	}

	private void initScroller() {
		try {
			mScrollerField = HorizontalScrollView.class
					.getDeclaredField("mScroller");
			mScrollerField.setAccessible(true);
			String type = mScrollerField.getType().getSimpleName();

			if ("OverScroller".equals(type)) {
				scrollerEx = new ScrollerEx() {
					private OverScroller mScroller = null;

					public void startScroll(int startX, int startY, int dx,
							int dy, int duration) {
						mScroller.startScroll(startX, startY, dx, dy, duration);
					}

					public boolean isFinished() {
						return mScroller.isFinished();
					}

					public Object getScroller() {
						return mScroller;
					}

					public void create(Context context,
							Interpolator interpolator) {
						mScroller = new OverScroller(context, interpolator);
					}

					public void abortAnimation() {
						if (mScroller != null) {
							mScroller.abortAnimation();
						}
					}
				};
			} else {
				scrollerEx = new ScrollerEx() {
					private Scroller mScroller = null;

					public void startScroll(int startX, int startY, int dx,
							int dy, int duration) {
						mScroller.startScroll(startX, startY, dx, dy, duration);
					}

					public boolean isFinished() {
						return mScroller.isFinished();
					}

					public Object getScroller() {
						return mScroller;
					}

					public void create(Context context,
							Interpolator interpolator) {
						mScroller = new Scroller(context, interpolator);
					}

					public void abortAnimation() {
						if (mScroller != null) {
							mScroller.abortAnimation();
						}
					}
				};
			}

		} catch (Exception ex) {
		}
	}

	public final void smoothScrollBy(int dx, int dy, int addDuration) {

		float tension = 0f;

		scrollerEx.abortAnimation();

		Interpolator ip = new OvershootInterpolator(tension);
		scrollerEx.create(getContext(), ip);

		try {
			mScrollerField.set(this, scrollerEx.getScroller());
		} catch (Exception e) {
		}

		long duration = AnimationUtils.currentAnimationTimeMillis()
				- mLastScroll;
		if (duration > ANIMATED_SCROLL_GAP) {
			scrollerEx.startScroll(getScrollX(), getScrollY(), dx, dy,
					addDuration);

			awakenScrollBars();

			// awakenScrollBars(mScroller.getDuration());

			invalidate();
		} else {
			if (!scrollerEx.isFinished()) {
				scrollerEx.abortAnimation();
			}
			scrollBy(dx, dy);
		}
		mLastScroll = AnimationUtils.currentAnimationTimeMillis();

	}

	public final void smoothScrollTo(int x, int y, int duration) {
		smoothScrollBy(x - getScrollX(), y - getScrollY(), duration);
	}

	private interface ScrollerEx {

		void create(Context context, Interpolator interpolator);

		Object getScroller();

		void abortAnimation();

		void startScroll(int startX, int startY, int dx, int dy, int duration);

		boolean isFinished();

	}

}
