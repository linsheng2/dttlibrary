package com.dttandroid.dttlibrary.gallery.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午9:21:34
 * @Description:
 */
public class MemLruCache<T> extends AbstractMemCache<T> {
	private LruCache<T, Bitmap> mMemoryCache;
	private int mEntryNum = 0;

	@Override
	public void init(int size) {
		mMemoryCache = new LruCache<T, Bitmap>(size) {

			@Override
			protected void entryRemoved(boolean evicted, T key, Bitmap oldValue, Bitmap newValue) {
				if (++mEntryNum >= 20) {
					mEntryNum = 0;
					System.gc();
				}
			}

			@Override
			protected int sizeOf(T key, Bitmap value) {
				return value.getRowBytes() * value.getHeight() / 1024;
			}
		};
	}

	@Override
	public void add(T key, Bitmap bitmap) {
		if (key == null || bitmap == null) {
			return;
		}

		if (mMemoryCache != null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	@Override
	public Bitmap get(T key) {
		Bitmap bitmap = null;

		if (mMemoryCache != null) {
			bitmap = mMemoryCache.get(key);
		}

		return bitmap;
	}

	@Override
	public void remove(T key) {
		if (mMemoryCache != null) {
			mMemoryCache.remove(key);
		}
	}

	@Override
	public void clear() {
		if (mMemoryCache != null) {
			mMemoryCache.evictAll();
		}
	}

	@Override
	public int maxSize() {
		return mMemoryCache.maxSize();
	}

	@Override
	public int size() {
		return mMemoryCache.size();
	}

	@Override
	public int keys() {
		return mMemoryCache.snapshot().size();
	}

	@Override
	public int evictions() {
		return mMemoryCache.evictionCount();
	}

	@Override
	public int hits() {
		return mMemoryCache.hitCount();
	}
}
