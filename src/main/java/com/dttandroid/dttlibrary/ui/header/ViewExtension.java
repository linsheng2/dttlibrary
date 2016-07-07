package com.dttandroid.dttlibrary.ui.header;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

/**
 * @Author: lufengwen
 * @Date: 2015年6月15日 下午7:31:59
 * @Description: View扩展类，主要对View设置图片、背景图片时容易引起的奔溃问题进行捕获
 */
public class ViewExtension {

	public static void setBackgroundResource(View v, int resId) {
		try {
			v.setBackgroundResource(resId);
		}
		catch (OutOfMemoryError err) {
			err.printStackTrace();
		}
	}

	public static void setBackgroundDrawable(View v, Drawable background) {
		try {
			v.setBackgroundDrawable(background);
		}
		catch (OutOfMemoryError err) {
			err.printStackTrace();
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void setBackground(View v, Drawable background) {
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				v.setBackground(background);
			}
			else {
				v.setBackgroundDrawable(background);
			}
		}
		catch (OutOfMemoryError err) {
			err.printStackTrace();
		}
	}

	public static void setImageResource(ImageView imageView, int resId) {
		try {
			imageView.setImageResource(resId);
		}
		catch (OutOfMemoryError err) {
			err.printStackTrace();
		}
	}

	public static void setImageDrawable(ImageView imageView, Drawable dr) {
		try {
			imageView.setImageDrawable(dr);
		}
		catch (OutOfMemoryError err) {
			err.printStackTrace();
		}
	}

	public static void setImageBitmap(ImageView imageView, Bitmap bitmap) {
		try {
			imageView.setImageBitmap(bitmap);
		}
		catch (OutOfMemoryError err) {
			err.printStackTrace();
		}
	}
}