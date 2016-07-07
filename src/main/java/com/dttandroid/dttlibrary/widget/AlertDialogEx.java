package com.dttandroid.dttlibrary.widget;

import android.app.AlertDialog;
import android.content.Context;

import com.dttandroid.dttlibrary.utils.ViewCompat;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午1:15:01
 * @Description: AlertDialog的扩展
 */
public class AlertDialogEx extends AlertDialog {
    protected AlertDialogEx(Context context) {
        super(ViewCompat.getLightContextThemeWrapper(context));
    }

    /**
     * @deprecated 该方法废弃，设置主题有bug
     * 
     * @param context
     * @param theme
     */
    protected AlertDialogEx(Context context, int theme) {
        super(context, theme);
    }

    protected AlertDialogEx(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(ViewCompat.getLightContextThemeWrapper(context), cancelable, cancelListener);

    }

    public static class Builder extends AlertDialog.Builder {

        public Builder(Context context) {
            super(ViewCompat.getLightContextThemeWrapper(context));
        }

        @Override
        public AlertDialog create() {
            AlertDialog dialog = super.create();
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }
    }
}