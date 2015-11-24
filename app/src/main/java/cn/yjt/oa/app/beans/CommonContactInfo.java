package cn.yjt.oa.app.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class CommonContactInfo implements Parcelable{
	private long id;
	private String name;
	private String description;
	private String phone1;
	private String phone2;
	private String phone3;
	private String url;
	private String createTime;
	public CommonContactInfo() {
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public String getPhone3() {
		return phone3;
	}

	public void setPhone3(String phone3) {
		this.phone3 = phone3;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CommonContactInfo other = (CommonContactInfo) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeString(phone1);
		dest.writeString(phone2);
		dest.writeString(phone3);
		dest.writeString(url);
		dest.writeString(createTime);
	}
	
	public CommonContactInfo(Parcel in) {
		id = in.readLong();
		name  = in.readString();
		description = in.readString();
		phone1 = in.readString();
		phone2 = in.readString();
		phone3 = in.readString();
		url = in.readString();
		createTime = in.readString();
	}
	
	public static final Creator<CommonContactInfo> CREATOR = new Creator<CommonContactInfo>() {
		
		@Override
		public CommonContactInfo[] newArray(int size) {
			return new CommonContactInfo[size];
		}
		
		@Override
		public CommonContactInfo createFromParcel(Parcel source) {
			return new CommonContactInfo(source);
		}
	};

}
