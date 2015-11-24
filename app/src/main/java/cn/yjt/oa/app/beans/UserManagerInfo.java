package cn.yjt.oa.app.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class UserManagerInfo implements Parcelable{
	private long id;// 用户id
	private String name; // 用户姓名
	private String phone; // 电话号码（不可修改）
	private int actived; // 0：未激活  1：已激活
	private int result;// 处理结果（增删改），目前暂定，0：未处理或处理失败，1：处理成功
	private int isCustAdmin;//是否为 企业管理员；0：否，1：是
	private int status;//用户当前状态；0：未注册，1：已发出邀请，等待用户响应，2：用户已拒绝，4：正式员工
	private String statusDesc;//状态描述：“未注册”、“待回应”、“已拒绝”、“”
	private String position; //职位
	private String department;//部门

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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getActived() {
		return actived;
	}

	public void setActived(int actived) {
		this.actived = actived;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
	
	

	public int getIsCustAdmin() {
		return isCustAdmin;
	}

	public void setIsCustAdmin(int isCustAdmin) {
		this.isCustAdmin = isCustAdmin;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	
	

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
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
		UserManagerInfo other = (UserManagerInfo) obj;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		return true;
	}
	
public static final Creator<UserManagerInfo> CREATOR=new Creator<UserManagerInfo>() {
		
		@Override
		public UserManagerInfo[] newArray(int size) {
			return new UserManagerInfo[size];
		}
		
		@Override
		public UserManagerInfo createFromParcel(Parcel source) {
			UserManagerInfo info=new UserManagerInfo();
			info.setId(source.readLong());
			info.setName(source.readString());
			info.setPhone(source.readString());
			info.setActived(source.readInt());
			info.setResult(source.readInt());
			info.setIsCustAdmin(source.readInt());
			info.setStatus(source.readInt());
			info.setStatusDesc(source.readString());
			return info;
		}
	}; 

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeString(phone);
		dest.writeInt(actived);
		dest.writeInt(result);
		dest.writeInt(isCustAdmin);
		dest.writeInt(status);
		dest.writeString(statusDesc);
	}
	

}
