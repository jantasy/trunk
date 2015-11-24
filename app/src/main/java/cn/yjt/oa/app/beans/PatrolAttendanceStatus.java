package cn.yjt.oa.app.beans;

/**
 * Created by xiong on 2015/9/25.
 */
public class PatrolAttendanceStatus {

    public static final int FINISH = 1; // 已完成（巡检完成）
    public static final int NOTFINISH = 2;// 未完成(巡检未完成)
    public static final int UNDERWAY = 3;// 进行中(正在进行巡检)
    public static final int NOSTART = 4;// 未开始

    public static final String FINISH_STATUS = "已完成";
    public static final String NOTFINISH_STATUS = "未完成";
    public static final String UNDERWAY_STATUS = "进行中";
    public static final String NOSTART_STATUS = "未开始";


}
