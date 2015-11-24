package cn.yjt.oa.app.utils;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.WindowManager;

import cn.yjt.oa.app.MainApplication;

/**
 * Created by 熊岳岳 on 2015/10/13.
 */
public class ManagerUtils {

    /**获取AlarmManager*/
    public static AlarmManager getAlarmManager(){
        return (AlarmManager) MainApplication.getApplication().getSystemService(Context.ALARM_SERVICE);
    }

    /**获取WindowManager*/
    public static WindowManager getWindowManager(){
        return (WindowManager) MainApplication.getApplication().getSystemService(Context.WINDOW_SERVICE);
    }

    /**获取NotificationManager*/
    public static NotificationManager getNotificationManager(Context context){
        return (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**获取ClipboardManager*/
    public static ClipboardManager getClipboardManager(){
        return (ClipboardManager) MainApplication.getApplication().getSystemService(Context.CLIPBOARD_SERVICE);
    }
}
