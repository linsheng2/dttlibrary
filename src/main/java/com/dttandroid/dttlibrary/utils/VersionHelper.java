package com.dttandroid.dttlibrary.utils;

import android.os.Build;
import android.os.Build.VERSION_CODES;

/**
 * @Author: lufengwen
 * @Date: 2015年6月15日 下午7:50:58
 * @Description: 用于Android版本判断
 */
public class VersionHelper {
	/**
	 * 版本号是否高于Android 4.1 (API 16)
	 * 
	 */
	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN;
	}

	/**
	 * 版本号是否高于Android 4.2 (API 17)
	 * 
	 */
	public static boolean hasJellyBeanMr1() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1;
	}

	/**
	 * 版本号是否高于Android 4.3 (API 18)
	 * 
	 */
	public static boolean hasJellyBeanMr2() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2;
	}

	/**
	 * 版本号是否高于Android 4.4 (API 19)
	 * 
	 */
	public static boolean hasKitKat() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT;
	}

	/**
	 * 版本号是否高于Android 5.0
	 * 
	 */
	public static boolean hasLollipop() {
		return Build.VERSION.SDK_INT >= 21;
	}

	public static boolean hasFroyo() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.FROYO;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1;
	}

}