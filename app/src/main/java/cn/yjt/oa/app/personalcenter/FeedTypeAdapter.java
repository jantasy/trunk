package cn.yjt.oa.app.personalcenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.yjt.oa.app.MainApplication;
import cn.yjt.oa.app.R;

public class FeedTypeAdapter extends BaseAdapter {
	private Context mContext;
	private String[] mData;
	
	
	public FeedTypeAdapter(Context context,String[] mData){
		this.mContext=context;
		this.mData=mData;
		
	}


	@Override
	public int getCount() {
		return mData.length;
	}


	@Override
	public Object getItem(int position) {
		return mData[position];
	}


	@Override
	public long getItemId(int position) {
		return position;
	}


	@SuppressLint("NewApi") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TypeItem typeItem;
		if(convertView==null){
			convertView=View.inflate(mContext, R.layout.feed_type_item, null);
			typeItem=new TypeItem();
			
			typeItem.typeLayout=(LinearLayout) convertView.findViewById(R.id.type_layout);
			typeItem.typeText=(TextView) convertView.findViewById(R.id.type_text);
			typeItem.line=(ImageView) convertView.findViewById(R.id.feed_line);
			convertView.setTag(typeItem);
		}else{
			typeItem=(TypeItem) convertView.getTag();
		}
	
		typeItem.typeText.setText(mData[position]);
		if(position==mData.length-1){
			typeItem.typeLayout.setBackgroundResource(R.drawable.feed_type_bottom);
			typeItem.line.setVisibility(View.INVISIBLE);
//			typeItem.typeText.setPadding(dp2px(13.5f),0,0,0);
		}else {
			typeItem.typeLayout.setBackgroundResource(R.drawable.feed_type_mid);
			typeItem.line.setVisibility(View.VISIBLE);

		}
		return convertView;
	}

	private int dp2px(float dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				MainApplication.getAppContext().getResources().getDisplayMetrics());
	}
	
	public final class TypeItem{
		private LinearLayout typeLayout;
		private TextView typeText;
		private ImageView line;
		
	}
	
	

	

	

}
