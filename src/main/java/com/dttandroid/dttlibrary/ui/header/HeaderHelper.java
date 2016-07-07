package com.dttandroid.dttlibrary.ui.header;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.dttandroid.dttlibrary.utils.ViewHelper;

/**
 * @Author: lufengwen
 * @Date: 2015年6月15日 下午7:29:52
 * @Description: 界面Header辅助类
 */
public class HeaderHelper {
	public static void initHeader(HeaderHolder header, HeaderParam left, HeaderParam title, HeaderParam right , HeaderParam right2) {
		initButton(header.getLeftTextButton(), header.getLeftIconButton(), left);
		initButton(header.getRightTextButton(), header.getRightIconButton(), right);
		header.getRightIconButton2().setVisibility(right2 == HeaderParam.ICON ? View.VISIBLE : View.GONE);

		header.getTabContainer().setVisibility(title == HeaderParam.TAB ? View.VISIBLE : View.GONE);
		header.getTextTitle().setVisibility(title == HeaderParam.TEXT ? View.VISIBLE : View.GONE);
		header.getIconTitle().setVisibility(title == HeaderParam.ICON ? View.VISIBLE : View.GONE);
	}

	private static void initButton(Button textButton, ImageButton iconButton, HeaderParam param) {
		textButton.setVisibility(param == HeaderParam.TEXT ? View.VISIBLE : View.GONE);
		iconButton.setVisibility(param == HeaderParam.ICON ? View.VISIBLE : View.GONE);
	}

	public static void initTabs(Context context, final HeaderListener listener, ViewGroup tabContainer, String[] tabs, int[] tabBackgrounds, ColorStateList textColor) {
		tabContainer.removeAllViews();
		for (int i = 0; i < tabs.length; i++) {
			TextView textView = new TextView(context);
			textView.setLayoutParams(new LayoutParams(ViewHelper.dp2px(context, 68), LayoutParams.WRAP_CONTENT));
			textView.setGravity(Gravity.CENTER);
			textView.setTextColor(textColor);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
			textView.setText(tabs[i]);
			textView.setId(i);
			textView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					listener.onHeaderTabClick(v.getId());
				}
			});
			tabContainer.addView(textView);
		}

		int childCount = tabContainer.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View view = tabContainer.getChildAt(i);
			if (i == 0) {
				ViewExtension.setBackgroundResource(view, tabBackgrounds[0]);
			}
			else if (i == childCount - 1) {
				ViewExtension.setBackgroundResource(view, tabBackgrounds[2]);
			}
			else {
				ViewExtension.setBackgroundResource(view, tabBackgrounds[1]);
			}
		}
	}
}
