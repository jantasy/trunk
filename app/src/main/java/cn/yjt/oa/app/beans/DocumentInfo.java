package cn.yjt.oa.app.beans;

import java.util.List;

public class DocumentInfo {
	public static final String MOMENT = "SPLENDIDMOMENT";
	public static final String DOCUMENT = "DOCUMENT";
	private long id	;//ID
	private String name; //文档名称（文档中心使用）
	private String type	;//类型”DOCUMENT”文档 “SPLENDIDMOMENT”精彩瞬间
	private long size;//大小（文档中心使用）
	private String description;//描述
	private int downCount;//	下载次数（文档中心使用）
	private String downUrl;	//	下载地址URL
	private String createTime;//	上传时间
	private String address	;//	所在位置（预留，后期精彩瞬间可能使用）
	private UserSimpleInfo fromUser	;//	文档分享的用户。userSimpleInfo参照公共对象说明
	private List<UserSimpleInfo>toUsers	;//	文档分享的关联用户。userSimpleInfo参照公共对象说明
	private List<GroupInfo>toGroups	;//	文档分享的关联组，GroupInfo参照公共对象说明
	private long custId	;//	所属企业
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getDownCount() {
		return downCount;
	}
	public void setDownCount(int downCount) {
		this.downCount = downCount;
	}
	public String getDownUrl() {
		return downUrl;
	}
	public void setDownUrl(String downUrl) {
		this.downUrl = downUrl;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public UserSimpleInfo getFromUser() {
		return fromUser;
	}
	public void setFromUser(UserSimpleInfo fromUser) {
		this.fromUser = fromUser;
	}
	public List<UserSimpleInfo> getToUsers() {
		return toUsers;
	}
	public void setToUsers(List<UserSimpleInfo> toUsers) {
		this.toUsers = toUsers;
	}
	public List<GroupInfo> getToGroups() {
		return toGroups;
	}
	public void setToGroups(List<GroupInfo> toGroups) {
		this.toGroups = toGroups;
	}
	public long getCustId() {
		return custId;
	}
	public void setCustId(long custId) {
		this.custId = custId;
	}

	
}
