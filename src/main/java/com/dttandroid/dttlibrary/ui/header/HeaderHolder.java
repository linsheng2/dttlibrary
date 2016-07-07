package com.dttandroid.dttlibrary.ui.header;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dttandroid.dttlibrary.R;

/**
 * @Author: lufengwen
 * @Date: 2015年9月24日 下午4:44:50
 * @Description:
 */
public class HeaderHolder {
	private ViewGroup mHeaderLayout;
	private Button mLeftTextButton;
	private Button mRightTextButton;
	private ImageButton mLeftIconButton;
	private ImageButton mRightIconButton;
	private ImageButton mRightIconButton2;
	private TextView mTextTitle;
	private ImageButton mIconTitle;
	private ViewGroup mTabContainer;

	public HeaderHolder(ViewGroup layout, final HeaderListener headerListener) {
		mHeaderLayout = layout;
		mLeftTextButton = (Button) mHeaderLayout.findViewById(R.id.common_header_left_text_btn);
		mRightTextButton = (Button) mHeaderLayout.findViewById(R.id.common_header_right_text_btn);
		mLeftIconButton = (ImageButton) mHeaderLayout.findViewById(R.id.common_header_left_icon_btn);
		mRightIconButton = (ImageButton) mHeaderLayout.findViewById(R.id.common_header_right_icon_btn);
		mRightIconButton2 = (ImageButton) mHeaderLayout.findViewById(R.id.common_header_right_icon_btn_2);
		mTextTitle = (TextView) mHeaderLayout.findViewById(R.id.common_header_text_title);
		mIconTitle = (ImageButton) mHeaderLayout.findViewById(R.id.common_header_icon_title);
		mTabContainer = (ViewGroup) mHeaderLayout.findViewById(R.id.common_header_tab_container);

		mLeftTextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				headerListener.onHeaderLeftButtonClick(v);
			}
		});

		mLeftIconButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				headerListener.onHeaderLeftButtonClick(v);
			}
		});

		mRightTextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				headerListener.onHeaderRightButtonClick(v);
			}
		});

		mRightIconButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				headerListener.onHeaderRightButtonClick(v);
			}
		});
		
		mRightIconButton2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				headerListener.onHeaderRightButton2Click(v);
			}
		});

		mTextTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				headerListener.onHeaderTitleClick(v);
			}
		});

		mIconTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				headerListener.onHeaderTitleClick(v);
			}
		});
	}

	public void setCurrentTab(int index) {
		for (int i = 0; i < mTabContainer.getChildCount(); i++) {
			mTabContainer.getChildAt(i).setSelected(index == i);
		}
	}

	public ViewGroup getHeaderLayout() {
		return mHeaderLayout;
	}

	public Button getLeftTextButton() {
		return mLeftTextButton;
	}

	public Button getRightTextButton() {
		return mRightTextButton;
	}

	public ImageButton getLeftIconButton() {
		return mLeftIconButton;
	}

	public ImageButton getRightIconButton() {
		return mRightIconButton;
	}
	
	public ImageButton getRightIconButton2() {
		return mRightIconButton2;
	}

	public TextView getTextTitle() {
		return mTextTitle;
	}

	public ImageButton getIconTitle() {
		return mIconTitle;
	}

	public ViewGroup getTabContainer() {
		return mTabContainer;
	}
}
