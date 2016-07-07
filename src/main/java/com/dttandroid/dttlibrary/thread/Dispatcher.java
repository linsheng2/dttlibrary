package com.dttandroid.dttlibrary.thread;

import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.os.Looper;

/**
 * @Author: lufengwen
 * @Date: 2015年6月22日 上午10:53:02
 * @Description:
 */
public class Dispatcher {
	private static Thread sUiThread;
	private static Handler sHandler;

	/**
	 * 初始化Dispatcher
	 * @param uiThread UI线程对象
	 */
	public static void init(Thread uiThread) {
		if (Looper.myLooper() != Looper.getMainLooper()) {
			throw new IllegalThreadStateException("Not on ui thread");
		}
		sUiThread = uiThread;
		sHandler = new Handler();
	}

	/**
	 * 当前线程是否在UI线程
	 */
	public static boolean isOnUiThread() {
		return Thread.currentThread() == sUiThread;
	}

	/**
	 * 任务延迟运行在UI线程上
	 * @param task 任务
	 * @param delayMillis 延迟时间，单位：毫秒
	 */
	public static void delayRunOnUiThread(Runnable task, long delayMillis) {
		sHandler.postDelayed(task, delayMillis);
	}

	/**
	 * 任务运行在UI线程上
	 * @param task 任务
	 */
	public static void runOnUiThread(Runnable task) {
		if (isOnUiThread()) {
			task.run();
		}
		else {
			sHandler.post(task);
		}
	}

	/**
	 * 任务运行在新创建的线程（非线程池）
	 * @param task
	 */
	public static void runOnNewThread(Runnable task) {
		new Thread(task).start();
	}

	/**
	 * 任务运行在公共线程上
	 * @param task 任务
	 */
	public static void runOnCommonThread(Runnable task) {
		CommonThreadPool.submit(task);
	}
	
	/**
     * 任务运行在Web线程池上
     * @param task 任务
     */
    public static void runOnWebThread(Runnable task) {
        WebThreadPool.submit(task);
    }

	/**
	 * 任务运行在单线程上，如果当前有任务正在运行，将添加到队列中
	 * @param task 任务
	 */
	public static void runOnSingleThread(Runnable task) {
		SingleThreadPool.submit(task);
	}

	/**
	 * 任务运行在调度线程上
	 * @param task  任务
	 * @param delay 延迟时间
	 * @param unit  时间单位
	 */
	public static void runOnScheduledThread(Runnable task, long delay, TimeUnit unit) {
		ScheduledThreadPool.submit(task, delay, unit);
	}

	/**
	 * 任务运行在调度线程上
	 * @param task   任务
	 * @param delay  延迟时间
	 * @param period 间隔时间
	 * @param unit   时间单位
	 */
	public static void runOnScheduledThread(Runnable task, long delay, long period, TimeUnit unit) {
		ScheduledThreadPool.submit(task, delay, period, unit);
	}

}
