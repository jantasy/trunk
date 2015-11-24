package com.chinatelecom.nfc.AsyncTask;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import com.chinatelecom.nfc.R;
import com.chinatelecom.nfc.Const.Const;
import com.chinatelecom.nfc.Utils.Constant;
import com.chinatelecom.nfc.Utils.MyUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;


public class WifiSendAsyncTask extends AsyncTask<String, String, String> {
    private String[] fileUri;
	String transmiting;
	String transmitending;
	String fileisnotfound;
	String failed2connect;
	String failed2transmit;
	String waitforsending;
	String sending;
	int fileSize;
	public static Boolean beSending = false;
	long lastRequestTime = 0;
	Context context;
	Intent intent;
	
	
	public WifiSendAsyncTask(Context context, Intent intent)
	{
		this.context = context;
		this.intent = intent;
	}
	
    @Override  
    protected void onPreExecute() {  
    }  
    
    @Override  
    protected String doInBackground(String... params) {
    	if (intent == null) return "";
    	transmiting = intent.getStringExtra("transmiting");
    	transmitending = intent.getStringExtra("transmitending");
    	fileisnotfound = intent.getStringExtra("fileisnotfound");
    	failed2connect = intent.getStringExtra("failed2connect");
    	failed2transmit = intent.getStringExtra("failed2transmit");
    	waitforsending = intent.getStringExtra("waitforsending");
    	sending = intent.getStringExtra("sending");
    	fileUri = intent.getStringArrayExtra("fileUri");
    	
    	out:
    	if (beSending)
    	{
    		if (lastRequestTime == 0)
    			lastRequestTime = System.currentTimeMillis();   
    		else{
    			long currentRequestTime = System.currentTimeMillis();
    			if (currentRequestTime - lastRequestTime > Const.intervalSendTime) {
    				lastRequestTime = 0;
    				beSending = false;
    				break out;
    			}
    		}
    		MyUtil.ltoast(context, waitforsending);
    		return "";
    	}
    	
		int port = Const.listenSharedPort;
		int len;
		ServerSocket serverSocket = null;
		OutputStream outputStream = null;
		InputStream inputStream = null;
		byte buf[] = new byte[4096];
		try {
			serverSocket = new ServerSocket(port);
			/**
			 * Create a server socket and wait for client connections. This
			 * call blocks until a connection is accepted from a client
			 */
			for(int i=0;i<fileUri.length;i++)
			{
				double transmitedSize = 0;
//				double transmitedRate = 0;
//				Iterator<Double> RateIterator = Const.RateIterator;
//				Double StepRate = (double) 0;
				
				
				Socket client = serverSocket.accept(); 
		    	if (!beSending)
				{
		    		beSending = true;
					publishProgress(sending); 
				}

				//socket.connect((new InetSocketAddress(host, port)), Const.SeekP2PTimeout);
				outputStream = client.getOutputStream();
				ContentResolver cr = context.getContentResolver();
				inputStream = cr.openInputStream(Uri.parse(fileUri[i]));

				String filenameOnly = fileUri[i];
				filenameOnly = filenameOnly.substring(filenameOnly.lastIndexOf("/") + 1);
				
				StringBuilder sb = new StringBuilder();
				sb.append(filenameOnly + "@@@" + inputStream.available());
				byte[] bytes = MyUtil.Pad2Solid100Size(sb).getBytes();
				ByteArrayInputStream filenameInputStream = new ByteArrayInputStream(bytes);
				inputStream = new SequenceInputStream(filenameInputStream, inputStream);
				double fileSize = inputStream.available();
			
				while ((len = inputStream.read(buf)) != -1) {
					outputStream.write(buf, 0, len);
				}
				publishProgress(transmitending);
				Thread.sleep(100);
			}
		} catch (FileNotFoundException e) {
			// catch logic
			publishProgress(fileisnotfound);
		}
		catch (IOException e) {
			// catch logic
			publishProgress(failed2connect);
		}
		catch (Exception e) {
			// catch logic
			publishProgress(failed2transmit);
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
			if (serverSocket != null) {
				// if (socket.isConnected())
				{
					try {
						serverSocket.close();
					} catch (IOException e) {
						// catch logic
					}
				}
			}
		}
    	return "";
    }

    @Override  
    protected void onProgressUpdate(String... values) {  
    	MyUtil.ltoast(context, values[0]);
    }    
    
    @Override  
    protected void onPostExecute(String result) {  
    }
    
}