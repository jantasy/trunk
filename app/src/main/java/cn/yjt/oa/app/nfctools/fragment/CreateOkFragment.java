package cn.yjt.oa.app.nfctools.fragment;

import io.luobo.common.Cancelable;

import java.util.List;

import org.ndeftools.wellknown.UriRecord;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.yjt.oa.app.R;
import cn.yjt.oa.app.nfctools.NfcTagOperationRecord;
import cn.yjt.oa.app.nfctools.NfcTagRecordDao;
import cn.yjt.oa.app.nfctools.NfcTagRecordDaoImpl;
import cn.yjt.oa.app.nfctools.NfcTagWriter.NfcTagWriterInterface;

public class CreateOkFragment extends NFCBaseFragment implements
		NfcTagWriterInterface {
	private NfcTagOperationRecord operationRecord;
	private ImageView image;
	private TextView msg;
	private NfcTagRecordDao recordDao;
	private AnimationDrawable drawable;
	private Cancelable nfcTagCancelable;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		recordDao = new NfcTagRecordDaoImpl();
		operationRecord = getActivity().getIntent().getParcelableExtra(
				"nfcTagRecord");
	}

	public void startAnimation() {
		if (drawable != null) {
			drawable.start();
		}
	}

	public void saveRecord() {
		recordDao.addNfcTagOperationRecord(operationRecord, getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.nfc_create_ok, container, false);
		image = (ImageView) view.findViewById(R.id.nfc_writting_promt);
		drawable = (AnimationDrawable) image.getDrawable();
		msg = (TextView) view.findViewById(R.id.nfc_write_msg);
		Button writeOk = (Button) view.findViewById(R.id.nfc_write_ok);
		writeOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		return view;
	}

	@Override
	public NdefMessage createNdefMessage() {
		if (nfcTagCancelable != null) {
			nfcTagCancelable.cancel();
			nfcTagCancelable = null;
		}
		UriRecord uriRecord = new UriRecord("http://yjt.189.cn");
		List<NdefRecord> ndefRecords = operationRecord.generateNdefRecord();
		NdefRecord[] records = new NdefRecord[1 + ndefRecords.size()];
		records[0] = uriRecord.getNdefRecord();
		for (int i = 0; i < ndefRecords.size(); i++) {
			records[i + 1] = ndefRecords.get(i);
		}
		NdefMessage message = new NdefMessage(records);
		return message;
	}
	
	@Override
	public byte[] createNtag216() {
//		return createNdefMessage().toByteArray();
		return operationRecord.getData();
	}

	@Override
	public void writeNdefFailed(Exception e) {
		Toast.makeText(getActivity(), "写入失败", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void writeNdefNotWritable() {
		Toast.makeText(getActivity(), "标签不可写", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void writeNdefTooSmall(int required, int capacity) {
		Toast.makeText(getActivity(), "标签容量太小", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void writeNdefCannotWriteTech() {
		Toast.makeText(getActivity(), "标签不支持该技术", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void writeNdefSuccess(String sn) {
		if (getArguments().getBoolean("save_record", true)) {
			saveRecord();
		}

		stopAnim();
		Toast.makeText(getActivity(), "写入成功", Toast.LENGTH_SHORT).show();
		image.setImageResource(R.drawable.nfc_write_ok);
		msg.setText("写入成功");
		
		final Intent data = new Intent();
		data.putExtra("sn", sn);
		long areaId = getArguments().getLong("area_id", -1);
		if(areaId!=-1){
			data.putExtra("area_id",areaId);
		}
		long delayFinish = getArguments().getLong("delay_finish", -1L);
		if(delayFinish >= 0){
			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
				
				@Override
				public void run() {
					if(getActivity() != null){
						getActivity().setResult(Activity.RESULT_OK, data);
						getActivity().finish();
					}
				}
			}, delayFinish);
		}
	}

	private void stopAnim() {
		if (drawable != null) {
			drawable.stop();
			drawable = null;
		}
	}

	@Override
	public void onNfcStateEnabled() {

	}

	@Override
	public void onNfcStateDisabled() {

	}

	@Override
	public void onNfcStateChange(boolean enabled) {

	}

	@Override
	public void onNfcFeatureNotFound() {

	}

	@Override
	public void onTagLost() {

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

}
