package com.dttandroid.dttlibrary.device;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午1:21:22
 * @Description:
 */
public class PackageHelper {

    /**
     * 检查是否有Activity能够执行指定的Intent
     * 
     * @param context
     *            上下文实例
     * @param intent
     *            intent实例
     * @return 是否能被执行
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> componentList = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (componentList.size() > 0) {
            return true;
        }

        return false;
    }

    /**
     * 获取包名
     * 
     * @param context
     *            上下文实例
     * @return 包名
     */
    public static String getPackageName(Context context) {
        if (context != null) {
            return context.getPackageName();
        }
        return "";
    }

    /**
     * 获取VersionCode
     * 
     * @param context
     *            上下文实例
     * @return VersionCode
     */
    public static int getVersionCode(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        }
        catch (NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 获取VersionName
     * 
     * @param context
     *            上下文实例
     * @return VersionName
     */
    public static String getVersionName(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        }
        catch (NameNotFoundException e) {
            e.printStackTrace();
            return "unknow";
        }
    }

    /**
     * 获取元数据
     * 
     * @param context
     *            上下文实例
     * @param key
     *            元数据key
     * @return 元数据值
     */
    public static String getMetaData(Context context, String key) {
        String packageName = context.getPackageName();
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            Object value = info.metaData.get(key);
            if (value != null) {
                return value.toString();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取初次安装时间
     * 
     * @param context
     *            上下文实例
     * @return firstInstallTime
     */
    @SuppressLint("NewApi")
    public static long getFirstInstallTime(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.firstInstallTime;
        }
        catch (NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
