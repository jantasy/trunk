package cn.yjt.oa.app.nfctools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class NfcTagRecordDaoImpl implements NfcTagRecordDao {
	private NfcDatabaseHelper databaseHelper;

	@Override
	public List<NfcTagOperationRecord> getNfcTagOperationRecords(Context context) {
		checkDatabaseOpen(context);
		Cursor cursor = null;
		try {
			cursor = databaseHelper.getReadableDatabase().query("record",
					new String[] {"_id", "data" }, null, null, null, null, null);
			if (cursor != null) {
				List<NfcTagOperationRecord> records = new ArrayList<NfcTagOperationRecord>();
				while (cursor.moveToNext()) {
					byte[] blob = cursor.getBlob(1);
					System.out.println("id:"+cursor.getInt(0));
					System.out.println("blob:"+Arrays.toString(blob));
					NfcTagOperationRecord record = NfcTagOperationRecord.parseRecord(blob);
					record.setId(cursor.getInt(0));
					records.add(record);
				}
				return records;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	@Override
	public long addNfcTagOperationRecord(NfcTagOperationRecord record,
			Context context) {
		System.out.println("addNfcTagOperationRecord:"+record);
		checkDatabaseOpen(context);
		ContentValues values = new ContentValues();
		values.put("data", record.getData());
		return databaseHelper.getWritableDatabase().insert("record", null, values );
	}
	

	@Override
	public int deleteNfcTagoperationRecord(long id,Context context) {
		checkDatabaseOpen(context);
		return databaseHelper.getWritableDatabase().delete("record", "_id=?", new String[]{String.valueOf(id)});
	}
	

	private void checkDatabaseOpen(Context context) {
		if (databaseHelper == null) {
			databaseHelper = new NfcDatabaseHelper(context);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		if(databaseHelper != null){
			databaseHelper.close();
		}
		super.finalize();
	}

}
