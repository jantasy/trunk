package cn.yjt.oa.app.beans;

import java.lang.reflect.Type;

import android.content.Intent;
import android.os.Parcel;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.push.PushMessageData;

public class NewMessageInfo extends PushMessageData{

	@Override
	public Type getHandleType() {
		return NewMessageInfo.class;
	}

	@Override
	protected boolean onHandleCmd(Object object) {
		Intent intent = new Intent();
		intent.setAction("yijitong.action.new_message");
		MainApplication.getAppContext().sendBroadcast(intent);
		return true;
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
		return NEW_MESSAGE;
	}

}
