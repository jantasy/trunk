package cn.yjt.oa.app.meeting.bean;

import java.text.ParseException;

import cn.yjt.oa.app.beans.MeetingInfo;
import cn.yjt.oa.app.meeting.utils.DateUtils;
import android.text.TextUtils;

/**
 * 会议的表单校验类
 * @author 熊岳岳
 * @since 20150826
 */
public class MeetingInfoForm {

	String name;
	String theme;
	String date;
	String starttime;
	String stoptime;
	String address;
	String qrcode;

	public MeetingInfoForm(){
		
	}
	
	public MeetingInfoForm(MeetingInfo info){
		this.name = info.getName();
		this.theme = info.getContent();
		this.qrcode = info.getTwoDimensionCode();
		this.address = info.getAddress();
		try {
			if(!TextUtils.isEmpty(info.getStartTime())){
				this.date = DateUtils.sDateFormat.format(DateUtils.sRequestFormat.parse(info.getStartTime()));
				this.starttime = DateUtils.sTimeFormat.format(DateUtils.sRequestFormat.parse(info.getStartTime()));
			}
			if(!TextUtils.isEmpty(info.getEndTime())){
				this.stoptime = DateUtils.sTimeFormat.format(DateUtils.sRequestFormat.parse(info.getEndTime()));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof MeetingInfoForm) {
			if (TextUtils.equals(this.name, ((MeetingInfoForm) o).name)
					&& TextUtils.equals(this.theme, ((MeetingInfoForm) o).theme)
					&& TextUtils.equals(this.date, ((MeetingInfoForm) o).date)
					&& TextUtils.equals(this.starttime, ((MeetingInfoForm) o).starttime)
					&& TextUtils.equals(this.stoptime, ((MeetingInfoForm) o).stoptime)
					&& TextUtils.equals(this.address, ((MeetingInfoForm) o).address)) {
				return true;
			}
		}
		return false;
	}

	/*-----set、get方法START-----*/
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getStoptime() {
		return stoptime;
	}

	public void setStoptime(String stoptime) {
		this.stoptime = stoptime;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getQrcode() {
		return qrcode;
	}

	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}
	
	/*-----set、get方法END-----*/
}
