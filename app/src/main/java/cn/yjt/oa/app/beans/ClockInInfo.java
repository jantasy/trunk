package cn.yjt.oa.app.beans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import cn.yjt.oa.app.widget.TimeLineAdapter.DateItem;

/**
 * 个人考勤详细信息
 *
 */
public class ClockInInfo implements DateItem {
	
	private String dutyDate;// YYYYMMDD
	
	private String checkInTime;// HHMMSS
	
	private String checkOutTime;// HHMMSS
	
	private String status;// 考勤状态
	
	private int statusCode;

    private boolean today;

    public boolean isToday() {
        return today;
    }

    public void setToday(boolean today) {
        this.today = today;
    }

    public String getDutyDate() {
		return dutyDate;
	}

	public void setDutyDate(String dutyDate) {
		this.dutyDate = dutyDate;
	}

	public String getCheckInTime() {
		return checkInTime;
	}

	public void setCheckInTime(String checkInTime) {
		this.checkInTime = checkInTime;
	}

	public String getCheckOutTime() {
		return checkOutTime;
	}

	public void setCheckOutTime(String checkOutTime) {
		this.checkOutTime = checkOutTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public Date getDate() {
		// TODO Auto-generated method stub
		try {
			return new SimpleDateFormat("yyyyMMdd").parse(dutyDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Date(System.currentTimeMillis());
	}
	
	
}
