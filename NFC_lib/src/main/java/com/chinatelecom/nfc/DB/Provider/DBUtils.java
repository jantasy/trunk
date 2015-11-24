package com.chinatelecom.nfc.DB.Provider;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
public class DBUtils {

	private final String TAG = "DBUtils";
	private Context context;

	public DatabaseHelper mDBHelper;
	public SQLiteDatabase sQLiteDatabase;

	public DBUtils(Context context) {
		this.context = context;
		mDBHelper = new DatabaseHelper(context);
	}
	/**
	 * 
	 */
	public void open() {
		synchronized(this){
			try {
				mDBHelper = new DatabaseHelper(context);
				sQLiteDatabase = mDBHelper.getWritableDatabase();
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, "发生SQLException!!------->open()");
			}
		}
	}
	/**
	 * 
	 */
	public void close() {
		synchronized(this){
			try {
				sQLiteDatabase.close();
				mDBHelper.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				Log.e(TAG, "发生SQLException!!------->close()");
			}
		}

	}

	/**
	 *  增加数据
	 * @param tablename
	 * @param contentValues
	 */
	public void insertData(String tablename,
			ArrayList<ContentValues> contentValues) {
		try {
			if (contentValues.size() > 0) {
				for (ContentValues contentValue : contentValues) {
					sQLiteDatabase.insert(tablename, null, contentValue);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 *  增加数据
	 * @param tablename
	 * @param contentValues
	 */
	public long insertData(String tablename, ContentValues contentValue) {
		return	sQLiteDatabase.insert(tablename, null, contentValue);
	}
	
	/**
	 *  删除数据
	 * @param tablename
	 * @return
	 */
	public int delete(String table, String whereClause, String[] whereArgs) {
		
		return sQLiteDatabase.delete(table, whereClause, whereArgs);
	};

	/**
	 *  查询数据
	 * @param tablename
	 * @return
	 */
	public Cursor query(String tablename) {
		return sQLiteDatabase.query(tablename, null, null, null, null, null,
				null);
	}

	/**
	 * 
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return
	 */
	public Cursor qurey(String table, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy) {
		return sQLiteDatabase.query(table, columns, selection, selectionArgs,
				groupBy, having, orderBy);
	}
	
	/**
	 * 
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return
	 */
	public Cursor qurey(String table, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy,String limit) {
		return sQLiteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
	}
	
	/**
	 * 
	 * @param table
	 * @param values
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public int update(String table, ContentValues values,
			String selection, String[] selectionArgs) {
		return sQLiteDatabase.update(table, values, selection, selectionArgs);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	static Hashtable<String, TableBase> mUriTable = null;

	static class DatabaseHelper extends SQLiteOpenHelper {

		public final static String DATABASE_NAME = "com.chinatelecom.nfc";
		public final static int DATABASE_VERSION = 1;

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub

			if (db == null) {
				return;
			}
			getTables();
			Enumeration<TableBase> e = null;
			if (mUriTable == null) {
				return;
			}
			e = mUriTable.elements();
			if (e == null) {
				return;
			}
			while (e.hasMoreElements()) {
				TableBase table = (TableBase) e.nextElement();
				if (table != null) {
					db.execSQL(table.getCreateTableSQL());
					String[] indexSQLs = table.getCreateIndexSQL();
					if (indexSQLs != null) {
						for (int i = 0; i < indexSQLs.length; i++) {
							db.execSQL(indexSQLs[i]);
						}
					}
					table.insertInitialData(db);
				}
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			if (db == null) {
				return;
			}
			getTables();
			Enumeration<TableBase> e = null;
			if (mUriTable == null) {
				return;
			}
			e = mUriTable.elements();
			if (e == null) {
				return;
			}
			while (e.hasMoreElements()) {
				TableBase table = (TableBase) e.nextElement();
				if (table != null) {
					db.execSQL(table.getDropTableSQL());
					String[] indexSQLs = table.getDropIndexSQL();
					if (indexSQLs != null) {
						for (int i = 0; i < indexSQLs.length; i++) {
							db.execSQL(indexSQLs[i]);
						}
					}
				}
			}
			onCreate(db);
		}

		private void getTables() {
			if (mUriTable == null) {
				mUriTable = new Hashtable<String, TableBase>();

				MyDataTable environmentsTable = new MyDataTable();
				mUriTable.put(environmentsTable.getTableName(),
						environmentsTable);

				//ashley 0923 删除无用资源
//				AdTable adTable = new AdTable();
//				mUriTable.put(adTable.getTableName(),
//						adTable);
//				
//				LotteryTable lotteryTable = new LotteryTable();
//				mUriTable.put(lotteryTable.getTableName(),
//						lotteryTable);
				
				MeetingTable meetingTable = new MeetingTable();
				mUriTable.put(meetingTable.getTableName(),
						meetingTable);
				
				MyModeTable myModeTable = new MyModeTable();
				mUriTable.put(myModeTable.getTableName(),
						myModeTable);
				
				//ashley 0923 删除无用资源
//				CouponTable scheduleTable = new CouponTable();
//				mUriTable.put(scheduleTable.getTableName(),
//						scheduleTable);
				
				NameCardTable namecardTable = new NameCardTable();
				mUriTable.put(namecardTable.getTableName(),
						namecardTable);
				
				//ashley 0923 删除无用资源
//				MovieTicketTable movietickettable = new MovieTicketTable();
//				mUriTable.put(movietickettable.getTableName(),
//						movietickettable);
//				
//				ParkTicketTable parktickettable = new ParkTicketTable();
//				mUriTable.put(parktickettable.getTableName(),
//						parktickettable);
			}
		}
	}

}
