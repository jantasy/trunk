package cn.yjt.oa.app.contactlist.data;

import cn.yjt.oa.app.beans.CommonContactInfo;
import cn.yjt.oa.app.beans.ContactInfo;
import cn.yjt.oa.app.contactlist.utils.ContactlistUtils;

public class ContactlistContactInfo {

	private int viewType;
	private ContactInfo contactInfo;
	private String namePinYin;
	private String index;
	private boolean isChecked;
	private CommonContactInfo commonContactInfo;
	
	public static final int VIEW_TYPE_INDEX = 0;
	public static final int VIEW_TYPE_CONTACT = 1;
	public static final int VIEW_TYPE_PUBLIC_SERVICE = 2;

	public static final long INFO_ID_DEFAULT = -1;

	private ContactlistContactInfo() {
	}

	public ContactlistContactInfo(int viewType) {
		this.viewType = viewType;
	}

	public ContactlistContactInfo(ContactInfo info, int viewType) {
		this(viewType);
		this.contactInfo = info;
		this.namePinYin = ContactlistUtils.getPinYin(info.getName());
	}

	public ContactlistContactInfo(CommonContactInfo info, int viewType) {
		this(viewType);
		this.commonContactInfo = info;
		this.namePinYin = ContactlistUtils.getPinYin(info.getName());
	}

	public int getViewType() {
		return viewType;
	}

	public void setViewType(int viewType) {
		this.viewType = viewType;
	}

	public ContactInfo getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(ContactInfo contactInfo) {
		this.contactInfo = contactInfo;
	}

	public CommonContactInfo getCommonContactInfo() {
		return commonContactInfo;
	}

	public void setCommonContactInfo(CommonContactInfo commonContactInfo) {
		this.commonContactInfo = commonContactInfo;
	}

	public String getNamePinYin() {
		return namePinYin;
	}

	public void setNamePinYin(String namePinYin) {
		this.namePinYin = namePinYin;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
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
		result = prime
				* result
				+ ((contactInfo == null) ? 0 : contactInfo.hashCode())
				+ ((commonContactInfo == null) ? 0 : commonContactInfo
						.hashCode());
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
		ContactlistContactInfo other = (ContactlistContactInfo) obj;
		if (contactInfo == null) {
			if (other.contactInfo != null)
				return false;
		} else if (!contactInfo.equals(other.contactInfo)) {

			return false;
		}
		if (commonContactInfo == null) {
			if (other.commonContactInfo != null)
				return false;
		} else if (!commonContactInfo.equals(other.commonContactInfo)) {

			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "ContactlistContactInfo [viewType=" + viewType
				+ ", contactInfo=" + contactInfo + ", namePinYin=" + namePinYin
				+ ", index=" + index + ", isChecked=" + isChecked
				+ ", commonContactInfo=" + commonContactInfo + "]";
	}
	

}
