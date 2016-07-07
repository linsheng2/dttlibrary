package com.dttandroid.dttlibrary.gallery.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午8:54:58
 * @Description:
 */
public class GalleryContainer {
    private static GalleryContainer sInstance;
    private List<ImageFolder> mFolders;
    private List<ImageFile> mSelctedImages;

    private GalleryContainer() {
        mFolders = new ArrayList<ImageFolder>();
        mSelctedImages = new ArrayList<ImageFile>();
    }

    public static GalleryContainer getInstance() {
        if (sInstance == null) {
            sInstance = new GalleryContainer();
        }
        return sInstance;
    }

    public List<ImageFolder> getFolders() {
        return mFolders;
    }

    public List<ImageFile> getSelectedImages() {
        return mSelctedImages;
    }

    public ImageFolder getFolder(int index) {
        if (mFolders != null && mFolders.size() > 0 && index >= 0 && index < mFolders.size()) {
            return mFolders.get(index);
        }
        else {
            return null;
        }
    }

    public void clear() {
        sInstance = null;
    }
}
