package cn.yjt.oa.app.beans;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AttendanceUserTime {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyyMMdd");

	private String date;
	private List<AttendanceTime> times;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<AttendanceTime> getTimes() {
		return times;
	}

	public void setTimes(List<AttendanceTime> times) {
		this.times = times;
	}

	public Date getParseDate() {
		try {
			return DATE_FORMAT.parse(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {
		return "AttendanceUserTime [date=" + date + ", times=" + times + "]";
	}
	
	

}
