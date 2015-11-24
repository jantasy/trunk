package cn.yjt.oa.app.teleconference.fragment;

import cn.yjt.oa.app.beans.UserInfo;

public interface IUserModel {
	public void setId();
	public void setUserName(String username);
	public void setPhoneNumber(String phoneNumber);
	public int getId();
	public UserInfo load(int id);
}
