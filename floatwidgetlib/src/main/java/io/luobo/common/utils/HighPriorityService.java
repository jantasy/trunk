package io.luobo.common.utils;


import java.lang.reflect.Method;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.util.Log;

public abstract class HighPriorityService extends Service {
	
	private static final String TAG = "HighPriorityService";
	// empty notification hack status bar, status bar can not show empty notification
	private static final Notification sNotify = new Notification();

	@Override
	public void onCreate() {
		superCreate();
		init();
		int id = getNotificationId();
		if (id != 0)
			startForegroundCompat(id, getNotification());
	}

	@Override
	public void onDestroy() {
		superDestroy();
		int id = getNotificationId();
		if (id != 0)
			stopForegroundCompat(id);
	}
	
	public void superCreate() {
		super.onCreate();
	}
	
	public void superDestroy() {
		super.onDestroy();
	}

	private NotificationManager mNM;
	private Method mSetForeground;
	private Method mStartForeground;
	private Method mStopForeground;
	private Object[] mSetForegroundArgs = new Object[1];
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];
	private static final Class<?>[] mSetForegroundSignature = new Class[] {
	    boolean.class};
	private static final Class<?>[] mStartForegroundSignature = new Class[] {
	    int.class, Notification.class};
	private static final Class<?>[] mStopForegroundSignature = new Class[] {
	    boolean.class};
	
	void init() {
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		try {
	        mStartForeground = getClass().getMethod("startForeground",
	                mStartForegroundSignature);
	        mStopForeground = getClass().getMethod("stopForeground",
	                mStopForegroundSignature);
	    } catch (NoSuchMethodException e) {
	        // Running on an older platform.
	        mStartForeground = mStopForeground = null;
	    }
	    try {
	        mSetForeground = getClass().getMethod("setForeground",
	                mSetForegroundSignature);
	    } catch (NoSuchMethodException e) {
	        throw new IllegalStateException(
	                "OS doesn't have Service.startForeground OR Service.setForeground!");
	    }
	}

	void invokeMethod(Method method, Object[] args) {
	    try {
	        method.invoke(this, args);
	    } catch (Throwable e) {
	        // Should not happen.
	        Log.w(TAG, "Unable to invoke method", e);
	    }
	}

	/**
	 * This is a wrapper around the new startForeground method, using the older
	 * APIs if it is not available.
	 */
	protected void startForegroundCompat(int id, Notification notification) {
	    // If we have the new startForeground API, then use it.
	    if (mStartForeground != null) {
	        mStartForegroundArgs[0] = Integer.valueOf(id);
	        mStartForegroundArgs[1] = notification;
	        invokeMethod(mStartForeground, mStartForegroundArgs);
	        return;
	    }

	    // Fall back on the old API.
	    mSetForegroundArgs[0] = Boolean.TRUE;
	    invokeMethod(mSetForeground, mSetForegroundArgs);
	    mNM.notify(id, notification);
	}

	/**
	 * This is a wrapper around the new stopForeground method, using the older
	 * APIs if it is not available.
	 */
	protected void stopForegroundCompat(int id) {
	    // If we have the new stopForeground API, then use it.
	    if (mStopForeground != null) {
	        mStopForegroundArgs[0] = Boolean.TRUE;
	        invokeMethod(mStopForeground, mStopForegroundArgs);
	        return;
	    }

	    // Fall back on the old API.  Note to cancel BEFORE changing the
	    // foreground state, since we could be killed at that point.
	    mNM.cancel(id);
	    mSetForegroundArgs[0] = Boolean.FALSE;
	    invokeMethod(mSetForeground, mSetForegroundArgs);
	}

	protected abstract int getNotificationId();
	
	/**
	 * 获取高优先级service的通知栏消息，默认返回空的notification
	 * @return
	 */
	protected Notification getNotification() {
		return sNotify;
	}
}
