package cn.yjt.oa.app.patrol.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Calendar;
import java.util.List;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.PatrolAttendanceInfo;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.meeting.utils.DateUtils;
import cn.yjt.oa.app.patrol.activitys.PatrolRecordActivity;
import cn.yjt.oa.app.patrol.http.PatrolApiHelper;
import cn.yjt.oa.app.utils.ManagerUtils;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;

/**
 * Created by xiong on 2015/10/14.
 */
public class PatrolRemindService extends Service {

    private NotificationManager mNotifiManage;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mNotifiManage = ManagerUtils.getNotificationManager(this);
//        requestPatrolInfo();
        Notification notify = getNotification();
        mNotifiManage.notify(1, notify);
        return super.onStartCommand(intent, flags, startId);
    }

    private void requestPatrolInfo() {
        Calendar calendar = Calendar.getInstance();
        String requestDate = DateUtils.sDateRequestFormat.format(calendar.getTime());
        PatrolApiHelper.getPatrolRecord(new Listener<Response<List<List<PatrolAttendanceInfo>>>>() {
            @Override
            public void onResponse(Response<List<List<PatrolAttendanceInfo>>> listResponse) {

            }

            @Override
            public void onErrorResponse(InvocationError invocationError) {

            }
        },requestDate,requestDate);
    }


    private Notification getNotification() {
        Notification notify = new Notification();
        Intent intent = new Intent(this, PatrolRecordActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PatrolRecordActivity.COME_NOTIFICATION,true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        notify.icon = R.drawable.ic_launcher;
        notify.tickerText = "您今天有巡检任务";
        notify.when = System.currentTimeMillis();
//        notify.sound = Uri.parse("android.resource://"
//                + getPackageName() + "/" + R.raw.umeng_push_notification_default_sound);
        notify.defaults|=Notification.DEFAULT_VIBRATE;
        notify.defaults|= Notification.DEFAULT_SOUND;

        notify.setLatestEventInfo(this, "巡检提醒","您有未完成的巡检工作 点我来看看都有哪些吧", pendingIntent);
        notify.number = 1;
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        return notify;
    }
}
