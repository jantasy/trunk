package cn.yjt.oa.app.beans;

public class CustJoinInfo {
	
	private long id;
	private long userId;			//申请人ID
	private long custId;			//申请加入的公司ID
	private String createTime;		//申请时间
	private String message;			//申请附言（申请理由等）
	
	private long handleUserId;		//处理人ID
	private int handleStatus;		//处理状态：（0：待审核 1：审核通过 -1：审核不通过）
	private String handleTime;		//处理时间
	private String handleMessage;	//处理附言（拒绝理由等）
	
	private String title;			//推送消息的标题
	private String content;			//推送消息的内容
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getCustId() {
		return custId;
	}
	public void setCustId(long custId) {
		this.custId = custId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public long getHandleUserId() {
		return handleUserId;
	}
	public void setHandleUserId(long handleUserId) {
		this.handleUserId = handleUserId;
	}
	public int getHandleStatus() {
		return handleStatus;
	}
	public void setHandleStatus(int handleStatus) {
		this.handleStatus = handleStatus;
	}
	public String getHandleTime() {
		return handleTime;
	}
	public void setHandleTime(String handleTime) {
		this.handleTime = handleTime;
	}
	public String getHandleMessage() {
		return handleMessage;
	}
	public void setHandleMessage(String handleMessage) {
		this.handleMessage = handleMessage;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
