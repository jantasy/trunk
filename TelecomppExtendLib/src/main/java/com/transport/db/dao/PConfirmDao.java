package com.transport.db.dao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.telecompp.activity.ConfirmActivity;
import com.telecompp.util.LoggerHelper;
import com.telecompp.util.SumaConstants;
import com.telecompp.util.SumaConstants.IpCons;
import com.telecompp.util.SumaPostHandler;
import com.transport.cypher.KEYSUtilProxy;
import com.transport.db.PPDBOpenHelper;
import com.transport.db.bean.ConfirmItem;
import com.transport.service.UploadConfirmService;

public class PConfirmDao {
	private PPDBOpenHelper helper;
	public static final String CONFIRM_TABLE = "PURCHASE_CONFIRM";
	public static final String _ID = "_id";
	public static final String XML = "XML";
	public static final String STAT = "STAT";
	public static final String TRADE_TIME = "TRADE_TIME";
	public static final String TRADE_MONEY = "TRADE_MONEY";
	public static final String CITY = "CITY";
	public static final String MD5 = "MD5";
	public static final String TYPE = "TYPE";
	public static final String TRADENUM = "TRADENUM";
	// ================================================
	public static final String KEYS_TABLE = "KEYS";
	public static final String KEYS = "KEYS";
	public static final String ICCID = "ICCID";
	public static final String COMMITED = "COMMITED";// 0表示提交其他表示未提交
	private Context context;
	private static boolean isSubmit = false;// 当前是否在上传未确认的订单
	public static final String CONFIRM_URI = "content://com.robusoft.pp.confirm";

	public static LoggerHelper logger = new LoggerHelper(ConfirmActivity.class);
	
	public PConfirmDao() {
		this.helper = PPDBOpenHelper.getInstance();
	}
	

	/**
	 * 提交未完成的订单
	 * 
	 * @return 处理之后依然异常的交易数
	 */

	public void addKEY(String key, String md5, String type) {
		SQLiteDatabase db = null;
		try {			
			if ("1".equals(type)) {
				// 查询是否有公钥信息
				if (getKEY("1") != null)
					return;
			}
			db = this.helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			// 获取ICCID
			// this.deleteAll();
			if ("0".equals(type))
				db.delete(KEYS_TABLE, null, null);
			values.put(ICCID, KEYSUtilProxy.getCardICCID());
			values.put(KEYS, key);
			values.put(MD5, md5);
			values.put(COMMITED, "1");
			values.put(TYPE, type);
			db.insert(KEYS_TABLE, null, values);
		} catch(Exception e) {
			e.printStackTrace();
			logger.printLogOnSDCard("====addKey" + e.toString());
		} catch(Throwable e) {
			e.printStackTrace();
			logger.printLogOnSDCard("=====addKey=thr=" + e.toString());
		} finally {
			if(db != null) 
				db.close();
		}
	}

	public void setCommitted() {
		SQLiteDatabase db = null;
		try {			
			db = this.helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(COMMITED, "0");
			db.update(KEYS_TABLE, values, null, null);
		} catch (Exception e) {
			logger.printLogOnSDCard(e.getStackTrace().toString());
		} finally {
			if(db != null) {
				db.close();
			}
		}
	}

	/**
	 * 返回密钥数据
	 * 
	 * @return KEYS|MD5
	 */
	public String getKEY(String type) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {			
			db = this.helper.getReadableDatabase();
			cursor = db.query(KEYS_TABLE, null, TYPE + "=?",
					new String[] { type }, null, null, null);
			if (cursor.moveToNext()) {
				if ("1".equals(type)) {
					return cursor.getString(cursor.getColumnIndex(KEYS));
				} else
					return cursor.getString(cursor.getColumnIndex(KEYS)) + "|"
					+ cursor.getString(cursor.getColumnIndex(MD5)) + "|"
					+ cursor.getString(cursor.getColumnIndex(ICCID)) + "|"
					+ cursor.getString(cursor.getColumnIndex(COMMITED));
			}
		} catch(Exception e) {
			e.printStackTrace();
			logger.printLogOnSDCard(e.getStackTrace().toString());
		} finally  {
			if(cursor != null) {
				cursor.close();
			}
			if(db != null) {
				db.close();
			}
		}
		return null;
	}

	public void deleteAllKeys() {
		SQLiteDatabase db = this.helper.getWritableDatabase();
		db.delete(KEYS_TABLE, null, null);
		db.close();
	}

	public void show() {
		Cursor cursor = null;
		SQLiteDatabase db = null;
		try {			
			db = this.helper.getReadableDatabase();
			cursor = db
					.query(KEYS_TABLE, null, null, null, null, null, null);
			while (cursor.moveToNext()) {
				System.out.println(cursor.getString(cursor.getColumnIndex(KEYS))
						+ "|" + cursor.getString(cursor.getColumnIndex(MD5)) + "|"
						+ cursor.getString(cursor.getColumnIndex(ICCID)) + "|"
						+ cursor.getString(cursor.getColumnIndex(COMMITED))
						+ "____" + cursor.getString(cursor.getColumnIndex(TYPE)));
			}
		} catch(Exception e) {
			e.printStackTrace();
			logger.printLogOnSDCard(e.getStackTrace().toString());
		} finally {
			if(cursor != null) {				
				cursor.close();
			}
			if(db != null) {
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
	public long add(ConfirmItem item) {
		item.setMd5();// 计算MD5值
		SQLiteDatabase db = null;
		try {
			db = this.helper.getWritableDatabase();
			long retVal = db.insert(CONFIRM_TABLE, null,
					item2ContentValues(item));
			logger.printLogOnSDCard("通知发送失败, 记录到数据库   type = " + item.getType());
			// 启动定时上传服务
			Intent service = new Intent();
			service.setClass(context, UploadConfirmService.class);
			return retVal;
		} catch (Exception e) {
			return -1;
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	
	
	/**
	 * 将确认对象中的数据封装到ContentValue中
	 * 
	 * @param item
	 * @return
	 */
	private ContentValues item2ContentValues(ConfirmItem item) {
		ContentValues values = new ContentValues();
		values.put(XML, item.getXml());
		values.put(STAT, item.getStat());
		values.put(TRADE_MONEY, item.getTrade_money());
		values.put(TRADE_TIME, item.getTrade_time());
		values.put(CITY, item.getCity());
		values.put(MD5, item.getMd5());
		values.put(TYPE, item.getType());
		values.put(TRADENUM, item.getTradeNum());
		return values;
	}
	
	
	/**
	 * 提交未完成的订单
	 * 
	 * @return 处理之后依然异常的交易数
	 */
	public int subMitConfirm(String type) {
		Log.i("PConfirmDao", "==wb===subMitConfirm====");
		try {
			//	查询出当前数据库中所有未提交的报文
			List<ConfirmItem> list = this.queryNeedConfirm(type);
			//	只有在数据库中不为空时才会执行上传, 否则就是空任务
			if(list != null && list.size() > 0) {
				// 1.获取所有的未完成交易
				if (!isSubmit) {
					synchronized (this.getClass()) {// 防止广播接收者和服务同时调用
						if (isSubmit) {
							return 1;
						} else {
							isSubmit = true;
						}
					}
					SumaPostHandler HttpPostHandler = new SumaPostHandler();// 向平台发送POST请求的工具
					Map<String, Object> responseMap;
					// 福州的依然可以进行确认
					// 写上送记录TODO 需要将
					File filed = Environment.getExternalStorageDirectory();
					File f = new File(filed, "碰碰日志");
					if (!f.exists()) {
						f.mkdirs();
					}
					File file = new File(f, "upload.txt");
					int count = 0;
					List<String> dolist = new ArrayList<String>();
					BufferedWriter bw = null;
					FileWriter fileWriter = null;
					try {
						fileWriter = new FileWriter(file);
						bw = new BufferedWriter(fileWriter);
						for (ConfirmItem item : list) {
							bw.write("重新发送:" + item.getXml() + "\n");
							
							Log.i("PConfirmDao", "==wb===subMitConfirm==重新发送==");
							
							responseMap = HttpPostHandler
									.httpPostAndGetResponse(
											IpCons.SERVER_ADDRESS_IP_BUS,
											item.getXml(),
											SumaConstants.ENCRYPT_LEVEL_FULL_LEVEL_ENC_DATA);
							if (responseMap != null) {
								Log.i("PConfirmDao", "==wb===subMitConfirm==重新发送成功==");
								// 将这条记录设置成已完成
								dolist.add(item.get_id());
								count++;
							} else {
								Log.i("PConfirmDao", "==wb===subMitConfirm==重新发送失败==");
								bw.write("处理失败" + item.get_id());
							}
							
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (bw != null) {
							try {
								bw.close();
							} catch (IOException e) {
								
							}
						}
						if (fileWriter != null) {
							try {
								fileWriter.close();
							} catch (IOException e) {
								
							}
						}
						//	将已上传的item删除
						this.setConfirmed(dolist);
					}
					isSubmit = false;
					return list.size() - count;
				}
			} else {
				Log.i("PConfirmDao", "==wb===subMitConfirm==数据库中没有需要上传的数据==");
			}
			return list.size();
		} catch (Exception e) {
			return 1;
		}
	}
	
	
	/**
	 * 将已经上传的item从数据库中删除
	 * @param _id
	 * @return
	 */
	public long setConfirmed(List<String> _id) {
		SQLiteDatabase db = null;
		try {
			db = this.helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(STAT, "1");
			long retVal = -1;
			for (String id : _id) {
				Log.i("PConfirmDao", "==wb===setConifirmed==删除数据库===id==" + id);
				//	删除表
				String sql = "DELETE FROM" + CONFIRM_TABLE + "WHERE" + _ID + "=?";
				db.delete(CONFIRM_TABLE, _ID + "=?", new String[] { id + "" });
			}
			return retVal;
		} catch (Exception e) {
			return -1;
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}
	
	/**
	 * 查询出所有未进行确认的消费
	 * 
	 * @return
	 */
	public List<ConfirmItem> queryNeedConfirm(String type) {
		List<ConfirmItem> retList;
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.helper.getReadableDatabase();
			//	查询获取了TAC但是没有上送的记录
			if(type != null && !"".equals(type)) {
				Log.i("PConfirmDao", "==wb===setConifirmed==查询数据库===type==" + type + "--");
				cursor = db.query(CONFIRM_TABLE, null, TYPE + "=?", new String[] { type }, null, null, null);
			} else {
				Log.i("PConfirmDao", "==wb===setConifirmed==查询数据库===type==" + type + "--");
				cursor = db.query(CONFIRM_TABLE, null, null, null,
						null, null, null);
			}
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
	 * 查询出所有未进行确认的消费
	 * 
	 * @param city
	 * @return
	 */
	public List<ConfirmItem> queryUnConfirm(String city) {
		List<ConfirmItem> retList;
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.helper.getReadableDatabase();
			cursor = db.query(CONFIRM_TABLE, null, STAT + "='0' AND " + CITY
					+ "=?", new String[] { city }, null, null, null);
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
	 * 查询已经完成确认的消费
	 * 
	 * @param city
	 * @return
	 */
	public List<ConfirmItem> queryConfirmed(String city) {
		List<ConfirmItem> retList;
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.helper.getReadableDatabase();
			cursor = db.query(CONFIRM_TABLE, null, STAT + "='1' AND " + CITY
					+ "=?", new String[] { city }, null, null, null);
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
	private List<ConfirmItem> cursor2List(Cursor cursor) {
		ArrayList<ConfirmItem> retList = new ArrayList<ConfirmItem>();
		ConfirmItem item;
		while (cursor.moveToNext()) {
			item = new ConfirmItem();
			item.set_id(cursor.getString(cursor.getColumnIndex(_ID)));
			item.setCity(cursor.getString(cursor.getColumnIndex(CITY)));
			item.setStat(cursor.getString(cursor.getColumnIndex(STAT)));
			item.setTrade_money(cursor.getString(cursor
					.getColumnIndex(TRADE_MONEY)));
			item.setTrade_time(cursor.getString(cursor
					.getColumnIndex(TRADE_TIME)));
			item.setXml(cursor.getString(cursor.getColumnIndex(XML)));
			item.setMd5(cursor.getString(cursor.getColumnIndex(MD5)));
			item.setType(cursor.getString(cursor.getColumnIndex(TYPE)));
			item.setTradeNum(cursor.getString(cursor.getColumnIndex(TRADENUM)));
			retList.add(item);
		}
		return retList;
	}
}
