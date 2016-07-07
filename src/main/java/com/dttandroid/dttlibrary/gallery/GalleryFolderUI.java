package com.dttandroid.dttlibrary.gallery;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.GridView;

import com.dttandroid.dttlibrary.R;
import com.dttandroid.dttlibrary.gallery.adapter.GalleryFolderAdapter;
import com.dttandroid.dttlibrary.gallery.model.GalleryContainer;
import com.dttandroid.dttlibrary.gallery.model.ImageFile;
import com.dttandroid.dttlibrary.gallery.model.ImageFolder;
import com.dttandroid.dttlibrary.ui.BaseActivity;
import com.dttandroid.dttlibrary.ui.header.HeaderParam;
import com.dttandroid.dttlibrary.widget.AlertDialogEx;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午8:55:39
 * @Description:
 */
public class GalleryFolderUI extends BaseActivity {

    public static final String IMAGE_FOLDER_INDEX = "ImageFolderIndex";
    public static final String RESULT = "MultipleGalleryFolderActivity_Result_Continue";
    public static final String RESULT_IS_COMPLETED = "MultipleGalleryFolderActivity_Result_Type";
    public static final int REQUEST_CODE = 22003;

    private List<ImageFile> mFiles;
    private List<ImageFile> mSelectedFiles;
    private GalleryFolderAdapter mFolderAdapter;

    private GridView mGridView;

    private int mMaxSelectionCount;
    private int mFolderIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_gallery_folder);
        initViews();
        initListeners();
        mFolderIndex = getIntent().getIntExtra(IMAGE_FOLDER_INDEX, -1);
        if (mFolderIndex == -1) {
            finish();
            return;
        }

        mMaxSelectionCount = getIntent().getIntExtra(GalleryUI.MAX_SELECTION_COUNT, GalleryUI.MAX_SELECTION_COUNT_UNLIMITED);

        mSelectedFiles = GalleryContainer.getInstance().getSelectedImages();
        ImageFolder folder = GalleryContainer.getInstance().getFolder(mFolderIndex);

        if (folder == null) {
            finish();
            return;
        }
        mFiles = folder.getItems();

        updateUI();
        initAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFolderAdapter != null) {
            mFolderAdapter.notifyDataSetChanged();
        }

        updateUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == GalleryViewerUI.REQUEST_CODE) {
            Bundle bundle = data.getExtras();
            boolean isCompleted = bundle.getBoolean(GalleryViewerUI.RESULT_IS_COMPLETED);
            if (isCompleted) {
                complete(true);
                finish();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initViews() {
        initHeader(HeaderParam.ICON, HeaderParam.TEXT, HeaderParam.TEXT);
        getHeader().getRightTextButton().setText(R.string.common_complete);

        mGridView = (GridView) findViewById(R.id.gallery_folder_container);
    }

    private void initListeners() {
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(GalleryFolderUI.this, GalleryViewerUI.class);
                intent.putExtra(GalleryUI.MAX_SELECTION_COUNT, mMaxSelectionCount);
                intent.putExtra(GalleryViewerUI.FOLDER_INDEX, mFolderIndex);
                intent.putExtra(GalleryViewerUI.IMAGE_INDEX, position);
                startActivityForResult(intent, GalleryViewerUI.REQUEST_CODE);
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

    private void initAdapter() {
        mFolderAdapter = new GalleryFolderAdapter(GalleryFolderUI.this, mFiles);
        mFolderAdapter.setOnCheckBoxClickedListner(new GalleryFolderAdapter.OnCheckBoxClickedListener() {
            
            @Override
            public void onClicked(CheckBox checkBox, ImageFile imageFile) {
                if (!imageFile.isSelected()) {
                    if (mSelectedFiles.size() < mMaxSelectionCount) {
                        imageFile.setSelected(true);
                        mSelectedFiles.add(imageFile);
                        updateUI();
                    }
                    else {
                        if (mMaxSelectionCount == 1) {
                            for (ImageFile file : mFiles) {
                                file.setSelected(false);
                            }
                            mSelectedFiles.clear();
                            
                            imageFile.setSelected(true);
                            mSelectedFiles.add(imageFile);
                            updateUI();
                            mFolderAdapter.notifyDataSetChanged();
                        }
                        else {
                            String msg = getString(R.string.gallery_select_tips).replace("{0}", String.valueOf(mMaxSelectionCount));
                            checkBox.setChecked(false);
                            AlertDialogEx.Builder builder = new AlertDialogEx.Builder(GalleryFolderUI.this);
                            builder.setTitle(R.string.common_prompt);
                            builder.setMessage(msg);
                            builder.setPositiveButton(R.string.common_i_known, null);
                            builder.create().show();
                            // Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else {
                    imageFile.setSelected(false);
                    mSelectedFiles.remove(imageFile);
                    updateUI();
                }
            }
        });
        mGridView.setAdapter(mFolderAdapter);
    }

    private void complete(boolean isCompleted) {
        setResult(RESULT_OK, new Intent().putExtra(RESULT_IS_COMPLETED, isCompleted));
    }

    private void updateUI() {
        if (mSelectedFiles == null) {
            return;
        }
        int count = mSelectedFiles.size();
        getHeader().getRightTextButton().setEnabled(count == 0 ? false : true);
        getHeader().getTextTitle().setText(getString(R.string.gallery_selected) + count + "/" + mMaxSelectionCount);
    }

    @Override
    protected boolean handleMessage(Message msg) {
        return true;
    }

}
