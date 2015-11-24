package cn.yjt.oa.app.app.controller;

import android.content.Context;
import cn.yjt.oa.app.app.download.AppDownloadManager;
import cn.yjt.oa.app.app.download.AppDownloadManager.AppDownloadListener;
import cn.yjt.oa.app.app.download.AppDownloadManager.AppDownloadTask;
import cn.yjt.oa.app.app.download.AppDownloadManager.Status;
import cn.yjt.oa.app.app.utils.AppUtils;
import cn.yjt.oa.app.beans.AppInfo;

public class AppItemDownloadTask{
	protected TaskStatus status;
	protected AppDownloadTask task;
	protected Context context;
	protected AppInfo appInfo;
	
	public AppItemDownloadTask(Context context, AppInfo appInfo) {
		this.context = context;
		this.appInfo = appInfo;
		status = getStatus();
	}
	
	public void doAction() {
		if(TaskStatus.FINISHED == status) {
			AppUtils.appInstall(context, AppDownloadTask.getAppDownloadedFile(appInfo));
		}
		if(TaskStatus.PAUSING == status) {
			task.resume();
		}
		if(TaskStatus.RUNNING == status) {
			task.pause();
		}
		if(TaskStatus.UNDOWNLOAD == status) {
			task = AppDownloadManager.downloadWithNotification(context, appInfo);
		}
		if(TaskStatus.WAITING == status) {
			
		}
	}
	public TaskStatus getStatus(){
		boolean taskExists = AppDownloadManager.isTaskExist(appInfo.getPackageName());
		// 判断是否已经存在下载任务，如果存在获取当前的下载状态，如果不存在，则返回当前状态为“未下载”
		if(taskExists) {
			if(task == null){
				task = AppDownloadManager.getAppDownloadTaskForPackage(appInfo.getPackageName());
			}
			Status innerStatus = task.getStatus();
			if(Status.STARTED == innerStatus) {
				status = TaskStatus.RUNNING;
			}
			if(Status.PAUSED == innerStatus) {
				status = TaskStatus.PAUSING;
			}
			if(Status.IDLE == innerStatus) {
				status = TaskStatus.WAITING;
			}
			if(Status.FINISHED == innerStatus) {
				status = TaskStatus.FINISHED;
			}
		} else {
			status = TaskStatus.UNDOWNLOAD;
		}
		return status;
	}
	public void setStatus(TaskStatus status){
		this.status = status;
	}
	public AppDownloadTask getTask(){
		if(task == null){
			task = AppDownloadManager.getAppDownloadTaskForPackage(appInfo.getPackageName());
		}
		return task;
	}
	public boolean isExistDownloadTask(AppInfo appInfo){
		if(getStatus() != TaskStatus.UNDOWNLOAD){
			return true;
		}
		return false;
	}
	public void setItemListener(AppDownloadListener listener){
		if (task != null) {
			task.setListener(listener);
		}
	}
}
