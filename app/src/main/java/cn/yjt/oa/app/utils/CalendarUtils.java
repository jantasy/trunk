package cn.yjt.oa.app.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

/**
 * 日历工具类
 * @author 熊岳岳
 * @since 20150724
 */
public class CalendarUtils {

	private final static String TAG = "CalendarUtils";
	
	/**获取本周周一*/
	public static Date getThisWeekMonday() {

		Calendar date = Calendar.getInstance();

		Date temp = date.getTime();
		date.setTime(temp);
		
		int i = date.get(Calendar.DAY_OF_WEEK);
		Log.i(TAG,i+"");
		
		date.setFirstDayOfWeek(Calendar.MONDAY);//将每周第一天设为星期一，默认是星期天

		if (i == 1) {
			date.add(Calendar.DATE, -1);
		} 
		date.add(Calendar.WEEK_OF_MONTH, 0);

		date.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);//日子设为星期一

		return date.getTime();

	}

	/**获取上周周一*/
	public static Date getLastWeekMonday() {

		Calendar date = Calendar.getInstance();

		date.setFirstDayOfWeek(Calendar.MONDAY);//将每周第一天设为星期一，默认是星期天

		date.add(Calendar.WEEK_OF_MONTH, -1);//周数减一，即上周

		date.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);//日子设为星期一

		return date.getTime();

	}

	/**获取上周周日*/
	public static Date getLastWeekSunday() {

		Calendar date = Calendar.getInstance();

		date.setFirstDayOfWeek(Calendar.MONDAY);//将每周第一天设为星期一，默认是星期天

		date.add(Calendar.WEEK_OF_MONTH, -1);//周数减一，即上周

		date.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);//日子设为星期天

		return date.getTime();

	}

	/**获取本月第一天*/
	public static Date getThisMonthFirstday() {

		Calendar date = Calendar.getInstance(Locale.CHINA);

		date.set(Calendar.DAY_OF_MONTH, 1);//先将日期设置为本月第一天

		return date.getTime();

	}

	/**获取上月第一天*/
	public static Date getLastMonthFirstday() {

		Calendar date = Calendar.getInstance();

		date.add(Calendar.MONTH, -1);//月数减一

		date.set(Calendar.DAY_OF_MONTH, 1);

		return date.getTime();

	}

	/**获取上月最后一天*/
	public static Date getLastMonthLastday() {

		Calendar date = Calendar.getInstance(Locale.CHINA);

		date.set(Calendar.DAY_OF_MONTH, 1);//先将日期设置为本月第一天

		date.add(Calendar.DATE, -1);//日期减去一天

		return date.getTime();

	}
}
