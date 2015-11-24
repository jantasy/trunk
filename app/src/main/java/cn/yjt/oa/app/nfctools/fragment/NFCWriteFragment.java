package cn.yjt.oa.app.nfctools.fragment;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.nfctools.CreateOkActivity;
import cn.yjt.oa.app.nfctools.NFCCreateActivity;
import cn.yjt.oa.app.nfctools.NFCListViewAdapter;
import cn.yjt.oa.app.nfctools.NfcTagOperationRecord;
import cn.yjt.oa.app.nfctools.NfcTagRecordDao;
import cn.yjt.oa.app.nfctools.NfcTagRecordDaoImpl;

public class NFCWriteFragment extends NFCBaseFragment implements OnClickListener, OnItemClickListener{
	private List<NfcTagOperationRecord> nfcRecords;
	private NfcTagRecordDao nfcTagRecordDao;
	private NFCWriteAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initRecords();
		initAdapter();
	}
	
	private void initAdapter(){
		mAdapter = new NFCWriteAdapter(getActivity());
		mAdapter.bindData(nfcRecords);
	}
	
	private void initRecords(){
		nfcTagRecordDao = new NfcTagRecordDaoImpl();
		nfcRecords = nfcTagRecordDao.getNfcTagOperationRecords(getActivity().getApplicationContext());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.nfc_fragment_write, container, false);
		initView(view);
		return view;
	}
	
	private ListView mListView;
	private Button mBtnCreate;
	private void initView(View view){
		mListView = (ListView) view.findViewById(R.id.nfc_list_view);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mBtnCreate = (Button) view.findViewById(R.id.nfc_create_nfc);
		mBtnCreate.setOnClickListener(this);
	}
	
	class NFCWriteAdapter extends NFCListViewAdapter<NfcTagOperationRecord>{

		public NFCWriteAdapter(Context context) {
			super(context);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.nfc_write_item, parent, false);
			}
			NfcTagOperationRecord record = NfcTagOperationRecord.parseRecord(nfcRecords.get(position).getData());
			TextView tagName = (TextView) convertView.findViewById(R.id.nfc_write_key);
			tagName.setText(record.getTagName());
			TextView content = (TextView) convertView.findViewById(R.id.nfc_tag_content);
			content.setText(record.getContent());
			ImageButton remove = (ImageButton) convertView.findViewById(R.id.nfc_item_remove);
			final int index = position;
			remove.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					nfcTagRecordDao.deleteNfcTagoperationRecord(nfcRecords.get(index).getId(), getActivity());
					nfcRecords.remove(nfcRecords.get(index));
					mAdapter.notifyDataSetChanged();
				}
			});
			return convertView;
		}
		
	}

	@Override
	public void onClick(View v) {
		toActivity(NFCCreateActivity.class);
	}
	
	private void goActivityForWrite(int index){
		NfcTagOperationRecord record = nfcRecords.get(index);
		if(record == null){
			Toast.makeText(getActivity(), "数据已损坏，请重新创建", Toast.LENGTH_SHORT).show();
			return;
		}
		CreateOkActivity.launchForResult(NFCWriteFragment.this, record, false, -1L, 0);;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		goActivityForWrite(position);
	}
	
	public void refreshData(){
		if(nfcTagRecordDao!=null){
			nfcRecords = nfcTagRecordDao.getNfcTagOperationRecords(getActivity().getApplicationContext());
			mAdapter.bindData(nfcRecords);
			mAdapter.notifyDataSetChanged();
		}
		
	}
	
}
