package cn.yjt.oa.app.push;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.Queue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.beans.MessageInfo;
import cn.yjt.oa.app.beans.PushMessage;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.http.AsyncRequest;

public class MessageGetter {

	private class ConnectivityReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isAvailable()) {
				requestMessageInQueue();
			}
		}
	}

	private static MessageGetter getter;

	private Queue<PushMessage> messageQueue;
	private Context context;

	private ConnectivityReceiver receiver;

	private MessageGetter(Context context) {
		this.messageQueue = new LinkedList<PushMessage>();
		this.context = context;
		IntentFilter filter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		receiver = new ConnectivityReceiver();
		context.registerReceiver(receiver, filter);
	}

	public static final synchronized MessageGetter getInstance() {
		if (getter == null) {
			getter = new MessageGetter(MainApplication.getAppContext());
		}
		return getter;
	}
	
	public Context getContext() {
		return context;
	}
	
	public void recycle(){
		context.unregisterReceiver(receiver);
	}

	public void addResultQueue(PushMessage pushMessage) {
		requestMessage(pushMessage);
	}
	
	private void requestMessageInQueue() {
		PushMessage poll = messageQueue.poll();
		if (poll != null) {
			requestMessage(poll);
		}
	}

	private void requestMessage(final PushMessage pushMessage) {
		String messageId = pushMessage.payload();
		if (messageId == null) {
			return;
		}
		Type responseType = new TypeToken<Response<MessageInfo>>() {
		}.getType();
		new AsyncRequest.Builder().setModule(AsyncRequest.MESSAGE_CENTER)
				.setModuleItem(pushMessage.payload())
				.setResponseType(responseType)
				.setResponseListener(new Listener<Response<MessageInfo>>() {

					@Override
					public void onErrorResponse(InvocationError error) {
						messageQueue.offer(pushMessage);
					}

					@Override
					public void onResponse(Response<MessageInfo> response) {
						if (response.getCode() == 0) {
							MessageInfo messageInfo = response.getPayload();
							PushMessageManager.handleMessageInfo(messageInfo,pushMessage.getCustId());
							requestMessageInQueue();
						} else {
							messageQueue.offer(pushMessage);
						}
					}

				}).build().get();
	}

}
