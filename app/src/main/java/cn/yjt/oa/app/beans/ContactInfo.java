package cn.yjt.oa.app.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactInfo extends UserInfo implements Parcelable {

	private boolean isRegister;
	private Long userId;

	public ContactInfo(){}
	public boolean isRegister() {
		return isRegister;
	}

	public void setRegister(boolean isRegister) {
		this.isRegister = isRegister;
	}

	public Long getUserId() {
		
		return userId != null ? userId.longValue() : -1;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContactInfo other = (ContactInfo) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	

	@Override
	public String toString() {
		return super.toString() + "ContactInfo [isRegister=" + isRegister + ", userId=" + userId
				+ "]";
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return super.describeContents();
	}
    
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeLong(getUserId());
		dest.writeInt(isRegister ? 0:1);
	}
	
	private ContactInfo(Parcel source){
		super(source);
		userId = source.readLong();
		isRegister = (source.readInt() == 0);
	}
	
	public static Creator<ContactInfo> CREATOR = new Creator<ContactInfo>() {

		@Override
		public ContactInfo createFromParcel(Parcel source) {
			return new ContactInfo(source);
		}

		@Override
		public ContactInfo[] newArray(int size) {
			return new ContactInfo[size];
		}
	};
}
