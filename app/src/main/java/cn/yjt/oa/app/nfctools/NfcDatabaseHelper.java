package cn.yjt.oa.app.nfctools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NfcDatabaseHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "nfc";
	private static final int VERSION = 1;
	
	public NfcDatabaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE record(_id INTEGER PRIMARY KEY AUTOINCREMENT,data BLOB)";
		
		db.execSQL(sql );
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
