package cn.yjt.oa.app.beans;

import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;

import android.os.Parcel;
import android.os.Parcelable;
import cn.yjt.oa.app.push.PushMessageData;
import cn.yjt.oa.app.push.PushMessageManager.PushMessageHandler;

public class CustJoinInviteInfo extends PushMessageData implements PushMessageHandler, Parcelable {
	
	private int id;
	private long userId; // 被邀请人ID
	private long custId; // 邀请加入的公司ID
	private String createTime; // 邀请时间时间
	private String message; // 邀请附言（申请理由等）

	private long inviteUserId; // 邀请人ID
	private int handleStatus; // 处理状态：（0：待处理 1：同意 -1：拒绝）
	private String handleTime; // 处理时间
	private String handleMessage; // 处理附言（拒绝理由等）

	private String title; // 推送消息的标题
	private String content; // 推送消息的内容
	
	public CustJoinInviteInfo() {
	}

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

	private CustJoinInviteInfo(Parcel in) {
		super(in);
		id = in.readInt();
		userId = in.readLong();
		custId = in.readLong();
		createTime = in.readString();
		message = in.readString();
		inviteUserId = in.readLong();
		handleStatus = in.readInt();
		handleTime = in.readString();
		handleMessage = in.readString();
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
		dest.writeInt(id);
		dest.writeLong(userId);
		dest.writeLong(custId);
		dest.writeString(createTime);
		dest.writeString(message);
		dest.writeLong(inviteUserId);
		dest.writeInt(handleStatus);
		dest.writeString(handleTime);
		dest.writeString(handleMessage);
		dest.writeString(title);
		dest.writeString(content);
	}

	public static final Creator<CustJoinInviteInfo> CREATOR = new Creator<CustJoinInviteInfo>() {

		@Override
		public CustJoinInviteInfo[] newArray(int size) {
			return new CustJoinInviteInfo[size];
		}

		@Override
		public CustJoinInviteInfo createFromParcel(Parcel source) {
			return new CustJoinInviteInfo(source);
		}
	};


	@Override
	public Type getHandleType() {
		return new TypeToken<CustJoinInviteInfo>() {}.getType();
	}

	@Override
	protected String getMessageType() {
		return NEW_CUST_JOIN_INVITE;
	}

}
