package cn.yjt.oa.app.app;

import io.luobo.common.http.FileClient;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.http.ProgressListener;
import io.luobo.common.json.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import cn.yjt.oa.app.app.http.AppTaskController;
import cn.yjt.oa.app.app.http.State;
import cn.yjt.oa.app.beans.AppInfo;
import cn.yjt.oa.app.beans.ListSlice;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.http.FileClientFactory;

public class AppRequest {
	public static final int APPS_CARDAPP = 1;
	public static final int APPS_BUSINESS = 2;
	public static final int APPS_PERSONAL = 3;
	public static final int APPS_RECOMMEND = 4;
	public static final String ACTION_CARD_APP = "action_card_app";
	public static final String ACTION_APP_MARKET = "action_app_market";
	public static final String ACTION_APP_ADD = "action_app_add";
	public static final String ACTION_APP_DETAILS = "action_app_details";
	private static final Type TYPE_APPINFO = new TypeToken<Response<ListSlice<AppInfo>>>(){}.getType();
	private static final Type TYPE_APPINFO_SINGLE = new TypeToken<Response<AppInfo>>(){}.getType();
	private int module;
	public AppRequest(int module){
		this.module = module;
	}
	public AppRequest(){
	}
	public void getApps(int from, int max,final AppInfoCallback callback) {
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(initModule(module))
				.setResponseType(TYPE_APPINFO)
				.addPageQueryParameter(from, max)
				.setResponseListener(
						new Listener<Response<ListSlice<AppInfo>>>() {

							@Override
							public void onErrorResponse(InvocationError error) {
								// TODO 获取数据错误处理
								error.printStackTrace();
							}

							@Override
							public void onResponse(Response<ListSlice<AppInfo>> response) {
								if(response!=null&&response.getPayload()!=null){
									callback.onAppInfoResponse(response.getPayload().getContent());
								}
							}
						}).build().get();
	}
	public void getAppInfo(String pkg,final AppInfoSingleCallback callback){
		AsyncRequest.Builder builder = new AsyncRequest.Builder();
		builder.setModule(AsyncRequest.MODULE_APPS)
				.setModuleItem(pkg+"/")
				.setResponseType(TYPE_APPINFO_SINGLE)
				.setResponseListener(
						new Listener<Response<AppInfo>>() {

							@Override
							public void onErrorResponse(InvocationError error) {
								// TODO 获取数据错误处理
								error.printStackTrace();
							}

							@Override
							public void onResponse(Response<AppInfo> response) {
								if(response!=null&&response.getPayload()!=null){
									callback.onAppInfoSingleResponse(response.getPayload());
								}
							}
						}).build().get();
	}
	public interface AppInfoSingleCallback{
		public void onAppInfoSingleResponse(AppInfo response);
	}
	public interface AppInfoCallback{
		public void onAppInfoResponse(List<AppInfo> appInfos);
	}
	
	private static final String APP_DOWNLOAD_DIRECTORY = "/yijitong/app/";
	
	public AppTaskController getDownloadController(Context context,String url,AppProgressListener listener){
		FileClient fileClient = FileClientFactory.createSingleThreadFileClient(context);
		try {
			return new AppTaskController(fileClient.download(url, getDownloadDirectory("yjt"), listener),listener);
		} catch (InvocationError e) {
			e.printStackTrace();
			// TODO 下载错误处理
		}
		return null;
	}
	
	public interface AppProgressListener extends ProgressListener<File> {
		public void onStateChanged(State state);
	}
	
	
	// 获取文件存储路径
	public File getDownloadDirectory(String name){
		File destFile = null;
		if(isExternalStorageAvailable()){
			destFile = new File(getExternalStorageDirectory()+APP_DOWNLOAD_DIRECTORY);
			destFile.mkdirs();
			destFile = new File(destFile, name+".apk");
		}else{
			// TODO 无外部存储器处理
		}
		return destFile;
	}

	// 判断外部存储器是否可用
	public boolean isExternalStorageAvailable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		} else {
			return false;
		}
	}
	
	public File getExternalStorageDirectory() {
		return Environment.getExternalStorageDirectory();
	}
	
	public String initModule(int module) {
		String appModule = null;
		switch (module) {
		case APPS_CARDAPP:
			appModule = AsyncRequest.MODULE_APPS_CARDAPP;
			break;
		case APPS_BUSINESS:
			appModule = AsyncRequest.MODULE_APPS_BUSINESS;
			break;
		case APPS_PERSONAL:
			appModule = AsyncRequest.MODULE_APPS_PERSONAL;
			break;
		case APPS_RECOMMEND:
			appModule = AsyncRequest.MODULE_APPS_RECOMMEND;
			break;
		default:
			break;
		}
		return appModule;
	}
	
}
