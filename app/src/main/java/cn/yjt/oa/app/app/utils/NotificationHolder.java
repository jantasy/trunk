package cn.yjt.oa.app.app.utils;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;

import java.io.File;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.download.AppDownloadManager.AppDownloadTask;
import cn.yjt.oa.app.app.download.AppDownloadManager.Status;
import cn.yjt.oa.app.beans.AppInfo;
import cn.yjt.oa.app.http.AsyncRequest;

public class NotificationHolder {
	private Context context;
	private NotificationManager manager;
	private NotificationCompat.Builder builder;
	private AppInfo appInfo;
	public NotificationHolder(Context context){
		this.context = context;
		manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		builder = new NotificationCompat.Builder(context);
	}
	public void sendNotification(AppDownloadTask task){
		int total = (int) task.getTotal();
		int progress = (int) task.getProgress();
		appInfo = task.getAppInfo();
		if(appInfo != null){
			int notifiId = appInfo.getPackageName().hashCode();
			if(total != 0){
				builder.setTicker(total == progress ?"下载完成":"开始下载...")
				.setContentTitle(appInfo.getName())
				.setContentText(total == progress ?"下载完成,点击安装":getStatus(task.getStatus()))
				.setContentInfo(progress*100/total+"%")
				.setProgress(total, progress, false)
				.setSmallIcon(R.drawable.notification_download_small_icon)
				.setAutoCancel(true)
				.setOngoing(true);
				AsyncRequest.getBitmap(appInfo.getIcon(), new Listener<Bitmap>() {
					
					@Override
					public void onResponse(Bitmap bitmap) {
						builder.setLargeIcon(bitmap);
					}
					
					@Override
					public void onErrorResponse(InvocationError error) {
						error.printStackTrace();
					}
				});
				if(task.getStatus() == Status.FINISHED){
					PendingIntent resultPendingIntent = PendingIntent.getActivity(
							context, notifiId,
							getInstallIntent(task),
							PendingIntent.FLAG_UPDATE_CURRENT);
					builder.setContentIntent(resultPendingIntent);
					builder.setOngoing(false);
				}
				manager.notify(appInfo.getPackageName(),notifiId, builder.build());
			}
		}
	}
	private Intent getInstallIntent(AppDownloadTask task){
		File appFile = task.getAppDownloadedFile();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(appFile),"application/vnd.android.package-archive");
		return intent;
	}
	private String getStatus(Status status){
		if(status == Status.FINISHED){
			return "完成下载";
		}else if(status == Status.IDLE){
			return "等待下载";
		}else if(status == Status.PAUSED){
			return "暂停下载";
		}
		return "正在下载,请耐心等待";
	}
	
	public static void clearNotification(Context context,String tag){
		if(context != null && tag != null ){
			NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			manager.cancel(tag, tag.hashCode());
		}
	}
}

