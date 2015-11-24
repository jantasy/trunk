package cn.yjt.oa.app.utils;

import android.text.TextUtils;

public class PhoneUtils {
	
	private static final String[] TELECOM_PREFIX = new String[]{"177","180","181","189","133","153","1700","149"};

	
	public static boolean isTelecom(String phone){
		if(!TextUtils.isEmpty(phone)){
			for (int i = 0; i < TELECOM_PREFIX.length; i++) {
				if(phone.startsWith(TELECOM_PREFIX[i])){
					return true;
				}
			}
		}
		//TODO:
		return false;
	}
}
