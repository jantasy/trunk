package cn.yjt.oa.app.beans;

/*
 * id	long	通知ID
from	string	来源：
“ADMIN”:系统管理员发布消息
“SYSTEM”：系统通告
title	string	通知标题
content	string	通知内容
createTime	string	通知时间
image	MutipartFile	

 */
public class NoticeCreateInfo {
	private long id;
	private int fromType = 0;
	private String title;
	private String content;
	private String createTime;
	private String image;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getFrom() {
		return fromType;
	}
	public void setFrom(int from) {
		this.fromType = from;
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
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	
	
}
