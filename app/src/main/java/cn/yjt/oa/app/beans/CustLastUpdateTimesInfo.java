package cn.yjt.oa.app.beans;


/**联系人更新时间实体类*/
public class CustLastUpdateTimesInfo{
	
	/**企业的id*/
	private long custId;
	/**通讯录最后一次更新时间*/
	private String userLastUpdateTime; 
	/**群组最后一次更新时间*/
	private String groupLastUpdateTime; 
	/**组织架构最后一次更新时间*/
	private String deptLastUpdateTime; 
	/**公共服务联系人最后一次更新时间*/
	private String commonContactLastUpdateTime; 
	
	/*-----get、set方法START-----*/
	public long getCustId() {
		return custId;
	}
	public void setCustId(long custId) {
		this.custId = custId;
	}
	public String getUserLastUpdateTime() {
		return userLastUpdateTime;
	}
	public void setUserLastUpdateTime(String userLastUpdateTime) {
		this.userLastUpdateTime = userLastUpdateTime;
	}
	public String getGroupLastUpdateTime() {
		return groupLastUpdateTime;
	}
	public void setGroupLastUpdateTime(String groupLastUpdateTime) {
		this.groupLastUpdateTime = groupLastUpdateTime;
	}
	public String getDeptLastUpdateTime() {
		return deptLastUpdateTime;
	}
	public void setDeptLastUpdateTime(String deptLastUpdateTime) {
		this.deptLastUpdateTime = deptLastUpdateTime;
	}
	public String getCommonContactLastUpdateTime() {
		return commonContactLastUpdateTime;
	}
	public void setCommonContactLastUpdateTime(String commonContactLastUpdateTime) {
		this.commonContactLastUpdateTime = commonContactLastUpdateTime;
	}
	/*-----get、set方法END-----*/
}
