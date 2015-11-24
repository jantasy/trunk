package cn.yjt.oa.app.beans;

import java.util.Arrays;

/**
 * 考勤属性信息 
 *
 */
public class DutyInfo {
	private String dutyDate;
	private String dutyName;
	private String[] onOffDutyTime;
	
	
	public String getDutyDate() {
		return dutyDate;
	}


	public void setDutyDate(String dutyDate) {
		this.dutyDate = dutyDate;
	}


	public String getDutyName() {
		return dutyName;
	}


	public void setDutyName(String dutyName) {
		this.dutyName = dutyName;
	}


	public String[] getOnOffDutyTime() {
		return onOffDutyTime;
	}


	public void setOnOffDutyTime(String[] onOffDutyTime) {
		this.onOffDutyTime = onOffDutyTime;
	}


	@Override
	public String toString() {
		return "DutyInfo [dutyDate=" + dutyDate + ", dutyName=" + dutyName
				+ ", onOffDutyTime=" + Arrays.toString(onOffDutyTime) + "]";
	}
	
	
}
