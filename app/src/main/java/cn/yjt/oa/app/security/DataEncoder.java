package cn.yjt.oa.app.security;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DataEncoder {
	static final String DATE_FORMAT = "' + 'yyyyMMddHHmmss";
	static final int FORMAT_LEN = DATE_FORMAT.length()-2; // 2 for ' symble
    public static String encode(String data) {
    	if (data == null)
    		return null;
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        String dateTime = df.format(System.currentTimeMillis());
        String result = "";
        try {
            result = Base64.encodeToString((data + dateTime).getBytes("utf8"), Base64.USE_CUSTOM_CHARS | Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            //Impossible
        }
        return result;
    }
    
    public static String decode(String data) {
    	if (data == null)
    		return null;
    	String result = data;
        try {
            byte[] decode = Base64.decode(data.getBytes("US-ASCII"), Base64.USE_CUSTOM_CHARS | Base64.NO_WRAP);
            result = new String(decode, 0, decode.length-FORMAT_LEN, "utf8");
        } catch (UnsupportedEncodingException e) {
            //Impossible
        } catch (Throwable tr) {
        	// source data may not encoded
        }
        return result;
    }
}
