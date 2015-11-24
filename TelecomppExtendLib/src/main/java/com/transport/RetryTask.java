package com.transport;

public class RetryTask {

	private int time = 3;// 重试次数
	private boolean async = false;// 是否开启新线程执行
	private TaskAndCallback task;// 要执行的任务

	public interface TaskAndCallback {
		/** 
		 * 要执行的任务
		 */
		public void task();

		/**
		 * 执行失败时要执行的逻辑
		 */
		public void failure();

		/**
		 * 是否执行成功的判断
		 */
		public boolean issuccess();
		/**
		 * 执行成功之后要执行的任务
		 */
		public void success();
	}

	/**
	 * 
	 * @param time
	 *            重试次数
	 * @param async
	 *            是否开新启线程执行
	 * @param task
	 *            要执行的任务
	 */
	public RetryTask(int time, boolean async, TaskAndCallback task) {
		this.time = time;
		this.async = async;
		this.task = task;
	}

	/**
	 * 开始进行retry操作
	 */
	public void start() {
		if (async) {
			// 开启新线程进行操作
			new Thread() {
				public void run() {
					tryDo();
				};
			}.start();
		} else {
			// 在当前线程中进行操作
			tryDo();
		}
	}
	/**
	 * 重试操作
	 */
	private void tryDo(){
		int i = 0;
		for (; i < time && !task.issuccess(); i++) {
			task.task();
		}
		if(i>=time&&!task.issuccess()){
			task.failure();//执行失败
		}else{
			task.success();//执行成功
		}
	}
}
