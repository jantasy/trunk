package cn.yjt.oa.app.beans;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import cn.yjt.oa.app.contactlist.data.ContactlistContactInfo;

/**
 * 简易用户信息
 *
 */
public class UserSimpleInfo implements Parcelable {

	private long id;
	private String name;
	private String icon;

	public UserSimpleInfo() {

	}

	public UserSimpleInfo(ContactlistContactInfo info){
		this.id = info.getContactInfo().getUserId();
		this.name = info.getContactInfo().getName();
		this.icon = info.getContactInfo().getAvatar();
	}
	
	public UserSimpleInfo(long id, String name) {
		this.id = id;
		this.name = name;
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
	

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
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
		UserSimpleInfo other = (UserSimpleInfo) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeString(icon);
	}

	private UserSimpleInfo(Parcel p){
		id = p.readLong();
		name = p.readString();
		icon = p.readString();
	}
	
	public static Parcelable.Creator<UserSimpleInfo> getCreator() {
       return CREATOR;
	}

	public static final Parcelable.Creator<UserSimpleInfo> CREATOR = new Parcelable.Creator<UserSimpleInfo>() {

		@Override
		public UserSimpleInfo createFromParcel(Parcel source) {
			return new UserSimpleInfo(source);
		}

		@Override
		public UserSimpleInfo[] newArray(int size) {
			return new UserSimpleInfo[size];
		}
	};
	
	public CharSequence toCharSequence() {
        SpannableString spannable;
        if (!TextUtils.isEmpty(name)) {
            spannable = new SpannableString("@" + getName());
        } else {
            spannable = new SpannableString("@" + getId());
        }
        int length = spannable.length();
        if (length > 0) {
            spannable.setSpan(
                    new RecipientSpan(this),
                    0,
                    length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
        return spannable;
    }
    
    public static class RecipientSpan extends ClickableSpan {
        public static final int USER_NAME_DISPLAY_COLOR = 0xFF078EED;
        private final UserSimpleInfo recipient;
     
        public RecipientSpan(UserSimpleInfo recipient) {
            super();
            this.recipient = recipient;
        }
        
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(USER_NAME_DISPLAY_COLOR);
//            ds.setUnderlineText(true);
        }
     
        @Override
        public void onClick(View view) {
        }
        
        public UserSimpleInfo getRecipient() {
            return recipient;
        }
    }
    
    private static class UserSpannableString extends SpannableString {

        public UserSpannableString(CharSequence source) {
            super(source);
        }
        
    }

	@Override
	public String toString() {
		return "UserSimpleInfo [id=" + id + ", name=" + name + ", icon=" + icon
				+ "]";
	}

    
    
}
