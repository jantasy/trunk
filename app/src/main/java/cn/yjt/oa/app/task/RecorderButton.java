package cn.yjt.oa.app.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import cn.yjt.oa.app.R;

public class RecorderButton extends View {

	public static interface OnPressListener {
		public void onDown(Mode mode);

		public void onUp(Mode mode);
	}

	public static interface ProgressCallback {
		public float getProgress();
	}

	public static interface OnModeChangedListener {
		public void onChanged(Mode mode);
	}

	public static enum Mode {
		RECORD, RECORDING, PLAY, PLAYING
	}

	private Paint progressPaint;
	private Paint progressTextPaint;
	private RectF progressOval;
	private Mode mode = Mode.RECORD;
	private float progress;
	private float max;

	private boolean isPressing;
	private BitmapDrawable circle;
	private BitmapDrawable circleHighlight;
	private BitmapDrawable circleProgress;
	private BitmapDrawable circleCenterNormal;
	private BitmapDrawable circleCenterPressing;
	private BitmapDrawable mic;
	private BitmapDrawable micHighlight;
	private BitmapDrawable play;
	private BitmapDrawable stop;

	private OnPressListener listener;
	private ProgressCallback callback;
	private OnModeChangedListener onModeChangedListener;
	
	private long audioLength;

	Rect bounds = new Rect();

	public RecorderButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public RecorderButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public RecorderButton(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {

		circle = (BitmapDrawable) context.getResources().getDrawable(
				R.drawable.task_speech_circle);
		circle.setBounds(0, 0, circle.getBitmap().getWidth(), circle
				.getBitmap().getHeight());
		circleHighlight = (BitmapDrawable) context.getResources().getDrawable(
				R.drawable.task_speech_circle_highlight);
		circleHighlight.setBounds(0, 0, circleHighlight.getBitmap().getWidth(),
				circleHighlight.getBitmap().getHeight());
		circleProgress = (BitmapDrawable) context.getResources().getDrawable(
				R.drawable.task_speech_progresscircle);
		circleProgress.setBounds(0, 0, circleHighlight.getBitmap().getWidth(),
				circleProgress.getBitmap().getHeight());

		circleCenterNormal = (BitmapDrawable) context.getResources()
				.getDrawable(R.drawable.task_speech_center);
		circleCenterNormal.setBounds(0, 0, circleCenterNormal.getBitmap()
				.getWidth(), circleCenterNormal.getBitmap().getHeight());
		circleCenterPressing = (BitmapDrawable) context.getResources()
				.getDrawable(R.drawable.task_speech_center_pressing);
		circleCenterPressing.setBounds(0, 0, circleCenterPressing.getBitmap()
				.getWidth(), circleCenterPressing.getBitmap().getHeight());

		mic = (BitmapDrawable) context.getResources().getDrawable(
				R.drawable.task_speech_mic);
		mic.setBounds(0, 0, mic.getBitmap().getWidth(), mic.getBitmap()
				.getHeight());
		micHighlight = (BitmapDrawable) context.getResources().getDrawable(
				R.drawable.task_speech_mic_highlight);
		micHighlight.setBounds(0, 0, micHighlight.getBitmap().getWidth(),
				micHighlight.getBitmap().getHeight());

		play = (BitmapDrawable) context.getResources().getDrawable(
				R.drawable.task_speech_play);
		play.setBounds(0, 0, play.getBitmap().getWidth(), play.getBitmap()
				.getHeight());
		stop = (BitmapDrawable) context.getResources().getDrawable(
				R.drawable.task_speech_stop);
		stop.setBounds(0, 0, stop.getBitmap().getWidth(), stop.getBitmap()
				.getHeight());

		progressPaint = new Paint();
		progressPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		progressOval = new RectF(0, 0, circleProgress.getBitmap().getWidth(),
				circleProgress.getBitmap().getHeight());
		BitmapShader shader = new BitmapShader(circleProgress.getBitmap(),
				TileMode.REPEAT, TileMode.REPEAT);
		progressPaint.setShader(shader);

		progressTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		progressTextPaint.setColor(Color.parseColor("#4e6278"));
		progressTextPaint.setTextAlign(Align.CENTER);

		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 11,
				getResources().getDisplayMetrics());
		progressTextPaint.setTextSize(px);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		BitmapDrawable drawable = (BitmapDrawable) circleProgress;
		Bitmap bmp = drawable.getBitmap();
		int width = bmp.getWidth();
		int height = bmp.getHeight();

		super.onMeasure(
				MeasureSpec.makeMeasureSpec(width,
						MeasureSpec.getMode(widthMeasureSpec)),
				MeasureSpec.makeMeasureSpec(height,
						MeasureSpec.getMode(heightMeasureSpec)));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		drawCircleCenter(canvas);
		drawCenter(canvas);
		drawCircle(canvas);
		drawProgressCircle(canvas);
		drawAudioLength(canvas);
	}

	private void drawAudioLength(Canvas canvas) {
		if(mode == Mode.PLAY){
			String progressText = String.valueOf((int) audioLength / 1000 + "\"");
			int save = canvas.save();

			float x = circleProgress.getBitmap().getWidth() / 2;
			float y = circleProgress.getBitmap().getHeight() / 4 * 3;
			canvas.drawText(progressText, 0, progressText.length(), x, y,
					progressTextPaint);
			canvas.restoreToCount(save);
		}
	}

	private void drawCircle(Canvas canvas) {
		if (mode == Mode.RECORDING && isPressing) {
			int save = canvas.save();
			canvas.translate(calcStartX(circleHighlight.getBitmap()),
					calcStartY(circleHighlight.getBitmap()));
			circleHighlight.draw(canvas);
			canvas.restoreToCount(save);
		} else {
			int save = canvas.save();
			canvas.translate(calcStartX(circle.getBitmap()),
					calcStartY(circle.getBitmap()));
			circle.draw(canvas);
			canvas.restoreToCount(save);
		}
	}

	private void drawProgressCircle(Canvas canvas) {
		if (mode == Mode.PLAYING) {
			int save = canvas.save();
			canvas.rotate(progress / max * 360, progressOval.centerX(),
					progressOval.centerY());
			canvas.drawArc(progressOval, -76f - progress / max * 360, progress
					/ max * 360, true, progressPaint);
			canvas.restoreToCount(save);

			String progressText = String.valueOf((int) progress / 1000 + "\"");
			save = canvas.save();

			float x = circleProgress.getBitmap().getWidth() / 2;
			float y = circleProgress.getBitmap().getHeight() / 4 * 3;
			canvas.drawText(progressText, 0, progressText.length(), x, y,
					progressTextPaint);
			canvas.restoreToCount(save);

			postInvalidateDelayed(25);
		}
	}

	@Override
	public void postInvalidateDelayed(long delayMilliseconds) {
		if (mode == Mode.PLAYING) {

			if (callback != null) {
				progress = callback.getProgress();
			}
		}
		super.postInvalidateDelayed(delayMilliseconds);
	}

	private void drawCircleCenter(Canvas canvas) {
		if (mode == Mode.RECORDING && isPressing) {
			int save = canvas.save();
			canvas.translate(calcStartX(circleCenterPressing.getBitmap()),
					calcStartY(circleCenterPressing.getBitmap()));
			circleCenterPressing.draw(canvas);
			canvas.restoreToCount(save);
		} else {
			int save = canvas.save();
			canvas.translate(calcStartX(circleCenterNormal.getBitmap()),
					calcStartY(circleCenterNormal.getBitmap()));
			circleCenterNormal.draw(canvas);
			canvas.restoreToCount(save);
		}
	}

	private void drawCenter(Canvas canvas) {
		int save;
		if (mode == Mode.RECORD) {
			save = canvas.save();
			canvas.translate(calcStartX(mic.getBitmap()),
					calcStartY(mic.getBitmap()));
			mic.draw(canvas);
			canvas.restoreToCount(save);
		} else if (mode == Mode.RECORDING) {
			save = canvas.save();
			canvas.translate(calcStartX(micHighlight.getBitmap()),
					calcStartY(micHighlight.getBitmap()));
			micHighlight.draw(canvas);
			canvas.restoreToCount(save);
		} else if (mode == Mode.PLAY) {
			save = canvas.save();
			canvas.translate(calcStartX(play.getBitmap()),
					calcStartY(play.getBitmap()));
			play.draw(canvas);
			canvas.restoreToCount(save);
		} else if (mode == Mode.PLAYING) {
			save = canvas.save();
			canvas.translate(calcStartX(stop.getBitmap()),
					calcStartY(stop.getBitmap()));
			stop.draw(canvas);
			canvas.restoreToCount(save);
		}
	}

	private float calcStartX(Bitmap bmp) {
		return (circleProgress.getBitmap().getWidth() - bmp.getWidth()) / 2.0f;
	}

	private float calcStartY(Bitmap bmp) {
		return (circleProgress.getBitmap().getHeight() - bmp.getHeight()) / 2.0f;
	}

	public void setProgress(int progress) {
		this.progress = progress;
		postInvalidate();
	}

	public void setMax(int max) {
		this.max = max;
		postInvalidate();
	}

	public void setMode(Mode mode) {
		if (this.mode != mode) {
			this.mode = mode;
			postInvalidate();
			if (onModeChangedListener != null) {
				onModeChangedListener.onChanged(mode);
			}
		}
	}
	
	public void setAudioLength(long audioLength) {
		this.audioLength = audioLength;
	}

	public void setOnPressListener(OnPressListener listener) {
		this.listener = listener;
	}

	public void setProgressCallback(ProgressCallback callback) {
		this.callback = callback;
	}

	public void setOnModeChangedListener(OnModeChangedListener listener) {
		this.onModeChangedListener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (onDown()) {
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if (onUp()) {
				return true;
			}
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	private boolean onDown() {

		isPressing = true;
		invalidate();

		if (listener != null) {
			listener.onDown(mode);
		}
		return true;
	}

	private boolean onUp() {

		isPressing = false;
		invalidate();

		if (listener != null) {
			listener.onUp(mode);
		}
		return true;
	}

}
