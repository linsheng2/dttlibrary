package com.dttandroid.dttlibrary.ui.header;

import android.view.View;

/**
 * @Author: lufengwen
 * @Date: 2015年6月15日 下午7:02:10
 * @Description: Header点击监听器
 */
public interface HeaderListener {
    public void onHeaderLeftButtonClick(View v);
    public void onHeaderRightButtonClick(View v);
    public void onHeaderRightButton2Click(View v);
    public void onHeaderTitleClick(View v);
    public void onHeaderTabClick(int index);
}
