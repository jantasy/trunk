package cn.yjt.oa.app.teleconference.fragment;

import java.util.List;

import cn.yjt.oa.app.beans.TCItem;

public interface IUserView {
	public void setUser(TCItem item);
	public void setUsers(List<TCItem> items);
	public void setParams(String mobile, String ecp_token, String access_token);
}
