package cn.yjt.oa.app.personalcenter;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.UserLoginInfo;

public class LoginEnterpriseAdapter extends BaseAdapter {

	private List<UserLoginInfo> enterpriseList=Collections.emptyList();
	private Context mContext;
	
	LoginEnterpriseAdapter(Context context,List<UserLoginInfo> enterpriseList){
		this.mContext=context;
		this.enterpriseList=enterpriseList;
	}

	@Override
	public int getCount() {
		return enterpriseList.size();
	}

	@Override
	public Object getItem(int position) {
		return enterpriseList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=LayoutInflater.from(mContext).inflate(R.layout.user_enterprise_item, null);
			holder.enterpriseName=(TextView) convertView.findViewById(R.id.enterprise_name);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		UserLoginInfo info = (UserLoginInfo) getItem(position);
		if(info.getCustVCode() == 1){
			holder.enterpriseName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_v, 0, 0, 0);
			holder.enterpriseName.setCompoundDrawablePadding(mContext.getResources().getDimensionPixelSize(R.dimen.dimen_5dp));
		}else{
			holder.enterpriseName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_no_v, 0, 0, 0);
			holder.enterpriseName.setCompoundDrawablePadding(mContext.getResources().getDimensionPixelSize(R.dimen.dimen_5dp));
		}
		holder.enterpriseName.setText(info.getCustName());
		return convertView;
	}
	
	private class ViewHolder{
		private TextView enterpriseName;
	}

}
