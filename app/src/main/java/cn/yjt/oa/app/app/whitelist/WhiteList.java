package cn.yjt.oa.app.app.whitelist;

import android.content.Context;
import cn.yjt.oa.app.app.utils.UpdateHelper;
import cn.yjt.oa.app.http.ApiHelper;
import cn.yjt.oa.app.http.ResponseListener;

public class WhiteList {

	public void checkWhiteList(final Context context) {
		ApiHelper.checkWhiteList(new ResponseListener<Boolean>() {

			@Override
			public void onSuccess(Boolean payload) {
				if(payload)
				{
					UpdateHelper.updateOnlyOnceLaunch(context);
				}
			}
		});
	}
}
