package cn.yjt.oa.app.beans;

import java.lang.reflect.Type;

import android.os.Parcel;
import android.os.Parcelable;
import cn.yjt.oa.app.push.PushMessageData;

import com.google.gson.Gson;

public class RedPacketShareUrlInfo extends PushMessageData implements Parcelable {
	
	private long userId;
	private String phone;
	private String url;
	private String description;
	private int imageType;
    private String imageUrl;

	
	public RedPacketShareUrlInfo() {
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
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	


	public int getImageType() {
		return imageType;
	}


	public void setImageType(int imageType) {
		this.imageType = imageType;
	}


	public String getImageUrl() {
		return imageUrl;
	}


	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeLong(userId);
		dest.writeString(phone);
		dest.writeString(url);
		dest.writeString(description);
		dest.writeInt(imageType);
		dest.writeString(imageUrl);
	}

	private RedPacketShareUrlInfo(Parcel in) {
		super(in);
		userId = in.readLong();
		phone = in.readString();
		url = in.readString();
		description = in.readString();
		imageType = in.readInt();
		imageUrl = in.readString();
	}
	
	
	public static RedPacketShareUrlInfo parseJson(String json){
		Gson gson =new Gson();
		return gson.fromJson(json, RedPacketShareUrlInfo.class);
	}
	
	public static final Creator<RedPacketShareUrlInfo> CREATOR = new Creator<RedPacketShareUrlInfo>() {
		
		@Override
		public RedPacketShareUrlInfo[] newArray(int size) {
			return new RedPacketShareUrlInfo[size];
		}
		
		@Override
		public RedPacketShareUrlInfo createFromParcel(Parcel source) {
			return new RedPacketShareUrlInfo(source);
		}
	};


	@Override
	public Type getHandleType() {
		return getClass();
	}


	@Override
	protected String getMessageType() {
		return NEW_SHARE_LINK;
	}
}
