package cn.yjt.oa.app.attendance;

public final class AlarmRequestCode {
	
	public static final int ATTENDANCE_TIME_CHECK = 1;
	
	public static final int AUTO_ATTENDANCE_TIME_BASE = 10;
	
	private static int allocTimes;
	
	public static final int allocAutoAttendanceTimeCode(){
		allocTimes ++;
		return AUTO_ATTENDANCE_TIME_BASE + allocTimes;
	}
	
	public static final void resetAlloc(){
		allocTimes = 0;
		
	}
}
