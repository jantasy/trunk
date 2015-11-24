package com.telecompp.handler;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Looper;
import android.os.Process;

import com.telecompp.activity.ConfirmActivity;
import com.telecompp.util.LoggerHelper;

/**
 * 异常处理
 * @author 王端晴
 *
 */
public class MyExceptionHanlder implements UncaughtExceptionHandler {

	private Context mContext = null;
	
	private LoggerHelper logger = new LoggerHelper(ConfirmActivity.class);

	public MyExceptionHanlder(Context context) {
		this.mContext = context;
		// this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
//		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang
	 * .Thread, java.lang.Throwable)
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (ex != null) {
			/*Log.i("UncaughtException", ex.getMessage());
			StringBuilder sb = new StringBuilder();
			String temp = ex.getMessage();
			if (temp != null) {
				sb.append(temp);
			}
			sb.append("\r\n");
			sb.append(thread.getName());
			sb.append(" Trace: \r\n");
			StackTraceElement[] elements = ex.getStackTrace();
			if (elements != null) {
				for (StackTraceElement element : elements) {
					temp = element.toString();
					if (temp != null) {
						sb.append(temp);
					}
					sb.append("\r\n");
				}
			}

			// if the exception was thrown in a background thread inside
			// AsyncTask, then the actual exception can be found with getCause
			sb.append("Cause: ");
			Throwable theCause = ex.getCause();
			if (theCause != null) {
				temp = theCause.toString();
				if (temp != null) {
					sb.append(temp);
				}
			}
			sb.append("\r\nCause Stack:\r\n");
			theCause = ex.getCause();
			if (theCause != null) {
				elements = theCause.getStackTrace();
				if (elements != null) {
					for (StackTraceElement element : elements) {
						temp = element.toString();
						if (temp != null) {
							sb.append(temp);
						}
						sb.append("\r\n");
					}
				}
			}*/
			if(ex != null) {				
				logger.printLogOnSDCard(ex.getStackTrace().toString());
			}
			System.out.println("==wb=========");
			new Thread() {
	            @Override
	            public void run() {
	                Looper.prepare();
	                new AlertDialog.Builder(mContext).setTitle("提示").setCancelable(false)
	                        .setMessage("系统异常, 如果您正在做支付充值交易, 系统会自动冲正, 款项会自动退回账户, 请耐心等待退款通知短信").setNeutralButton("确定", new OnClickListener() {
	                            @Override
	                            public void onClick(DialogInterface dialog, int which) {
//	                            	android.os.Process.killProcess(android.os.Process.myPid());   
	                            	crash(mContext);
//	                                System.exit(0);
	                            }
	                        })
	                        .create().show();
	                Looper.loop();
	            }
	        }.start();
	                
	        System.out.println("==wb11111111111111111111111111");
			
			
			// you can write this content to file if necessary

//			Intent intent = new Intent(mContext, ReportPage.class);
//			intent.putExtra(ReportPage.REPORT_CONTENT,
//					sb.toString());
//			mContext.startActivity(intent);
//			crash(mContext);

		}
	}

	public void uncaughtException(Exception e) {
		uncaughtException(Thread.currentThread(), e);
	}

	private boolean crash(Context context) {
		if (context == null) {
			return false;
		}

		if (context instanceof Activity) {
			((Activity) context).finish();
		}

		Process.killProcess(Process.myPid());
		return true;
	}

}
