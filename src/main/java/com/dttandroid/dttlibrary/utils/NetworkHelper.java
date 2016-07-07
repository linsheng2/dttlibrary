package com.dttandroid.dttlibrary.utils;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.text.TextUtils;

/**
 * @Author: lufengwen
 * @Date: 2015年6月13日 下午6:53:56
 * @Description: 网络辅助工具
 */
public class NetworkHelper {

    /**
     * 是否连接WIFI
     *
     * @param context 应用程序Context实例
     * @return WIFI连接状态
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiInfo != null && wifiInfo.isConnected();
    }

    /**
     * 是否连接移动数据网络
     *
     * @param context 应用程序Context实例
     * @return 移动数据网络连接状态
     */
    public static boolean isMobileConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return mobileInfo != null && mobileInfo.isConnected();
    }

    /**
     * 检查是否有可用网络
     *
     * @param context 应用程序Context实例
     * @return 网络可用状态
     */
    public static boolean isAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isAvailable();
    }

    /**
     * 检查网络是否连接上
     *
     * @param context 应用程序Context实例
     * @return 网络连接状态
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    /**
     * 获取WIFI锁
     *
     * @param tag 标识WIFI锁
     * @return WIFI锁实例
     */
    public static WifiLock acquireWifiLock(Context context, String tag) {
        WifiLock lock = null;
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr != null) {
            lock = wifiMgr.createWifiLock(tag);
            lock.acquire();
            lock.setReferenceCounted(false);
        }
        return lock;
    }

    /**
     * 释放WIFI锁
     *
     * @param lock WIFI锁实例
     */
    public static void releaseWifiLock(WifiLock lock) {
        if (lock != null) {
            if (lock.isHeld()) {
                lock.release();
            }
        }
    }

    public static String host2ip(String domain) {
        String ip;
        try {
            InetAddress addr = InetAddress.getByName(domain);
            ip = addr.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            ip = "";
        }
        return ip;
    }

    public static boolean ping(String domain) {
        int timeOut = 3000;
        try {
            return InetAddress.getByName(domain).isReachable(timeOut);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean telnet(String domain, int port) {
        String ip = host2ip(domain);
        if (TextUtils.isEmpty(ip))
            return false;
        try {
            new Socket(ip, port);
            return true;
        } catch (UnknownHostException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean checkhttp(String url) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.connect();
            return true;
        } catch (final Exception e) {
            return false;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}