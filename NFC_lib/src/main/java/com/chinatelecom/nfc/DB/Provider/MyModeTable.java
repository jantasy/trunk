package com.chinatelecom.nfc.DB.Provider;

public class MyModeTable extends TableBase {
	public static final String URI = "tt_mymode";

	public static final String[][] COLUMNS = new String[][] {
		new String[]{"id","INTEGER PRIMARY KEY AUTOINCREMENT"},
		new String[]{"name","TEXT"},
		new String[]{"mutemode","INTEGER NOT NULL"},
		new String[]{"wifiswitch","INTEGER NOT NULL"},
		new String[]{"digitalswitch","INTEGER NOT NULL"},
		new String[]{"bluetooth","INTEGER NOT NULL"},
		new String[]{"wifissid","TEXT"},
		new String[]{"wifipassword","TEXT"},
		new String[]{"content","TEXT"},
		new String[]{"modeflag","INTEGER NOT NULL"}

	};

	/** id column */
	public static final String ID = 				COLUMNS[0][0];
	public static final String NAME = 				COLUMNS[1][0];
	public static final String MUTEMODE = 			COLUMNS[2][0];
	public static final String WIFISWITCH = 		COLUMNS[3][0];
	public static final String DIGITALSWITCH = 		COLUMNS[4][0];
	public static final String BLUETOOTH = 			COLUMNS[5][0];
	public static final String WIFISSID = 			COLUMNS[6][0];
	public static final String WIFIPASSWORD = 		COLUMNS[7][0];
	public static final String CONTENT = 			COLUMNS[8][0];
	public static final String MODEFLAG = 			COLUMNS[9][0];





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

