package cn.yjt.oa.app.beans;

import java.util.Date;
import java.util.List;

import cn.yjt.oa.app.widget.TimeLineAdapter.DateItem;

public class AttendanceInfo implements DateItem{
	private ClockInInfo clockInInfo;
	private List<CardCheckinInfo> cardCheckList;
	public ClockInInfo getClockInInfo() {
		return clockInInfo;
	}
	public void setClockInInfo(ClockInInfo clockInInfo) {
		this.clockInInfo = clockInInfo;
	}
	public List<CardCheckinInfo> getCardCheckList() {
		return cardCheckList;
	}
	public void setCardCheckList(List<CardCheckinInfo> cardCheckList) {
		this.cardCheckList = cardCheckList;
	}
	@Override
	public Date getDate() {
		return clockInInfo.getDate();
	}
	
	

}
