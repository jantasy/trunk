package cn.yjt.oa.app.beans;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * 自定义的beacon实体类 
 *
 */
public class BeaconInfo implements Parcelable {

	private long id;
	private String name;
	private String uumm;

	private long custId;

	private long areaId;
	private String areaName;

	private long creatorId;
	private String creatorName;

	private String createTime;

	public BeaconInfo() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUumm() {
		return uumm;
	}

	public void setUumm(String uumm) {
		this.uumm = uumm;
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
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

	public long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(long creatorId) {
		this.creatorId = creatorId;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeString(uumm);
		dest.writeLong(custId);
		dest.writeLong(areaId);
		dest.writeString(areaName);
		dest.writeLong(creatorId);
		dest.writeString(creatorName);
		dest.writeString(createTime);
	}

	private BeaconInfo(Parcel in) {
		id = in.readLong();
		name = in.readString();
		uumm = in.readString();
		custId = in.readLong();
		areaId = in.readLong();
		areaName = in.readString();
		creatorId = in.readLong();
		creatorName = in.readString();
		createTime = in.readString();
	}

	
	
	@Override
	public String toString() {
		return "BeaconInfo [uumm=" + uumm + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof BeaconInfo) {
			return TextUtils.equals(((BeaconInfo) o).uumm, uumm);
		}
		return super.equals(o);
	}

	public static final Creator<BeaconInfo> CREATOR = new Creator<BeaconInfo>() {

		@Override
		public BeaconInfo[] newArray(int size) {
			return new BeaconInfo[size];
		}

		@Override
		public BeaconInfo createFromParcel(Parcel source) {
			return new BeaconInfo(source);
		}
	};

}
