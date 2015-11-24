package cn.yjt.oa.app.attendance;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.BeaconInfo;
import cn.yjt.oa.app.component.BackTitleFragmentActivity;

public class BeaconListActivity extends BackTitleFragmentActivity implements OnItemClickListener {
	
	private List<BeaconInfo> beaconInfos;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.listview);
		beaconInfos = getIntent().getParcelableArrayListExtra("beaconInfos");
		listView = (ListView) findViewById(R.id.listView1);
		listView.setDivider(null);
		listView.setAdapter(new BeaconListAdapter());
		listView.setOnItemClickListener(this);
		
	}
	
	public static void launchWithBeaconInfos(Fragment fragment,ArrayList<BeaconInfo> beaconInfos,int requestCode){
		Intent intent = new Intent(fragment.getActivity(), BeaconListActivity.class);
		intent.putParcelableArrayListExtra("beaconInfos", beaconInfos);
		fragment.startActivityForResult(intent, requestCode);
	}
	
	private class BeaconListAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		public BeaconListAdapter() {
			inflater = LayoutInflater.from(getApplicationContext());
		}

		@Override
		public int getCount() {
			return beaconInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return beaconInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_attendance_beacon_list,
						parent, false);
			}
			TextView beaconName = (TextView) convertView
					.findViewById(R.id.beacon_name);
			TextView beaconUumm = (TextView) convertView
					.findViewById(R.id.beacon_uumm);

			final BeaconInfo beaconInfo = (BeaconInfo) getItem(position);
			beaconName.setText(beaconInfo.getName());
			beaconUumm.setText(beaconInfo.getUumm());

			return convertView;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		BeaconInfo beaconInfo = (BeaconInfo) parent.getItemAtPosition(position);
		if(beaconInfo.getId()!=0){
			Intent data = new Intent();
			data.putExtra("beaconInfo", beaconInfo);
			setResult(RESULT_OK, data );
			finish();
		}
	}
}
