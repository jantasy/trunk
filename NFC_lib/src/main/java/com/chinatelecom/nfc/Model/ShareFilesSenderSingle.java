package com.chinatelecom.nfc.Model;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.SimpleAdapter;

import com.chinatelecom.nfc.R;
import com.chinatelecom.nfc.Abstractor.WifiSender;
import com.chinatelecom.nfc.Service.BluetoothSenderService;
import com.chinatelecom.nfc.Service.WifiSenderService;

public class ShareFilesSenderSingle extends WifiSender {
	public ContentValues contentvalues = new ContentValues();
	public Intent intent;
	public String fileUri;

	
	public ShareFilesSenderSingle(Intent intent)
	{
		this.intent = intent;
		Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
	    this.fileUri = uri.toString();
	}
	
	//@Override
	public void send(Context context, Activity activity, CommunicationModule mModule) {
		if (mModule instanceof BluetoothModule) {
			sendBybluetooth(context, ((BluetoothModule)mModule));
		}
		else if (mModule instanceof WifiModule) {
			sendBywifi(context, activity, ((WifiModule)mModule));
		}
	}
	
	private void sendBybluetooth(Context context, BluetoothModule bModule) {
		// TODO Auto-generated method stub
//	    contentvalues.put("uri", this.fileUri);
//	    contentvalues.put("destination", bModule.Address);
//	    contentvalues.put("direction", 0);            
//        Long ts = System.currentTimeMillis();     
//        contentvalues.put("timestamp", ts);
//        context.getContentResolver().insert(Uri.parse("content://com.android.bluetooth.opp/btopp"), contentvalues);
    	
		Intent intent = new Intent(context, BluetoothSenderService.class);
    	intent.putExtra("fileUri", new String[]{fileUri});
    	intent.putExtra("bModuleAddress", bModule.Address);
    	intent.putExtra("transmiting", context.getString(R.string.nfc_transmiting));
    	intent.putExtra("transmitending", context.getString(R.string.nfc_transmitending));
    	intent.putExtra("fileisnotfound", context.getString(R.string.nfc_fileisnotfound));    	
    	intent.putExtra("failed2connect", context.getString(R.string.nfc_failed2connect));
    	intent.putExtra("failed2transmit", context.getString(R.string.nfc_failed2transmit));
    	intent.putExtra("waitforsending", context.getString(R.string.nfc_waitforsending));
    	intent.putExtra("sending", context.getString(R.string.nfc_sending));
    	intent.setType("*/*");
    	context.startService(intent);
	}
	
	
	
	private void sendBywifi(final Context context, Activity activity, WifiModule wModule) {
		// TODO Auto-generated method stub
//		WifiReceiver wifiOper = new WifiReceiver(context, activity, wModule);
//		wifiOper.discover();
		
//		String wModuleIp = wModule.Ip;
    	Intent intent = new Intent(context, WifiSenderService.class);
//    	intent.putExtra("wModuleIp", wModuleIp);
    	intent.putExtra("fileUri", new String[]{fileUri});
    	intent.putExtra("transmiting", context.getString(R.string.nfc_transmiting));
    	intent.putExtra("transmitending", context.getString(R.string.nfc_transmitending));
    	intent.putExtra("fileisnotfound", context.getString(R.string.nfc_fileisnotfound));    	
    	intent.putExtra("failed2connect", context.getString(R.string.nfc_failed2connect));
    	intent.putExtra("failed2transmit", context.getString(R.string.nfc_failed2transmit));
    	intent.putExtra("waitforsending", context.getString(R.string.nfc_waitforsending));
    	intent.putExtra("sending", context.getString(R.string.nfc_sending));
    	intent.setType("*/*");
    	context.startService(intent);
	}
	
	public SimpleAdapter getSimpleAdapter(Context context)
	{
    	ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
    	String filesname = this.getFilesName();

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("FilesName", filesname);
        listItem.add(map);

        SimpleAdapter listItemAdapter = new SimpleAdapter(context, listItem,//数据源 
                R.layout.nfc_sharefiles_list_items,//ListItem的XML实现
                //动态数组与ImageItem对应的子项        
                new String[] {"FilesName"}, 
                //ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[] {R.id.FilesName_textview}
        );
        return listItemAdapter;
	}
	
	public String getFilesName()
	{
		String names = fileUri.substring(fileUri.lastIndexOf("/")+1, fileUri.length());
		return names;
	}
}
