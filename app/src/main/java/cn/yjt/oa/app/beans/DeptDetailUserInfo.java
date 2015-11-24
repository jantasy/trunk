package cn.yjt.oa.app.beans;

import io.luobo.widget.XNode;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class DeptDetailUserInfo implements XNode,Parcelable{
	
	private long userId; // 用户ID
	private String name;//显示用
    private String avatar;
    private String position; // 用户职务头衔
    private long parentId;
    private String phone;
    private String email;
    private String tel;
    
	private int sex;
    //---
    private int orderIndex;
    private int status;//用户当前的状态：0：未注册，1：已发出邀请，等待用户响应，2：用户已拒绝，3：正式员工
    private String statusDesc;//状态描述：“未注册”、”待回应“、”已拒绝“
    //------
    public DeptDetailUserInfo() {
		// TODO Auto-generated constructor stub
	}
    
    public DeptDetailUserInfo(Parcel in) {
    	userId = in.readLong();
    	position = in.readString();
    	name = in.readString();
    	avatar = in.readString();
    	parentId = in.readLong();
    	phone = in.readString();
    	email = in.readString();
    	tel = in.readString();
    	sex = in.readInt();
    	orderIndex = in.readInt();
    	status = in.readInt();
    	statusDesc = in.readString();
  	}
    

    public int getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(int orderIndex) {
		this.orderIndex = orderIndex;
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
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	@Override
	public List<XNode> getXChildren() {
		return null;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getXTitle() {
		return name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getName() {
		return name;
	}

	
	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	@Override
	public String toString() {
		return "DeptDetailUserInfo [userId=" + userId + ", name=" + name
				+ ", avatar=" + avatar + ", position=" + position
				+ ", parentId=" + parentId + ", phone=" + phone + ", email="
				+ email + ", tel=" + tel + ", sex=" + sex + ", orderIndex="
				+ orderIndex + ", status=" + status + ", statusDesc="
				+ statusDesc + "]";
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
    	
    	dest.writeLong(userId);
    	dest.writeString(position);
    	dest.writeString(name);
    	dest.writeString(avatar);
    	dest.writeLong(parentId);
    	dest.writeString(phone);
    	dest.writeString(email);
    	dest.writeString(tel);
    	dest.writeInt(sex);
    	dest.writeInt(orderIndex);
    	dest.writeInt(status);
    	dest.writeString(statusDesc);
	}
	
	
	public static final Creator<DeptDetailUserInfo> CREATOR = new Creator<DeptDetailUserInfo>() {
		
		@Override
		public DeptDetailUserInfo[] newArray(int size) {
			return new DeptDetailUserInfo[size];
		}
		
		@Override
		public DeptDetailUserInfo createFromParcel(Parcel source) {
			return new DeptDetailUserInfo(source);
		}
	};
	
	
	

}
