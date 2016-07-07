package com.dttandroid.dttlibrary.setting;

import android.hardware.Camera;

import com.dttandroid.dttlibrary.booter.BaseApp;

/**
 * @Author: lufengwen
 * @Date: 2015年6月15日 下午4:31:54
 * @Description: 保存库的配置数据
 */
public class LibSettings {
	private static final String FILE_NAME = "system_settings";
	private static SettingsObject sSettings = new SettingsObject(BaseApp.getContext(), FILE_NAME);

	/**相机闪光灯模式 */
	private static final String CAMERA_FLASH_MODE = "camera_flash_mode";
	private final static String LAST_VERSION_CODE = "last_version_code";

	/**
	 * 设置相机闪光灯模式
	 * @param mode 闪光灯模式
	 */
	public static void setCameraFlashMode(String mode) {
		sSettings.setString(CAMERA_FLASH_MODE, mode);
	}

	/**
	 * 获取相机闪光灯模式
	 */
	public static String getCameraFlashMode() {
		return sSettings.getString(CAMERA_FLASH_MODE, Camera.Parameters.FLASH_MODE_AUTO);
	}

	public static void setLastVersionCode(int versionCode) {
		sSettings.setInt(LAST_VERSION_CODE, versionCode);
	}

	public static int getLastVersionCode() {
		return sSettings.getInt(LAST_VERSION_CODE, 0);
	}
}