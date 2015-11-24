package cn.yjt.oa.app.beans;

import java.lang.reflect.Type;

import android.os.Parcel;
import cn.yjt.oa.app.push.PushMessageData;

public class ContactsSync extends PushMessageData{

	public ContactsSync(Parcel in) {
		super(in);
	}
	public ContactsSync() {
	}

	@Override
	public Type getHandleType() {
		return ContactsSync.class;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
	}

	@Override
	protected String getMessageType() {
		return null;
	}

	
}
