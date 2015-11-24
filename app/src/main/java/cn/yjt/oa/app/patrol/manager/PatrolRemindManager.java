package cn.yjt.oa.app.patrol.manager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import cn.yjt.oa.app.meeting.utils.DateUtils;
import cn.yjt.oa.app.patrol.receiver.PatrolRemindReceiver;
import cn.yjt.oa.app.patrol.utils.PatrolRemindData;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.utils.ManagerUtils;

/**
 * 巡更提醒管理，用来设置或取消“提醒广播”
 * Created by 熊岳岳  on 2015/10/13.
 */
public class PatrolRemindManager {

    private final String TAG = "PatrolRemindManager";

    private Context mContext;

    private AlarmManager manager;
    private Intent intent;
    private PendingIntent broadCastIntent;


    public PatrolRemindManager(Context context) {
        this.mContext = context;
        manager = ManagerUtils.getAlarmManager();
        intent = new Intent(PatrolRemindReceiver.BROADCAST_PATROL_REMIND);
        broadCastIntent = PendingIntent.getBroadcast(mContext
                , 0
                , intent
                , PendingIntent.FLAG_UPDATE_CURRENT);
    }


    /** 设置巡检提醒的广播 */
    public void remind() {
        LogUtils.d(TAG, "设置巡检提醒广播");
        manager.cancel(broadCastIntent);

//      debug专用
//      long debugTime = getAlarmTime();

        manager.setRepeating(AlarmManager.RTC_WAKEUP
                , getAlarmTime()
                , AlarmManager.INTERVAL_DAY
                , broadCastIntent);
    }

    /** 取消巡检提醒的广播 */
    public void cancleRemind() {
        LogUtils.d(TAG, "取消巡检提醒广播");
        manager.cancel(broadCastIntent);
    }

    /**获取提醒的时间*/
    private long getAlarmTime() {
        /*
        *  巡检提醒时间保存的格式是  "HH"mm",没有年月日
        *  所以，要给巡检提醒的数据拼上一个年月日
        */
        Calendar calendar = Calendar.getInstance();
        String date = DateUtils.sDateFormat.format(calendar.getTime());
        String time = PatrolRemindData.getRemindTime();
        String timeString = date + time;


        Date alarmTime = null;
        try {
            // 拼成功后,得到Date类型的值
            alarmTime = DateUtils.sDateTimeFormat.parse(timeString);

            /*
            *  由于AlarmManager设定闹钟时间小于当前系统时间的话，系统会马上响应这个任务
            *  这个如果设定时间小于当前系统时间，就将设定闹钟的日期推后一天
            *  如 2015-10-14 10：00  ----> 2015-10-15 10:00
            */
            if (alarmTime.compareTo(calendar.getTime()) > 0) {
                return alarmTime.getTime();
            } else {
                Calendar alarmCalendar = Calendar.getInstance();
                alarmCalendar.setTime(alarmTime);
                alarmCalendar.set(Calendar.DAY_OF_YEAR, alarmCalendar.get(Calendar.DAY_OF_YEAR) + 1);

//              debug专用
//              String debugAlarmTime = DateUtils.sDateTimeFormat.format(alarmCalendar.getTime());

                return alarmCalendar.getTime().getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            LogUtils.e(TAG, "格式化时间失败");
            return 0;
        }
    }
}
