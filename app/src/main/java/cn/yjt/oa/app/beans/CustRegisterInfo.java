package cn.yjt.oa.app.beans;

import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;

import android.os.Parcel;
import android.os.Parcelable;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.push.PushMessageData;
import cn.yjt.oa.app.push.PushMessageManager.PushMessageHandler;

public class CustRegisterInfo extends PushMessageData implements PushMessageHandler,Parcelable{

	private long id;
	private String name; // 注册企业名称
	private String licenseNum; // 营业执照编号
	private String licenseImg; // 营业执照URL；只存文件名
	private long createUserId; // 注册者ID
	private String idCardName; //经营者身份证姓名
	private String idCard; // 经营者身份证号
	private String idCardImg; // 经营者身份证扫描件
	private String phone; // 经营者手机号
	private int checkStatus; // 0：待审核 1：审核通过 -1：审核不通过
	
	private String registerTime; //注册时间
	private String checkTime; //审核时间
	private long checkUserId; //审核人

	private String address;		//注册地址
	private String title;		//推送消息的标题
	private String content;		//推送消息的内容
	private long custId;
	
	
	public CustRegisterInfo() {
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
	public String getLicenseNum() {
		return licenseNum;
	}
	public void setLicenseNum(String licenseNum) {
		this.licenseNum = licenseNum;
	}
	public String getLicenseImg() {
		return licenseImg;
	}
	public void setLicenseImg(String licenseImg) {
		this.licenseImg = licenseImg;
	}
	public long getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(long createUserId) {
		this.createUserId = createUserId;
	}
	public String getIdCardName() {
		return idCardName;
	}
	public void setIdCardName(String idCardName) {
		this.idCardName = idCardName;
	}
	public String getIdCard() {
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	public String getIdCardImg() {
		return idCardImg;
	}
	public void setIdCardImg(String idCardImg) {
		this.idCardImg = idCardImg;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	/**
	 * 0：待审核 1：审核通过 -1：审核不通过
	 * @return
	 */
	public int getCheckStatus() {
		return checkStatus;
	}
	public void setCheckStatus(int checkStatus) {
		this.checkStatus = checkStatus;
	}
	public String getRegisterTime() {
		return registerTime;
	}
	public void setRegisterTime(String registerTime) {
		this.registerTime = registerTime;
	}
	public String getCheckTime() {
		return checkTime;
	}
	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}
	public long getCheckUserId() {
		return checkUserId;
	}
	public void setCheckUserId(long checkUserId) {
		this.checkUserId = checkUserId;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	
	@Override
	public String toString() {
		return "CustRegisterInfo [id=" + id + ", name=" + name
				+ ", licenseNum=" + licenseNum + ", licenseImg=" + licenseImg
				+ ", createUserId=" + createUserId + ", idCardName="
				+ idCardName + ", idCard=" + idCard + ", idCardImg="
				+ idCardImg + ", phone=" + phone + ", checkStatus="
				+ checkStatus + ", registerTime=" + registerTime
				+ ", checkTime=" + checkTime + ", checkUserId=" + checkUserId
				+ ", address=" + address + ", title=" + title + ", content="
				+ content + "]";
	}

	@Override
	public Type getHandleType() {
		return new TypeToken<CustRegisterInfo>() {}.getType();
	}

	
	public CustRegisterInfo(Parcel in){
		super(in);
		
		id = in.readLong();
		name = in.readString();
		licenseNum = in.readString();
		licenseImg = in.readString();
		createUserId = in.readLong();
		idCardName = in.readString();
		idCard = in.readString();
		idCardImg = in.readString();
		phone = in.readString();
		checkStatus = in.readInt();
		registerTime = in.readString();
		checkTime = in.readString();
		checkUserId = in.readLong();
		address = in.readString();
		title = in.readString();
		content = in.readString();
		custId = in .readLong();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeString(licenseNum);
		dest.writeString(licenseImg);
		dest.writeLong(createUserId);
		dest.writeString(idCardName);
		dest.writeString(idCard);
		dest.writeString(idCardImg);
		dest.writeString(phone);
		dest.writeInt(checkStatus);
		dest.writeString(registerTime);
		dest.writeString(checkTime);
		dest.writeLong(checkUserId);
		dest.writeString(address);
		dest.writeString(title);
		dest.writeString(content);
		dest.writeLong(custId);
		
	}
	
	public static final Creator<CustRegisterInfo> CREATOR = new Creator<CustRegisterInfo>() {
		
		@Override
		public CustRegisterInfo[] newArray(int size) {
			return new CustRegisterInfo[size];
		}
		
		@Override
		public CustRegisterInfo createFromParcel(Parcel source) {
			return new CustRegisterInfo(source);
		}
	};


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	protected String getMessageType() {
		return NEW_CREATE_CUST_APPLY;
	}
	
	@Override
	protected boolean onHandleCmd(Object object) {
		CustRegisterInfo registerInfo = (CustRegisterInfo) object;
		if(registerInfo.getCheckStatus() == 1){
			MainApplication.addTag("cust"+registerInfo.getCustId());
		}
		return super.onHandleCmd(object);
	}
	
}
