package cn.yjt.oa.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.StrictMode;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import cn.robusoft.CTBeaconManager;
import cn.robusoft.Mode;
import cn.yjt.oa.app.account.AccountManager;
import cn.yjt.oa.app.attendance.AttendanceTimeCheckManager;
import cn.yjt.oa.app.attendance.AttendanceTimeCheckReceiver;
import cn.yjt.oa.app.attendance.BeaconServerLoader;
import cn.yjt.oa.app.beans.BaiduTagSingleInstance;
import cn.yjt.oa.app.beans.BarCodeAttendanceInfo;
import cn.yjt.oa.app.beans.ContactInfo;
import cn.yjt.oa.app.beans.MessageInfo;
import cn.yjt.oa.app.beans.OperaEvent;
import cn.yjt.oa.app.beans.OperaStatistics;
import cn.yjt.oa.app.beans.OperaStatisticsRequest;
import cn.yjt.oa.app.beans.Response;
import cn.yjt.oa.app.beans.UserInfo;
import cn.yjt.oa.app.contactlist.db.ContactManager;
import cn.yjt.oa.app.contactlist.server.UpdateCheckServerLoader;
import cn.yjt.oa.app.find.config.FinderConfig;
import cn.yjt.oa.app.http.ApiHelper;
import cn.yjt.oa.app.imageloader.ImageLoader;
import cn.yjt.oa.app.imageloader.MemoryImageCache;
import cn.yjt.oa.app.meeting.MeetingSigninActivity;
import cn.yjt.oa.app.nfctools.operation.NfcTagOperatorConfig;
import cn.yjt.oa.app.patrol.activitys.QrCodeActivity;
import cn.yjt.oa.app.patrol.manager.PatrolRemindManager;
import cn.yjt.oa.app.patrol.utils.PatrolRemindData;
import cn.yjt.oa.app.push.BaiduPushMessageReceiver;
import cn.yjt.oa.app.push.PushMessageManager;
import cn.yjt.oa.app.utils.HeadImageLoader;
import cn.yjt.oa.app.utils.LogUtils;
import cn.yjt.oa.app.utils.OperaEventUtils;
import cn.yjt.oa.app.utils.ToastUtils;
import cn.yjt.oa.app.utils.UserData;
import io.luobo.common.http.InvocationError;
import io.luobo.common.http.Listener;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.baidu.android.pushservice.PushManager;
import com.baidu.frontia.FrontiaApplication;
import com.baidu.mapapi.SDKInitializer;
import com.telecompp.ContextUtil;
import com.telecompp.GetYjtPhoneNum;
import com.umeng.message.PushAgent;
import com.umeng.update.UpdateConfig;
import com.zbar.lib.CaptureActivity.QRScanCallback;

/**
 * 整个程序的application， 用于处理整个程序的一些全局的操作
 * 
 */
public class MainApplication extends Application implements QRScanCallback {

	public static final String TAG = "MainApplication";

	/**全局常量*/
	@SuppressWarnings("unused")
	private static final String BEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
	public static final String ACTION_LOGIN = "cn.yjt.oa.app.ACTION_LOGIN";
	public static final String ACTION_CHANGED_CUST_SHORT_NAME = "cn.yjt.oa.app.ACTION_CHANGED_CUST_SHORT_NAME";
	public static final String ACTION_CURRENT_LOGINUSER_CHANGED = "cn.yjt.oa.app.ACTION_CURRENT_LOGINUSER_CHANGED";

	/**全局的上下文*/
	private static Context sAppContext;
	/**MainApplication对象*/
	private static MainApplication sApplication;

	/**图片加载类*/
	private static ImageLoader sImageLoader;
	private static ImageLoader sHeadimImageLoader;
	/**可以得到分辨率信息的类*/
	private static DisplayMetrics displayMetrics;

	/**全局变量，用于判断通讯录中的全员联系人是否正在加载*/
	public static boolean isLoadingALLContacts = false;
	public static boolean isLoadingDeptContacts = false;
	/**消息中心数据集合*/
	private List<MessageInfo> messageCenterMessages = new ArrayList<MessageInfo>();

	/**友盟推送消息*/
	@SuppressWarnings("unused")
	private static PushAgent mPushAgent;

	public static List<ContactInfo> sContactInfoLists;
//	public static List<DeptDetailInfo> sDeptDetailInfoLists;
	
	//		private static String currentAlias;
	//TODO:
	@Override
	protected void attachBaseContext(Context base) {
		sAppContext = base;
		super.attachBaseContext(base);
	}

	/*-----生命周期函数START-----*/
	@Override
	public void onCreate() {
		super.onCreate();
		strictMode();
		initParams();
		initOtherSDK();
		initAsyncTask();
	}
	/*-----生命周期函数END-----*/

	/*-----在onCreate方法中执行START-----*/
	/**初始化成员参数*/
	private void initParams() {
		//初始化上下文
		sApplication = MainApplication.this;
	}

	/**初始化第三方sdk*/
	private void initOtherSDK() {
		// 百度推送，初始化调用（耗时很长）
		FrontiaApplication.initFrontiaApplication(getApplicationContext());
		// 第三方ActiveAndroid初始化，用于缓存消息中心的离散数据
		ActiveAndroid.initialize(this);
		// 初始化ctbeancon_lib_v.jar中BeaconManager的设置，
		CTBeaconManager ctbm = CTBeaconManager.getInstance(this); // 设置 Beacon
		//ctbm.setAppKey("26DC0686AA460F215375FE0B468F6F4BA8BA22F208EA357F2DA6367193694494");
		//ctbm.setAppKey("test-key");
		ctbm.setDecryptMode(Mode.MODE_LOCAL_DECODE);
	}

	/**在子线程中执行的初始化操作*/
	private void initAsyncTask() {
		//在子线程中执行
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				onCreateAsync();
				return null;
			}
		}.execute();
	}

	/**异步初始化成员参数*/
	private void initAsyncParams() {
		// 初始化图片加载器
		sImageLoader = new ImageLoader(this, new MemoryImageCache(this));
		sHeadimImageLoader = new HeadImageLoader(this, new MemoryImageCache(this));
		// 初始化屏幕分辨率测量类
		preDisplayMetrics();
		// 获取手机号码的回调方法
		ContextUtil.setGetYjtPhoneNum(new GetYjtPhoneNum() {
			@Override
			public String getYjtPhoneNum() {
				return AccountManager.getCurrent(getApplicationContext()).getPhone();
			}
		});
		//TODO：
		//注册推送？
		PushMessageManager.registerPushMessageHandlers();
		//
		FinderConfig.getInstance(this).load();
		NfcTagOperatorConfig.getInstance(this).load("nfctools.xml");
	}

	/**异步初始化第三方sdk*/
	private void initAsyncOtherSDK() {
		//百度sdk初始化
		SDKInitializer.initialize(getApplicationContext());
		//友盟推送初始化
		mPushAgent = PushAgent.getInstance(this);
		//TODO:尚无
		UpdateConfig.setDebug(true);
	}

	/**oncreate中的异步操作*/
	private void onCreateAsync() {
		initAsyncParams();
		initAsyncOtherSDK();
	}

	/*-----在onCreate方法中执行START-----*/

	/*-----成员变量START-----*/
	/**获取消息中心消息集合*/
	public List<MessageInfo> getMessageCenterMessages() {
		return this.messageCenterMessages;
	}

	/**设置消息中心消息集合*/
	public void setMessageCenterMessages(List<MessageInfo> infos) {
		this.messageCenterMessages = infos;
	}

	/**获取上下文*/
	public static Context getAppContext() {
		return sAppContext;
	}

	/**获取MainApplication对象*/
	public static MainApplication getApplication() {
		return sApplication;
	}

	/**获取imageLoader*/
	public static ImageLoader getImageLoader() {
		return sImageLoader;
	}

	/**获取headimImageLoader*/
	public static ImageLoader getHeadImageLoader() {
		return sHeadimImageLoader;
	}

	/**获取屏幕像素测量类*/
	public static DisplayMetrics getDisplayMetrics() {
		return displayMetrics;
	}
	/*-----成员变量END-----*/
	
	OnSharedPreferenceChangeListener attendanceSpListener = new OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.d(TAG, "onSharedPreferenceChanged:" + key);
			if (UserData.KEY_AUTO_ATTENDANCE.equals(key)) {
				boolean boolean1 = sharedPreferences.getBoolean(key, false);
				if (boolean1) {
					new AttendanceTimeCheckManager(sAppContext).check();
				} else {
					new AttendanceTimeCheckManager(sAppContext).cancelCheck();
				}
			}
		}
	};

    OnSharedPreferenceChangeListener patrolRemindSpListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (PatrolRemindData.REMIND_TIME.equals(key)) {
                if (PatrolRemindData.TYPE_NO_REMIND == PatrolRemindData.getRemindType()) {
                    new PatrolRemindManager(sAppContext).cancleRemind();
                } else {
                    LogUtils.d("PatrolRemindManager","提醒时间被修改时调用");
                    new PatrolRemindManager(sAppContext).remind();
                }
            }
        }
    };

	private void attendanceTimeCheck() {
		Log.d(TAG, "attendanceTimeCheck");
		boolean autoAttendance = UserData.getAutoAttendance();
		if (autoAttendance) {
			new AttendanceTimeCheckManager(this).check();
		}
		UserData.getUserPreferences().registerOnSharedPreferenceChangeListener(attendanceSpListener);
	}

	/**初始化屏幕分辨率测量类*/
	private void preDisplayMetrics() {
		WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
		displayMetrics = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(displayMetrics);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		ActiveAndroid.dispose();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		getHeadImageLoader().getImageCache().trim();
		getImageLoader().getImageCache().trim();
	}

	/**登陆时进行的一些操作*/
	public void onLogin() {
		checkContactsUpdate();
		AttendanceTimeCheckReceiver.sendBroadcast(sAppContext);
		checkAttendanceBeacons();
		attendanceTimeCheck();
        //巡检提醒的先关操作
        patrolRemind();
//        commitOpera();
	}

    private void patrolRemind() {
        //巡检提醒处于开启状态
        if(PatrolRemindData.TYPE_NO_REMIND != PatrolRemindData.getRemindType()){
            LogUtils.d("PatrolRemindManager","application中自动调用");
            new PatrolRemindManager(sAppContext).remind();
        }
        PatrolRemindData.getPatrolRemindSP().registerOnSharedPreferenceChangeListener(patrolRemindSpListener);
    }

    /**检测联系人更新情况*/
	private void checkContactsUpdate() {
		new UpdateCheckServerLoader().load();
	}

	private void checkAttendanceBeacons() {
		new BeaconServerLoader().load();
	}

	/**只在debug时候使用的方法*/
	private void strictMode() {

		if (BuildConfig.DEBUG && VERSION.SDK_INT > VERSION_CODES.FROYO) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
			// .detectDiskReads()
			// .detectDiskWrites()
					.detectNetwork() // 这里可以替换为detectAll() 就包括了磁盘读写和网络I/O
					.penaltyLog() // 打印logcat，当然也可以定位到dropbox，通过文件保存相应的log
					.build());
			StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects() // 探测SQLite数据库操作
					.penaltyLog() // 打印logcat
			// .penaltyDeath() //遇到violation不要杀进程
			;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				builder.detectActivityLeaks().detectLeakedClosableObjects();
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				builder.detectLeakedRegistrationObjects();
			}
			StrictMode.setVmPolicy(builder.build());
		}
	}

	private void playVibrate() {
		Vibrator vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		vibrator.vibrate(new long[] { 100, 10, 10, 100 }, -1);
	}

	@Override
	public void callback(Context context, String result) {
		if (context instanceof Activity) {
			((Activity) context).finish();
		}

		if (result.contains("checkin.html")) {
			UserInfo current = AccountManager.getCurrent(getApplicationContext());
			if (current == null || current.getId() == 0) {
				LaunchActivity.launch(context);
				return;
			}
			Uri uri = Uri.parse(result);
			String sn = uri.getQueryParameter("ID");
            BarCodeAttendanceInfo info = new BarCodeAttendanceInfo();
            info.setActrualData(sn);
			QrCodeActivity.launchWithBarCodeAttendanceInfo(context, info);

             /*记录操作 0804*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_SCAN_QRCODE);
			return;
		}else if(result.contains("meetingId")){
            UserInfo current = AccountManager.getCurrent(getApplicationContext());
            if (current == null || current.getId() == 0) {
                LaunchActivity.launch(context);
                return;
            }
			Uri uri = Uri.parse(result);
			String meetingId = uri.getQueryParameter("meetingId");
			String token = uri.getQueryParameter("token");
			MeetingSigninActivity.launch(context,meetingId,token);

            /*记录操作 1604*/
            OperaEventUtils.recordOperation(OperaEvent.OPERA_SCAN_MEETING_QRCODE);
			return;
//			Toast.makeText(this, Uri.parse(result).getQueryParameter("ID")+"签到标签", 1).show();
		}
		try {
			Uri uri = Uri.parse(result);
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(uri);
			context.startActivity(intent);
		} catch (Throwable e) {
			Toast.makeText(sAppContext, "未识别的二维码：" + result, Toast.LENGTH_LONG).show();
		}
	}

	/*-----MainApplication中的静态方法START-----*/
	/**
	 * 百度推送时设置tag时候使用的
	 * @param userId
	 * @param phone
	 */
	public static void setAlias(String userId, String phone) {
		LogUtils.i(TAG, "setAlias: userId=" + userId + "  phone=" + phone);
		// 现更改为使用百度的tag进行推送
		BaiduTagSingleInstance instance = BaiduTagSingleInstance.getInstance();
		// 先设置userId 和 phoneId
		instance.setUserId("userId" + userId);
		// 设置phoneId
		instance.setPhone("phone" + phone);
		// 首先列出当前应用绑定的所有tag, 在listTags回调中删除所有tag, 再在delTags的回调中将新的userId,
		// phonId, custId作为新的tag添加进去
		PushManager.listTags(getAppContext());
	}

	/**推送时设置tag*/
	public static void addTag(String tag) {
		LogUtils.i(TAG, "addTag:" + tag);
		// 工程原来的友盟推送时候设置tag的
		/*
		 * PushAgent pa = MainApplication.getPushAgent(); try {
		 * pa.getTagManager().reset(); pa.getTagManager().add(tag); } catch
		 * (Exception e) { e.printStackTrace(); }
		 */
		// 更改为使用百度的tag进行推送
		// 先设置custId
		BaiduTagSingleInstance instance = BaiduTagSingleInstance.getInstance();
		instance.setCustId(tag);
		// 首先列出当前应用绑定的所有tag, 在listTags回调中删除所有tag, 再在delTags的回调中将新的userId,
		// phonId, custId作为新的tag添加进去
		PushManager.listTags(getAppContext());
	}

	/**用户登出, 删除用户绑定的tag, 防止登出后仍能接收百度推送的消息*/
	public static void logoutDelAllTag() {
		// 首先将isUserLogout设置为true
		BaiduPushMessageReceiver.isRequestDeleteTag = true;
		// 删除所有tag
		BaiduTagSingleInstance.getInstance().clear();
		PushManager.listTags(getAppContext());
	}

	/**发送登录广播*/
	public static void sendLoginBroadcast(Context context) {
		Intent intent = new Intent(MainApplication.ACTION_LOGIN);
		context.sendBroadcast(intent);
		sApplication.onLogin();
	}

	public static void sendChangedCustShortNameBroadcast(Context context) {
		Intent intent = new Intent(MainApplication.ACTION_CHANGED_CUST_SHORT_NAME);
		context.sendBroadcast(intent);
	}

	public static void sendCurrentLoginUserChanged(Context context) {
		Intent intent = new Intent(MainApplication.ACTION_CURRENT_LOGINUSER_CHANGED);
		context.sendBroadcast(intent);
	}

	public static void setTestMode(Context context, boolean isTestMode) {
		SharedPreferences preferences = context.getSharedPreferences("test_mode", MODE_PRIVATE);
		preferences.edit().putBoolean("test_mode", isTestMode).commit();
	}

	public static boolean isTestMode(Context context) {
		SharedPreferences preferences = context.getSharedPreferences("test_mode", MODE_PRIVATE);
		return preferences.getBoolean("test_mode", false);
	}

	public static void clearContacts() {
		ContactManager manager = ContactManager.getContactManager(getAppContext());
		manager.deleteAllRegisteredContacts();
		manager.deleteAllUnregisteredContacts();
		manager.deleteAllDept();
		manager.deleteAllDeptUser();
		manager.deleteAllGroups();
		manager.deleteAllPublicService();
	}

	public static boolean isLoadingALLContacts() {
		return isLoadingALLContacts;
	}

	public static void setLoadingALLContacts(boolean isLoadingALLContacts) {
		MainApplication.isLoadingALLContacts = isLoadingALLContacts;
	}

	/*-----MainApplication中的静态方法END-----*/

	private void initPushHandler() {
		/**
		 * 该Handler是在IntentService中被调用，故 1.
		 * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK 2.
		 * IntentService里的onHandleIntent方法是并不处于主线程中，因此，如果需调用到主线程，需如下所示;
		 * 或者可以直接启动Service
		 * */
		/*
		 * UmengMessageHandler messageHandler = new UmengMessageHandler() {
		 * 
		 * @Override public void dealWithCustomMessage(Context context, UMessage
		 * msg) { Map<String, String> extras = msg.extra;
		 * LogUtils.i("dealWithCustomMessage extra = " + extras); String json =
		 * extras.get("pushMessage"); int alertTone =
		 * StorageUtils.getSystemSettings(context).getInt( Config.ALERT_TONE,
		 * 0); switch (alertTone) { // 有声音 case Config.RINGER_MODE_NORMAL: //
		 * SoundManager.getInstance(context).playSound();
		 * MediaPlayerManager.getInstance(context).play();
		 * 
		 * break; // 震动 case Config.RINGER_MODE_VIBRATE: playVibrate(); break;
		 * // 震动和声音 case Config.RINGER_NORMAL_VIBRATE: //
		 * SoundManager.getInstance(context).playSound();
		 * 
		 * MediaPlayerManager.getInstance(context).play(); playVibrate(); break;
		 * 
		 * default: break; }
		 * 
		 * PushMessage pushMessage = Config.CONVERTER.convertToObject( json, new
		 * TypeToken<PushMessage>() { }.getType());
		 * PushMessageManager.handlePushMessage(pushMessage); } };
		 * mPushAgent.setMessageHandler(messageHandler);
		 */
	}
	
	public static int getVersionCode(){
		try {
			return getApplication().getPackageManager().getPackageInfo(getAppContext().getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
