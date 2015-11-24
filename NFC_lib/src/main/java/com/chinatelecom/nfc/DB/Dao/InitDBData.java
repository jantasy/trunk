package com.chinatelecom.nfc.DB.Dao;

import java.util.Calendar;

import android.content.Context;

import com.chinatelecom.nfc.R;
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
import com.chinatelecom.nfc.DB.Provider.MyDataTable;
import com.chinatelecom.nfc.DB.Provider.SettingData;
import com.chinatelecom.nfc.Debug.MyDebug;
import com.chinatelecom.nfc.Utils.MyUtil;


public class InitDBData extends SQLiteDao{

	public InitDBData(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	
	public static void initDB(Context context){
		try {
			getDataBase(context).open();
			clearTables();
			
			// -1-
			String []modes = context.getResources().getStringArray(R.array.nfc_enviroments);
//			NetworkManager _NetworkManager = new NetworkManager(context);
//			int digitalSwitch = SettingData.DEFAULT_MODE;
//			int wifiSwitch = SettingData.DEFAULT_MODE;
//			
//			if(_NetworkManager.isMobileConnected()){
//				digitalSwitch = SettingData.ON;
//			}else{
//				digitalSwitch = SettingData.OFF;
//			}
//			if(_NetworkManager.isWifiConnected()){
//				wifiSwitch = SettingData.ON;
//			}else{
//				wifiSwitch = SettingData.OFF;
//			}
			MyMode _MyMode = new MyMode(null, modes[0], 
					SettingData.DEFAULT_MODE, SettingData.DEFAULT_MODE, SettingData.DEFAULT_MODE, SettingData.DEFAULT_MODE,
					"", "", 
					"", 
					MyDataTable.MYMODE);
			long id = MyModeDao.insert(context, _MyMode, false);
			MyData environments = new MyData(null,modes[0],MyDataTable.MYMODE,MyUtil.longToInteger(id),-1l,MyDataTable.TAG_WRITETAG);
			MyDataDao.insert(context, environments, false);
			
			// -6-
			NameCard namecard = new NameCard(null, "我的名字", null, null,null, null,
					null, null, null, null, null, null, null, (short) 0x002c, 0);
			long id_namecard = NameCardDao.insert(context, namecard, false);
			MyData e_namecard = new MyData(null, namecard.getName(),
					MyDataTable.NAMECARD, MyUtil.longToInteger(id_namecard),
					-1l, MyDataTable.TAG_MY_NAMECARD);
			MyDataDao.insert(context, e_namecard, false);
			
			if(MyDebug.TEST_DATA){
				// -3-
				Calendar cal = Calendar.getInstance();
				cal.set(2013, 2, 14, 13, 30);
				
				StringBuilder sb = new StringBuilder();
				sb.append(SettingData.DEFAULT_MODE).append(",")
				.append(SettingData.OFF).append(",")
				.append(SettingData.ON).append(",")
				.append(SettingData.ON);
				
				
				NameCard nc = new NameCard(null, "刘婷", null,"15910415444", null,  "软通动力",
						"http://www.isoftstone.com/", null, "松下", "软件工程师",
						"软件园", "aijlouy@163.com", "86后",(short)0x002c, 0);
				long id_nc =NameCardDao.insert(context, nc, false);
				MyData e_nc = new MyData(null,nc.getName(),MyDataTable.NAMECARD,MyUtil.longToInteger(id_nc),-1l,MyDataTable.TAG_WRITETAG);
				MyDataDao.insert(context, e_nc, false);
				
				
				Meetting _Meeting = new Meetting(null, "NFC工具集的会议", "小刘，小李，小高，小小，风行", "天鹅座", "关于NFC工具集的会议，准时参加，不能缺席。关于NFC工具集的会议，准时参加，不能缺席。关于NFC工具集的会议，准时参加，不能缺席。关于NFC工具集的会议，准时参加，不能缺席。", 
						cal.getTimeInMillis(),sb.toString(), "TP-LINK_hems", "hems1235");
				long id_meetting = MeetingDao.insert(context, _Meeting, false);
				MyData e_meetting = new MyData(null,"NFC工具集的会议",MyDataTable.MEETTING,MyUtil.longToInteger(id_meetting),-1l,MyDataTable.TAG_WRITETAG);
				MyDataDao.insert(context, e_meetting, false);
				
				//ashley 0923 删除无用资源
//				// -4-
//				Coupon _Schedule = new Coupon(null, "优惠券","麦当劳的优惠券","6","2013年04月30日止","一号店","8001234567","asdjalsrweriqnwpeorewpo8237423shflkghq");
//				long id_schedule = CouponDao.insert(context, _Schedule, false);
//				MyData e_schedule = new MyData(null,"今天优惠券",MyDataTable.COUPON,MyUtil.longToInteger(id_schedule),-1l,MyDataTable.TAG_WRITETAG);
//				MyDataDao.insert(context, e_schedule, false);
				
				//ashley 0923 删除无用资源
//	//			for (int i = 0; i < 3; i++) {
//	//			 -5-
//				String str_Awards = "zJ5G4KXwoXf2+jWuhIR2YapdskFI3vM47ebNlX0z8x+u+bao8Qs7v2mEezqy Ub4xXFC8HxA9QQ2aY8CcrEvlzA==";
//				
//				Lottery _lottery = new Lottery(null, "大东北抽奖活动", "开业大吉,大东北抽奖活动", "A0000039DEB1A0", str_Awards, SettingData.ADMIN);
//				long id_lottery = LotteryDao.insert(context, _lottery, false);
//				MyData e_lottery = new MyData(null,"抽奖活动",MyDataTable.LOTTERY,MyUtil.longToInteger(id_lottery),-1l,MyDataTable.TAG_WRITETAG);
//				MyDataDao.insert(context, e_lottery, false);
//	//			}
//				// -7-
//				Ad ad = new Ad(null, "肯德基出新品了", "www.kfc.com.cn");
//				long id_ad = AdDao.insert(context, ad, false);
//				MyData e_ad = new MyData(null,ad.n,MyDataTable.AD,MyUtil.longToInteger(id_ad),-1l,MyDataTable.TAG_WRITETAG);
//				MyDataDao.insert(context, e_ad, false);
//				
//				// -8-
////				for (int i = 0; i < 3; i++) {
//				MovieTicket movieticket = new MovieTicket(null, "天台爱情", "嘉禾影院" ,"http://movie.mtime.com", "2013/08/15", "100元", "35元", "北京上地", "80012345678","周杰伦","周杰伦 李心艾 柯有伦......","adagf234235");
//				long id_movieticket = MovieTicketDao.insert(context, movieticket, false);
//				MyData e_movieticket = new MyData(null,movieticket.n,MyDataTable.MOVIETICKET,MyUtil.longToInteger(id_movieticket),-1l,MyDataTable.TAG_WRITETAG);
//				MyDataDao.insert(context, e_movieticket, false);
////				}
//				// -9-
////				for (int i = 0; i < 2; i++) {
//				ParkTicket parkticket = new ParkTicket(null, "景山公园门票", "景山公园是个好地方", "150", "10", "景山公园地址","9:00-18:00" ,"海洋", "全票", "80012345678","123asd234");
//				long id_parkticket = ParkTicketDao.insert(context, parkticket, false);
//				MyData e_parkticket = new MyData(null,parkticket.n,MyDataTable.PARKTICKET,MyUtil.longToInteger(id_parkticket),-1l,MyDataTable.TAG_WRITETAG);
//				MyDataDao.insert(context, e_parkticket, false);
////				}
				
				// -11-
				MyData e_Text = new MyData(null,"文本标签文本标签文本标签文本标签",MyDataTable.TEXT,-1,-1l,MyDataTable.TAG_WRITETAG);
				MyDataDao.insert(context, e_Text, false);
				// -12-
				MyData e_web = new MyData(null,"http://www.taobao.com",MyDataTable.WEB,-1,-1l,MyDataTable.TAG_WRITETAG);
				MyDataDao.insert(context, e_web, false);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			
		}finally{
			getDataBase(context).close();
		}
	}
	
}
