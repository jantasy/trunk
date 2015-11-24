package cn.yjt.oa.app.push;

import io.luobo.common.http.volley.GsonConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Type;
import java.util.ArrayList;

import android.text.TextUtils;
import cn.yjt.oa.app.beans.ApplyInfo;
import cn.yjt.oa.app.beans.CustJoinInviteInfo;
import cn.yjt.oa.app.beans.CustRegisterInfo;
import cn.yjt.oa.app.beans.InviteUserInfo;
import cn.yjt.oa.app.beans.MessageInfo;
import cn.yjt.oa.app.beans.NoticeInfo;
import cn.yjt.oa.app.beans.PushMessage;
import cn.yjt.oa.app.beans.ShareLinkInfo;
import cn.yjt.oa.app.beans.TaskInfo;
import cn.yjt.oa.app.http.GsonHolder;

/**推送消息管理类*/
public class PushMessageManager {

	public static final String CMD_NEW_MESSAGE = "NEW_MESSAGE";
	public static final String CMD_NEW_TASK = "TASK";

	public interface PushMessageHandler {
		String getHandleCmd();

		Type getHandleType();

		void handleCmd(Object object);
	}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface RegisterPushMessageHandler {

	}

	static final ArrayList<PushMessageHandler> handlers = new ArrayList<PushMessageHandler>();

	public static void registPushMessageHandler(PushMessageHandler handler) {
		synchronized (handlers) {
			handlers.add(handler);
		}
	}

	public static void registerPushMessageHandlers() {
		PushMessageManager.registPushMessageHandler(new TaskInfo());
		PushMessageManager.registPushMessageHandler(new NoticeInfo());
		PushMessageManager.registPushMessageHandler(new MessageInfo());
		PushMessageManager.registPushMessageHandler(new ApplyInfo());
		PushMessageManager.registPushMessageHandler(new CustRegisterInfo());
		PushMessageManager.registPushMessageHandler(new CustJoinInviteInfo());
		PushMessageManager.registPushMessageHandler(new ShareLinkInfo());
		PushMessageManager.registPushMessageHandler(new InviteUserInfo());
	}

	public static void unregistPushMessageHandler(PushMessageHandler handler) {
		synchronized (handlers) {
			handlers.remove(handler);
		}
	}

	public static void handlePushMessage(PushMessage pushMessage) {
		String cmd = pushMessage.getCmd();
		GsonConverter converter = new GsonConverter(GsonHolder.getInstance()
				.getGson());
		for (PushMessageHandler handler : handlers) {
			if (TextUtils.equals(cmd, handler.getHandleCmd())) {
				Type type = handler.getHandleType();
				handler.handleCmd(type != null ? pushMessage.getPayload(
						converter, type) : null);
				return;// if the message is handled,return.
			}
		}
		// if the message is not handled,it shows that this message is a new
		// type message(ApplyInfo or CustRegisterInfo).These types message is
		// wrapped by a MessageInfo, so let MessageInfo handle it.
		// In the future,all push message will be wrapped by MessageInfo.
		for (PushMessageHandler handler : handlers) {
			// MessageInfo's handle cmd is null.
			if (handler.getHandleCmd() == null) {
				Type type = handler.getHandleType();
				handler.handleCmd(type != null ? pushMessage.getPayload(
						converter, type) : null);
			}
		}

	}
	
	public static void handleMessageInfo(MessageInfo messageInfo,String custId) {
		GsonConverter converter = new GsonConverter(GsonHolder.getInstance()
				.getGson());
		if(messageInfo!=null){
			for (PushMessageHandler handler : handlers) {
				if (TextUtils.equals(messageInfo.getType(), handler.getHandleCmd())) {
					Type type = handler.getHandleType();
					PushMessageData pushMessageData;
					if (handler instanceof HandleWholeMessage) {
						pushMessageData = messageInfo;
					} else {
						pushMessageData = converter.convertToObject(
								messageInfo.getPayload(), type);
						if(pushMessageData != null){
							pushMessageData.setTitle(messageInfo.getTitle());
						}
					}
					if(pushMessageData != null){
						pushMessageData.setUserCustId(custId);
					}
					handler.handleCmd(type != null ? pushMessageData : null);
					return ;// if the message is handled,return.
				}
			}
			// if the message is not handled,it shows that this message is a new
			// type message(ApplyInfo or CustRegisterInfo).These types message is
			// wrapped by a MessageInfo, so let MessageInfo handle it.
			// In the future,all push message will be wrapped by MessageInfo.
			for (PushMessageHandler handler : handlers) {
				// MessageInfo's handle cmd is null.
				if (handler.getHandleCmd() == null) {
					Type type = handler.getHandleType();
					if(messageInfo.getPayload() != null){
						handler.handleCmd(type != null ? converter.convertToObject(messageInfo.getPayload(), type) : null);
					}
				}
			}
		}
		

	}
}
