package cn.yjt.oa.app.beans;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AttendanceTime implements Serializable{

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm");

	private long id;
	
	private long custId;
	private String inStartTime;
	private String inEndTime;
	private String outStartTime;
	private String outEndTime;
	
	public long getCustId() {
		return custId;
	}
	public void setCustId(long custId) {
		this.custId = custId;
	}
	public String getInStartTime() {
		return inStartTime;
	}
	public void setInStartTime(String inStartTime) {
		this.inStartTime = inStartTime;
	}
	public String getInEndTime() {
		return inEndTime;
	}
	public void setInEndTime(String inEndTime) {
		this.inEndTime = inEndTime;
	}
	public String getOutStartTime() {
		return outStartTime;
	}
	public void setOutStartTime(String outStartTime) {
		this.outStartTime = outStartTime;
	}
	public String getOutEndTime() {
		return outEndTime;
	}
	public void setOutEndTime(String outEndTime) {
		this.outEndTime = outEndTime;
	}
	
	public static Date parseTime(String time){
		try {
			return DATE_FORMAT.parse(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public String toString() {
		return "AttendanceTime [id=" + id + ", custId=" + custId
				+ ", inStartTime=" + inStartTime + ", inEndTime=" + inEndTime
				+ ", outStartTime=" + outStartTime + ", outEndTime="
				+ outEndTime + "]";
	}
	
	
}
