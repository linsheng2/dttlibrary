//DeviceInfo.java FILEHEADER_BEGIN ***************************
package com.dttandroid.dttlibrary.device;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午9:23:15
 * @Description: 设备信息
 */
public class DeviceInfo {
    private static int sScreenWidth;
    private static int sScreenHeight;
    private static int sScreenDPI;
    private static float sScreenDensity;
    private static int sChannelCode;
    private static String sCompileDate;
    private static String sMapKey;
    private static String sFirstPublishChannelStr;
    private static Set<Integer> sFirstPublishChannels;
    
    /**
     * 初始化硬件信息，该方法只在Application.onCreate中调用一次
     * 
     * @param context 应用程序上下文
     */
    public static void init(Context context) {
        sScreenWidth = ScreenHelper.getWidth(context);
        sScreenHeight = ScreenHelper.getHeight(context);
        sScreenDensity = ScreenHelper.getDensity(context);
        sScreenDPI = ScreenHelper.getDPI(context);

        sChannelCode = getChannel(context);

        sCompileDate = PackageHelper.getMetaData(context, "CompileDate");
        sMapKey = PackageHelper.getMetaData(context, "com.amap.api.v2.apikey");

        sFirstPublishChannelStr = PackageHelper.getMetaData(context, "FirstPublishChannel");
        sFirstPublishChannels = new HashSet<Integer>();
        initPublishChannels(sFirstPublishChannelStr);
    }
    
    private static void initPublishChannels(String channelsString) {
        try {
        	if (channelsString == null) {
        		return;
        	}
            String[] channels = channelsString.split(",");
            for (String channel : channels) {
                sFirstPublishChannels.add(Integer.valueOf(channel));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 获取屏幕宽度，单位：px
     * 
     * @return 屏幕像素宽度
     */
    public static int getScreenWidth() {
        return sScreenWidth;
    }
    
    /**
     * 获取屏幕高度，单位：px
     * 
     * @return 屏幕像素高度
     */
    public static int getScreenHeight() {
        return sScreenHeight;
    }
    
    /**
     * 获取屏幕密度
     * 
     * @return 屏幕密度
     */
    public static float getScreenDensity() {
        return sScreenDensity;
    }
    
    /**
     * 获取屏幕DPI
     * 
     * @return 屏幕DPI
     */
    public static int getScreenDPI() {
        return sScreenDPI;
    }
    
    /**
     * 获取渠道编号
     * 
     * @return 渠道编号
     */
    public static int getChannelCode() {
        return sChannelCode;
    }
    
    /**
     * 返回Apk编译日期（仅在用Ant编译时才可用）
     * 
     * @return Apk编译日期
     */
    public static String getCompileDate() {
        return sCompileDate;
    }

    /**
     * 获取首发渠道
     *
     * @return 首发渠道列表字符串
     */
    public static String getFirstPublishChannelStr() {
        return sFirstPublishChannelStr;
    }

    /**
     * 获取首发渠道
     * 
     * @return 首发渠道
     */
    public static Set<Integer> getFirstPublishChannels() {
        return sFirstPublishChannels;
    }

    /**
     * 获取高德地图Key
     *
     */
    public static String getMapKey() {
        return sMapKey;
    }

    private static int getChannel(Context context) {
        InputStream input = null;
        try {
            input = context.getAssets().open("channel");
            byte[] buffer = new byte[10]; // eg: 81018, size is 5 bytes
            int length = input.read(buffer);
            String channel = new String(buffer, 0, length);
            return Integer.valueOf(channel);
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        finally {
            if (input != null) {
                try {
                    input.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
