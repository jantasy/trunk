package cn.yjt.oa.app.widget;

import java.util.Date;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import cn.yjt.oa.app.R;

public class ClockWidget extends View {

	public ClockWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}

	public ClockWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0);
	}

	public ClockWidget(Context context) {
		super(context);
		init(context, null, 0);
	}
	
	private void init(Context context, AttributeSet attrs, int defStyle) {
		setWillNotDraw(false);
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClockWidget, defStyle, 0);
			hourDrawable = a.getDrawable(R.styleable.ClockWidget_hourDrawable);
			minuteDrawable = a.getDrawable(R.styleable.ClockWidget_minuteDrawable);
			a.recycle();
		}
	}
	
	private Drawable hourDrawable;
	private Drawable minuteDrawable;
	private int currentHour;
	private int currentMinute;
	private int animateFromHour;
	private int animateFromMinute;
	private int animateToHour;
	private int animateToMinute;
	private boolean isClockWise;
	
	private boolean isAnimating;
	private long startAnimateTime;
	private long animateDuration = 300;
	private static final long DEFAULT_FRAME_DELAY = 10;
	
	private AnimatorSet animator;
	private Animator.AnimatorListener listener;

	public void animateTo(Date date, boolean isClockWise) {
		animateToHour = date.getHours()%12;
		animateToMinute = date.getMinutes();
		animateFromHour = currentHour;
		animateFromMinute = currentMinute;
		startAnimateTime = SystemClock.uptimeMillis();
		this.isClockWise = isClockWise;
		
		boolean thisAnimating = (animateToHour != animateFromHour) || (animateToMinute != animateFromMinute);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			if (isAnimating != thisAnimating) {
				isAnimating = thisAnimating;
				if (isAnimating)
					invalidate();
			}
		} else {
			if (isAnimating != thisAnimating) {
				isAnimating = thisAnimating;
				// 高版本使用animator
				if (animator != null) {
					animator.cancel();
					animator = null;
				}
				if (isAnimating) {
					animator = new AnimatorSet();
					ObjectAnimator animateHour = ObjectAnimator.ofInt(this, "currentHour", animateFromHour, animateToHour);
					ObjectAnimator animateMinute = ObjectAnimator.ofInt(this, "currentMinute", animateFromMinute, animateToMinute);
					animator.playTogether(animateHour, animateMinute);
					animator.setDuration(animateDuration);
					if (listener == null) {
						listener = new Animator.AnimatorListener() {

							@Override
							public void onAnimationStart(Animator animation) {
							}

							@Override
							public void onAnimationEnd(Animator animation) {
								isAnimating = false;
							}

							@Override
							public void onAnimationCancel(Animator animation) {
								isAnimating = false;
							}

							@Override
							public void onAnimationRepeat(Animator animation) {
							}
							
						};
					}
					animator.addListener(listener);
					animator.start();
				}
			}
		}
		
	}
	
	public void setCurrentHour(int hour) {
		hour = normalizeHour(hour);
		if (currentHour != hour) {
			currentHour = hour;
			invalidate();
		}
	}
	
	public void setCurrentMinute(int minute) {
		minute = normalizeMinute(minute);
		if (currentMinute != minute) {
			currentMinute = minute;
			invalidate();
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isAnimating && Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			long now = SystemClock.uptimeMillis();
			if (startAnimateTime == 0) {
				startAnimateTime = now;
			}
			
			long passedTime = now-startAnimateTime;
			float percentage = passedTime > animateDuration ? 1f : (passedTime/(float)animateDuration);
			
			int hour = (int) (animateFromHour + (animateToHour-animateFromHour)*percentage);
			int minute = (int) (animateFromMinute + (animateToMinute-animateFromMinute)*percentage);
			currentHour = normalizeHour(hour);
			currentMinute = normalizeMinute(minute);
			
			if (passedTime >= animateDuration) {
				isAnimating = false;
			}
			
			postInvalidateDelayed(DEFAULT_FRAME_DELAY);
		}
		
		float hourDegree = hourToDegree(currentHour, currentMinute);
		float minuteDegree = minuteToDegree(currentMinute);
		
		int px = getWidth()/2;
		int py = getHeight()/2;
		
		int saveCount = canvas.save();
		canvas.rotate(hourDegree, px, py);
		hourDrawable.draw(canvas);
		canvas.restoreToCount(saveCount);
		saveCount = canvas.save();
		canvas.rotate(minuteDegree, px, py);
		minuteDrawable.draw(canvas);
		canvas.restoreToCount(saveCount);
	}
	
	public int normalizeHour(int hour) {
		hour = hour%12;
		if (hour < 0) {
			hour = 12+hour;
		}
		return hour;
	}
	
	public int normalizeMinute(int minute) {
		minute = minute%60;
		if (minute < 0) {
			minute = 60+minute;
		}
		return minute;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(getBackground().getIntrinsicWidth(), getBackground().getIntrinsicHeight());
		int halfWidth = getMeasuredWidth()/2;
		int halfHeight = getMeasuredHeight()/2;
		int left = halfWidth;
		int bottom = halfHeight;
		hourDrawable.setBounds(left, bottom-hourDrawable.getIntrinsicHeight(), left+hourDrawable.getIntrinsicWidth(), halfHeight);
		minuteDrawable.setBounds(left, bottom-minuteDrawable.getIntrinsicHeight(), left+minuteDrawable.getIntrinsicWidth(), halfHeight);
	}
	
	private float hourToDegree(int hour, int minute) {
		return (hour+minute/60f)/12f*360;
	}
	
	private float minuteToDegree(int minute) {
		return minute/60f*360;
	}

}
