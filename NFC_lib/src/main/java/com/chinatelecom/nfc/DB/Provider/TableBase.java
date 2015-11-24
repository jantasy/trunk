package com.chinatelecom.nfc.DB.Provider;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;


public abstract class TableBase {
	public abstract String getTableName();

	public abstract String[][] getColumns();

	public  abstract String[][] getIndexs();

	public String getCreateTableSQL(){
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE IF NOT EXISTS ");
		sb.append(getTableName());
		sb.append(" (");
		String[][] columns = getColumns();
		if (columns == null){
			return "";
		}
		for(int i=0; i<columns.length; i++){
			String[] column = columns[i];
			sb.append(column[0]);
			sb.append(" ");
			sb.append(column[1]);
			sb.append(",");
		}
		sb.delete(sb.length()-1, sb.length());
		sb.append(");");
		return sb.toString();
	}

	public String getDropTableSQL(){
		return "DROP TABLE IF EXISTS " + getTableName();
	}

	public ContentValues appendUpdateMarker(ContentValues values){
    	return values;
    }

	public ContentValues appendInsertMarker(ContentValues values){
		return values;
	}

	public String appendQuerySelection(){
		return null;
	}

	public ContentValues replaceDeleteValue(){
		return null;
	}

	public String[] getCreateIndexSQL() {
		String[][] indexname = getIndexs();
		if (indexname == null){
			return null;
		}
		String[] sqls = new String[indexname.length];
		for(int i=0; i<indexname.length; i++){
			StringBuffer sb = new StringBuffer();
			sb.append("CREATE INDEX IF NOT EXISTS ");
			sb.append(indexname[i][0]);
			sb.append(" ON ");
			sb.append(getTableName());
			sb.append(" (");
			sb.append(indexname[i][1]);
			sb.append(");");
			sqls[i] = sb.toString();
		}
		return sqls;
	}
	
	public String[] getDropIndexSQL() {
		String[][] indexname = getIndexs();
		if (indexname == null){
			return null;
		}
		String[] sqls = new String[indexname.length];
		for(int i=0; i<indexname.length; i++){
			StringBuffer sb = new StringBuffer();
			sb.append("DROP INDEX ");
			sb.append(indexname[i][0]);
			sb.append(";");
			sqls[i] = sb.toString();
		}
		return sqls;
	}
	
	public void insertInitialData(SQLiteDatabase db){
		return;
	}
}
