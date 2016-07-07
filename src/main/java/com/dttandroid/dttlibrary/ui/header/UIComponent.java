package com.dttandroid.dttlibrary.ui.header;

import android.os.Handler;

import com.dttandroid.dttlibrary.widget.WaitingDialog;

/**
 * @Author: lufengwen
 * @Date: 2015年6月15日 下午8:01:47
 * @Description: 界面常用的方法接口
 */
public interface UIComponent {
    public Handler getHandler();

    public void showToast(int resId);
    public void showToast(CharSequence text);

    public void showWaitingDialog(int resId);
    public void showWaitingDialogWithoutTimeout(int resId);
    public void showWaitingDialogWithoutTimeout(String message);
    public void showWaitingDialog(String message);
    public void showWaitingDialog(int resId, int timeout);
    public void showWaitingDialog(String message, int timeout);
    public void showWaitingDialog(int resId, int timeout, WaitingDialog.OnTimeoutListener listener);
    public void showWaitingDialog(String message, int timeout, WaitingDialog.OnTimeoutListener listener);
    public void showWaitingDialog(WaitingDialog dialog, int timeout, WaitingDialog.OnTimeoutListener listener);
    public void dismissWaitingDialog();
    public boolean showNetworkUnavailableIfNeed();
}