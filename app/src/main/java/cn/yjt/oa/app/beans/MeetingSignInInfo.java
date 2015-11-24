package cn.yjt.oa.app.beans;

/**
 * 会议签到信息
 * @author 熊岳岳
 * @since 20150825
 */
public class MeetingSignInInfo {

	private long id;
	/**签到者Id*/
	private long userId;
	/**会议Id*/
	private long meetingId;
	/**签到时间*/
	private String signInTime;
	/**会议唯一token，客户端扫码签到时携带*/
	private String token;
	/**签到者手机号*/
	private String phone;
	/**签到者姓名*/
	private String name;
	/**签到者头像URL*/
	private String avatar;
	/**签到者所在单位名称*/
	private String custName;


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

	public long getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(long meetingId) {
		this.meetingId = meetingId;
	}

	public String getSignInTime() {
		return signInTime;
	}

	public void setSignInTime(String signInTime) {
		this.signInTime = signInTime;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}
	
}
