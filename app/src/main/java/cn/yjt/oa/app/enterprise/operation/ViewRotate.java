package cn.yjt.oa.app.enterprise.operation;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.animation.Rotate3DAnimation;

public class ViewRotate{
	private View convertView;
	private ViewGroup mContainer;
	private View frontView;
	private View backView;
	private int duration;

	public ViewRotate(View convertView,View frontView,View backView,int duration) {
		this.convertView = convertView;
		this.frontView=frontView;
		this.backView=backView;
		this.duration=duration;
		initView();
	}

	private void initView() {
		mContainer = (ViewGroup) convertView.findViewById(R.id.container);
		
		
        // Since we are caching large views, we want to keep their cache
        // between each animation
        mContainer.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
		
	}

	
	public void applyRotation(int position, float start, float end) {

	        // Create a new 3D rotation with the supplied parameter
	        // The animation listener is used to trigger the next animation
	        final Rotate3DAnimation rotation =
	        		new Rotate3DAnimation(mContainer);
	        rotation.setFromDegrees(start);
	        rotation.setToDegrees(end);
	        rotation.setDuration(duration);
	        rotation.setFillAfter(true);
	        rotation.setInterpolator(new AccelerateInterpolator());
	        rotation.setAnimationListener(new DisplayNextView(position));

	        mContainer.startAnimation(rotation);
	    }
	 
	 /**
	     * This class listens for the end of the first half of the animation.
	     * It then posts a new action that effectively swaps the views when the container
	     * is rotated 90 degrees and thus invisible.
	     */
	    private final class DisplayNextView implements Animation.AnimationListener {
	        private final int mPosition;

	        private DisplayNextView(int position) {
	            mPosition = position;
	        }

	        public void onAnimationStart(Animation animation) {
	        }

	        public void onAnimationEnd(Animation animation) {
	            mContainer.post(new SwapViews(mPosition));
	        }

	        public void onAnimationRepeat(Animation animation) {
	        }
	    }
	    
	    private final class SwapViews implements Runnable {
	        private final int mPosition;

	        public SwapViews(int position) {
	            mPosition = position;
	        }

	        public void run() {
	            Rotate3DAnimation rotation;
	            
	            if (mPosition > -1) {
	            	frontView.setVisibility(View.GONE);
	            	backView.setVisibility(View.VISIBLE);
	            	backView.requestFocus();
	                rotation = new Rotate3DAnimation(mContainer);
	                rotation.setFromDegrees(270);
	                rotation.setToDegrees(360);
	            } else {
	            	backView.setVisibility(View.GONE);
	            	frontView.setVisibility(View.VISIBLE);
	            	frontView.requestFocus();

	                rotation = new Rotate3DAnimation(mContainer);
	                rotation.setFromDegrees(90);
	                rotation.setToDegrees(0);
	            }

	            rotation.setDuration(duration);
	            rotation.setFillAfter(true);
	            rotation.setInterpolator(new DecelerateInterpolator());

	            mContainer.startAnimation(rotation);
	        }
	    }

	

}