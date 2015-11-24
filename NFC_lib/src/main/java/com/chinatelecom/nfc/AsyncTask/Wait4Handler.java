package com.chinatelecom.nfc.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.chinatelecom.nfc.AsyncTask.Wait4Handler.innerObject;

public class Wait4Handler extends AsyncTask<String, Integer, String> {
	ProgressDialog progressDialog;
	Context context;
	String pTitle,pMessage;
	Runnable mainRunnable;
	Runnable lastRunnable;
	class innerObject
	{
		Context context;
	}
	innerObject iClass = new innerObject();

	
	public Wait4Handler(Context context, String pTitle, String pMessage, Runnable mainRunnable, Runnable lastRunnable)
	{
		iClass.context = context;
		this.pTitle = pTitle;
		this.pMessage = pMessage;
    	this.mainRunnable = mainRunnable;
    	this.lastRunnable = lastRunnable;
	}
	
    @Override  
    protected void onPreExecute() {  
    	progressDialog = ProgressDialog.show(iClass.context, pTitle, pMessage, true, false);
    }  
    
    @Override  
    protected String doInBackground(String... params) {  
    	mainRunnable.run();
    	return "";
    }

    @Override  
    protected void onProgressUpdate(Integer... values) {  

    }    
    
    @Override  
    protected void onPostExecute(String result) {  
    	progressDialog.dismiss();
    	lastRunnable.run();
    }   
}