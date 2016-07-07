package com.dttandroid.dttlibrary.debug;

import android.util.Log;

/**
 * @Author: lufengwen
 * @Date: 2015年6月18日 下午11:49:40
 * @Description: 日志工具
 */
public class AppLogger {
	public static final String APP_TAG = "foodstore";
	private static boolean sIsDubug = true;

	private static final int DEFAULT_RESULT = -1;

	public static void init(boolean isDebug) {
		sIsDubug = isDebug;
	}

	public static boolean isDebug() {
		return sIsDubug;
	}

	/**
	 * 使用APP_TAG作为标签打印调试日志
	 * @param msg 日志消息
	 */
	public static int d(String msg) {
		if (sIsDubug) {
			return Log.d(APP_TAG, buildMessage(msg));
		}
		return DEFAULT_RESULT;
	}

	/**
	 * 使用APP_TAG作为标签打印调试日志
	 * @param msg 日志消息
	 * @param isPrintCaller 是否打印调用函数
	 */
	public static int d(String msg, boolean isPrintCaller) {
		if (sIsDubug) {
			return Log.d(APP_TAG, isPrintCaller ? buildMessage(msg) : msg);
		}
		return DEFAULT_RESULT;
	}

	/**
	 * 使用APP_TAG作为标签打印错误日志
	 * @param msg 日志信息
	 */
	public static int e(String msg) {
		if (sIsDubug) {
			return Log.e(APP_TAG, buildMessage(msg));
		}
		return DEFAULT_RESULT;
	}

	/**
	 * 使用APP_TAG作为标签打印错误日志
	 * @param msg 日志信息
	 * @param isPrintCaller 是否打印调用函数
	 */
	public static int e(String msg, boolean isPrintCaller) {
		if (sIsDubug) {
			return Log.e(APP_TAG, isPrintCaller ? buildMessage(msg) : msg);
		}
		return DEFAULT_RESULT;
	}

	/**
	 * 使用指定tag作为标签打印调试日志
	 * @param tag 指定tag
	 * @param msg 日志信息
	 */
	public static int d(String tag, String msg) {
		if (sIsDubug) {
			return Log.d(tag, buildMessage(msg));
		}
		return DEFAULT_RESULT;
	}

	/**
	 * 使用指定tag作为标签打印调试日志
	 * @param tag 指定tag
	 * @param msg 日志信息
	 * @param isPrintCaller 是否打印调用者
	 */
	public static int d(String tag, String msg, boolean isPrintCaller) {
		if (sIsDubug) {
			return Log.d(tag, isPrintCaller ? buildMessage(msg) : msg);
		}
		return DEFAULT_RESULT;
	}

	/**
	 * 使用指定tag作为标签打印调试日志
	 * @param tag 指定tag
	 * @param msg 日志信息
	 * @param tr  异常日志
	 * @return
	 */
	public static int d(String tag, String msg, Throwable tr) {
		if (sIsDubug) {
			return Log.d(tag, msg, tr);
		}
		return DEFAULT_RESULT;
	}

	/**
	 * 使用指定tag作为标签打印错误日志
	 * @param tag 指定tag
	 * @param msg 日志信息
	 */
	public static int e(String tag, String msg) {
		if (sIsDubug) {
			Log.e(tag, buildMessage(msg));
		}
		return DEFAULT_RESULT;
	}

	/**
	 * 使用指定tag作为标签打印错误日志
	 * @param tag 指定tag
	 * @param msg 日志信息
	 * @param isPrintCaller 是否打印调用者
	 */
	public static int e(String tag, String msg, boolean isPrintCaller) {
		if (sIsDubug) {
			return Log.e(tag, isPrintCaller ? buildMessage(msg) : msg);
		}
		return DEFAULT_RESULT;
	}

	/**
	 * 使用指定tag作为标签打印调试日志
	 * @param tag 指定tag
	 * @param msg 日志信息
	 * @param tr  异常日志
	 * @return
	 */
	public static int e(String tag, String msg, Throwable tr) {
		if (sIsDubug) {
			return Log.e(tag, msg, tr);
		}
		return DEFAULT_RESULT;
	}

	/**
	 * 使用指定tag作为标签打印错误日志
	 * @param tag 指定tag
	 * @param msg 日志信息
	 */
	public static int i(String tag, String msg) {
		if (sIsDubug) {
			Log.i(tag, buildMessage(msg));
		}
		return DEFAULT_RESULT;
	}

	/**
	 * 使用指定tag作为标签打印调试日志
	 * 
	 * @param tag 指定tag
	 * @param msg 日志信息
	 * @param tr  异常日志
	 * @return
	 */
	public static int i(String tag, String msg, Throwable tr) {
		if (sIsDubug) {
			return Log.i(tag, msg, tr);
		}
		return DEFAULT_RESULT;
	}

	/**
	 * 使用指定tag作为标签打印错误日志
	 * @param tag 指定tag
	 * @param msg 日志信息
	 */
	public static int v(String tag, String msg) {
		if (sIsDubug) {
			Log.v(tag, buildMessage(msg));
		}
		return DEFAULT_RESULT;
	}

	/**
	 * 使用指定tag作为标签打印调试日志
	 * @param tag 指定tag
	 * @param msg 日志信息
	 * @param tr  异常日志
	 * @return
	 */
	public static int v(String tag, String msg, Throwable tr) {
		if (sIsDubug) {
			return Log.v(tag, msg, tr);
		}
		return DEFAULT_RESULT;
	}

	/**
	 * 使用指定tag作为标签打印错误日志
	 * @param tag 指定tag
	 * @param msg 日志信息
	 */
	public static int w(String tag, String msg) {
		if (sIsDubug) {
			Log.w(tag, buildMessage(msg));
		}
		return DEFAULT_RESULT;
	}

	/**
	 * 使用指定tag作为标签打印错误日志
	 * @param tag 指定tag
	 * @param tr  异常日志
	 */
	public static int w(String tag, Throwable tr) {
		if (sIsDubug) {
			return Log.w(tag, tr);
		}
		return DEFAULT_RESULT;
	}

	/**
	 * 使用指定tag作为标签打印调试日志
	 * @param tag 指定tag
	 * @param msg 日志信息
	 * @param tr  异常日志
	 * @return
	 */
	public static int w(String tag, String msg, Throwable tr) {
		if (sIsDubug) {
			return Log.w(tag, msg, tr);
		}
		return DEFAULT_RESULT;
	}

	public static String getStackTraceString(Throwable tr) {
		if (sIsDubug) {
			return Log.getStackTraceString(tr);
		}
		return "";
	}

	private static String buildMessage(String msg) {
		StackTraceElement caller = new Throwable().fillInStackTrace().getStackTrace()[2];
		return new StringBuilder().append(caller.getClassName()).append(".").append(caller.getMethodName()).append("(): ").append(msg).toString();
	}
}
