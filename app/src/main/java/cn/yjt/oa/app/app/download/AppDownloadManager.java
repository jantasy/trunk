package cn.yjt.oa.app.app.download;

import io.luobo.common.http.FileClient;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.ProgressListener;
import io.luobo.common.http.TaskController;
import io.luobo.common.utils.MD5Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.app.AppRequest;
import cn.yjt.oa.app.app.AppRequest.AppInfoSingleCallback;
import cn.yjt.oa.app.app.utils.NotificationHolder;
import cn.yjt.oa.app.beans.AppInfo;
import cn.yjt.oa.app.http.FileClientFactory;

public class AppDownloadManager {
	public enum Status {
		/**
		 * queued, wait to start
		 * 
		 */
		IDLE, 
		/**
		 * started, will receive progress soon
		 */
		STARTED, 
		/**
		 * paused
		 */
		PAUSED,
		/**
		 * finished
		 */
		FINISHED
	}
	
	public interface AppDownloadListener extends ProgressListener<File> {
		void onStatus(Status status);
	}
	
	public static class AppDownloadTask implements AppDownloadListener, TaskController {
		private final String pkg;
		private AppInfo appInfo;
		private long progress;
		private long total;
		private ArrayList<AppDownloadListener> listeners = new ArrayList<AppDownloadListener>();
		private Status status = Status.IDLE;
		private long progressParam0;
		private long progressParam1;
		private TaskController taskController;
		private AppDownloadListener listener;
		
		public AppDownloadTask(AppInfo appInfo) {
			this.appInfo = appInfo;
			this.pkg = appInfo.getPackageName();
		}
		
		public AppDownloadTask(String pkg) {
			this.pkg = pkg;
		}
		
		public AppInfo getAppInfo(){
			return appInfo;
		}
		
		public void setListener(AppDownloadListener listener){
			this.listener = listener;
			if(listener != null){
				listener.onStatus(status);
				if (Status.IDLE != status) {
					listener.onProgress(progressParam0, progressParam1);
				}
			}
		}
		
		public void registListener(AppDownloadListener listener) {
			listeners.add(listener);
			listener.onStatus(status);
			if (Status.IDLE != status) {
				listener.onProgress(progressParam0, progressParam1);
			}
		}
		
		public void unregistListener(AppDownloadListener listener) {
			listeners.remove(listener);
		}
		
		public void startDownload() {
			if(appInfo == null){
				new AppRequest().getAppInfo(pkg,new AppInfoSingleCallback() {
					
					@Override
					public void onAppInfoSingleResponse(AppInfo response) {
						appInfo = response;
						download(appInfo);
					}
				});
			}else{
				download(appInfo);
			}
			
		}
		private static String generateAppName(String url){
			return MD5Utils.md5(url);
		}
		
		private static File getDownloadFile(String name) {
			return new AppRequest().getDownloadDirectory(name);
		}
		
		public static File getAppDownloadedFile(AppInfo appInfo){
			return getDownloadFile(generateAppName(appInfo.getDownUrl()));
		}
		
		private File getDefaultDownloadFile(){
			return AppDownloadTask.getAppDownloadedFile(appInfo);
		}
		
		public File getAppDownloadedFile(){
			if(status == Status.FINISHED){
				return getDefaultDownloadFile();
			}
			return null;
		}
		public long getProgress(){
			return progress;
		}
		public long getTotal(){
			return total;
		}
		public void download(AppInfo appInfo){
			FileClient client = FileClientFactory.createSingleThreadFileClient(MainApplication.getAppContext());
			try {
				taskController = client.download(appInfo.getDownUrl(), getDefaultDownloadFile(), new ProgressListener<File>() {
					@Override
					public void onError(InvocationError arg0) {
						AppDownloadTask.this.onError(arg0);
					}

					@Override
					public void onFinished(File arg0) {
						AppDownloadTask.this.onFinished(arg0);
					}

					@Override
					public void onProgress(long arg0, long arg1) {
						if(arg1 == 0) {
							return;
						}
						progress = arg0;
						total = arg1;
						AppDownloadTask.this.onProgress(arg0, arg1);
					}

					@Override
					public void onStarted() {
						AppDownloadTask.this.onStarted();
					}
				});
				start();
				
			} catch (InvocationError e) {
				return;
			}
		}
		
		@Override
		public void onError(InvocationError arg0) {
			for (AppDownloadListener listener:listeners) {
				listener.onError(arg0);
			}
		}

		@Override
		public void onFinished(File arg0) {
			this.status = Status.FINISHED;
			finishTask(AppDownloadTask.this);
			if(this.listener != null){
				listener.onFinished(arg0);
			}
			for (AppDownloadListener listener:listeners) {
				listener.onFinished(arg0);
			}
		}

		@Override
		public void onProgress(long arg0, long arg1) {
			progressParam0 = arg0;
			progressParam1 = arg1;
			if(this.listener != null){
				this.listener.onProgress(arg0, arg1);
			}
			for (AppDownloadListener listener:listeners) {
				listener.onProgress(arg0, arg1);
			}
		}

		@Override
		public void onStarted() {
			for (AppDownloadListener listener:listeners) {
				listener.onStarted();
			}
		}

		@Override
		public void onStatus(Status status) {
			this.status = status;
			if(this.listener != null){
				this.listener.onStatus(status);
			}
			for (AppDownloadListener listener:listeners) {
				listener.onStatus(status);
			}
		}

		@Override
		public void stop() {
			taskController.stop();
		}
		
		@Override
		public void start() {
			taskController.start();
			AppDownloadTask.this.onStatus(Status.STARTED);
		}
		
		@Override
		public void resume() {
			taskController.resume();
			AppDownloadTask.this.onStatus(Status.STARTED);
		}
		
		@Override
		public void pause() {
			taskController.pause();
			AppDownloadTask.this.onStatus(Status.PAUSED);
		}
		
		public Status getStatus() {
			return status;
		}
	}
	
	private static Map<String, AppDownloadTask> tasks = new HashMap<String, AppDownloadTask>();
	private static Queue<AppDownloadTask> taskQueue = new LinkedList<AppDownloadTask>();
	private static Thread downloadThread;
	private static final int MAX_DOWNLOAD_COUNT = 2;
	private static ArrayList<AppDownloadTask> startedTask = new ArrayList<AppDownloadTask>(MAX_DOWNLOAD_COUNT);
	
	public static ArrayList<AppDownloadTask> getAppDownloadTasks(){
		synchronized (startedTask) {
			return startedTask;
		}
	}
	public static ArrayList<AppInfo> getAppInfosFromTask(){
		ArrayList<AppInfo> list = new ArrayList<AppInfo>();
		if(getAppDownloadTasks() != null){
			for(AppDownloadTask task:getAppDownloadTasks()){
				list.add(task.getAppInfo());
			}
		}
		return list;
	}
	
	public static List<String> getPackageNamesWithAppInfo(){
		List<String> list = new ArrayList<String>();
		for (AppInfo appInfo : getAppInfosFromTask()) {
			list.add(appInfo.getPackageName());
		}
		return list;
	}
	
	public static int getIndexWithPackageName(List<AppInfo> list,String pkg){
		if(list != null){
			for(AppInfo appInfo:list){
				if(pkg.equals(appInfo.getPackageName())){
					return list.indexOf(appInfo);
				}
			}
		}
		return -1;
	}
	
	public static AppDownloadTask getAppDownloadTaskForPackage(String pkg) {
		synchronized (tasks) {
			return tasks.get(pkg);
		}
	}
	public static Map<String,AppDownloadTask> getAppDownloadedTasks(){
		synchronized (tasks) {
			return tasks;
		}
	}
	private static void addTask(AppDownloadTask task) {
		synchronized (tasks) {
			if(!tasks.containsKey(task.pkg)){
				tasks.put(task.pkg, task);
			}
		}
		addTaskToQueue(task);
	}
	
	private static void addTaskToQueue(AppDownloadTask task) {
		synchronized (taskQueue) {
			if(!taskQueue.contains(task)){
				taskQueue.add(task);
				startThread();
			}
		}
	}
	
	private static void startThread() {
		if (downloadThread == null) {
			downloadThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					while (true) {
						AppDownloadTask task = null;
						synchronized (taskQueue) {
							if (!taskQueue.isEmpty()) {
								task = taskQueue.poll();
							}
						}
						if (task == null) {
							synchronized (taskQueue) {
								if (taskQueue.isEmpty()) {
									downloadThread = null;
									return;
								}
							}
						} else {
							task.startDownload();
							startedTask.add(task);
							synchronized (startedTask) {
								if (startedTask.size() >= MAX_DOWNLOAD_COUNT) {
									try {
										startedTask.wait();
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}
							
						}
						
					}
					
				}
			}, "app download thread");
			downloadThread.start();
		}
	}
	
	private static void finishTask(AppDownloadTask task) {
		synchronized (startedTask) {
			startedTask.remove(task);
			startedTask.notify();
		}
	}
	public static boolean isTaskExist(String pkg){
		return getAppDownloadTaskForPackage(pkg) != null;
	}
	
	private static AppDownloadTask downloadApp(AppInfo appInfo) {
		AppDownloadTask task = new AppDownloadTask(appInfo);
		addTask(task);
		return task;
	}
	
	private static AppDownloadTask downloadApp(String pkg) {
		AppDownloadTask task = new AppDownloadTask(pkg);
		addTask(task);
		return task;
	}
	
	private static final int UPDATE_GAP = 1000;
	private static long lastUpdatedTime = 0;
	private static NotificationHolder notificationHolder;
	//private static AtomicInteger notifId = new AtomicInteger(0);
	public static AppDownloadTask downloadWithNotification(Context context,String pkg){
		AppDownloadTask appDownloadTask = null;
		//int id = 0;
		if(AppDownloadManager.isTaskExist(pkg)){
			appDownloadTask = AppDownloadManager.getAppDownloadTaskForPackage(pkg);
		}else{
			appDownloadTask = downloadApp(pkg);
			//id = notifId.incrementAndGet();
			if(appDownloadTask != null){
				registNotificationListenerForDownloadTask(context,appDownloadTask,pkg);
			}
		}
		return appDownloadTask;
	}
	public static AppDownloadTask downloadWithNotification(Context context,AppInfo appInfo){
		AppDownloadTask appDownloadTask = null;
		String pkg = appInfo.getPackageName();
		if(AppDownloadManager.isTaskExist(pkg)){
			appDownloadTask = AppDownloadManager.getAppDownloadTaskForPackage(pkg);
		}else{
			appDownloadTask = downloadApp(appInfo);
			if(appDownloadTask != null){
				registNotificationListenerForDownloadTask(context,appDownloadTask,pkg);
			}
		}
		return appDownloadTask;
	}
	public static void registNotificationListenerForDownloadTask(final Context context,final AppDownloadTask task ,final String pkg){
		notificationHolder = new NotificationHolder(context);
		AppDownloadListener listener = new AppDownloadListener() {
			
			@Override
			public void onStarted() {
				
			}
			
			@Override
			public void onProgress(long progress, long total) {
				if (lastUpdatedTime == 0 || System.currentTimeMillis() - lastUpdatedTime > UPDATE_GAP || progress == total){
					lastUpdatedTime = System.currentTimeMillis();
					notificationHolder.sendNotification(task);
				}
			}
			
			@Override
			public void onFinished(File file) {
				if(file != null){
					notificationHolder.sendNotification(task);
					appInstall(context,file);
				}
			}
			
			@Override
			public void onError(InvocationError error) {
				// TODO 错误处理
				error.printStackTrace();
			}
			
			@Override
			public void onStatus(Status status) {
				notificationHolder.sendNotification(task);
			}
		};
		task.registListener(listener);
	}
	private static void appInstall(Context context,File file){
		Intent intent = new Intent(Intent.ACTION_VIEW); 
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive"); 
		context.startActivity(intent);
	}

}
