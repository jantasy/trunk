package cn.yjt.oa.app.app.controller;

import android.content.Context;
import cn.yjt.oa.app.app.download.AppDownloadManager.AppDownloadTask;
import cn.yjt.oa.app.app.utils.AppUtils;
import cn.yjt.oa.app.beans.AppInfo;

public class AppCardNormalItem extends AppItem {
	public AppCardNormalItem(Context context, AppInfo data) {
		super(context, data);
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
				if (getTotal() != 0) {
					return getProgress() * 100 / getTotal() + "%";
				}
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
}
