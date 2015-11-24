package cn.yjt.oa.app.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 用户信息
 *
 */
public class UserInfo implements Parcelable, Cloneable {
	
	private long id = -1;
    private String name;
	private int sex = 2;
    private String avatar;
    private String userCode;
    private String custId;
    private String custName;
    private String custShortName;
    private String email;
    private String phone;
    private String tel;
    private String department;
    private String position;
    private String address;
    private String registerTime;
    private String splash480;
    private String splash720;
    private int isYjtUser;   //1：实体企业
    private int hasApplyCust; //0：没有申请过。。。1：已经申请过注册企业或加入企业；
    private String permission;
    private int custVCode;
	private String externalCustId;
	private int useCardMachine;//0:使用考勤机，-1:不使用考勤机



	public static final int SEX_MALE = 0;
    public static final int SEX_FEMALE = 1;
    public static final int SEX_UNKNOWN = 2;
    
    public UserInfo(){
    	
    }
	public String getExternalCustId() {
		return externalCustId;
	}

	public void setExternalCustId(String externalCustId) {
		this.externalCustId = externalCustId;
	}
    public UserInfo(long id){
    	this.id = id;
    }
    
	public int getIsYjtUser() {
		return isYjtUser;
	}

	public void setIsYjtUser(int isYjtUser) {
		this.isYjtUser = isYjtUser;
	}
	
	public int getHasApplyCust() {
		return hasApplyCust;
	}

	public void setHasApplyCust(int hasApplyCust) {
		this.hasApplyCust = hasApplyCust;
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
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}

	public int getUseCardMachine() {
		return useCardMachine;
	}

	public void setUseCardMachine(int useCardMachine) {
		this.useCardMachine = useCardMachine;
	}


	public String getCustShortName() {
		return custShortName;
	}

	public void setCustShortName(String custShortName) {
		this.custShortName = custShortName;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getRegisterTime() {
		return registerTime;
	}
	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}
	
	public String getSplash480() {
		return splash480;
	}

	public void setSplash480(String splash480) {
		this.splash480 = splash480;
	}

	public String getSplash720() {
		return splash720;
	}

	public void setSplash720(String splash720) {
		this.splash720 = splash720;
	}
	

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}
	

	public int getCustVCode() {
		return custVCode;
	}

	public void setCustVCode(int custVCode) {
		this.custVCode = custVCode;
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
		UserInfo other = (UserInfo) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getAddress());
		dest.writeString(getAvatar());
		dest.writeString(getCustId());
		dest.writeString(getCustName());
		dest.writeString(getCustShortName());
		dest.writeString(getDepartment());
		dest.writeString(getEmail());
		dest.writeString(getName());
		dest.writeString(getPhone());
		dest.writeString(getPosition());
		dest.writeString(getRegisterTime());
		dest.writeString(getTel());
		dest.writeString(getUserCode());
		dest.writeLong(getId());
		dest.writeInt(getSex());
		dest.writeString(getSplash480());
		dest.writeString(getSplash720());
		dest.writeInt(getIsYjtUser());
		dest.writeInt(getHasApplyCust());
		dest.writeString(getPermission());
		dest.writeInt(custVCode);
		dest.writeString(externalCustId);
		dest.writeInt(useCardMachine);
	}
	
	protected UserInfo(Parcel source){
		address = source.readString();
		avatar = source.readString();
		custId = source.readString();
		custName = source.readString();
		custShortName = source.readString();
		department = source.readString();
		email = source.readString();
		name = source.readString();
		phone = source.readString();
		position = source.readString();
		registerTime = source.readString();
		tel = source.readString();
		userCode = source.readString();
		id = source.readLong();
		sex = source.readInt();
		splash480 = source.readString();
		splash720 = source.readString();
		isYjtUser = source.readInt();
		hasApplyCust = source.readInt();
		permission = source.readString();
		custVCode = source.readInt();
		externalCustId = source.readString();
		useCardMachine = source.readInt();
	}
	
	public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
		
		@Override
		public UserInfo[] newArray(int size) {
			return new UserInfo[size];
		}
		
		@Override
		public UserInfo createFromParcel(Parcel source) {
			
			return new UserInfo(source);
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public UserInfo clone() {
		try {
			return (UserInfo) super.clone();
		} catch (CloneNotSupportedException e) {
			//ignore
			return this;
		}
	}
    
}
