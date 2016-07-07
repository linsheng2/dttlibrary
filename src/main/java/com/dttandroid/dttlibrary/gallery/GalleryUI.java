package com.dttandroid.dttlibrary.gallery;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.dttandroid.dttlibrary.R;
import com.dttandroid.dttlibrary.gallery.adapter.GalleryAdapter;
import com.dttandroid.dttlibrary.gallery.manager.GalleryViewerManager;
import com.dttandroid.dttlibrary.gallery.model.GalleryContainer;
import com.dttandroid.dttlibrary.gallery.model.ImageFile;
import com.dttandroid.dttlibrary.gallery.model.ImageFolder;
import com.dttandroid.dttlibrary.text.HanziToPinyinEx;
import com.dttandroid.dttlibrary.ui.BaseActivity;
import com.dttandroid.dttlibrary.ui.header.HeaderParam;
import com.dttandroid.dttlibrary.utils.StorageUtil;


/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午8:55:50
 * @Description:
 */
public class GalleryUI extends BaseActivity {

    public static final String BUNDLE_KEY = "MutipleGalleryActivity_Intent_Key";
    public static final String PATH_LIST = "MutipleGalleryActivity_Path_List";
    public static final String MAX_SELECTION_COUNT = "MutipleGalleryActivity_Max_Selection_Count";
    public static final int REQUEST_CODE = 20066;
    public static final int MAX_SELECTION_COUNT_UNLIMITED = 0;
    private List<ImageFolder> mFolders;
    private GalleryAdapter mAdapter;

    private ListView mListView;
    private int mCurrent;
    private int mMaxSelectionCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_gallery);

        if (!StorageUtil.hasSDCard()) {
            Toast.makeText(getApplicationContext(), "无SD卡", Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        mMaxSelectionCount = getIntent().getIntExtra(MAX_SELECTION_COUNT, MAX_SELECTION_COUNT_UNLIMITED);

        initViews();
        initListeners();

        mFolders = GalleryContainer.getInstance().getFolders();

        updateUI();

        new Thread(new Runnable() {
            @Override
            public void run() {
                HanziToPinyinEx.init(getContext()); // 如果初始化过，这方法不会重复初始化

                try {
                    searchImages();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }
                
                Collections.sort(mFolders);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter = new GalleryAdapter(GalleryUI.this, mFolders);
                        mListView.setAdapter(mAdapter);
                    }
                });
            }
        }).start();
    }

    private void initViews() {
        initHeader(HeaderParam.ICON, HeaderParam.TEXT, HeaderParam.TEXT);
        getHeader().getTextTitle().setText(R.string.gallery_title);
        getHeader().getRightTextButton().setText(R.string.common_complete);

        mListView = (ListView) findViewById(R.id.gallery_container);
        mListView.setDivider(new ColorDrawable(getResources().getColor(R.color.list_divider_color)));

        mListView.setDivider(null);
    }

    private void initListeners() {
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(GalleryUI.this, GalleryFolderUI.class);
                intent.putExtra(GalleryFolderUI.IMAGE_FOLDER_INDEX, position);
                intent.putExtra(MAX_SELECTION_COUNT, mMaxSelectionCount);

                mCurrent = position;
                startActivityForResult(intent, GalleryFolderUI.REQUEST_CODE);
            }
        });
    }

    @Override
    public void onHeaderRightButtonClick(View view) {
        complete();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == GalleryFolderUI.REQUEST_CODE) {
            if (data == null)
                return;
            Bundle bundle = data.getExtras();
            boolean isCompleted = bundle.getBoolean(GalleryFolderUI.RESULT_IS_COMPLETED);

            if (isCompleted) {
                complete();
                finish();
            }
        }
    }

    @Override
    public void finish() {
        clear();
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mAdapter != null) {
            updateSelectionCount(mCurrent);
            mAdapter.notifyDataSetChanged();
        }
        updateUI();
    }

    private void searchImages() {
        String[] projection = new String[] { MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID, // 直接包含该图片文件的文件夹ID，防止在不同下的文件夹重名
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // 直接包含该图片文件的文件夹名
                MediaStore.Images.Media.DISPLAY_NAME, // 图片文件名
                MediaStore.Images.Media.DATA // 图片绝对路径
        };
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = getContentResolver().query(uri, projection, "", null, MediaStore.Images.Media.DATE_MODIFIED + " DESC");
        if (cursor == null) {
            return;
        }
        if (cursor.moveToFirst()) {
            int folderIdColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
            int folderColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int fileIdColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            int fileNameColumn = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
            int pathColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

            do {
                String folderId = cursor.getString(folderIdColumn);
                String folder = cursor.getString(folderColumn);
                String fileId = cursor.getString(fileIdColumn);
                String fileName = cursor.getString(fileNameColumn);
                String path = cursor.getString(pathColumn);

                if (new File(path).exists()) {
                    addImage(folderId, folder, fileId, fileName, path);
                }
            }
            while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void addImage(String folderId, String folderName, String fileId, String fileName, String absolutePath) {
        String ext = StorageUtil.getExtension(fileName);
        if ("jpg".equals(ext) || "jpeg".equals(ext) || "png".equals(ext) || "bmp".equals(ext)) {
            ImageFolder folder = getImageFolder(folderId, folderName);

            folder.add(new ImageFile(fileId, fileName, absolutePath));
        }
    }

    private ImageFolder getImageFolder(String id, String name) {
        for (ImageFolder folder : mFolders) {
            if (folder.getId().equals(id)) {
                return folder;
            }
        }
        ImageFolder folder = new ImageFolder(id, name);
        folder.setPinyin(HanziToPinyinEx.getInstance().getPinyinString(folder.getName()));

        mFolders.add(folder);
        return mFolders.get(mFolders.size() - 1);
    }

    private void updateSelectionCount(int position) {
        ImageFolder folder = mFolders.get(position);
        List<ImageFile> files = folder.getItems();
        int i = 0;
        for (ImageFile file : files) {
            if (file.isSelected()) {
                i++;
            }
        }
        folder.setSelectionCount(i);
    }

    private void complete() {
        ArrayList<String> files = new ArrayList<String>();

        for (ImageFile file : GalleryContainer.getInstance().getSelectedImages()) {
            files.add(file.getPath());
        }

        Bundle bundle = new Bundle();
        bundle.putStringArrayList(PATH_LIST, files);

        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void clear() {
        //GalleryThumbManager.clear();
        GalleryViewerManager.clear();
        GalleryContainer.getInstance().clear();
    }

    private void updateUI() {
        int count = GalleryContainer.getInstance().getSelectedImages().size();
        getHeader().getRightTextButton().setEnabled(count == 0 ? false : true);
    }

    @Override
    protected boolean handleMessage(Message msg) {
        return true;
    }

    public static void startActivity(Activity activity, int maxSelectionCount) {
        Intent intent = new Intent(activity, GalleryUI.class);
        intent.putExtra(GalleryUI.MAX_SELECTION_COUNT, maxSelectionCount);
        activity.startActivityForResult(intent, GalleryUI.REQUEST_CODE);
    }
    
	public static void startActivity(Fragment fragment, int maxSelectionCount) {
        Intent intent = new Intent(fragment.getActivity(), GalleryUI.class);
        intent.putExtra(GalleryUI.MAX_SELECTION_COUNT, maxSelectionCount);
        fragment.startActivityForResult(intent, GalleryUI.REQUEST_CODE);
    }
}
