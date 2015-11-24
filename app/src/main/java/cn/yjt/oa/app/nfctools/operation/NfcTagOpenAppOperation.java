package cn.yjt.oa.app.nfctools.operation;

import org.ndeftools.externaltype.AndroidApplicationRecord;

import android.nfc.NdefRecord;
import cn.yjt.oa.app.MainApplication;

public class NfcTagOpenAppOperation extends NfcTagOperation implements NfcTagWellkownOperation{

	@Override
	public void excuteOperation() {
		//System operation.
	}

	@Override
	public NdefRecord generateNdefRecord() {
		byte[] extraData = getExtraData();
		if(extraData == null){
			extraData = MainApplication.getAppContext().getPackageName().getBytes();
		}
		AndroidApplicationRecord androidApplicationRecord = new AndroidApplicationRecord(extraData);
		return androidApplicationRecord.getNdefRecord();
	}

}
