package cn.yjt.oa.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;


public class LightOnOffFrameLayout extends FrameLayout {

	//---------------构造函数----------------
	public LightOnOffFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public LightOnOffFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LightOnOffFrameLayout(Context context) {
		super(context);
		init();
	}

	//--------------------------------------

	private boolean isLightOff; //是否处于变暗状态
	private boolean isLightAnimating; //是否正在执行变暗动画

	private long lightAnimateStartTime; //变暗动画开始时间
	private long animateDuration; //变暗动画持续时间

	private int lightCurrentAlpha; //当前透明度
	private int lightStartAlpha; //开始透明度
	private int lightTargetAlpha; //目标透明度

	//最大的透明度
	private static final int MAX_ALPHA = 125;

	private void init() {
	}

	//变暗
	public void lightOff(long duration) {
		if (!isLightOff) {
			//设置参数
			animateDuration = duration;
			isLightAnimating = true;
			isLightOff = true;
			lightAnimateStartTime = 0;
			lightStartAlpha = lightCurrentAlpha;
			lightTargetAlpha = MAX_ALPHA;
			//刷新界面
			postInvalidate();
		}
	}

	//变亮
	public void lightOn(long duration) {
		if (isLightOff) {
			//设置参数
			animateDuration = duration;
			isLightAnimating = true;
			isLightOff = false;
			lightAnimateStartTime = 0;
			lightStartAlpha = lightCurrentAlpha;
			lightTargetAlpha = 0;
			//刷新界面
			postInvalidate();
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (isLightAnimating) {
			long passedTime = 0;
			if (lightAnimateStartTime == 0) {
				lightAnimateStartTime = SystemClock.uptimeMillis();
			} else {
				passedTime = SystemClock.uptimeMillis() - lightAnimateStartTime;
			}

			if (passedTime > animateDuration)
				passedTime = animateDuration;

			lightCurrentAlpha = (int) (lightStartAlpha + (lightTargetAlpha - lightStartAlpha) * passedTime
					/ animateDuration);

			if (lightCurrentAlpha == lightTargetAlpha) {
				isLightAnimating = false;
			} else {
				postInvalidate();
			}
		}

		if (lightCurrentAlpha != 0) {
			int color = Color.argb(lightCurrentAlpha, 0, 0, 0);
			canvas.drawColor(color);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		//当初与变暗状态时，拦截所有的触摸事件
		if (isLightOff)
			return true;
		return super.onInterceptTouchEvent(ev);
	}
}
