package cn.yjt.oa.app.utils;

import io.luobo.common.http.volley.GsonConverter;
import cn.yjt.oa.app.http.GsonHolder;

public class Config {

	
	
	public static final String POSITION_DATA = "POSITION_DATA";
	public static final String POSITION_DES = "POSITION_DES";
	
	public static final String IS_OPEN_PUSH_MESSAGE = "IsOpenPushMessage";
	public static final String IS_OPEN_INCOMECALL_ALERT = "IsOpenIncomecallAlert";
	/**判断企业是否更换*/
	public static final String IS_DEPT_CHANGED = "IsDeptChanged";
	public static final String ALERT_TONE = "AlertTone";
	/**是否开启摇一摇音效*/
	public static final String IS_OPEN_SHAKING_SOUND = "IsOpenShakingSound";
	/**是否开启巡更提醒*/
	public static final String IS_OPEN_PATROL_REMIND = "IsOpenPatrolRemind";
	/**巡更提醒的类型*/
	public static final String TYPE_PATROL_REMIND = "TypePatrolRemind";
	
	
	public static final String USER_LAST_UPDATETIME = "userLastUpdateTime";
	public static final String GROUP_LAST_UPDATETIME = "groupLastUpdateTime";
	public static final String DEPT_LAST_UPDATETIME = "deptLastUpdateTime";
	public static final String COMMON_CONTACT_LAST_UPDATETIME = "commonContactLastUpdateTime";
	
	public static final String DASH_CONFIG = "dashConfig";
	
	public static final int REDUCTION_UNREAD_COUNT = 1;
	/**声音模式*/ 
	public static final int RINGER_MODE_NORMAL = 3;
	/**静音模式*/ 
	public static final int RINGER_MODE_SILENT = 4;
	/**震动模式*/ 
	public static final int RINGER_MODE_VIBRATE = 5;
	/**声音和震动模式*/ 
	public static final int RINGER_NORMAL_VIBRATE = 6;
	
	/**客户端巡更模式*/ 
	public static final int TYPE_PATROL_CLIENT = 0;
	/**短信巡更模式*/ 
	public static final int TYPE_PATROL_MESSAGE = 1;
	/**客户端和短信巡更模式*/ 
	public static final int TYPE_PATROL_BOTH = 2;
	
	public static final GsonConverter CONVERTER = new GsonConverter(GsonHolder
			.getInstance().getGson());
}
