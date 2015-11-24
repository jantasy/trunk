package cn.yjt.oa.app.beans;

public class UserLoginInfo {
	private long userId;
	private long custUniqueId;
	private String phone;
	private String password;
	private String custName;
	private String type; //企业类型：CUST:正常企业；CUST_REGISTER:注册企业；JOIN_CUST_APPLY:申请加入的企业
	private long contentId; //关联id，custId/custRegisterId/custJoinId
	private int custVCode;
	private String iccid;

	public String getIccid() {
		return iccid;
	}

	public void setIccid(String iccid) {
		this.iccid = iccid;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getCustUniqueId() {
		return custUniqueId;
	}

	public void setCustUniqueId(long custUniqueId) {
		this.custUniqueId = custUniqueId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getContentId() {
		return contentId;
	}

	public void setContentId(long contentId) {
		this.contentId = contentId;
	}

	public int getCustVCode() {
		return custVCode;
	}

	public void setCustVCode(int custVCode) {
		this.custVCode = custVCode;
	}

}
