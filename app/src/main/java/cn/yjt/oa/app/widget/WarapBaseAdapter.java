package cn.yjt.oa.app.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class WarapBaseAdapter extends BaseAdapter {

	private BaseAdapter internalAdapters[];
	
	public void setWarapedAdapters(BaseAdapter... adapters) {
		internalAdapters = adapters;
	}

	@Override
	public int getCount() {
		int count = 0;
		for (BaseAdapter adapter:internalAdapters) {
			count += adapter.getCount();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		for (BaseAdapter adapter:internalAdapters) {
			int count = adapter.getCount();
			if (position < count) {
				return adapter.getItem(position);
			} else {
				position -= count;
			}
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		for (BaseAdapter adapter:internalAdapters) {
			int count = adapter.getCount();
			if (position < count) {
				return adapter.getItemId(position);
			} else {
				position -= count;
			}
		}
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		for (BaseAdapter adapter:internalAdapters) {
			int count = adapter.getCount();
			if (position < count) {
				return adapter.getView(position, convertView, parent);
			} else {
				position -= count;
			}
		}
		return null;
	}
	
	public int getItemViewType(int position) {
		int addItemViewTypeCount = 0;
		for (BaseAdapter adapter:internalAdapters) {
			int count = adapter.getCount();
			if (position < count) {
				return adapter.getItemViewType(position) + addItemViewTypeCount;
			} else {
				position -= count;
				addItemViewTypeCount += adapter.getViewTypeCount();
			}
		}
        return 0;
    }

    public int getViewTypeCount() {
    	int count = 0;
    	for (BaseAdapter adapter:internalAdapters) {
			count += adapter.getViewTypeCount();
		}
        return count;
    }

}
