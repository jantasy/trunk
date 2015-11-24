package cn.yjt.oa.app.meeting.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import cn.yjt.oa.app.utils.LogUtils;

@SuppressLint("SimpleDateFormat")
public class DateUtils {

	private final static String TAG = "DateUtils";

	public static final String DATE_ONLY_FROMAT = "yyyy-MM-dd ";
	public static final String TIME_ONLY_FROMAT = "HH:mm";
    public static final String DATE_ONLY_REQUEST_FORMAT = "yyyyMMdd";
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
	public static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";
    public static final String TIME_SECOND_FROMAT = "HH:mm:ss";
    public static final String TIME_REQUEST_FROMAT = "HHmmss";


	/** 时间格式 :yyyy-MM-dd */
	public static SimpleDateFormat sDateFormat = new SimpleDateFormat(DATE_ONLY_FROMAT);
    /** 时间格式 :yyyyMMdd */
    public static SimpleDateFormat sDateRequestFormat = new SimpleDateFormat(DATE_ONLY_REQUEST_FORMAT);
	/** 时间格式 :HH:mm */
	public static SimpleDateFormat sTimeFormat = new SimpleDateFormat(TIME_ONLY_FROMAT);
    /** 时间格式 :HH:mm:ss */
    public static SimpleDateFormat sTimeSecondFormat = new SimpleDateFormat(TIME_SECOND_FROMAT);
    /** 时间格式 :HHmmss */
    public static SimpleDateFormat sTimeRequestFormat = new SimpleDateFormat(TIME_REQUEST_FROMAT);
	/** 时间格式:yyyy-MM-dd-hh:mm */
	public static SimpleDateFormat sDateTimeFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
	/** 时间格式：iso8601 */
	public static SimpleDateFormat sRequestFormat = new SimpleDateFormat(ISO8601_FORMAT);

    /** 将DATE_ONLY_REQUEST_FROMAT格式的字符串转化成日期格式 */
    public static Date parseDateRequestFormat(String date) {
        try {
            return sDateRequestFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            LogUtils.e(TAG, e.getMessage());
        }
        return null;
    }
	/** 将DATE_ONLY_FROMAT格式的字符串转化成日期格式 */
	public static Date parseDateFormat(String date) {
		try {
			return sDateFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			LogUtils.e(TAG, e.getMessage());
		}
		return null;
	}

	/** 将DATE_ONLY_FROMAT格式的字符串转化成日期格式 */
	public static Date parseTimeFormat(String date) {
		try {
			return sTimeFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			LogUtils.e(TAG, e.getMessage());
		}
		return null;
	}

	/** 将DATE_ONLY_FROMAT格式的字符串转化成日期格式 */
	public static Date parseDateTimeFormat(String date) {
		try {
			return sDateTimeFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			LogUtils.e(TAG, e.getMessage());
		}
		return null;
	}

	/** 将DATE_ONLY_FROMAT格式的字符串转化成日期格式 */
	public static Date parseRequestFormat(String date) {
		try {
			return sRequestFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			LogUtils.e(TAG, e.getMessage());
		}
		return null;
	}

	/** 将Date数据变成"yyyy-MM-dd"格式的Date数据 */
	public static Date changeDateToDateOnly(Date date) {
		try {
			return sDateFormat.parse(sDateFormat.format(date));
		} catch (ParseException e) { 
			e.printStackTrace();
			LogUtils.e(TAG, e.getMessage());
		}
		return null;
	}

	/** 将Date数据变成"HH:mm"格式的Date数据 */
	public static Date changeDateToTimeOnly(Date date) {
		try {
			return sTimeFormat.parse(sTimeFormat.format(date));
		} catch (ParseException e) {
			e.printStackTrace();
			LogUtils.e(TAG, e.getMessage());
		}
		return null;
	}

    /** 将Date数据变成"HH:mm:ss"格式的Date数据 */
    public static Date changeDateToTimeSecond(Date date) {
        try {
            return sTimeSecondFormat.parse(sTimeSecondFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            LogUtils.e(TAG, e.getMessage());
        }
        return null;
    }

	/** 将Date数据变成"yyyy-MM-ddhh:mm"格式的Date数据 */
	public static Date changeDateToDateTime(Date date) {
		try {
			return sDateTimeFormat.parse(sDateTimeFormat.format(date));
		} catch (ParseException e) {
			e.printStackTrace();
			LogUtils.e(TAG, e.getMessage());
		}
		return null;
	}

	/** 将Date数据变成"yyyy-MM-dd-"格式的Date数据 */
	public static Date changeDateToRequest(Date date) {
		try {
			return sRequestFormat.parse(sRequestFormat.format(date));
		} catch (ParseException e) {
			e.printStackTrace();
			LogUtils.e(TAG, e.getMessage());
		}
		return null;
	}

	/** 将iso8601格式的日期字符串转换成"yyyy-MM-dd-hh:mm"格式的字符串 */
	public static String requestToDateTime(String date) {
		try {
			return sDateTimeFormat.format(sRequestFormat.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
			LogUtils.e(TAG, e.getMessage());
		}
		return null;
	}

	/** 将iso8601格式的日期字符串转换成"yyyy-MM-dd-hh:mm"格式的字符串 */
	public static String requestToDate(String date) {
		try {
			return sDateFormat.format(sRequestFormat.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
			LogUtils.e(TAG, e.getMessage());
		}
		return null;
	}

    /** 将"yyyyMMdd"格式的日期字符串转换成"yyyy-MM-dd"格式的字符串 */
    public static String dateRequestToDateTime(String date) {
        try {
            return sDateFormat.format(sDateRequestFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            LogUtils.e(TAG, e.getMessage());
        }
        return null;
    }
    /** 将"yyyy-MM-dd"格式的日期字符串转换成"yyyyMMdd"格式的字符串 */
    public static String dateToDateRequestTime(String date) {
        try {
            return sDateRequestFormat.format(sDateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            LogUtils.e(TAG, e.getMessage());
        }
        return null;
    }

    /** 将"HHmmss"格式的日期字符串转换成"HH:mm:ss"格式的字符串 */
    public static String timeRequestToTimeSecond(String date) {
        try {
            return sTimeSecondFormat.format(sTimeRequestFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            LogUtils.e(TAG, e.getMessage());
        }
        return null;
    }
}
