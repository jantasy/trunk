package cn.yjt.oa.app.teleconferencenew;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import cn.yjt.oa.app.R;

public class CallListAdapter extends BaseExpandableListAdapter {
    private List<ArrayList<String>> childData;
    private Context mContext;
    private ArrayList<String> groupData;
    private ArrayList<String> timeData;
    private ArrayList<String> duringData;

    public CallListAdapter(Context context, ArrayList<String> proList, ArrayList<String> timeList, ArrayList<String> duringList, ArrayList<ArrayList<String>> cityLists) {
    	mContext = context;
        groupData = new ArrayList();
        timeData = new ArrayList();
        timeData = new ArrayList();
        childData = new ArrayList();
        groupData = proList;
        timeData = timeList;
        duringData = duringList;
        childData = cityLists;
    }

    public Object getChild(int i, int i2) {
        return ((ArrayList) childData.get(i)).get(i2);
    }

    public long getChildId(int i, int i2) {
        return (long) i2;
    }

    public View getChildView(int i, int i2, boolean z, View view, ViewGroup viewGroup) {
    	ViewHolder holder = null;
        if (view == null) {
            view = ((LayoutInflater) mContext.getSystemService("layout_inflater")).inflate(R.layout.item_citychild, null);
            holder = new ViewHolder();
            holder.textJoin = (TextView) view.findViewById(R.id.tv_join);
            holder.textNumber = (TextView) view.findViewById(R.id.tv_child);
            view.setTag(holder);
        }
        holder = (ViewHolder) view.getTag();
        if(i2 != 0) {
        	holder.textJoin.setVisibility(View.INVISIBLE);
        } else {
        	holder.textJoin.setVisibility(View.VISIBLE);
        }
        holder.textNumber.setText((String) ((ArrayList) childData.get(i)).get(i2));
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

    public View getGroupView(final int i, boolean z, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = ((LayoutInflater) mContext.getSystemService("layout_inflater")).inflate(R.layout.item_citygroup, null);
        }
        ((TextView) view.findViewById(R.id.tv_group)).setText((String) getGroup(i)); 
        String startTime = timeData.get(i);
        if(startTime.length() == 14) {
        	startTime = startTime.substring(0, 4) + "-" + startTime.substring(4, 6) + "-" + startTime.substring(6, 8)
					+ " " + startTime.substring(8, 10) + ":" + startTime.substring(10, 12) + ":" + startTime.substring(12, 14);

        }
        ((TextView) view.findViewById(R.id.tv_time)).setText(startTime); 
        if(TextUtils.isEmpty(duringData.get(i))) {
        	((TextView) view.findViewById(R.id.tv_during)).setText("");
        } else {
        	((TextView) view.findViewById(R.id.tv_during)).setText("(" + duringData.get(i) + "分钟" + ")");
        }
         
        ((Button) view.findViewById(R.id.button_meeting)).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				((CallListActivity)mContext).setData(i);
				
			}
		});
        return view;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int i, int i2) {
        return true;
    }
    
	class ViewHolder {
		TextView textJoin;
		TextView textNumber;
	}
}
