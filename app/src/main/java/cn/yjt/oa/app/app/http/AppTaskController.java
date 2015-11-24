package cn.yjt.oa.app.app.http;

import io.luobo.common.http.TaskController;
import cn.yjt.oa.app.app.AppRequest.AppProgressListener;

public class AppTaskController{
	private AppProgressListener listener;
	private TaskController taskController;
	
	public AppTaskController(TaskController taskController,AppProgressListener listener){
		this.listener = listener;
		this.taskController = taskController;
	}
	public void start(){
		taskController.start();
		listener.onStateChanged(State.RUNNING);
	}
	public void pause(){
		taskController.pause();
		listener.onStateChanged(State.PAUSED);
	}
	public void resume(){
		taskController.resume();
		listener.onStateChanged(State.RUNNING);
	}
	public void stop(){
		taskController.stop();
		listener.onStateChanged(State.STOPED);
	}
}
