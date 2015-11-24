package cn.yjt.oa.app.animation;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class Rotate3DAnimation extends Animation {
	private float fromDegrees;
	private float toDegrees;
	private float centerX;
	private float centerY;
	private boolean reverse;
	private Camera mCamera;
	private View view;

	public Rotate3DAnimation(View view) {
		this.view = view;
		centerX = view.getWidth() / 2f;
		centerY = view.getHeight() / 2f;
	}
	

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mCamera = new Camera();
	}
	
	public void setView(View view){
		if(this.view != view){
		centerX = view.getWidth() / 2f;
		centerY = view.getHeight() / 2f;
		}
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		final float fromDegrees = this.getFromDegrees();
		float degrees = fromDegrees
				+ ((getToDegrees() - fromDegrees) * interpolatedTime);

		final float centerX = this.centerX;
		final float centerY = this.centerY;
		final Camera camera = mCamera;

		final Matrix matrix = t.getMatrix();

		camera.save();
		float z = 0;
		z = (float) (Math.abs(Math.sin(degrees / 180 * Math.PI)) * centerX);
		if (isReverse()) {
			camera.translate(0, 0, z);
		} else {
			camera.translate(0, 0, z);
		}
		camera.rotateY(degrees);
		camera.getMatrix(matrix);
		camera.restore();

		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
	}

	public float getFromDegrees() {
		return fromDegrees;
	}

	public void setFromDegrees(float fromDegrees) {
		this.fromDegrees = fromDegrees;
	}

	public float getToDegrees() {
		return toDegrees;
	}

	public void setToDegrees(float toDegrees) {
		this.toDegrees = toDegrees;
	}

	public boolean isReverse() {
		return reverse;
	}

	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}
}
