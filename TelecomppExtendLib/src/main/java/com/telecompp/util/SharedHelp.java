package com.telecompp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedHelp {

	private static final String SP_HELP_NAMES = "help_names";
	private static final String SP_HELP_NAMES_ISNEED = "isneed";
	
	
	private static SharedPreferences getIsHelpNeedSp(Context context){
		SharedPreferences sp = context.getSharedPreferences(SP_HELP_NAMES, Context.MODE_PRIVATE);
		return sp;
	}
	
	public static boolean getIsHelpNeed(Context context) {
		return getIsHelpNeedSp(context).getBoolean(SP_HELP_NAMES_ISNEED, true);
	}
	
	public static void setIsHelpNeed(Context context,boolean isNeed){
		Editor edit = getIsHelpNeedSp(context).edit();
		edit.putBoolean(SP_HELP_NAMES_ISNEED, isNeed);
		edit.commit();
	}
	
}
