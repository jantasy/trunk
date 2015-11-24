package cn.yjt.oa.app.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.WallpaperManager;
import android.content.Context;
import android.view.WindowManager;

public class WindowUtils {
	private WindowUtils(){}
	@SuppressWarnings("deprecation")
	public static int getWindowWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getWidth();
	}

	@SuppressWarnings("deprecation")
	public static int getWindowHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getHeight();
	}

	// 设置图片成为系统壁纸
	public static boolean setWallpaper(Context context,File file) {
		FileInputStream in = null;
		WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
		try {
			in = new FileInputStream(file);
			wallpaperManager.setStream(in);
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
}
