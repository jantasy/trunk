package cn.yjt.oa.app.beans;

import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;

import android.os.Parcel;
import android.os.Parcelable;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.push.PushMessageData;
import cn.yjt.oa.app.push.PushMessageManager.PushMessageHandler;

/**
 * 申请加入企业信息
 * 
 * @author chenshang
 * 
 */
public class ApplyInfo extends PushMessageData implements Parcelable ,PushMessageHandler{

	private int id;// ID
	private long userId;// 申请人ID
	private long custId;// 申请加入的公司ID
	private String createTime;// 申请时间
	private String message;// 申请附言（理由
	private int handleStatus;// （0：待审核 1：审核通过 -1：审核不通过）
	private String handleTime;// 处理时间
	private long handleUserId;// 处理人ID
	private String title;
	private String content;
	private String phone;//申请人的 手机号

	public ApplyInfo() {
		// TODO Auto-generated constructor stub
	}
	//
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	

	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * （0：待审核 1：审核通过 -1：审核不通过）
	 * @return
	 */
	public int getHandleStatus() {
		return handleStatus;
	}

	public void setHandleStatus(int handleStatus) {
		this.handleStatus = handleStatus;
	}

	public String getHandleTime() {
		return handleTime;
	}

	public void setHandleTime(String handleTime) {
		this.handleTime = handleTime;
	}

	public long getHandleUserId() {
		return handleUserId;
	}

	public void setHandleUserId(long handleUserId) {
		this.handleUserId = handleUserId;
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(id);
		dest.writeLong(userId);
		dest.writeLong(custId);
		dest.writeString(createTime);
		dest.writeString(message);
		dest.writeInt(handleStatus);
		dest.writeLong(handleUserId);
		dest.writeString(title);
		dest.writeString(content);
		dest.writeString(phone);
	}

	private ApplyInfo(Parcel in) {
		super(in);
		id = in.readInt();
		userId = in.readLong();
		custId = in.readLong();
		createTime = in.readString();
		message = in.readString();
		handleStatus = in.readInt();
		handleUserId = in.readLong();
		title = in.readString();
		content = in.readString();
		phone=in.readString();
	}

	public static final Creator<ApplyInfo> CREATOR = new Creator<ApplyInfo>() {

		@Override
		public ApplyInfo[] newArray(int size) {
			return new ApplyInfo[size];
		}

		@Override
		public ApplyInfo createFromParcel(Parcel source) {
			return new ApplyInfo(source);
		}
	};

	@Override
	public Type getHandleType() {
		return new TypeToken<ApplyInfo>() {}.getType();
	}

	@Override
	protected String getMessageType() {
		return NEW_JOIN_CUST_APPLY;
	}
	
	@Override
	protected boolean onHandleCmd(Object object) {
		ApplyInfo applyInfo = (ApplyInfo) object;
		if(applyInfo.handleStatus == 1){
			MainApplication.addTag("cust"+applyInfo.getCustId());
		}
		return super.onHandleCmd(object);
	}

}
