package cn.yjt.oa.app.beans;

import java.lang.reflect.Type;

import android.os.Parcel;
import android.os.Parcelable;
import cn.yjt.oa.app.message.Message;
import cn.yjt.oa.app.message.MessageManager;
import cn.yjt.oa.app.push.PushMessageData;
import cn.yjt.oa.app.push.PushMessageManager.PushMessageHandler;

/**
 * 消息实体类 
 *
 */
public class NoticeInfo extends PushMessageData implements Parcelable, PushMessageHandler, Message {

	public NoticeInfo() {
	}

	private long id;
	private int fromType;//0:企业管理员发布消息 1：系统通告
	private String title;
	private String content;
	private String image;
	private String createTime;
	private String relatedType;//关联类型：
	private long relatedId;
	private int isRead;
	private String fromUser;//格式为：部门+” “ +姓名

	@Override
	public void setRead(int read) {
		this.isRead = read;
	}

	/*-----set、get方法START-----*/
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public int getFromType() {
		return fromType;
	}

	public void setFromType(int fromType) {
		this.fromType = fromType;
	}

	public String getRelatedType() {
		return relatedType;
	}

	public void setRelatedType(String relatedType) {
		this.relatedType = relatedType;
	}

	public long getRelatedId() {
		return relatedId;
	}

	public void setRelatedId(long relatedId) {
		this.relatedId = relatedId;
	}

	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	/**
	 * 
	 * @param isRead
	 *            0:未读 1：已读
	 */
	public void setIsRead(int isRead) {
		this.isRead = isRead;
		MessageManager.getInstance().notifyReadChanged("notice", id, isRead);
	}

	/**
	 * 
	 * @return isRead 0:未读 1：已读
	 */
	public int getIsRead() {
		return isRead;
	}
	/*-----set、get方法END-----*/

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeLong(id);
		dest.writeInt(fromType);
		dest.writeString(title);
		dest.writeString(content);
		dest.writeString(image);
		dest.writeString(createTime);
		dest.writeString(relatedType);
		dest.writeLong(relatedId);
		dest.writeInt(isRead);
		dest.writeString(fromUser);
	}

	public NoticeInfo(Parcel in) {
		super(in);
		id = in.readLong();
		fromType = in.readInt();
		title = in.readString();
		content = in.readString();
		image = in.readString();
		createTime = in.readString();
		relatedType = in.readString();
		relatedId = in.readLong();
		isRead = in.readInt();
		fromUser = in.readString();
	}

	public static final Creator<NoticeInfo> CREATOR = new Creator<NoticeInfo>() {

		@Override
		public NoticeInfo createFromParcel(Parcel source) {
			return new NoticeInfo(source);
		}

		@Override
		public NoticeInfo[] newArray(int size) {
			return new NoticeInfo[size];
		}
	};

	@Override
	public Type getHandleType() {
		return NoticeInfo.class;
	}

	@Override
	public long getTypeId() {
		return id;
	}

	@Override
	protected String getMessageType() {
		return NEW_NOTICE;
	}

}
