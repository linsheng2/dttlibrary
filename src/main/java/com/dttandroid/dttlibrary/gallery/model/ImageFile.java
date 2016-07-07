package com.dttandroid.dttlibrary.gallery.model;

import java.io.Serializable;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午8:55:07
 * @Description:
 */
public class ImageFile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String mId;
    private String mName;
    private String mPath;
    private boolean mIsSelected;

    public ImageFile(String id, String name, String path) {
        mId = id;
        mName = name;
        mPath = path;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getPath() {
        return mPath;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean value) {
        mIsSelected = value;
    }
}
