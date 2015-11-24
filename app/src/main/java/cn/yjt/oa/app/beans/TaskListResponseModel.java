package cn.yjt.oa.app.beans;

public class TaskListResponseModel {
    private String code;
    private String description;
    private ListSlice<TaskInfo> payload;
    
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
    
    public ListSlice<TaskInfo> getPayload() {
        return payload;
    }
    public void setPayload(ListSlice<TaskInfo> payload) {
        this.payload = payload;
    }
    
    
}
