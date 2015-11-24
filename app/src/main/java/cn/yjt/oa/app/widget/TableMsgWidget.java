package cn.yjt.oa.app.widget;

import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;
import io.luobo.launcher.floatwidget.FloatWidgetService;
import io.luobo.launcher.floatwidget.aidl.IFloatWidgetService;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.RemoteViews;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.ApplyInfo;
import cn.yjt.oa.app.beans.CustJoinInviteInfo;
import cn.yjt.oa.app.beans.CustRegisterInfo;
import cn.yjt.oa.app.beans.InviteUserInfo;
import cn.yjt.oa.app.beans.MessageInfo;
import cn.yjt.oa.app.beans.NoticeInfo;
import cn.yjt.oa.app.beans.TaskInfo;
import cn.yjt.oa.app.push.MessageWigetActivity;
import cn.yjt.oa.app.push.PushMessageData;

public class TableMsgWidget extends Service {
	
	public static final String EXTRA_DELETE_WIDGET = "delete_widget";
	
	IFloatWidgetService mFloatWidgeService;
	int mAssistiveTouchWidget;
	ServiceConnection mFloatWidgetConnection;
	
	private ArrayList<PushMessageData> listPushMessage;
	
	private TaskInfo taskInfo;
	private NoticeInfo noticeInfo;
	private ApplyInfo applyInfo;
	private MessageInfo messageInfo;
	private CustRegisterInfo custRegisterInfo;
	private CustJoinInviteInfo custJoinInviteInfo;
	private MessageInfo shareLinkInfo;
	private InviteUserInfo inviteUserInfo;
	
	private String tag;
	
	private static TableMsgWidget msgWidget;
	
	public static TableMsgWidget getTableMsgWidget(){
		return msgWidget;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		msgWidget = this;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (listPushMessage == null) {
			listPushMessage = new ArrayList<PushMessageData>();
		}
		if (intent != null) {
			if(intent.hasExtra(EXTRA_DELETE_WIDGET)){
				deleteMsgWidget();
				return super.onStartCommand(intent, flags, startId);
			}
			tag = intent.getStringExtra(PushMessageData.PUSH_MSG_TAG);
			if (PushMessageData.NEW_TASK.equals(tag)) {
				taskInfo = intent.getParcelableExtra(PushMessageData.NEW_TASK);
				listPushMessage.add(taskInfo);
			} else if (PushMessageData.NEW_NOTICE.equals(tag)) {
				noticeInfo = intent.getParcelableExtra(PushMessageData.NEW_NOTICE);
				listPushMessage.add(noticeInfo);
			}else if(PushMessageData.NEW_JOIN_CUST_APPLY.equals(tag)){
				applyInfo = intent.getParcelableExtra(PushMessageData.NEW_JOIN_CUST_APPLY);
				listPushMessage.add(applyInfo);
			}else if(PushMessageData.NEW_CREATE_CUST_APPLY.equals(tag)){
				custRegisterInfo = intent.getParcelableExtra(PushMessageData.NEW_CREATE_CUST_APPLY);
				listPushMessage.add(custRegisterInfo);
			}else if(PushMessageData.NEW_CUST_JOIN_INVITE.equals(tag)){
				custJoinInviteInfo = intent.getParcelableExtra(PushMessageData.NEW_CUST_JOIN_INVITE);
//				if(AccountManager.getCurrent(MainApplication.getAppContext()).getId()==custJoinInviteInfo.getInviteUserId()){
//					MainApplication.clearContacts();
//					ContactManager.getContactManager(getApplicationContext()).notifyContactChanged();
//				}
				listPushMessage.add(custJoinInviteInfo);
			}else if(PushMessageData.MESSAGE.equals(tag)){
				messageInfo = intent.getParcelableExtra(PushMessageData.MESSAGE);
				listPushMessage.add(messageInfo);
			}else if(PushMessageData.NEW_SHARE_LINK.equals(tag)){
				shareLinkInfo = intent.getParcelableExtra(PushMessageData.NEW_SHARE_LINK);
				listPushMessage.add(shareLinkInfo);
			}else if(PushMessageData.NEW_INVITE_USER.equals(tag)){
				inviteUserInfo = intent.getParcelableExtra(PushMessageData.NEW_INVITE_USER);
				listPushMessage.add(inviteUserInfo);
			}
		}
		if (taskInfo == null && noticeInfo == null && applyInfo==null 
				&&custRegisterInfo == null&&custJoinInviteInfo == null && messageInfo == null 
				&& shareLinkInfo==null && inviteUserInfo==null) {
			try {
				TableMsgWidget.this.stopSelf();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			connectFloatWidgetService();
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	public  ArrayList<PushMessageData> getPushMessageDatas(){
		return listPushMessage;
	}
	
	private void connectFloatWidgetService() {
		if (mFloatWidgetConnection == null || mFloatWidgeService == null) {
			mFloatWidgetConnection = new ServiceConnection() {
				
				@Override
				public void onServiceDisconnected(ComponentName name) {
					mFloatWidgeService = null;
					mAssistiveTouchWidget = 0;
				}
				
				@Override
				public void onServiceConnected(ComponentName name, IBinder service) {
					mFloatWidgeService = IFloatWidgetService.Stub.asInterface(service);
					setupAssistiveTouchWidget(mFloatWidgeService);
				}
			};
			
			Intent i = new Intent();
			i.setClass(this, FloatWidgetService.class);
			bindService(i, mFloatWidgetConnection, Context.BIND_AUTO_CREATE);
		} else {
			setupAssistiveTouchWidget(mFloatWidgeService);
		}
	}
	
	private void disconnectFloatWidgetService() {
		if (mAssistiveTouchWidget != 0 && mFloatWidgeService != null) {
			try {
				mFloatWidgeService.deleteWidget(mAssistiveTouchWidget);
			} catch (RemoteException e) {
			}
		}
		if (mFloatWidgetConnection != null) {
			unbindService(mFloatWidgetConnection);
			mFloatWidgetConnection = null;
		}
	}
	
	private void deleteMsgWidget() {
		if (mAssistiveTouchWidget != 0 && mFloatWidgeService != null) {
			try {
				mFloatWidgeService.deleteWidget(mAssistiveTouchWidget);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void setupAssistiveTouchWidget(IFloatWidgetService floatWidgetService) {
		try {
			if (mAssistiveTouchWidget == 0) {
				mAssistiveTouchWidget = floatWidgetService.allocWidgetId();
			}
			WindowManager.LayoutParams params = FloatWidgetService
					.getDefaultLayoutParams();
			params.gravity = Gravity.LEFT | Gravity.CENTER;
			params.width = WindowManager.LayoutParams.WRAP_CONTENT;
			params.height = WindowManager.LayoutParams.WRAP_CONTENT;
			params.alpha=0.5f;
			params.setTitle("New Task");
			
			RemoteViews rv = new RemoteViews(this.getPackageName(),
					R.layout.table_message_widget);
			rv.setImageViewResource(R.id.table_widget_img,
					R.drawable.table_message_widget);
			
			Intent intent = new Intent(this, MessageWigetActivity.class);
			intent.putParcelableArrayListExtra(PushMessageData.PUSH_MSG_TAG, listPushMessage);
			if (PushMessageData.NEW_TASK.equals(tag)) {
				intent.putExtra("page", 1);
			} else if (PushMessageData.NEW_NOTICE.equals(tag)) {
				intent.putExtra("page", 0);
			}
			PendingIntent pi = PendingIntent.getActivity(this, 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.table_widget_img, pi);

			floatWidgetService.updateViewsParams(mAssistiveTouchWidget, params, rv);
//			floatWidgetService.updateViewsAndVisibilityByLayout(widgetId,
//					R.layout.table_message_widget, false, params);
			floatWidgetService.setWidgetMoveable(mAssistiveTouchWidget, true);
			floatWidgetService.setMagnetic(mAssistiveTouchWidget, true);
			floatWidgetService.setPositionPreferenceKey(mAssistiveTouchWidget, "table_msg_widget");
			updateAssistiveTouchVisibilityByPreference();
		} catch (RemoteException e) {
		}
	}
	
	private void updateAssistiveTouchVisibilityByPreference() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO:判断是否需要显示
				boolean show = true;
				showAssistiveTouch(show);
			}
		}).start();
	}

	private void showAssistiveTouch(boolean show) {
		if (mFloatWidgeService != null && mAssistiveTouchWidget != 0) {
			try {
				mFloatWidgeService.showWidget(mAssistiveTouchWidget, show);
			} catch (RemoteException e) {
			}
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		disconnectFloatWidgetService();
		msgWidget = null;
		super.onDestroy();
	}
}
