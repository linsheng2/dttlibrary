package com.dttandroid.dttlibrary.gallery;

import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.Toast;

import com.dttandroid.dttlibrary.R;
import com.dttandroid.dttlibrary.debug.AppLogger;
import com.dttandroid.dttlibrary.gallery.adapter.GalleryViewerAdapter;
import com.dttandroid.dttlibrary.gallery.manager.GalleryViewerManager;
import com.dttandroid.dttlibrary.gallery.model.GalleryContainer;
import com.dttandroid.dttlibrary.gallery.model.ImageFile;
import com.dttandroid.dttlibrary.ui.BaseActivity;
import com.dttandroid.dttlibrary.ui.header.HeaderParam;
import com.dttandroid.dttlibrary.widget.GalleryViewer;


/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午9:40:01
 * @Description:
 */
public class GalleryViewerUI extends BaseActivity implements OnClickListener {

    public final static String MAX_SELECTED_COUNT = "MaxSelectedCount";
    public final static String FOLDER_INDEX = "FolderIndex";
    public final static String IMAGE_INDEX = "ImageIndex";
    public final static String RESULT_IS_COMPLETED = "MutipleChoiceGalleryViewerIsCompleted";
    public final static int REQUEST_CODE = 21001;

    private List<ImageFile> mFiles;
    private List<ImageFile> mSelectedFiles;
    private int mMaxSelectionCount;
    private int mImageIndex;
    private boolean mIsHideTopBar;

    private CheckBox mCheckBox;
    private GalleryViewer mGalleryViewer;
    private GalleryViewerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_gallery_viewer);

        initViews();
        initListeners();
        initData();

        updateUI();
    }

    private void initViews() {
        initHeader(HeaderParam.ICON, HeaderParam.TEXT, HeaderParam.TEXT);
        getHeader().getHeaderLayout().setBackgroundResource(R.drawable.gallery_viewer_title_bg);
        getHeader().getLeftIconButton().setImageResource(R.drawable.common_back_selector);
        getHeader().getRightTextButton().setText(R.string.common_complete);
        getHeader().getRightTextButton().setTextColor(getResources().getColorStateList(R.color.gallery_viewer_ok_text_color));
        getHeader().getTextTitle().setTextColor(Color.WHITE);

        mCheckBox = (CheckBox) findViewById(R.id.gallery_viewer_checkbox);
        mGalleryViewer = (GalleryViewer) findViewById(R.id.gallery_viewer_self);
    }

    private void initData() {
        int folderIndex = getIntent().getIntExtra(FOLDER_INDEX, -1);
        mImageIndex = getIntent().getIntExtra(IMAGE_INDEX, -1);
        mMaxSelectionCount = getIntent().getIntExtra(GalleryUI.MAX_SELECTION_COUNT, GalleryUI.MAX_SELECTION_COUNT_UNLIMITED);
        if (folderIndex <= -1 || mImageIndex <= -1) {
            finish();
        }
        mFiles = GalleryContainer.getInstance().getFolder(folderIndex).getItems();
        mSelectedFiles = GalleryContainer.getInstance().getSelectedImages();

        mAdapter = new GalleryViewerAdapter(this, mFiles);
        mGalleryViewer.setAdapter(mAdapter);
        mGalleryViewer.setSelection(mImageIndex);
    }

    private void initListeners() {
        mCheckBox.setOnClickListener(this);

        mGalleryViewer.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mImageIndex = position;
                updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mGalleryViewer.setOnSingleTapConfirmedListener(new GalleryViewer.OnSingleTapConfirmedListener() {
            @Override
            public void onTapped() {
                toggleTopBar();
            }
        });
    }

    @Override
    public void onHeaderLeftButtonClick(View view) {
        complete(false);
        finish();
    }

    @Override
    public void onHeaderRightButtonClick(View view) {
        complete(true);
        finish();
    }

    @Override
    public void onClick(View v) {
    	if (v.getId() == R.id.gallery_viewer_checkbox) {
    		setCheckBox();
    	}
    }

    private void setCheckBox() {
        ImageFile current = (ImageFile) mAdapter.getItem(mImageIndex);
        if (current.isSelected()) {
            current.setSelected(false);
            mSelectedFiles.remove(current);
            updateUI();
        }
        else {
            if (mSelectedFiles.size() < mMaxSelectionCount) {
                current.setSelected(true);
                mSelectedFiles.add(current);
                updateUI();
            }
            else {
                if (mMaxSelectionCount == 1) {
                    for (ImageFile file : mFiles) {
                        file.setSelected(false);
                    }
                    mSelectedFiles.clear();
                    
                    current.setSelected(true);
                    mSelectedFiles.add(current);
                    updateUI();
                    mAdapter.notifyDataSetChanged();
                    AppLogger.d("lufengwen", "notifyDataSetChanged", false);
                }
                else {
                    String msg = getString(R.string.gallery_select_tips).replace("{0}", String.valueOf(mMaxSelectionCount));
                    Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
                    mCheckBox.setChecked(false);
                }
            }
        }
    }

    private void toggleTopBar() {
        mIsHideTopBar = !mIsHideTopBar;
        getHeader().getHeaderLayout().setVisibility(mIsHideTopBar ? View.GONE : View.VISIBLE);
    }

    private void complete(boolean isCompleted) {
        GalleryViewerManager.clear();
        setResult(RESULT_OK, new Intent().putExtra(RESULT_IS_COMPLETED, isCompleted));
    }

    private void updateUI() {
        // int count = m_selectedFiles.size();
        // String maxCount = m_iMaxSelectionCount ==
        // PPMutipleChoiceGalleryUI.MAX_SELECTION_COUNT_UNLIMITED ?
        // "不限" : String.valueOf(m_iMaxSelectionCount);
        // m_title.setText(getString(R.string.multiple_choice_gallery_selected)
        // + count + "/" + maxCount);
        getHeader().getTextTitle().setText((mImageIndex + 1) + "/" + mFiles.size());

        ImageFile current = (ImageFile) mAdapter.getItem(mImageIndex);
        mCheckBox.setChecked(current.isSelected());

        getHeader().getRightTextButton().setEnabled(mSelectedFiles.size() > 0 ? true : false);
    }

    @Override
    protected boolean handleMessage(Message msg) {
        return true;
    }
}
