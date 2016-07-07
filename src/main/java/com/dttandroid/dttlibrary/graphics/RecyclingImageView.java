package com.dttandroid.dttlibrary.graphics;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午8:57:07
 * @Description: 增强的ImageView，添加对图片回收功能
 */
public class RecyclingImageView extends ImageView {

    public RecyclingImageView(Context context) {
        super(context);
    }

    public RecyclingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        setImageDrawable(null);
        setBackgroundDrawable(null);
        super.onDetachedFromWindow();
    }
}