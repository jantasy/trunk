package cn.yjt.oa.app.nfctools.executehandler;

import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.nfctools.NfcTagOperationExecuteHandler;
import cn.yjt.oa.app.signin.SigninActivity;


public class NfcTagSigninOperationExecuteHandler extends NfcTagOperationExecuteHandler{
	public static final String SigninOperationSuccessACtion="cn.yjt.oa.app.nfctools.operation.signin.success";
	public static final String SigninOperationFailACtion="cn.yjt.oa.app.nfctools.operation.signin.fail";
	public static final String SigninOperationNotloginACtion="cn.yjt.oa.app.nfctools.operation.signin.notlogin";
	private ExcuteHandleReceiver mReceiver;

	
	
	@Override
	public void handle(Context context, String tagName) {
//		List<String> receiverActions=new ArrayList<String>();
//		receiverActions.add(SigninOperationSuccessACtion);
//		receiverActions.add(SigninOperationFailACtion);
//		receiverActions.add(SigninOperationNotloginACtion);
//		registerReceiver(context, receiverActions,tagName);
		String format="已执行标签：%s";
		sendNotification(context, format, tagName);
		if(context instanceof Activity){
			((Activity) context).finish();
		}
	}
	
	private void registerReceiver(Context  context,List<String> receiverActions,String tagName) {
		IntentFilter filter = new IntentFilter();
		for (String action : receiverActions) {
			filter.addAction(action);
		}
		mReceiver=new ExcuteHandleReceiver();
		((Activity) context).registerReceiver(mReceiver, filter);
	}
	
	
	private void sendNotification(Context context,String format, String tagName) {
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher,
				String.format(format, tagName), System.currentTimeMillis());
		
		Intent intent = new Intent(context, SigninActivity.class); 
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		PendingIntent contentIntent=PendingIntent.getActivity(context, 0, intent, 0);
		notification.setLatestEventInfo(context, "\"翼机通+\"NFC标签", String.format(format, tagName), contentIntent);
		manager.notify(new Random(10000).nextInt(), notification);
		
	}
	
	private void createSuccessDialog(final Context context) {
		final Dialog dialog = new Dialog(context, R.style.dialogNoTitle);
		View contentView = View.inflate(context,
				R.layout.signin_success_dialog, null);
		contentView.findViewById(R.id.btn_comfirm).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						((Activity) context).unregisterReceiver(mReceiver);
						((Activity) context).finish();
					}
				});
		dialog.setContentView(contentView);
		dialog.setCanceledOnTouchOutside(false);

		if (dialog != null && !dialog.isShowing()) {
			dialog.show();
		}

	}
	
	private void createErrorDialog(final Context context) {
		final Dialog errorDialog = new Dialog(context, R.style.dialogNoTitle);
		View dialogView = View.inflate(context, R.layout.signin_error_dialog,
				null);
		dialogView.findViewById(R.id.signin_cancle).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						errorDialog.dismiss();
						((Activity) context).unregisterReceiver(mReceiver);
						((Activity) context).finish();
					}
				});
		errorDialog.setContentView(dialogView);
		errorDialog.setCanceledOnTouchOutside(false);
		errorDialog.show();

	}
	
	
	public class ExcuteHandleReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (SigninOperationSuccessACtion.equals(action)) {
				String format="已执行标签：%s";
				if(!TextUtils.isEmpty(getNfcTagOperation().getNotificationFormat())){
					format=getNfcTagOperation().getNotificationFormat();
				}
				sendNotification(context, format, getNfcTagOperation().getTagName());
				createSuccessDialog(context);
			}else if(SigninOperationFailACtion.equals(action)){
				createErrorDialog(context);
			}else if(SigninOperationNotloginACtion.equals(action)){
				Toast.makeText(context, "您未登录，请先登录！", Toast.LENGTH_LONG).show();
				((Activity) context).unregisterReceiver(mReceiver);
				((Activity) context).finish();
			}
		}


	}
	

	
	
	
	
	


}
