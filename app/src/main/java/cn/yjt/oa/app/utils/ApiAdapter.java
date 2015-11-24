package cn.yjt.oa.app.utils;

import java.lang.reflect.Field;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.view.View;
import android.widget.GridView;

public class ApiAdapter {
    public static final int CURRENT_VERSION = Build.VERSION.SDK_INT;
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static float getAlpha(View target) {
        return (CURRENT_VERSION >= Build.VERSION_CODES.HONEYCOMB) ? target.getAlpha() : 0;
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setAlpha(View target, float alpha) {
        if (CURRENT_VERSION >= Build.VERSION_CODES.HONEYCOMB) {
            target.setAlpha(alpha);
        }
    }
    
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void setCanvasNullBitmap(Canvas target) {
        if (CURRENT_VERSION >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            target.setBitmap(null);
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static boolean isHardwareAccelerated(View v) {
        if (CURRENT_VERSION >= Build.VERSION_CODES.HONEYCOMB) {
            return v.isHardwareAccelerated();
        }
        return false;
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static boolean isHardwareAccelerated(Canvas c) {
        if (CURRENT_VERSION >= Build.VERSION_CODES.HONEYCOMB) {
            return c.isHardwareAccelerated();
        }
        return false;
    }
    
    /**
     * this version we can use some morden view api to implement animation
     * @return
     */
    public static boolean isMordenViewApi() {
    	return CURRENT_VERSION >= Build.VERSION_CODES.HONEYCOMB;
    }
    
    static Field sGridViewField;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static int getGridViewColumnWidth(GridView gridView) {
    	if (CURRENT_VERSION >= Build.VERSION_CODES.JELLY_BEAN) {
    		return gridView.getColumnWidth();
    	} else {
    		if (sGridViewField == null) {
    			try {
					sGridViewField = GridView.class.getDeclaredField("mColumnWidth");
					sGridViewField.setAccessible(true);
				} catch (NoSuchFieldException e) {
				}
    		}
    		if (sGridViewField != null) {
    			try {
					return sGridViewField.getInt(gridView);
				} catch (Exception e) {
				}
    		}
    	}
    	return 0;
    }
    
    /**
     * check if we have a camera device, under api level 9 always return true
     * @return
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean haveCameraDevice() {
    	if (CURRENT_VERSION >= Build.VERSION_CODES.GINGERBREAD)
    		return Camera.getNumberOfCameras() > 0;
    	else
    		return true;
	}
    
    public static boolean haveCameraEnumerateApi() {
    	return CURRENT_VERSION >= Build.VERSION_CODES.GINGERBREAD;
    }
    
    public static Drawable getDrawableForDensity(Resources res, int id, int density) {
    	if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH && density > 0) {
    		return res.getDrawableForDensity(id, density);
    	} else {
    		return res.getDrawable(id);
    	}
    }
}
