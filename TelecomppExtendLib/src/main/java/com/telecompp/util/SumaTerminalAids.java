
package com.telecompp.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;

/**
 * 客户端开发辅助接口
 * @author xiongdazhuang 
 *
 */
public class SumaTerminalAids
{
	/**
	 * 获取系统时间，格式是MMddHHmmss
	 * @return 时间
	 */
	public static String getSystemTime()
	{
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
		String nowTime = format.format(new Date());
		return nowTime;
	}
	
	
	public static String getString(Bundle bundle,String key,String defaString){
		if(bundle==null)
			throw new RuntimeException("bunlde can not be null");
		if(key == null)
			throw new RuntimeException("key in bundle cannot be null");
		Object ret = bundle.get(key);
		if(ret == null){
			return defaString;
		}else{
			return ret.toString();
		}
	}
	/**
	 * 获取系统时间
	 * @param theFormat 时间格式
	 * @return 时间
	 */
	public static String getSystemTime(String theFormat)
	{
		SimpleDateFormat format = new SimpleDateFormat(theFormat);
		String nowTime = format.format(new Date());
		return nowTime;
	}
	
	/**
	 * 比较日期
	 * @param date1
	 * @param date2
	 * @return 0 一样 1 date1 after date2  -1 date1 before date2
	 */
	public static int compareDate(Calendar date1,Calendar date2){
		int y = date1.get(Calendar.YEAR) - date2.get(Calendar.YEAR);
		int m = date1.get(Calendar.MONTH) - date2.get(Calendar.MONTH);
		int d = date1.get(Calendar.DAY_OF_MONTH) - date2.get(Calendar.DAY_OF_MONTH);
		if( y != 0){
			return y;
		}
		if(m != 0){
			return m;
		}
		if(d != 0){
			return d;
		}
		return 0;
	}
	
}
