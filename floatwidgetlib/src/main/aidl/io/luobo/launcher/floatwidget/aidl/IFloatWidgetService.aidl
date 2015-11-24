package io.luobo.launcher.floatwidget.aidl;

import android.widget.RemoteViews;
import android.view.WindowManager.LayoutParams;
import io.luobo.launcher.floatwidget.aidl.ReflectAction;

interface IFloatWidgetService {

	int allocWidgetId();
	void updateViews(int widgetId, int gravity, int x, int y, in RemoteViews views);
	void updateViewsParams(int widgetId, in LayoutParams params, in RemoteViews views);
	void updateWindowBrightness(int widgetId, float brightness);
	void updateViewsByLayout(int widgetId, int layoutId, in LayoutParams params);
	void updateViewsAndVisibilityByLayout(int widgetId, int layoutId, boolean show, in LayoutParams params);
	void setViewVisibility(int widgetId, int viewId, int visiblity);
	int setShowingWidth(int widgetId, int width); 
	int getMaxShowingWidth(int widgetId);
	void setWidgetMoveable(int widgetId, boolean moveable);
	void deleteWidget(int widgetId);
	void showWidget(int widgetId, boolean show);
	boolean isWidgetShowing(int widgetId);
	void setReflectAction(int widgetId, in ReflectAction action);
	void setMagnetic(int widgetId, boolean magnetic);
	void setPositionPreferenceKey(int widgetId, String key);
}