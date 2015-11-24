package cn.yjt.oa.app.attendance;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.beans.AttendanceTime;
import cn.yjt.oa.app.beans.AttendanceUserTime;
import cn.yjt.oa.app.http.ApiHelper;
import cn.yjt.oa.app.http.ResponseListener;
import cn.yjt.oa.app.utils.UserData;
/**
 * 考勤时间核查的广播接收者 
 *
 */
public class AttendanceTimeCheckReceiver extends BroadcastReceiver {

	//设置该类的TAG
	private static final String TAG = "AttendanceTimeCheckReceiver";
	//初始化一个时间的 简单格式（应该有工具类？）
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	//
	public static final String ACTION_ATTENDANCE_TIME_CHECK = "cn.yjt.oa.app.attendance.ACTION_ATTENDANCE_TIME_CHECK";
	private static final long HALF_A_HOUR = 30 * 60 * 1000L;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive");
		// boolean fetch = intent.getBooleanExtra("fetch", false);
		// if (fetch) {
		// fetchAttendanceUserTime();
		// return;
		// }
		fetchAttendanceUserTime();
	}

	private void checkAutoBeaconAlarm() {
		AttendanceUserTime attendanceUserTime = UserData
				.getAttendanceUserTime();
		
		if (UserData.getAutoAttendance()&&attendanceUserTime != null
				&& isToday(attendanceUserTime.getParseDate())) {
			List<AttendanceTime> times = attendanceUserTime.getTimes();
			setAlarm(times);
		}
	}

	private void setAlarm(List<AttendanceTime> attendanceTimes) {
		AlarmRequestCode.resetAlloc();
		for (AttendanceTime attendanceTime : attendanceTimes) {
			setAlarm(attendanceTime);
		}
	}

	private void setAlarm(AttendanceTime attendanceTime) {
		String inStartTime = attendanceTime.getInStartTime();
		String inEndTime = attendanceTime.getInEndTime();
		setInTimeAlarm(inStartTime, inEndTime);

		String outStartTime = attendanceTime.getOutStartTime();
		String outEndTime = attendanceTime.getOutEndTime();
		setOutTimeAlarm(outStartTime, outEndTime);
	}

	private void setOutTimeAlarm(String outStartTime, String outEndTime) {
		if (!TextUtils.equals(outStartTime, outEndTime)) {
			setStartAutoBeaconServiceAlarm(convertTodayTime(outStartTime));
			setStopAutoBeaconServiceAlarm(convertTodayTime(outEndTime));
		} else {
			setStartAutoBeaconServiceAlarm(convertTodayTime(outStartTime));
			setStopAutoBeaconServiceAlarm(calcAfterHalfHourTime(outStartTime));
		}
	}

	private void setInTimeAlarm(String inStartTime, String inEndTime) {
		if (!TextUtils.equals(inStartTime, inEndTime)) {
			setStartAutoBeaconServiceAlarm(convertTodayTime(inStartTime));
			setStopAutoBeaconServiceAlarm(convertTodayTime(inEndTime));
		} else {
			setStartAutoBeaconServiceAlarm(calcBeforeHalfHourTime(inStartTime));
			setStopAutoBeaconServiceAlarm(convertTodayTime(inStartTime));
		}
	}

	private long convertTodayTime(String time) {
		Date parseTime = AttendanceTime.parseTime(time);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(parseTime);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		int milsec = calendar.get(Calendar.MILLISECOND);

		Calendar instance = Calendar.getInstance();
		instance.set(Calendar.HOUR_OF_DAY, hour);
		instance.set(Calendar.MINUTE, min);
		instance.set(Calendar.SECOND, second);
		instance.set(Calendar.MILLISECOND, milsec);

		return instance.getTimeInMillis();
	}

	private long calcBeforeHalfHourTime(String time) {
		return convertTodayTime(time) - HALF_A_HOUR;
	}

	private long calcAfterHalfHourTime(String time) {
		return convertTodayTime(time) + HALF_A_HOUR;
	}

	private void setStartAutoBeaconServiceAlarm(long triggerAtMillis) {
		Context context = MainApplication.getAppContext();
		setBeaconServiceAlarm(AutoAttendanceService.getStartIntent(context),
				triggerAtMillis);
	}

	private void setStopAutoBeaconServiceAlarm(long triggerAtMillis) {
		Context context = MainApplication.getAppContext();
		setBeaconServiceAlarm(AutoAttendanceService.getStopIntent(context),
				triggerAtMillis);
	}

	private void setBeaconServiceAlarm(Intent intent, long triggerAtMillis) {
		if (AttendanceData.containsAttendanceTime(triggerAtMillis)) {
			System.out
					.println("AttendanceData.containsAttendanceTime(triggerAtMillis):"
							+ triggerAtMillis);
			return;
		}
		System.out.println("setBeaconServiceAlarm:"+convertString(triggerAtMillis));
		Context context = MainApplication.getAppContext();
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		PendingIntent operation = PendingIntent.getService(context,
				AlarmRequestCode.allocAutoAttendanceTimeCode(), intent, 0);
		alarmManager.cancel(operation);
		alarmManager.set(AlarmManager.RTC, triggerAtMillis, operation);
		AttendanceData.addAttendanceTime(triggerAtMillis);
	}
	
	private String convertString(long millis){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return DATE_FORMAT.format(calendar.getTime());
	}

	private void fetchAttendanceUserTime() {
		Log.d(TAG, "fetchAttendanceUserTime()");
		ApiHelper
				.getAttendanceUserTimes(new ResponseListener<List<AttendanceUserTime>>() {

					@Override
					public void onSuccess(List<AttendanceUserTime> payload) {
						if (!payload.isEmpty()) {
							UserData.setAttendanceUserTime(payload.get(0));
							checkAutoBeaconAlarm();
							AttendanceData.clearNotToday();
						}
					}
				});
	}

	private static Date getToday() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
		return calendar.getTime();
	}

	private static boolean isToday(Date date) {

		Date today = getToday();
		Log.d(TAG, "today:" + today);
		Log.d(TAG, "date:" + date);
		return today.equals(date);
	}

	public static void sendBroadcast(Context context) {
		Log.d(TAG, "sendBroadcast");
		Intent intent = new Intent(ACTION_ATTENDANCE_TIME_CHECK);
		intent.putExtra("fetch", true);
		context.sendBroadcast(intent);
	}
}
