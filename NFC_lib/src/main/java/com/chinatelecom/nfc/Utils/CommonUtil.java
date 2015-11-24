package com.chinatelecom.nfc.Utils;
//package com.chinatelecom.Utils;
//
//import java.io.BufferedReader;
//import java.io.DataInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.PrintStream;
//import java.io.UnsupportedEncodingException;
//import java.lang.reflect.Type;
//import java.net.MalformedURLException;
//import java.net.Socket;
//import java.net.URL;
//import java.net.URLConnection;
//import java.text.DecimalFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.LinkedList;
//import java.util.List;
//
//import android.os.Environment;
//import android.os.StatFs;
//import android.text.format.DateFormat;
//import android.view.Menu;
//import android.widget.Toast;
//
//import com.automotiveSafety.vo.MessageVo;
//import com.automotiveSafety.vo.Scene;
//import com.google.android.maps.GeoPoint;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//public class CommonUtil {
//	
//	/**
//	 * requestCode,文件管理
//	 */
//	public final static int RC_CONTACT = 0x101;
//	/**
//	 * requestCode,查询路线
//	 */
//	public final static int RC_PATH = 0x102;
//	/**
//	 * requestCode,设置
//	 */
//	public final static int RC_SETTING = 0x103;
//	/**
//	 * 运输任务
//	 */
//	public final static int RC_POSITION = 0x104;
//	/**
//	 *救援任务
//	 */
//	public final static int RC_HELPPOSITION = 0x105;
//	
//	/**
//	 * 菜单，定位导航
//	 */
//	public final static int ITEM_MAPS = Menu.FIRST;
//	
//	public final static int ITEM_HELP=ITEM_MAPS+1;
//
//	/**
//	 * 菜单，设置
//	 */
//	public final static int ITEM_SETTING = ITEM_HELP + 1;
//	/**
//	 * 菜单，测试
//	 */
//	public final static int ITEM_TEST = ITEM_SETTING + 1;
//	
//	/**
//	 * 停车记录 
//	 */
//	public final static int	ITEM_PARKING=ITEM_TEST+1;
//
//	/**
//	 * 服务器IP
//	 */
//	//public static String IP = "192.168.1.102";
//	  public static String IP = "202.85.212.14";
//	//public static String IP = "10.10.43.177";
//	
//	/**
//	 * 服务器PORT
//	 */
//	public static int PORT_WEB = 8080;
//	
//	/**
//	 * 服务器IP
//	 */
//	public static String IP_SOCKET = "10.10.43.201";
//	
//	/**
//	 * SOCKET PORT
//	 */
//	public static int PORT_SOCKET = 10000;
//	/**
//	 * PORT_SOCKET_UPLOAD
//	 */
//	public static int PORT_SOCKET_UPLOAD = 9888;
//	
//	/**
//	 * 服务器URL
//	 */
//	public static String URL = "http://" + IP + ":" + PORT_WEB + "/Automotive/";
//	
//	/**
//	 * 登录URL
//	 */
//	public static String URL_LOGIN = URL+"/rest/service/login.xml";
//	/**
//	 * 保存停车记录
//	 */
//	public static  String URL_SAVE_STOPRECORD = URL+"/rest/service/saveDrivingRecord.xml";
//	/**
//	 * 周边同事URL
//	 */
//	public static String URL_QUERYNEARLYVEHICLE = URL+"/rest/service/queryNearlyVehicle.xml";
//	/**
//	 * 查询运输任务URL
//	 */
//	public static String URL_QUERY_TRANSPORT_TASK = URL+"/rest/service/queryTransportTask.xml";
//	/**
//	 * 应急处理URL
//	 */
//	public static String URL_SAVE_ALARMINFO = URL+"/rest/service/saveAlarmInfo.xml";
//	public static String URL_SAVE_ALARMINFOLIST = URL+"/rest/service/saveAlarmInfoList.xml";
//	/**
//	 * 救援任务
//	 */
//	public static String URL_QUERY_RESCUE_TASK=URL+"/rest/service/queryRescueInfo.xml";
//
//	/**
//	 * 保存当前位置URL
//	 */
//	public static String URL_SAVE_VEHICLE_LOCATION = URL+"/rest/service/saveVehicleLocation.xml";
//	
//	/**
//	 * 上传任务当前任务还是救援任务
//	 */
//	public static String URL_SAVE_TASKCHANGE= URL+"/rest/service/saveTaskChange.xml";
//	
//	/**
//	 * ERROR_MSG
//	 */
//	public final static String ERROR_MSG = "error";
//	
//	/**
//	 * STATE_SUCCESS
//	 */
//	public static final String STATE_SUCCESS = "success";
//	
//	/**
//	 * STATE_FAILURE
//	 */
//	public static final String STATE_FAILURE = "failure";
//	/**
//	 * 还没有登录
//	 */
//	public static final String LOGIN_MSG = "您还没登陆，请登陆！";
//	/**
//	 * 现在还没有任务信息
//	 */
//	public static final String NOCONTENT = "现在还没有任务信息";
//	/**
//	 * User ID
//	 */
//	public static  String USERID = "";
//	public static  String USERNAME = "";
//	public static  String USERTELNUM = "";
//	
//	/**
//	 * User Type
//	 */
//	public static  String USERTYPE = "";
//	
//	/**
//	 * 上传标识
//	 */
//	public final static int UP_LOAD = 0x01;
//	/**
//	 * 下载标识
//	 */
//	public final static int DOWN_LOAD = 0x02;
//	/**
//	 * 上传下载的最小单位
//	 */
//	public final static int UP_DOWN_BUFFER = 1024;
//	
//	
//	/**
//	 *  文件夹的名称
//	 */
//	public static String AUTOMOTIVESAFTY = "/AutomotiveSafety";
//	/**
//	 *  存放下载的文件夹名称
//	 */
//	public static String DOWNLOAD_AUTOMOTIVESAFETY = "/AutomotiveSafety/download";
//	
//	/**
//	 * 0表示当前任务
//	 */
//	public static String TRANSPORTTASK_CURRENT = "0";
//	/**
//	 * 1表示历史任务
//	 */
//	public static String TRANSPORTTASK_HISTORY = "1";
//	/**
//	 * 运输任务中返回的值
//	 */
//	public static int TRANSPORTTASK_FLAG=10;
//	
//	/**
//	 * 运输任务 的起始地点，
//	 */
//	public static String startPosition="";
//	/**
//	 * 运输任务 的结束地点
//	 * 
//	 */
//	public static String endPosition="";
//	
//	public static double[] DESTADDRESSINFO=null;
//	public static boolean TASKFLAG=false;
//	public static String TRANSTASKID;
//	/**
//	 * 救援任务的目的地
//	 * 
//	 */
//	
//	public static String HELPPOSITION="";
//	/**
//	 * 是否有sdcard的权限
//	 * @return boolean
//	 */
//	public static boolean externalStorageState(){
//		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
//	}
//	/**
//	 * 读取文件内容
//	 * @param inputStream
//	 * @return String
//	 */
//	public static String getFileToString(InputStream inputStream) {
//		InputStreamReader inputStreamReader = null;
//		try {
//			inputStreamReader = new InputStreamReader(inputStream, "gbk");
//		} catch (UnsupportedEncodingException e1) {
//			e1.printStackTrace();
//		}
//		BufferedReader reader = new BufferedReader(inputStreamReader);
//		StringBuffer sb = new StringBuffer("");
//		String line;
//		try {
//			while ((line = reader.readLine()) != null) {
//				sb.append(line);
//			}
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		try {
//			inputStreamReader.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return sb.toString();
//	}
//	
//	/**
//	 * 将字符串转换成date
//	 * @param dateStr 2012-07-1 10:00:00
//	 * @param format "yyyy-MM-dd hh:mm"
//	 * @return Date
//	 */
//	public static Date StringToDate(String dateStr,String format) {
//		try {
//			return new SimpleDateFormat(format).parse(dateStr);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	/**
//	 * 将date转换成CharSequence
//	 * @param date 2012-07-1 10:00:00
//	 * @param format "yyyy-MM-dd hh:mm"
//	 * @return 字符
//	 */
//	public static CharSequence DateToString(Date date,String format){
//		return DateFormat.format(format, date);
//	}
//	
//	
//
//	
//	/**
//	 * 监听服务器端是否发送新的任务
//	 * */
//	public void notifyNewTask() {
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				Socket soc = null;
//				BufferedReader in = null;
//				PrintStream out = null;
//				DataInputStream sysin = null;
//				String strin = null;
//				String strout = null;
//				try {
//					soc = new Socket(CommonUtil.IP, 10000);
//					System.out.println("Connecting to the Server");
//					out = new PrintStream(soc.getOutputStream());
//					out.println("user id is 1,中文");
//					in = new BufferedReader(new InputStreamReader(soc.getInputStream(),"GB2312"));
//				while(true){
//					strin = in.readLine();
//					System.out.println("Server said:" +strin);
//					if(strin.equals("newtask"))
//						System.out.println("有新的任务");
//				}
//				
//				} catch (Exception e) {
//					System.out.println("error:" + e);
//					e.printStackTrace();
//				} finally {
//					try {
//						in.close();
//						out.close();
//						soc.close();
//						
//					
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					
//				}	
//				
//			}
//		}).start();
//		
//	 }
///**
// * 根据经纬度求出地名的名称
// * 
// */
//	
//	 public static String GetAddr(String latitude, String longitude) {  
//		  String addr = "";  
//		  
//		  // 也可以是http://maps.google.cn/maps/geo?output=csv&key=abcdef&q=%s,%s，不过解析出来的是英文地址  
//		  // 密钥可以随便写一个key=abc  
//		 // output=csv,也可以是xml或json，不过使用csv返回的数据最简洁方便解析  
//	  String url = String.format(  
//		   "http://ditu.google.cn/maps/geo?output=csv&key=abcdef&q=%s,%s",  
//		   latitude, longitude);  
//		  URL myURL = null;  
//		  URLConnection httpsConn = null;  
//	  try {  
//		  myURL = new URL(url);  
//		 } catch (MalformedURLException e) {  
//		   e.printStackTrace();  
//		   return null;  
//		  }  
//		 try {  
//		   httpsConn = (URLConnection) myURL.openConnection();  
//		  if (httpsConn != null) {  
//		   InputStreamReader insr = new InputStreamReader(  
//	     httpsConn.getInputStream(), "UTF-8");  
//		   BufferedReader br = new BufferedReader(insr);  
//		   String data = null;  
//		   if ((data = br.readLine()) != null) {  
//	    System.out.println(data);  
//		    String[] retList = data.split(",");  
//		    if (retList.length > 2 && ("200".equals(retList[0]))) {  
//	     addr = retList[2];  
//	      addr = addr.replace("/", "");  
//	    } else {  
//		     addr = "";  
//		     }  
//		   }  
//		    insr.close();  
//		  }  
//		 } catch (IOException e) {  
//		   e.printStackTrace();  
//		
//	   return null;  
//		  }  
//	  return addr;  
//	}  
//	
//	
//	/**
//	 * 获得存储 下载而来的文件的文件夹
//	 * 
//	 * @return File类型 存储 下载而来的文件的文件夹
//	 */
//	public static File getDownTestFileDir() {
//		File testFile = new File(CommonUtil.getSdCardAbsolutePath() + DOWNLOAD_AUTOMOTIVESAFETY);
//		if (!testFile.exists()) {
//			testFile.mkdir();// 此处可能会创建失败，暂不考虑
//		}
//		return testFile;
//	}
//	
//	/**
//	 * 获得SD卡根目录路径
//	 * 
//	 * @return String类型 SD卡根目录路径
//	 */
//	public static String getSdCardAbsolutePath() {
//		return Environment.getExternalStorageDirectory().getAbsolutePath();
//	}
//	
////////////
//	/**
//	 * JsonString TO MessageVo
//	 * 
//	 * @return MessageVo
//	 */
//	public static MessageVo jsonStringToMessageVo(String jsonString) {
//		if (!jsonString.equals("")) {
//			Gson gson = new Gson();
//			MessageVo msssge = gson.fromJson(jsonString, MessageVo.class);
//			return msssge;
//		}
//		return null;
//	}
//	/**
//	 * URL,请求direction 的地址
//	 */
//	public static String placeToURL(String start, String end) {
//		return "http://maps.google.com/maps/api/directions/xml?origin=" + start
//				+ "&destination=" + end + "&sensor=false&mode=walking&language=zh-CN";
//	}
//	
//
//	/**
//	 * URL,请求direction 的地址
//	 */
//	public static String findPlaceByUrl(String address) {
//		return "http://maps.google.com/maps/api/directions/xml?origin=" + address
//		+ "&destination=" + address + "&sensor=false&mode=walking&language=zh-CN";
//	}
//
//	/**
//	 * 解析返回xml中overview_polyline的路线编码,官方源码，不解释。
//	 * 
//	 * @param encoded
//	 * @return
//	 */
//	public static List<GeoPoint> decodePoly(String encoded) {
//
//		List<GeoPoint> poly = new ArrayList<GeoPoint>();
//		int index = 0, len = encoded.length();
//		int lat = 0, lng = 0;
//
//		while (index < len) {
//			int b, shift = 0, result = 0;
//			do {
//				b = encoded.charAt(index++) - 63;
//				result |= (b & 0x1f) << shift;
//				shift += 5;
//			} while (b >= 0x20);
//			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//			lat += dlat;
//
//			shift = 0;
//			result = 0;
//			do {
//				b = encoded.charAt(index++) - 63;
//				result |= (b & 0x1f) << shift;
//				shift += 5;
//			} while (b >= 0x20);
//			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//			lng += dlng;
//
//			GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),
//					(int) (((double) lng / 1E5) * 1E6));
//			poly.add(p);
//		}
//
//		return poly;
//	}
//	/**
//	 * 模拟场景数据
//	 * @param filename
//	 * @param filePath
//	 * @return
//	 */
//	public static LinkedList<Scene> getScenceData(String filename,String filePath) {
//		String jsonData = "";// 文件内容
//		// "正在模拟场景...",Toast.LENGTH_SHORT).show();
//		FileInputStream fis = null;
//		try {
//			String p = filePath + "/" + filename;
//			// 打开文件
//			fis = new FileInputStream(new File(p));
//			// 读取文件内容
//			jsonData = CommonUtil.getFileToString(fis);
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (fis != null)
//					fis.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		if (!jsonData.equals("")) {
//			Type listType = new TypeToken<LinkedList<Scene>>() {}.getType();
//			Gson gson = new Gson();
//			LinkedList<Scene> sceneList = gson.fromJson(jsonData, listType);
//			return sceneList;
//		}else {
//			return null;
//		}
//	} 
//	
//	/**
//	 * String To Int
//	 * @param num
//	 * @return
//	 */
//	public static int StringToInt(String num){
//		return (int)(Double.parseDouble(num)*1E6);
//	}
//	/**
//	 * Int TO String 
//	 * @param int num
//	 * @return String
//	 */
//	public static String geoPointToString(int num){
//		return String.valueOf(num/1E6);
//	}
//	
//	/**
//	 * GeoPoint To Double
//	 * @param geoPoint
//	 * @return
//	 */
//	private static double[] geoPointToDouble(GeoPoint geoPoint){
//		double[] dPoint = new double[2];
//		dPoint[0] = geoPoint.getLatitudeE6()/1E6;
//		dPoint[1] = geoPoint.getLongitudeE6()/1E6;
//		return dPoint;
//	}
//	private static double intToDouble(int geoInt){
//		
//		return geoInt/1E6;
//	}
//	private final static double EARTH_RADIUS = 6378.137;
//	public static final String TEL = "106";
//	public static final String NEWTASK = "newtask";
//	public static final String HELPTASK = "helptask";
//	public static final String TAKEPICTURE = "takepicture";
//	public static final String NOTIFYMESSAGE = "notifymessage";
//
//
//	private static double rad(double d) {
//		return d * Math.PI / 180.0;
//	}
//	/**
//	 * 通过经纬度求位移
//	 * @param geoPointLat
//	 * @param geoPointLon
//	 * @param geoPoint2
//	 * @return double 位移
//	 */
//	public static double getDistance(int geoPointLat,int geoPointLon,GeoPoint geoPoint2) {
//		double[] dPoint2 = geoPointToDouble(geoPoint2);
//		double radLat1 = rad(intToDouble(geoPointLat));
//		double radLat2 = rad(dPoint2[0]);
//		double a = radLat1 - radLat2;
//		double b = rad(intToDouble(geoPointLon)) - rad(dPoint2[1]);
//		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
//				+ Math.cos(radLat1) * Math.cos(radLat2)
//				* Math.pow(Math.sin(b / 2), 2)));
//		s = s * EARTH_RADIUS;
//		//s = Math.round(s * 10000) / 10000;
//		return s;
//	}
//	
//	/**
//	 * 求速度
//	 * @param distance
//	 * @param now
//	 * @param last
//	 * @return 速度
//	 */
//	public static double getSpeed(double distance,Date now,Date last){
//		return distance*3.6*1E6/(now.getTime()-last.getTime());
//	}
//	
//
//		 public long getFileSizes(File f){//取得文件大小
//		        long s=0;
//		       
//		        if (f.exists()) {
//		            FileInputStream fis = null;
//		            try {
//						fis = new FileInputStream(f);
//						 try {
//							s= fis.available();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//							System.out.println("IO异常");
//						}
//				           
//					} catch (FileNotFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						System.out.println("创建文件出错");
//					}
//		           
//		        } else {
//		           // f.createNewFile();
//		            System.out.println("文件不存在");
//		        }
//		        return s;
//	}
//  
//
//	 public String FormetFileSize(long fileS) {//转换文件大小
//	        DecimalFormat df = new DecimalFormat("#.00");
//	        String fileSizeString ="";
//	        if (fileS < 1024) {
//	            fileSizeString = df.format((double) fileS) + "B";
//	        } else if (fileS < 1048576) {
//	            fileSizeString = df.format((double) fileS / 1024) + "K";
//	        } else if (fileS < 1073741824) {
//	            fileSizeString = df.format((double) fileS / 1048576) + "M";
//	        } else {
//	            fileSizeString = df.format((double) fileS / 1073741824) + "G";
//	        }
//	        return fileSizeString;
//	    }
//	   
//	 public double showSDCardSize(){ 
//		
//
//		 File sdcard = Environment.getExternalStorageDirectory(); 
//		 /** 
//		 * 我们可以通过StatFs访问文件系统的空间容量等信息 
//		 */ 
//		 StatFs statFs = new StatFs(sdcard.getPath()); 
//
//		 /** 
//		 * statFs.getBlockSize可以获取当前的文件系统中，一个block所占有的字节数 
//		 */ 
//		 int blockSize = statFs.getBlockSize(); 
//		 System.out.println("blockSize "+blockSize);
//		 /** 
//		 * statFs.getAvaliableBlocks方法可以返回尚未使用的block的数量 
//		 */ 
//		 int avaliableBlocks = statFs.getAvailableBlocks(); 
//		 System.out.println("avaliableBlocks "+avaliableBlocks);
//		 /** 
//		 * statFs.getBlockCount可以获取总的block数量 
//		 */ 
//	 
//
//		 int totalBlocks = statFs.getBlockCount(); 
//		
//		 
//      //获得所剩的空间大小 M
//		 double result=(avaliableBlocks/1024)*(blockSize/1024); 
//		
//		 return result;
//		 } 
//
//	
//}
