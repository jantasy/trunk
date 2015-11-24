package cn.yjt.oa.app.beans;

public class TeleconferenceInfo {
	private long phoneId;
	private String icon;
	private String name;
	private String phone;
	
	public long getPhotoId() {
		return phoneId;
	}
	public void setPhotoId(long phoneId) {
		this.phoneId = phoneId;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	@Override
	public String toString() {
		return "Item [icon = " + icon + "name=" + name + ", phone=" + phone + "]";
	}
	
}
