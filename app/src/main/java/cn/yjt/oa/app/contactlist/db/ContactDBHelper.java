package cn.yjt.oa.app.contactlist.db;

import com.activeandroid.util.Log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.beans.UserInfo;

/**联系人数据库helper类*/
public class ContactDBHelper extends SQLiteOpenHelper {

	private final static String TAG = "ContactDBHelper";

	public ContactDBHelper(Context context) {
		super(context, getDBName(context), null, DB.DB_VERSION);
	}

	public ContactDBHelper(Context context, String dbName) {
		super(context, dbName, null, DB.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(DB.SQL_CONTACT);
		} catch (Exception e) {
			// ignore
		}
		try {
			db.execSQL(DB.SQL_GROUP);
		} catch (Exception e) {
			// ignore
		}
		try {
			db.execSQL(DB.SQL_CONTACT_UNREGISTER);
		} catch (Exception e) {
			// ignore
		}
		try {
			db.execSQL(DB.SQL_PUBLIC_SERVICE);
		} catch (Exception e) {
			// ignore
		}
		try {
			db.execSQL(DB.SQL_DEPT);
		} catch (Exception e) {
			// ignore
		}
		try {
			db.execSQL(DB.SQL_DEPT_USER);
		} catch (Exception e) {
			// ignore
		}

		try {
			db.execSQL(DB.SQL_DEFT_USER_UNREGISTER);
		} catch (Exception e) {

		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion == 1){
			try {
				db.execSQL(DB.SQL_DEPT);
			} catch (Exception e) {
				// ignore
			}
			try {
				db.execSQL(DB.SQL_DEPT_USER);
			} catch (Exception e) {
				// ignore
			}
		}
		if(oldVersion<=4){
			try {
				db.execSQL(DB.ALFTER_SQL_DEPT_USER_ORDER_INDEX);	
				Log.e(TAG, "更新数据库");
			} catch (Exception e) {
				Log.e(TAG, "更新异常1");
			}
			try {
				db.execSQL(DB.ALFTER_SQL_DEPT_USER_STATUS);
				Log.e(TAG, "更新数据库");
			} catch (Exception e) {
				Log.e(TAG, "更新异常2");
			}
			try {
				db.execSQL(DB.ALFTER_SQL_DEPT_USER_STATUS_DESC);
				Log.e(TAG, "更新数据库");
			} catch (Exception e) {
				Log.e(TAG, "更新异常3");
			}
		}
		if(oldVersion<=5){
			try {
				db.execSQL(DB.SQL_DEFT_USER_UNREGISTER);
			} catch (Exception e) {
				
			}
		}
		if(oldVersion<=6){
			try {
				db.execSQL(DB.ALFTER_SQL_DEPT);
			} catch (Exception e) {

			}
		}
	}

	private static String getDBName(Context context) {
		UserInfo current = AccountManager.getCurrent(context);
		if (current != null) {
			return DB.NAME + current.getId();
		} else {
			return "None";
		}
	}

	interface DB {
		public static final String DB_NAME = "yjt_contacts";
		public static final int DB_VERSION = 7;
		public static final String TABLE_CONTACT = "contact_table";
		public static final String TABLE_CONTACT_UNREGISTER = "contact_unregister_table";
		public static final String TABLE_GROUP = "group_table";
		public static final String TABLE_PUBLIC_SERVICE = "public_service_table";
		public static final String TABLE_DEPT = "dept";
		public static final String TABLE_DEPT_USER = "dept_user";
		public static final String TABLE_DEFT_USER_UNREGISTER = "deft_user_unregister";

		public static final String ID = "id";
		public static final String USERID = "user_id";
		public static final String NAME = "name";
		public static final String SEX = "sex";
		public static final String AVATAR = "avatar";
		public static final String USER_CODE = "user_code";
		public static final String CUSTID = "cust_id";
		public static final String CUSTNAME = "cust_name";
		public static final String EMAIL = "email";
		public static final String PHONE = "phone";
		public static final String PHONE2 = "phone2";
		public static final String PHONE3 = "phone3";
		public static final String TEL = "tel";
		public static final String DEPARTMENT = "department";
		public static final String POSITION = "position";
		public static final String ADDRESS = "address";
		public static final String REGISTERTIME = "registerTime";
		public static final String GROUP_ID = "group_id";
		public static final String DESCRIPTION = "description";
		public static final String CREATE_TIME = "createtime";
		public static final String UPDATE_TIME = "updatetime";
		public static final String GROUP_CONTACTS = "group_contacts";
		public static final String URL = "url";

		public static final String PARENT_ID = "parent_id";
		public static final String DEPT_ID = "parent_id";
		public static final String STATUS = "status";
		public static final String STATUS_DESC = "status_desc";

		public static final String ORDER_INDEX = "order_index";

		public static final String SQL_CONTACT = "CREATE TABLE " + TABLE_CONTACT + " (" + USERID
				+ " INTEGER PRIMARY KEY UNIQUE , " + NAME + " VARCHAR default null , " + SEX + " INTEGER default 2 , "
				+ AVATAR + " VARCHAR default null , " + USER_CODE + " VARCHAR default null , " + CUSTID
				+ " VARCHAR default null , " + CUSTNAME + " VARCHAR default null , " + EMAIL
				+ " VARCHAR default null , " + PHONE + " VARCHAR default null , " + TEL + " VARCHAR default null , "
				+ DEPARTMENT + " VARCHAR default null , " + POSITION + " VARCHAR default null , " + ADDRESS
				+ " VARCHAR default null , " + REGISTERTIME + " VARCHAR default null )";
		public static final String SQL_CONTACT_UNREGISTER = "CREATE TABLE " + TABLE_CONTACT_UNREGISTER + " (" + NAME
				+ " VARCHAR default null , " + SEX + " INTEGER default 2 , " + AVATAR + " VARCHAR default null , "
				+ USER_CODE + " VARCHAR default null , " + CUSTID + " VARCHAR default null , " + CUSTNAME
				+ " VARCHAR default null , " + EMAIL + " VARCHAR default null , " + PHONE
				+ " VARCHAR PRIMARY KEY NOT NULL  UNIQUE , " + TEL + " VARCHAR default null , " + DEPARTMENT
				+ " VARCHAR default null , " + POSITION + " VARCHAR default null , " + ADDRESS
				+ " VARCHAR default null , " + REGISTERTIME + " VARCHAR default null )";

		public static final String SQL_GROUP = "CREATE TABLE " + TABLE_GROUP + " (" + GROUP_ID
				+ " INTEGER PRIMARY KEY NOT NULL  UNIQUE , " + NAME + " VARCHAR default null , " + CREATE_TIME
				+ " VARCHAR default null , " + UPDATE_TIME + " VARCHAR default null , " + DB.GROUP_CONTACTS
				+ " VARCHAR default null , " + AVATAR + " VARCHAR default null , " + DESCRIPTION
				+ " VARCHAR default null)";

		public static final String SQL_PUBLIC_SERVICE = "CREATE TABLE " + TABLE_PUBLIC_SERVICE + " (" + DB.ID
				+ " INTEGER PRIMARY KEY UNIQUE , " + DB.NAME + " VARCHAR default null , " + DB.DESCRIPTION
				+ " VARCHAR default null , " + DB.PHONE + " VARCHAR default null , " + DB.PHONE2
				+ " VARCHAR default null , " + DB.PHONE3 + " VARCHAR default null , " + DB.URL
				+ " VARCHAR default null , " + DB.CREATE_TIME + " VARCHAR default null )";

		public static final String SQL_DEPT = "CREATE TABLE " + TABLE_DEPT + " (" + DB.ID
				+ " INTEGER PRIMARY KEY UNIQUE , " + DB.NAME + " VARCHAR default nulL , " + DB.PARENT_ID + " INTEGER , " + DB.ORDER_INDEX
				+ " VARCHAR default null )";

		//TODO:
		public static final String SQL_DEPT_USER = "CREATE TABLE " + TABLE_DEPT_USER + " (" + DB.ID
				+ " INTEGER PRIMARY KEY UNIQUE , " + DB.USERID + " INTEGER , " + DB.POSITION
				+ " VARCHAR default nulL , " + DB.DEPT_ID + " INTEGER , " + DB.ORDER_INDEX + " INTEGER ," + DB.STATUS
				+ " INTEGER , " + DB.STATUS_DESC + " VARCHAR default null)";

		public static final String ALFTER_SQL_DEPT_USER_ORDER_INDEX = "ALTER TABLE " + TABLE_DEPT_USER + " ADD "
				+ DB.ORDER_INDEX + " INTEGER ";

		public static final String ALFTER_SQL_DEPT_USER_STATUS = "ALTER TABLE " + TABLE_DEPT_USER + " ADD " + DB.STATUS
				+ " INTEGER ";

		public static final String ALFTER_SQL_DEPT_USER_STATUS_DESC = "ALTER TABLE " + TABLE_DEPT_USER + " ADD "
				+ DB.STATUS_DESC + " VARCHAR default null ";

		public static final String SQL_DEFT_USER_UNREGISTER = "CREATE TABLE " + TABLE_DEFT_USER_UNREGISTER + " ("
				+ DB.ID + " INTEGER PRIMARY KEY UNIQUE , " + DB.USERID + " INTEGER , " + DB.POSITION
				+ " VARCHAR default nulL , " + DB.DEPT_ID + " INTEGER , " + DB.NAME + " VARCHAR default null , "
				+ DB.EMAIL + " VARCHAR default null , " + DB.PHONE + " VARCHAR default null , " + DB.TEL
				+ " VARCHAR default null , " + DB.SEX + " INTEGER default 2 , " + DB.ORDER_INDEX + " INTEGER , "
				+ DB.STATUS + " INTEGER , " + DB.STATUS_DESC + " VARCHAR default null)";
		
		public static final String ALFTER_SQL_DEPT = "ALTER TABLE " + DB.TABLE_DEPT + " ADD " + DB.ORDER_INDEX
				+ " VARCHAR default null ";
	}
}
