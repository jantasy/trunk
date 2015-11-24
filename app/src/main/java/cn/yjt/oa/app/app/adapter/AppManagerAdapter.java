package cn.yjt.oa.app.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.yjt.oa.app.R;

public class AppManagerAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	public AppManagerAdapter(Context context){
		inflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return 5;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.app_listview_item_manager, parent, false);
			holder.app_thubnails = (ImageView) convertView.findViewById(R.id.app_manager_thumbnails);
			holder.app_name = (TextView) convertView.findViewById(R.id.app_manager_title);
			holder.app_state = (TextView) convertView.findViewById(R.id.app_manager_state);
			holder.app_progress = (ProgressBar) convertView.findViewById(R.id.app_manager_progress);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		return convertView;
	}
	static class ViewHolder{
		ImageView app_thubnails;
		TextView app_name;
		TextView app_state;
		ProgressBar app_progress;
	}
}
