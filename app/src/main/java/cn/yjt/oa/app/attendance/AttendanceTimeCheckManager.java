package cn.yjt.oa.app.attendance;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AttendanceTimeCheckManager {

    private static final String TAG = "AttendanceTimeCheckManager";

    private Context context;

    public AttendanceTimeCheckManager(Context context) {
        this.context = context;
    }

    public void check() {

        Log.d(TAG, "check");

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AttendanceTimeCheckReceiver.ACTION_ATTENDANCE_TIME_CHECK);
        PendingIntent operation = PendingIntent.getBroadcast(context,
                AlarmRequestCode.ATTENDANCE_TIME_CHECK, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.cancel(operation);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                AlarmManager.INTERVAL_HOUR, operation);
    }

    public void cancelCheck() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AttendanceTimeCheckReceiver.ACTION_ATTENDANCE_TIME_CHECK);
        PendingIntent operation = PendingIntent.getBroadcast(context,
                AlarmRequestCode.ATTENDANCE_TIME_CHECK, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.cancel(operation);
    }
}
