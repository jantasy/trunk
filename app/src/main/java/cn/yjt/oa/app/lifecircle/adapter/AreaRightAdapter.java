package cn.yjt.oa.app.lifecircle.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

import cn.yjt.oa.app.R;

public class AreaRightAdapter extends BaseAdapter {
    ArrayList<ArrayList<String>> cities;
    Context mContext;
    public int foodpoition;
    LayoutInflater layoutInflater;

    public static class ViewHolder {
        public TextView textView;
    }

    public AreaRightAdapter(Context context, ArrayList<ArrayList<String>> arrayList, int i) {
    	mContext = context;
        cities = arrayList;
        layoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        foodpoition = i;
    }

    public int getCount() {
        return ((ArrayList) cities.get(foodpoition)).size();
    }

    public Object getItem(int i) {
        return Integer.valueOf(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_arearight, null);
            ViewHolder viewHolder2 = new ViewHolder();
            viewHolder2.textView = (TextView) view.findViewById(R.id.textview1);
            view.setTag(viewHolder2);
            viewHolder = viewHolder2;
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Log.d("AreaRightAdapter", foodpoition + ",,,,,,,,,,,,," + i + ",,,,,," + cities.size());
        viewHolder.textView.setText((CharSequence) ((ArrayList) cities.get(foodpoition)).get(i));
        return view;
    }
}
