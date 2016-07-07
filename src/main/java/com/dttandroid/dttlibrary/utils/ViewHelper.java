package com.dttandroid.dttlibrary.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

/**
 * @Author: lufengwen
 * @Date: 2015年6月13日 下午12:38:21
 * @Description: View辅助类
 */
public class ViewHelper {
	public static final int NOT_OUT_OF_BOUNDS = 0;
	public static final int OUT_OF_LEFT_BOUNDS = 1;
	public static final int OUT_OF_TOP_BOUNDS = 2;
	public static final int OUT_OF_RIGHT_BOUNDS = 3;
	public static final int OUT_OF_BOTTOM_BOUNDS = 4;

	public static int dp2px(Context context, float dp) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics));
	}

	public static int sp2px(Context context, float sp) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metrics));
	}

	public static int px2dp(Context context, float px) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) ((px / scale) + 0.5f);
	}

	/**
	 * 获取文字高度和行高
	 * 
	 * @param fontSize
	 * @return
	 */
	public static int getFontHeight(float fontSize) {
		Paint paint = new Paint();
		paint.setTextSize(fontSize);
		FontMetrics fm = paint.getFontMetrics();
		return (int) Math.ceil(fm.descent - fm.ascent);
	}

	/**
	 * 设置TextView控件的值，当值的长度过长时，在最后用省略号代替
	 * @param view TextView控件
	 * @param text TextView控件的值
	 * @param dp dp值
	 */
	public static void setEllipsize(TextView view, CharSequence text, float dp) {
		CharSequence charSequence = TextUtils.ellipsize(text, view.getPaint(), ViewHelper.dp2px(view.getContext(), dp), TextUtils.TruncateAt.END);
		view.setText(charSequence);
	}

	/**
	 * 获取状态栏高度
	 * 
	 * @param context Context实例
	 * @return         状态栏高度
	 */
	public static int getStatusBarHeight(Context context) {
		Resources res = context.getResources();
		int resId = res.getIdentifier("status_bar_height", "dimen", "android");
		if (resId > 0) {
			return res.getDimensionPixelSize(resId);
		}
		return 0;
	}

	/**
	 * 获取导航栏高度（使用该方法前应该先用hasPermanentMenuKey方法判断是否是虚拟导航栏）
	 * 
	 * @param context Context实例
	 * @return        导航栏高度
	 */
	public static int getNavigationBarHeight(Context context) {
		Resources res = context.getResources();
		int resId = res.getIdentifier("navigation_bar_height", "dimen", "android");
		if (resId > 0) {
			return res.getDimensionPixelSize(resId);
		}
		return 0;
	}

	/**
	 * 从资源文件获取ColorStateList
	 * 
	 * @param res 资源实例
	 * @param resId 资源ID
	 * @return ColorStateList实例
	 */
	public static ColorStateList getColorStateList(Resources res, int resId) {
		XmlResourceParser parser = res.getXml(resId);
		ColorStateList colorStateList = null;
		try {
			colorStateList = ColorStateList.createFromXml(res, parser);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return colorStateList;
	}

	/**
	 * 检查是否超过X边界
	 * 
	 * @param bounds 边界
	 * @param x      x坐标
	 * @return
	 */
	public static int checkOutOfBoundsX(Rect bounds, int x) {
		if (x < bounds.left) {
			return OUT_OF_LEFT_BOUNDS;
		}
		else if (x > bounds.right) {
			return OUT_OF_RIGHT_BOUNDS;
		}
		else {
			return NOT_OUT_OF_BOUNDS;
		}
	}

	/**
	 * 检查是否超过Y边界
	 * 
	 * @param bounds 边界
	 * @param y      y坐标
	 * @return
	 */
	public static int checkOutOfBoundsY(Rect bounds, int y) {
		if (y < bounds.top) {
			return OUT_OF_TOP_BOUNDS;
		}
		else if (y > bounds.bottom) {
			return OUT_OF_BOTTOM_BOUNDS;
		}
		else {
			return NOT_OUT_OF_BOUNDS;
		}
	}

	/**
	 * 获取View在屏幕上的坐标(View左上角)
	 * 
	 * @param v View对象
	 * @return View左上角坐标
	 */
	public static Point getLocationOnScreen(View v) {
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		return new Point(location[0], location[1]);
	}

	/**
	 * 获取View在窗口上的坐标
	 * 
	 * @param v View对象
	 * @return View左上角坐标
	 */
	public static Point getLocationOnWindow(View v) {
		int[] location = new int[2];
		v.getLocationInWindow(location);
		return new Point(location[0], location[1]);
	}

	public static Rect getLocalVisibleRect(View v) {
		Rect rect = new Rect();
		v.getLocalVisibleRect(rect);
		return rect;
	}

	/**
	 * 禁用OverScrollMode模式
	 *
	 * @param view 待禁用该模式的View示例
	 */
	@SuppressLint("NewApi")
	public static void disableOverScrollMode(View view) {
		view.setOverScrollMode(View.OVER_SCROLL_NEVER);
	}
	
	/**
	 * 显示软键盘
	 * 
	 * @param activity
	 * @param view
	 */
	public static final void showSoftPad(Activity activity, View view)
	{
		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, 0); // 显示软键盘
	}
}