package com.chinatelecom.nfc.DB.Provider;

public class MyDataTable extends TableBase{
	public static final String URI = "tt_mydata";
	public static final int DEFAULT_TYPE = 		99;
	
	public static final int MYMODE = 			1;
	public static final int MEETTING = 			2;

	//ashley 0923 删除无用资源
//	/**
//	 * 优惠券
//	 */
//	public static final int COUPON = 			3;
//	public static final int LOTTERY = 			4;
//	public static final int AD = 				5;
	public static final int MYMODECUSTOM = 		6;
	
	public static final int NAMECARD = 			7;
	public static final int TEXT = 				8;
	public static final int WEB = 				9;
	
	public static final int BUS_CARD = 			10;
	//ashley 0923 删除无用资源
//	/**
//	 * 票据夹:电影票
//	 */
//	public static final int MOVIETICKET = 		12;
//	/**
//	 * 票据夹:公园门票
//	 */
//	public static final int PARKTICKET = 		13;
	
//	/**
//	 * 读标签：字符标签，网址标签，广告
//	 */
	public static final int READTAG = 			14;
	
	public static final int OTHER = 			99;
//	public static final int NAME_CARD = 		11;
	
	/**
	 * 从NFC上读取数据的标志
	 */
	public static final int TAG_READFROMNFC = 		1;
	/**
	 * 制作标签的标志
	 */
	public static final int TAG_WRITETAG = 			2;
	
	/**
	 * 收藏从NFC上读取的标签。
	 */
	public static final int TAG_READ_FAVORITE = 	3;
	/**
	 * 收藏从NFC上读取的标签。
	 */
	public static final int TAG_WRITE_FAVORITE = 	4;
	/**
	 * 收藏从NFC上读取的标签。
	 */
	public static final int TAG_MY_NAMECARD = 	   5;
	
	
	public static final String[][] COLUMNS = new String[][] {
		new String[]{"t_id","INTEGER PRIMARY KEY AUTOINCREMENT"},
		new String[]{"t_name","TEXT"},
		new String[]{"datatype","INTEGER NOT NULL"},
		new String[]{"tableid","INTEGER NOT NULL"},
		new String[]{"createtime","INTEGER NOT NULL"},
		new String[]{"readorwrite","INTEGER NOT NULL DEFAULT 1"},
		new String[]{"uuid","TEXT"}
	};
	
	/** id column  */
	public static final String ID = COLUMNS[0][0];
	public static final String NAME = COLUMNS[1][0];
	public static final String DATATYPE = COLUMNS[2][0];
	public static final String TABLEID = COLUMNS[3][0];
	public static final String CREATETIME = COLUMNS[4][0];
	public static final String READORWRITE = COLUMNS[5][0];
	public static final String UUID = COLUMNS[6][0];

	
	
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
