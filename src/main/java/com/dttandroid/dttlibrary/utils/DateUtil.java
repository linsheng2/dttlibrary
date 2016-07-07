package com.dttandroid.dttlibrary.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.text.TextUtils;

/**
 * @Author: lufengwen
 * @Date: 2015年8月28日 下午3:01:22
 * @Description:
 */
public class DateUtil {
	public static final int COMPARE_TYPE_YEAR = 0;
	public static final int COMPARE_TYPE_MONTH = 1;
	public static final int COMPARE_TYPE_DAY = 2;

	public static final int SECOND = 1;
	public static final int MINUTE = 60 * SECOND;
	public static final int HOUR = 60 * MINUTE;
	public static final int DAY = 24 * HOUR;
	public static final int WEEK = 7 * DAY;
	public static final int YEAR = 365 * DAY;

	/**
	 * 生日转年龄
	 * 
	 * @param date
	 *            8位整形数如 19890604
	 * @return 返回年龄，负数表示发送错误
	 */
	public static int birthdayToAge(int date) {
		return DateUtil.birthdayToAge(String.valueOf(date), "yyyyMMdd");
	}

	/**
	 * 生日转年龄
	 * 
	 * @param dateString
	 *            生日日期字符串 "1999-09-09"
	 * @param formatString
	 *            日期格式 "yyyy-MM-dd"
	 * @return 返回年龄，负数表示发送错误
	 */
	public static int birthdayToAge(String dateString, String formatString) {
		return DateUtil.compareDate(dateString, null, formatString, COMPARE_TYPE_YEAR);
	}

	/**
	 * 生日转年龄
	 * 
	 * @param date
	 *            生日日期
	 * @return 返回年龄，负数表示发生错误
	 */
	public static int birthdayToAge(Date date) {
		return DateUtil.compareDate(date, null, COMPARE_TYPE_YEAR);
	}

	/**
	 * 日期比较
	 * 
	 * @param dateString1
	 *            要比较的日期字符串1 "1999-09-09"
	 * @param dateString2
	 *            要比较的日期字符串2 "2009-09-09" 此参数传null表示当前日期
	 * @param formatString
	 *            日期格式 "yyyy-MM-dd"
	 * @param type
	 *            要比较的类型 COMPARE_TYPE_YEAR 返回相差的年 COMPARE_TYPE_MONTH 返回相差的月
	 *            COMPARE_TYPE_DAY 返回相差的天
	 * @return 返回相差类型的数目，负数表示发生错误
	 */
	public static int compareDate(String dateString1, String dateString2, String formatString, int type) {
		if (TextUtils.isEmpty(dateString1)) {
			return -1;
		}
		Date date1 = DateUtil.parseDate(dateString1, formatString);
		Date date2 = null;
		if (!TextUtils.isEmpty(dateString2)) {
			date2 = DateUtil.parseDate(dateString2, formatString);
			if (date2 == null) {
				return -1;
			}
		}
		return compareDate(date1, date2, type);
	}

	/**
	 * 日期比较
	 * 
	 * @param date1
	 *            要比较的日期1 "1999-09-09"
	 * @param date2
	 *            要比较的日期2 "2009-09-09" 此参数传null表示当前日期
	 * @param type
	 *            要比较的类型 COMPARE_TYPE_YEAR 返回相差的年 COMPARE_TYPE_MONTH 返回相差的月
	 *            COMPARE_TYPE_DAY 返回相差的天
	 * @return 返回相差类型的数目，负数表示发生错误
	 */
	public static int compareDate(Date date1, Date date2, int type) {
		long ret = 0;
		if (date1 == null) {
			return -1;
		}
		if (date2 == null) {
			date2 = new Date();
		}
		switch (type) {
		case COMPARE_TYPE_DAY:
			ret = Math.abs(date1.getTime() - date2.getTime()) / 1000 / DAY;
			break;
		case COMPARE_TYPE_MONTH:
			ret = Math.abs(getYear(date1) - getYear(date2)) * 12;
			if (ret == 0) {
				ret = Math.abs(getMonth(date1) - getMonth(date2));
			}
			else {
				ret -= (getMonth(date1) - getMonth(date2));
			}
			break;
		case COMPARE_TYPE_YEAR:
			ret = Math.abs(getYear(date1) - getYear(date2));
			break;
		}
		return (int) ret;
	}

	public enum DateType {
		TODAY, YESTERDAY, THIS_YEAR, OTHER_YEAR
	}

	/**
	 * 获取日期类型
	 * 
	 * @param dstDate
	 *            日期
	 * @return {@link DateType} 今天、昨天、今年、非今年
	 */
	public static DateType getDateType(Date dstDate) {
		if (dstDate == null) {
			return DateType.OTHER_YEAR;
		}
		int dstYear = getYear(dstDate);
		int dstMonth = getMonth(dstDate);
		int dstDay = getDay(dstDate);

		Date currentDate = new Date();
		int currentYear = getYear(currentDate);
		int currentMonth = getMonth(currentDate);
		int currentDay = getDay(currentDate);

		if (dstYear == currentYear) {
			// 这个月的日期
			if (dstMonth == currentMonth) {
				// 今天
				if (dstDay == currentDay) {
					return DateType.TODAY;
				}
				// 昨天
				else if (currentDay - dstDay == 1) {
					return DateType.YESTERDAY;
				}
			}
			// 今年的日期
			return DateType.THIS_YEAR;
		}
		// 不是今年的日期
		return DateType.OTHER_YEAR;
	}

	/**
	 * 获取日期类型
	 * 
	 * @param time
	 *            从 1970-01-01 00:00:00 开始的秒数
	 * @return {@link DateType} 今天、昨天、今年、非今年
	 */
	public static DateType getDateType(long time) {
		return getDateType(new Date(time * 1000));
	}

	/**
	 * 获取上个月的最后一天
	 * 
	 * @return 上个月最后一天
	 */
	public static String lastMonLastDay() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String months = "";
		String days = "";
		if (month > 1) {
			month--;
		}
		else {
			year--;
			month = 12;
		}
		if (!(String.valueOf(month).length() > 1)) {
			months = "0" + month;
		}
		else {
			months = String.valueOf(month);
		}
		if (!(String.valueOf(day).length() > 1)) {
			days = "0" + day;
		}
		else {
			days = String.valueOf(day);
		}
		String lastDay = "" + year + "-" + months + "-" + days;
		String[] lastMonth = new String[2];
		lastMonth[1] = lastDay;
		return days;
	}

	public static boolean isSameDay(long timeA, long timeB) {
		return isSameDay(new Date(timeA), new Date(timeB));
	}

	public static boolean isSameDay(Date dateA, Date dateB) {
		return DateUtil.getYear(dateA) == DateUtil.getYear(dateB) && DateUtil.getMonth(dateA) == DateUtil.getMonth(dateB) && DateUtil.getDay(dateA) == DateUtil.getDay(dateB);
	}

	/**
	 * 解析日期
	 * 
	 * @param year
	 *            日期的8位整数如19890604
	 * @return 返回日期 null表示发生错误
	 */
	public static Date parseDate(int year) {
		return DateUtil.parseDate(String.valueOf(year), "yyyyMMdd");
	}

	/**
	 * 解析日期
	 * 
	 * @param year
	 *            4位年份 2008
	 * @param month
	 *            2位月份 05
	 * @param day
	 *            2位日份 12
	 * @return 返回日期 null表示发生错误
	 */
	public static Date parseDate(int year, int month, int day) {
		return parseDate(year, month, day, 0, 0, 0);
	}

	/**
	 * 解析日期
	 * 
	 * @param year
	 *            4位年份 2008
	 * @param month
	 *            2位月份 05
	 * @param day
	 *            2位日份 12
	 * @param hour
	 *            2位小时 15
	 * @param minute
	 *            2位分钟 11
	 * @param second
	 *            2位秒数 11
	 * @return 返回日期 null表示发生错误
	 */
	public static Date parseDate(int year, int month, int day, int hour, int minute, int second) {
		String dateFormat = "yyyy-MM-dd HH:mm:ss";
		String dateString = String.format(Locale.getDefault(), "%04d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, minute, second);
		return parseDate(dateString, dateFormat);
	}

	/**
	 * 解析日期
	 * 
	 * @param dateString
	 *            日期字符串 "1989-06-04 18:00:00"
	 * @param dateFormat
	 *            日期格式 "yyyy-MM-dd HH:mm:ss"
	 * @return 返回日期 null表示发生错误
	 */
	public static Date parseDate(String dateString, String dateFormat) {
		Date date = null;
		SimpleDateFormat format = null;
		try {
			format = new SimpleDateFormat(dateFormat, Locale.getDefault());
		}
		catch (NullPointerException e) {
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		if (format != null) {
			try {
				date = format.parse(dateString);
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return date;
	}

	/**
	 * 日期转字符串
	 * 
	 * @param date
	 *            要转换的日期
	 * @param dateFormat
	 *            "yyyy-MM-dd HH:mm:ss"
	 * @return 返回日期 null表示发生错误
	 */
	public static String parseString(Date date, String dateFormat) {
		String dateString = null;
		SimpleDateFormat format = null;
		try {
			format = new SimpleDateFormat(dateFormat, Locale.getDefault());
		}
		catch (NullPointerException e) {
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		if (format != null && date != null) {
			dateString = format.format(date);
		}
		return dateString;
	}

	/**
	 * 日期转字符串
	 * 
	 * @param date
	 *            要转换的日期
	 * @return 返回日期整数 如199890604 错误返回-1
	 */
	public static int parseInt(Date date) {
		String dateString = parseString(date, "yyyyMMdd");
		if (TextUtils.isEmpty(dateString)) {
			return -1;
		}
		else {
			try {
				return Integer.parseInt(dateString);
			}
			catch (NumberFormatException e) {
				return -1;
			}
		}
	}

	/**
	 * 获取date的年份
	 * 
	 * @param date
	 * @return 返回年份 -1表示发生错误
	 */
	public static int getYear(Date date) {
		return calendarGetField(Calendar.YEAR, date);
	}

	/**
	 * 获取date的月份 (1-12月)
	 * 
	 * @param date
	 * @return 返回月份 -1表示发生错误
	 */
	public static int getMonth(Date date) {
		// 返回的month -1 是发生了异常 0-11 表示12个月 12表示转换时错误
		int month = calendarGetField(Calendar.MONTH, date);
		return month <= Calendar.DECEMBER && month >= Calendar.JANUARY ? month + 1 : -1;
	}

	/**
	 * 获取date的日份(该日是该月中的哪天)
	 * 
	 * @param date
	 * @return 返回日份 -1表示发生错误
	 */
	public static int getDay(Date date) {
		return calendarGetField(Calendar.DAY_OF_MONTH, date);
	}

	/**
	 * 获取date的日份是星期几(该日是星期几)
	 * 
	 * @param date
	 * @return 返回星期几 -1表示发生错误
	 */
	public static int getDayOfWeek(Date date) {
		return calendarGetField(Calendar.DAY_OF_WEEK, date);
	}

	/**
	 * 获取date的小时
	 * 
	 * @param date
	 *            日期
	 * @return 返回小时 -1表示发生错误
	 */
	public static int getHour(Date date) {
		return calendarGetField(Calendar.HOUR_OF_DAY, date);
	}

	/**
	 * 获取date的分钟
	 * 
	 * @param date
	 *            日期
	 * @return 返回分钟 -1表示发生错误
	 */
	public static int getMinute(Date date) {
		return calendarGetField(Calendar.MINUTE, date);
	}

	/**
	 * 获取date的秒钟
	 * 
	 * @param date
	 *            日期
	 * @return 返回秒钟 -1表示发生错误
	 */
	public static int getSecond(Date date) {
		return calendarGetField(Calendar.SECOND, date);
	}

	public static String formatElapsedTime(int seconds) {
		return String.format(Locale.CHINA, "%02d:%02d", seconds / 60, seconds % 60);
	}

	private static int calendarGetField(int field, Date date) {
		int ret = -1;
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(date);
			ret = calendar.get(field);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	@SuppressLint("SimpleDateFormat")
	public static String formatDateStr(Calendar cal, String format) {
		SimpleDateFormat formator = new SimpleDateFormat(format);
		return formator.format(cal.getTime());
	}

	@SuppressLint("SimpleDateFormat")
	public static String formatDateStr(Date date, String format) {
		SimpleDateFormat formator = new SimpleDateFormat(format);
		return formator.format(date);
	}
}
