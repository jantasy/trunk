package cn.yjt.oa.app.beans;

public class AttendanceSummaryInfo {

	private long userId;
	private String name;
	private String phone;
	private String date;
	
	private int status; //用户考勤状态，若只查一天考勤汇总，则会设置该值，否则为0
	private String statusDesc; //考勤状态描述，同上
	
	private int zhengchangNum; //正常打卡天数
	private int queqinNum; //缺勤（无打卡记录）天数
	private int weijisuanNum; //未计算天数
	private int noScheduleNum; //计划外出勤天数
	private int abnormalNum; //打卡异常天数
	private int noDutyNum; //当日无排班天数
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	public int getZhengchangNum() {
		return zhengchangNum;
	}
	public void setZhengchangNum(int zhengchangNum) {
		this.zhengchangNum = zhengchangNum;
	}
	public int getQueqinNum() {
		return queqinNum;
	}
	public void setQueqinNum(int queqinNum) {
		this.queqinNum = queqinNum;
	}
	public int getWeijisuanNum() {
		return weijisuanNum;
	}
	public void setWeijisuanNum(int weijisuanNum) {
		this.weijisuanNum = weijisuanNum;
	}
	public int getNoScheduleNum() {
		return noScheduleNum;
	}
	public void setNoScheduleNum(int noScheduleNum) {
		this.noScheduleNum = noScheduleNum;
	}
	public int getAbnormalNum() {
		return abnormalNum;
	}
	public void setAbnormalNum(int abnormalNum) {
		this.abnormalNum = abnormalNum;
	}
	public int getNoDutyNum() {
		return noDutyNum;
	}
	public void setNoDutyNum(int noDutyNum) {
		this.noDutyNum = noDutyNum;
	}
	@Override
	public String toString() {
		return "AttendanceSummaryInfo [userId=" + userId + ", name=" + name + ", phone=" + phone + ", date=" + date
				+ ", status=" + status + ", statusDesc=" + statusDesc + ", zhengchangNum=" + zhengchangNum
				+ ", queqinNum=" + queqinNum + ", weijisuanNum=" + weijisuanNum + ", noScheduleNum=" + noScheduleNum
				+ ", abnormalNum=" + abnormalNum + ", noDutyNum=" + noDutyNum + "]";
	}
}
