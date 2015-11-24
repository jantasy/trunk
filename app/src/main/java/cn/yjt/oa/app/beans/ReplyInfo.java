package cn.yjt.oa.app.beans;

public class ReplyInfo {
    private long id;
    private long taskId;
    private String content;
    private UserSimpleInfo fromUser;
    private int mark;
    private String replyTime;
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    
    public long getTaskId() {
        return taskId;
    }
    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }
    
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    
    public UserSimpleInfo getFromUser() {
        return fromUser;
    }
    public void setFromUser(UserSimpleInfo fromUser) {
        this.fromUser = fromUser;
    }
    
    public int getMark() {
        return mark;
    }
    public void setMark(int mark) {
        this.mark = mark;
    }
    
    public String getReplyTime() {
        return replyTime;
    }
    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }
    
    
}
