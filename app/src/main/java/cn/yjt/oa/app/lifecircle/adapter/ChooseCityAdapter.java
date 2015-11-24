package cn.yjt.oa.app.lifecircle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import cn.yjt.oa.app.R;

public class ChooseCityAdapter extends BaseExpandableListAdapter {
    private List<ArrayList<String>> childData;
    private Context mContext;
    private ArrayList<String> groupData;

    public ChooseCityAdapter(Context context, ArrayList<String> proList, ArrayList<ArrayList<String>> cityLists) {
    	mContext = context;
        groupData = new ArrayList();
        childData = new ArrayList();
        groupData = proList;
        childData = cityLists;
    }

    public Object getChild(int i, int i2) {
        return ((ArrayList) childData.get(i)).get(i2);
    }

    public long getChildId(int i, int i2) {
        return (long) i2;
    }

    public View getChildView(int i, int i2, boolean z, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = ((LayoutInflater) mContext.getSystemService("layout_inflater")).inflate(R.layout.item_citychild, null);
        }
        ((TextView) view.findViewById(R.id.tv_child)).setText((String) ((ArrayList) childData.get(i)).get(i2));
        return view;
    }

    public int getChildrenCount(int i) {
        return ((ArrayList) childData.get(i)).size();
    }

    public Object getGroup(int i) {
        return groupData.get(i);
    }

    public int getGroupCount() {
        return groupData.size();
    }

    public long getGroupId(int i) {
        return (long) i;
    }

    public View getGroupView(int i, boolean z, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = ((LayoutInflater) mContext.getSystemService("layout_inflater")).inflate(R.layout.item_citygroup, null);
        }
        ((TextView) view.findViewById(R.id.tv_group)).setText((String) getGroup(i));
        return view;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int i, int i2) {
        return true;
    }
}
