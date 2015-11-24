package cn.yjt.oa.app.beans;

import java.io.Serializable;

import org.apache.http.entity.SerializableEntity;

/**
 * 会议信息
 * 
 * @author 熊岳岳
 * @since 20150825
 */
public class MeetingInfo implements Serializable{

	/**我创建的会议*/
	public final static int TYPE_PUBLIC = 0;
	/**我参与的会议*/
	public final static int TYPE_JOIN = 1;
	
	private long id;
	/**创建者id*/
	private long createUserId;
	/**会议名称*/
	private String name;
	/**会议简介*/
	private String content;
	/**会议开始时间*/
	private String startTime;
	/**会议结束时间*/
	private String endTime;
	/**会议地点*/
	private String address;
	/**会议二维码下载地址URL*/
	private String twoDimensionCode;
	/**会议是否被关闭 0:未关闭；-1:已关闭*/
	private int isClosed;
	/**是否被删除*/
	private int isDeleted;
	/**0：自己创建的会议，1：参加的会议*/
	private int type;
	/**签到人数*/
	private int signInNums;
	/**签到时间，查找我参加的会议时会返回该字段*/
	private String signInTime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(long createUserId) {
		this.createUserId = createUserId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTwoDimensionCode() {
		return twoDimensionCode;
	}

	public void setTwoDimensionCode(String twoDimensionCode) {
		this.twoDimensionCode = twoDimensionCode;
	}

	public int getIsClosed() {
		return isClosed;
	}

	public void setIsClosed(int isClosed) {
		this.isClosed = isClosed;
	}

	public int getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(int isDeleted) {
		this.isDeleted = isDeleted;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSignInNums() {
		return signInNums;
	}

	public void setSignInNums(int signInNums) {
		this.signInNums = signInNums;
	}

	public String getSignInTime() {
		return signInTime;
	}

	public void setSignInTime(String signInTime) {
		this.signInTime = signInTime;
	}

}
