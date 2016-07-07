package com.dttandroid.dttlibrary.gallery.cache;


/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午9:21:07
 * @Description:
 */
public class ImageCache<T> {
    private ImageCacheParams mParams;
    private AbstractMemCache<T> mMemCache;
    private AbstractDiskCache<T> mDiskCache;
    
    public ImageCache(ImageCacheParams params, AbstractMemCache<T> memCache, AbstractDiskCache<T> diskCache) {
        mParams = params;
        mMemCache = memCache;
        mDiskCache = diskCache;
        
        if (mParams.memCacheEnabled) {
            mMemCache.init(mParams.memCacheSize);
        }
        
        if (mParams.diskCacheEnabled) {
            mDiskCache.init(mParams.diskCacheDir, mParams.diskCacheSize);
        }
    }
    
    public AbstractMemCache<T> getMemCache() {
        return mMemCache;
    }
    
    public AbstractDiskCache<T> getDiskCache() {
        return mDiskCache;
    }
    
    public static class ImageCacheParams {
        public int memCacheSize;
        public boolean memCacheEnabled;
        public String diskCacheDir;
        public int diskCacheSize;
        public boolean diskCacheEnabled;
        
        public int getMaxMemory() {
            return (int) (Runtime.getRuntime().maxMemory() / 1024);
        }
    }
}

