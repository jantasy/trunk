package cn.yjt.oa.app.beans;

import java.util.List;

public class UserListInfo {
	private List<UserManagerInfo> adds; // 添加用户的列表
	private List<UserManagerInfo> updates; // 修改用户的列表
	private List<UserManagerInfo> deletes; // 删除用户的列表 

	public List<UserManagerInfo> getAdds() {
		return adds;
	}

	public void setAdds(List<UserManagerInfo> adds) {
		this.adds = adds;
	}

	public List<UserManagerInfo> getUpdates() {
		return updates;
	}

	public void setUpdates(List<UserManagerInfo> updates) {
		this.updates = updates;
	}

	public List<UserManagerInfo> getDeletes() {
		return deletes;
	}

	public void setDeletes(List<UserManagerInfo> deletes) {
		this.deletes = deletes;
	}

}
