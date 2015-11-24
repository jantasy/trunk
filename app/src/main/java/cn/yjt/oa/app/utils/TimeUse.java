package cn.yjt.oa.app.utils;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * 测试方法执行所耗的时间
 */
public class TimeUse {

	private static final String TAG = "TimeUse";
	private String name;
	private long startTime;

	// 初始化一个ThreadLocal对象，泛型是TimeUse，用来保证多线程下TimeUse的唯一性
	private static ThreadLocal<TimeUse> timeuseThreadLocal = new ThreadLocal<TimeUse>();

	// 设置TimeUse池
	private static final int POOL_MAX_SIZE = 5;
	private static List<TimeUse> sPool = new ArrayList<TimeUse>(POOL_MAX_SIZE);

	/**
	 * 从TimeUse池中获取一个TimeUse
	 * 
	 * @param name 将该name赋值给返回的TimeUse对象中的name属性，相当于所测的代码段或者函数的的标识，用于打印显示用
	 * @return 一个TimeUse对象
	 */
	static TimeUse obtain(String name) {
		TimeUse t;
		synchronized (sPool) {
			if (!sPool.isEmpty()) {
				t = sPool.remove(0);
			} else {
				t = new TimeUse();
			}
		}
		t.reset();
		t.name = name;
		// 设置开始时间
		t.startTime = System.nanoTime();
		return t;
	}

	/**
	 * 重置该TimeUse 将其name属性置为null，startTime赋值为0
	 */
	void reset() {
		name = null;
		startTime = 0;
	}

	/**
	 * 当TimeUse不使用时，将其放回TimeUse池中
	 */
	public void recycle() {
		reset();
		synchronized (sPool) {
			if (sPool.size() < POOL_MAX_SIZE) {
				sPool.add(this);
			}
		}
	}

	/**
	 * 计时开始，获取一个TimeUse放入ThreadLocal中
	 * 
	 * @param name TimeUse的名称，用于打印日志时标识方法
	 */
	public static void start(String name) {
		timeuseThreadLocal.set(TimeUse.obtain(name));
	}

	/**
	 * 计时结束，将TimeUse会受到TimeUse池中，
	 */
	public static void stop() {
		TimeUse timeUse = timeuseThreadLocal.get();
		if (timeUse != null) {
			// 通过下面的日志，打印出该方法执行的耗时
			Log.v(TAG, String.format("%s uses %s millseconds", timeUse.name,
					(System.nanoTime() - timeUse.startTime) / 1000000f));
			timeUse.recycle();
			timeuseThreadLocal.set(null);
		}
	}
}
