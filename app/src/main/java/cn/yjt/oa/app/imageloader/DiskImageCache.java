package cn.yjt.oa.app.imageloader;

import io.luobo.common.utils.MD5Utils;

import java.io.File;
import java.io.FileNotFoundException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.utils.FileUtils;

public class DiskImageCache {
	private static final String TAG = "DiskImageCache";

	private File cacheDir;
	private Context context;

	public DiskImageCache(Context context) {
		this.context = context;
		String esState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(esState)) {
			cacheDir = context.getExternalCacheDir();
		} else {
			cacheDir = context.getCacheDir();
		}
	}

	public File getCacheDir() {
		return cacheDir;
	}

	public Bitmap getBitmap(String uri, int maxWidth, int maxHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		if (uri.startsWith("http")) {
			String absolutePath = createCacheFile(cacheDir, uri);
			if (new File(absolutePath).exists()) {
				return decodeFileWithHandleOutOfMemery(absolutePath, maxWidth, maxHeight);
			} else {
				Log.w(TAG, "file not exist :" + absolutePath);
			}
		} else if (uri.startsWith("content")) {
			File imageFile = FileUtils.createFileInUserFolder(MD5Utils.md5(uri)+".img");
			try {
				if(!imageFile.exists()){
					FileUtils.copy(context.getContentResolver().openInputStream(Uri.parse(uri)), imageFile);
				}
				return decodeFileWithHandleOutOfMemery(imageFile.getAbsolutePath(), maxWidth, maxHeight);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		} else if (uri.startsWith("file")) {
			return decodeFileWithHandleOutOfMemery(Uri.parse(uri).getPath(), maxWidth, maxHeight);
		}
		return null;
	}
	
	private static Bitmap decodeFileWithHandleOutOfMemery(String absolutePath, int maxWidth, int maxHeight){
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(absolutePath, options);
		options.inSampleSize = ImageSizeAdapter.calculateInSampleSize(
				options, maxWidth, maxHeight);
		options.inJustDecodeBounds = false; 
		Bitmap bitmap = null;
		try{
			bitmap = BitmapFactory.decodeFile(absolutePath, options);
		}catch(OutOfMemoryError e){
			e.printStackTrace();
			System.gc();
			MainApplication.getImageLoader().getImageCache().trim();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
			try{
				bitmap = BitmapFactory.decodeFile(absolutePath, options);
			}catch(OutOfMemoryError e1){
				Log.w(TAG, "Decode file also out of memery when trim imagecache.");
			}
		}
		return bitmap;
	}

	public static String createCacheFile(File cacheDir, String key) {
		String absolutePath = new File(cacheDir, MD5Utils.md5(key) + ".png")
				.getAbsolutePath();
		return absolutePath;
	}
}
