package cn.yjt.oa.app.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

public class TelephonyUtil {
	public static String getICCID(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSimSerialNumber();  
	}

    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

}
