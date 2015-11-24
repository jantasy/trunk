package com.chinatelecom.nfc.Service;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.UUID;

import com.chinatelecom.nfc.AsyncTask.BluetoothSendAsyncTask;
import com.chinatelecom.nfc.AsyncTask.WifiSendAsyncTask;
import com.chinatelecom.nfc.Const.Const;
import com.chinatelecom.nfc.Utils.MyUtil;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class BluetoothSenderService extends Service {
    private String[] fileUri;
//    private Activity activity;
//    private Context context;
    
	String transmiting;
	String transmitending;
	String fileisnotfound;
	String failed2connect;
	String failed2transmit;
	String waitforsending;
	String sending;
	public static Boolean beSending = false;

	long lastRequestTime = 0;
    

    public BluetoothSenderService() {
    }
	
//    public sendService(Activity activity, Context context, String[] fileUri) {
//        this.activity = activity;
//        this.context = context;
//        this.fileUri = fileUri;
//    }
    
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	final Handler handler = new Handler(){
        @Override   
        public void handleMessage(Message msg) {
        	MyUtil.stoast(BluetoothSenderService.this, msg.obj.toString());
        }
	};
	
	
	@Override
	public void onStart(Intent intent, int startId) {
//    	BluetoothSendAsyncTask bAT = new BluetoothSendAsyncTask(this, intent);
//    	bAT.execute();
		
		if (intent == null) return;
		transmiting = intent.getStringExtra("transmiting");
		transmitending = intent.getStringExtra("transmitending");
		fileisnotfound = intent.getStringExtra("fileisnotfound");
		failed2connect = intent.getStringExtra("failed2connect");
		failed2transmit = intent.getStringExtra("failed2transmit");
		waitforsending = intent.getStringExtra("waitforsending");
		sending = intent.getStringExtra("sending");
		fileUri = intent.getStringArrayExtra("fileUri");
		
//		out:
			if (beSending)
			{
				if (lastRequestTime == 0)
					lastRequestTime = System.currentTimeMillis();   
				else{
					long currentRequestTime = System.currentTimeMillis();
					if (currentRequestTime - lastRequestTime < Const.intervalSendTime) {
						lastRequestTime = 0;
						beSending = false;
						MyUtil.ltoast(this, waitforsending);
						return;
//						break out;
					}
					else
						MyUtil.ltoast(this, "对不起，上次传输被中断");
				}
			}
		
		new Thread(new Runnable() {
			@Override
			synchronized public void run() {
				int len;
				BluetoothServerSocket serverSocket = null;
				BluetoothSocket client = null;
				OutputStream outputStream = null;
				InputStream inputStream = null;
				byte buf[] = new byte[4096];
				String SPP_UUID = Const.BlueUUID;    
				UUID uuid = UUID.fromString(SPP_UUID);
				BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				if (!beSending)
				{
					beSending = true;
				}
				while(serverSocket == null){
					try {
						/**
						 * Create a server socket and wait for client connections. This
						 * call blocks until a connection is accepted from a client
						 */
						serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(bluetoothAdapter.getName(), uuid); 
						for(int i=0;i<fileUri.length;i++)
						{
							client = serverSocket.accept();
							mes(sending);
							outputStream = client.getOutputStream();
							ContentResolver cr = getContentResolver();
							inputStream = cr.openInputStream(Uri.parse(fileUri[i]));
							
							String filenameOnly = fileUri[i];
							filenameOnly = filenameOnly.substring(filenameOnly.lastIndexOf("/") + 1);
							
							StringBuilder sb = new StringBuilder();
							sb.append(filenameOnly + "@@@" + inputStream.available());
							byte[] bytes = MyUtil.Pad2Solid100Size(sb).getBytes();
							ByteArrayInputStream filenameInputStream = new ByteArrayInputStream(bytes);
							inputStream = new SequenceInputStream(filenameInputStream, inputStream);
							
							while ((len = inputStream.read(buf)) != -1) {
								outputStream.write(buf, 0, len);
							}
							mes(transmitending);
							Thread.sleep(100);
						}
					} catch (FileNotFoundException e) {
						// catch logic
						mes(fileisnotfound);
					}
					catch (IOException e) {
						// catch logic
						mes(failed2connect);
					}
					catch (Exception e) {
						// catch logic
						mes(failed2transmit);
					}
					
					/**
					 * Clean up any open sockets when done transferring or if an
					 * exception occurred.
					 */
					finally {
						beSending = false;
						if (inputStream != null)
							try {
								inputStream.close();
							} catch (Exception e) {
								// TODO: handle exception
							}
						if (outputStream != null)
							try {
								outputStream.close();
							} catch (Exception e) {
								// TODO: handle exception
							}
						if (client != null) {
							{
								try {
									client.close();
								} catch (IOException e) {
									// catch logic
								}
							}
						}
						if (serverSocket != null) {
							{
								try {
									serverSocket.close();
									serverSocket = null;
								} catch (IOException e) {
									// catch logic
								}
							}
						}
//					Thread.currentThread().interrupt();
					}
				}
			}
		}).start();
	}
	
    
    private void mes(String s){
    	Message msg = handler.obtainMessage();
    	msg.obj = s;
    	handler.sendMessage(msg);
    }
    

}
