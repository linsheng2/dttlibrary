package com.dttandroid.dttlibrary.gallery.cache;

import android.graphics.Bitmap;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午9:20:21
 * @Description:
 */
public abstract class AbstractMemCache<T>{
    public abstract void init(int size);
    public abstract void add(T key, Bitmap bitmap);
    public abstract Bitmap get(T key);
    public abstract void remove(T key);
    public abstract void clear();
    public abstract int maxSize();
    public abstract int size();
    public abstract int keys();
    public abstract int evictions();
    public abstract int hits();
}

