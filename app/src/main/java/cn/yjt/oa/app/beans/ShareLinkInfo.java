package cn.yjt.oa.app.beans;

import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;

import android.os.Parcel;
import android.os.Parcelable;
import cn.yjt.oa.app.push.HandleWholeMessage;
import cn.yjt.oa.app.push.PushMessageData;
import cn.yjt.oa.app.push.PushMessageManager.PushMessageHandler;

public class ShareLinkInfo extends PushMessageData implements PushMessageHandler, Parcelable ,HandleWholeMessage{
	
	
	private String updateTime;
	private String title;
	private String content;
	private String icon;
	private long id;
	private String type;
	private String payload;
	private int isRead;
	

	
	public void setIsRead(int isRead) {
		this.isRead = isRead;
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
	
	
	@Override
	public boolean equals(Object o) {
		if (o == this){
			return true;
		}
		if (o instanceof ShareLinkInfo && getId() == ((ShareLinkInfo) o).getId()){
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		 return (int)getId()+"".hashCode();
	}

	

	@Override
	public int describeContents() {
		return 0;
	}

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
	
	public ShareLinkInfo() {
	}
	
	public ShareLinkInfo(Parcel in){
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
	public ShareLinkInfo(MessageInfo info){
		updateTime = info.getUpdateTime();
		title = info.getTitle();
		content = info.getContent();
		icon = info.getIcon();
		id = info.getId();
		type = info.getType();
		payload = info.getPayload();
		isRead = info.getIsRead();
	}
	
	public static final Creator<ShareLinkInfo> CREATOR = new Creator<ShareLinkInfo>() {
		
		@Override
		public ShareLinkInfo[] newArray(int size) {
			return new ShareLinkInfo[size];
		}
		
		@Override
		public ShareLinkInfo createFromParcel(Parcel source) {
			return new ShareLinkInfo(source);
		}
	};


	@Override
	public Type getHandleType() {
		return new TypeToken<ShareLinkInfo>() {}.getType();
	}


	@Override
	protected String getMessageType() {
		return NEW_SHARE_LINK;
	}
}

