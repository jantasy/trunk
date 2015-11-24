package com.chinatelecom.nfc.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import com.chinatelecom.nfc.R;
import com.chinatelecom.nfc.AsyncTask.BluetoothReceiveAsyncTask;
import com.chinatelecom.nfc.AsyncTask.WifiReceiveAsyncTask;
import com.chinatelecom.nfc.Const.Const;
import com.chinatelecom.nfc.Utils.Constant;
import com.chinatelecom.nfc.Utils.MyUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class BluetoothReceiverService extends Service  {
	String receive2direction;
	NotificationManager notiManager;
	Notification noti = new Notification(); 
	PendingIntent updatePendingIntent = null;
	int filesSize;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	final Handler handler = new Handler(){
    @Override   
    public void handleMessage(Message msg) {
    	MyUtil.stoast(BluetoothReceiverService.this, msg.obj.toString());
    }
};
	
    @Override 
    public void onStart(Intent intent, int startId) {
//    	BluetoothReceiveAsyncTask bAT = new BluetoothReceiveAsyncTask(this, intent);
//    	bAT.execute();
    	
    	
    	final String bModuleAddress;
    	if (intent == null) return;
    	notiManager = (NotificationManager)getSystemService("notification");
    	noti = new Notification(R.drawable.nfc_arrow, "Service started", System.currentTimeMillis());
    	receive2direction = intent.getStringExtra("receive2direction");
//    	final String host = intent.getStringExtra("wModuleIp");
    	filesSize = intent.getIntExtra("filesSize", 0);
    	bModuleAddress = intent.getStringExtra("bModuleAddress");
    	
    	new Thread(new Runnable() {
    		@Override
    		synchronized public void run() {
    			BluetoothSocket client = null;
    			try{
    				final String SPP_UUID = Const.BlueUUID;    
    				UUID uuid = UUID.fromString(SPP_UUID);
    				for(int i = 0 ; i < filesSize ; i++){
    					BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    					BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(bModuleAddress);
    					client = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
    					client.connect();
    					
    					final File f = new File(Constant.NFCFORCHINATELECOM_DIR + "/myshares/wifip2pshared-" + System.currentTimeMillis() + ".tmp");
    					File dirs = new File(f.getParent());
    					if (!dirs.exists()) dirs.mkdirs();
    					f.createNewFile();
    					InputStream inputstream = client.getInputStream();
    					String resultFilename;
    					resultFilename = copyFile(inputstream, new FileOutputStream(f)).trim();
    					resultFilename = resultFilename.contains(".") ? resultFilename : (resultFilename + ".jpg");
    					f.renameTo(new File(f.getParentFile().getPath() + "/" + resultFilename));
    					
    					inputstream.close();
    					client.close();	    			
    					mes(receive2direction);
    					Thread.sleep(100);
    				}
    				ResultBrowser();
    			}
    			catch(Exception e){
    				String s = e.getMessage();
    				mes("接收失败，请重新选择文件");
    				notiManager.cancel(0);
    			}
    			finally{
    				if (client != null)
    					try {
    						client.close();
    					} catch (IOException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
    				Thread.currentThread().interrupt();
    			}
    		}
    	}).start();
    }
    
    private void ResultBrowser() {
//		Intent intent = new Intent();
//		intent.setClass(getApplicationContext(), FilesResult.class);
//		startActivity(intent);
		Intent intent = new Intent(Const.transmitFinished); 
		sendBroadcast(intent);   
    }

    @Override
    public void onCreate() {
    	
    }
    
    
    public String copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[4096];
        byte filenamebuf[] = new byte[101];
        String filename = "Iamafilename";
        double receivedSize = 0;
		double receivedRate = 0;
		Iterator<Double> RateIterator = getRateIterator();
		double StepRate = 0;
		double fileSize = 0;
		Boolean b = false;
		
        int len;
        try {
        	if (inputStream.read(filenamebuf) != -1)
        	{
        		String[] tempArr = new String(filenamebuf).trim().split("@@@");
        		filename = tempArr[0];
        		fileSize = Integer.valueOf(tempArr[1]);
        	}
        	noti.tickerText = "开始接收";
        	noti.setLatestEventInfo(this, "翼卡通传输", "文件已传输" + "0" + "%", updatePendingIntent);
        	notiManager.notify(0, noti);
        	if (RateIterator.hasNext())
        		StepRate =(double)RateIterator.next();
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
                receivedSize += len;
            	receivedRate = receivedSize / fileSize;

				b = (receivedRate >= StepRate && receivedRate <= 1.0);
				if (b){
					noti.setLatestEventInfo(this, "翼卡通传输", "文件已传输" + StepRate * 100 + "%", updatePendingIntent);
					notiManager.notify(0, noti);
					if (RateIterator.hasNext())
						StepRate =(double)RateIterator.next(); 
				}
				if (receivedSize == fileSize) break;
            }
        } catch (IOException e) {
//            Log.d(WiFiDirectActivity.TAG, e.toString());
        	if (!(e.getMessage().equals("Software caused connection abort")))
        		return null;
        }
        catch (Exception e) {
//        	Log.d(WiFiDirectActivity.TAG, e.toString());
        	return null;
        }
        finally{
        	noti.tickerText = "接收成功";
        	notiManager.notify(0, noti);
        	if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }
        return filename;
    }

    private Iterator<Double> getRateIterator()
    {
    	ArrayList<Double> AL = new ArrayList<Double>();
    	AL.add(0.1);
    	AL.add(0.2);
    	AL.add(0.3);
    	AL.add(0.4);
    	AL.add(0.5);
    	AL.add(0.6);
    	AL.add(0.7);
    	AL.add(0.8);
    	AL.add(0.9);
    	AL.add(1.0);
    	return AL.iterator();
    }
    
    private void mes(String s){
    	Message msg = handler.obtainMessage();
    	msg.obj = s;
    	handler.sendMessage(msg);
    }
}
