package cn.yjt.oa.app.beans;

import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;

import android.os.Parcel;
import android.os.Parcelable;
import cn.yjt.oa.app.push.PushMessageData;
import cn.yjt.oa.app.push.PushMessageManager.PushMessageHandler;


public class InviteUserInfo extends PushMessageData implements PushMessageHandler, Parcelable{
	
	private long id;
	
	private long userId;			//被邀请人ID
	private long custId;			//邀请加入的公司ID
	private String createTime;		//邀请时间时间
	private String message;			//邀请附言（申请理由等）
	
	private long inviteUserId;		//邀请人ID
	private int handleStatus;		//处理状态：（0：待处理 1：已同意 -1：已拒绝）
	private String handleTime;		//处理时间
	private String handleMessage;	//处理附言（拒绝理由等）
	private long handleAdminId;	//处理邀请同事请求的企业管理员Id
	
	private int isCancel; //是否已经取消，0：未取消，-1：已取消；已取消的申请将不做处理
	private String phone; //被邀请人手机号，同一手机号对应企业邀请只有一条
	
	private String inviteName; //被邀请人姓名
	
	private String title; //消息中显示的标题
	private String content; //消息中显示的内容
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
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
	public long getInviteUserId() {
		return inviteUserId;
	}
	public void setInviteUserId(long inviteUserId) {
		this.inviteUserId = inviteUserId;
	}
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
	public String getHandleMessage() {
		return handleMessage;
	}
	public void setHandleMessage(String handleMessage) {
		this.handleMessage = handleMessage;
	}
	public int getIsCancel() {
		return isCancel;
	}
	public void setIsCancel(int isCancel) {
		this.isCancel = isCancel;
	}
	public String getInviteName() {
		return inviteName;
	}
	public void setInviteName(String inviteName) {
		this.inviteName = inviteName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
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
	public long getHandleAdminId() {
		return handleAdminId;
	}
	public void setHandleAdminId(long handleAdminId) {
		this.handleAdminId = handleAdminId;
	}
	
	public InviteUserInfo(){
		
	} 
	private InviteUserInfo(Parcel in) {
		super(in);
		id = in.readLong();
		userId = in.readLong();
		custId = in.readLong();
		createTime = in.readString();
		message = in.readString();
		inviteUserId = in.readLong();
		handleStatus = in.readInt();
		handleTime = in.readString();
		handleMessage = in.readString();
		handleAdminId = in.readLong();
		isCancel = in.readInt();
		
		phone = in.readString();
		inviteName = in.readString();
		title = in.readString();
		content = in.readString();
		

	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeLong(id);
		dest.writeLong(userId);
		dest.writeLong(custId);
		dest.writeString(createTime);
		dest.writeString(message);
		dest.writeLong(inviteUserId);
		dest.writeInt(handleStatus);
		dest.writeString(handleTime);
		dest.writeString(handleMessage);
		dest.writeLong(handleAdminId);
		dest.writeInt(isCancel);
		dest.writeString(phone);
		dest.writeString(inviteName);
		dest.writeString(title);
		dest.writeString(content);
	}

	public static final Creator<InviteUserInfo> CREATOR = new Creator<InviteUserInfo>() {

		@Override
		public InviteUserInfo[] newArray(int size) {
			return new InviteUserInfo[size];
		}

		@Override
		public InviteUserInfo createFromParcel(Parcel source) {
			return new InviteUserInfo(source);
		}
	};


	@Override
	public Type getHandleType() {
		return new TypeToken<InviteUserInfo>() {}.getType();
	}

	@Override
	protected String getMessageType() {
		return NEW_INVITE_USER;
	}
}
