package cn.yjt.oa.app.beacon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cn.yjt.oa.app.MainApplication;

public class DecoderDatabase extends SQLiteOpenHelper {

	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "decoder.db";

	private static DecoderDatabase database;

	public DecoderDatabase(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	public static synchronized DecoderDatabase getInstanceFromApplication() {
		if (database == null) {
			database = new DecoderDatabase(MainApplication.getAppContext());
		}
		return database;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE decoder(uumm VARCHAR,decoded_uumm VARCHAR UNIQUE);";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public String getDecodedUumm(String uumm) {
		Cursor cursor = getReadableDatabase().query("decoder",
				new String[] { "decoded_uumm" }, "uumm=?",
				new String[] { uumm }, null, null, null);
		String decodedUumm = null;
		if (cursor != null) {
			if (cursor.moveToNext()) {
				decodedUumm = cursor.getString(0);
			}
			cursor.close();
		}

		return decodedUumm;
	}

	public void putUumm(String uumm, String decodedUumm) {
		if (containsUumm(decodedUumm)) {
			updateUumm(uumm, decodedUumm);
		} else {
			insertUumm(uumm, decodedUumm);
		}
	}

	private void insertUumm(String uumm, String decodedUumm) {
		ContentValues values = buildContentValues(uumm, decodedUumm);
		getWritableDatabase().insert("decoder", null, values);
	}

	private void updateUumm(String uumm, String decodedUumm) {
		ContentValues values = buildContentValues(uumm, decodedUumm);
		getWritableDatabase().update("decoder", values, "decoded_uumm=?",
				new String[] { decodedUumm });
	}

	private ContentValues buildContentValues(String uumm, String decodedUumm) {
		ContentValues values = new ContentValues();
		values.put("uumm", uumm);
		values.put("decoded_uumm", decodedUumm);
		return values;
	}

	private boolean containsUumm(String uumm) {
		Cursor cursor = getReadableDatabase().query("decoder",
				new String[] { "uumm" }, "decoded_uumm=?",
				new String[] { uumm }, null, null, null);
		try {

			if (cursor != null && cursor.getCount() != 0) {
				return true;
			}
			return false;
		} finally {
			if(cursor != null){
				cursor.close();
			}
		}
	}
}
