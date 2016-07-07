package com.dttandroid.dttlibrary.ui;

import android.view.View;
import android.view.View.OnClickListener;

/**
 * @author: DuanLingYun
 * @date: 2013年12月26日 下午4:25:46
 * @description:
 */

public abstract class OnSingleClickListener implements OnClickListener {
	private long mLastClickTime = 0;
	private int mInterval;

	/**
	 * 使用默认2秒间隔时间
	 */
	public OnSingleClickListener() {
		this(2000);
	}

	/**
	 * @param interval
	 *            点击间隔时间
	 */
	public OnSingleClickListener(int interval) {
		mInterval = interval;
	}

	@Override
	public final void onClick(View v) {
		long current = System.currentTimeMillis();
		if (current - mLastClickTime > mInterval) {
			mLastClickTime = current;
			onSingleClick(v);
		}
	}

	public abstract void onSingleClick(View v);
}
