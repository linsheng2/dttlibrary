package com.dttandroid.dttlibrary.gallery.manager;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.res.Resources;
import android.graphics.Bitmap;

import com.dttandroid.dttlibrary.booter.BaseApp;
import com.dttandroid.dttlibrary.device.DeviceInfo;
import com.dttandroid.dttlibrary.gallery.ImageWorker;
import com.dttandroid.dttlibrary.gallery.cache.ImageCache;
import com.dttandroid.dttlibrary.gallery.cache.MemLruCache;
import com.dttandroid.dttlibrary.utils.ImageUtil;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午9:16:57
 * @Description:
 */
public class GalleryThumbWorker extends ImageWorker<String> {
    private static final int POOL_SIZE = 5;
    private static final int BLOCKING_QUEUE_SIZE = DeviceInfo.getScreenDPI() < 320 ? 20 : 30;
    private static final int SIDE_SIZE = DeviceInfo.getScreenDPI() < 320 ? 100 : 150;
    
    public GalleryThumbWorker() {
        init();
    }
    
    @Override
    protected ImageCache<String> initCache() {
        int mb = 1024 * 1024;
        
        int memCacheSize;
        if (DeviceInfo.getScreenDPI() < 320) {
            memCacheSize = 4 * mb;
        }
        else if (DeviceInfo.getScreenDPI() == 320) {
            memCacheSize = 6 * mb;
        }
        else {
            memCacheSize = 8 * mb;
        }
        
        ImageCache.ImageCacheParams params = new ImageCache.ImageCacheParams();
        params.diskCacheEnabled = false;
        params.memCacheEnabled = true;
        params.memCacheSize = memCacheSize;
        return new ImageCache<String>(params, new MemLruCache<String>(), null);
    }

    @Override
    protected Resources initResources() {
        return BaseApp.getContext().getResources();
    }

    @Override
    protected Executor initExecutor() {
        final ThreadFactory factory = new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            public Thread newThread(Runnable r) {
                return new Thread(r, "GalleryThumbExecutor Thread #" + mCount.getAndIncrement());
            }
        };
        
        return new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE, 0L, TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>(BLOCKING_QUEUE_SIZE), factory,
                                      new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    @Override
    protected Bitmap loadBitmap(String path) {
        Bitmap bitmap = null;
        try {
            bitmap = ImageUtil.decodeSampledFile(path, SIDE_SIZE, SIDE_SIZE, true);
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
