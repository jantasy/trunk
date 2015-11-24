package cn.yjt.oa.app.beans;

public class TaskCreationResponseModel {
    private String code;
    private String description;
    private TaskInfo taskInfo;
    
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
    public TaskInfo getTaskInfo() {
        return taskInfo;
    }
    public void setTaskInfo(TaskInfo taskInfo) {
        this.taskInfo = taskInfo;
    }
    
    
}
