package cn.yjt.oa.app.app.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import cn.yjt.oa.app.app.utils.AdditionalIconUtils;
import cn.yjt.oa.app.beans.AppInfo;

public class AppAddInternalItem extends AppItem{

	public AppAddInternalItem(Context context, AppInfo data) {
		super(context, data);
	}
	
	@Override
	public void doAction(int position) {
		Intent intent = new Intent();
		intent.putExtra(AdditionalIconUtils.EXTRA_RESULT_INDEX, position);
		((Activity)context).setResult(Activity.RESULT_OK, intent);
		Toast.makeText(context, "已添加", Toast.LENGTH_LONG).show();
		((Activity)context).finish();
	}
}
