package cn.yjt.oa.app.app.utils;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import cn.yjt.oa.app.app.AppRequest;
import cn.yjt.oa.app.app.activity.AdditionalIcon;
import cn.yjt.oa.app.app.activity.AppEntranceActivity;

public class AdditionalIconUtils {
	public static final String EXTRA_RESULT_PKG = "pkg";
	public static final String EXTRA_RESULT_INDEX = "index";
	public static final String EXTRA_RESULT_ICON_URL = "iconUrl";
	public static final String EXTRA_RESULT_APPNAME = "appName";
	public static final String EXTRA_RESULT_FILE = "file";
	public static final String EXTRA_REQUEST_ADDICONS = "AdditionalIcons";
	public static final String EXTRA_REQUEST_ADDITIONAL = "additional";
	public static final String EXTRA_REQUEST_REQUESTCODE = "requestCode";
	
	public static void startAppActiviyForResult(Activity activity, ArrayList<? extends AdditionalIcon> list, ArrayList<String> addedPackages,int requestCode){
		Intent intent = new Intent(activity, AppEntranceActivity.class);
		intent.setAction(AppRequest.ACTION_APP_ADD);
		intent.putExtra(EXTRA_REQUEST_REQUESTCODE, requestCode);
		intent.putStringArrayListExtra(EXTRA_REQUEST_ADDITIONAL, addedPackages);
		intent.putParcelableArrayListExtra(EXTRA_REQUEST_ADDICONS, list);
		activity.startActivityForResult(intent, requestCode);
	}
	
	public static void startAppActiviyForResult(Fragment fragment, Context context, ArrayList<? extends AdditionalIcon> list,ArrayList<String> addedPackages, int requestCode){
		Intent intent = new Intent(context, AppEntranceActivity.class);
		intent.setAction(AppRequest.ACTION_APP_ADD);
		intent.putExtra(EXTRA_REQUEST_REQUESTCODE, requestCode);
		intent.putStringArrayListExtra(EXTRA_REQUEST_ADDITIONAL, addedPackages);
		intent.putParcelableArrayListExtra(EXTRA_REQUEST_ADDICONS, list);
		fragment.startActivityForResult(intent, requestCode);
	}
}
