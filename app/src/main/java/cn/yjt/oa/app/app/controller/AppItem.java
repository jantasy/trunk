package cn.yjt.oa.app.app.controller;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.common.http.InvocationError;

import java.io.File;

import android.content.Context;
import cn.yjt.oa.app.app.download.AppDownloadManager.AppDownloadListener;
import cn.yjt.oa.app.app.download.AppDownloadManager.AppDownloadTask;
import cn.yjt.oa.app.app.download.AppDownloadManager.Status;
import cn.yjt.oa.app.app.utils.AppUtils;
import cn.yjt.oa.app.beans.AppInfo;

public class AppItem implements Action , AppDownloadListener{
	protected OnButtonRefreshListener listener;
	protected Context context;
	protected AppInfo appInfo;
	protected AppItemDownloadTask task;
	public AppItem(Context context, AppInfo appInfo) {
		this.context = context;
		this.appInfo = appInfo;
	}
	
	@Override
	public void doAction() {
		if (AppStatus.INSTALLED == getStatus()) {
			doInstalledAction();
		} else {
			doNotInstalledAction();
		}
		if (task != null) {
			task.setItemListener(this);
		}
	}
	
	public void doNotInstalledAction() {
		if (task == null) {
			task = new AppItemDownloadTask(context, appInfo);
		}
		task.doAction();
	}

	public AppStatus getStatus() {
		if (AppUtils.isAppInstalled(context, appInfo.getPackageName())) {
			return AppStatus.INSTALLED;
		}
		return AppStatus.UNINSTALLED;
	}

	protected void doInstalledAction() {
		AppUtils.open(context, appInfo.getPackageName());

         /*记录操作 0504*/
        OperaEventUtils.recordOperation(OperaEvent.OPERA_DASHBOARD_ADD_ENTERPRISE_APP);
	}

	public String getStatusText() {
		String text = "下载";
		if (AppStatus.INSTALLED == getStatus()) {
			text = "启动";
		} else {
			if (AppUtils.isAppExsit(AppDownloadTask.getAppDownloadedFile(appInfo))) {

				if (task == null) {
					task = new AppItemDownloadTask(context, appInfo);
					task.setStatus(TaskStatus.FINISHED);
				}
			}
		}
		
		if (task != null) {
			if (task.getStatus() == TaskStatus.UNDOWNLOAD) {
				text = "下载";
			} else if (task.getStatus() == TaskStatus.PAUSING) {
				text = "暂停";
			} else if (task.getStatus() == TaskStatus.WAITING) {
				text = "等待";
			} else if (task.getStatus() == TaskStatus.FINISHED) {
				if (AppStatus.INSTALLED == getStatus()) {
					text = "启动";
				} else{
					text = "安装";
				}
			} else {
				text = "下载";
			}
		}
		return text;
	}
	
	public int getProgress() {
		if(task != null){
			return (int)task.getTask().getProgress();
		}
		return 0;
	}

	public int getTotal() {
		if(task != null){
			return (int)task.getTask().getTotal();
		}
		return 0;
	}
	
	public void registerListener(){
		if (task == null) {
			task = new AppItemDownloadTask(context, appInfo);
		}
		if (task.isExistDownloadTask(appInfo)) {
			task.setItemListener(this);
		}
	}
	
	@Override
	public void doAction(int position) {
		
	}

	@Override
	public void onError(InvocationError error) {
		refresh();
	}

	@Override
	public void onFinished(File file) {
		refresh();
	}
	
	private static final int UPDATE_GAP = 1000;
	private long lastUpdateTime = 0;
	
	@Override
	public void onProgress(long progress, long total) {
		long currentTime = System.currentTimeMillis();
		if(lastUpdateTime == 0 || currentTime - lastUpdateTime > UPDATE_GAP || progress == total ){
			lastUpdateTime = currentTime;
			refresh();
		}
	}

	@Override
	public void onStarted() {
		refresh();
	}

	@Override
	public void onStatus(Status status) {
		refresh();
	}
	private void refresh(){
		if(listener != null){
			listener.onRefresh();
		}
	}
	public void setOnButtonRefreshListener(OnButtonRefreshListener listener){
		this.listener = listener;
	}
}
