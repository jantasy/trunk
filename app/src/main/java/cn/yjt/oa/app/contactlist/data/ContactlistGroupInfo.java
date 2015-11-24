package cn.yjt.oa.app.contactlist.data;

import java.util.Arrays;

import android.os.Parcel;
import android.os.Parcelable;
import cn.yjt.oa.app.beans.ContactInfo;
import cn.yjt.oa.app.beans.GroupInfo;

public class ContactlistGroupInfo implements Parcelable{
    private ContactInfo[] users;
    private GroupInfo info;
    private boolean isChecked;
    
	public ContactlistGroupInfo() {

	}
	
	public ContactlistGroupInfo(GroupInfo info){
		this.info = info;
	}

	public ContactInfo[] getUsers() {
		return users;
	}

	public void setUsers(ContactInfo[] users) {
		this.users = users;
	}

	public GroupInfo getInfo() {
		return info;
	}

	public void setInfo(GroupInfo info) {
		this.info = info;
	}
	
	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((info == null) ? 0 : info.hashCode());
		result = prime * result + Arrays.hashCode(users);
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
		ContactlistGroupInfo other = (ContactlistGroupInfo) obj;
		if (info == null) {
			if (other.info != null)
				return false;
		} else if (!info.equals(other.info))
			return false;
		if (!Arrays.equals(users, other.users))
			return false;
		return true;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelableArray(users, flags);
		dest.writeParcelable(info, flags);
	}
    public static final Creator<ContactlistGroupInfo> CREATOR = new Creator<ContactlistGroupInfo>() {

		@Override
		public ContactlistGroupInfo createFromParcel(Parcel source) {
			ContactlistGroupInfo info = new ContactlistGroupInfo();
			ContactInfo[] infos = (ContactInfo[]) source.readArray(ContactInfo.class.getClassLoader());
			info.setUsers(infos);
			GroupInfo gInfo = source.readParcelable(GroupInfo.class.getClassLoader());
			info.setInfo(gInfo);
			return info;
		}

		@Override
		public ContactlistGroupInfo[] newArray(int size) {
			// TODO Auto-generated method stub
			return new ContactlistGroupInfo[size];
		}
	};
}
