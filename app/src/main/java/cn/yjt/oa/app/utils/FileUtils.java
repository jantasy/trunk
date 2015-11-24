package cn.yjt.oa.app.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import cn.yjt.oa.app.MainActivity;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.app.utils.LogUtils;
import cn.yjt.oa.app.imageloader.DiskImageCache;

public class FileUtils {
	static final String TAG = FileUtils.class.getSimpleName();
	
	public static String getYjtDir ()
	{
		MainActivity.userName=AccountManager.getCurrentLogiInfo(MainApplication.getAppContext()).getPhone();
		return  Environment.getExternalStorageDirectory()
				+ "/yijitong/" + MainActivity.userName + "/";
	}
	public static boolean deleteFile(String path){
		return new File(path).delete();
	}
	
	public static boolean copy(InputStream in,File dist ){
		FileOutputStream out  = null;
		File tempFile = new File(dist.getAbsolutePath()+"."+System.currentTimeMillis()+".tmp");
		if(tempFile.getParentFile()!=null&&!tempFile.getParentFile().exists()){
			tempFile.getParentFile().mkdirs();
		}
		try {
			 out = new FileOutputStream(tempFile);
			 int len = 0;
			 byte[] b = new byte[1024*8];
			 while((len = in.read(b))!= -1){
				 out.write(b,0,len);
			 }
			 tempFile.renameTo(dist);
			 return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static boolean copy(File src,File dist){
		FileInputStream stream;
		try {
			stream = new FileInputStream(src);
			return copy(stream, dist);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean compressImageFile(String path,int maxWidth,int maxHeight){
		Log.d(TAG, String.format("compressImageFile(path=%s,maxWidth=%d,maxHeight=%d)", path,maxWidth,maxHeight));
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		if(opts.outWidth>opts.outHeight){
			int temp = maxHeight;
			maxHeight = maxWidth;
			maxWidth = temp;
		}
		Bitmap bitmap = new DiskImageCache(MainApplication.getAppContext()).getBitmap(Uri.fromFile(new File(path)).toString(), maxWidth, maxHeight);
//		Bitmap bitmap2 = ImageSizeAdapter.scaledBitmap(bitmap, maxWidth, maxHeight);
//		bitmap.recycle();
//		bitmap = bitmap2;
		FileOutputStream stream = null;
		
		try {
			stream = new FileOutputStream(path);
			bitmap.compress(CompressFormat.JPEG, 100, stream);
			bitmap.recycle();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (Throwable e) {
			e.printStackTrace();
		}finally{
			if(stream != null){
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return false;
	}
	
	public static File createFileInUserFolder(String fileName){
		File file = new File(getYjtDir(), fileName);
		File dir = file.getParentFile();
		if(!dir.exists()){
			dir.mkdirs();
		}
		return file;
	}
	
	public static File getUserFolder(){
		return new File(getYjtDir());
	}
	// 字节转成带单位的字符串
	public static String sizeFromBToString (double size) {
		String unit = "B";
		if (size >= 1024) {
			size /= 1024.0;
			LogUtils.i("===K size = " + size);
			unit = "K";
			if (size >= 1024) {
				size /= 1024.0;
				unit = "M";
				LogUtils.i("===M size = " + size);
			}
		}
		DecimalFormat decimalFormat = new DecimalFormat("#.00");
		return decimalFormat.format(size) + unit;
	}
	
}
