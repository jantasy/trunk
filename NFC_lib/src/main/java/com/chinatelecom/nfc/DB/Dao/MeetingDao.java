package com.chinatelecom.nfc.DB.Dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.chinatelecom.nfc.DB.Pojo.Meetting;
import com.chinatelecom.nfc.DB.Pojo.NameCard;
import com.chinatelecom.nfc.DB.Provider.MeetingTable;
import com.chinatelecom.nfc.DB.Provider.NameCardTable;
import com.chinatelecom.nfc.DB.Provider.SettingData;
import com.chinatelecom.nfc.Utils.MyUtil;

public class MeetingDao extends SQLiteDao{
	private final static String table = MeetingTable.URI;
	public MeetingDao(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	
	public static long insert(Context context, Meetting  meeting,boolean isNeedOpenDB) {
		if(meeting == null){
			return SettingData.INSERT_ERR;
		}
		long id = SettingData.INSERT_ERR;
		try {
			if(isNeedOpenDB){
				getDataBase(context).open();
			}
			ContentValues c;
				c = new ContentValues();
				c.put(MeetingTable.NAME,meeting.n);
				c.put(MeetingTable.PARTNER,meeting.p);
				c.put(MeetingTable.PLACE,meeting.pl);
				c.put(MeetingTable.CONTENT,meeting.c);
				c.put(MeetingTable.STARTTIME,meeting.st);
				c.put(MeetingTable.MODE,meeting.mm);
				c.put(MeetingTable.WIFISSID,meeting.ss);
				c.put(MeetingTable.WIFIPASSWORD,meeting.pw);
				
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
	public static List<Meetting> query(Context context, String name,boolean isNeedOpenDB){
		List<Meetting> datas = new ArrayList<Meetting>();
		try	{
			if(isNeedOpenDB){
				getDataBase(context).open();
			}
			String[] columns = new String[]{
					MeetingTable.ID,
					MeetingTable.NAME,
					MeetingTable.PARTNER,
					MeetingTable.PLACE,
					MeetingTable.CONTENT,
					MeetingTable.STARTTIME,
					MeetingTable.MODE,
					MeetingTable.WIFISSID,
					MeetingTable.WIFIPASSWORD,
			};
			String selection = MeetingTable.NAME + "=?";
			List<String> selectionArgs = new ArrayList<String>();
			selectionArgs.add(name);
			Cursor c = getDataBase(context).qurey(table,columns, selection, (String[])selectionArgs.toArray(new String[0]), null, null, null,null);
			if (c != null) {
				int cnt = c.getCount();
				c.moveToFirst();
				for (int i = 0; i < cnt; i++) {
					Meetting data = new Meetting(
							c.getInt(c.getColumnIndex(MeetingTable.ID)),
							c.getString(c.getColumnIndex(MeetingTable.NAME)),
							c.getString(c.getColumnIndex(MeetingTable.PARTNER)),
							c.getString(c.getColumnIndex(MeetingTable.PLACE)),
							c.getString(c.getColumnIndex(MeetingTable.CONTENT)),
							c.getLong(c.getColumnIndex(MeetingTable.STARTTIME)),
							c.getString(c.getColumnIndex(MeetingTable.MODE)),
							c.getString(c.getColumnIndex(MeetingTable.WIFISSID)),
							c.getString(c.getColumnIndex(MeetingTable.WIFIPASSWORD))
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
	/**
	 * 
	 * @param context
	 * @param id
	 * @param startTime 开始时间,-1
	 * @param endTime 结束时间,-1
	 * @param orderBy
	 * @param limit
	 * @param isNeedOpenDB
	 * @return
	 */
	public static List<Meetting> query(Context context,Integer id,long startTime,long endTime,String orderBy, String limit,boolean isNeedOpenDB) {
		// TODO Auto-generated method stub
		List<Meetting> datas = new ArrayList<Meetting>();
		try {
			if (isNeedOpenDB) {
				getDataBase(context).open();
			}
			String[] columns = new String[]{
					MeetingTable.ID,
					MeetingTable.NAME,
					MeetingTable.PARTNER,
					MeetingTable.PLACE,
					MeetingTable.CONTENT,
					MeetingTable.STARTTIME,
					MeetingTable.MODE,
					MeetingTable.WIFISSID,
					MeetingTable.WIFIPASSWORD,
			};
			String selection ="";
			List<String> selectionArgs = new ArrayList<String>();
			if (id != null) {
				if (!selection.equals("")) {
					selection += " and ";
				}
				selection += MeetingTable.ID + "=?";
				selectionArgs.add(String.valueOf(id));
			}
			if (startTime > 100 ) {
				if(endTime > 100){
					if (!selection.equals("")) {
						selection += " and ";
					}
					long _startTime = MyUtil.calendarToDay(startTime);
					selection += MeetingTable.STARTTIME + ">=?";
					selectionArgs.add(String.valueOf(_startTime));
					
					
					if (!selection.equals("")) {
						selection += " and ";
					}
					long _endTime = MyUtil.calendarToDay(endTime);
					selection += MeetingTable.STARTTIME + "<?";
					selectionArgs.add(String.valueOf((_endTime+SettingData.ONE_DAY)));
				}else{
					if (!selection.equals("")) {
						selection += " and ";
					}
					long _startTime = MyUtil.calendarToDay(startTime);
					selection += MeetingTable.STARTTIME + ">=?";
					selectionArgs.add(String.valueOf(_startTime));
					
					
					if (!selection.equals("")) {
						selection += " and ";
					}
					selection += MeetingTable.STARTTIME + "<?";
					selectionArgs.add(String.valueOf((_startTime+SettingData.ONE_DAY)));
				}
			}
			Cursor c = getDataBase(context).qurey(table, columns, selection, (String[])selectionArgs.toArray(new String[0]), null, null, orderBy,limit);
			
			if (c != null) {
				int cnt = c.getCount();
				c.moveToFirst();
				for (int i = 0; i < cnt; i++) {
					Meetting data = new Meetting(
							c.getInt(c.getColumnIndex(MeetingTable.ID)),
							c.getString(c.getColumnIndex(MeetingTable.NAME)),
							c.getString(c.getColumnIndex(MeetingTable.PARTNER)),
							c.getString(c.getColumnIndex(MeetingTable.PLACE)),
							c.getString(c.getColumnIndex(MeetingTable.CONTENT)),
							c.getLong(c.getColumnIndex(MeetingTable.STARTTIME)),
							c.getString(c.getColumnIndex(MeetingTable.MODE)),
							c.getString(c.getColumnIndex(MeetingTable.WIFISSID)),
							c.getString(c.getColumnIndex(MeetingTable.WIFIPASSWORD))
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
	
	public static int update(Context context, Meetting meeting, boolean isNeedOpenDB) {
		// TODO Auto-generated method stub
		if(meeting == null){
			return SettingData.INSERT_ERR;
		}
		int statement = SettingData.INSERT_ERR;
		try {
			if(isNeedOpenDB){
				getDataBase(context).open();
			}
			ContentValues c;
			c = new ContentValues();
			c.put(MeetingTable.ID,meeting.id);
			c.put(MeetingTable.NAME,meeting.n);
			c.put(MeetingTable.PARTNER,meeting.p);
			c.put(MeetingTable.PLACE,meeting.pl);
			c.put(MeetingTable.CONTENT,meeting.c);
			c.put(MeetingTable.STARTTIME,meeting.st);
			c.put(MeetingTable.MODE,meeting.mm);
			c.put(MeetingTable.WIFISSID,meeting.ss);
			c.put(MeetingTable.WIFIPASSWORD,meeting.pw);
			
			String selection ="";
			List<String> selectionArgs = new ArrayList<String>();
			
			if (meeting.id != null) {
				if (!selection.equals("")) {
					selection += " and ";
				}
				selection += MeetingTable.ID + "=?";
				selectionArgs.add(String.valueOf(meeting.id));
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
