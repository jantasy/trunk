package cn.yjt.oa.app.push;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;

public class DoubleTapFragment extends Fragment {
	
	private static final String TAG = DoubleTapFragment.class.getSimpleName();
	
	private View doubleTapView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	protected void onDoubleTap(View view) {

	}

	protected void listenDoubleTapView(View view) {
		doubleTapView = view;
		final GestureDetector detector = new GestureDetector(getActivity(),new DoubleTapListener(doubleTapView));
//		detector.setOnDoubleTapListener(new DoubleTapListener(doubleTapView));
		
		doubleTapView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				/*记录操作 1202*/
				OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_WIDGET_REMIND);

				detector.onTouchEvent(event);
				return true;
			}
		});
	}

	private class DoubleTapListener extends SimpleOnGestureListener {
		private View view;
		
		public DoubleTapListener(View view) {
			super();
			this.view = view;
		}

		@Override
		public boolean onDoubleTap(MotionEvent event) {
			try{
				DoubleTapFragment.this.onDoubleTap(view);
			}catch(Exception e){
				Log.w(TAG, e.getMessage(),e);
			}
			return true;
		}
		
	}
}
