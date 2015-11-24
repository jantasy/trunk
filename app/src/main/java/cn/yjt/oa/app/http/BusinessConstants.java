package cn.yjt.oa.app.http;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BusinessConstants {

	//    private static final String BASE_URL = "http://219.142.69.139:9090/yjtoa/s/";
	//	private static String BASE_URL = "http://192.168.1.181:9090/yjtoa/s/";
	//	private static final String BASE_URL = "http://192.168.1.45:9090/yjtoa/s/";
	//    private static  String BASE_URL = "http://219.142.69.139:9090/yjtoa/s/";
	//private static final String BASE_URL = "http://192.168.1.112:9090/s/";
//	private static String BASE_URL = "http://192.168.1.181:9090/yjtoa/s/";
	//    private static String BASE_URL = "http://192.168.1.58:9090/yjtoa/s/";
//	private static String BASE_URL = "http://test.yijitongoa.com:9090/yjtoa/s/";
    //private static String BASE_URL = "http://192.168.1.233:9090/yjtoa/s/";
    //private static String BASE_URL = "http://192.168.1.45:9090/yjtoa/s/";
	//    private static final String BASE_URL = "http://192.168.1.250:9090/s/";
	//    private static  String BASE_URL = "http://42.123.76.176:9090/yjtoa/s/";
	//    private static String BASE_URL = "http://42.123.77.157:9090/yjtoa/s/";
	    private static  String BASE_URL = "http://www.yijitongoa.com:9090/yjtoa/s/";
	private static final String API_VER = "1";
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	private static final String DATE_FROMAT = "yyyy-MM-dd";
	private static final String SIMPLE_DATE_FORMAT = "MM月dd日";
	private static final String DATE_AND_TIME_FORMAT = "MM月dd日 HH:mm";
	private static final String TIME_FORMAT = "HH:mm";
	private static final String TIME_FORMAT_WITH_SS = "HH:mm:ss";

	public static String getBusinessUrl() {
		return getBASE_URL();
	}

	public static Map<String, String> getBusinessHeaders() {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("apiVer", API_VER);

		return headers;
	}

	public static String getDate(Date date) {
		return new SimpleDateFormat(DATE_FROMAT).format(date);
	}

	public static String getTime(Date date) {
		return new SimpleDateFormat(TIME_FORMAT).format(date);
	}

	public static String getTimeWithSS(Date date) {
		return new SimpleDateFormat(TIME_FORMAT_WITH_SS).format(date);
	}

	public static String formatDate(Date date) {
		return new SimpleDateFormat(DATE_AND_TIME_FORMAT).format(date);
	}

	public static String formatSimpleDate(Date date) {
		return new SimpleDateFormat(SIMPLE_DATE_FORMAT).format(date);
	}

	public static String formatTime(long time) {
		return new SimpleDateFormat(DATE_TIME_FORMAT).format(time);
	}

	public static String formatTime(Date time) {
		return new SimpleDateFormat(DATE_TIME_FORMAT).format(time);
	}

	public static Date parseTime(String dateTime) throws ParseException {
		return new SimpleDateFormat(DATE_TIME_FORMAT).parse(dateTime);
	}

	public static String buildUrl(String moudle) {
		return getBASE_URL() + moudle;
	}

	public static String buildPath(String path) {
		String substring = getBASE_URL().substring(0, getBASE_URL().lastIndexOf("/s/"));
		return substring + path;
	}

	public static String getBASE_URL() {
		return BASE_URL;
	}

	public static void setBASE_URL(String bASE_URL) {
		BASE_URL = bASE_URL;
	}
}
