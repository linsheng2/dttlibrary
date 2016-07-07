package com.dttandroid.dttlibrary.gallery;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.dttandroid.dttlibrary.gallery.cache.ImageCache;
import com.dttandroid.dttlibrary.graphics.ImageHelper;
import com.dttandroid.dttlibrary.graphics.ImageOptions;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午9:19:29
 * @Description:
 */
public abstract class ImageWorker<T> implements CacheStat {
    protected ImageCache<T> mCache;
    protected Resources mResources;
    protected Executor mExecutor;
    protected Handler mHandler;

    protected AtomicLong cache2Hits = new AtomicLong(0);
    protected AtomicLong allRequests = new AtomicLong(0);

    /**
     * 初始化方法，必须调用，推荐在子类构造函数中选择合适的时机调用
     */
    protected void init() {
        mCache = initCache();
        mResources = initResources();
        mExecutor = initExecutor();
    }

    /**
     * 加载图片
     * 
     * @param key
     *            用于获取图片的key
     * @param imageView
     *            ImageView实例
     * @param opts
     *            图片加载参数
     */
    public void loadImage(T key, ImageView imageView, ImageOptions opts) {
        if (key == null) {
            return;
        }

        if (opts == null) {
            opts = new ImageOptions.Builder().build();
        }

        if (opts.isProcessAsync() && mHandler == null) {
            mHandler = new Handler();
        }

        allRequests.incrementAndGet();

        // 从内存缓存中读取
        Bitmap bitmap = null;
        if (mCache != null && mCache.getMemCache() != null) {
            bitmap = mCache.getMemCache().get(key);
        }

        if (bitmap != null) {
            final ImageView iv = imageView;
            final ImageOptions options = opts;
            process(bitmap, opts, new OnProcessListener() {

                @Override
                public void onCompleted(Bitmap bitmap) {
                    setImageDrawable(iv, bitmap, options);
                }
            });
        }
        else if (cancelPotentialWork(key, imageView, opts)) { // 取消潜在任务
            if (opts.onLoadBegin() != null) {
                opts.onLoadBegin().run();
            }

            BitmapWorkerTask<T> task = new BitmapWorkerTask<T>(this, key, imageView, opts, mResources);

            if (imageView != null) {
                AsyncDrawable<T> asyncDrawable = new AsyncDrawable<T>(mResources, getLoadingBitmap(imageView, opts), task);

                if (opts.isBackground()) {
                    imageView.setBackgroundDrawable(asyncDrawable);
                }
                else {
                    imageView.setImageDrawable(asyncDrawable);
                }
            }

            task.executeOnExecutor(mExecutor);
        }
    }

    public void shutdownNow() {
        if (mExecutor != null) {
            ExecutorService srv = (ExecutorService) mExecutor;
            if (srv != null) {
                srv.shutdownNow();
            }
        }
    }

    public ImageCache<T> getCache() {
        return mCache;
    }

    public boolean hasMemCache() {
        return mCache != null && mCache.getMemCache() != null;
    }

    public boolean hasDiskCache() {
        return mCache != null && mCache.getDiskCache() != null;
    }

    /**
     * 初始化缓存，如果不需要则返回null
     * 
     * @return 图片缓存实例
     */
    protected abstract ImageCache<T> initCache();

    /**
     * 初始化资源（必须）
     * 
     * @return Resource实例
     */
    protected abstract Resources initResources();

    /**
     * 初始化线程池
     * 
     * @return 线程池实例
     */
    protected abstract Executor initExecutor();

    /**
     * 后台线程执行加载图片操作
     * 
     * @param key
     *            用于获取图片的key
     * @return Bitmap实例
     */
    protected abstract Bitmap loadBitmap(T key);

    // public BitmapDrawable load(T key) {
    // return ImageHelper.bitmapToDrawable(mResources, loadBitmap(key));
    // }

    private Bitmap getLoadingBitmap(ImageView imageView, ImageOptions opts) {
        if (imageView == null) {
            return null;
        }

        Bitmap loadingBitmap = null;

        if (!opts.isResetView()) {
            BitmapDrawable currentDrawable = null;
            if (opts.isBackground()) {
                currentDrawable = (BitmapDrawable) imageView.getBackground();
            }
            else {
                currentDrawable = (BitmapDrawable) imageView.getDrawable();
            }

            // 如果ImageView 已经有图片显示，则loading图片显示之前的图片，否则显示ImageOptions配置的图片
            if (currentDrawable != null) {
                loadingBitmap = currentDrawable.getBitmap();
            }
            else {
                loadingBitmap = opts.getImageOnLoading(mResources);
            }
        }
        else {
            loadingBitmap = opts.getImageOnLoading(mResources);
        }
        return loadingBitmap;
    }

    public boolean cancelPotentialWork(T key, ImageView imageView, ImageOptions opts) {
        final BitmapWorkerTask<T> bitmapWorkerTask = getBitmapWorkerTask(imageView, opts);

        if (bitmapWorkerTask != null) {
            final T oldKey = bitmapWorkerTask.key;
            if (oldKey == null || !oldKey.equals(key)) {
                bitmapWorkerTask.cancel(true);
            }
            else {
                return false;
            }
        }
        return true;
    }

    private BitmapWorkerTask<T> getBitmapWorkerTask(ImageView imageView, ImageOptions opts) {
        if (imageView != null) {
            final Drawable drawable = opts.isBackground() ? imageView.getBackground() : imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                @SuppressWarnings("unchecked")
                final AsyncDrawable<T> asyncDrawable = (AsyncDrawable<T>) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public static class BitmapWorkerTask<T> extends AsyncTask<Void, Void, Bitmap> {
        private final ImageWorker<T> loader;
        private final T key;
        private final WeakReference<ImageView> imageViewRef;
        private final ImageOptions opts;

        public BitmapWorkerTask(ImageWorker<T> loader, T key, ImageView imageView, ImageOptions opts, Resources res) {
            this.loader = loader;
            this.key = key;
            this.imageViewRef = new WeakReference<ImageView>(imageView);
            this.opts = opts;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = null;

            // 读取磁盘缓存
            if (loader.hasDiskCache() && !isCancelled() && getAttachedImageView() != null) {
                bitmap = loader.mCache.getDiskCache().get(key);

                if (bitmap != null) {
                    loader.cache2Hits.incrementAndGet();
                }
            }

            // 从网络载入或者从本地其它地方读取
            if (bitmap == null && !isCancelled()) {
                // 当isIgnoreImageView 为true，只是利用加载框架来进行缓存
                if (opts.isIgnoreImageView() || getAttachedImageView() != null) {
                    bitmap = loader.loadBitmap(key);

                    if (opts.getOnHandlerImage() != null) {
                        bitmap = opts.getOnHandlerImage().onHandler(bitmap);
                    }

                    if (bitmap != null && loader.hasDiskCache()) {
                        loader.mCache.getDiskCache().add(key, bitmap);
                    }
                }
            }

            if (loader.hasMemCache()) {
                loader.mCache.getMemCache().add(key, bitmap);
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap value) {
            if (isCancelled()) {
                value = null;
            }

            final ImageView imageView = getAttachedImageView();
            if (imageView != null) {
                loader.process(value, opts, new OnProcessListener() {

                    @Override
                    public void onCompleted(Bitmap bitmap) {
                        loader.setImageDrawable(imageView, bitmap, opts);
                    }
                });
            }
        }

        private ImageView getAttachedImageView() {
            final ImageView imageView = imageViewRef.get();
            final BitmapWorkerTask<T> bitmapWorkerTask = loader.getBitmapWorkerTask(imageView, opts);

            if (this == bitmapWorkerTask) {
                return imageView;
            }

            return null;
        }
    }

    public static class AsyncDrawable<T> extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask<T>> bitmapWorkerTaskRef;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask<T> bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskRef = new WeakReference<BitmapWorkerTask<T>>(bitmapWorkerTask);
        }

        public BitmapWorkerTask<T> getBitmapWorkerTask() {
            return bitmapWorkerTaskRef.get();
        }
    }

    private void setImageDrawable(final ImageView imageView, final Bitmap bitmap, final ImageOptions opts) {
        Runnable task = new Runnable() {

            @Override
            public void run() {
                if (imageView != null) {
                    Bitmap image = bitmap;
                    if (image == null) {
                        image = opts.getImageOnFail(mResources);
                    }

                    Drawable dr = null;
                    if (opts.isRounded() &&  image != null) {
                        if (opts.getRoundedType() == ImageOptions.RoundedType.Full) {
                            dr = new RoundedDrawable(image, true, 0);
                        }
                        else {
                            dr = new RoundedDrawable(image, false, opts.getRoundedRadius());
                        }
                    }
                    else {
                        dr = new BitmapDrawable(mResources, image);
                    }

                    if (opts.isBackground()) {
                        imageView.setBackgroundDrawable(dr);
                    }
                    else {
                        imageView.setImageDrawable(dr);
                    }
                }

                if (opts.onLoadEnd() != null) {
                    opts.onLoadEnd().run();
                }
            }
        };

        if (Looper.myLooper() != Looper.getMainLooper()) {
            mHandler.post(task);
        }
        else {
            task.run();
        }
    }

    protected void process(final Bitmap bitmap, final ImageOptions opts, final OnProcessListener listener) {
        Runnable task = new Runnable() {

            @Override
            public void run() {
                Bitmap dstBitmap = null;
                if (opts.getOnProcessingListener() == null) {
                    dstBitmap = bitmap;
                }
                else {
                    dstBitmap = opts.getOnProcessingListener().onProcessing(bitmap);
                }

                dstBitmap = ImageHelper.toGrayscaleIfNeeded(mResources, dstBitmap, opts);
                // dstBitmap = ImageHelper.roundedIfNeeded(mResources,
                // dstBitmap, opts);
                if (opts.isBlur() && bitmap != null) {
                    try {
                        dstBitmap = FastBlur.doBlur(dstBitmap, opts.getBlurRadius(), false);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    catch (OutOfMemoryError err) {
                        err.printStackTrace();
                    }
                }

                listener.onCompleted(dstBitmap);
            }
        };

        if (opts.isProcessAsync()) {
            mExecutor.execute(task);
        }
        else {
            task.run();
        }
    }

    public long getMaxSize() {
        return mCache.getMemCache().maxSize();
    }

    public long getUsedSize() {
        return mCache.getMemCache().size();
    }

    public long getTotalKeys() {
        return mCache.getMemCache().keys();
    }

    public long getEvictionCount() {
        return mCache.getMemCache().evictions();
    }

    public long getCache1Hits() {
        return mCache.getMemCache().hits();
    }

    public long getCache2Hits() {
        return cache2Hits.get();
    }

    public long getPreloads() {
        return 0;
    }

    public long getAllRequests() {
        return allRequests.get();
    }

    protected interface OnProcessListener {
        public void onCompleted(Bitmap bitmap);
    }
}
