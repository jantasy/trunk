package com.chinatelecom.nfc.DB.Provider;

public class NameCardTable extends TableBase {
	public static final String URI = "tt_namecard";
	public static final String[][] COLUMNS = new String[][] {
			new String[] { "id", "INTEGER PRIMARY KEY AUTOINCREMENT" },
			new String[] { "name", "TEXT" },
			new String[] { "contactIcon", "BLOB" },
			new String[] { "telPhone", "TEXT"	},
			new String[] { "fax", "TEXT"	},
			new String[] { "companyName", "TEXT"	},
			new String[] { "companyNetAddress", "TEXT"	},
			new String[] { "companyPhone", "TEXT"	},
			new String[] { "section", "TEXT"	},
			new String[] { "rank", "TEXT"	},
			new String[] { "address", "TEXT"	},
			new String[] { "email", "TEXT"	},
			new String[] { "description", "TEXT"	},
			new String[] {	"showflag","INTEGER"},
			new String[] {"shortcut","INTEGER"}};

	/** id column */
	public static final String ID = COLUMNS[0][0];
	public static final String NAME = COLUMNS[1][0];
	public static final String CONTACTICON = COLUMNS[2][0];
	public static final String TELPHONE = COLUMNS[3][0];
	public static final String FAX = COLUMNS[4][0];
	public static final String COMPANYNAME = COLUMNS[5][0];
	public static final String COMPANYNETADDRESS = COLUMNS[6][0];
	public static final String COMPANYPHONE = COLUMNS[7][0];
	public static final String SECTION = COLUMNS[8][0];
	public static final String RANK = COLUMNS[9][0];
	public static final String ADDRESS = COLUMNS[10][0];
	public static final String EMAIL = COLUMNS[11][0];
	public static final String DESCRIPTION = COLUMNS[12][0];
	public static final String SHOWFLAG = COLUMNS[13][0];
	public static final String SHORTCUT = COLUMNS[14][0];
	public String getTableName() {
		// TODO Auto-generated method stub
		return URI;
	}

	public String[][] getColumns() {
		// TODO Auto-generated method stub
		return COLUMNS;
	}

	public String[][] getIndexs() {
		// TODO Auto-generated method stub
		return null;
	}
}
