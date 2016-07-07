package com.dttandroid.dttlibrary.device;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.hardware.Camera;


/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午12:41:17
 * @Description: 相机辅助类
 */
@SuppressWarnings("deprecation")
public class CameraHelper {
    public static Camera.Size getOptimalSize(List<Camera.Size> sizes, int w, int h) {
        Collections.sort(sizes, new SizeComparator());
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) {
            return null;
        }

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.height - targetHeight) <= minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) <= minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private static class SizeComparator implements Comparator<Camera.Size> {

        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            return lhs.width * lhs.height - rhs.width * rhs.height;
        }
    }
}
