package cn.yjt.oa.app.beans;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "MessageInfoContentCache")
public class MessageInfoContentCache extends Model {

	@Column(name="i_d",unique=true,onUniqueConflict = Column.ConflictAction.REPLACE)
	public String i_d;
	
	@Column(name = "updateTime")
	private String updateTime;

	@Column(name = "title")
	private String title;

	@Column(name = "content")
	private String content;

	@Column(name = "icon")
	private String icon;

	@Column(name = "id_")
	private long id_;

	@Column(name = "type")
	private String type;

	@Column(name = "payload")
	private String payload;

	@Column(name = "isRead")
	private int isRead; // 0.未读 1.已读

	public MessageInfo change2MessageInfo(){
		MessageInfo info = new MessageInfo();
		info.setId(this.getId_());
		info.setUpdateTime(this.getUpdateTime());
		info.setTitle(this.getTitle());
		info.setContent(this.getContent());
		info.setIcon(this.getIcon());
		info.setType(this.getType());
		info.setPayload(this.getPayload());
		info.setRead(this.getIsRead());
		return info;
	}
	
	public MessageInfoContentCache(){
		super();
	}
	public MessageInfoContentCache(MessageInfo info){
		this.setId(info.getId());
		this.setUpdateTime(info.getUpdateTime());
		this.setTitle(info.getTitle());
		this.setContent(info.getContent());
		this.setIcon(info.getIcon());
		this.setType(info.getType());
		this.setPayload(info.getPayload());
		this.setIsRead(info.getIsRead());
	}
	
	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public long getId_() {
		return id_;
	}

	public void setId(long id) {
		this.id_ = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public int getIsRead() {
		return isRead;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}

	
}
