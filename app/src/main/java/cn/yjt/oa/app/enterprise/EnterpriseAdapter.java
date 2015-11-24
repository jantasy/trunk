package cn.yjt.oa.app.enterprise;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.CustInfo;

public class EnterpriseAdapter extends BaseAdapter {
	private Context context;
	private List<CustInfo> enterpriseList=Collections.emptyList();
	
	
	public EnterpriseAdapter(Context context){
		this.context=context;
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
		if(convertView==null){
			convertView=LayoutInflater.from(context).inflate(R.layout.enterprise_item, null);
		}
		CustInfo custInfo = enterpriseList.get(position);
		if(custInfo!=null){
			TextView enterpriseName = (TextView) convertView.findViewById(R.id.enterprise_name);
			TextView enterpriseId = (TextView) convertView.findViewById(R.id.enterprise_id);
			enterpriseName.setText(custInfo.getName());
			enterpriseId.setText("ID:"+String.valueOf(custInfo.getUniqueId()));
			if(custInfo.getvCode() == 1){
				enterpriseName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_v, 0, 0, 0);
				enterpriseName.setCompoundDrawablePadding(context.getResources().getDimensionPixelSize(R.dimen.dimen_5dp));
			}else{
				enterpriseName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_no_v, 0, 0, 0);
				enterpriseName.setCompoundDrawablePadding(context.getResources().getDimensionPixelSize(R.dimen.dimen_5dp));
			}
			
		}
		return convertView;
	}
	
	
	
	

	public void setEnterpriseList(List<CustInfo> enterpriseList) {
		this.enterpriseList = enterpriseList;
	}


}
