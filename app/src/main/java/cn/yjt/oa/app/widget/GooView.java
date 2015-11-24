package cn.yjt.oa.app.widget;

import cn.yjt.oa.app.widget.utils.GeometryUtil;
import cn.yjt.oa.app.widget.utils.Utils;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

/**
 * 黏性控件
 * 
 * @author 熊岳岳
 * 
 */
//@SuppressLint("NewApi")
public class GooView extends View {

	private static final String TAG = "GOOVIEW";

	private Paint mPaint;

	/* 固定圆的圆心点 */
	private PointF mStickCenter;
	/* 固定圆的半径 */
	private float mStickRadius;
	/* 拖拽圆的圆心点 */
	private PointF mDragCenter;
	/* 拖拽圆的半径 */
	private float mDragRadius;
	/* 固定圆的两个附着点 */
	private PointF mStickPoints[];
	/* 拖拽圆的两个附着点 */
	private PointF mDragPoints[];
	/* 中心控制点 */
	private PointF mControlPoint;

	/* 顶部任务栏的高度 */
	private float mStateBarHeight;
	/* 拖动的最大距离 */
	private float mFarestDistance;

	/* 上下文 */
	private Context mContext;
	/* 中心显示的消息数量 */
	private int mCount = 0;

	/* 拖动是否超出界限 */
	private boolean isOutOfRange = false;
	/* 绘制图案是否应是消失状态 */
	private boolean isDisapper = false;
	/* 是否响应触摸事件 */
	private boolean isCanTouch = true;

	/* 构造方法 */
	public GooView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	/* 构造方法 */
	public GooView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
	}

	/* 构造方法 */
	public GooView(Context context) {
		super(context);
		this.mContext = context;
		// 初始化一个抗锯齿的画笔
		init(context);
	}

	// 初始化画笔,圆,最大距离
	private void init(Context context) {
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(Color.RED);
		mStickCenter = new PointF(Utils.dip2Dimension(75, context),
				Utils.dip2Dimension(520, context));
		mDragCenter = new PointF(Utils.dip2Dimension(75, context),
				Utils.dip2Dimension(520, context));
		mStickRadius = Utils.dip2Dimension(
				5 + String.valueOf(mCount).length() * 2, context);
		mDragRadius = Utils.dip2Dimension(
				5 + String.valueOf(mCount).length() * 2, context);
		mFarestDistance = Utils.dip2Dimension(60, context);
	}
	
	public void init(){
		init(mContext);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		// 计算链接部件
		float xOffset = mStickCenter.x - mDragCenter.x;
		float yOffset = mStickCenter.y - mDragCenter.y;
		Double linek = null;
		if (xOffset != 0) {
			linek = (double) (yOffset / xOffset);
		} else {
			linek = Double.MAX_VALUE;
		}
		// 计算固定圆的半径（根据两圆圆心的距离）
		float tempStickRadius = getTempStickRadius();
		// 根据圆心半径和角度计算过该圆的直线与圆的交点
		mStickPoints = GeometryUtil.getIntersectionPoints(mStickCenter,
				tempStickRadius, linek);
		mDragPoints = GeometryUtil.getIntersectionPoints(mDragCenter,
				mDragRadius, linek);
		// 获取控制点
		mControlPoint = GeometryUtil.getMiddlePoint(mStickCenter, mDragCenter);

		// 保存移动之前画布的位置，平移画布
		canvas.save();
		canvas.translate(0, -mStateBarHeight);
		if (!isDisapper) {
			mPaint.setColor(Color.RED);
			// 画拖拽圆
			canvas.drawCircle(mDragCenter.x, mDragCenter.y, mDragRadius, mPaint);
			if (!isOutOfRange) {
				// 画出中间连接部分
				Path path = new Path();
				path.moveTo(mStickPoints[0].x, mStickPoints[0].y);
				path.quadTo(mControlPoint.x, mControlPoint.y, mDragPoints[0].x,
						mDragPoints[0].y);
				path.lineTo(mDragPoints[1].x, mDragPoints[1].y);
				path.quadTo(mControlPoint.x, mControlPoint.y,
						mStickPoints[1].x, mStickPoints[1].y);
				path.close();
				canvas.drawPath(path, mPaint);
				// 画固定圆
				canvas.drawCircle(mStickCenter.x, mStickCenter.y,
						tempStickRadius, mPaint);

				mPaint.setColor(Color.WHITE);
				if (mCount != 0) {
					mPaint.setTextSize(Utils.dip2Dimension(9, mContext));
					canvas.drawText(mCount + "", mDragCenter.x - mDragRadius
							/ 2, mDragCenter.y + mDragRadius / 2, mPaint);
				}
			}
		} else {
			this.setVisibility(View.GONE);
		}

		// 恢复画布
		canvas.restore();
	}

	// 获取显示的固定圆的半径
	private float getTempStickRadius() {
		// 获取两圆圆心的距离
		float distance = GeometryUtil.getDistanceBetween2Points(mStickCenter,
				mDragCenter);

		// 让distance不大于80f
		distance = Math.min(distance, mFarestDistance);
		// 0->80f>>>
		float percent = distance / mFarestDistance;
		System.out.println("percent" + percent);

		// 半径12f ->4f
		return evaluate(percent, mStickRadius, mStickRadius * 0.33f);
	}

	public Float evaluate(float fraction, Number startValue, Number endValue) {
		float startFloat = startValue.floatValue();
		return startFloat + fraction * (endValue.floatValue() - startFloat);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
	public boolean onTouchEvent(MotionEvent event) {
		float x1 = mStickCenter.x - 2 * mStickRadius;
		float x2 = mStickCenter.x + 2 * mStickRadius;
		float y1 = mStickCenter.y - 2 * mStickRadius;
		float y2 = mStickCenter.y + 2 * mStickRadius;
		float rawX;
		float rawY;
		if(isCanTouch){
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				rawX = event.getX();
				rawY = event.getY();
				if (x1 < rawX && rawX < x2 && y1 < rawY && rawY < y2) {
					Log.i(GooView.TAG, "在圆点内");
					updateDragCenter(rawX, rawY);
					break;
				} else {
					return false;
				}
			case MotionEvent.ACTION_MOVE:

				rawX = event.getX();
				rawY = event.getY();

				float distance = GeometryUtil.getDistanceBetween2Points(
						mStickCenter, mDragCenter);
				if (distance > mFarestDistance) {
					isOutOfRange = true;
				}

				if (isCanTouch) {
					updateDragCenter(rawX, rawY);
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				if (isOutOfRange) {
					float d = GeometryUtil.getDistanceBetween2Points(mStickCenter,
							mDragCenter);
					if (d > mFarestDistance) {
						// 拖拽超出范围，断开，松手，消失
						isDisapper = true;
						isOutOfRange = false;
						if (mListener != null) {
							mListener.updataData();
							Log.i(GooView.TAG, "消失事件触发");
						}
						invalidate();
					} else {
						// 拖拽超出范围，断开，松手放回去
						updateDragCenter(mStickCenter.x, mStickCenter.y);
						isOutOfRange = false;
					}
				} else {
					// 拖拽没有超出范围，松手，弹回去
					ValueAnimator mAnim = ValueAnimator.ofFloat(1.0f);
					mAnim.addUpdateListener(new AnimatorUpdateListener() {

						@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
                        @Override
						public void onAnimationUpdate(ValueAnimator animation) {
							float percent = animation.getAnimatedFraction();
							PointF p = GeometryUtil.getPointByPercent(mDragCenter,
									mStickCenter, percent);
							updateDragCenter(p.x, p.y);
						}
					});

					mAnim.setDuration(500);
					mAnim.setInterpolator(new OvershootInterpolator(3));
					mAnim.start();

				}
				break;

			default:
				break;
			}
			return true;
		}else{
			return false;
		}
		
	}

	/* 设置拖拽圆的圆心 */
	private void updateDragCenter(float rawX, float rawY) {
		mDragCenter.set(rawX, rawY);
		invalidate();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// mStateBarHeight = Utils.getStatusBarHeight(this);
		mStateBarHeight = 0;
	}

	// ----------回调接口-----------------
	private onGooViewDispperListener mListener;

	public interface onGooViewDispperListener {

		public void updataData();
	}

	/**
	 * 注册小圆点消失的监听事件
	 * 
	 * @param listener
	 */
	public void setOnGooViewDispperListener(onGooViewDispperListener listener) {
		this.mListener = listener;
	}

	// --------------------------------

	// ---------set、get方法-------------
	public int getmCount() {
		return mCount;
	}

	public void setmCount(int mCount) {
		this.mCount = mCount;
	}

	public boolean isDisapper() {
		return isDisapper;
	}

	public void setDisapper(boolean isDisapper) {
		this.isDisapper = isDisapper;
	}

	public boolean isCanTouch() {
		return isCanTouch;
	}

	public void setCanTouch(boolean isCanTouch) {
		this.isCanTouch = isCanTouch;
	}
	// --------------------------------
}
