package com.dttandroid.dttlibrary.gallery.manager;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Process;

import com.dttandroid.dttlibrary.booter.BaseApp;
import com.dttandroid.dttlibrary.device.DeviceInfo;
import com.dttandroid.dttlibrary.gallery.ImageWorker;
import com.dttandroid.dttlibrary.gallery.cache.ImageCache;
import com.dttandroid.dttlibrary.gallery.cache.MemLruCache;
import com.dttandroid.dttlibrary.utils.ImageUtil;


/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午9:24:47
 * @Description:
 */
public class GalleryViewerWorker extends ImageWorker<String> {
	private static final int POOL_SIZE = 2;
	private static final int BLOCKING_QUEUE_SIZE = 3;
	private static int mWidth = DeviceInfo.getScreenWidth() / 2;
	private static int mHeight = DeviceInfo.getScreenHeight() / 2;

	public GalleryViewerWorker() {
		init();
	}

	@Override
	protected ImageCache<String> initCache() {
		ImageCache.ImageCacheParams params = new ImageCache.ImageCacheParams();
		params.diskCacheEnabled = false;
		params.memCacheEnabled = true;
		params.memCacheSize = ((int) (Runtime.getRuntime().maxMemory() / 1024)) / 8;
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
				return new Thread(r, "GalleryViewerExecutor Thread #" + mCount.getAndIncrement()) {

					@Override
					public void run() {
						Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
						super.run();
					}
				};
			}
		};

		return new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(BLOCKING_QUEUE_SIZE), factory, new ThreadPoolExecutor.DiscardOldestPolicy());
	}

	@Override
	protected Bitmap loadBitmap(String path) {
		Bitmap bitmap = null;
		try {
			bitmap = ImageUtil.decodeSampledFile(path, mWidth, mHeight, false);
		}
		catch (OutOfMemoryError e) {
			e.printStackTrace();
		}

		return bitmap;
	}
}
