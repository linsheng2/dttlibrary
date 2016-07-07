package com.dttandroid.dttlibrary.gallery;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午9:25:36
 * @Description: 针对应用缓存的统计
 */
public interface CacheStat {
    long getMaxSize();

    long getUsedSize();

    long getTotalKeys();
    
    long getEvictionCount();

    long getCache1Hits();

    long getCache2Hits();
    
    long getPreloads();

    long getAllRequests();
}
