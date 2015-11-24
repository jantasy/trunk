package cn.yjt.oa.app.lifecircle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

import cn.yjt.oa.app.R;

public class AreaLeftAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<String> foods;
    int[] images;
    LayoutInflater inflater;
    int last_item;
    private int selectedPosition;

    public static class ViewHolder {
        public TextView arrow;
        public RelativeLayout layout;
        public TextView textView;
    }

    public AreaLeftAdapter(Context context, ArrayList<String> arrayList) {
        selectedPosition = -1;
        mContext = context;
        foods = arrayList;
        inflater = LayoutInflater.from(context);
    }

    public AreaLeftAdapter(Context context, ArrayList<String> arrayList, int[] iArr) {
        selectedPosition = -1;
        mContext = context;
        foods = arrayList;
        inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return foods.size();
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
            view = inflater.inflate(R.layout.item_arealeft, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.textview);
            viewHolder.arrow = (TextView) view.findViewById(R.id.arrow);
            viewHolder.layout = (RelativeLayout) view.findViewById(R.id.colorlayout);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.textView.setText((CharSequence) foods.get(i));
        if (selectedPosition == i) {
            viewHolder.textView.setTextColor(mContext.getResources().getColor(R.color.widget_selected_front));
            viewHolder.arrow.setVisibility(View.VISIBLE);
        } else {
            viewHolder.textView.setTextColor(mContext.getResources().getColor(R.color.city_c));
            viewHolder.arrow.setVisibility(View.GONE);
        }
        return view;
    }

    public void setSelectedPosition(int i) {
        selectedPosition = i;
    }
}
