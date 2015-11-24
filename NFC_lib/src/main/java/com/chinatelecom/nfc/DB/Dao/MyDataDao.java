package com.chinatelecom.nfc.DB.Dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.anim;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.RawContacts.Data;
import android.text.TextUtils;

//ashley 0923 删除无用资源
//import com.chinatelecom.nfc.DetailAdActivity;
//import com.chinatelecom.nfc.DetailCouponActivity;
//import com.chinatelecom.nfc.DetailLotteryActivity;
import com.chinatelecom.nfc.DetailMeetingActivity;
//ashley 0923 删除无用资源
//import com.chinatelecom.nfc.DetailMovieActivity;
//import com.chinatelecom.nfc.DetailParkActivity;
import com.chinatelecom.nfc.DetailTextActivity;
import com.chinatelecom.nfc.DetailWebActivity;
import com.chinatelecom.nfc.NameCardManageActivity;
import com.chinatelecom.nfc.R;
import com.chinatelecom.nfc.AsyncTask.Wait4Handler;
import com.chinatelecom.nfc.Const.Const;
//ashley 0923 删除无用资源
//import com.chinatelecom.nfc.DB.Pojo.Ad;
//import com.chinatelecom.nfc.DB.Pojo.Coupon;
//import com.chinatelecom.nfc.DB.Pojo.Lottery;
import com.chinatelecom.nfc.DB.Pojo.Meetting;
//ashley 0923 删除无用资源
//import com.chinatelecom.nfc.DB.Pojo.MovieTicket;
import com.chinatelecom.nfc.DB.Pojo.MyData;
import com.chinatelecom.nfc.DB.Pojo.MyMode;
import com.chinatelecom.nfc.DB.Pojo.NameCard;
//ashley 0923 删除无用资源
//import com.chinatelecom.nfc.DB.Pojo.ParkTicket;
import com.chinatelecom.nfc.DB.Pojo.ProtocolTitle;
//ashley 0923 删除无用资源
//import com.chinatelecom.nfc.DB.Provider.AdTable;
//import com.chinatelecom.nfc.DB.Provider.CouponTable;
//import com.chinatelecom.nfc.DB.Provider.LotteryTable;
import com.chinatelecom.nfc.DB.Provider.MeetingTable;
//import com.chinatelecom.nfc.DB.Provider.MovieTicketTable;
import com.chinatelecom.nfc.DB.Provider.MyDataTable;
import com.chinatelecom.nfc.DB.Provider.MyModeTable;
import com.chinatelecom.nfc.DB.Provider.NameCardTable;
//ashley 0923 删除无用资源
//import com.chinatelecom.nfc.DB.Provider.ParkTicketTable;
import com.chinatelecom.nfc.DB.Provider.SettingData;
import com.chinatelecom.nfc.Utils.Constant;
import com.chinatelecom.nfc.Utils.MyUtil;
import com.google.gson.Gson;

public class MyDataDao extends SQLiteDao {
	// public final Object lock = new Object();
	private final static String table = MyDataTable.URI;

	public MyDataDao(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public static long insert(Context context, Object object, Integer tagFlag) {
		long id = SettingData.INSERT_ERR;
		String name = "";
		Integer tagType = MyDataTable.MYMODECUSTOM;
		try {
			getDataBase(context).open();
			if (object instanceof Meetting) {
				Meetting m = (Meetting) object;
				id = MeetingDao.insert(context, m, false);
				name = m.n;
				tagType = MyDataTable.MEETTING;
			//ashley 0923 删除无用资源
//			} else if (object instanceof Coupon) {
//				Coupon s = (Coupon) object;
//				id = CouponDao.insert(context, s, false);
//				name = s.n;
//				tagType = MyDataTable.COUPON;
//			} else if (object instanceof Lottery) {
//				Lottery l = (Lottery) object;
//				id = LotteryDao.insert(context, l, false);
//				name = l.n;
//				tagType = MyDataTable.LOTTERY;
//			} else if (object instanceof Ad) {
//				Ad a = (Ad) object;
//				id = AdDao.insert(context, a, false);
//				name = a.n;
//				tagType = MyDataTable.AD;
			} else if (object instanceof MyMode) {
				MyMode my = (MyMode) object;
				id = MyModeDao.insert(context, my, false);
				name = my.name;
				tagType = MyDataTable.MYMODECUSTOM;
			} else if (object instanceof NameCard) {
				NameCard nameCard = (NameCard) object;
				id = NameCardDao.insert(context, nameCard, false);
				name = nameCard.getName();
				tagType = MyDataTable.NAMECARD;
			//ashley 0923 删除无用资源
//			} else if (object instanceof MovieTicket) {
//				MovieTicket movieTicket = (MovieTicket) object;
//				id = MovieTicketDao.insert(context, movieTicket, false);
//				name = movieTicket.n;
//				tagType = MyDataTable.MOVIETICKET;
//			} else if (object instanceof ParkTicket) {
//				ParkTicket parkTicket = (ParkTicket) object;
//				id = ParkTicketDao.insert(context, parkTicket, false);
//				name = parkTicket.n;
//				tagType = MyDataTable.PARKTICKET;
			}
			if (id != SettingData.INSERT_ERR) {
				MyData e = new MyData(null, name, tagType,
						MyUtil.longToInteger(id), 1l, tagFlag);
				id = MyDataDao.insert(context, e, false);
				if (id != SettingData.INSERT_ERR) {
					MyUtil.showMessage(R.string.nfc_add_success, context);
				} else {
					MyUtil.showMessage(R.string.nfc_add_faild, context);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		} finally {
			getDataBase(context).close();
		}
		return id;
	}

	public static long insert(Context context, MyData environments,
			boolean isNeedOpenDB) {
		if (environments == null) {
			return SettingData.INSERT_ERR;
		}
		long id = SettingData.INSERT_ERR;
		try {
			if (isNeedOpenDB) {
				getDataBase(context).open();
			}
			ContentValues c;
			c = new ContentValues();
			c.put(MyDataTable.NAME, environments.n);
			c.put(MyDataTable.DATATYPE, environments.dt);
			c.put(MyDataTable.TABLEID, environments.tId);
			c.put(MyDataTable.CREATETIME, System.currentTimeMillis());
			c.put(MyDataTable.READORWRITE, environments.row);
			c.put(MyDataTable.UUID, environments.uuid);
			id = saveData(table, c);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		} finally {
			if (isNeedOpenDB) {
				getDataBase(context).close();
			}
		}
		return id;
	}

	public static int update(Context context, MyData environments) {
		if (environments == null) {
			return SettingData.INSERT_ERR;
		}
		int statement = SettingData.INSERT_ERR;
		try {
			getDataBase(context).open();
			ContentValues c;
			c = new ContentValues();
			if (environments.id != null)
				c.put(MyDataTable.ID, environments.id);
			if (environments.n != null)
				c.put(MyDataTable.NAME, environments.n);
			if (environments.dt != null)
				c.put(MyDataTable.DATATYPE, environments.dt);
			if (environments.tId != null)
				c.put(MyDataTable.TABLEID, environments.tId);
			if (environments.time != null)
				c.put(MyDataTable.CREATETIME, System.currentTimeMillis());
			if (environments.row != null)
				c.put(MyDataTable.READORWRITE, environments.row);
			if (environments.uuid != null)
				c.put(MyDataTable.UUID, environments.uuid);
			String selection = "";
			List<String> selectionArgs = new ArrayList<String>();

			if (environments.id != null) {
				if (!selection.equals("")) {
					selection += " and ";
				}
				selection += MyDataTable.ID + "=?";
				selectionArgs.add(String.valueOf(environments.id));
			}
			statement = getDataBase(context).update(table, c, selection,
					selectionArgs.toArray(new String[0]));

			switch (environments.dt) {
			case MyDataTable.MYMODE:
				break;
			case MyDataTable.MEETTING:
				if (environments.getMeeting() != null) {
					MeetingDao
							.update(context, environments.getMeeting(), false);
				}
				break;
			//ashley 0923 删除无用资源
//			case MyDataTable.COUPON:
//				if (environments.getCoupon() != null) {
//					CouponDao.update(context, environments.getCoupon(), false);
//				}
//				break;
//			case MyDataTable.LOTTERY:
//				if (environments.getLottery() != null) {
//					LotteryDao
//							.update(context, environments.getLottery(), false);
//				}
//				break;
//			case MyDataTable.AD:
//				if (environments.getAd() != null) {
//					AdDao.update(context, environments.getAd(), false);
//				}
//				break;
			case MyDataTable.MYMODECUSTOM:
				break;
			case MyDataTable.NAMECARD:
				if (environments.getNameCard() != null) {
					NameCardDao.update(context, environments.getNameCard(),
							false);
				}
				break;
			//ashley 0923 删除无用资源
//			case MyDataTable.PARKTICKET:
//				if (environments.getParkticket() != null) {
//					ParkTicketDao.update(context, environments.getParkticket(),
//							false);
//				}
//				break;
//			case MyDataTable.MOVIETICKET:
//				if (environments.getMovieticket() != null) {
//					MovieTicketDao.update(context,
//							environments.getMovieticket(), false);
//				}
//				break;
			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		} finally {
			getDataBase(context).close();
		}
		return statement;
	}

	// public static List<MyData> query(Context context,String dateType,Integer
	// readorwrite,String limit,boolean isNeedOpenDB) {
	// // TODO Auto-generated method stub
	// List<MyData> datas = new ArrayList<MyData>();
	// try {
	// if (isNeedOpenDB) {
	// getDataBase(context).open();
	// }
	// String[] columns = new String[]{
	// MyDataTable.ID,
	// MyDataTable.NAME,
	// MyDataTable.DATATYPE,
	// MyDataTable.TABLEID,
	// MyDataTable.CREATETIME,
	// MyDataTable.READORWRITE,
	// MyDataTable.UUID
	// };
	// String selection ="";
	// List<String> selectionArgs = new ArrayList<String>();
	//
	// if (!selection.equals("")) {
	// selection += " and ";
	// }
	// selection += MyDataTable.DATATYPE + " != " + MyDataTable.MYMODE;
	//
	// if (!TextUtils.isEmpty(dateType)) {
	// if (!selection.equals("")) {
	// selection += " and ";
	// }
	// String dateTypeStr[] = dateType.split(",");
	//
	// selection += "(";
	// for (int i = 0; i < dateTypeStr.length; i++) {
	// if(i != 0){
	// selection += " or ";
	// }
	// selection += MyDataTable.DATATYPE + "=?";
	// selectionArgs.add(dateTypeStr[i]);
	// }
	// selection += ")";
	// }
	// if (readorwrite != null) {
	// if (!selection.equals("")) {
	// selection += " and ";
	// }
	// selection += MyDataTable.READORWRITE + "=?";
	// selectionArgs.add(String.valueOf(readorwrite));
	// }
	//
	//
	// StringBuffer orderBySB = new StringBuffer();
	//
	// //排序
	// if(TextUtils.isEmpty(dateType)){
	// //历史记录
	// orderBySB.append(MyDataTable.CREATETIME).append(" desc");
	// }else if(dateType.equals(String.valueOf(MyDataTable.MEETTING))){
	// //会议室
	// orderBySB.append("(case ").append(MyDataTable.DATATYPE).append(" when ").append(MyDataTable.MYMODE)
	// .append(" then 1 else 2 end), ").append(MyDataTable.CREATETIME).append(" desc");
	// }else {
	// //其他
	// orderBySB.append(MyDataTable.DATATYPE).append(" , ").append(MyDataTable.CREATETIME).append(" desc");
	// }
	//
	//
	// String groupBy = "";
	// String orderBy = orderBySB.toString();
	//
	// Cursor c = getDataBase(context).qurey(table, columns, selection,
	// (String[])selectionArgs.toArray(new String[0]), groupBy, null,orderBy,
	// limit);
	//
	// if (c != null) {
	// int cnt = c.getCount();
	// c.moveToFirst();
	// for (int i = 0; i < cnt; i++) {
	// Integer envirType = c.getInt(c.getColumnIndex(MyDataTable.DATATYPE));
	// Integer envirId = c.getInt(c.getColumnIndex(MyDataTable.TABLEID));
	// MyData data = new MyData(
	// c.getInt(c.getColumnIndex(MyDataTable.ID)),
	// c.getString(c.getColumnIndex(MyDataTable.NAME)),
	// envirType,
	// envirId,
	// c.getLong(c.getColumnIndex(MyDataTable.CREATETIME)),
	// c.getInt(c.getColumnIndex(MyDataTable.READORWRITE))
	// );
	//
	// switch (envirType) {
	// case MyDataTable.MYMODE:
	// List<MyMode> _MyModes = MyModeDao.query(context, envirId, envirType,
	// null, null, false);
	// if(_MyModes != null && _MyModes.size()>0){
	// data.setMyMode(_MyModes.get(0));
	// }
	//
	// break;
	// case MyDataTable.MEETTING:
	// ///???
	// // long startTime = System.currentTimeMillis();
	// List<Meetting> _Meetings = MeetingDao.query(context, envirId, -1, -1,
	// null, null, false);
	// if(_Meetings != null && _Meetings.size()>0){
	// data.setMeeting(_Meetings.get(0));
	// }
	//
	// break;
	// case MyDataTable.COUPON:
	// List<Coupon> _Schedules = CouponDao.query(context, envirId,false);
	// if(_Schedules != null && _Schedules.size()>0){
	// data.setCoupon(_Schedules.get(0));
	// }
	//
	// break;
	// case MyDataTable.LOTTERY:
	// List<Lottery> _Lotterys = LotteryDao.query(context, envirId,false);
	// if(_Lotterys != null && _Lotterys.size()>0){
	// data.setLottery(_Lotterys.get(0));
	// }
	//
	// break;
	// case MyDataTable.AD:
	// List<Ad> _Ads = AdDao.query(context, envirId,false);
	// if(_Ads != null && _Ads.size()>0){
	// data.setAd(_Ads.get(0));
	// }
	// break;
	// case MyDataTable.MYMODECUSTOM:
	// List<MyMode> _MyModesCustom = MyModeDao.query(context, envirId,
	// envirType, null, null, false);
	// if(_MyModesCustom != null && _MyModesCustom.size()>0){
	// data.setMyMode(_MyModesCustom.get(0));
	// }
	// break;
	// case MyDataTable.NAMECARD:
	// List<NameCard> _nameCard = NameCardDao.query(context, envirId, false);
	// if(_nameCard != null && _nameCard.size()>0){
	// data.setNameCard(_nameCard.get(0));
	// }
	// break;
	//
	// case MyDataTable.PARKTICKET:
	// List<ParkTicket> _parkticket =ParkTicketDao.query(context, envirId,
	// false);
	// if(_parkticket != null && _parkticket.size()>0){
	// data.setParkticket(_parkticket.get(0));
	// }
	// break;
	// case MyDataTable.MOVIETICKET:
	// List<MovieTicket> _movieticket = MovieTicketDao.query(context, envirId,
	// false);
	// if(_movieticket != null && _movieticket.size()>0){
	// data.setMovieticket(_movieticket.get(0));
	// }
	// break;
	//
	// default:
	// break;
	// }
	// datas.add(data);
	// c.moveToNext();
	// }
	// c.close();
	// }
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	//
	// } finally {
	// if (isNeedOpenDB) {
	// getDataBase(context).close();
	// }
	// }
	// return datas;
	// }
	public static List<MyData> query(Context context, String ids,
			String dateType, String readorwrite, String limit, String uuid,
			boolean isNeedOpenDB) {
		// TODO Auto-generated method stub
		List<MyData> datas = new ArrayList<MyData>();
		try {
			if (isNeedOpenDB) {
				getDataBase(context).open();
			}
			String[] columns = new String[] { MyDataTable.ID, MyDataTable.NAME,
					MyDataTable.DATATYPE, MyDataTable.TABLEID,
					MyDataTable.CREATETIME, MyDataTable.READORWRITE,
					MyDataTable.UUID };
			String selection = "";
			List<String> selectionArgs = new ArrayList<String>();

			if (!selection.equals("")) {
				selection += " and ";
			}
			selection += MyDataTable.DATATYPE + " != " + MyDataTable.MYMODE;

			if (!TextUtils.isEmpty(ids)) {
				if (!selection.equals("")) {
					selection += " and ";
				}
				String idsStr[] = ids.split(",");

				selection += "(";
				for (int i = 0; i < idsStr.length; i++) {
					if (i != 0) {
						selection += " or ";
					}
					selection += MyDataTable.ID + "=?";
					selectionArgs.add(idsStr[i]);
				}
				selection += ")";
			}
			if (!TextUtils.isEmpty(dateType)) {
				if (!selection.equals("")) {
					selection += " and ";
				}
				String dateTypeStr[] = dateType.split(",");

				selection += "(";
				for (int i = 0; i < dateTypeStr.length; i++) {
					if (i != 0) {
						selection += " or ";
					}
					selection += MyDataTable.DATATYPE + "=?";
					selectionArgs.add(dateTypeStr[i]);
				}
				selection += ")";
			}
			if (!TextUtils.isEmpty(readorwrite)) {
				if (!selection.equals("")) {
					selection += " and ";
				}
				String readorwriteStr[] = readorwrite.split(",");

				selection += "(";
				for (int i = 0; i < readorwriteStr.length; i++) {
					if (i != 0) {
						selection += " or ";
					}
					selection += MyDataTable.READORWRITE + "=?";
					selectionArgs.add(readorwriteStr[i]);
				}
				selection += ")";
			}
			if (!TextUtils.isEmpty(uuid)) {
				if (!selection.equals("")) {
					selection += " and ";
				}
				selection += MyDataTable.UUID + "=?";
				selectionArgs.add(uuid);
			}

			StringBuffer orderBySB = new StringBuffer();

			// 排序
			if (TextUtils.isEmpty(dateType)) {
				// 历史记录
				orderBySB.append(MyDataTable.CREATETIME).append(" desc");
			} else if (dateType.equals(String.valueOf(MyDataTable.MEETTING))) {
				// 会议室
				orderBySB.append(MyDataTable.CREATETIME).append(" desc");
			} else if (dateType.equals(String.valueOf(MyDataTable.NAMECARD))) {
				// 名片
				orderBySB.append("(case ").append(MyDataTable.READORWRITE)
						.append(" when ").append(MyDataTable.TAG_MY_NAMECARD)
						.append(" then 1 else 2 end), ")
						.append(MyDataTable.CREATETIME).append(" desc");
			} else {
				// 其他
				orderBySB.append(MyDataTable.DATATYPE).append(" , ")
						.append(MyDataTable.CREATETIME).append(" desc");
			}

			String groupBy = "";
			String orderBy = orderBySB.toString();

			Cursor c = getDataBase(context).qurey(table, columns, selection,
					(String[]) selectionArgs.toArray(new String[0]), groupBy,
					null, orderBy, limit);

			if (c != null) {
				int cnt = c.getCount();
				c.moveToFirst();
				for (int i = 0; i < cnt; i++) {
					Integer envirType = c.getInt(c
							.getColumnIndex(MyDataTable.DATATYPE));
					Integer envirId = c.getInt(c
							.getColumnIndex(MyDataTable.TABLEID));
					MyData data = new MyData(c.getInt(c
							.getColumnIndex(MyDataTable.ID)), c.getString(c
							.getColumnIndex(MyDataTable.NAME)), envirType,
							envirId, c.getLong(c
									.getColumnIndex(MyDataTable.CREATETIME)),
							c.getInt(c.getColumnIndex(MyDataTable.READORWRITE)));

					switch (envirType) {
					case MyDataTable.MYMODE:
						List<MyMode> _MyModes = MyModeDao.query(context,
								envirId, envirType, null, null, false);
						if (_MyModes != null && _MyModes.size() > 0) {
							data.setMyMode(_MyModes.get(0));
						}

						break;
					case MyDataTable.MEETTING:
						// /???
						// long startTime = System.currentTimeMillis();
						List<Meetting> _Meetings = MeetingDao.query(context,
								envirId, -1, -1, null, null, false);
						if (_Meetings != null && _Meetings.size() > 0) {
							data.setMeeting(_Meetings.get(0));
						}

						break;
					//ashley 0923 删除无用资源
//					case MyDataTable.COUPON:
//						List<Coupon> _Schedules = CouponDao.query(context,
//								envirId, false);
//						if (_Schedules != null && _Schedules.size() > 0) {
//							data.setCoupon(_Schedules.get(0));
//						}
//
//						break;
//					case MyDataTable.LOTTERY:
//						List<Lottery> _Lotterys = LotteryDao.query(context,
//								envirId, false);
//						if (_Lotterys != null && _Lotterys.size() > 0) {
//							data.setLottery(_Lotterys.get(0));
//						}
//
//						break;
//					case MyDataTable.AD:
//						List<Ad> _Ads = AdDao.query(context, envirId, false);
//						if (_Ads != null && _Ads.size() > 0) {
//							data.setAd(_Ads.get(0));
//						}
//						break;
					case MyDataTable.MYMODECUSTOM:
						List<MyMode> _MyModesCustom = MyModeDao.query(context,
								envirId, envirType, null, null, false);
						if (_MyModesCustom != null && _MyModesCustom.size() > 0) {
							data.setMyMode(_MyModesCustom.get(0));
						}
						break;
					case MyDataTable.NAMECARD:
						List<NameCard> _nameCard = NameCardDao.query(context,
								envirId, false);
						if (_nameCard != null && _nameCard.size() > 0) {
							data.setNameCard(_nameCard.get(0));
						}
						break;
					//ashley 0923 删除无用资源
//					case MyDataTable.PARKTICKET:
//						List<ParkTicket> _parkticket = ParkTicketDao.query(
//								context, envirId, false);
//						if (_parkticket != null && _parkticket.size() > 0) {
//							data.setParkticket(_parkticket.get(0));
//						}
//						break;
//					case MyDataTable.MOVIETICKET:
//						List<MovieTicket> _movieticket = MovieTicketDao.query(
//								context, envirId, false);
//						if (_movieticket != null && _movieticket.size() > 0) {
//							data.setMovieticket(_movieticket.get(0));
//						}
//						break;

					default:
						break;
					}
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
	 * @param dataType
	 *            null:所有标签;有值:各类标签
	 * @param readOrWrite
	 * @return
	 */
	public static List<Map<String, Object>> getMyDatasFromDB(Context context,
			String dataType, String readOrWrite) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		try {
			getDataBase(context).open();
			List<MyData> list = query(context, null, dataType, readOrWrite,
					null, null, false);
			if (list != null) {
				String[] detail_title = context.getResources().getStringArray(
						R.array.nfc_detail_title);
				for (MyData e : list) {
					System.out.println("e:" + e);
					map = new HashMap<String, Object>();
					map.put("id", e.id);
					map.put("hideId", e.tId);
					map.put("title", e.n);
					map.put("tagType", e.dt);
					map.put("createTime", MyUtil.dateFormat(e.time));
					// map.put("bg", R.drawable.nfc_tag_bg);
					map.put("readorwrite", e.row);

					StringBuffer str = new StringBuffer();

					switch (e.dt) {
					// case MyDataTable.MYMODE:
					// getMyModeStr(context, e, str);
					// map.put("img", R.drawable.nfc_my);
					// break;
					case MyDataTable.MEETTING:
						Meetting _Meeting = e.getMeeting();
						str.append(_Meeting.c);
						map.put("img", R.drawable.nfc_icon_meeting);
//						map.put("bg", R.drawable.nfc_bg_meetting);
						break;
					//ashley 0923 删除无用资源
//					case MyDataTable.COUPON:
//						Coupon _Schedule = e.getCoupon();
//						// /by lyc
//						str.append(_Schedule.c);
//						map.put("img", R.drawable.nfc_coupon);
//						map.put("tagType", MyDataTable.COUPON);
//						map.put("bg", R.drawable.nfc_bg_coupon);
//						break;
//					case MyDataTable.LOTTERY:
//						Lottery _Lottery = e.getLottery();
//						str.append(_Lottery.c);
//						map.put("img", R.drawable.nfc_lottery);
//						map.put("bg", R.drawable.nfc_bg_lottery);
//						break;
//					case MyDataTable.AD:
//						Ad _Ad = e.getAd();
//						str.append(_Ad.url);
//						map.put("img", R.drawable.nfc_ad);
//						map.put("bg", R.drawable.nfc_bg_ad);
//						break;
						
						
						
					// case MyDataTable.MYMODECUSTOM:
					// MyMode _MyModeCustom = e.getMyMode();
					// String _content = _MyModeCustom.content;
					// str.append(_content);
					// map.put("img", R.drawable.nfc_other);
					// break;

					case MyDataTable.NAMECARD:
						NameCard nameCard = e.getNameCard();
						str.append(nameCard.getTelPhone());
						map.put("img", R.drawable.nfc_icon_card);
//						map.put("bg", R.drawable.nfc_bg_namecard_);
						break;
					case MyDataTable.TEXT:
						str.append(e.n);
						map.put("title", detail_title[0]);
						map.put("img", R.drawable.nfc_text);
						map.put("tagType", MyDataTable.TEXT);
//						map.put("bg", R.drawable.nfc_bg_text);
						break;
					case MyDataTable.WEB:
						map.put("title", detail_title[1]);
						str.append(e.n);
						map.put("img", R.drawable.nfc_web);
//						map.put("bg", R.drawable.nfc_bg_web);
						break;
					case MyDataTable.BUS_CARD:
						map.put("title", detail_title[2]);
						str.append(e.n);
						map.put("img", R.drawable.nfc_icon_bus);
//						map.put("bg", R.drawable.nfc_bg_bus);
						break;
						
					//ashley 0923 删除无用资源
//					case MyDataTable.PARKTICKET:
//						ParkTicket _ParkTicket = e.getParkticket();
//						// map.put("title", detail_title[3]);
//						// 在ListView回显公园票的时候，需要变更为显示公园票的名称
//						// by wunan 2013/06/18 Start
//						map.put("title", _ParkTicket.n);
//						// by wunan 2013/06/18 End
//						str.append(_ParkTicket.c);
//						map.put("img", R.drawable.nfc_parkticket);
//						map.put("bg", R.drawable.nfc_bg_park);
//						break;
//					case MyDataTable.MOVIETICKET:
//						MovieTicket _MovieTicket = e.getMovieticket();
//						// map.put("title", detail_title[4]);
//						// 在ListView回显电影票的时候，需求变更为显示电影票的名称
//						// by wunan 2013/06/18 Start
//						map.put("title", _MovieTicket.n);
//						// by wunan 2013/06/18 End
//						str.append(_MovieTicket.c);
//						map.put("img", R.drawable.nfc_movieticket);
//						map.put("bg", R.drawable.nfc_bg_movie);
//						break;

					default:
						break;
					}
					map.put("info", str.toString());

					datas.add(map);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		} finally {
			getDataBase(context).close();
		}
		return datas;
	}

	public static List<Map<String, Object>> getMyDatasFromDB(Context context,
			String ids, String dataType, String readOrWrite) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		try {
			getDataBase(context).open();
			List<MyData> list = query(context, ids, dataType, readOrWrite,
					null, null, false);
			if (list != null) {
				for (MyData e : list) {
					map = new HashMap<String, Object>();
					map.put("id", e.id);
					map.put("hideId", e.tId);
					map.put("title", e.n);
					map.put("tagType", e.dt);
					map.put("createTime", MyUtil.dateFormat(e.time));
					// map.put("bg", R.drawable.nfc_tag_bg);
					map.put("readorwrite", e.row);

					StringBuffer str = new StringBuffer();

					switch (e.dt) {
					// case MyDataTable.MYMODE:
					// getMyModeStr(context, e, str);
					// map.put("img", R.drawable.nfc_my);
					// break;
					case MyDataTable.MEETTING:
						Meetting _Meeting = e.getMeeting();
						str.append(_Meeting.c);
						map.put("img", R.drawable.nfc_icon_meeting);
//						map.put("bg", R.drawable.nfc_bg_meetting);
						break;
						
					//ashley 0923 删除无用资源
//					case MyDataTable.COUPON:
//						Coupon _Schedule = e.getCoupon();
//						// /by lyc
//						str.append(_Schedule.c);
//						map.put("img", R.drawable.nfc_coupon);
//						map.put("tagType", MyDataTable.COUPON);
//						map.put("bg", R.drawable.nfc_bg_coupon);
//						break;
//					case MyDataTable.LOTTERY:
//						Lottery _Lottery = e.getLottery();
//						str.append(_Lottery.c);
//						map.put("img", R.drawable.nfc_lottery);
//						map.put("bg", R.drawable.nfc_bg_lottery);
//						break;
//					case MyDataTable.AD:
//						Ad _Ad = e.getAd();
//						str.append(_Ad.url);
//						map.put("img", R.drawable.nfc_ad);
//						map.put("bg", R.drawable.nfc_bg_ad);
//						break;
						
						
					// case MyDataTable.MYMODECUSTOM:
					// MyMode _MyModeCustom = e.getMyMode();
					// String _content = _MyModeCustom.content;
					// str.append(_content);
					// map.put("img", R.drawable.nfc_other);
					// break;

					case MyDataTable.NAMECARD:
						NameCard nameCard = e.getNameCard();
						str.append(nameCard.getTelPhone());
						map.put("img", R.drawable.nfc_icon_card);
//						map.put("bg", R.drawable.nfc_bg_namecard_);
						break;
					case MyDataTable.TEXT:
						str.append(e.n);
						map.put("title", "文本标签");
						map.put("img", R.drawable.nfc_text);
						map.put("tagType", MyDataTable.TEXT);
//						map.put("bg", R.drawable.nfc_bg_text);
						break;
					case MyDataTable.WEB:
						str.append(e.n);
						map.put("title", "网址标签");
						map.put("img", R.drawable.nfc_web);
//						map.put("bg", R.drawable.nfc_bg_web);
						break;
					case MyDataTable.BUS_CARD:
						map.put("title", "公交卡");
						str.append(e.n);
						map.put("img", R.drawable.nfc_icon_bus);
//						map.put("bg", R.drawable.nfc_bg_bus);
						break;
						
						
					//ashley 0923 删除无用资源
//					case MyDataTable.PARKTICKET:
//						ParkTicket _ParkTicket = e.getParkticket();
//						map.put("title", "公园门票");
//						str.append(_ParkTicket.c);
//						map.put("img", R.drawable.nfc_parkticket);
//						map.put("bg", R.drawable.nfc_bg_park);
//						break;
//					case MyDataTable.MOVIETICKET:
//						MovieTicket _MovieTicket = e.getMovieticket();
//						map.put("title", "电影票");
//						str.append(_MovieTicket.c);
//						map.put("img", R.drawable.nfc_movieticket);
//						map.put("bg", R.drawable.nfc_bg_movie);
//						break;

					default:
						break;
					}
					map.put("info", str.toString());

					datas.add(map);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		} finally {
			getDataBase(context).close();
		}
		return datas;
	}

	public static String getMyModeStr(Context context, MyData e,
			StringBuffer str) {
		MyMode _MyMode = e.getMyMode();
		String on = context.getResources().getString(R.string.nfc_rbStart);
		String off = context.getResources().getString(R.string.nfc_rbClose);
		String[] elements = context.getResources().getStringArray(
				R.array.nfc_elements);
		if (_MyMode.muteMode == SettingData.ON) {
			str.append(elements[0]).append(",");
		} else if (_MyMode.muteMode == SettingData.DEFAULT_MODE) {
			str.append(context.getResources().getString(R.string.nfc_phone_ling))
					.append(",");
		} else {
			str.append(elements[1]).append(",");
		}
		// if (_MyMode.vibrationMode == SettingData.ON) {
		// }else if (_MyMode.vibrationMode == SettingData.OFF) {
		// str.append(off).append(elements[1]).append(",");
		// }
		if (_MyMode.bluetooth == SettingData.ON) {
			str.append(on).append(elements[4]).append(",");
		} else if (_MyMode.bluetooth == SettingData.OFF) {
			str.append(off).append(elements[4]).append(",");
		}
		if (_MyMode.digitalSwitch == SettingData.ON) {
			str.append(on).append(elements[3]).append(",");
		} else if (_MyMode.digitalSwitch == SettingData.OFF) {
			str.append(off).append(elements[3]).append(",");
		}
		if (_MyMode.wifiSwitch == SettingData.ON) {
			str.append(on).append(elements[2]).append(",");
		} else if (_MyMode.wifiSwitch == SettingData.OFF) {
			str.append(off).append(elements[2]).append(",");
		}
		String content = str.toString();
		if (!TextUtils.isEmpty(content))
			content = content.substring(0, content.length() - 1);
		return content;
	}

	/***
	 * 更新下数据库的createTime
	 */
	private static void updateNameCardTime(Context context, NameCard nameCard) {
		StringBuilder readOrWrite = new StringBuilder();
		readOrWrite.append(MyDataTable.TAG_READFROMNFC).append(",")
				.append(MyDataTable.TAG_READ_FAVORITE).append(",")
				.append(MyDataTable.TAG_MY_NAMECARD);
		List<MyData> list = MyDataDao.query(context, null,
				String.valueOf(MyDataTable.NAMECARD), readOrWrite.toString(),
				null, null, false);
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getNameCard().getName().equals(nameCard.getName())) {
				MyDataDao.update(context, list.get(i));
				break;
			}
		}
	}

	/**
	 * 更新内容
	 */
	private static void updateNameCard(Context context, NameCard nameCard) {
		MyData mydata;
		StringBuilder readOrWrite = new StringBuilder();
		readOrWrite.append(MyDataTable.TAG_READFROMNFC).append(",")
				.append(MyDataTable.TAG_READ_FAVORITE).append(",")
				.append(MyDataTable.TAG_MY_NAMECARD);
		List<MyData> list = MyDataDao.query(context, null,
				String.valueOf(MyDataTable.NAMECARD), readOrWrite.toString(),
				null, null, true);
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getNameCard().getName().equals(nameCard.getName())) {
				NameCard contactAllInfoCache = new NameCard(list.get(i)
						.getNameCard().getId(), nameCard.getName(),
						nameCard.getContactIcon(), nameCard.getTelPhone(),
						nameCard.getFax(), nameCard.getCompanyName(),
						nameCard.getCompanyNetAddress(),
						nameCard.getCompanyTelPhone(), nameCard.getSection(),
						nameCard.getRank(), nameCard.getAddress(),
						nameCard.getEmail(), nameCard.getDescription(),
						nameCard.getShowFlag(), nameCard.getShortCut());
				// mMyData.id = mydataId;
				mydata = MyDataDao.query(context, list.get(i).id,
						MyDataTable.NAMECARD);
				mydata.n = contactAllInfoCache.getName();
				mydata.setNameCard(contactAllInfoCache);
				MyDataDao.update(context, mydata);
				updateContact(context,contactAllInfoCache);
				break;
			}
		}
	}


	public static void updateContact(Context context, NameCard tmp) {
		// 插入raw_contacts表，并获取_id属性
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		ContentResolver resolver = context.getContentResolver();
		context.getContentResolver().delete(uri, ContactsContract.Contacts.DISPLAY_NAME + "=?", new String[] { tmp.getName() });
		ContentValues values = new ContentValues();
		long contact_id = ContentUris.parseId(resolver.insert(uri, values));
		// 插入data表
		uri = Uri.parse("content://com.android.contacts/data");
		// add Name
		values.put("raw_contact_id", contact_id);
		values.put(Data.MIMETYPE, "vnd.android.cursor.item/name");
		// values.put("data2", "zdong");
		values.put("data1", tmp.getName().toString());
		resolver.insert(uri, values);
		values.clear();
		// add Phone
		values.put("raw_contact_id", contact_id);
		values.put(Data.MIMETYPE, "vnd.android.cursor.item/phone_v2");
		values.put("data2", "2"); // 手机
		values.put("data1", tmp.getTelPhone().toString());
		resolver.insert(uri, values);
		values.clear();
		// add email
		values.put("raw_contact_id", contact_id);
		values.put(Data.MIMETYPE, "vnd.android.cursor.item/email_v2");
		values.put("data2", "2"); // 单位
		values.put("data1", tmp.getEmail().toString());
		resolver.insert(uri, values);
	}

	public static boolean readData(final Context context, String jsonStr,
			boolean isNeedOpenDB) {
		Gson gson = new Gson();
		ProtocolTitle protocolTitle = null;
		try {
			protocolTitle = gson.fromJson(jsonStr, ProtocolTitle.class);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		if (protocolTitle == null) {
			// 为了支持其他程序写的标签
			MyData data_text = new MyData(null, jsonStr, MyDataTable.TEXT, -1,
					System.currentTimeMillis(), MyDataTable.TAG_READFROMNFC);
			long id = MyDataDao.insert(context, data_text, true);
			data_text.id = MyUtil.longToInteger(id);
			data_text.setText(jsonStr);

			Intent intent = new Intent(context, DetailTextActivity.class);
			intent.putExtra(Constant.MYDATA_DATATYPE, MyDataTable.TEXT);
			intent.putExtra(Constant.MYDATA_ID, data_text.id);
			context.startActivity(intent);
			return true;
		}
		MyData data = protocolTitle.getData();
		if (data == null) {
			return false;
		}
		try {
			if (isNeedOpenDB) {
				getDataBase(context).open();
			}

			Intent intent = new Intent();
			intent.putExtra(Constant.INTENT_TYPE, Constant.INTENT_TYPE_READ_TAG);
			switch (data.dt) {
			case MyDataTable.MEETTING: {
				final Meetting meeting = data.getMeeting();

				boolean flag = false;
				StringBuilder sb = new StringBuilder();
				sb.append(MyDataTable.TAG_READ_FAVORITE).append(",")
						.append(MyDataTable.TAG_READFROMNFC);
				getDataBase(context).open();
				List<MyData> list1 = query(context, null,
						String.valueOf(MyDataTable.MEETTING), sb.toString(),
						null, null, false);
				for (int i = 0; i < list1.size(); i++) {
					if (list1.get(i).dt == MyDataTable.MEETTING) {
						String str = list1.get(i).getMeeting().n;
						if (meeting.n.equals(str))
							flag = true;
					}
				}

				if (!flag) {
					long id_meeting = MeetingDao
							.insert(context, meeting, false);
					meeting.id = MyUtil.longToInteger(id_meeting);

					MyData data_meeting = new MyData(null, meeting.n,
							MyDataTable.MEETTING,
							MyUtil.longToInteger(id_meeting),
							System.currentTimeMillis(),
							MyDataTable.TAG_READFROMNFC);
					long id_mydata = MyDataDao.insert(context, data_meeting,
							false);
					data_meeting.id = MyUtil.longToInteger(id_mydata);
					data_meeting.setMeeting(meeting);
					final List<MyMode> myModes = MyModeDao.query(context, null,
							MyDataTable.MYMODE, null, null, true);
					
					// by yuzhao 2013/7/4 start
					// Integer toWifiMode = myModes.get(0).wifiSwitch;
					Integer toWifiMode = Integer
							.valueOf(meeting.mm.split(",")[3]);
					int curentWIFIState = ((WifiManager) context
							.getSystemService(Context.WIFI_SERVICE))
							.getWifiState();
					if (toWifiMode == SettingData.OFF) {
						if (curentWIFIState != WifiManager.WIFI_STATE_DISABLED)
							MyUtil.ltoast(context, "正在关闭无线WLAN");
					} else if (toWifiMode == SettingData.ON) {
						if (curentWIFIState != WifiManager.WIFI_STATE_ENABLED)
							// by yuzhao 2013/7/11 start
							MyUtil.lltoast(context, "正在连接无线WLAN");
							// by yuzhao 2013/7/11 end
					}
					// by yuzhao 2013/7/4 end					
					
					new Thread() {
						public void run() {

							MyUtil.setPhoneMode(context, myModes, meeting.mm,
									meeting.ss, meeting.pw);

						};

					}.start();


					intent.setClass(context, DetailMeetingActivity.class);
					intent.putExtra(Constant.MYDATA_ID, data_meeting.id);
					intent.putExtra(Constant.MYDATA_DATATYPE,
							MyDataTable.MEETTING);
					intent.putExtra(Constant.INTENT_FORM_NFC,
							Constant.TAG_FORMNFC);
					context.startActivity(intent);
				} else {
					MyUtil.showMessage(R.string.nfc_meetting_exist, context, true);
					StringBuilder readOrWrite = new StringBuilder();
					readOrWrite.append(MyDataTable.TAG_READFROMNFC).append(",")
							.append(MyDataTable.TAG_READ_FAVORITE).append(",")
							.append(MyDataTable.TAG_MY_NAMECARD);
					List<MyData> list = MyDataDao.query(context, null,
							String.valueOf(MyDataTable.MEETTING),
							readOrWrite.toString(), null, null, true);
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).getMeeting().n.equals(meeting.n)) {
							MyData mydata;
							Meetting tmp = new Meetting(list.get(i)
									.getMeeting().id, meeting.n, meeting.p,
									meeting.pl, meeting.c, meeting.st,
									meeting.mm, meeting.ss, meeting.pw);
							mydata = MyDataDao.query(context, list.get(i).id,
									MyDataTable.MEETTING);
							mydata.setMeeting(tmp);
							MyDataDao.update(context, mydata);
							
							// by yuzhao 2013/6/20 start
							// Integer toWifiMode = myModes.get(0).wifiSwitch;
							Integer toWifiMode = Integer
									.valueOf(meeting.mm.split(",")[3]);
							int curentWIFIState = ((WifiManager) context
									.getSystemService(Context.WIFI_SERVICE))
									.getWifiState();
							if (toWifiMode == SettingData.OFF) {
								// by yuzhao 2013/6/18 start
								if (curentWIFIState != WifiManager.WIFI_STATE_DISABLED)
									MyUtil.ltoast(context, "正在关闭无线WLAN");
								// by yuzhao 2013/6/18 end
							} else if (toWifiMode == SettingData.ON) {
								if (curentWIFIState != WifiManager.WIFI_STATE_ENABLED)
									// by yuzhao 2013/7/11 start
									MyUtil.lltoast(context, "正在连接无线WLAN");
									// by yuzhao 2013/7/11 end
							}
							
							
							final List<MyMode> myModes = MyModeDao.query(context, null,
									MyDataTable.MYMODE, null, null, true);
							new Thread() {
								public void run() {

									MyUtil.setPhoneMode(context, myModes, meeting.mm,
											meeting.ss, meeting.pw);

								};

							}.start();
							// by yuzhao 2013/6/20 end		
							break;
						}
					}
				}
				return true;
			}
			//ashley 0923 删除无用资源
//			case MyDataTable.COUPON: {
//				Coupon coupon = data.getCoupon();
//				long id_coupon = CouponDao.insert(context, coupon, false);
//
//				MyData data_coupon = new MyData(null, coupon.n,
//						MyDataTable.COUPON, MyUtil.longToInteger(id_coupon),
//						System.currentTimeMillis(), MyDataTable.TAG_READFROMNFC);
//				long id_myData = MyDataDao.insert(context, data_coupon, false);
//				data_coupon.id = MyUtil.longToInteger(id_myData);
//				data_coupon.setCoupon(coupon);
//
//				intent.setClass(context, DetailCouponActivity.class);
//				intent.putExtra(Constant.MYDATA_ID, data_coupon.id);
//				intent.putExtra(Constant.MYDATA_DATATYPE, MyDataTable.COUPON);
//				intent.putExtra(Constant.INTENT_FORM_NFC, Constant.TAG_FORMNFC);
//				context.startActivity(intent);
//				return true;
//			}
//			case MyDataTable.LOTTERY: {
//				List<MyData> mds = MyDataDao.query(context, null,
//						String.valueOf(MyDataTable.LOTTERY), null, null,
//						data.uuid, false);
//				if (mds.size() > 0) {
//					MyUtil.showMessage(R.string.nfc_msg_repeat_lottery_tag, context);
//					return false;
//				}
//				Lottery lottery = data.getLottery();
//				long id_lottery = LotteryDao.insert(context, lottery, false);
//
//				MyData data_lottery = new MyData(null, lottery.n,
//						MyDataTable.LOTTERY, MyUtil.longToInteger(id_lottery),
//						System.currentTimeMillis(),
//						MyDataTable.TAG_READFROMNFC, data.uuid);
//				long id_myData = MyDataDao.insert(context, data_lottery, false);
//				data_lottery.id = MyUtil.longToInteger(id_myData);
//				data_lottery.setLottery(lottery);
//				intent.putExtra("flag", Constant.fromNFC);
//				intent.setClass(context, DetailLotteryActivity.class);
//				intent.putExtra(Constant.MYDATA_ID, data_lottery.id);
//				intent.putExtra(Constant.MYDATA_DATATYPE, MyDataTable.LOTTERY);
//				intent.putExtra(Constant.INTENT_FORM_NFC, Constant.TAG_FORMNFC);
//				context.startActivity(intent);
//				return true;
//			}
//			case MyDataTable.AD: {
//				Ad ad = data.getAd();
//				long id_ad = AdDao.insert(context, ad, false);
//
//				MyData data_ad = new MyData(null, ad.n, MyDataTable.AD,
//						MyUtil.longToInteger(id_ad),
//						System.currentTimeMillis(), MyDataTable.TAG_READFROMNFC);
//				long id_myData = MyDataDao.insert(context, data_ad, false);
//				data_ad.id = MyUtil.longToInteger(id_myData);
//				data_ad.setAd(ad);
//				intent.setClass(context, DetailAdActivity.class);
//				intent.putExtra(Constant.MYDATA_ID, data_ad.id);
//				intent.putExtra(Constant.MYDATA_DATATYPE, MyDataTable.AD);
//				intent.putExtra(Constant.INTENT_FORM_NFC, Constant.TAG_FORMNFC);
//				context.startActivity(intent);
//				return true;
//			}
			case MyDataTable.NAMECARD: {
				final NameCard nameCard = data.getNameCard();
				boolean flag = false;
				int num = -1;
				StringBuilder sb = new StringBuilder();
				sb.append(MyDataTable.TAG_READ_FAVORITE).append(",")
						.append(MyDataTable.TAG_READFROMNFC).append(",")
						.append(MyDataTable.TAG_MY_NAMECARD);
				getDataBase(context).open();
				List<MyData> list1 = query(context, null,
						String.valueOf(MyDataTable.NAMECARD), sb.toString(),
						null, null, true);
				for (int i = 0; i < list1.size(); i++) {
					if (list1.get(i).dt == MyDataTable.NAMECARD) {
						String str = list1.get(i).getNameCard().getName();
						if (nameCard.getName().equals(str)) {
							flag = true;
							num = i;
						}
					}
				}
				getDataBase(context).close();
				if (!flag) {
					long id_nameCard = NameCardDao.insert(context, nameCard,
							true);

					MyData data_nameCard = new MyData(null, nameCard.getName(),
							MyDataTable.NAMECARD,
							MyUtil.longToInteger(id_nameCard), -1l,
							MyDataTable.TAG_READFROMNFC);
					long id_myData = MyDataDao.insert(context, data_nameCard,
							true);
					data_nameCard.id = MyUtil.longToInteger(id_myData);
					data_nameCard.setNameCard(nameCard);

					intent.setClass(context, NameCardManageActivity.class);
					intent.putExtra(Constant.MYDATA_ID,
							MyUtil.longToInteger(id_myData));
					intent.putExtra(Constant.MYDATA_DATATYPE,
							MyDataTable.NAMECARD);
					intent.putExtra(Constant.INTENT_FORM_NFC,
							Constant.TAG_FORMNFC);
					intent.putExtra(Constant.INTENT_TYPE,
							Constant.INTENT_TYPE_READ_TAG);
					intent.setAction(Intent.ACTION_DEFAULT);
					context.startActivity(intent);
				} else {
					if (list1.get(num).getNameCard().equals(nameCard)) {
						MyUtil.showMessage(R.string.nfc_nc_exist, context, true);
						updateNameCardTime(context, nameCard);
					} else {
						Builder builder = new Builder(context);
						builder.setMessage(R.string.nfc_nc_not_same);
						builder.setTitle(R.string.nfc_nc_tip);
						builder.setPositiveButton(R.string.nfc_cancel,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										updateNameCardTime(context, nameCard);
										dialog.dismiss();
									}
								});
						builder.setNegativeButton(R.string.nfc_ok,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										updateNameCard(context, nameCard);
										dialog.dismiss();
									}
								});
						builder.create().show();
					}
				}
				return true;
			}
			//ashley 0923 删除无用资源
//			case MyDataTable.MOVIETICKET:
//				MovieTicket m = data.getMovieticket();
//				long movie_id = MovieTicketDao.insert(context, m, false);
//				MyData data_m = new MyData(null, m.n, MyDataTable.MOVIETICKET,
//						MyUtil.longToInteger(movie_id), -1l,
//						MyDataTable.TAG_READFROMNFC);
//				long id_mmm = MyDataDao.insert(context, data_m, false);
//				data_m.id = MyUtil.longToInteger(id_mmm);
//				data_m.setMovieticket(m);
//				intent.setClass(context, DetailMovieActivity.class);
//				intent.putExtra(Constant.MYDATA_ID, data_m.id);
//				intent.putExtra(Constant.MYDATA_DATATYPE,
//						MyDataTable.MOVIETICKET);
//				intent.putExtra(Constant.INTENT_FORM_NFC, Constant.TAG_FORMNFC);
//				context.startActivity(intent);
//
//				return true;
//			case MyDataTable.PARKTICKET:
//				ParkTicket p = data.getParkticket();
//				long p_id = ParkTicketDao.insert(context, p, false);
//				MyData data_p = new MyData(null, p.n, MyDataTable.PARKTICKET,
//						MyUtil.longToInteger(p_id), -1l,
//						MyDataTable.TAG_READFROMNFC);
//				long id_myData1 = MyDataDao.insert(context, data_p, false);
//				data_p.id = MyUtil.longToInteger(id_myData1);
//				data_p.setParkticket(p);
//				intent.setClass(context, DetailParkActivity.class);
//				intent.putExtra(Constant.MYDATA_ID, data_p.id);
//				intent.putExtra(Constant.MYDATA_DATATYPE,
//						MyDataTable.PARKTICKET);
//				intent.putExtra(Constant.INTENT_FORM_NFC, Constant.TAG_FORMNFC);
//				context.startActivity(intent);
//
//				return true;
			case MyDataTable.TEXT: {
				// NFC翼卡通程序写的标签，属于文本标签

				MyData data_text = new MyData(null, data.n, MyDataTable.TEXT,
						-1, -1l, MyDataTable.TAG_READFROMNFC);
				long id_myData = MyDataDao.insert(context, data_text, false);
				// data_text.id = MyUtil.longToInteger(id_myData);
				// data_text.setText(jsonStr);

				intent.setClass(context, DetailTextActivity.class);
				intent.putExtra(Constant.MYDATA_ID,
						MyUtil.longToInteger(id_myData));
				intent.putExtra(Constant.MYDATA_DATATYPE, MyDataTable.TEXT);
				intent.putExtra(Constant.INTENT_FORM_NFC, Constant.TAG_FORMNFC);
				context.startActivity(intent);
				return true;
			}
			case MyDataTable.WEB: {

				MyData data_web = new MyData(null, data.n, MyDataTable.WEB, -1,
						-1l, MyDataTable.TAG_READFROMNFC);
				long id_myData = MyDataDao.insert(context, data_web, false);
				// data_web.id = MyUtil.longToInteger(id_myData);
				// data_web.setText(jsonStr);

				intent.setClass(context, DetailWebActivity.class);
				intent.putExtra(Constant.MYDATA_ID,
						MyUtil.longToInteger(id_myData));
				intent.putExtra(Constant.MYDATA_DATATYPE, MyDataTable.WEB);
				intent.putExtra(Constant.INTENT_FORM_NFC, Constant.TAG_FORMNFC);
				context.startActivity(intent);
				return true;
			}
			default:

				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		} finally {
			if (isNeedOpenDB) {
				getDataBase(context).close();
			}
		}

		return false;
	}

	public static MyData query(Context context, Integer mydataId,
			Integer dataType) {
		// TODO Auto-generated method stub
		MyData data = null;
		try {
			getDataBase(context).open();

			StringBuilder sb_selection = new StringBuilder();

			String selection = "";
			String subTab = "";
			// true表示只查询MyDataTable表
			boolean isMyDataTable = false;

			switch (dataType) {
			case MyDataTable.MYMODE:
				subTab = MyModeTable.URI;
				break;
			case MyDataTable.MEETTING:
				subTab = MeetingTable.URI;
				break;
			//ashley 0923 删除无用资源
//			case MyDataTable.COUPON:
//				subTab = CouponTable.URI;
//				break;
//			case MyDataTable.LOTTERY:
//				subTab = LotteryTable.URI;
//				break;
//			case MyDataTable.AD:
//				subTab = AdTable.URI;
//				break;
			case MyDataTable.MYMODECUSTOM:
				subTab = MyModeTable.URI;
				break;
			case MyDataTable.NAMECARD:
				subTab = NameCardTable.URI;
				break;
			//ashley 0923 删除无用资源
//			case MyDataTable.MOVIETICKET:
//				subTab = MovieTicketTable.URI;
//				break;
//			case MyDataTable.PARKTICKET:
//				subTab = ParkTicketTable.URI;
//				break;

			case MyDataTable.TEXT:
				isMyDataTable = true;
				break;

			case MyDataTable.WEB:
				isMyDataTable = true;
				break;

			case MyDataTable.BUS_CARD:
				isMyDataTable = true;
				break;

			default:
				break;
			}
			StringBuilder tables = new StringBuilder();
			tables.append(table);
			if (!isMyDataTable) {
				tables.append(",").append(subTab);
				selection = sb_selection.append(MyDataTable.URI).append(".")
						.append(MyDataTable.TABLEID).append("=").append(subTab)
						.append(".id").toString();
			}
			List<String> selectionArgs = new ArrayList<String>();
			// MyData ID
			if (mydataId != null) {
				if (!selection.equals("")) {
					selection += " and ";
				}
				selection += MyDataTable.URI + "." + MyDataTable.ID + "=?";
				selectionArgs.add(String.valueOf(mydataId));
			}

			Cursor c = getDataBase(context).qurey(tables.toString(), null,
					selection, (String[]) selectionArgs.toArray(new String[0]),
					null, null, null, null);

			if (c != null && c.getCount() > 0) {
				c.moveToFirst();
				Integer envirType = c.getInt(c
						.getColumnIndex(MyDataTable.DATATYPE));
				Integer envirId = c.getInt(c
						.getColumnIndex(MyDataTable.TABLEID));
				data = new MyData(c.getInt(c.getColumnIndex(MyDataTable.ID)),
						c.getString(c.getColumnIndex(MyDataTable.NAME)),
						envirType, envirId, c.getLong(c
								.getColumnIndex(MyDataTable.CREATETIME)),
						c.getInt(c.getColumnIndex(MyDataTable.READORWRITE)));

				switch (envirType) {
				case MyDataTable.MYMODE:
					MyMode _MyMode = new MyMode(
							c.getInt(c.getColumnIndex(MyModeTable.ID)),
							c.getString(c.getColumnIndex(MyModeTable.NAME)),
							c.getInt(c.getColumnIndex(MyModeTable.MUTEMODE)),
							c.getInt(c.getColumnIndex(MyModeTable.WIFISWITCH)),
							c.getInt(c
									.getColumnIndex(MyModeTable.DIGITALSWITCH)),
							c.getInt(c.getColumnIndex(MyModeTable.BLUETOOTH)),
							c.getString(c.getColumnIndex(MyModeTable.WIFISSID)),
							c.getString(c
									.getColumnIndex(MyModeTable.WIFIPASSWORD)),
							c.getString(c.getColumnIndex(MyModeTable.CONTENT)),
							c.getInt(c.getColumnIndex(MyModeTable.MODEFLAG)));
					data.setMyMode(_MyMode);

					break;
				case MyDataTable.MEETTING:
					// /???
					// long startTime = System.currentTimeMillis();
					// List<Meeting> _Meetings = MeetingDao.query(context,
					// envirId, -1, -1, null, null, false);
					Meetting _Meeting = new Meetting(
							c.getInt(c.getColumnIndex(MeetingTable.ID)),
							c.getString(c.getColumnIndex(MeetingTable.NAME)),
							c.getString(c.getColumnIndex(MeetingTable.PARTNER)),
							c.getString(c.getColumnIndex(MeetingTable.PLACE)),
							c.getString(c.getColumnIndex(MeetingTable.CONTENT)),
							c.getLong(c.getColumnIndex(MeetingTable.STARTTIME)),
							c.getString(c.getColumnIndex(MeetingTable.MODE)),
							c.getString(c.getColumnIndex(MeetingTable.WIFISSID)),
							c.getString(c
									.getColumnIndex(MeetingTable.WIFIPASSWORD)));
					data.setMeeting(_Meeting);

					break;

				//ashley 0923 删除无用资源
//				case MyDataTable.COUPON:
//					Coupon _Coupon = new Coupon(
//							c.getInt(c.getColumnIndex(CouponTable.ID)),
//							c.getString(c.getColumnIndex(CouponTable.NAME)),
//							c.getString(c.getColumnIndex(CouponTable.CONTENT)),
//							c.getString(c.getColumnIndex(CouponTable.DISCOUNT)),
//							c.getString(c.getColumnIndex(CouponTable.VALID)),
//							c.getString(c.getColumnIndex(CouponTable.ADDRESS)),
//							c.getString(c.getColumnIndex(CouponTable.TEL)),
//							c.getString(c
//									.getColumnIndex(CouponTable.ACTIVATIONCODE)));
//					data.setCoupon(_Coupon);
//					break;
//				case MyDataTable.LOTTERY:
//					Lottery lottery = new Lottery(
//							c.getInt(c.getColumnIndex(LotteryTable.ID)),
//							c.getString(c.getColumnIndex(LotteryTable.NAME)),
//							c.getString(c.getColumnIndex(LotteryTable.CONTENT)),
//							c.getString(c.getColumnIndex(LotteryTable.TELLIST)),
//							c.getString(c.getColumnIndex(LotteryTable.AWARDS)),
//							c.getInt(c.getColumnIndex(LotteryTable.USERORADMIN)));
//					data.setLottery(lottery);
//					break;
//				case MyDataTable.AD:
//					Ad _Ad = new Ad(c.getInt(c.getColumnIndex(AdTable.ID)),
//							c.getString(c.getColumnIndex(AdTable.NAME)),
//							c.getString(c.getColumnIndex(AdTable.URL)));
//					data.setAd(_Ad);
//					break;
				case MyDataTable.MYMODECUSTOM:
					MyMode _MyModeCustom = new MyMode(
							c.getInt(c.getColumnIndex(MyModeTable.ID)),
							c.getString(c.getColumnIndex(MyModeTable.NAME)),
							c.getInt(c.getColumnIndex(MyModeTable.MUTEMODE)),
							c.getInt(c.getColumnIndex(MyModeTable.WIFISWITCH)),
							c.getInt(c
									.getColumnIndex(MyModeTable.DIGITALSWITCH)),
							c.getInt(c.getColumnIndex(MyModeTable.BLUETOOTH)),
							c.getString(c.getColumnIndex(MyModeTable.WIFISSID)),
							c.getString(c
									.getColumnIndex(MyModeTable.WIFIPASSWORD)),
							c.getString(c.getColumnIndex(MyModeTable.CONTENT)),
							c.getInt(c.getColumnIndex(MyModeTable.MODEFLAG)));
					data.setMyMode(_MyModeCustom);
					break;
				case MyDataTable.NAMECARD:
					NameCard _NameCard = new NameCard(
							c.getInt(c.getColumnIndex(NameCardTable.ID)),
							c.getString(c.getColumnIndex(NameCardTable.NAME)),
							c.getBlob(c
									.getColumnIndex(NameCardTable.CONTACTICON)),
							c.getString(c
									.getColumnIndex(NameCardTable.TELPHONE)),
							c.getString(c.getColumnIndex(NameCardTable.FAX)),
							c.getString(c
									.getColumnIndex(NameCardTable.COMPANYNAME)),
							c.getString(c
									.getColumnIndex(NameCardTable.COMPANYNETADDRESS)),
							c.getString(c
									.getColumnIndex(NameCardTable.COMPANYPHONE)),
							c.getString(c.getColumnIndex(NameCardTable.SECTION)),
							c.getString(c.getColumnIndex(NameCardTable.RANK)),
							c.getString(c.getColumnIndex(NameCardTable.ADDRESS)),
							c.getString(c.getColumnIndex(NameCardTable.EMAIL)),
							c.getString(c
									.getColumnIndex(NameCardTable.DESCRIPTION)),
							c.getShort(c.getColumnIndex(NameCardTable.SHOWFLAG)),
							c.getInt(c.getColumnIndex(NameCardTable.SHORTCUT)));
					data.setNameCard(_NameCard);

					break;
				//ashley 0923 删除无用资源
//				case MyDataTable.MOVIETICKET:
//					MovieTicket _MovieTicket = new MovieTicket(
//							c.getInt(c.getColumnIndex(MovieTicketTable.ID)),
//							c.getString(c.getColumnIndex(MovieTicketTable.NAME)),
//							c.getString(c
//									.getColumnIndex(MovieTicketTable.COMPANY)),
//							c.getString(c
//									.getColumnIndex(MovieTicketTable.CONTENT)),
//							c.getString(c
//									.getColumnIndex(MovieTicketTable.VALID)),
//							c.getString(c
//									.getColumnIndex(MovieTicketTable.ORIGINAL)),
//							c.getString(c
//									.getColumnIndex(MovieTicketTable.PRICE)),
//							c.getString(c
//									.getColumnIndex(MovieTicketTable.ADDRESS)),
//							c.getString(c.getColumnIndex(MovieTicketTable.TEL)),
//							c.getString(c
//									.getColumnIndex(MovieTicketTable.DIRECTOR)),
//							c.getString(c
//									.getColumnIndex(MovieTicketTable.STARRING)),
//							c.getString(c
//									.getColumnIndex(MovieTicketTable.ACTIVATIONCODE)));
//					data.setMovieticket(_MovieTicket);
//					break;
//				case MyDataTable.PARKTICKET:
//					ParkTicket _ParkTicket = new ParkTicket(
//							c.getInt(c.getColumnIndex(ParkTicketTable.ID)),
//							c.getString(c.getColumnIndex(ParkTicketTable.NAME)),
//							c.getString(c
//									.getColumnIndex(ParkTicketTable.CONTENT)),
//							c.getString(c
//									.getColumnIndex(ParkTicketTable.ORIGINAL)),
//							c.getString(c.getColumnIndex(ParkTicketTable.PRICE)),
//							c.getString(c
//									.getColumnIndex(ParkTicketTable.ADDRESS)),
//							c.getString(c
//									.getColumnIndex(ParkTicketTable.OPENINGHOURS)),
//							c.getString(c
//									.getColumnIndex(ParkTicketTable.SCENICTHEME)),
//							c.getString(c.getColumnIndex(ParkTicketTable.TYPE)),
//							c.getString(c.getColumnIndex(ParkTicketTable.TEL)),
//							c.getString(c
//									.getColumnIndex(ParkTicketTable.ACTIVATIONCODE)));
//					data.setParkticket(_ParkTicket);
//					break;

				default:
					break;
				}
				c.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		} finally {
			getDataBase(context).close();
		}
		return data;
	}

	//ashley 0923 删除无用资源
	public static final String[] tables = new String[] { MyModeTable.URI,
		MeetingTable.URI, MyModeTable.URI, NameCardTable.URI, MyDataTable.URI };
//	public static final String[] tables = new String[] { MyModeTable.URI,
//			MeetingTable.URI, CouponTable.URI, LotteryTable.URI, AdTable.URI,
//			MyModeTable.URI, NameCardTable.URI, MovieTicketTable.URI,
//			ParkTicketTable.URI, MyDataTable.URI };

	// @SuppressWarnings("unchecked")
	public static void delete(Context context, List<Map<String, Object>> ids) {
		try {
			getDataBase(context).open();
			if (ids != null) {
				String selection = "";
				List<String> selectionArgs = new ArrayList<String>();

				for (Map<String, Object> map : ids) {
					List<Integer> tableIds = (List<Integer>) map.get("ids");
					if (tableIds != null) {
						String _table = (String) map.get("table");
						for (int i = 0; i < tableIds.size(); i++) {
							if (selection.equals("")) {
								if (_table.equals(MyDataTable.URI)) {
									selection += MyDataTable.ID + " in (?";
								} else {
									selection += "id in (?";
								}
							} else {
								selection += ",?";
							}
							selectionArgs.add(String.valueOf(tableIds.get(i)));
						}
						if (!selection.equals("")) {
							selection += ")";
						}
						getDataBase(context)
								.delete(_table,
										selection,
										(String[]) selectionArgs
												.toArray(new String[0]));
						selection = "";
						selectionArgs.clear();
					}
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		} finally {
			getDataBase(context).close();
		}

	}

}
