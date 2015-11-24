package cn.yjt.oa.app.nfctools.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.nfctools.AddOperationActivity;
import cn.yjt.oa.app.nfctools.CreateOkActivity;
import cn.yjt.oa.app.nfctools.NFCListViewAdapter;
import cn.yjt.oa.app.nfctools.NfcTagOperationRecord;
import cn.yjt.oa.app.nfctools.operation.NfcTagOperation;
import cn.yjt.oa.app.nfctools.operation.NfcTagOperator;
import cn.yjt.oa.app.utils.OperaEventUtils;

public class NFCCreateFragment extends NFCBaseFragment implements OnClickListener{

	private static final int REQUEST_CODE_ADD = 1;
	
	private NfcTagOperationRecord operationRecord;
	private EditText nfcTagName;
	private Button btnAdd;
	private Button btnWrite;
	private ListView nfcListView;
	private NfcCreateAdapter adapter;
	private List<NfcTagOperator> list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.nfc_create_new, container, false);
		initAdapter();
		initView(view);
		return view;
	}
	
	
	
	private void initView(View view){
		nfcTagName = (EditText) view.findViewById(R.id.nfc_tag_name);
		btnAdd = (Button) view.findViewById(R.id.nfc_add_operation);
		btnWrite = (Button) view.findViewById(R.id.nfc_write_tag);
		nfcListView = (ListView) view.findViewById(R.id.nfc_add_operation_list);
		nfcListView.setAdapter(adapter);
		btnAdd.setOnClickListener(this);
		btnWrite.setOnClickListener(this);
	}
	
	private void initAdapter(){
		adapter = new NfcCreateAdapter(getActivity());
		list = new ArrayList<NfcTagOperator>();
		adapter.bindData(list);
	}
	
	private void refresh(){
		adapter.notifyDataSetChanged();
	}
	
	public void addOperator(NfcTagOperator info){
		list.add(info);
		refresh();
	}
	
	public void addOperators(List<NfcTagOperator> infos){
		list.addAll(infos);
		refresh();
	}
	
	public void removeOperator(NfcTagOperator info){
		list.remove(info);
		refresh();
	}
	
	private String getNfcTagName(){
		return nfcTagName.getText().toString().trim();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.nfc_add_operation:
			if(!checkNfcTagName()){
				goActivityForAdd();
			}else{
				Toast.makeText(getActivity(), R.string.nfc_tag_name_empty, Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.nfc_write_tag:
			if (!checkNfcTagName()) {
				goActivityForWrite();
			}else{
				Toast.makeText(getActivity(), R.string.nfc_tag_name_empty, Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
	}
	
	
	private boolean checkNfcTagName(){
		return TextUtils.isEmpty(nfcTagName.getText().toString());
	}
	
	class NfcCreateAdapter extends NFCListViewAdapter<NfcTagOperator>{

		public NfcCreateAdapter(Context context) {
			super(context);
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.nfc_create_item, parent, false);
			}
			TextView operatorName = (TextView) convertView.findViewById(R.id.nfc_operator_name);
			operatorName.setText(list.get(position).getOperatorName());
			Drawable drawable = list.get(position).getIconDrawable(getActivity());
			drawable.setBounds(0, 0, getResources().getDimensionPixelSize(R.dimen.operator_drawable), getResources().getDimensionPixelSize(R.dimen.operator_drawable));
			operatorName.setCompoundDrawables(drawable, null, null, null);
			TextView operator = (TextView) convertView.findViewById(R.id.nfc_operation);
			operator.setText(list.get(position).getSelectedOperation().getOperationName());
			ImageButton remove = (ImageButton) convertView.findViewById(R.id.nfc_operation_remove);
			remove.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					removeOperator(list.get(position));
				}
			});
			return convertView;
		}
	}
	
	private static final int REQUEST_CODE_SELECT = 0;
	
	private void goActivityForAdd(){
		Intent intent = new Intent(getActivity(),AddOperationActivity.class);
		startActivityForResult(intent, REQUEST_CODE_SELECT);
	}

	private void goActivityForWrite(){
		/*记录操作 1404*/
		OperaEventUtils.recordOperation(OperaEvent.OPERA_WRITE_NFCTAG);

		NfcTagOperationRecord operationRecord = new NfcTagOperationRecord();
		operationRecord.setTagName(getNfcTagName());
		operationRecord.setOperations(getNfcTagOperations(list));
		CreateOkActivity.launchForResult(this, operationRecord, true, -1L, 0);
	}
	
	private List<NfcTagOperation> getNfcTagOperations(List<NfcTagOperator> operators){
		List<NfcTagOperation> tagOperations= new ArrayList<NfcTagOperation>();
		if(operators != null){
			for (NfcTagOperator nfcTagOperator : operators) {
				tagOperations.add(nfcTagOperator.getSelectedOperation());
			}
		}
		return tagOperations;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE_SELECT && resultCode == Activity.RESULT_OK){
			List<NfcTagOperator> operators = data.getParcelableArrayListExtra("operators");
			addOperators(operators);
		}
	}
	
}
