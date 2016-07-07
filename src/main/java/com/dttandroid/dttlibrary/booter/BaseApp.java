package com.dttandroid.dttlibrary.booter;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.dttandroid.dttlibrary.device.DeviceInfo;
import com.dttandroid.dttlibrary.thread.Dispatcher;
import com.dttandroid.dttlibrary.ui.BaseActivity;

/**
 * @Author: lufengwen
 * @Date: 2015年8月18日 下午8:04:28
 * @Description:
 */
public class BaseApp extends Application {

	private static Context sContext;
	private static WeakReference<BaseActivity> sCurrentActivity;

	@Override
	public void onCreate() {
		super.onCreate();
		sContext = this;
		Dispatcher.init(Thread.currentThread());
		DeviceInfo.init(this);
	}

	public static Context getContext() {
		return sContext;
	}

	public static void setCurrentActivity(BaseActivity activity) {
		if (activity == null) {
			if (sCurrentActivity != null) {
				sCurrentActivity.clear();
				sCurrentActivity = null;
			}
		}
		else {
			if (sCurrentActivity != null) {
				Activity act = sCurrentActivity.get();
				if (act != null && act.hashCode() == activity.hashCode()) {
					return;
				}
			}
			sCurrentActivity = new WeakReference<BaseActivity>(activity);
		}
	}

	public static BaseActivity getCurrentActivity() {
		return sCurrentActivity != null ? sCurrentActivity.get() : null;
	}

	public static void showToast(int resId) {
		showToast(getContext().getString(resId));
	}

	public static void showToast(final CharSequence text) {
		Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
	}

}
