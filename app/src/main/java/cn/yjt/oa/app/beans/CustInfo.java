package cn.yjt.oa.app.beans;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

public class CustInfo implements Parcelable {

	private long id;
	private long uniqueId;
	private String name;
	private String email;
	private String address;
	private String telephone; 
	private String shortName;
	/*
	 * private String provinceCode; private String extenalCustId; private long
	 * custRegisterId;
	 */
	private String createTime;
	// private long createUserId;
	private String createUserName;
	private int userCount;
	private boolean isNeedCheck = true;
	private int isOpen = 1;// 0:不公开 1:公开
	private int vCode;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	

	public long getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(long uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	/*
	 * public String getProvinceCode() { return provinceCode; } public void
	 * setProvinceCode(String provinceCode) { this.provinceCode = provinceCode;
	 * } public String getExtenalCustId() { return extenalCustId; } public void
	 * setExtenalCustId(String extenalCustId) { this.extenalCustId =
	 * extenalCustId; } public long getCustRegisterId() { return custRegisterId;
	 * } public void setCustRegisterId(long custRegisterId) {
	 * this.custRegisterId = custRegisterId; }
	 */
	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	/*
	 * public long getCreateUserId() { return createUserId; } public void
	 * setCreateUserId(long createUserId) { this.createUserId = createUserId; }
	 */
	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public int getUserCount() {
		return userCount;
	}

	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}

	public boolean getIsNeedCheck() {
		return isNeedCheck;
	}

	public void setIsNeedCheck(boolean isNeedCheck) {
		this.isNeedCheck = isNeedCheck;
	}
	
	

	public int getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(int isOpen) {
		this.isOpen = isOpen;
	}
	
	

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	
	public int getvCode() {
		return vCode;
	}

	public void setvCode(int vCode) {
		this.vCode = vCode;
	}

	private CustInfo(Parcel in) {
		id = in.readLong();
		uniqueId = in.readLong();
		name = in.readString();
		shortName=in.readString();
		email = in.readString();
		address = in.readString();
		telephone=in.readString();
		createTime = in.readString();
		createUserName = in.readString();
		userCount = in.readInt();
		boolean[] booleanArray = new boolean[1];
		in.readBooleanArray(booleanArray);
		isNeedCheck = booleanArray[0];
		isOpen=in.readInt();
		vCode = in.readInt();
	}

	public static Creator<CustInfo> CREATOR = new Creator<CustInfo>() {

		@Override
		public CustInfo createFromParcel(Parcel source) {
			return new CustInfo(source);
		}

		@Override
		public CustInfo[] newArray(int size) {
			return new CustInfo[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeLong(uniqueId);
		dest.writeString(name);
		dest.writeString(shortName);
		dest.writeString(email);
		dest.writeString(address);
		dest.writeString(telephone);
		dest.writeString(createTime);
		dest.writeString(createUserName);
		dest.writeInt(userCount);
		dest.writeBooleanArray(new boolean[]{isNeedCheck});
		dest.writeInt(isOpen);
		dest.writeInt(vCode);

	}

}
