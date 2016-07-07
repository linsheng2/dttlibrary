package com.dttandroid.dttlibrary.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.TextUtils;

/**
 * @Author: lufengwen
 * @Date: 2015年9月9日 下午3:01:28
 * @Description:
 */
public class RegExpValidatorUtils {

	/**
	* 验证电话号码
	* @param 待验证的字符串
	* @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	*/
	public static boolean isTelephone(String str) {
		// String regex = "^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8]))\\d{8}$";
		if (!TextUtils.isEmpty(str)) {
			if (str.startsWith("1") && str.length() == 11) {
				return true;
			}
		}
		return false;
		// return match(regex, str);
	}

	/**
	* 验证网址Url
	* @param 待验证的字符串
	* @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	*/
	public static boolean isUrl(String str) {
		String regex = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
		return match(regex, str);
	}

	/**
	* @param regex 正则表达式字符串
	* @param str 要匹配的字符串
	* @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
	*/
	private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

}
