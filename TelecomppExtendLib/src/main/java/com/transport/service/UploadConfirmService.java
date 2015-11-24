package com.transport.service;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.telecompp.ContextUtil;
import com.telecompp.util.LoggerHelper;
import com.transport.db.dao.PConfirmDao;
import com.transport.db.dao.PIsLoadSuccessDao;

public class UploadConfirmService extends Service {
	private static final long SUB_TIME = 1000 * 60 * 3;
	private TimerTask task = null;
	private Timer timer;
	
	private static LoggerHelper logger = new LoggerHelper(UploadConfirmService.class);
 
	@Override
	public void onCreate() {
		super.onCreate();
		// 初始化
		ContextUtil.setInstance(this.getApplicationContext());
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("UploadConfirmService", "==wb===startService onStartCommand====");
		// 定时上未确认的交易记录
		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				try {					
					Log.i("UploadConfirmService", "==wb===startService onStartCommand==run= 上传记录=");
					//	正常的异常处理
					PConfirmDao dao = new PConfirmDao();
					//	这里需要查询数据库中所有需要上传的记录上传到服务器
					dao.subMitConfirm(null);
					
					//	极特殊的异常处理
					PIsLoadSuccessDao isDao = new PIsLoadSuccessDao();
					isDao.subExtremeRecord(UploadConfirmService.this);
				} catch (Exception e) {
					e.printStackTrace();
					logger.printLogOnSDCard(e.getStackTrace().toString());
				}
			}
		};
		timer.schedule(task, 1000 * 30, SUB_TIME);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (task != null) {
			timer.cancel();
			timer = null;
			task.cancel();
			task = null;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
