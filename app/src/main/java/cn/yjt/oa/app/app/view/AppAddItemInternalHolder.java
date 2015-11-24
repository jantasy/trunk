package cn.yjt.oa.app.app.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.activity.AdditionalIcon;
import cn.yjt.oa.app.beans.AppInfo;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.utils.OperaEventUtils;

public class AppAddItemInternalHolder extends AppItemHolder{
	private Drawable icon;
	private String appName;
	public AppAddItemInternalHolder(Context context, AppInfo data) {
		super(context, data);
		this.icon = ((AdditionalIcon) data).getIcon(context);
		this.appName = ((AdditionalIcon) data).getTitle(context);
	}

	@Override
	public View createView() {
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.app_listview_item_additional, parent, false);
			holder.app_thumbnails = (ImageView) convertView.findViewById(R.id.iv_app_thumbnails);
			holder.app_title = (TextView) convertView.findViewById(R.id.tv_app_title);
			holder.app_add = (Button) convertView.findViewById(R.id.btn_app_add);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.app_thumbnails.setImageDrawable(icon);
		holder.app_title.setText(appName);
		holder.app_add.setText("添加");
		holder.app_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				appItem.doAction(position);
                 /*记录操作 0505*/
                OperaEventUtils.recordOperation(OperaEvent.OPERA_DASHBOARD_ADD_LOCAL_APP);

			}
		});
		return convertView;
	}

	public static class ViewHolder{
		ImageView app_thumbnails;
		TextView app_title;
		Button app_add;
	}

}
