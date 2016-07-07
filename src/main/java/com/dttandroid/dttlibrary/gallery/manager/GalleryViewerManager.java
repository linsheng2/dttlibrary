package com.dttandroid.dttlibrary.gallery.manager;

import android.widget.ImageView;

import com.dttandroid.dttlibrary.graphics.ImageOptions;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午8:54:24
 * @Description:
 */
public class GalleryViewerManager {
    private static GalleryViewerWorker sWorker;
    private static ImageOptions sOpts;
    
    public static void clear() {
        getInstance().getCache().getMemCache().clear();
        getInstance().shutdownNow();
        sWorker = null;
        sOpts = null;
    }
    
    private static GalleryViewerWorker getInstance() {
        if (sWorker == null) {
            synchronized (GalleryViewerWorker.class) {
                if (sWorker == null) {
                    sWorker = new GalleryViewerWorker();
                    
                    ImageOptions.Builder builder = new ImageOptions.Builder();
                    builder.isIgnoreImageView(true);
                    sOpts = builder.build();
                }
            }
        }
        return sWorker;
    }
    
    public static void get(final ImageView imageView, String path) {
        getInstance().loadImage(path, imageView, sOpts);
    }
}
