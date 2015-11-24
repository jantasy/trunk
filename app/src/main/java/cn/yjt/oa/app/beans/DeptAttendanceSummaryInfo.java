package cn.yjt.oa.app.beans;

import java.util.List;


public class DeptAttendanceSummaryInfo {

	private long id;
	private String name;
	private List<DeptAttendanceSummaryInfo> children;
	private List<AttendanceSummaryInfo> members;
	
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
	public List<DeptAttendanceSummaryInfo> getChildren() {
		return children;
	}
	public void setChildren(List<DeptAttendanceSummaryInfo> children) {
		this.children = children;
	}
	public List<AttendanceSummaryInfo> getMembers() {
		return members;
	}
	public void setMembers(List<AttendanceSummaryInfo> members) {
		this.members = members;
	}
	
	@Override
	public String toString() {
		return "DeptAttendanceSummaryInfo [id=" + id + ", name=" + name + ", children=" + children + ", members="
				+ members + "]";
	}
}
