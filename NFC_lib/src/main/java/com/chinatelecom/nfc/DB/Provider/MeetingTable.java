package com.chinatelecom.nfc.DB.Provider;

public class MeetingTable extends TableBase {
	public static final String URI = "tt_meeting";

	public static final String[][] COLUMNS = new String[][] {
		new String[]{"id","INTEGER PRIMARY KEY AUTOINCREMENT"},
		new String[]{"name","TEXT"},
		new String[]{"partner","TEXT"},
		new String[]{"place","TEXT"},
		new String[]{"content","TEXT"},
		new String[]{"starttime","INTEGER NOT NULL"},
		new String[]{"mode","TEXT NOT NULL"},
		new String[]{"wifissid","TEXT"},
		new String[]{"wifipassword","TEXT"}
	};

	/** id column */
	public static final String ID = COLUMNS[0][0];
	public static final String NAME = COLUMNS[1][0];
	public static final String PARTNER = COLUMNS[2][0];
	public static final String PLACE = COLUMNS[3][0];
	public static final String CONTENT = COLUMNS[4][0];
	public static final String STARTTIME = COLUMNS[5][0];
	public static final String MODE = COLUMNS[6][0];
	public static final String WIFISSID = COLUMNS[7][0];
	public static final String WIFIPASSWORD = COLUMNS[8][0];




	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return URI;
	}

	@Override
	public String[][] getColumns() {
		// TODO Auto-generated method stub
		return COLUMNS;
	}

	@Override
	public String[][] getIndexs() {
		// TODO Auto-generated method stub
		return null;
	}

}