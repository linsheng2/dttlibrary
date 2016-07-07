package com.dttandroid.dttlibrary.device;

import java.util.List;

import android.hardware.Camera;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午12:38:34
 * @Description: 硬件助手
 */
@SuppressWarnings("deprecation")
public class HardwareHelper {
    
    /**
     * 检查设备是否有闪光灯
     * @param camera
     * @return
     */
    public static boolean hasFlashLight(Camera camera) {
        if (camera == null) {
            return false;
        }

        Camera.Parameters params = camera.getParameters();

        if (params.getFlashMode() == null) {
            return false;
        }

        List<String> supportedModes = params.getSupportedFlashModes();
        if (supportedModes == null || supportedModes.isEmpty() || 
                (supportedModes.size() == 1 && supportedModes.get(0).equals(Camera.Parameters.FLASH_MODE_OFF))) {
            return false;
        }

        return true;
    }
}

