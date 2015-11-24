package cn.yjt.oa.app.beans;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;

public class GroupInfo implements Parcelable {

	private long id;
	private String name;
	private String description;
	private UserSimpleInfo[] users;
	private String avatar;
	private String createTime;
	private String updateTime;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UserSimpleInfo[] getUsers() {
		return users;
	}

	public void setUsers(UserSimpleInfo[] users) {
		this.users = users;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupInfo other = (GroupInfo) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public CharSequence toCharSequence() {
		SpannableString spannable;
		if (!TextUtils.isEmpty(name)) {
			spannable = new SpannableString("@" + getName());
		} else {
			spannable = new SpannableString("@" + getId());
		}
		int length = spannable.length();
		if (length > 0) {
			spannable.setSpan(new RecipientSpan(this), 0, length,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return spannable;
	}

	public static class RecipientSpan extends ClickableSpan {
		public static final int USER_NAME_DISPLAY_COLOR = 0xFF078EED;
		private final GroupInfo recipient;

		public RecipientSpan(GroupInfo recipient) {
			super();
			this.recipient = recipient;
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setColor(USER_NAME_DISPLAY_COLOR);
			// ds.setUnderlineText(true);
		}

		@Override
		public void onClick(View view) {
		}

		public GroupInfo getRecipient() {
			return recipient;
		}
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeTypedArray(users, flags);
		dest.writeString(avatar);
		dest.writeString(createTime);
		dest.writeString(updateTime);
	}

	public static final Creator<GroupInfo> CREATOR = new Creator<GroupInfo>() {

		@Override
		public GroupInfo createFromParcel(Parcel source) {
			GroupInfo info = new GroupInfo();
			info.setId(source.readLong());
			info.setName(source.readString());
			info.setDescription(source.readString());
			info.setUsers(source.createTypedArray(UserSimpleInfo.CREATOR));
			info.setAvatar(source.readString());
			info.setCreateTime(source.readString());
			info.setUpdateTime(source.readString());
			return info;
		}

		@Override
		public GroupInfo[] newArray(int size) {
			return new GroupInfo[size];
		}
	};
}
