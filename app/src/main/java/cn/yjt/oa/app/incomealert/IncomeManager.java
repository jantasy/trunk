package cn.yjt.oa.app.incomealert;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.PixelFormat;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.MessageInfo;
import cn.yjt.oa.app.beans.TaskInfo;
import cn.yjt.oa.app.contactlist.db.GlobalContactManager;
import cn.yjt.oa.app.contactlist.db.GlobalContactManager.GlobalContact;
import cn.yjt.oa.app.push.PushMessageData;
import cn.yjt.oa.app.task.URLSpanNoUnderline;
import cn.yjt.oa.app.utils.AppSettings;
import cn.yjt.oa.app.widget.TableMsgWidget;

public class IncomeManager {
	
	private static final String TAG  = "IncomeManager";

	private static final int LIMIT_MESSAGE_COUNT = 3;

	static final WindowManager.LayoutParams DEFALUT_LAYOUTPARAM = new WindowManager.LayoutParams();
	static {
		DEFALUT_LAYOUTPARAM.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		DEFALUT_LAYOUTPARAM.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		DEFALUT_LAYOUTPARAM.format = PixelFormat.TRANSLUCENT;
		DEFALUT_LAYOUTPARAM.width = WindowManager.LayoutParams.MATCH_PARENT;
		DEFALUT_LAYOUTPARAM.height = WindowManager.LayoutParams.WRAP_CONTENT;
		DEFALUT_LAYOUTPARAM.gravity = Gravity.TOP;
//		DEFALUT_LAYOUTPARAM.y = MainApplication.getAppContext().getResources()
//				.getDimensionPixelSize(R.dimen.dimen_64dp);

	}

	private static IncomeManager incomeManager;

	private Context context;

	private WindowManager windowManager;

	private View alertView;

	private GlobalContactManager contactManager;

	private boolean isShowing;

	private IncomeManager(Context context) {
		this.context = context;
		windowManager = (WindowManager) context.getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		contactManager = new GlobalContactManager(context);
	}

	public static IncomeManager getInstance(Context context) {
		if (incomeManager == null) {
			incomeManager = new IncomeManager(context);
		}
		incomeManager.context = context;
		return incomeManager;
	}

	public void release() {
		context = null;
		windowManager = null;
		alertView = null;
		if (contactManager != null) {
			contactManager.release();
			contactManager = null;
		}
		incomeManager = null;
	}

	public void receive(String phone) {
		Log.d(TAG, "receive:"+phone);
		if (isShowing) {
			Log.d(TAG, "isShowing");
			return;
		}
		GlobalContact globalContact = contactManager
				.queryContactWithPhone(phone);
		if (globalContact != null) {
			View alertView = prepareTipView(globalContact);
			showTopWindow(alertView);
			this.alertView = alertView;
		}else{
			Log.d(TAG, "globalContact == null");
		}
	}

	public void close() {
		Log.d(TAG, "close");
		if (alertView != null) {
			windowManager.removeView(alertView);
			isShowing = false;
		}
		release();
	}

	private View prepareTipView(GlobalContact globalContact) {
		View alertView = buildAlertView(context);
		TextView name = (TextView) alertView.findViewById(R.id.income_name);
		TextView position = (TextView) alertView
				.findViewById(R.id.income_position);
		TextView department = (TextView) alertView
				.findViewById(R.id.income_department);
		TextView enterprise = (TextView) alertView
				.findViewById(R.id.income_enterprisename);
		TextView number = (TextView) alertView.findViewById(R.id.income_number);
		ImageView iconCircle = (ImageView) alertView.findViewById(R.id.income_icon_circle);
		final LinearLayout messagesLayout = (LinearLayout) alertView
				.findViewById(R.id.income_messages);
		TextView messagesRemind = (TextView) alertView
				.findViewById(R.id.income_message_remind);
		name.setText(globalContact.name);
		if(!TextUtils.isEmpty(globalContact.position)){
			position.setText(globalContact.position);
		}else{
			position.setText("未知职位");
		}
		department.setText(buildInfo(globalContact));
		enterprise.setText(globalContact.custName);
		number.setText(globalContact.phone);
		setupIconAnimation(iconCircle);

		List<TaskInfo> tasks = getTaskInfoWithUserId(globalContact.userId);
		if (tasks.isEmpty()) {
			messagesLayout.setVisibility(View.GONE);
		} else {
			setupMessageViews(messagesLayout, tasks);
			setupCountView(messagesLayout, messagesRemind, tasks);
		}

		return alertView;
	}

	private void setupIconAnimation(ImageView icon) {
		if (icon != null) {
			Animation animation = AnimationUtils.loadAnimation(context, R.anim.income_rotate);
			icon.startAnimation(animation);
		}
	}

	private void setupMessageViews(final LinearLayout messagesLayout,
			List<TaskInfo> tasks) {
		for (int i = 0; i < tasks.size(); i++) {
			messagesLayout.addView(buildMessageView(messagesLayout, tasks, i));
			if(i < tasks.size() - 1){
				View line = inflateLine();
				messagesLayout.addView(line);
				line.setVisibility(View.GONE);
			}
		}
	}

	private View inflateLine() {
		View view = LayoutInflater.from(context).inflate(R.layout.line, null);
		view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, context.getResources().getDimensionPixelSize(R.dimen.dimen_0_4dp)));
		return view;
	}

	private void setupCountView(final LinearLayout messagesLayout,
			TextView messagesRemind, List<TaskInfo> tasks) {
		messagesRemind.setText(String.format("%d条未读消息", tasks.size()));
		messagesRemind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.setVisibility(View.GONE);
				int childCount = messagesLayout.getChildCount();
				if (childCount > 1) {
					for (int i = 1; i < childCount; i++) {
						messagesLayout.getChildAt(i)
								.setVisibility(View.VISIBLE);
					}
				}

			}
		});
	}

	private TextView buildMessageView(final LinearLayout messagesLayout,
			List<TaskInfo> tasks, int i) {
		TextView textView = (TextView) LayoutInflater.from(context).inflate(
				R.layout.view_income_message, null);
		Spannable processedText = parseHtml(tasks, i);
		textView.setText(processedText);
		textView.setVisibility(View.GONE);
		if (i == tasks.size() - 1) {
			setupLastMessageView(messagesLayout, textView);
		}
		return textView;
	}

	private Spannable parseHtml(List<TaskInfo> tasks, int i) {
		Spannable spannedText = Spannable.Factory.getInstance().newSpannable(
				Html.fromHtml(String.format("消息%d：%s", i + 1, tasks.get(i)
						.getContent())));
		Spannable processedText = URLSpanNoUnderline
				.removeUnderlines(spannedText);
		return processedText;
	}

	private void setupLastMessageView(final LinearLayout messagesLayout,
			TextView textView) {
		textView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
				R.drawable.ic_income_arrow_up, 0);
		textView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int childCount = messagesLayout.getChildCount();
				messagesLayout.getChildAt(0).setVisibility(View.VISIBLE);
				if (childCount > 1) {
					for (int i = 1; i < childCount; i++) {
						messagesLayout.getChildAt(i).setVisibility(View.GONE);
					}
				}
			}
		});
	}

	private List<TaskInfo> getTaskInfoWithUserId(long userId) {
		List<TaskInfo> infos = new ArrayList<TaskInfo>();
		List<TaskInfo> pushTaskInfos = getTaskInfosInPushMessagesWithUserId(userId);
		int needMessageCount = LIMIT_MESSAGE_COUNT;
		if (pushTaskInfos != null) {
			needMessageCount -= pushTaskInfos.size();
			infos.addAll(pushTaskInfos);
		}
		List<TaskInfo> messageTaskInfos = getTaskInfosInMessageCenterWithUserId(
				userId, needMessageCount);
		if (messageTaskInfos != null) {
			infos.addAll(messageTaskInfos);
		}

		return infos;
	}

	private List<TaskInfo> getTaskInfosInPushMessagesWithUserId(long userId) {
		TableMsgWidget tableMsgWidget = TableMsgWidget.getTableMsgWidget();
		if (tableMsgWidget != null) {
			ArrayList<PushMessageData> pushMessageDatas = tableMsgWidget
					.getPushMessageDatas();
			ArrayList<TaskInfo> tasks = filtTaskWithUserId(pushMessageDatas,
					userId);
			return tasks;
		}
		return null;
	}

	private List<TaskInfo> getTaskInfosInMessageCenterWithUserId(long userId,
			int limit) {
		MainApplication application = (MainApplication) MainApplication
				.getApplication();
		List<MessageInfo> taskMessages = filtTaskWithUserId(application
				.getMessageCenterMessages());
		if (taskMessages.size() > limit) {
			taskMessages = taskMessages.subList(0, limit);
		}
		return parseTaskInfos(taskMessages);
	}

	private List<TaskInfo> parseTaskInfos(List<MessageInfo> taskMessages) {
		List<TaskInfo> infos = new ArrayList<TaskInfo>();
		for (MessageInfo messageInfo : taskMessages) {
			infos.add(TaskInfo.parseJson(messageInfo.getPayload()));
		}
		return infos;
	}

	private ArrayList<TaskInfo> filtTaskWithUserId(
			ArrayList<PushMessageData> pushMessageDatas, long userId) {
		ArrayList<TaskInfo> tasks = new ArrayList<TaskInfo>();
		for (PushMessageData pushMessageData : pushMessageDatas) {
			if (pushMessageData instanceof TaskInfo) {
				TaskInfo taskInfo = (TaskInfo) pushMessageData;
				if (taskInfo.getFromUser().getId() == userId) {
					tasks.add(taskInfo);
				}
			}
		}
		return tasks;
	}

	private List<MessageInfo> filtTaskWithUserId(List<MessageInfo> infos) {
		List<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
		for (MessageInfo messageInfo : infos) {
			if (messageInfo.getIsRead() == 0
					&& MessageInfo.TASK.equals(messageInfo.getType())) {
				messageInfos.add(messageInfo);
			}
		}

		return messageInfos;
	}

	private String buildInfo(GlobalContact globalContact) {

		StringBuilder builder = new StringBuilder();

		if (!TextUtils.isEmpty(globalContact.department)) {
//			if (globalContact.department.indexOf("/") > 0) {
//				builder.append(globalContact.department.split("/")[0]);
//			} else {
				builder.append(globalContact.department);
//			}
		}

		if(TextUtils.isEmpty(builder)){
			return "未知部门";
		}
		return builder.toString();

	}

	private View buildAlertView(final Context context) {
		View view;
		int incomeAlertMode = AppSettings.getIncomeAlertMode();
		if(incomeAlertMode == AppSettings.INCOME_MODE_SIMPLE){
			view = LayoutInflater.from(context).inflate(
					R.layout.view_income_alert_default, null);
		}else{
			view = LayoutInflater.from(context).inflate(
					R.layout.view_income_alert, null);
		}
		
		view.findViewById(R.id.income_close).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						System.out.println("onClick");
						try {
							windowManager.removeView(alertView);
						} catch (Exception e) {
							e.printStackTrace();
						}
						release();
					}
				});
		return view;
	}

	private void showTopWindow(View view) {
		Log.d(TAG, "showTopWindow");
		windowManager.addView(view, DEFALUT_LAYOUTPARAM);
		isShowing = true;
	}
}
