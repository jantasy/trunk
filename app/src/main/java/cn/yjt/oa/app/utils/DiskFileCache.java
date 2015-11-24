package cn.yjt.oa.app.utils;

import io.luobo.common.utils.MD5Utils;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class DiskFileCache {
	private File cacheDiskFile;

	public DiskFileCache(Context context) {
		String esState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(esState)) {
			cacheDiskFile = context.getExternalCacheDir();
		} else {
			cacheDiskFile = context.getCacheDir();
		}
	}

	public File getFile(String key) {
		File file = new File(createCacheFile(cacheDiskFile, key));
		if (file.exists() && file.length() > 0) {
			return file;
		}
		return null;

	}

	public File getCacheDiskFile() {
		return cacheDiskFile;
	}
	
	public static String createCacheFile(File cacheDir, String key) {
		String absolutePath = new File(cacheDir, MD5Utils.md5(key))
				.getAbsolutePath();
		return absolutePath;
	}


	
	

}
