package cn.yjt.oa.app.signin.utils;

import io.luobo.common.Cancelable;
import io.luobo.common.http.FileClient;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.ProgressListener;
import io.luobo.common.json.TypeToken;

import java.io.File;
import java.lang.reflect.Type;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.BusinessConstants;
import cn.yjt.oa.app.http.FileClientFactory;

public class HttpHelper implements ProgressListener<Response<String>>{
	private static final Type TYPE_RESPONSE = new TypeToken<Response<String>>() {}.getType();
	private Context context;
	private UploadCallback uploadCallback;
	private ProgressDialog progressDialog;
	private Cancelable task;
	public HttpHelper(Context context){
		this.context = context;
		progressDialog = new ProgressDialog(context);
		hander = new Handler(Looper.getMainLooper());
	}
	
	private String getImageLoadUrl(){
		return BusinessConstants.buildUrl(AsyncRequest.MODULE_UPLOAD_IMAGE);
	}
	
	// 文件上传
	public void uploadImage(String uploadName,File uploadFile,UploadCallback uploadCallback){
		this.uploadCallback = uploadCallback;
		FileClient client = FileClientFactory.createSingleThreadFileClient(context);
		AsyncRequest.upload(context, Uri.fromFile(uploadFile), getImageLoadUrl(), uploadName, 720, 1280, true, this, client);
//		try {
//			task = client.upload(getImageLoadUrl(), uploadName, uploadFile, TYPE_RESPONSE,this);
//		} catch (InvocationError e) {
//			e.printStackTrace();
//		}
	}
	
	private long lastUpdateTime = 0;
	private static final long UPDATE_GAP = 1000;
	private Handler hander;
	
	@Override
	public void onProgress(long progress, long total) {
		long currentTime = System.currentTimeMillis();
		if(lastUpdateTime == 0 || currentTime - lastUpdateTime > UPDATE_GAP || progress == total ){
			lastUpdateTime = currentTime;
		}
	}
	
	@Override
	public void onStarted() {
		hander.post(new Runnable() {
			
			@Override
			public void run() {
				initProgressDialog();
			}
		});
	}
	
	private void updateProgress(final int progress){
	}
	
	private void initProgressDialog(){
		progressDialog.setMessage("文件上传中...");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				if(task != null){
					task.cancel();
				}
			}
		});
		progressDialog.show();
	}
	
	private void cancel(){
		hander.post(new Runnable() {
			
			@Override
			public void run() {
				if (context != null && !((Activity) context).isFinishing()) {
					progressDialog.dismiss();
				}
			}
		});
	}
	
	@Override
	public void onFinished(Response<String> response) {
		cancel();
		uploadCallback.onResponse(response.getPayload());
	}
	
	@Override
	public void onError(InvocationError error) {
		cancel();
	}
	
	public interface UploadCallback{
		void onResponse(String url);
	}
}
