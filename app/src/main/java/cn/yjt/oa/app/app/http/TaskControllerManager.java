package cn.yjt.oa.app.app.http;

public class TaskControllerManager {
	public static TaskControllerManager manger = new TaskControllerManager();
	public static TaskControllerManager newInstance(){
		return manger;
	}
}
