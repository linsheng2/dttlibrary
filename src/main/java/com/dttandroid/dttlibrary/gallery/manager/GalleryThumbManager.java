package com.dttandroid.dttlibrary.gallery.manager;

import com.dttandroid.dttlibrary.gallery.model.ImageFile;
import com.dttandroid.dttlibrary.gallery.model.ImageFolder;
import com.dttandroid.dttlibrary.graphics.ImageOptions;
import com.dttandroid.dttlibrary.graphics.RecyclingImageView;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午8:54:14
 * @Description:
 */
public class GalleryThumbManager {
    private static GalleryThumbWorker sWorker;
    private static ImageOptions sOpts;
    
    public static void clear() {
        getInstance().getCache().getMemCache().clear();
        getInstance().shutdownNow();
        sWorker = null;
        sOpts = null;
    }
    
    private static GalleryThumbWorker getInstance() {
        if (sWorker == null) {
            synchronized (GalleryThumbWorker.class) {
                if (sWorker == null) {
                    sWorker = new GalleryThumbWorker();
                    sOpts = new ImageOptions.Builder().build();
                }
            }
        }
        return sWorker;
    }

    public static void getFolderThumbnail(final RecyclingImageView imageView, final ImageFolder folder) {
        getThumbnail(imageView, folder.getItems().get(0).getPath());
    }

    public static void getFileThumbnail(final RecyclingImageView imageView, final ImageFile file) {
        getThumbnail(imageView, file.getPath());
    }

    private static void getThumbnail(final RecyclingImageView imageView, String path) {
        getInstance().loadImage(path, imageView, sOpts);
    }
}
