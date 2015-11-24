package cn.yjt.oa.app.beans;

public class TaskCloseModel {
    private String action;
    
    public TaskCloseModel(String action) {
        this.setAction(action);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
