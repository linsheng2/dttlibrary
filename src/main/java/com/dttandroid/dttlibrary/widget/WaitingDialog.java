package com.dttandroid.dttlibrary.widget;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.dttandroid.dttlibrary.R;

public class WaitingDialog extends Dialog {
    private Builder mBuilder;
    private TextView mTextView;
    
    public WaitingDialog(Context context) {
        super(context, R.style.WaitingDialogStyle);
        setContentView(R.layout.custom_waiting_dialog);
        
        mTextView = (TextView) findViewById(R.id.waiting_dialog_message);
    }

    private void setBuidler(Builder builder) {
        mBuilder = builder;
    }
    
    @Override
    public void show() {
        if(mBuilder != null) {
            mTextView.setText(mBuilder._mMessage);
            setCancelable(mBuilder._mCancelable);
            setCanceledOnTouchOutside(mBuilder._mCanceledOnTouchOutside);
        }
        super.show();
    }

    
    public static class Builder {
        private Context _mContext;
        private CharSequence _mMessage;
        private boolean _mCancelable;
        private boolean _mCanceledOnTouchOutside;
        
        public Builder(Context context) {
            _mContext = context;
        }
        
        public Builder setMessage(CharSequence message) {
            _mMessage = message;
            return this;
        }
        
        public Builder setCancelable(boolean cancelable)
        {
            _mCancelable = cancelable;
            return this;
        }
        
        public Builder setCanceledOnTouchOutside(boolean cancelable)
        {
            _mCanceledOnTouchOutside = cancelable;
            return this;
        }
        
        public WaitingDialog create() {
            WaitingDialog dialog = new WaitingDialog(_mContext);
            dialog.setBuidler(this);
            return dialog;
        }
    }
    
    public interface OnTimeoutListener {
        /**
         * 此回调是在UI线程执行
         */
        void onTimeout();
    }
}