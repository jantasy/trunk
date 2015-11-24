package cn.yjt.oa.app.find;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.BaseFragment;
import cn.yjt.oa.app.MainActivity;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.app.utils.AppUtils;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.find.config.FinderConfig;
import cn.yjt.oa.app.find.config.FinderItem;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.widget.SectionAdapter;

public class FinderListFragment extends BaseFragment implements
		OnItemClickListener {

	private View root;

	@Override
	public CharSequence getPageTitle(Context context) {
		return context.getString(R.string.live_circle);
	}

	@Override
	public boolean onRightButtonClick() {
		return false;
	}

	@Override
	public void configRightButton(ImageView imgView) {
		//no right button
		imgView.setImageBitmap(null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MainActivity activity = (MainActivity) getActivity();
		activity.addToFragments(3, this);
		if(root==null){
			root = inflater.inflate(R.layout.finder_list, container, false);
			ListView listView = (ListView) root.findViewById(R.id.listView);
			FinderAdapter adapter = new FinderAdapter(inflater);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(this);
			List<List<FinderItem>> groups = FinderConfig.getInstance(getActivity()).getGroups();
			adapter.setData(groups);
		}


		return root;
	}

	private class FinderAdapter extends SectionAdapter {
		LayoutInflater inflater;
		List<List<FinderItem>> groups = Collections.emptyList();

		public FinderAdapter(LayoutInflater inflater) {
			super();
			this.inflater = inflater;
		}

		public void setData(List<List<FinderItem>> groups) {
			this.groups = groups;
		}

		@Override
		public int getSectionCount() {
			return groups.size();
		}

		@Override
		public int getItemCountAtSection(int section) {
			return groups.get(section).size();
		}

		@Override
		public Object getSection(int section) {
			return groups.get(section);
		}

		@Override
		public Object getItem(int section, int position) {
			return groups.get(section).get(position);
		}

		@Override
		public View getSectionView(int section, View convertView,
				ViewGroup parent) {
			convertView = new View(getActivity());
			convertView.setBackgroundColor(Color.rgb(0xf2, 0xf6, 0xfc));
			int height = 0;
			if (section == 0) {
				height = convertView.getResources().getDimensionPixelSize(
						R.dimen.finder_itemTop_height);
			} else {
				height = convertView.getResources().getDimensionPixelSize(
						R.dimen.finder_itemMid_height);
			}
			convertView.setLayoutParams(new AbsListView.LayoutParams(
					AbsListView.LayoutParams.MATCH_PARENT, height));
			return convertView;
		}

		@Override
		public View getItemView(int section, int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.finder_item, null);
			}

			ImageView icon = (ImageView) convertView
					.findViewById(R.id.finder_icon);
			TextView title = (TextView) convertView
					.findViewById(R.id.finder_title);
			ImageView line = (ImageView) convertView
					.findViewById(R.id.find_line);
			if (position == getItemCountAtSection(section) - 1) {
				line.setVisibility(View.GONE);
			} else {
				line.setVisibility(View.VISIBLE);
			}

			FinderItem child = (FinderItem) getItem(section, position);

			icon.setImageResource(child.getIcon());
			title.setText(child.getLabel());

			return convertView;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		int[] detailPosition = SectionAdapter.getDetailPosition(
				(SectionAdapter) parent.getAdapter(), position);
		if (detailPosition.length == 2) {

			String besypay = getResources().getString(R.string.title_bestpay);
			String wallet  = getResources().getString(R.string.title_wallet);
			String nfcreadbuscard = getResources().getString(R.string.nfc_readcard);

			TextView title = (TextView) view.findViewById(R.id.finder_title);
			try{
				if(besypay.equals(title.getText().toString())){
					/*记录操作 1505*/
					OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_YIZHIFU);
				}
				if(wallet.equals(title.getText().toString())){
					/*记录操作 1606*/
					OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_PHONEWALLET);
				}

				if(nfcreadbuscard.equals(title.getText().toString())){
					/*记录操作 1401*/
					OperaEventUtils.recordOperation(OperaEvent.OPERA_ENTER_READBUSCARD);
				}
			}catch (Exception e){

			}

			SectionAdapter adapter = (SectionAdapter) parent.getAdapter();
			FinderItem item = (FinderItem) adapter.getItem(position);
			Intent intent = null;
			try {
				 intent = Intent.parseUri(item.getIntent(), 0);
				startActivity(intent);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
				String package1 = intent.getPackage();
				if (!AppUtils.open(getActivity(), package1)) {
					startBrowerDownload(item.getUrl());
				}
			}
		}
	}

	private void startBrowerDownload(String url) {
		if (!TextUtils.isEmpty(url)) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			try {
				startActivity(intent);
			} catch (Exception e) {
				Toast.makeText(getActivity(), "无法打开浏览器", Toast.LENGTH_SHORT).show();
			}
		}
	}
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ViewGroup parent = (ViewGroup) root.getParent();
		if(parent!=null){
			parent.removeView(root);
		}
	}

}
