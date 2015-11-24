package cn.yjt.oa.app.beans;

public class TaskListRequestModel {
    private String requestType;
    private String statusFilter;
    private int from;
    private int max;
    
    public TaskListRequestModel(String requestType, String statusFilter,
            int from, int max) {
        this.requestType = requestType;
        this.statusFilter = statusFilter;
        this.from = from;
        this.max = max;
    }
    
    public String getRequestType() {
        return requestType;
    }
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
    
    public String getStatusFilter() {
        return statusFilter;
    }
    public void setStatusFilter(String statusFilter) {
        this.statusFilter = statusFilter;
    }
    
    public int getFrom() {
        return from;
    }
    public void setFrom(int from) {
        this.from = from;
    }
    
    public int getMax() {
        return max;
    }
    public void setMax(int max) {
        this.max = max;
    }
    
    
    
}
