package com.chinatelecom.nfc.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinatelecom.nfc.R;

public class AddEnvironmentAdapter  extends BaseAdapter{

	private LayoutInflater mInflater;
	private Context context;
	private List<Map<String, Object>> mData;
	
	public AddEnvironmentAdapter(Context context,List<Map<String, Object>> mData){
		this.mInflater = LayoutInflater.from(context);
		this.context = context;
		this.mData = mData;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		AddEnvironmentHolder holder = null;
		if (convertView == null) {
			
			holder=new AddEnvironmentHolder();  
			convertView = mInflater.inflate(R.layout.nfc_add_environment_item, null);
			
			holder.img = (ImageView)convertView.findViewById(R.id.img);
			holder.title = (TextView)convertView.findViewById(R.id.title);
			holder.info = (TextView)convertView.findViewById(R.id.info);
			holder.checkBox = (CheckBox)convertView.findViewById(R.id.cb);
			convertView.setTag(holder);
			
		}else {
			
			holder = (AddEnvironmentHolder)convertView.getTag();
		}
		
//		final View convertViewTemp = convertView;
		
		holder.img.setBackgroundResource((Integer)mData.get(position).get("img"));
		holder.title.setText((String)mData.get(position).get("title"));
		holder.info.setText((String)mData.get(position).get("info"));
		
		holder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					buttonView.setText(R.string.nfc_rbStart);
				}else{
					buttonView.setText(R.string.nfc_rbClose);
				}
			}

		});
		
/*		setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				 int radioButtonId = group.getCheckedRadioButtonId();
				 //根据ID获取RadioButton的实例
				RadioButton rb = (RadioButton)convertViewTemp.findViewById(radioButtonId);
				 //更新文本内容，以符合选中项
				MyUtil.showMessage(rb.getText().toString(), context);
			}
		});*/
		
		
		return convertView;
	}
	
	public final class AddEnvironmentHolder {
		public ImageView img;
		public TextView title;
		public TextView info;
		public CheckBox checkBox;
	}

	
}