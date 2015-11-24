package cn.yjt.oa.app.push;

import com.activeandroid.Model;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.push.PushMessageManager.PushMessageHandler;
import cn.yjt.oa.app.widget.TableMsgWidget;


public abstract class PushMessageData implements PushMessageHandler,Parcelable {

	public static final String NEW_TASK = "TASK";
	public static final String NEW_MESSAGE = "NEW_MESSAGE";
	public static final String NEW_VERSION = "NEW VERSION";
	public static final String NEW_NOTICE = "NOTICE";
	public static final String NEW_JOIN_CUST_APPLY = "JOIN_CUST_APPLY";
	public static final String NEW_CREATE_CUST_APPLY = "CREATE_CUST_APPLY";
	public static final String NEW_CUST_JOIN_INVITE = "CUST_JOIN_INVITE";
	public static final String NEW_SHARE_LINK = "SHARE_LINK";
	public static final String NEW_INVITE_USER = "INVITE_USER";
	
	public static final String PUSH_MSG_TAG = "PUSH_MSG_TAG";
	public static final String MESSAGE = "MESSAGE";
	
	private String userCustId;
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(userCustId);
	}
	public PushMessageData(Parcel in) {
		userCustId = in.readString();
	}
	
	public PushMessageData() {
	}
	
	public String getUserCustId() {
		return userCustId;
	}

	public void setUserCustId(String userCustId) {
		this.userCustId = userCustId;
	}

	@Override
	public final void handleCmd(Object object) {
		if(onHandleCmd(object)){
			return;
		}
		Context context = MainApplication.getAppContext();
		Intent service = new Intent(context, TableMsgWidget.class);
		service.putExtra(getMessageType(), (PushMessageData)object);
		service.putExtra(PUSH_MSG_TAG, getMessageType());
		context.startService(service);	
	}
	
	@Override
	public final String getHandleCmd() {
		return getMessageType();
	}

	protected abstract String getMessageType();
	
	/**
	 *  If it returned true,handleCmd method would not execute.
	 * @param object handle Object.
	 * @return
	 */
	protected boolean onHandleCmd(Object object){
		return false;
	}
	
	public void setTitle(String title){
		
	}
	
	
	public String getTitle(){
		return "";
	}
	
	public String getContent(){
		return "";
	}

}
