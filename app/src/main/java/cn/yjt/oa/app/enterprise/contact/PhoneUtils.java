package cn.yjt.oa.app.enterprise.contact;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneUtils {
	public static boolean isMobileNum(String mobiles) {
		if(mobiles == null){
			return false;
		}
		Pattern p = Pattern.compile("^((1[0-9][0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles.trim().replace(" ", ""));
		return m.matches();
	}
	
	public static String formatPhoneNumber(String phone) {
		if (phone != null) {
			return phone.replace("+86", "").replace("-", "").replaceAll(" ", "");
		}
		return null;
	}
	public static boolean isNum(String mobiles) {
		if(mobiles == null){
			return false;
		}
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(mobiles.trim());
		return m.matches();
	}
	public static boolean isLetter(String mobiles) {
		if(mobiles == null){
			return false;
		}
		Pattern p = Pattern.compile("^[A-Za-z]+$");
		Matcher m = p.matcher(mobiles.trim());
		return m.matches();
	}
}
