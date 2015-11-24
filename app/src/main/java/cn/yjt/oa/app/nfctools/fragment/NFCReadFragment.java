package cn.yjt.oa.app.nfctools.fragment;

import java.util.List;

import org.ndeftools.Message;
import org.ndeftools.Record;
import org.ndeftools.externaltype.ExternalTypeRecord;
import org.ndeftools.wellknown.TextRecord;
import org.ndeftools.wellknown.UriRecord;

import android.nfc.NdefRecord;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.nfctools.NfcReader.NfcReaderInterface;
import cn.yjt.oa.app.nfctools.NfcTagInfo;
import cn.yjt.oa.app.nfctools.NfcTagOperationRecord;
import cn.yjt.oa.app.nfctools.operation.NfcTagOperation;
import cn.yjt.oa.app.utils.OperaEventUtils;


public class NFCReadFragment extends NFCBaseFragment implements NfcReaderInterface{
	private NfcTagInfo tagInfo;
	
	private ImageView animation;
	private ScrollView nfctaginfoContent;
	private TextView serialNumber;
	private TextView type;
	private TextView dataFormat;
	private TextView space;
	private TextView readOnly;
	private LinearLayout messageData;
	private boolean isNfcFeature=false;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.nfc_fragment_read, container, false);
		initView(view);
		return view;
	}
	
	private void initView(View view){
		animation=(ImageView) view.findViewById(R.id.animation);
		nfctaginfoContent=(ScrollView) view.findViewById(R.id.nfctaginfo_content);
		messageData=(LinearLayout) view.findViewById(R.id.message_data);
		serialNumber=(TextView) view.findViewById(R.id.serial_number);
		type=(TextView) view.findViewById(R.id.type);
		dataFormat=(TextView) view.findViewById(R.id.data_format);
		space=(TextView) view.findViewById(R.id.space);
		readOnly=(TextView) view.findViewById(R.id.read_only);
		
		if(!isNfcFeature){
			animation.setVisibility(View.VISIBLE);
			nfctaginfoContent.setVisibility(View.GONE);
		}else{
			animation.setVisibility(View.GONE);
			nfctaginfoContent.setVisibility(View.VISIBLE);
			}
		if(tagInfo!=null){
			displayNfcTagInfo(tagInfo);
		}
	}
	
	private void displayNfcTagInfo(NfcTagInfo tagInfo) {

		 /*记录操作 1405*/
		OperaEventUtils.recordOperation(OperaEvent.OPERA_READ_NFCTAG);

		messageData.removeAllViews();
		if(!isNfcFeature){
			animation.setVisibility(View.VISIBLE);
			nfctaginfoContent.setVisibility(View.GONE);
		}else{
			animation.setVisibility(View.GONE);
			nfctaginfoContent.setVisibility(View.VISIBLE);
			serialNumber.setText(tagInfo.getSerialNumber());
			type.setText(tagInfo.getType());
			dataFormat.setText(tagInfo.getDataFormat());
			space.setText(tagInfo.getUsedSize()+"/"+tagInfo.getMaxSize()+"字节");
			if(tagInfo.isWriteable()){
				readOnly.setText("读写");
			}else{
				readOnly.setText("只读");
			}
			
			if(tagInfo.getMessage()!=null &&tagInfo.getMessage().size()>0){
				for (int i = 0; i < tagInfo.getMessage().size(); i++) {
					View info_layout = LayoutInflater.from(getActivity()).inflate(R.layout.nfc_read_item, null);
					TextView tagName=(TextView) info_layout.findViewById(R.id.tag_name);
					TextView contentTV=(TextView) info_layout.findViewById(R.id.content);
					if(tagInfo.getMessage().get(i) instanceof TextRecord){
						tagName.setText("数据"+(i+1));
						contentTV.setText(((TextRecord)tagInfo.getMessage().get(i)).getText());
					}else if(tagInfo.getMessage().get(i) instanceof UriRecord){
						tagName.setText("数据"+(i+1));
						contentTV.setText(((UriRecord)tagInfo.getMessage().get(i)).getUri().toString());
					}else if(tagInfo.getMessage().get(i) instanceof ExternalTypeRecord){
						NfcTagOperationRecord parseContent = NfcTagOperationRecord.parseRecord(((ExternalTypeRecord)tagInfo.getMessage().get(i)).getData());
						tagName.setText(parseContent.getTagName());
						contentTV.setText(parseContent.getContent());
					}else{
						break;
					}
					
					messageData.addView(info_layout);
				}
			}
			
		}
		
	}

	@Override
	public void onNfcStateEnabled() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNfcStateDisabled() {	
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNfcStateChange(boolean enabled) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNfcFeatureNotFound() {
		isNfcFeature=false;
	}

	@Override
	public void onTagLost() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readNdefMessage(NfcTagInfo info) {
		isNfcFeature=true;
		tagInfo=info;
		displayNfcTagInfo(tagInfo);
//		performOperation(info);
		
	}

	private void performOperation(NfcTagInfo info) {
		Message message = info.getMessage();
		for (Record record : message) {
			if(record instanceof ExternalTypeRecord){
				NfcTagOperationRecord parseRecord = NfcTagOperationRecord.parseRecord(record.getNdefRecord().getPayload());
				List<NfcTagOperation> operations = parseRecord.getOperations();
				for (NfcTagOperation nfcTagOperation : operations) {
					nfcTagOperation.excuteOperation();
				}
			}
		}
	}

	@Override
	public void readEmptyNdefMessage(NfcTagInfo info) {
		isNfcFeature=true;
		tagInfo=info;
		displayNfcTagInfo(tagInfo);
	}


	@Override
	public void readNonNdefMessage(NfcTagInfo info) {
		isNfcFeature=false;
		
	}

	@Override
	public void connectError(Exception e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readTag216(NfcTagInfo info) {
		System.out.println("readTag216:"+info);
		try{
			
		
		isNfcFeature=true;
		tagInfo = info;
		
		
		byte[] tag216Data = info.getTag216Data();
		NfcTagOperationRecord parseRecord = NfcTagOperationRecord
				.parseRecord(tag216Data);
		Message message = new Message();
		List<NdefRecord> ndefRecords = parseRecord.generateNdefRecord();
		for (int i = 0; i < ndefRecords.size(); i++) {
			message.add(Record.parse(ndefRecords.get(i)));
		}
		tagInfo.setMessage(message );
		
		displayNfcTagInfo(tagInfo);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
