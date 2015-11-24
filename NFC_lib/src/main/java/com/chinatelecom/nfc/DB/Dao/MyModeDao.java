package com.chinatelecom.nfc.DB.Dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.chinatelecom.nfc.DB.Pojo.MyMode;
import com.chinatelecom.nfc.DB.Provider.MeetingTable;
import com.chinatelecom.nfc.DB.Provider.MyModeTable;
import com.chinatelecom.nfc.DB.Provider.SettingData;

public class MyModeDao extends SQLiteDao{
	private final static String table = MyModeTable.URI;
	public MyModeDao(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public static long insert(Context context,MyMode mymode,boolean isNeedOpenDB) {
		if(mymode == null){
			return SettingData.INSERT_ERR;
		}
		long id = SettingData.INSERT_ERR;
		try {
			if(isNeedOpenDB){
				getDataBase(context).open();
			}
			ContentValues c;
				c = new ContentValues();
				c.put(MyModeTable.NAME,mymode.name);
				c.put(MyModeTable.MUTEMODE,mymode.muteMode);
				c.put(MyModeTable.WIFISWITCH,mymode.wifiSwitch);
				c.put(MyModeTable.DIGITALSWITCH,mymode.digitalSwitch);
				c.put(MyModeTable.BLUETOOTH,mymode.bluetooth);
				c.put(MyModeTable.WIFISSID,mymode.wifiSSID);
				c.put(MyModeTable.WIFIPASSWORD,mymode.wifiPassword);
				c.put(MyModeTable.CONTENT,mymode.content);
				c.put(MyModeTable.MODEFLAG,mymode.modeFlag);
				
				id = saveData(table, c);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			
		}finally{
			if(isNeedOpenDB){
				getDataBase(context).close();
			}
		}
		return id;
	}
	
//	public static List<MyMode> getData(Context context,boolean isNeedOpenDB) {
//		// TODO Auto-generated method stub
//		List<MyMode> datas = new ArrayList<MyMode>();
//		try {
//			if(isNeedOpenDB){
//				getDataBase(context).open();
//			}
//			Cursor c = getDataBase(context).query(table);
//			if (c!=null){
//				int cnt = c.getCount();
//				c.moveToFirst();
//				for(int i=0; i<cnt; i++){
////					Integer id = c.getInt(c.getColumnIndex(EnvironmentsTable.ENVIR_ELEMENTS));
//					
//					MyMode data = new MyMode(
//							c.getInt(c.getColumnIndex(MyModeTable.ID)),
//							c.getString(c.getColumnIndex(MyModeTable.NAME)),
//							c.getInt(c.getColumnIndex(MyModeTable.MUTEMODE)),
//							c.getInt(c.getColumnIndex(MyModeTable.VIBRATIONMODE)),
//							c.getInt(c.getColumnIndex(MyModeTable.WIFISWITCH)),
//							c.getInt(c.getColumnIndex(MyModeTable.DIGITALSWITCH)),
//							c.getInt(c.getColumnIndex(MyModeTable.AIRPLANEMODE)),
//							c.getInt(c.getColumnIndex(MyModeTable.BLUETOOTH)),
//							c.getString(c.getColumnIndex(MyModeTable.WIFISSID)),
//							c.getString(c.getColumnIndex(MyModeTable.WIFIPASSWORD)),
//							c.getString(c.getColumnIndex(MyModeTable.CONTENT)),
//							c.getInt(c.getColumnIndex(MyModeTable.MODEFLAG))
//					);
//					
//					List<MyMode> listE = MyModeDao.query(context);
//					datas.add(data);
//					c.moveToNext();
//				}
//				c.close();
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//			
//		}finally{
//			if(isNeedOpenDB){
//				getDataBase(context).close();
//			}
//		}
//		return datas;
//	}

	public static List<MyMode> query(Context context,Integer id,Integer modeflag,String orderBy, String limit,boolean isNeedOpenDB) {
		// TODO Auto-generated method stub
		List<MyMode> datas = new ArrayList<MyMode>();
		try {
			if (isNeedOpenDB) {
				getDataBase(context).open();
			}
			String[] columns = new String[]{
					MyModeTable.ID,
					MyModeTable.NAME,
					MyModeTable.MUTEMODE,
					MyModeTable.WIFISWITCH,
					MyModeTable.DIGITALSWITCH,
					MyModeTable.BLUETOOTH,
					MyModeTable.WIFISSID,
					MyModeTable.WIFIPASSWORD,
					MyModeTable.CONTENT,
					MyModeTable.MODEFLAG
			};
			String selection ="";
			List<String> selectionArgs = new ArrayList<String>();
			if (id != null) {
				if (!selection.equals("")) {
					selection += " and ";
				}
				selection += MyModeTable.ID + "=?";
				selectionArgs.add(String.valueOf(id));
			}
			if (modeflag != null) {
				if (!selection.equals("")) {
					selection += " and ";
				}
				selection += MyModeTable.MODEFLAG + "=?";
				selectionArgs.add(String.valueOf(modeflag));
			}
			Cursor c = getDataBase(context).qurey(table, columns, selection, (String[])selectionArgs.toArray(new String[0]), null, null, orderBy,limit);
			
			if (c != null) {
				int cnt = c.getCount();
				c.moveToFirst();
				for (int i = 0; i < cnt; i++) {
					MyMode data = new MyMode(
							c.getInt(c.getColumnIndex(MyModeTable.ID)),
							c.getString(c.getColumnIndex(MyModeTable.NAME)),
							c.getInt(c.getColumnIndex(MyModeTable.MUTEMODE)),
							c.getInt(c.getColumnIndex(MyModeTable.WIFISWITCH)),
							c.getInt(c.getColumnIndex(MyModeTable.DIGITALSWITCH)),
							c.getInt(c.getColumnIndex(MyModeTable.BLUETOOTH)),
							c.getString(c.getColumnIndex(MyModeTable.WIFISSID)),
							c.getString(c.getColumnIndex(MyModeTable.WIFIPASSWORD)),
							c.getString(c.getColumnIndex(MyModeTable.CONTENT)),
							c.getInt(c.getColumnIndex(MyModeTable.MODEFLAG))
							);
					datas.add(data);
					c.moveToNext();
				}
				c.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		} finally {
			if (isNeedOpenDB) {
				getDataBase(context).close();
			}
		}
		return datas;
	}
	
	public static int update(Context context,MyMode mymode,boolean isNeedOpenDB) {
		if(mymode == null){
			return SettingData.INSERT_ERR;
		}
		int statement = SettingData.INSERT_ERR;
		try {
			if(isNeedOpenDB){
				getDataBase(context).open();
			}
			ContentValues c;
				c = new ContentValues();
				c.put(MyModeTable.ID,mymode.id);
				c.put(MyModeTable.NAME,mymode.name);
				c.put(MyModeTable.MUTEMODE,mymode.muteMode);
				c.put(MyModeTable.WIFISWITCH,mymode.wifiSwitch);
				c.put(MyModeTable.DIGITALSWITCH,mymode.digitalSwitch);
				c.put(MyModeTable.BLUETOOTH,mymode.bluetooth);
				c.put(MyModeTable.WIFISSID,mymode.wifiSSID);
				c.put(MyModeTable.WIFIPASSWORD,mymode.wifiPassword);
				c.put(MyModeTable.CONTENT,mymode.content);
				c.put(MyModeTable.MODEFLAG,mymode.modeFlag);
				
				String selection ="";
				List<String> selectionArgs = new ArrayList<String>();
				
				if (mymode.id != null) {
					if (!selection.equals("")) {
						selection += " and ";
					}
					selection += MyModeTable.ID + "=?";
					selectionArgs.add(String.valueOf(mymode.id));
				}
				statement = getDataBase(context).update(table, c, selection, (String[])selectionArgs.toArray(new String[0]));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			
		}finally{
			if(isNeedOpenDB){
				getDataBase(context).close();
			}
		}
		return statement;
	}
	
}
