package cn.yjt.oa.app.nfctools;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class NFCListViewAdapter<T> extends BaseAdapter{
	
	protected Context mContext;
	protected LayoutInflater mInflater;
	protected List<T> mList;
	
	public NFCListViewAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mList = new ArrayList<T>();
	}
	
	public void bindData(List<T> list){
		mList = list;
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}
}
