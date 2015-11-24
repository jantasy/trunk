package cn.yjt.oa.app.beans;

import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;

import android.annotation.TargetApi;
import android.os.Parcel;
import android.os.Parcelable;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.message.Message;
import cn.yjt.oa.app.message.MessageManager;
import cn.yjt.oa.app.push.HandleWholeMessage;
import cn.yjt.oa.app.push.PushMessageData;
import cn.yjt.oa.app.push.PushMessageManager.PushMessageHandler;
import cn.yjt.oa.app.widget.TimeLineAdapter.DateItem;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.Gson;

/**
 * 消息实体类
 *
 */
public class MessageInfo extends PushMessageData implements DateItem,Message,Parcelable,PushMessageHandler,HandleWholeMessage{

	public static final String TASK = "TASK";
	public static final String TASK_REPLY = "TASK_REPLY";
	public static final String NOTICE = "NOTICE";
	public static final String JOIN_CUST_APPLY = "JOIN_CUST_APPLY";
	public static final String CREATE_CUST_APPLY = "CREATE_CUST_APPLY";
	public static final String CUST_JOIN_INVITEINFO = "CUST_JOIN_INVITE";
	public static final String MESSAGE = "MESSAGE";
	public static final String SHARE_LINK = "SHARE_LINK";
	public static final String INVITE_USER = "INVITE_USER";
	
	private long id;
	private int isRead; //0.未读 1.已读
	private String updateTime;
	private String title;
	private String content;
	private String icon;
	private String type;
	private String payload;
	
	public MessageInfo() {
	}
	
	/*-----set、get方法START-----*/
	public void setRead(int read) {
		this.isRead = read;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
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
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
	public int getIsRead() {
		return isRead;
	}
	public void setIsRead(int isRead) {
		this.isRead = isRead;
		
		if ("task".equalsIgnoreCase(getType())){
			TaskInfo taskInfo = new Gson().fromJson(getPayload(), TaskInfo.class);
			long taskId = taskInfo.getId();
			MessageManager.getInstance().notifyReadChanged("task",taskId, isRead);
		}else if("notice".equalsIgnoreCase(getType())){
			NoticeInfo noticeInfo = new Gson().fromJson(getPayload(), NoticeInfo.class);
			long noticeId = noticeInfo.getId();
			MessageManager.getInstance().notifyReadChanged("notice",noticeId, isRead);
		}else if(JOIN_CUST_APPLY.equalsIgnoreCase(getType())){
			ApplyInfo applyInfo = new Gson().fromJson(getPayload(), ApplyInfo.class);
			int applyId = applyInfo.getId();
			MessageManager.getInstance().notifyReadChanged(JOIN_CUST_APPLY,applyId, isRead);
			MainApplication.addTag("cust"+applyInfo.getCustId());
		}else if(CREATE_CUST_APPLY.equalsIgnoreCase(getType())){
			CustRegisterInfo info = new Gson().fromJson(getPayload(), CustRegisterInfo.class);
			long custRegisterId = info.getId();
			MainApplication.addTag("cust"+info.getCustId());
			MessageManager.getInstance().notifyReadChanged(CREATE_CUST_APPLY,custRegisterId, isRead);
		}else if(CUST_JOIN_INVITEINFO.equalsIgnoreCase(getType())){
			CustJoinInviteInfo info = new Gson().fromJson(getPayload(), CustJoinInviteInfo.class);
			long custJoinInviteId = info.getId();
			MainApplication.addTag("cust"+info.getCustId());
			MessageManager.getInstance().notifyReadChanged(CUST_JOIN_INVITEINFO,custJoinInviteId, isRead);
		}else if(MESSAGE.equalsIgnoreCase(getType())){
			MessageManager.getInstance().notifyReadChanged(MESSAGE,id, isRead);
		}else if(SHARE_LINK.equalsIgnoreCase(getType())){
			MessageManager.getInstance().notifyReadChanged(SHARE_LINK,id, isRead);
		}else{
			MessageManager.getInstance().notifyReadChanged(MESSAGE,id, isRead);
		}
	}
	/*-----set、get方法END-----*/
	
	//当两条消息的id 相同时为同一条消息
	@Override
	public boolean equals(Object o) {
		if (o == this){
			return true;
		}
		if (o instanceof MessageInfo && getId() == ((MessageInfo) o).getId()){
			return true;
		}
		return false;
	}
	
	//hascode值加上消息的id
	@Override
	public int hashCode() {
		 return (int)getId()+"".hashCode();
	}
	@Override
	public Date getDate() {
		try {
			return BusinessConstants.parseTime(getUpdateTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date(System.currentTimeMillis());
	}

	@Override
	public long getTypeId() {
		if ("task".equalsIgnoreCase(getType())){
			TaskInfo taskInfo = new Gson().fromJson(getPayload(), TaskInfo.class);
			long taskId = taskInfo.getId();
			return taskId;
		}else if("notice".equalsIgnoreCase(getType())){
			NoticeInfo noticeInfo = new Gson().fromJson(getPayload(), NoticeInfo.class);
			long noticeId = noticeInfo.getId();
			return noticeId;
		}
		return 0;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	/*-----序列化本类对象的操作START-----*/
	//实现Parcelable的方法writeToParcel，将ParcelableDate序列化为一个Parcel对象 
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(updateTime);
		dest.writeString(title);
		dest.writeString(content);
		dest.writeString(icon);
		dest.writeLong(id);
		dest.writeString(type);
		dest.writeString(payload);
		dest.writeInt(isRead);
	}
	
	public MessageInfo(Parcel in){
		super(in);
		updateTime = in.readString();
		title = in.readString();
		content = in.readString();
		icon = in.readString();
		id = in.readLong();
		type = in.readString();
		payload = in.readString();
		isRead = in.readInt();
	}
	
	public static final Creator<MessageInfo> CREATOR = new Creator<MessageInfo>() {
		
		@Override
		public MessageInfo[] newArray(int size) {
			return new MessageInfo[size];
		}
		
		//将Parcel对象反序列化为ParcelableDate
		@Override
		public MessageInfo createFromParcel(Parcel source) {
			return new MessageInfo(source);
		}
	};
	/*-----序列化本类对象的操作END-----*/

	@Override
	public Type getHandleType() {
		return new TypeToken<MessageInfo>() {}.getType();
	}


	@Override
	protected String getMessageType() {
		return MESSAGE;
	}
	
	
	@Override
	public String toString() {
		return "MessageInfo [updateTime=" + updateTime + ", title=" + title
				+ ", content=" + content + ", icon=" + icon + ", id=" + id
				+ ", type=" + type + ", payload=" + payload + ", isRead="
				+ isRead + "]";
	}
}

