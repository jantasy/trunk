package cn.yjt.oa.app.beacon;

import java.util.Calendar;
import java.util.Date;

import android.text.TextUtils;

/** Beacon实体类*/
public class Beacon {
	
	private String uumm;
	private Date date;
	private String distance;
	
	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public Beacon(String uumm) {
		this.uumm = uumm;
		date = Calendar.getInstance().getTime();
	}

	public String getUumm() {
		return uumm;
	}

	public void setUumm(String uumm) {
		this.uumm = uumm;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Beacon){
			Beacon beacon = (Beacon) o;
			return TextUtils.equals(uumm, beacon.uumm);
		}
		return super.equals(o);
	}
}
