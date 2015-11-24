package io.luobo.launcher.floatwidget;

import io.luobo.common.utils.MyThreadUtils;
import io.luobo.launcher.floatwidget.aidl.IFloatWidgetService;
import io.luobo.launcher.floatwidget.aidl.ReflectAction;

import java.util.HashMap;
import java.util.Locale;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.RemoteViews;

public class FloatWidgetService extends Service {

	
	class FloatWidgetManager extends IFloatWidgetService.Stub {

		@Override
		public synchronized int allocWidgetId() throws RemoteException {
			return mNextWidgetId++;
		}

		@Override
		public void updateViews(final int widgetId, final int gravity, final int x, final int y, final RemoteViews views)
				throws RemoteException {
			runOnMainThread(new Runnable() {
				
				@Override
				public void run() {
					FloatWidgetService.this.updateViews(widgetId, gravity, x, y, views);
				}
			});
		}
		
		@Override
		public void updateViewsParams(final int widgetId, final LayoutParams params,
				final RemoteViews views) throws RemoteException {
			runOnMainThread(new Runnable() {
				
				@Override
				public void run() {
					FloatWidgetService.this.updateViews(widgetId, params, views);
				}
			});
		}
		
		@Override
		public void deleteWidget(int widgetId) throws RemoteException {
			FloatWidgetService.this.deleteWidget(widgetId);
		}

		@Override
		public void showWidget(int widgetId, boolean show)
				throws RemoteException {
			FloatView container = null;
			synchronized (mContainers) {
				container = mContainers.get(widgetId);
			}
			if (container != null)
				showWidget(container, show);
		}
		
		private void showWidget(final FloatView container, final boolean show) {
			if (container == null) return;
			Runnable setVisibility = new Runnable() {
				
				@Override
				public void run() {
					View v = container.getChildAt(0);
					boolean requestAccepted = true;
					if (v != null && v instanceof FloatWidgetView) {
						if (!show)
							requestAccepted = ((FloatWidgetView)v).requestClose();
					}
					if (requestAccepted) {
						if (show) {
							container.setVisibility(View.VISIBLE);
						} else {
							container.setVisibility(View.GONE);
						}
					}
				}
			};
			if (MyThreadUtils.isOnMainThread()) {
				setVisibility.run();
			} else {
				MyThreadUtils.getMainThreadHandler().post(setVisibility);
			}
		}

		@Override
		public void updateViewsByLayout(final int widgetId, final int layoutId,
				final LayoutParams params) throws RemoteException {
			updateViewsAndVisibilityByLayout(widgetId, layoutId, true, params);
		}
		
		@Override
		public void updateViewsAndVisibilityByLayout(final int widgetId,
				final int layoutId, final boolean show, final LayoutParams params)
				throws RemoteException {
			runOnMainThread(new Runnable() {
				
				@Override
				public void run() {
					FloatWidgetService.this.updateViewsByLayout(widgetId, layoutId, params, show);
				}
			});
		}
		
		private void runOnMainThread(Runnable action) {
			if (MyThreadUtils.isOnMainThread()) {
				action.run();
			} else {
				MyThreadUtils.getMainThreadHandler().post(action);
			}
		}

		@Override
		public void setWidgetMoveable(int widgetId, boolean moveable) throws RemoteException {
			FloatView container = null;
			synchronized (mContainers) {
				container = mContainers.get(widgetId);
			}
			if (container != null) {
				container.setTouchMoveable(moveable);
			}
		}

		@Override
		public void setViewVisibility(int widgetId, int viewId, int visibility)
				throws RemoteException {
			FloatView container = null;
			synchronized (mContainers) {
				container = mContainers.get(widgetId);
			}
			if (container != null) {
				container.setViewVisibility(viewId, visibility);
			}
		}

		@Override
		public int setShowingWidth(int widgetId, int width)
				throws RemoteException {
			FloatView container = null;
			synchronized (mContainers) {
				container = mContainers.get(widgetId);
			}
			if (container != null) {
				return container.setShowingChildWidth(width);
			}
			return -1;
		}

		@Override
		public int getMaxShowingWidth(int widgetId) throws RemoteException {
			FloatView container = null;
			synchronized (mContainers) {
				container = mContainers.get(widgetId);
			}
			if (container != null) {
				return container.getChildMaxWidth();
			}
			return -1;
		}

		@Override
		public void setReflectAction(int widgetId, ReflectAction action) throws RemoteException {
			FloatView container = null;
			synchronized (mContainers) {
				container = mContainers.get(widgetId);
			}
			if (container != null) {
				action.doAction(container);
			}
		}

		@Override
		public void setMagnetic(int widgetId, boolean magnetic) throws RemoteException {
			FloatView container = null;
			synchronized (mContainers) {
				container = mContainers.get(widgetId);
			}
			if (container != null) {
				container.setMagnetic(magnetic);
			}
		}

		@Override
		public void setPositionPreferenceKey(int widgetId, String key) throws RemoteException {
			FloatView container = null;
			synchronized (mContainers) {
				container = mContainers.get(widgetId);
			}
			if (container != null) {
				container.setPositionPreferenceKey(key);
			}
		}

		@Override
		public boolean isWidgetShowing(int widgetId) throws RemoteException {
			FloatView container = null;
			synchronized (mContainers) {
				container = mContainers.get(widgetId);
			}
			return container != null && container.getVisibility() == View.VISIBLE;
		}

		@Override
		public void updateWindowBrightness(int widgetId, float brightness)
				throws RemoteException {
			FloatView container = null;
			synchronized (mContainers) {
				container = mContainers.get(widgetId);
			}
			if (container != null) {
				final View updateView = container;
				final LayoutParams params = (LayoutParams) container.getLayoutParams();
				params.screenBrightness = brightness;
				runOnMainThread(new Runnable() {
					
					@Override
					public void run() {
						mWndManager.updateViewLayout(updateView, params);
					}
				});
			}
		}

	}
	
	WindowManager mWndManager;
	LayoutInflater mLayoutInflator;
	static final LayoutParams DEFALUT_LAYOUTPARAM = new LayoutParams();
	static {
		DEFALUT_LAYOUTPARAM.type = LayoutParams.TYPE_SYSTEM_ALERT;
		DEFALUT_LAYOUTPARAM.flags = LayoutParams.FLAG_NOT_FOCUSABLE
				| LayoutParams.FLAG_LAYOUT_IN_SCREEN
				| LayoutParams.FLAG_LAYOUT_INSET_DECOR;
		DEFALUT_LAYOUTPARAM.format = PixelFormat.TRANSLUCENT;
		DEFALUT_LAYOUTPARAM.width = LayoutParams.WRAP_CONTENT;
		DEFALUT_LAYOUTPARAM.height = LayoutParams.WRAP_CONTENT;
	}
	
	private int mNextWidgetId = 1;
	
	private SparseArray<FloatView> mContainers = 
			new SparseArray<FloatView>();
	private HashMap<View, int[]> mPoints = 
			new HashMap<View, int[]>();
	
	private int mTouchSlop;
	
	private FloatWidgetManager mFloatWidgetManager = new FloatWidgetManager();
	
	public static LayoutParams getDefaultLayoutParams() {
		LayoutParams params = new LayoutParams();
		params.copyFrom(DEFALUT_LAYOUTPARAM);
		
		return params;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mFloatWidgetManager;
	}

	private int mScreenWidth;
	private int mScreenHeight;
	private int mOrientation;
	private int mVerticalPading;
	private Locale mLocale;
	
	private BroadcastReceiver mConfigrationReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Configuration configuration = context.getResources().getConfiguration();
			int newOrientation = configuration.orientation;
			if (mOrientation != newOrientation) {
				mOrientation = newOrientation;
				int count = mContainers.size();
				for (int i=0; i<count; ++i) {
					FloatView view = mContainers.valueAt(i);
					if (view != null) {
						view.onOrientationChanged();
					}
				}
			}
			
			Locale newLocale = configuration.locale;
			if (!mLocale.equals(newLocale)) {
				mLocale = newLocale;
				int count = mContainers.size();
				for (int i=0; i<count; ++i) {
					FloatView view = mContainers.valueAt(i);
					if (view != null) {
						view.onLocaleChanged();
					}
				}
			}
		}
	};
	
	private int getScreenWidth(){
		if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			return mScreenHeight;
		}
		
		return mScreenWidth;
	}
	
	private int getScreenHeight() {
		if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			return mScreenWidth;
		}
		
		return mScreenHeight;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		super.onCreate();
		mWndManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		mLayoutInflator = (LayoutInflater) getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
		Configuration configuration = getResources().getConfiguration();
		mOrientation = configuration.orientation;
		mLocale = configuration.locale;
		mVerticalPading = FloatWidgetPreference.getVerticalMagneticOffset(this);
		if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			mScreenWidth = mWndManager.getDefaultDisplay().getHeight();
			mScreenHeight = mWndManager.getDefaultDisplay().getWidth();
		} else {
			mScreenWidth = mWndManager.getDefaultDisplay().getWidth();
			mScreenHeight = mWndManager.getDefaultDisplay().getHeight();
		}
		IntentFilter filter = new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED);
		registerReceiver(mConfigrationReceiver, filter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mConfigrationReceiver);
	}
	
	public LayoutParams getLayoutParams(int gravity, int x, int y) {
		LayoutParams params = new LayoutParams();
		params.copyFrom(DEFALUT_LAYOUTPARAM);
		params.gravity = gravity;
		params.x = x;
		params.y = y;
		return params;
	}
	
	public LayoutParams getLayoutParams() {
		LayoutParams params = new LayoutParams();
		params.copyFrom(DEFALUT_LAYOUTPARAM);
		return params;
	}
	
	
	public void updateViews(int widgetId, int gravity, int x, int y, RemoteViews views) {
		updateViews(widgetId, getLayoutParams(gravity, x, y), views);
	}
	
	public void updateViews(int widgetId, LayoutParams params, RemoteViews views) {
		FloatView container = null;
		synchronized (mContainers) {
			container = mContainers.get(widgetId);
		}
		if (container == null) {
			container = new FloatView(this);
			synchronized (mContainers) {
				mContainers.put(widgetId, container);
			}
			View view = views.apply(this, container);
			if (view instanceof FloatWidgetView) {
				((FloatWidgetView)view).setWidgetInfo(mFloatWidgetManager, widgetId);
			}
			container.addView(view);
			mWndManager.addView(container, params);
		} else {
			if (container.getChildCount() == 1) {
				views.reapply(this, container.getChildAt(0));
			}
			
		}
	}
	
	public FloatView updateViewsByLayout(int widgetId, int layoutId,
			LayoutParams params, boolean show) {
		FloatView container = null;
		synchronized (mContainers) {
			container = mContainers.get(widgetId);
		}
		if (container == null) {
			container = new FloatView(this);
			synchronized (mContainers) {
				mContainers.put(widgetId, container);
			}
			View view = mLayoutInflator.inflate(layoutId, container, false);
			if (view instanceof FloatWidgetView) {
				((FloatWidgetView)view).setWidgetInfo(mFloatWidgetManager, widgetId);
			}
			container.addView(view);
			container.setLayoutId(layoutId);
			container.setVisibility(show ? View.VISIBLE : View.GONE);
			mWndManager.addView(container, params);
		} else {
			if (container.getChildCount() == 1) {
				if (Integer.valueOf(layoutId).equals(container.getLauoutId()) || layoutId == 0) {
					// layout not changed
				} else {
					container.removeAllViews();
					View view = mLayoutInflator.inflate(layoutId, container, false);
					if (view instanceof FloatWidgetView) {
						((FloatWidgetView)view).setWidgetInfo(mFloatWidgetManager, widgetId);
					}
					container.addView(view);
					container.setLayoutId(layoutId);
				}
			}
			container.setVisibility(show ? View.VISIBLE : View.GONE);
			if (params != null) {
				mWndManager.updateViewLayout(container, params);
			}
		}
		
		return container;
	}
	
	public void deleteWidget(int widgetId) {
		FloatView container = null;
		synchronized (mContainers) {
			container = mContainers.get(widgetId);
		}
		if (container != null) {
			synchronized (mContainers) {
				mContainers.remove(widgetId);
			}
			mPoints.remove(container);
			container.setOnTouchListener(null);
			try {
				mWndManager.removeView(container);
			} catch (Throwable e) {
				// view may not attached to window
			}
			
		}
	}
	
	class FloatView extends FrameLayout {

		boolean isTouchMoveable = false;
		int layoutId;
		int childWidth;
		int showingChildWidth = -1;
		int[] tempPt = new int[2];
		boolean isTouchMoving = false;
		Rect fitSystemInsets = new Rect();
		boolean isViewPositioned = false;
		boolean isMagnetic;
		boolean isAttatched;
		float currentPosPercentX = 0f;
		float currentPosPercentY = .5f;
		String positionPreferenceKey;
		boolean needInitPosition;
		
		public FloatView(Context context) {
			super(context);
		}
		
		public void setTouchMoveable(boolean moveable) {
			isTouchMoveable = moveable;
		}
		
		public void setMagnetic(boolean magnetic) {
			isMagnetic = magnetic;
		}
		
		public void setLayoutId(int layoutId) {
			this.layoutId = layoutId;
		}
		
		public void setPositionPreferenceKey(String key) {
			positionPreferenceKey = key;
			setNewPositionFromPreference();
		}
		
		public void setViewVisibility(final int viewId, final int visibility) {
			Runnable setVisibility = new Runnable() {
				
				@Override
				public void run() {
					View v = findViewById(viewId);
					if (v != null) {
						v.setVisibility(visibility);
					}
				}
			};
			if (MyThreadUtils.isOnMainThread()) {
				setVisibility.run();
			} else {
				post(setVisibility);
			}
		}
		
		private void setNewPositionFromPreference() {
			if (positionPreferenceKey == null) {
				return;
			}
			new Thread("loadFloatWidgetPos") {
				public void run() {
					SharedPreferences pref = FloatWidgetPreference.getPreferences(getContext());
					String value = pref.getString(positionPreferenceKey, null);
					if (!TextUtils.isEmpty(value)) {
						String[] pos = value.split(",");
						if (pos.length == 2) {
							final float xPercent = Float.valueOf(pos[0]);
							final float yPercent = Float.valueOf(pos[1]);
							if (isAttatched) {
								post(new Runnable() {
									
									@Override
									public void run() {
										setPosition(xPercent, yPercent);
									}
								});
							} else {
								needInitPosition = true;
								currentPosPercentX = xPercent;
								currentPosPercentY = yPercent;
							}
							
						}
					}
				}
			}.start();
		}
		
		private void savePositionToPreference() {
			if (positionPreferenceKey == null)
				return;
			new Thread("saveFloatWidgetPos") {
				public void run() {
					SharedPreferences pref = FloatWidgetPreference.getPreferences(getContext());
					pref.edit().putString(positionPreferenceKey, currentPosPercentX+","+currentPosPercentY).commit();
				}
			}.start();
		}
		
		public int getLauoutId() {
			return layoutId;
		}
		
		public int setShowingChildWidth(int width) {
			if (width < 0)
				width = 0;
			if (width > childWidth)
				width = childWidth;
			
			if (showingChildWidth != width) {
				showingChildWidth = width;
				postInvalidate();
			}
			
			return width;
		}
		
		public int getChildMaxWidth() {
			return childWidth;
		}
		
		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			if (!isTouchMoveable)
				return super.onInterceptTouchEvent(ev);
			if (ev.getAction() == MotionEvent.ACTION_DOWN) {
				int[] pt = mPoints.get(this);
				if (pt == null) {
					pt = new int[4];
					mPoints.put(this, pt);
				}
				pt[0] = (int)ev.getX();
				pt[1] = (int)ev.getY();
				pt[2] = (int)ev.getRawX();
				pt[3] = (int)ev.getRawY();
				
			} else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
				int[] pt = mPoints.get(this);
				if (pt != null) {
					int curX = (int)ev.getRawX();
					int curY = (int)ev.getRawY();
					if (Math.abs(curX-pt[2])>mTouchSlop || Math.abs(curY-pt[3])>mTouchSlop) {
						moveToLocaton((int) (curX-pt[0]), (int) (curY-pt[1]));
						isTouchMoving = true;
						return true;
					}
				}
				
			}
			return super.onInterceptTouchEvent(ev);
		}

		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			if (!isTouchMoveable)
				return super.onTouchEvent(ev);
			
			if (ev.getAction() == MotionEvent.ACTION_MOVE) {
				int[] pt = mPoints.get(this);
				if (pt != null) {
					moveToLocaton((int) (ev.getRawX()-pt[0]), (int) (ev.getRawY()-pt[1]));
					return true;
				}
				
			} else if (ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_UP) {
				isTouchMoving = false;
				if (isMagnetic)
					magneticToSide();
				else {
					fitSystemWindowsInternal(fitSystemInsets);
				}
			}
			return super.onTouchEvent(ev);
		}
		
		public void onOrientationChanged() {
			if (isMagnetic) {
				int screenHeight = getScreenHeight();
				int screenWidth = getScreenWidth();
				int[] location = tempPt;
				location[0] = (int)(screenWidth*currentPosPercentX);
				location[1] = (int)(screenHeight*currentPosPercentY);
				magneticToSide(location);
			}
		}
		
		public void setPosition(float xPercent, float yPercent) {
			int screenHeight = getScreenHeight();
			int screenWidth = getScreenWidth();
			int[] location = tempPt;
			location[0] = (int)(screenWidth*xPercent);
			location[1] = (int)(screenHeight*yPercent);
			if (isMagnetic) {
				magneticToSide(location);
			} else {
				moveToLocaton(location);
			}
		}
		
		public void onLocaleChanged() {
			View v = getChildAt(0);
			if (v != null && v instanceof FloatWidgetView) {
				((FloatWidgetView)v).onLocaleChanged();
			}
		}
		
		private void magneticToSide() {
			int screenHeight = getScreenHeight();
			int screenWidth = getScreenWidth();
			int[] location = tempPt;
			getLocationOnScreen(location);
			magneticToSide(location);
			
			currentPosPercentX = location[0]/(float)screenWidth;
			currentPosPercentY = location[1]/(float)screenHeight;
			
			savePositionToPreference();
		}
		
		private void magneticToSide(int[] location) {
			int screenHeight = getScreenHeight();
			int screenWidth = getScreenWidth();
			
			if (location[1] < mVerticalPading) {
				location[1] = 0;
			} else if (screenHeight-location[1]<mVerticalPading) {
				location[1] = screenHeight;
			} else if (location[0] < screenWidth/2) {
				location[0] = 0;
			} else if (location[0] > screenWidth/2) {
				location[0] = screenWidth;
			}
			
			moveToLocaton(location);
		}
		
		private void moveToLocaton(int[] location) {
			moveToLocaton(location[0], location[1]);
		}
		
		private void moveToLocaton(int x, int y) {
			WindowManager.LayoutParams params = (WindowManager.LayoutParams) this.getLayoutParams();
			params.gravity = Gravity.LEFT | Gravity.TOP;
			params.x = x;
			params.y = y;
			if (isAttatched) mWndManager.updateViewLayout(this, params);
		}

		@Override
		protected void dispatchDraw(Canvas canvas) {
			// only draw the first child
			if (getChildCount() > 0) {
				View child = getChildAt(0);
				if (child.getVisibility() == View.VISIBLE) {
					if (showingChildWidth >= 0)
						canvas.translate(-(childWidth-showingChildWidth), 0);
					drawChild(canvas, child, getDrawingTime());
				}
			}
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			if (getChildCount() > 0) {
				View child = getChildAt(0);
				childWidth = child.getMeasuredWidth();
			}
		}
		
		private void fitSystemWindowsInternal(Rect insets) {
			if (!isTouchMoving && isViewPositioned) {
				int[] location = tempPt;
				getLocationOnScreen(location);
				boolean changed = false;
				if (insets.left > 0) {
					location[0] += insets.left;
					changed = true;
				}
				if (insets.right > 0) {
					location[0] -= insets.right;
					changed = true;
				}
				if (insets.top > 0) {
					location[1] += insets.top;
					changed = true;
				}
				if (insets.bottom > 0) {
					location[1] -= insets.bottom;
					changed = true;
				}
				
				if (changed) {
					if (location[0] < 0)
						location[0] = 0;
					if (location[1] < 0)
						location[1] = 0;
					
					moveToLocaton(location);
				}
			}
		}
		
		protected boolean fitSystemWindows(Rect insets) {
			if (isTouchMoveable) {
				fitSystemInsets.set(insets);
				return true;
			} else {
				setPadding(insets.left, insets.top, insets.right, insets.bottom);
				return true;
			}
		}

		@Override
		protected void onLayout(boolean changed, int left, int top, int right,
				int bottom) {
			super.onLayout(changed, left, top, right, bottom);
			isViewPositioned = true;
			if (isTouchMoveable && !isTouchMoving) {
				// fit after layout complete
				fitSystemWindowsInternal(fitSystemInsets);
			}
		}

		@Override
		protected void onAttachedToWindow() {
			super.onAttachedToWindow();
			isAttatched = true;
			if (needInitPosition) {
				setPosition(currentPosPercentX, currentPosPercentY);
				needInitPosition = false;
			}
		}

		@Override
		protected void onDetachedFromWindow() {
			super.onDetachedFromWindow();
			isAttatched = false;
		}
	}
	
	public interface FloatWidgetView {
		void setWidgetInfo(IFloatWidgetService service, int widgetId);
		boolean requestClose();
		void onLocaleChanged();
	}

}
