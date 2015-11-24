package com.telecompp.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.os.Environment;

public class WriteLog {

	@SuppressLint("NewApi")
	public static void writeLogOnSDCard(String exception) {
		SimpleDateFormat sdf = new SimpleDateFormat("", Locale.SIMPLIFIED_CHINESE);
		sdf.applyPattern("yyyy年MM月dd日 HH时mm分ss秒");
		File filed = Environment.getExternalStorageDirectory();
		File f = new File(filed, "翼机通-碰碰");
		if (!f.exists()) {
			f.mkdirs();
		}
		File file = new File(f, "exception.txt");
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(file, true);
			bw = new BufferedWriter(fw);
			bw.write(System.lineSeparator() + sdf.format(System.currentTimeMillis()) + exception);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(bw != null) {
				try {					
					bw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
