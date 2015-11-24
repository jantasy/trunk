package com.chinatelecom.nfc.Utils;

import android.content.Context;
import android.view.WindowManager;

public class PhoneConstant {
	
	/**
	 * 获取屏幕宽度
	 */
	public static int screenWidth = 0;
	
	/**
	 * 获取屏幕高度
	 */
	public static int screenHeight = 0;

	/**
	 * 获取屏幕宽高
	 */
	public static void getScreenContext(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

		screenWidth = wm.getDefaultDisplay().getWidth();
		screenHeight = wm.getDefaultDisplay().getHeight();
		System.out.println(screenWidth+"  "+screenHeight);
	}

}
