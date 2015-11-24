package io.luobo.common.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Handler;
import android.os.Looper;

public class MyThreadUtils {
	/**
	 * 此方法检查当前线程是否主线程，若非，则抛出运行时异常
	 */
	public static void checkIsOnMainThread() throws RuntimeException{
		checkOnMainThread(Thread.currentThread());
	}
	
	public static boolean isOnMainThread() {
		return (Thread.currentThread() == Looper
				.getMainLooper().getThread());
	}

	/**
	 * 此方法检查某线程是否主线程，若非，则抛出运行时异常
	 */
	public static void checkOnMainThread(Thread thread) throws RuntimeException{
		boolean isMainThread = (thread == Looper
				.getMainLooper().getThread());
		if (!isMainThread)
			throw new RuntimeException(
					"can't invoke from other threads except main thread:" + thread.getName());
	}

	/**
	 * 返回一个绑定在主线程上的Handler
	 * @return
	 */
	public static Handler getMainThreadHandler() {
		if(mMainThreadHandler == null)
			mMainThreadHandler = new Handler(Looper.getMainLooper());
		return mMainThreadHandler;
	}

	private static Handler mMainThreadHandler;
	
	public static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        public DefaultThreadFactory(String suffix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                                  Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" +
                          poolNumber.getAndIncrement() +
                         "-thread-" +
                         suffix + "-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                                  namePrefix + threadNumber.getAndIncrement(),
                                  0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
