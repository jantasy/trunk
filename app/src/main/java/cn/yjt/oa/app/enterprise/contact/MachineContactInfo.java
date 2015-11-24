package cn.yjt.oa.app.enterprise.contact;

import android.os.Parcel;
import android.os.Parcelable;
import cn.yjt.oa.app.contactlist.utils.ContactlistUtils;


public class MachineContactInfo implements Parcelable{
	private long contactId;
	private String displayName;
	private String number;
	private long photoid; 
	
	private int viewType;
	private String namePinYin;
	private String index;
	private int isChecked;
	
	public static final int VIEW_TYPE_INDEX = 0;
	public static final int VIEW_TYPE_CONTACT = 1;
	
	public static final long INFO_ID_DEFAULT = -1;
	public long getContactId() {
		return contactId;
	}
	public void setContactId(long contactId) {
		this.contactId = contactId;
	}
	
	
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public long getPhotoid() {
		return photoid;
	}
	public void setPhotoid(long photoid) {
		this.photoid = photoid;
	}
	public int getViewType() {
		return viewType;
	}
	public void setViewType(int viewType) {
		this.viewType = viewType;
	}
	public String getNamePinYin() {
		return namePinYin;
	}
	public void setNamePinYin(String displayName) {
		this.namePinYin = ContactlistUtils.getPinYin(displayName);;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	
	
	
	public int getIsChecked() {
		return isChecked;
	}
	public void setIsChecked(int isChecked) {
		this.isChecked = isChecked;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((number == null) ? 0 : number.hashCode());
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
		MachineContactInfo other = (MachineContactInfo) obj;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		return true;
	}



	public static final Creator<MachineContactInfo> CREATOR=new Creator<MachineContactInfo>() {
		
		@Override
		public MachineContactInfo[] newArray(int size) {
			return new MachineContactInfo[size];
		}
		
		@Override
		public MachineContactInfo createFromParcel(Parcel source) {
			MachineContactInfo info=new MachineContactInfo();
			info.setContactId(source.readLong());
			info.setDisplayName(source.readString());
			info.setNumber(source.readString());
			info.setPhotoid(source.readLong());
			info.setViewType(source.readInt());
			info.setNamePinYin(source.readString());
			info.setIndex(source.readString());
			info.setIsChecked(source.readInt());
			return info;
		}
	};
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(contactId);
		dest.writeString(displayName);
		dest.writeString(number);
		dest.writeLong(photoid);
		dest.writeInt(viewType);
		dest.writeString(namePinYin);
		dest.writeString(index);
		dest.writeInt(isChecked);
	}
	
	
	
	

}
