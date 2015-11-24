package cn.yjt.oa.app.http;

import io.luobo.common.http.FileClient;
import io.luobo.common.http.download.HttpUrlFileClient;
import io.luobo.common.http.volley.GsonConverter;

import java.io.File;
import java.util.concurrent.Executors;

import android.content.Context;
import android.os.Environment;

public class FileClientFactory {

	private static File getCacheDir(Context context){
		File tempDir = null;
		if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
			tempDir = context.getExternalCacheDir();
		} else {
			tempDir = context.getCacheDir();
		}
		
		return tempDir;
	}
	
	public static FileClient createSingleThreadFileClient(Context context) {
		return new HttpUrlFileClient(getCacheDir(context), Executors.newSingleThreadExecutor(), new GsonConverter(GsonHolder.getInstance().getGson()), BusinessConstants.getBusinessHeaders());
	}
}
