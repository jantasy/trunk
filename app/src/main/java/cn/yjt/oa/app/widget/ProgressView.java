package cn.yjt.oa.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import cn.yjt.oa.app.R;

public class ProgressView extends View {

	public static enum Style {
		SMALL, MEDIUM, LARGE
	}

	private static final int EFFECT_SPEED = 5;
	private int max;
	private int progress;
	private RectF oval;
	private RectF waitingOval;
	private Paint backgroundPaint;
	private Paint progressPaint;
	private float effectAngle;
	private float strokeWidth;
	private float width;
	private float height;
	private Paint textPaint;
	private Rect bounds;
	private int effectSpeed;
	private boolean isWaiting;

	public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressView,
                defStyleAttr, 0);
		int style = a.getInt(a.getIndex(R.styleable.ProgressView_style), 1);
		if(style == 0){
			setStyle(Style.SMALL);
		}else if(style == 1){
			setStyle(Style.MEDIUM);
		}else if(style == 2){
			setStyle(Style.LARGE);
		}
		
		int effectSpeed = a.getInt(a.getIndex(R.styleable.ProgressView_effect_speed), 5);
		setEffectSpeed(effectSpeed);
		a.recycle();
	}

	public ProgressView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ProgressView(Context context) {
		this(context, null);
	}

	private void init(Context context) {

		backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		backgroundPaint.setColor(Color.parseColor("#F0F0F0"));
		backgroundPaint.setStyle(Paint.Style.STROKE);
		backgroundPaint.setStrokeCap(Cap.ROUND);

		progressPaint = new Paint(backgroundPaint);
		progressPaint.setColor(Color.parseColor("#078eed"));

		textPaint = new Paint(progressPaint);

		textPaint.setStyle(Paint.Style.FILL);
		textPaint.setTextAlign(Align.CENTER);
		
		effectAngle = -90;
		bounds = new Rect();
		
		effectSpeed = EFFECT_SPEED;
		setStyle(Style.MEDIUM);
	}

	private float applyDimension(float dimen) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimen,
				getResources().getDisplayMetrics());
	}

	private void configDimentions(float textDimen, float ovalDimen,
			float strokeDimen) {
		textPaint.setTextSize(applyDimension(textDimen));
		oval = new RectF(0, 0, applyDimension(ovalDimen), applyDimension(ovalDimen));
		waitingOval = new RectF(0, 0, applyDimension(ovalDimen/4f), applyDimension(ovalDimen/4f));
		backgroundPaint.setStrokeWidth(applyDimension(strokeDimen));
		progressPaint.setStrokeWidth(applyDimension(strokeDimen));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		drawBackground(canvas);

		drawProgress(canvas);
		
		if(isWaiting){
			drawWaiting(canvas);
		}else{
			drawText(canvas);
		}

		postInvalidateDelayed(25);

	}

	private void drawText(Canvas canvas) {
		int save;
		save = canvas.save();
		String text = String.valueOf((int) (100f * progress / max))+"%";
		textPaint.getTextBounds(text, 0, text.length(), bounds);
		canvas.drawText(text, this.width / 2f, this.height / 2f
				+ (bounds.bottom - bounds.top) / 2f, textPaint);
		canvas.restoreToCount(save);
	}
	
	private void drawWaiting(Canvas canvas){
		int save;
		save = canvas.save();
		canvas.translate((this.width - this.waitingOval.right) / 2f,
				(this.height - this.waitingOval.bottom) / 2f);
		canvas.drawArc(waitingOval, - (effectAngle += effectSpeed), 270, false, backgroundPaint);
		canvas.restoreToCount(save);
	}

	private void drawProgress(Canvas canvas) {
		int save;
		save = canvas.save();
		canvas.translate((this.width - this.oval.right) / 2f,
				(this.height - this.oval.bottom) / 2f);
		canvas.drawArc(oval, effectAngle += effectSpeed, 360.0f * progress
				/ max, false, progressPaint);
		canvas.restoreToCount(save);
	}

	private void drawBackground(Canvas canvas) {
		int save;
		save = canvas.save();
		canvas.translate((this.width - this.oval.right) / 2f,
				(this.height - this.oval.bottom) / 2f);
		canvas.drawArc(oval, 0, 360, false, backgroundPaint);
		canvas.restoreToCount(save);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (widthMode == MeasureSpec.AT_MOST) {
			this.width = oval.right + 2 * strokeWidth;

			widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) this.width,
					MeasureSpec.EXACTLY);
		} else {
			this.width = MeasureSpec.getSize(widthMeasureSpec);
		}
		if (heightMode == MeasureSpec.AT_MOST) {
			this.height = oval.bottom + 2 * strokeWidth;

			heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) this.height,
					MeasureSpec.EXACTLY);
		} else {
			this.height = MeasureSpec.getSize(heightMeasureSpec);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void setMax(int max) {
		this.max = max;
	}

	public void setProgress(int progress) {
		this.progress = progress;
		postInvalidate();
	}
	
	public void setWaiting(boolean waiting) {
		this.isWaiting = waiting;
		postInvalidate();
	}

	public void setStyle(Style style) {
		if (style == Style.SMALL) {
			configDimentions(6, 30, 2);
		} else if (style == Style.MEDIUM) {
			configDimentions(9, 40, 2.7f);
		} else if (style == Style.LARGE) {
			configDimentions(12, 60, 4);
		}
	}
	
	public void setEffectSpeed(int speed){
		this.effectSpeed = speed;
	}
	
}
