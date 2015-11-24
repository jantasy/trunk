package cn.yjt.oa.app.beans;

import android.os.Parcel;
import android.os.Parcelable;


public class CustSignCommonInfo implements Parcelable{
	private long id;
	private long custId;
	private String name;
	private String positionDescription; //位置POI信息
	private String positionData; //坐标
	private int signRange; //范围（单位米）
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getCustId() {
		return custId;
	}
	public void setCustId(long custId) {
		this.custId = custId;
	}
	public String getPositionDescription() {
		return positionDescription;
	}
	public void setPositionDescription(String positionDescription) {
		this.positionDescription = positionDescription;
	}
	public String getPositionData() {
		return positionData;
	}
	public void setPositionData(String positionData) {
		this.positionData = positionData;
	}
	public int getSignRange() {
		return signRange;
	}
	public void setSignRange(int signRange) {
		this.signRange = signRange;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		/*
		 * private long id;
	private long custId;
	private String positionDescription; //位置POI信息
	private String positionData; //坐标
	private int signRange; //范围（单位米）
	
	private String workTime; //最晚上班签到时间，格式：HH:ss
	private String offTime; //最早下班签到时间，格式：HH:ss
	
	private double duration; //工作时长（包含午休时间）：单位：小时
		 */
		dest.writeLong(id);
		dest.writeLong(custId);
		dest.writeString(name);
		dest.writeString(positionDescription);
		dest.writeString(positionData);
		dest.writeInt(signRange);
		
	}
	
	public CustSignCommonInfo() {
	}
	public CustSignCommonInfo(Parcel in) {
		id = in.readLong();
		custId = in.readLong();
		name = in.readString();
		positionDescription = in.readString();
		positionData = in.readString();
		signRange =in.readInt();
	}
	
	public static final Creator<CustSignCommonInfo> CREATOR = new Creator<CustSignCommonInfo>() {
		
		@Override
		public CustSignCommonInfo[] newArray(int size) {
			return new CustSignCommonInfo[size];
		}
		
		@Override
		public CustSignCommonInfo createFromParcel(Parcel source) {
			return new CustSignCommonInfo(source);
		}
	};
}
