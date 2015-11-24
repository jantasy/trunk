package cn.yjt.oa.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.ViewConfiguration;
import android.widget.ImageView;

public class WaveImageView extends ImageView {
	
	public static final int SHAPE_TYPE_CIRCLE = 0;
	public static final int SHAPE_TYPE_RECT = 1;
	private int waveCount = 3;
	private int waveShape = SHAPE_TYPE_CIRCLE;
	private float initSpeed;
	private int initWaveDistance;
	private int waveInterval;
	private int minWaveDistance;
	private int repeatCount = 1;
	private int userInitedRepearCount = 1;
	private boolean animating;
	private long animateBeginTime;
	private Paint paint = new Paint();
	private Rect sharedRect = new Rect();

	public WaveImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public WaveImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public WaveImageView(Context context) {
		super(context);
		init();
	}

	private void init() {
		setScaleType(ScaleType.CENTER);
		setWillNotDraw(false);
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(9);
		paint.setStyle(Style.STROKE);
		paint.setAntiAlias(true);
		initWaveDistance = ViewConfiguration.get(getContext()).getScaledTouchSlop()*2;
		minWaveDistance = ViewConfiguration.get(getContext()).getScaledTouchSlop()/2;
		initSpeed = ViewConfiguration.get(getContext()).getScaledTouchSlop()/(float)200;
		waveInterval = (int) (initWaveDistance/initSpeed);
	}
	
	/**
	 * 设置波纹颜色
	 * @param speed
	 */
	public void setWaveColor(int color) {
		paint.setColor(color);
	}
	
	/**
	 * 设置波纹移动速度
	 * @param speed
	 */
	public void setInitSpeed(float speed) {
		initSpeed = speed;
	}
	
	public void setAnimateRepeatCount(int count) {
		userInitedRepearCount = count;
	}
	
	/**
	 * 设置初始化时两个波纹的距离，此距离应该大于最小波纹距离
	 * @param distance
	 */
	public void setInitWaveDistance(int distance) {
		initWaveDistance = distance;
	}
	
	/**
	 * 设置波纹的形状, 设置{@link #SHAPE_TYPE_CIRCLE}, {@link #SHAPE_TYPE_RECT}
	 * @param shape
	 */
	public void setWaveShape(int shape) {
		waveShape = shape;
	}
	
	/**
	 * 设置绘制wave的线宽
	 * @param width
	 */
	public void setWaveShapeStrokeWidth(int width) {
		paint.setStrokeWidth(width);
	}
	
	/**
	 * 设置两个波纹的最小间距
	 * @param distance
	 */
	public void setMinWaveDistance(int distance) {
		minWaveDistance = distance;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (animating) {
			long now = SystemClock.uptimeMillis();
			if (animateBeginTime == 0)
				animateBeginTime = now;
			Drawable d = getDrawable();
			if (d != null) {
				int l = (getWidth()-d.getIntrinsicWidth())/2;
				int t = (getHeight()-d.getIntrinsicHeight())/2;
				int maxDistance = l;
				/**
				 * v+at=0;
				 * v+0.5at^2=maxDistance;
				 * 当移动到某处时速度正好为0。
				 * 当前是移动到最大值的80%时停止
				 */
				float accelerateFactor = -(initSpeed*initSpeed)/(2*(maxDistance*.8f));
				Rect rc = sharedRect;
				rc.set(l, t, l+d.getIntrinsicWidth(), t+d.getIntrinsicHeight());
				/**
				 * 减速度下两条波纹会拉近距离，现在已知两条波纹的最小距离，则能够算出达到最小距离时的最大位移或最大位移时间
				 * d1=vt+0.5at^2;
				 * d2=v*(t-waveInterval)+0.5a(t-waveInterval)^2;
				 * d1-d2=minWaveDistance;
				 */
				long maxPassedTime = (long) ((minWaveDistance-initSpeed*waveInterval+0.5f*accelerateFactor*waveInterval*waveInterval)/(accelerateFactor*waveInterval));
				boolean finished = true;
				for (int i=0; i<waveCount; ++i) {
					long delayedTime = waveInterval*i;
					long passedTime = now - delayedTime - animateBeginTime;
					if (passedTime >= 0 && passedTime <= maxPassedTime) {
						float v = initSpeed+passedTime*accelerateFactor;
						if (v >= 0) {
							int movedDistance = (int) (initSpeed * passedTime+0.5f*accelerateFactor*passedTime*passedTime);
							if (movedDistance < maxDistance && movedDistance >= 0) {
								rc.inset(-movedDistance, -movedDistance);
								paint.setAlpha((int) ((1f-passedTime/(float)maxPassedTime)*255));
								switch (waveShape) {
								case SHAPE_TYPE_CIRCLE: {
									canvas.drawCircle(rc.exactCenterX(), rc.exactCenterY(), rc.width()/2f, paint);
								}
								break;
								case SHAPE_TYPE_RECT: {
									canvas.drawRect(rc, paint);
								}
								break;
								default: break;
								}
								rc.inset(movedDistance, movedDistance);
								finished = false;
							}
						}
						
					}
				}
				if (finished) {
					if (repeatCount > 0) {
						repeatCount--;
					} else if (repeatCount == 0) {
						animating = false;
					}
					
					animateBeginTime = 0;
				}
			}
			
			postInvalidateDelayed(10);
		}
	}
	
	public void startAnimate() {
		if (animating)
			return;
		animating = true;
		animateBeginTime = 0;
		repeatCount = userInitedRepearCount;
		invalidate();
	}
	
	public void stopAnimate() {
		if (!animating)
			return;
		animating = false;
		animateBeginTime = 0;
		invalidate();
	}
	
	public boolean isAnimating() {
		return animating;
	}
}
