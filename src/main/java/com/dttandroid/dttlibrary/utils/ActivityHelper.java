package com.dttandroid.dttlibrary.utils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * @Author: lufengwen
 * @Date: 2015年6月15日 下午8:16:20
 * @Description: Activity辅助类
 */
public class ActivityHelper {

	public static void showSoftInput(final Context context, final View focusView) {
		focusView.requestFocus();
		focusView.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager inputMgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMgr.showSoftInput(focusView, 0);
			}
		}, 200);
	}

	public static void hideSoftInput(final Activity activity) {
		if (activity != null && activity.getCurrentFocus() != null) {
			if (activity.getCurrentFocus().getWindowToken() != null) {
				InputMethodManager inputMgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMgr.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}

	public static void hideSoftInput(final Activity activity, View currentFocus) {
		if (activity != null && activity.getCurrentFocus() != null) {
			if (activity.getCurrentFocus().getWindowToken() != null) {
				InputMethodManager inputMgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMgr.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}

	public static void defaultHideSoftInput(final Activity activity) {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager inputMgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
				if (inputMgr != null && activity != null && activity.getCurrentFocus() != null)
					inputMgr.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 200);
	}

	/**
	 * 判断指定Activity实例是否正在运行
	 * 
	 * @param activity
	 *            指定Activity实例
	 * @return 指定Activity实例运行结果
	 */
	public static boolean isActivityRunning(Activity activity) {
		return activity != null && !activity.isFinishing();
	}

	// 该方法在Android 5.0以后，已经被废弃
	public static boolean isActivityRunning(Context ctx, String activityClassName) {
		ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		@SuppressWarnings("deprecation")
		List<RunningTaskInfo> info = activityManager.getRunningTasks(1);
		if (info != null && info.size() > 0) {
			ComponentName component = info.get(0).topActivity;
			if (activityClassName.equals(component.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 将dip转换为px
	 * 
	 * @param a_fDipValue
	 * @return
	 */
	public static int dipToPx(Context a_oContext, float a_fDipValue) {
		return dipTopx(a_oContext, a_fDipValue);
	}

	/**
	 * Dip转成对应Px值
	 * 
	 * @param context
	 *            上下文
	 * @param dip
	 * @return
	 */
	public static int dipTopx(Context context, float dip) {
		float s = context.getResources().getDisplayMetrics().density;
		return (int) (dip * s + 0.5f);
	}
}