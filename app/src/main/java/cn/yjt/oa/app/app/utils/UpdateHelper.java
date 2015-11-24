package cn.yjt.oa.app.app.utils;

import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;
import io.luobo.common.json.TypeToken;

import java.util.Date;

import android.app.Application;
import android.content.Context;

import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.http.AsyncRequest;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.utils.TelephonyUtil;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

/**处理升级的类*/
public final class UpdateHelper {
	
	private static final String TAG = "UpdateHelper";
	
	private static boolean updatedThisLaunch;
	
	public static final void update(final Context context) {
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(context);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

			@Override
			public void onUpdateReturned(int arg0, UpdateResponse arg1) {
				switch (arg0) {
				case UpdateStatus.Yes: // has update
					boolean ignore = UmengUpdateAgent.isIgnore(context, arg1);
					if (!ignore) {
						UmengUpdateAgent.showUpdateDialog(context, arg1);
						updatedThisLaunch = true;
					}else{
						LogUtils.i(TAG, "update had been ignored by user.");
					}
					break;
				case UpdateStatus.No: // has no update
					break;
				case UpdateStatus.NoneWifi: // none wifi
					break;
				case UpdateStatus.Timeout: // time out
					break;
				}
			}
		});
	}
	
	public static final void updateOnlyOnceLaunch(Context context){
		if(!updatedThisLaunch){
			
			update(context);
		}
	}
	
	//TODO:
	public static final void requestIsWhiteList(Context context){
		cn.yjt.oa.app.http.AsyncRequest.Builder builder = new cn.yjt.oa.app.http.AsyncRequest.Builder();
		String url = "/whitelist/iswhitelist";
		String uploadUrl = String.format(url,TelephonyUtil.getICCID(context));
		builder.setModule(uploadUrl);
		builder.setResponseType(new TypeToken<Response<Boolean>>() {
		}.getType());
		builder.setResponseListener(new Listener<Response<Boolean>>() {
			@Override
			public void onErrorResponse(InvocationError arg0) {
				
			}

			@Override
			public void onResponse(Response<Boolean> response) {
				
			}

		});
		builder.build().get();
	}
	
	public static final void reset(){
		updatedThisLaunch = false;
	}
}
