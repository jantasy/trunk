package cn.yjt.oa.app.beans;

import java.util.List;

public class UserLoginInfoList {

	private List<UserLoginInfo> userLoginInfos;
	private String groupName;
	
	public List<UserLoginInfo> getUserLoginInfos() {
		return userLoginInfos;
	}
	public void setUserLoginInfos(List<UserLoginInfo> userLoginInfos) {
		this.userLoginInfos = userLoginInfos;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
}
