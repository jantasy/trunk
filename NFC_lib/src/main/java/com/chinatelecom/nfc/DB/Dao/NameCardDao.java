package com.chinatelecom.nfc.DB.Dao;

import java.util.ArrayList;
import java.util.List;

import com.chinatelecom.nfc.DB.Pojo.NameCard;
import com.chinatelecom.nfc.DB.Provider.NameCardTable;
import com.chinatelecom.nfc.DB.Provider.SettingData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class NameCardDao extends SQLiteDao{

	public NameCardDao(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	private final static String table = NameCardTable.URI;

	
	public static long insert(Context context, NameCard  namecard,boolean isNeedOpenDB) {
		if(namecard == null){
			return SettingData.INSERT_ERR;
		}
		long id = SettingData.INSERT_ERR;
		try {
			if(isNeedOpenDB){
				getDataBase(context).open();
			}
			ContentValues c;
				c = new ContentValues();
				c.put(NameCardTable.NAME,namecard.getName());
				c.put(NameCardTable.CONTACTICON,namecard.getContactIcon());
				c.put(NameCardTable.TELPHONE,namecard.getTelPhone());
				c.put(NameCardTable.FAX,namecard.getFax());
				c.put(NameCardTable.COMPANYNAME,namecard.getCompanyName());
				c.put(NameCardTable.COMPANYNETADDRESS,namecard.getCompanyNetAddress());
				c.put(NameCardTable.COMPANYPHONE,namecard.getCompanyTelPhone());
				c.put(NameCardTable.SECTION,namecard.getSection());
				c.put(NameCardTable.RANK,namecard.getRank());
				c.put(NameCardTable.ADDRESS,namecard.getAddress());
				c.put(NameCardTable.EMAIL,namecard.getEmail());
				c.put(NameCardTable.DESCRIPTION,namecard.getDescription());
				c.put(NameCardTable.SHOWFLAG,namecard.getShowFlag());
				c.put(NameCardTable.SHORTCUT, namecard.getShortCut());
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
	public static List<NameCard> query(Context context, String name,boolean isNeedOpenDB){
		List<NameCard> datas = new ArrayList<NameCard>();
		try	{
			if(isNeedOpenDB){
				getDataBase(context).open();
			}
			String[] columns = new String[]{
					NameCardTable.ID,
					NameCardTable.NAME,
					NameCardTable.CONTACTICON,
					NameCardTable.TELPHONE,
					NameCardTable.FAX,
					NameCardTable.COMPANYNAME,
					NameCardTable.COMPANYNETADDRESS,
					NameCardTable.COMPANYPHONE,
					NameCardTable.SECTION,
					NameCardTable.RANK,
					NameCardTable.ADDRESS,
					NameCardTable.EMAIL,
					NameCardTable.DESCRIPTION,
					NameCardTable.SHOWFLAG,
					NameCardTable.SHORTCUT
			};
			String selection = NameCardTable.NAME + "=?";
			List<String> selectionArgs = new ArrayList<String>();
			selectionArgs.add(name);
			Cursor c = getDataBase(context).qurey(table,columns, selection, (String[])selectionArgs.toArray(new String[0]), null, null, null,null);
			if(c != null){
				c.moveToFirst();
				for(int i= 0; i<=c.getCount(); i++){
					NameCard tmp = new NameCard(
							c.getInt(c.getColumnIndex(NameCardTable.ID)),
							c.getString(c.getColumnIndex(NameCardTable.NAME)),
							c.getBlob(c.getColumnIndex(NameCardTable.CONTACTICON)),
							c.getString(c.getColumnIndex(NameCardTable.TELPHONE)),
							c.getString(c.getColumnIndex(NameCardTable.FAX)),
							c.getString(c.getColumnIndex(NameCardTable.COMPANYNAME)),
							c.getString(c.getColumnIndex(NameCardTable.COMPANYNETADDRESS)),
							c.getString(c.getColumnIndex(NameCardTable.COMPANYPHONE)),
							c.getString(c.getColumnIndex(NameCardTable.SECTION)),
							c.getString(c.getColumnIndex(NameCardTable.RANK)),
							c.getString(c.getColumnIndex(NameCardTable.ADDRESS)),
							c.getString(c.getColumnIndex(NameCardTable.EMAIL)),
							c.getString(c.getColumnIndex(NameCardTable.DESCRIPTION)),
							c.getShort(c.getColumnIndex(NameCardTable.SHOWFLAG)),
							c.getInt(c.getColumnIndex(NameCardTable.SHORTCUT)));
					datas.add(tmp);
					c.moveToNext();
				}
				c.close();
			}
				
		}catch (Exception e) {
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
	 * @param isNeedOpenDB
	 * @return
	 */
	public static List<NameCard> query(Context context,Integer id,boolean isNeedOpenDB) {
		// TODO Auto-generated method stub
		List<NameCard> datas = new ArrayList<NameCard>();
		try {
			if (isNeedOpenDB) {
				getDataBase(context).open();
			}
			String[] columns = new String[]{
					NameCardTable.ID,
					NameCardTable.NAME,
					NameCardTable.CONTACTICON,
					NameCardTable.TELPHONE,
					NameCardTable.FAX,
					NameCardTable.COMPANYNAME,
					NameCardTable.COMPANYNETADDRESS,
					NameCardTable.COMPANYPHONE,
					NameCardTable.SECTION,
					NameCardTable.RANK,
					NameCardTable.ADDRESS,
					NameCardTable.EMAIL,
					NameCardTable.DESCRIPTION,
					NameCardTable.SHOWFLAG,
					NameCardTable.SHORTCUT
			};
			String selection ="";
			List<String> selectionArgs = new ArrayList<String>();
			if (id != null) {
				if (!selection.equals("")) {
					selection += " and ";
				}
				selection += NameCardTable.ID + "=?";
				selectionArgs.add(String.valueOf(id));
			}
			
			Cursor c = getDataBase(context).qurey(table, columns, selection, (String[])selectionArgs.toArray(new String[0]), null, null, null,null);
			
			if (c != null) {
				int cnt = c.getCount();
				c.moveToFirst();
				for (int i = 0; i < cnt; i++) {
					NameCard data = new NameCard(
							c.getInt(c.getColumnIndex(NameCardTable.ID)),
							c.getString(c.getColumnIndex(NameCardTable.NAME)),
							c.getBlob(c.getColumnIndex(NameCardTable.CONTACTICON)),
							c.getString(c.getColumnIndex(NameCardTable.TELPHONE)),
							c.getString(c.getColumnIndex(NameCardTable.FAX)),
							c.getString(c.getColumnIndex(NameCardTable.COMPANYNAME)),
							c.getString(c.getColumnIndex(NameCardTable.COMPANYNETADDRESS)),
							c.getString(c.getColumnIndex(NameCardTable.COMPANYPHONE)),
							c.getString(c.getColumnIndex(NameCardTable.SECTION)),
							c.getString(c.getColumnIndex(NameCardTable.RANK)),
							c.getString(c.getColumnIndex(NameCardTable.ADDRESS)),
							c.getString(c.getColumnIndex(NameCardTable.EMAIL)),
							c.getString(c.getColumnIndex(NameCardTable.DESCRIPTION)),
							c.getShort(c.getColumnIndex(NameCardTable.SHOWFLAG)),
							c.getInt(c.getColumnIndex(NameCardTable.SHORTCUT)));
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
	public static int update(Context context,NameCard namecard,boolean isNeedOpenDB) {
		if(namecard == null){
			return SettingData.INSERT_ERR;
		}
		int statement = SettingData.INSERT_ERR;
		try {
			if(isNeedOpenDB){
				getDataBase(context).open();
			}
			ContentValues c;
			c = new ContentValues();
			c.put(NameCardTable.ID,namecard.getId());
			c.put(NameCardTable.NAME,namecard.getName());
			c.put(NameCardTable.CONTACTICON,namecard.getContactIcon());
			c.put(NameCardTable.TELPHONE,namecard.getTelPhone());
			c.put(NameCardTable.FAX,namecard.getFax());
			c.put(NameCardTable.COMPANYNAME,namecard.getCompanyName());
			c.put(NameCardTable.COMPANYNETADDRESS,namecard.getCompanyNetAddress());
			c.put(NameCardTable.COMPANYPHONE,namecard.getCompanyTelPhone());
			c.put(NameCardTable.SECTION,namecard.getSection());
			c.put(NameCardTable.RANK,namecard.getRank());
			c.put(NameCardTable.ADDRESS,namecard.getAddress());
			c.put(NameCardTable.EMAIL,namecard.getEmail());
			c.put(NameCardTable.DESCRIPTION,namecard.getDescription());
			c.put(NameCardTable.SHOWFLAG,namecard.getShowFlag());
			c.put(NameCardTable.SHORTCUT,namecard.getShortCut());
			
			String selection ="";
			List<String> selectionArgs = new ArrayList<String>();
			
			if (namecard.getId() != null) {
				if (!selection.equals("")) {
					selection += " and ";
				}
				selection += NameCardTable.ID + "=?";
				selectionArgs.add(String.valueOf(namecard.getId()));
			}
			statement = getDataBase(context).update(table, c, selection, selectionArgs.toArray(new String[0]));
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
