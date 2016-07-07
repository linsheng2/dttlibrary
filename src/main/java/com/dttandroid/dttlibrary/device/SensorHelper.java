package com.dttandroid.dttlibrary.device;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午12:49:41
 * @Description:
 */
public class SensorHelper {
	public static final int ORIENTATION_ROTATE_0 = 0;
	public static final int ORIENTATION_ROTATE_90 = 90;
	public static final int ORIENTATION_ROTATE_180 = 180;
	public static final int ORIENTATION_ROTATE_270 = 270;

	public static int getOrientation(float x, float y) {
		if (x >= Math.abs(y)) {
			return ORIENTATION_ROTATE_0;
		}
		else if (y >= Math.abs(x)) {
			return ORIENTATION_ROTATE_90;
		}
		else if (Math.abs(x) >= Math.abs(y)) {
			return ORIENTATION_ROTATE_180;
		}
		else if (Math.abs(y) >= Math.abs(x)){
			return ORIENTATION_ROTATE_270;
		}
		else {
			return ORIENTATION_ROTATE_0;
		}
	}
}
