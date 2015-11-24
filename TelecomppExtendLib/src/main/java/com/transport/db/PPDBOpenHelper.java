package com.transport.db;


import com.telecompp.ContextUtil;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 将数据库对象修改成单例模式
 * @author poet 
 *
 */
public class PPDBOpenHelper extends SQLiteOpenHelper {

	
	private static PPDBOpenHelper instance = null;
	private  PPDBOpenHelper(){
		super(ContextUtil.getInstance(), "ppdb", null, 1);
	}
	public static PPDBOpenHelper getInstance(){
//		if(instance == null)
//			instance = new PPDBOpenHelper();
//		return instance;
		if(instance == null) {
			synchronized (PPDBOpenHelper.class) {				
				PPDBOpenHelper temp = instance;
				if(instance == null) {
					temp = new PPDBOpenHelper();
					instance = temp;
				}
			}
		}
		return instance;
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE PURCHASE_CONFIRM "
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"//主键
				+ "XML VARCHAR(500),"//请求报文
				+ "STAT INTEGER,"//当前状态
				+ "TRADE_TIME DATE,"//交易日期
				+ "TRADE_MONEY VARCHAR(6),"//交易金额
				+ "CITY VARCHAR(6),"//城市代码
				+ "TYPE VARCHAR(6),"//交易类型
				+ "TRADENUM VARCHAR(6),"//交易类型
				+ "MD5 VARCHAR(6))");//MD5验证码
		db.execSQL("CREATE TABLE KEYS "
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"//主键
				+ "KEYS VARCHAR(500),"//密文
				+ "MD5 VARCHAR(50),"//MAC
				+ "COMMITED VARCHAR(2),"//是否已经提交服务器
				+ "TYPE VARCHAR(2),"//类型0：客户端密钥 1：平台公钥
				+ "ICCID VARCHAR(20))");//MD5验证码
		
		//	创建表 IS_WRITE_CARD_SUCCESS 主要用于极端情况下的写卡失败例如 刚获取mac2 还没来得及写卡就断电了
		db.execSQL("CREATE TABLE IS_WRITE_CARD_SUCCESS"
				+ "(_ID INTEGER PRIMARY KEY AUTOINCREMENT,"	//	主键
				+ "APPLICATIONSN VARCHAR(50),"	//	交易序列号
				+ "CONSUMECOUNT VARCHAR(10),"	//	消费计数器
				+ "LOADCOUNT VARCHAR(10),"	//	充值
				+ "TAC VARCHAR(50),"		//	上送的TAC
				+ "TRANRESULTFLAG VARCHAR(10),"	//	交易状态标识
				+ "TRADENUM VARCHAR(50),"	//	交易流水号
				+ "PHONE VARCHAR(15),"	//	电话号码
				+ "BALANCE VARCHAR(15),"	//	交易前余额
				+ "MD5 VARCHAR(50))"	//	MD5验证码
				);	
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
