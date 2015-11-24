package cn.yjt.oa.app.nfctools;

import java.util.List;

import android.content.Context;

public interface NfcTagRecordDao {

	public List<NfcTagOperationRecord> getNfcTagOperationRecords(Context context);

	public long addNfcTagOperationRecord(NfcTagOperationRecord record,Context context);
	
	public int deleteNfcTagoperationRecord(long id,Context context);
}