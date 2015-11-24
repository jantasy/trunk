package cn.yjt.oa.app.nfctools.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import cn.yjt.oa.app.R;

public abstract class NFCListFragment extends NFCBaseFragment{

	protected ListView mListView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.nfc_fragment_listview, container, false);
		mListView = (ListView) view.findViewById(R.id.nfc_list_view);
		mListView.setAdapter(getAdapter());
		return view;
	}
	
	protected abstract BaseAdapter getAdapter();
	
}
