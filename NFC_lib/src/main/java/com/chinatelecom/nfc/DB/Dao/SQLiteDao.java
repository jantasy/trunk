package com.chinatelecom.nfc.DB.Dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;

//ashley 0923 删除无用资源
//import com.chinatelecom.nfc.DB.Pojo.ParkTicket;
//import com.chinatelecom.nfc.DB.Provider.AdTable;
import com.chinatelecom.nfc.DB.Provider.DBUtils;
//ashley 0923 删除无用资源
//import com.chinatelecom.nfc.DB.Provider.MovieTicketTable;
import com.chinatelecom.nfc.DB.Provider.MyDataTable;
//import com.chinatelecom.nfc.DB.Provider.LotteryTable;
import com.chinatelecom.nfc.DB.Provider.MeetingTable;
import com.chinatelecom.nfc.DB.Provider.MyModeTable;
//import com.chinatelecom.nfc.DB.Provider.CouponTable;
//import com.chinatelecom.nfc.DB.Provider.ParkTicketTable;

public class SQLiteDao {
	protected  static DBUtils mDataBase = null;
	protected static Context context;
//	private static SQLiteDao instance = null;
//	public synchronized static SQLiteDao getInstance(Context context){
//		if(instance == null){
//			instance = new SQLiteDao(context);
//		}
//		return instance;
//	}
	
	public SQLiteDao(Context context) {
		super();
		this.context = context;
		if(mDataBase == null){
			mDataBase = new DBUtils(context);
		}
	}

	/**
	 * 得到DBUtils
	 * @return DBUtils
	 */
	public static DBUtils getDataBase(Context context){
		if(mDataBase == null){
			mDataBase = new DBUtils(context);
		}
		return mDataBase;
	}
	
	/** 
	 * 删除数据库所有表数据
	 */
	public static void clearTables() {
		mDataBase.delete(MyDataTable.URI,null,null);
		//ashley 0923 删除无用资源
//		mDataBase.delete(AdTable.URI,null,null);
//		mDataBase.delete(LotteryTable.URI,null,null);
		mDataBase.delete(MeetingTable.URI,null,null);
		mDataBase.delete(MyModeTable.URI,null,null);
//		mDataBase.delete(CouponTable.URI,null,null);
//		mDataBase.delete(ParkTicketTable.URI,null,null);
//		mDataBase.delete(MovieTicketTable.URI,null,null);
	}
	
	/**
	 * 需要手动打开连接，关闭连接
	 * @param table
	 */
	public void clearTable(String table){
		mDataBase.delete(table,null,null);
	}
	
	/**
	 * 需要手动打开连接，关闭连接
	 * @param table
	 * @param contentValues
	 */
	public void saveData(String table,ArrayList<ContentValues> contentValues){
		// 插入数据
		mDataBase.insertData(table, contentValues);
	}
	
	/**
	 * 需要手动打开连接，关闭连接
	 * @param table
	 * @param contentValue
	 */
	public static long saveData(String table,ContentValues contentValue){
		// 插入数据
		return mDataBase.insertData(table, contentValue);
	}
}
