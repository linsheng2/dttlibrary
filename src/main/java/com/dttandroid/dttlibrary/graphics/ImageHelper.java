package com.dttandroid.dttlibrary.graphics;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import android.view.Window;

import com.dttandroid.dttlibrary.utils.ImageUtil;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午9:15:43
 * @Description:
 */
public class ImageHelper {

	public static Bitmap roundedIfNeeded(Resources resources, Bitmap bitmap, ImageOptions opts) {
		if (opts != null && opts.isRounded() && bitmap != null) {
			if (opts.getRoundedType() == ImageOptions.RoundedType.Full) {
				bitmap = ImageUtil.getRoundedBitmap(bitmap, opts.getRoundedBorderWidth(), opts.getRoundedBorderColor());
			}
			else if (opts.getRoundedType() == ImageOptions.RoundedType.Corner) {
				bitmap = ImageUtil.getRoundedCornerBitmap(bitmap, opts.getRoundedRadius(), opts.getRoundedBorderWidth(), opts.getRoundedBorderColor());
			}
		}
		return bitmap;
	}

	public static Bitmap toGrayscaleIfNeeded(Resources resources, Bitmap bitmap, ImageOptions opts) {
		if (opts != null && opts.isGrayscale() && bitmap != null) {
			bitmap = ImageUtil.toGrayscale(bitmap);
		}
		return bitmap;
	}

	public static Bitmap convertViewToBitmap(View view, int bitmapWidth, int bitmapHeight) {
		Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
		view.draw(new Canvas(bitmap));
		return bitmap;
	}

	public static Bitmap getScreenBitmap(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache(true);
		Bitmap bitmap = view.getDrawingCache(); // 获取当前窗口快照，相当于截屏
		int height = getOtherHeight(activity); // 除去状态栏和标题栏
		return Bitmap.createBitmap(bitmap, 0, height, bitmap.getWidth(), bitmap.getHeight() - height);
	}

	/**
	 * 获取系统状态栏和软件标题栏，部分软件没有标题栏，看自己软件的配置；
	 * @return
	 */
	private static int getOtherHeight(Activity activity) {
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		int contentTop = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
		int titleBarHeight = contentTop - statusBarHeight;
		return statusBarHeight + titleBarHeight;
	}
}
