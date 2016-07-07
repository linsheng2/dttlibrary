package com.dttandroid.dttlibrary.utils;

import android.R;
import android.annotation.TargetApi;
import android.content.Context;
import android.view.ContextThemeWrapper;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午12:13:21
 * @Description: View低版本兼容类
 */
public class ViewCompat {
	/**
	 * 获取指定Dark主题样式的
	 * 
	 * @param context
	 * @return
	 */
	public static Context getDarkContextThemeWrapper(Context context) {
		return new ContextThemeWrapper(context, getDarkThemeResId());
	}

	/**
	 * 获取指定Light主题样式的
	 * 
	 * @param context
	 * @return
	 */
	public static Context getLightContextThemeWrapper(Context context) {
		return new ContextThemeWrapper(context, getLightThemeResId());
	}

	public static int getDarkNoActionBarThemeResId() {
		return VersionHelper.hasLollipop() ? R.style.Theme_Material_NoActionBar : R.style.Theme_Holo_NoActionBar;
	}

	/**
	 * 获取指定Dark主题样式资源ID
	 * 
	 * @return 0为不采用主题样式，大于0为具体平台主题样式
	 */
	@TargetApi(21)
	public static int getDarkThemeResId() {
		return VersionHelper.hasLollipop() ? R.style.Theme_Material : R.style.Theme_Holo;
	}

	/**
	 * 获取指定Light主题样式资源ID
	 * 
	 * @return 0为不采用主题样式，大于0为具体平台主题样式
	 */
	@TargetApi(21)
	public static int getLightThemeResId() {
		return VersionHelper.hasLollipop() ? R.style.Theme_Material_Light : R.style.Theme_Holo_Light;
	}
}
