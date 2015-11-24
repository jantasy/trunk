package cn.yjt.oa.app.utils;

import android.os.Environment;

public class EnvUtils {
	public static String getWallpaperDir() {
		return Environment.getExternalStorageDirectory()+ "/robusoft/wallpaper/";
	}
}
