package com.dttandroid.dttlibrary.gallery.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午8:55:17
 * @Description:
 */
public class ImageFolder implements Serializable, Comparable<ImageFolder> {
    private static final long serialVersionUID = 1L;

    private List<ImageFile> mFiles;
    private int mSelectedCount;

    private String mName;
    private String mPinyin;
    private String mId;

    public ImageFolder(String id, String name) {
        mId = id;
        mName = name;
        mFiles = new ArrayList<ImageFile>();
    }

    public boolean add(ImageFile file) {
        if (file == null) {
            return false;
        }

        return mFiles.add(file);
    }

    public void clear() {
        mFiles.clear();
    }

    public int size() {
        return mFiles.size();
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getPinyin() {
        return mPinyin;
    }

    public void setPinyin(String pinyin) {
        mPinyin = pinyin;
    }

    public void setSelectionCount(int count) {
        mSelectedCount = count;
    }

    public int getSelectionCount() {
        return mSelectedCount;
    }

    public List<ImageFile> getItems() {
        return mFiles;
    }

    @Override
    public int compareTo(ImageFolder another) {
        return getPinyin().compareTo(another.getPinyin());
    }
}
