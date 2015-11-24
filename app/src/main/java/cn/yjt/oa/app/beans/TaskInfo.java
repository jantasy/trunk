package cn.yjt.oa.app.beans;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import cn.yjt.oa.app.http.GsonHolder;
import cn.yjt.oa.app.message.Message;
import cn.yjt.oa.app.message.MessageManager;
import cn.yjt.oa.app.push.PushMessageData;
import cn.yjt.oa.app.push.PushMessageManager.PushMessageHandler;

/**
 * 任务实体类
 *
 */
public class TaskInfo extends PushMessageData implements Parcelable, PushMessageHandler, Message {

	private long id;
	private String content;
	private UserSimpleInfo fromUser;
	private List<UserSimpleInfo> toUsers;
	private List<GroupInfo> toGroups;
	private String createTime;
	private String lastReplyTime;
	private int replyCount;
	private int status;
	private int mark;
	private boolean dailyReport;
	private String icon;
	private int isRead;
	private String title;
	private String imageUrl;
	private String voiceUrl;

	/*-----构造方法START-----*/
	public TaskInfo(long id, String content, UserSimpleInfo fromUser, List<UserSimpleInfo> toUsers, String createTime,
			String lastReplyTime, int status, int mark) {
		this.id = id;
		this.content = content;
		this.setFromUser(fromUser);
		this.setToUsers(toUsers);
		this.createTime = createTime;
		this.lastReplyTime = lastReplyTime;
		this.status = status;
		this.mark = mark;
	}

	public TaskInfo(long id, String content, String createTime, String lastReplyTime, int status, int mark) {
		this.id = id;
		this.content = content;
		this.createTime = createTime;
		this.lastReplyTime = lastReplyTime;
		this.status = status;
		this.mark = mark;
	}

	public TaskInfo() {

	}
	/*-----构造方法END-----*/
	
	private TaskInfo(Parcel in) {
		super(in);

		id = in.readLong();
		content = in.readString();
		fromUser = in.readParcelable(UserSimpleInfo.class.getClassLoader());
		toUsers = new ArrayList<UserSimpleInfo>();
		in.readTypedList(toUsers, UserSimpleInfo.CREATOR);
		toGroups = new ArrayList<GroupInfo>();
		in.readTypedList(toGroups, GroupInfo.CREATOR);
		createTime = in.readString();
		lastReplyTime = in.readString();
		replyCount = in.readInt();
		status = in.readInt();
		mark = in.readInt();
		boolean[] booleanArray = new boolean[1];
		in.readBooleanArray(booleanArray);
		dailyReport = booleanArray[0];
		isRead = in.readInt();
		title = in.readString();
		imageUrl = in.readString();
		voiceUrl = in.readString();
		icon = in.readString();
	}

	/*-----set、get方法START-----*/
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void setRead(int read) {
		this.isRead = read;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public UserSimpleInfo getFromUser() {
		return fromUser;
	}

	public void setFromUser(UserSimpleInfo fromUser) {
		this.fromUser = fromUser;
	}

	public List<UserSimpleInfo> getToUsers() {
		return toUsers;
	}

	public void setToUsers(List<UserSimpleInfo> toUsers) {
		this.toUsers = toUsers;
	}

	public List<GroupInfo> getToGroups() {
		return toGroups;
	}

	public void setToGroups(List<GroupInfo> toGroups) {
		this.toGroups = toGroups;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getLastReplyTime() {
		return lastReplyTime;
	}

	public void setLastReplyTime(String lastReplyTime) {
		this.lastReplyTime = lastReplyTime;
	}

	public int getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}

	public boolean isDailyReport() {
		return dailyReport;
	}

	public void setDailyReport(boolean dailyReport) {
		this.dailyReport = dailyReport;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getIsRead() {
		return isRead;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getVoiceUrl() {
		return voiceUrl;
	}

	public void setVoiceUrl(String voiceUrl) {
		this.voiceUrl = voiceUrl;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
		MessageManager.getInstance().notifyReadChanged("task", id, isRead);
	}
	/*-----set、get方法END-----*/
	
	public void writeToParcel(Parcel out, int flags) {
		super.writeToParcel(out, flags);
		out.writeLong(id);
		out.writeString(content);
		out.writeParcelable(fromUser, 0);
		out.writeTypedList(toUsers);
		out.writeTypedList(toGroups);
		out.writeString(createTime);
		out.writeString(lastReplyTime);
		out.writeInt(replyCount);
		out.writeInt(status);
		out.writeInt(mark);
		out.writeBooleanArray(new boolean[] { dailyReport });
		out.writeInt(isRead);
		out.writeString(title);
		out.writeString(imageUrl);
		out.writeString(voiceUrl);
		out.writeString(icon);
	}

	public static final Parcelable.Creator<TaskInfo> CREATOR = new Parcelable.Creator<TaskInfo>() {
		public TaskInfo createFromParcel(Parcel in) {
			return new TaskInfo(in);
		}

		public TaskInfo[] newArray(int size) {
			return new TaskInfo[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	@Override
	public Type getHandleType() {
		return TaskInfo.class;
	}

	@Override
	public String toString() {
		return "TaskInfo [id=" + id + ", content=" + content + "]";
	}

	@Override
	public long getTypeId() {
		return id;
	}

	@Override
	protected String getMessageType() {
		return NEW_TASK;
	}

	public static TaskInfo parseJson(String json) {
		return GsonHolder.getInstance().getGson().fromJson(json, TaskInfo.class);
	}

}
