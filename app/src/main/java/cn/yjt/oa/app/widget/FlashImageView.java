package cn.yjt.oa.app.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FlashImageView extends ImageView implements Runnable {

	private float speed = 1f;

	private float alpha;
	private boolean toLighter = true;

	public FlashImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FlashImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FlashImageView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// long thisDrawTime = System.currentTimeMillis();
		// if(lastDrawTime == 0){
		// lastDrawTime = thisDrawTime;
		// }
		// long passTime = thisDrawTime - lastDrawTime;
		// lastDrawTime = thisDrawTime;
		// System.out.println("passTime:"+passTime);
		// if(toLighter){
		// alpha = (int) (alpha+ speed*passTime/1000);
		// }else{
		// alpha = (int) (alpha - speed*passTime/1000);
		// }
		// if(alpha >= 255){
		// alpha =255;
		// toLighter = false;
		// }else if(alpha <= 0){
		// alpha = 0;
		// toLighter = true;
		// }
		// postDelayed(this, 25);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		post(this);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void run() {
		if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {

			setAlpha(alpha);
			if (toLighter) {
				alpha += 0.01f;
			} else {
				alpha -= 0.01f;
			}
			if (alpha >= 1) {
				toLighter = false;
			} else if (alpha <= 0) {
				toLighter = true;
			}
			postDelayed(this, 5);
		}
	}

}
