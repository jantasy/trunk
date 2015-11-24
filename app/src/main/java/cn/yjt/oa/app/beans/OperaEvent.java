package cn.yjt.oa.app.beans;

/**
 * Created by 熊岳岳 on 2015/10/8.
 */
public class OperaEvent {

    /*
        0101	账号	首界面	登录
        0102	账号	首界面	注册
        0103	账号	首界面	体验账号
        0104	账号	首界面	忘记密码
        0105	账号	修改密码	修改密码
        0106	账号	侧边栏	进入个人中心
        0107	账号	个人中心	修改信息
        0108	账号	侧边栏	退出登录
    */
    public static final String OPERA_LOGIN = "0101";
    public static final String OPERA_REGIEST = "0102";
    public static final String OPERA_EXPE_ACCOUNT = "0103";
    public static final String OPERA_FORGET_PASSWORD = "0104";
    public static final String OPERA_MODIFY_PASSWORD = "0105";
    public static final String OPERA_PERSON_CENTER = "0106";
    public static final String OPERA_MODIFY_INFO = "0107";
    public static final String OPERA_LOGOUT = "0108";
    /*
        0201	企业	创建或加入企业	创建企业
        0202	企业	创建或加入企业	加入企业
        0203	企业	侧边栏	进入单位列表
        0204	企业	主界面	点击单位名称呼出单位列表
        0205	企业	单位列表	切换企业
        0206	企业	工作台	进入企业管理
        0207	企业	企业管理	查看成员列表
        0208	企业	通讯录/企业成员列表	手动输入联系人加入企业
        0209	企业	通讯录/企业成员列表	邀请本机联系人加入企业
        0210	企业	企业成员列表	编辑成员
        0211	企业	企业成员列表	删除成员
        0212	企业	修改企业信息	修改企业信息
        0213	企业	企业管理	注销企业
    */
    public static final String OPERA_CENTER_ENTERPRISE = "0201";
    public static final String OPERA_ENTER_ENTERPRISE = "0202";
    public static final String OPERA_ENTER_DEPTLISTS = "0203";
    public static final String OPERA_POPUP_DEPTLISTS = "0204";
    public static final String OPERA_CHANGE_ENTERPRISE = "0205";
    public static final String OPERA_ENTER_ENTERPRISE_MANAGE = "0206";
    public static final String OPERA_WATCH_MEMBERLISTS = "0207";
    public static final String OPERA_WRITE_CONTACTS_TO_JION = "0208";
    public static final String OPERA_INVITE_CONTACTS_TO_JION = "0209";
    public static final String OPERA_EDIT_MEMBER = "0210";
    public static final String OPERA_DELETE_MEMBER = "0211";
    public static final String OPERA_MODIFY_ENTERPRISE_INFO = "0212";
    public static final String OPERA_LOGOUT_ENTERPRISE = "0213";
    /*
        0301	客户端	侧边栏	进入系统设置
        0302	客户端	系统设置	打开新消息提醒
        0303	客户端	系统设置	关闭新消息提醒
        0304	客户端	提醒提示音	选择静音
        0305	客户端	提醒提示音	选择声音和震动
        0306	客户端	提醒提示音	选择声音
        0307	客户端	提醒提示音	选择震动
        0308	客户端	系统设置	打开摇一摇音效
        0309	客户端	系统设置	关闭摇一摇音效
        0310	客户端	系统设置	恢复默认设置
        0311	客户端	侧边栏	进入意见反馈
        0312	客户端	意见反馈	提交意见反馈
        0313	客户端	侧边栏	进入关于
        0314	客户端	侧边栏	进入帮助
    */
    public static final String OPERA_ENTER_SYSTEM_SETTING = "0301";
    public static final String OPERA_OPEN_NEWINFO_REMIND = "0302";
    public static final String OPERA_CLOSE_NEWINFO_REMIND = "0303";
    public static final String OPERA_CHANGE_SOUND_OFF = "0304";
    public static final String OPERA_CHANGE_SOUNDANDSHOCK = "0305";
    public static final String OPERA_CHANGE_SOUND = "0306";
    public static final String OPERA_CHANGE_SHOCK = "0307";
    public static final String OPERA_OPEN_SHAKE_SOUND = "0308";
    public static final String OPERA_CLOSE_SHAKE_SOUND = "0309";
    public static final String OPERA_RESTORE_DEFAULTS = "0310";
    public static final String OPERA_ENTER_FEEDBACK = "0311";
    public static final String OPERA_COMMIT_FEEDBACK = "0312";
    public static final String OPERA_ENTER_ABOUT = "0313";
    public static final String OPERA_HELP = "0314";
    /*
        0401	消息中心	主界面	底边栏的菜单切换（消息中心）
        0402	消息中心	消息中心	刷新消息中心
        0403	消息中心	消息中心	消息中心列表项的置顶
        0404	消息中心	消息中心	消息中心列表项的取消置顶
        0405	消息中心	消息中心	消息中心列表项内容的切换（全部）
        0406	消息中心	消息中心	消息中心列表项内容的切换（任务）
        0407	消息中心	消息中心	消息中心列表项内容的切换（公告）
    */
    public static final String OPERA_ENTER_MESSAGE_CENTER = "0401";
    public static final String OPERA_REFRESH_MESSAGE_CENTER = "0402";
    public static final String OPERA_MESSAGE_CENTER_TOP = "0403";
    public static final String OPERA_MESSAGE_CENTER_TOP_CANCLE = "0404";
    public static final String OPERA_MESSAGE_CENTER_ALL = "0405";
    public static final String OPERA_MESSAGE_CENTER_TASK = "0406";
    public static final String OPERA_MESSAGE_CENTER_NOTICE = "0407";
    /*
        0501	工作台	主界面	底边栏的菜单切换（工作台）
        0502	工作台	工作台	隐藏已添加到工作台的图标
        0503	工作台	工作台	将工作台的图标发送到桌面
        0504	工作台	应用添加	添加企业应用到工作台
        0505	工作台	应用添加	添加本地应用到工作台
    */
    public static final String OPERA_ENTER_DASHBOARD = "0501";
    public static final String OPERA_DASHBOARD_HIDE_ICON = "0502";
    public static final String OPERA_DASHBOARD_SEND_ICON_DESKTOP = "0503";
    public static final String OPERA_DASHBOARD_ADD_ENTERPRISE_APP = "0504";
    public static final String OPERA_DASHBOARD_ADD_LOCAL_APP = "0505";
    /*
        0601	通讯录	主界面	底边栏的菜单切换（通讯录）
        0602	通讯录	通讯录	通讯录内容的切换（全员）
        0603	通讯录	通讯录	通讯录内容的切换（群组）
        0604	通讯录	通讯录	通讯录内容的切换（组织架构）
        0605	通讯录	通讯录	点击查看全员个人详情
        0606	通讯录	通讯录个人详情	打电话
        0607	通讯录	通讯录个人详情	发短信
        0608	通讯录	通讯录	点击查看公共服务
        0609	通讯录	公共服务	打电话
        0610	通讯录	通讯录	添加群组
        0611	通讯录	通讯录	查看组织架构个人详情
        0612	通讯录	通讯录	从组织架构打电话
        0613	通讯录	通讯录	从组织架构发短信
        0614	通讯录	通讯录	从组织架构@
        0615	通讯录	/	来电弹屏弹出
        0616	通讯录	来电弹屏	选择关闭
        0617	通讯录	来电弹屏	选择开启简约弹屏
        0618	通讯录	来电弹屏	选择开启华丽弹屏
    */
    public static final String OPERA_ENTER_CONTACTLIST = "0601";
    public static final String OPERA_CONTACTLIST_ALL = "0602";
    public static final String OPERA_CONTACTLIST_GROUP = "0603";
    public static final String OPERA_CONTACTLIST_STRUCT = "0604";
    public static final String OPERA_CONTACTLIST_PERSONINFO = "0605";
    public static final String OPERA_CONTACTLIST_DAIL = "0606";
    public static final String OPERA_CONTACTLIST_SEND_MESSAGE = "0607";
    public static final String OPERA_CONTACTLIST_WATCH_SERVICE = "0608";
    public static final String OPERA_CONTACTLIST_DAIL_SERVICE = "0609";
    public static final String OPERA_CONTACTLIST_ADD_GROUP = "0610";
    public static final String OPERA_CONTACTLIST_PERSONINFO_STRUCT = "0611";
    public static final String OPERA_CONTACTLIST_DAIL_STRUCT = "0612";
    public static final String OPERA_CONTACTLIST_SEND_MESSAGE_STRUCT = "0613";
    public static final String OPERA_CONTACTLIST_AT_STRUCT = "0614";
    public static final String OPERA_CONTACTLIST_INCOMING = "0615";
    public static final String OPERA_CONTACTLIST_INCOMING_CLOSE = "0616";
    public static final String OPERA_CONTACTLIST_INCOMING_OPEN_BRIEF = "0617";
    public static final String OPERA_CONTACTLIST_INCOMING_OPEN_GORGEOUS = "0618";
    /*
        0701	生活圈	主界面	底边栏的菜单切换（生活圈）
    */
    public static final String OPERA_ENTER_LIFECIRCLE = "0701";
    /*
        0801	考勤	工作台	进入定位考勤（弹出定位考勤）
        0802	考勤	/	刷NFC标签弹出（NFC标签考勤）
        0803	考勤	工作台	进入扫码考勤
        0804	考勤	扫一扫	扫考勤标签
        0805	考勤	工作台	进入蓝牙考勤
        0806	考勤	蓝牙考勤	摇一摇
        0807	考勤	蓝牙考勤	点一点
        0808	考勤	蓝牙考勤	开启自动考勤
        0809	考勤	蓝牙考勤	关闭自动考勤
        0810	考勤	企业管理	查看考勤区域列表
        0811	考勤	考勤区域	新建考勤区域
        0812	考勤	考勤区域	修改考勤区域信息
        0813	考勤	考勤区域	管理区域人员
        0814	考勤	考勤区域列表	删除考勤区域
        0815	考勤	企业管理	查看考勤时间
        0816	考勤	考勤时间设置	设置简约型单班段
        0817	考勤	考勤时间设置	设置简约型双班段
        0818	考勤	考勤时间设置	设置精细型
        0819	考勤	企业管理	查看NFC考勤标签
        0820	考勤	制作签到标签	写入标签
        0821	考勤	企业管理	查看蓝牙考勤标签
        0822	考勤	蓝牙标签设置	关联标签
        0823	考勤	工作台	进入考勤记录
        0824	考勤	我的考勤记录	查看成员考勤记录
        0825	考勤	我的考勤记录	展开查看打卡记录
        0826	考勤	我的考勤记录	展开查看今日排班
        0827	考勤	考勤报表	发送到邮箱
        0828	考勤	考勤报表	下载到手机
    */
    public static final String OPERA_ATTENDANCE_LOCATION = "0801";
    public static final String OPERA_ATTENDANCE_NFC = "0802";
    public static final String OPERA_ENTER_QRCODE = "0803";
    public static final String OPERA_SCAN_QRCODE = "0804";
    public static final String OPERA_ENTER_ATTENDANCE_BEACON = "0805";
    public static final String OPERA_ATTENDANCE_SHAKE = "0806";
    public static final String OPERA_ATTENDANCE_CLICK = "0807";
    public static final String OPERA_OPEN_AUTO_ATTENDANCE = "0808";
    public static final String OPERA_CLOSE_AUTO_ATTENDANCE = "0809";
    public static final String OPERA_WATCH_ATTENDANCE_AREALISTS = "0810";
    public static final String OPERA_CREATE_ATTENDANCE_AREA = "0811";
    public static final String OPERA_MODIFY_ATTENDANCE_AREAINFO = "0812";
    public static final String OPERA_MANAGE_ATTENDANCE_AREAPERSON = "0813";
    public static final String OPERA_DELETE_ATTENDANCE_AREA = "0814";
    public static final String OPERA_WATCH_ATTENDANCE_TIME = "0815";
    public static final String OPERA_SETTING_SIGNLE_TIME = "0816";
    public static final String OPERA_SETTING_DOUBLE_TIME = "0817";
    public static final String OPERA_SETTING_COMPLEX_TIME = "0818";
    public static final String OPERA_WATCH_ATTENDANCES_NFCTAGLISTS = "0819";
    public static final String OPERA_WRITE_ATTENDANCE_NFCTAG = "0820";
    public static final String OPERA_WATCH_ATTENDANCE_BEACONTAG = "0821";
    public static final String OPERA_BIND_ATTENDANCE_BEANCONTAG = "0822";
    public static final String OPERA_ENTER_ATTENDANCE_RECORD = "0823";
    public static final String OPERA_WATCH_ATTENDANCE_RECORD = "0824";
    public static final String OPERA_EXPAND_CARD_RECORD = "0825";
    public static final String OPERA_EXPAND_TODAY_CLASS = "0826";
    public static final String OPERA_EMAIL_ATTENDANCE_REPORT = "0827";
    public static final String OPERA_DOWNLOAD_ATTENDANCE_REPORT = "0828";
    /*
        0901	公告	工作台/企业管理	进入电子公告
        0902	公告	电子公告/消息中心/widget	公告详情查看
        0903	公告	公告详情	查看大图
        0904	公告	电子公告	删除公告
    */
    public static final String OPERA_ENTER_NOTIFICATION = "0901";
    public static final String OPERA_WATCH_NOTIFICATION = "0902";
    public static final String OPERA_WATCH_NOTIFICATION_BIGIMAGE = "0903";
    public static final String OPERA_DELETE_NOTIFICATION = "0904";
    /*
        1001	任务	工作台	进入任务待办
        1002	任务	发起任务	发只含文字的任务
        1003	任务	发起任务	发含文字、语音的任务
        1004	任务	发起任务	发含文字、图片的任务
        1005	任务	发起任务	发含文字、语音、图片的任务
        1006	任务	发起任务	发只含文字的任务（含@群组）
        1007	任务	发起任务	发含文字、语音的任务（含@群组）
        1008	任务	发起任务	发含文字、图片的任务（含@群组）
        1009	任务	发起任务	发含文字、语音、图片的任务（含@群组）
        1010	任务	任务待办	筛选任务
        1011	任务	任务待办/消息中心/widget	任务详情查看
        1012	任务	任务详情	查看大图
        1013	任务	任务详情	关闭任务
        1014	任务	任务详情	删除任务
        1015	任务	任务详情	回复任务
    */
    public static final String OPERA_ENTER_TASK = "1001";
    public static final String OPERA_PUBLIC_TASK_ONLYWRITING = "1002";
    public static final String OPERA_PUBLIC_TASK_W_S = "1003";
    public static final String OPERA_PUBLIC_TASK_W_I = "1004";
    public static final String OPERA_PUBLIC_TASK_W_S_I = "1005";
    public static final String OPERA_PUBLIC_TASK_W_AT_GROUP = "1006";
    public static final String OPERA_PUBLIC_TASK_W_S_AT_GROUP = "1007";
    public static final String OPERA_PUBLIC_TASK_W_I_AT_GROUP = "1008";
    public static final String OPERA_PUBLIC_TASK_W_S_I_AT_GROUP = "1009";
    public static final String OPERA_SCREEN_TASK = "1010";
    public static final String OPERA_WATCH_TASK_DETAIL = "1011";
    public static final String OPERA_WATCH_TASK_BIGIMAGE = "1012";
    public static final String OPERA_CLOSE_TASK = "1013";
    public static final String OPERA_DELETE_TASK = "1014";
    public static final String OPERA_REPLY_TASK = "1015";
    /*
        1101	消费	工作台	进入消费记录
        1102	消费	工作台	进入自助充值
    */
    public static final String OPERA_ENTER_CONSUME_RECORD = "1101";
    public static final String OPERA_ENTER_AUTO_RECHARGE = "1102";
    /*
        1201	widget	/	widget提醒弹出
        1202	widget	widget提醒	点击进入消息/任务/公告详情
    */
    public static final String OPERA_WIDGET_REMIND = "1201";
    public static final String OPERA_ENTER_WIDGET_REMIND = "1202";
    /*
        1301	消息	消息中心/widget	消息详情查看
        1302	消息	消息详情	接受邀请
        1303	消息	消息详情	同意加入企业的请求
        1304	消息	消息详情	分享
        1305	消息	消息详情	查看企业列表
        1306	消息	消息详情	去管理企业
   */
    public static final String OPERA_WATCH_NEWS_DETAIL = "1301";
    public static final String OPERA_ACCEPT_INVITE = "1302";
    public static final String OPERA_AGREE_JOIN_ENTERPRISE = "1303";
    public static final String OPERA_SHARE = "1304";
    public static final String OPERA_GOTO_WATCH_ENTERPRISELISTS = "1305";
    public static final String OPERA_GOTO_MANAGE_ENTERPRISE = "1306";
    /*
        1401	NFC应用	生活圈	进入手机读公交卡
        1402	NFC应用	手机读公交卡	读取
        1403	NFC应用	生活圈	进入NFC互动标签
        1404	NFC应用	NFC互动标签	写入标签
        1405	NFC应用	NFC互动标签	读取
        1406	NFC应用	/	读取和执行NFC互动标签
    */
    public static final String OPERA_ENTER_READBUSCARD = "1401";
    /*1402已取消*/
//    public static final String OPERA_READ_BUSCARD = "1402";
    public static final String OPERA_ENTER_INTERACTTAG = "1403";
    public static final String OPERA_WRITE_NFCTAG = "1404";
    public static final String OPERA_READ_NFCTAG = "1405";
    public static final String OPERA_READ_EXECUTE_NFCTAG = "1406";
    /*
        1501	其他应用	工作台	进入其他应用（乐健康）
        1502	其他应用	工作台	进入其他应用（综合办公）
        1503	其他应用	工作台	进入其他应用（189邮箱）
        1504	其他应用	工作台	进入其他应用（外勤助手）
        1505	其他应用	生活圈	进入翼支付
        1506	其他应用	生活圈	进入手机钱包
    */
    public static final String OPERA_ENTER_HEALTHY = "1501";
    public static final String OPERA_ENTER_OFFICE = "1502";
    public static final String OPERA_ENTER_189EMAIL = "1503";
    public static final String OPERA_ENTER_OUTWORK = "1504";
    public static final String OPERA_ENTER_YIZHIFU = "1505";
    public static final String OPERA_ENTER_PHONEWALLET = "1506";
    /*
        1601	会议	工作台	进入会议签到
        1602	会议	新建会议	提交会议
        1603	会议	新建会议/会议管理	下载二维码
        1604	会议	扫一扫	扫会议标签
        1605	会议	会议签到	查看会议详情
        1606	会议	会议管理	更新二维码
        1607	会议	会议管理	查看出席人员名单
        1608	会议	出席人员	下载报表
        1609	会议	出席人员	发送到邮箱
    */
    public static final String OPERA_ENTER_MEETING = "1601";
    public static final String OPERA_COMMIT_MEETING = "1602";
    public static final String OPERA_DOWNLOAD_QRCODE = "1603";
    public static final String OPERA_SCAN_MEETING_QRCODE = "1604";
    public static final String OPERA_WATCH_MEETING_DETAIL = "1605";
    public static final String OPERA_UPDATE_QRCODE = "1606";
    public static final String OPERA_WATCH_JOINMEETING_PERSON = "1607";
    public static final String OPERA_DOWNLOAD_MEETING_REPORT = "1608";
    public static final String OPERA_EMAIL_MEETING_REPORT = "1609";
    /*
        1701	巡检	工作台	进入巡检
        1702	巡检	扫一扫	扫巡检标签（已取消）
        1703	巡检	巡检	巡检定位签到
        1704	巡检	巡检	进入巡检管理
        1705	巡检	巡检	查看巡检记录
        1706	巡检	巡检报表	下载到手机
        1707	巡检	巡检报表	发送到邮箱
        1708	巡检	巡检管理	查看巡检点列表
        1709	巡检	巡检管理	查看巡检路线列表
        1710	巡检	巡检管理	查看本单位NFC巡检标签列表
        1711	巡检	巡检管理	查看巡检时间
        1712	巡检	巡检点列表	删除巡检点
        1713	巡检	巡检点	新建巡检点
        1714	巡检	巡检点	修改巡检点
        1715	巡检	巡检路线列表	删除路线
        1716	巡检	巡检路线	新建路线
        1717	巡检	巡检路线	修改路线
        1718	巡检	巡检路线	关联巡检点
        1719	巡检	巡检路线	关联人员
        1720	巡检	NFC巡检标签列表	删除标签
        1721	巡检	制作巡检标签	写入标签
        1722	巡检	巡检时间设置	设置简约型单班段
        1723	巡检	巡检时间设置	设置简约型双班段
        1724	巡检	巡检时间设置	设置精细型
    */
    public static final String OPERA_ENTER_PATROL = "1701";
    //    public static final String OPERA_SCAN_PATROL_TAG = "1702"; //已取消
    public static final String OPERA_LOCATING_PATROL = "1703";
    public static final String OPERA_ENTER_PATROL_MANAGE = "1704";
    public static final String OPERA_WATCH_PATROL_RECORD = "1705";
    public static final String OPERA_DOWNLOAD_PATROL_REPORT = "1706";
    public static final String OPERA_EMAIL_PATROL_REPORT = "1707";
    public static final String OPERA_WATCH_PATORLPOINT_LISTS = "1708";
    public static final String OPERA_WTACH_PATROLROUTE_LISTS = "1709";
    public static final String OPERA_WATCH_PATROLTAG_LISTS = "1710";
    public static final String OPERA_WATCH_PATROLTIME = "1711";
    public static final String OPERA_DELETE_PATROLPOINT = "1712";
    public static final String OPERA_CREATE_PATROLPOINT = "1713";
    public static final String OPERA_MODIFY_PATROLPOINT = "1714";
    public static final String OPERA_DELETE_PATROLROUTE = "1715";
    public static final String OPERA_CREATE_PATROLROUTE = "1716";
    public static final String OPERA_MODIFY_PATROLROUTE = "1717";
    public static final String OPERA_BIND_PATROLPOINT = "1718";
    public static final String OPERA_BIND_PATROLPERSON = "1719";
    public static final String OPERA_DELETE_PATROLTAG = "1720";
    public static final String OPERA_CREATE_PATROLTAG = "1721";
    public static final String OPERA_SETTING_SIGNLE_PATROLTIME = "1722";
    public static final String OPERA_SETTING_DOUBLE_PATROLTIME = "1723";
    public static final String OPERA_SETTING_COMPLEX_PATROLTIME = "1724";
}
