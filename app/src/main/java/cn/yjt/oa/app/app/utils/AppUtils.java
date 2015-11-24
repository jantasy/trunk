package cn.yjt.oa.app.app.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
/**
 * 静默安装所需权限
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 * <uses-permission android:name="android.permission.INSTALL_PACKAGES" />
 * <uses-permission android:name="android.permission.DELETE_PACKAGES" />
 * <uses-permission android:name="android.permission.CLEAR_APP_CACHE" />
 * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
 * <uses-permission android:name="android.permission.CLEAR_APP_USER_DATA" />
 * 
 */
public class AppUtils {
	public static String install(String apkAbsolutePath) {
		String[] args = { "pm", "install", "-f", apkAbsolutePath };
		String result = "";
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		Process process = null;
		InputStream errIs = null;
		InputStream inIs = null;

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read = -1;
			process = processBuilder.start();
			errIs = process.getErrorStream();
			while ((read = errIs.read()) != -1) {
				baos.write(read);
			}

			inIs = process.getInputStream();
			while ((read = inIs.read()) != -1) {
				baos.write(read);
			}
			
			byte[] data = baos.toByteArray();
			result = new String(data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(process != null){
				process.destroy();
			}
		}

		return result;

	}
	// 获取系统所有已安装的应用
	public static List<ApplicationInfo> getApplicationInfos(Context context){
		return context.getPackageManager().getInstalledApplications(0);
	}

	// 获取系统所有已安装的可启动应用
	public static List<ResolveInfo> getResolveInfos(Context context) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		PackageManager pm = context.getPackageManager();
		return pm.queryIntentActivities(intent, 0);
	}
	
	// 获取系统已安装的所有应用的包名
	public static List<String> getPackageNamesInstalled(Context context) {
		List<String> packageNames = new ArrayList<String>();
		for(ApplicationInfo appInfo : getApplicationInfos(context)){
			packageNames.add(appInfo.packageName);
		}
		return packageNames;
	}
	// 打开应用
	public static boolean open(Context context,String packageName) {
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		
		resolveIntent.setPackage(packageName);
		List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(resolveIntent, 0);
		if (resolveInfoList != null && resolveInfoList.size() > 0) {
			ResolveInfo resolveInfo = resolveInfoList.get(0);
			String activityPackageName = resolveInfo.activityInfo.packageName;
			String className = resolveInfo.activityInfo.name;

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			ComponentName componentName = new ComponentName(activityPackageName, className);
			if(!(context instanceof Activity)){
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			intent.setComponent(componentName);
			context.startActivity(intent);
			return true;
		}
		return false;
	}
	
	// 判断系统中app是否安装
	public static boolean isAppInstalled(Context context,String packageName){
		if(getPackageInfo(context, packageName) != null){
			return true;
		}
		return false;
	}
	// 判断app安装文件是否存在
	public static boolean isAppExsit(String archiveFilePath){
		if(new File(archiveFilePath).exists()){
			return true;
		}
		return false;
	}
	// 判断app安装文件是否存在
	public static boolean isAppExsit(File file){
		if(file.exists()){
			return true;
		}
		return false;
	}
	// 根据apk文件路径获取应用信息
	public static ApplicationInfo getApplicationInfo(Context context , String archiveFilePath){
		if(context != null && archiveFilePath != null && archiveFilePath != ""){
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
			if(info != null){
				return info.applicationInfo;
			}
		}
		return null;
	}
	
	public static ApplicationInfo getApplication(Context context,String packageName){
		PackageManager pm = context.getPackageManager();
		try {
			pm.getApplicationInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// 根据包名获取应用信息
	public static ApplicationInfo getApplicationInfoWithPackageName(Context context , String packageName){
		return getPackageInfo(context ,packageName).applicationInfo;
	}
	// 根据包名获取应用信息
	public static PackageInfo getPackageInfo(Context context , String packageName){
		if(context != null && packageName != null && packageName != ""){
			try {
				return context.getPackageManager().getPackageInfo(packageName, 0);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	public static void appInstall(Context context,File file){
		Intent intent = new Intent(Intent.ACTION_VIEW); 
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive"); 
		context.startActivity(intent);
	}
	
	public static boolean isComponentExist(Context context, String pkg, String cls){
		try {
			ComponentName component= new ComponentName(pkg, cls);
			return context.getPackageManager().getActivityInfo(component, 0) != null;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return false;
	}
}
