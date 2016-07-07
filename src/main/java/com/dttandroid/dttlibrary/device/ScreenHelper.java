package com.dttandroid.dttlibrary.device;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午12:37:07
 * @Description: 屏幕助手类
 */
public class ScreenHelper {

	/**
	 * 获取屏幕宽度，单位：px
	 * 
	 * @param context
	 *            应用程序上下文
	 * @return 屏幕宽度
	 */
	public static int getWidth(Context context) {
		return getDisplayMetrics(context).widthPixels;
	}

	/**
	 * 获取屏幕高度，单位：px
	 * 
	 * @param context
	 *            应用程序上下文
	 * @return 屏幕高度
	 */
	public static int getHeight(Context context) {
		return getDisplayMetrics(context).heightPixels;
	}

	/**
	 * 获取手机屏幕DPI，只会获得下面几个值
	 * 
	 * DENSITY_LOW = 120; DENSITY_MEDIUM = 160; DENSITY_TV = 213; DENSITY_HIGH = 240; DENSITY_XHIGH = 320; DENSITY_XXHIGH = 480; DENSITY_DEFAULT = 160;
	 * 
	 * @param context
	 *            应用程序上下文
	 * @return 手机屏幕DPI
	 */
	public static int getDPI(Context context) {
		return getDisplayMetrics(context).densityDpi;
	}

	/**
	 * 获取手机屏幕密度
	 * 
	 * @param context
	 *            应用程序上下文
	 * @return 手机屏幕密度
	 */
	public static float getDensity(Context context) {
		return getDisplayMetrics(context).density;
	}

	/**
	 * 全屏
	 * 
	 * @param activity
	 *            需要全屏的Activity实例
	 */
	public static void fullScreen(Activity activity) {
		// activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
		attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		activity.getWindow().setAttributes(attrs);
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}

	/**
	 * 退出全屏
	 * 
	 * @param activity
	 *            需要退出全屏的Activity实例
	 */
	public static void exitFullScreen(Activity activity) {
		// activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
		attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
		activity.getWindow().setAttributes(attrs);
		activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}

	/**
	 * 当前系统显示是否开启自动亮度调节
	 * 
	 */
	public static boolean isAutoBrightness(Context context) {
		boolean isAutoBrightness = false;
		try {
			int mode = Settings.System.getInt(context.getContentResolver(), "screen_brightness_mode");
			isAutoBrightness = mode == 1;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return isAutoBrightness;
	}

	/**
	 * 获取当前屏幕亮度
	 * 
	 */
	public static int getCurrentBrightness(Context context) {
		int currentBrightness = 0;
		try {
			currentBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return currentBrightness;
	}

	/**
	 * 设置当前屏幕亮度模式
	 * 
	 * 0: 手动 1: 自动
	 */
	public static void setBrightnessMode(Context context, int mode) {
		Settings.System.putInt(context.getContentResolver(), "screen_brightness_mode", mode);
	}

	/**
	 * 设置屏幕亮度
	 * 
	 * @param activity
	 *            activity实例
	 * @param brightness
	 *            亮度值
	 */
	public static void setBrightness(Activity activity, float brightness) {
		WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
		attrs.screenBrightness = brightness;
		activity.getWindow().setAttributes(attrs);
	}

	private static DisplayMetrics getDisplayMetrics(Context context) {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wndMgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wndMgr.getDefaultDisplay().getMetrics(metrics);
		return metrics;
	}
}
