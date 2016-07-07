package com.dttandroid.dttlibrary.setting;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @Author: lufengwen
 * @Date: 2015年6月15日 下午4:06:09
 * @Description: 保存配置数据对象实体
 */
public class SettingsObject {
	private SharedPreferences.Editor mEditor;
	private SharedPreferences mPreferences;
	private Context mContext;
	private String mName;

	public SettingsObject(Context context, String name) {
		mName = name;
		mContext = context;
	}

	private SharedPreferences getSharedPreferences() {
		if (mPreferences == null) {
			mPreferences = mContext.getSharedPreferences(mName, Context.MODE_PRIVATE);
		}
		return mPreferences;
	}

	private SharedPreferences.Editor getEditor() {
		if (mEditor == null) {
			mEditor = getSharedPreferences().edit();
		}
		return mEditor;
	}

	public int getInt(String key, int defValue) {
		return getSharedPreferences().getInt(key, defValue);
	}

	public long getLong(String key, long defValue) {
		return getSharedPreferences().getLong(key, defValue);
	}

	public boolean getBoolean(String key, boolean defValue) {
		return getSharedPreferences().getBoolean(key, defValue);
	}

	public String getString(String key, String defValue) {
		return getSharedPreferences().getString(key, defValue);
	}

	public void setInt(String key, int value) {
		getEditor().putInt(key, value).commit();
	}

	public void setLong(String key, long value) {
		getEditor().putLong(key, value).commit();
	}

	public void setBoolean(String key, boolean value) {
		getEditor().putBoolean(key, value).commit();
	}

	public void setString(String key, String value) {
		getEditor().putString(key, value).commit();
	}
}