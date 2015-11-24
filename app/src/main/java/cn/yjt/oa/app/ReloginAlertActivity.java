package cn.yjt.oa.app;

import java.lang.reflect.Method;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import cn.yjt.oa.app.component.AlertDialogBuilder;
import cn.yjt.oa.app.personalcenter.LoginActivity;

public class ReloginAlertActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AlertDialog.Builder builder = AlertDialogBuilder.newBuilder(this);
		builder.setTitle("登录失效").setMessage("您的密码不正确，请重新登录").setPositiveButton("重新登录", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				LoginActivity.launch(ReloginAlertActivity.this);
				finish();
			}
		}).setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		}).show();
	}
	
	public static void alertAndClearTask(){
		Context context = MainApplication.getAppContext();
//		ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
//		List<RunningTaskInfo> runningTasks = activityManager.getRunningTasks(10);
//		for (RunningTaskInfo runningTaskInfo : runningTasks) {
//			String packageName = runningTaskInfo.baseActivity.getPackageName();
//			if(packageName.equals(context.getPackageName())){
//				removeTask(activityManager, runningTaskInfo.id);
//			}
//		}
		Intent intent = new Intent(context, ReloginAlertActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	private static void removeTask(ActivityManager activityManager,int taskId){
		System.out.println("removeTask:"+taskId);
		try {
			Class<?> clazz = Class.forName(ActivityManager.class.getName());
			Method method = clazz.getDeclaredMethod("removeTask", int.class,int.class);
			method.invoke(activityManager, taskId,0);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
}
