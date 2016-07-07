package com.dttandroid.dttlibrary.ui;

import android.view.View;
import android.view.View.OnClickListener;

/**
 * @Author: lufengwen
 * @Date: 2015年12月1日 下午1:24:18
 * @Description:
 */
public abstract class OnQuickClickListener implements OnClickListener {
    private int mInterval;
    private int mCount;
    private int mCurrentCount;
    private long mLastClickTime;

    public OnQuickClickListener(int interval, int count) {
        mInterval = interval;
        mCount = count;
    }
    
    @Override
    public void onClick(View v) {
        long current = System.currentTimeMillis();
        if (current - mLastClickTime <= mInterval) {
            if (++mCurrentCount >= mCount) {
                onQuickClick(v);
            }
        }
        else {
            mCurrentCount = 1;
        }
        mLastClickTime = current;
    }
    
    public abstract void onQuickClick(View v);
}