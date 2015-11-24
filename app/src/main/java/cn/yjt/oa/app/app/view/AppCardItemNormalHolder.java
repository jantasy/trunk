package cn.yjt.oa.app.app.view;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.controller.OnButtonRefreshListener;
import cn.yjt.oa.app.beans.AppInfo;
import cn.yjt.oa.app.http.AsyncRequest;

public class AppCardItemNormalHolder extends AppItemHolder implements OnClickListener{
	private static final int CONVERT_TO_MB = 1024*1024;
	private static int iconDefault = R.drawable.app_default_icon;
	private String iconUrl;
	private String appName;
	private int downCount;
	private long size;
	public AppCardItemNormalHolder(Context context, AppInfo data) {
		super(context, data);
		this.iconUrl = ((AppInfo) data).getIcon();
		this.appName = ((AppInfo) data).getName();
		this.downCount = ((AppInfo) data).getDownCount();
		this.size = ((AppInfo) data).getSize();
	}

	@Override
	public View createView() {
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.app_listview_item_normal, parent, false);
			holder.app_thumbnails = (ImageView) convertView.findViewById(R.id.iv_app_thumbnails);
			holder.app_title = (TextView) convertView.findViewById(R.id.tv_app_title);
			holder.app_info = (TextView) convertView.findViewById(R.id.tv_app_info);
			holder.app_download = (Button) convertView.findViewById(R.id.btn_app_download);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		if(iconUrl != null){
			AsyncRequest.getInImageView(iconUrl, holder.app_thumbnails, iconDefault ,0);
		}
		holder.app_title.setText(appName);
		holder.app_info.setText(downCount +"人下载 | "+size/CONVERT_TO_MB+"MB");
		final Button button = holder.app_download;
		button.setText(appItem.getStatusText());
		appItem.setOnButtonRefreshListener(new OnButtonRefreshListener() {
			
			@Override
			public void onRefresh() {
				button.post(new Runnable() {
					
					@Override
					public void run() {
						button.setText(appItem.getStatusText());
					}
				});
			}
		});
		holder.app_download.setOnClickListener(this);
		appItem.registerListener();
		return convertView;
	}
	
	public static class ViewHolder{
		ImageView app_thumbnails;
		TextView app_title;
		TextView app_info;
		Button app_download;
		String packName;
	}

	@Override
	public void onClick(View v) {
		appItem.doAction();

	}
}
