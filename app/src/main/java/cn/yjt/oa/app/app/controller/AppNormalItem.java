package cn.yjt.oa.app.app.controller;

import android.content.Context;
import cn.yjt.oa.app.app.download.AppDownloadManager.AppDownloadTask;
import cn.yjt.oa.app.app.utils.AppUtils;
import cn.yjt.oa.app.beans.AppInfo;

public class AppNormalItem extends AppItem {
	private AppInfo appInfo;
	private AppItemDownloadTask task;
	private Context context;

	public AppNormalItem(Context context, AppInfo data) {
		super(context, data);
		this.context = context;
		appInfo = (AppInfo) data;
	}

	@Override
	public void doAction() {
		super.doAction();
		if (task != null && task.getTask() != null) {
			task.getTask().setListener(this);
		}
	}

	public void doNotInstalledAction() {
		if (task == null) {
			task = new AppItemDownloadTask(context, appInfo);
		}
		task.doAction();
	}

	protected void doInstalledAction() {
		AppUtils.open(context, appInfo.getPackageName());
	}
	@Override
	public void registerListener(){
		if(task == null){
			task = new AppItemDownloadTask(context, appInfo);
		}
		if(task.isExistDownloadTask(appInfo)){
			if (task.getTask() != null) {
				task.getTask().setListener(this);
			}
		}
	}
	@Override
	public AppStatus getStatus() {
		if (AppUtils.isAppInstalled(context, appInfo.getPackageName())) {
			return AppStatus.INSTALLED;
		}
		return AppStatus.UNINSTALLED;
	}

	@Override
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
			} else if (task.getStatus() == TaskStatus.RUNNING) {
				long total = task.getTask().getTotal();
				long progress = task.getTask().getProgress();
				if(total != 0){
					text = progress * 100 / total + "%";
				}
			} else if (task.getStatus() == TaskStatus.PAUSING) {
				text = "暂停";
			} else if (task.getStatus() == TaskStatus.WAITING) {
				text = "等待";
			} else if (task.getStatus() == TaskStatus.FINISHED) {
				text = "安装";
			} else {
				text = "下载";
			}
		}
		return text;
	}

}
