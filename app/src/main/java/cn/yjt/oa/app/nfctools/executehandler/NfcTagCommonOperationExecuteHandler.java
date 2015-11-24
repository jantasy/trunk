package cn.yjt.oa.app.nfctools.executehandler;

import java.util.Random;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.nfctools.NfcTagOperationExecuteHandler;
import cn.yjt.oa.app.utils.OperaEventUtils;

public class NfcTagCommonOperationExecuteHandler extends
		NfcTagOperationExecuteHandler {

	
	private void sendNotification(Context context,String format, String tagName) {
		/*记录操作 1406*/
		OperaEventUtils.recordOperation(OperaEvent.OPERA_READ_EXECUTE_NFCTAG);

		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher,
				String.format(format, tagName), System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(context.getApplicationContext(), "\"翼机通+\"NFC标签",
				String.format(format, tagName), null);
		manager.notify(new Random(10000).nextInt(), notification);
	}

	@Override
	public void handle(Context context, String tagName) {
		sendNotification(context, "已执行标签：%s", tagName);	
		if(context instanceof Activity){
			((Activity) context).finish();
		}
	}

}
