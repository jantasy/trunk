package com.transport.db.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.telecompp.activity.ConfirmActivity;
import com.telecompp.util.LoggerHelper;
import com.telecompp.util.SumaConstants;
import com.telecompp.util.SumaConstants.IpCons;
import com.telecompp.util.SumaPostHandler;
import com.transport.db.PPDBOpenHelper;
import com.transport.db.bean.IsLoadSuccessItem;
import com.transport.processor.ImpYJTCardProcessor;

public class PIsLoadSuccessDao {
	
	private PPDBOpenHelper helper;
	
	public static String TABLE = "IS_WRITE_CARD_SUCCESS";
	
	public static String APPLICATIONSN = "APPLICATIONSN";
	public static String CONSUMECOUNT = "CONSUMECOUNT";
	public static String LOADCOUNT = "LOADCOUNT";
	public static String TAC = "TAC";
	public static String TRANRESULTFLAG = "TRANRESULTFLAG";
	public static String TRADENUM = "TRADENUM";
	public static String PHONE = "PHONE";
	public static String BALANCE = "BALANCE";
	public static String MD5 = "MD5";
	
	private LoggerHelper logger = new LoggerHelper(ConfirmActivity.class); 
	

	public PIsLoadSuccessDao() {
		this.helper = PPDBOpenHelper.getInstance();
	}
	
	
	public boolean subExtremeRecord(Context context) {
		SumaPostHandler HttpPostHandler = new SumaPostHandler();// 向平台发送POST请求的工具
		Map<String, Object> responseMap;
		PIsLoadSuccessDao isDao = new PIsLoadSuccessDao();
		List<IsLoadSuccessItem> list_isLoadSuccessItem = isDao.queryUnConfirmed();
		if(list_isLoadSuccessItem != null && list_isLoadSuccessItem.size() > 0) {
			//	如果有极端情况, 这种情况只可能有一个
			IsLoadSuccessItem item = list_isLoadSuccessItem.get(0);
			String temp_balance = item.getBalance();
			String temp_loadCount = item.getLoadCount();
			//	获取卡片的余额 和 充值金额
			ImpYJTCardProcessor processor = new ImpYJTCardProcessor(context, null);
			String balance_str = processor.getCardBalanceHex();
			String loadCount = processor.getCardLoadCount();
			
			//	与从卡片中读取的充值计数器, 和 卡内余额 进行比较  
			if(temp_balance.equals(balance_str) && temp_loadCount.equals(loadCount)) {
				//	如果相同则表示充值失败 , 需要发送失败通知
				String xml = processor.getLoadNoticeXml(item.getApplicationSN(), item.getConsumeCount(), item.getLoadCount(), 
						item.getTac(), "0x01", item.getTradeNum(), item.getPhone(), item.getBalance());
				responseMap = HttpPostHandler.httpPostAndGetResponse(IpCons.SERVER_ADDRESS_IP_BUS,
						xml, SumaConstants.ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA);
				
				//	这里判断是否发送成功, 成功则删除
				if (responseMap != null) {
					Log.i("PConfirmDao", "==wb===subMitConfirm==获取mac2 充值失败通知重新发送成功==");
					// 将这条记录删除
					this.delIsLoadSuccessItem(item);
					return true;
				}
//				new RetryTask(3, true, new ConfirmTask(xml, tradeMoney, new Date(), LOAD_GET_MAC2, tradeNum)).start();
				//	写log
				logger.printLogOnSDCard("获取mac2 充值失败通知---发送失败");
				return false;
			} else {
				//	充值成功, 发送成功确认通知
				//	首先获取TAC
				String temp_tac = processor.getPreTAC();
				//	拼接成功报文
				String xml = processor.getLoadNoticeXml(item.getApplicationSN(), item.getConsumeCount(), item.getLoadCount(), 
						temp_tac, "0x99", item.getTradeNum(), item.getPhone(), item.getBalance());
				responseMap = HttpPostHandler.httpPostAndGetResponse(IpCons.SERVER_ADDRESS_IP_BUS,
						xml, SumaConstants.ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA);
//				这里判断是否发送成功, 成功则删除
				if (responseMap != null) {
					Log.i("PConfirmDao", "==wb===subMitConfirm==获取mac2 充值成功通知重新发送成功==");
					// 将这条记录删除
					this.delIsLoadSuccessItem(item);
					return true;
				}
				
//				new RetryTask(3, true, new ConfirmTask(xml, tradeMoney, new Date(), LOAD_GET_MAC2, tradeNum)).start();
				logger.printLogOnSDCard("获取mac2 充值成功通知重新发送失败");
				return false;
			}
		}
		return true;
	}
	

	/**
	 * 查询数据库中是否存在需要确认
	 * 
	 * @param city
	 * @return
	 */
	public List<IsLoadSuccessItem> queryUnConfirmed() {
		List<IsLoadSuccessItem> retList;
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.helper.getReadableDatabase();
			cursor = db.query(TABLE, null, null, null, null, null, null);
			retList = cursor2List(cursor);
			return retList;
		} catch (Exception e) {
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
		}
	}
	
	/**
	 * 将cursor里面的内容解析成确认对象
	 * 
	 * @param cursor
	 * @return
	 */
	private List<IsLoadSuccessItem> cursor2List(Cursor cursor) {
		ArrayList<IsLoadSuccessItem> retList = new ArrayList<IsLoadSuccessItem>();
		IsLoadSuccessItem item;
		while (cursor.moveToNext()) {
			item = new IsLoadSuccessItem();
  			item.setApplicationSN(cursor.getString(cursor.getColumnIndex(APPLICATIONSN)));
  			item.setBalance(cursor.getString(cursor.getColumnIndex(BALANCE)));
  			item.setConsumeCount(cursor.getString(cursor.getColumnIndex(CONSUMECOUNT)));
  			item.setLoadCount(cursor.getString(cursor.getColumnIndex(LOADCOUNT)));
  			item.setMd5(cursor.getString(cursor.getColumnIndex(MD5)));
  			item.setPhone(cursor.getString(cursor.getColumnIndex(PHONE)));
  			item.setTac(cursor.getString(cursor.getColumnIndex(TAC)));
  			item.setTradeNum(cursor.getString(cursor.getColumnIndex(TRADENUM)));
  			item.setTranResultFlag(cursor.getString(cursor.getColumnIndex(TRANRESULTFLAG)));
  			if(item.isValid()) {  				
  				retList.add(item);
  			}
		}
		return retList;
	}
	
	
	/**
	 * 将已经上传的item从数据库中删除
	 * @param _id
	 * @return
	 */
	public int delIsLoadSuccessItem(IsLoadSuccessItem item) {
		SQLiteDatabase db = null;
		try {
			db = this.helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			//	删除item
			int retVal = 0;
			retVal = db.delete(TABLE, APPLICATIONSN + "=? and " + CONSUMECOUNT + "=? and " + LOADCOUNT + "=? and " + TRADENUM + "=? and " + PHONE + "=? and "
					  + BALANCE + "=? and " + MD5 + "=?", new String[] {item.getApplicationSN(), item.getConsumeCount(), item.getLoadCount(),
					item.getTradeNum(), item.getPhone(), item.getBalance(), item.getMd5()});
			return retVal;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	
	/**
	 * 添加确认记录
	 * 
	 * @param item
	 *            确认记录信息
	 * @return
	 */
	public long addIsLoadSuccessItem(IsLoadSuccessItem item) {
		SQLiteDatabase db = null;
		try {
			db = this.helper.getWritableDatabase();
			long retVal = db.insert("IS_WRITE_CARD_SUCCESS", null,
					isLoadSuccessItem2ContentValues(item));
			return retVal;
		} catch (Exception e) {
			return -1;
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	
	private ContentValues isLoadSuccessItem2ContentValues(IsLoadSuccessItem item) {
		ContentValues values = new ContentValues();
		values.put(APPLICATIONSN, item.getApplicationSN());
		values.put(CONSUMECOUNT, item.getConsumeCount());
		values.put(LOADCOUNT, item.getLoadCount());
		values.put(TAC, item.getTac());
		values.put(TRANRESULTFLAG, item.getTranResultFlag());
		values.put(TRADENUM, item.getTradeNum());
		values.put(PHONE, item.getPhone());
		values.put(BALANCE, item.getBalance());
		values.put(MD5, item.getMd5());
		return values;
	}
}
