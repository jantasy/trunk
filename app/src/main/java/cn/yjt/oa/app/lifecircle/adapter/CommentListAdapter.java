package cn.yjt.oa.app.lifecircle.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import java.util.List;

import cn.yjt.oa.app.R;
import cn.yjt.oa.app.lifecircle.model.Netable;

public class CommentListAdapter extends BaseAdapter {
    private static final String tag = "TaskListViewAdapter";
    private LayoutInflater mInflater;
    private List<Netable> mNetable;
    private ListView myListView;

    class ViewHolder {
        private TextView content;
        private TextView cost;
        private TextView name;
        private RatingBar ratingbar;
        private TextView time;

        ViewHolder() {
        }
    }
    
    public CommentListAdapter(Context context, ListView listView, List<Netable> list) {        
        mInflater = LayoutInflater.from(context);
        mNetable = list;
        myListView = listView;
    }

    public void addNewsItem(Netable netable) {
        mNetable.add(netable);
    }

    public int getCount() {
        return mNetable.size();
    }

    public Object getItem(int i) {
        return mNetable.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        Log.d(tag, "第几条" + i);
        if (view == null) {
            view = mInflater.inflate(R.layout.item_pl, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.ratingbar = (RatingBar) view.findViewById(R.id.ratingbar);
            viewHolder.cost = (TextView) view.findViewById(R.id.cost);
            viewHolder.content = (TextView) view.findViewById(R.id.content);
            viewHolder.time = (TextView) view.findViewById(R.id.time);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (mNetable != null && mNetable.size() > 0) {
            viewHolder.name.setText(((Netable) mNetable.get(i)).getAuthor());
            viewHolder.ratingbar.setRating(((Netable) mNetable.get(i)).getScore());
            if ("-1".equals(Float.valueOf(((Netable) mNetable.get(i)).getMoney()))) {
                viewHolder.cost.setText("未填写");
            } else {
                viewHolder.cost.setText(((Netable) mNetable.get(i)).getMoney()+"");
            }
            viewHolder.content.setText(((Netable) mNetable.get(i)).getContent());
            viewHolder.time.setText(((Netable) mNetable.get(i)).getPostTime());
        }
        return view;
    }

    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
        if (dataSetObserver != null) {
            super.unregisterDataSetObserver(dataSetObserver);
        }
    }
}
