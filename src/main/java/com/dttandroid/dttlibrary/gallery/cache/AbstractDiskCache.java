package com.dttandroid.dttlibrary.gallery.cache;

import android.graphics.Bitmap;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午9:20:03
 * @Description:
 */
public abstract class AbstractDiskCache<T>{
    public void init(String dir, int size) { }
    
    public abstract void add(T key, Bitmap bitmap);
    protected abstract Bitmap getBitmap(T key);
    
    public Bitmap get(T key) {
        return getBitmap(key);
    }
    
    public abstract boolean contains(T key);
    public abstract void remove(T key);
    public abstract void clear();
    public void flush() { }
    public void close() { } 
}

