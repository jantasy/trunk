package cn.yjt.oa.app.nfctools;

import java.util.List;

import org.ndeftools.Message;
import org.ndeftools.Record;
import org.ndeftools.externaltype.ExternalTypeRecord;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import cn.yjt.oa.app.app.utils.AppUtils;
import cn.yjt.oa.app.nfctools.NfcReader.NfcReaderInterface;
import cn.yjt.oa.app.nfctools.operation.NfcTagOperation;

public class NfcTagBlankExecutorActivity extends Activity implements
		NfcReaderInterface {
	private NfcReader nfcReader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		nfcReader = new NfcReader(this, this);
		nfcReader.newIntent(getIntent());
		nfcReader.processIntent();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		nfcReader.newIntent(getIntent());
		nfcReader.processIntent();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
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
		finish();

	}

	@Override
	public void onTagLost() {

	}

	@Override
	public void readNdefMessage(NfcTagInfo info) {
		Message message = info.getMessage();
		for (Record record : message) {
			if (record instanceof ExternalTypeRecord) {
				NfcTagOperationRecord parseRecord = NfcTagOperationRecord
						.parseRecord(record.getNdefRecord().getPayload());
				List<NfcTagOperation> operations = parseRecord.getOperations();
				if (operations == null || operations.isEmpty()) {
					AppUtils.open(getApplicationContext(),
							getApplicationContext().getPackageName());
					return;
				}
				if (operations.size() == 1) {
					NfcTagOperationExecuteHandler.create(operations.get(0))
							.handle(this, parseRecord.getTagName());
				} else {
					NfcTagOperationExecuteHandler.create(null).handle(this,
							parseRecord.getTagName());
				}
				for (NfcTagOperation nfcTagOperation : operations) {
					nfcTagOperation.setSn(info.getSerialNumber());
					nfcTagOperation.setTagName(parseRecord.getTagName());
					nfcTagOperation.setContext(this);
					nfcTagOperation.excuteOperation();
				}

			}
		}
	}

	@Override
	public void readEmptyNdefMessage(NfcTagInfo info) {
		finish();

	}

	@Override
	public void readNonNdefMessage(NfcTagInfo info) {
		finish();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void connectError(Exception e) {
		finish();
	}

	@Override
	public void readTag216(NfcTagInfo info) {
		byte[] tag216Data = info.getTag216Data();
		NfcTagOperationRecord parseRecord = NfcTagOperationRecord
				.parseRecord(tag216Data);
		
		List<NfcTagOperation> operations = parseRecord.getOperations();
		if (operations == null || operations.isEmpty()) {
			AppUtils.open(getApplicationContext(),
					getApplicationContext().getPackageName());
			return;
		}
		if (operations.size() == 1) {
			NfcTagOperationExecuteHandler.create(operations.get(0))
					.handle(this, parseRecord.getTagName());
		} else {
			NfcTagOperationExecuteHandler.create(null).handle(this,
					parseRecord.getTagName());
		}
		for (NfcTagOperation nfcTagOperation : operations) {
			nfcTagOperation.setSn(info.getSerialNumber());
			nfcTagOperation.setTagName(parseRecord.getTagName());
			nfcTagOperation.setContext(this);
			nfcTagOperation.excuteOperation();
		}
	}

}
