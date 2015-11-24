package cn.yjt.oa.app.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class NFCTagInfo implements Parcelable {
	private long custId;
	private long id;
	private long areaId;
	private long creatorId;
	private String sn;
	private String name;
	private String positionData;
	private int signRange;
	private String createTime;
	private String areaName;
	private String creatorName;

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getTagName() {
		return name;
	}

	public void setTagName(String tagName) {
		this.name = tagName;
	}

	public long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(long creatorId) {
		this.creatorId = creatorId;
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

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getAreaId() {
		return areaId;
	}

	public void setAreaId(long areaId) {
		this.areaId = areaId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public static Creator<NFCTagInfo> CREATOR = new Creator<NFCTagInfo>() {

		@Override
		public NFCTagInfo createFromParcel(Parcel source) {
			NFCTagInfo info = new NFCTagInfo();
			info.sn = source.readString();
			info.name = source.readString();
			info.creatorId = source.readLong();
			info.positionData = source.readString();
			info.signRange = source.readInt();
			info.createTime = source.readString();
			info.custId = source.readLong();
			info.id = source.readLong();
			info.areaId = source.readLong();
			info.areaName = source.readString();
			info.creatorName = source.readString();
			return info;
		}

		@Override
		public NFCTagInfo[] newArray(int size) {
			return new NFCTagInfo[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(sn);
		dest.writeString(name);
		dest.writeLong(creatorId);
		dest.writeString(positionData);
		dest.writeInt(signRange);
		dest.writeString(createTime);
		dest.writeLong(custId);
		dest.writeLong(id);
		dest.writeLong(areaId);
		dest.writeString(areaName);
		dest.writeSerializable(creatorName);
	}

}
