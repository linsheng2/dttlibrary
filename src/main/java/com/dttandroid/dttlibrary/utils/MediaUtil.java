package com.dttandroid.dttlibrary.utils;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.dttandroid.dttlibrary.R;
import com.dttandroid.dttlibrary.device.PackageHelper;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午1:24:30
 * @Description:
 */
@SuppressLint("NewApi")
public class MediaUtil {
    /**
     * 打开相册的requestCode
     */
    public static final int OPEN_GALLERY_REQUEST_CODE = Short.MAX_VALUE - 1;

    /**
     * 打开系统相机拍照requestCode
     */
    public static final int OPEN_CAMERA_REQUEST_CODE = Short.MAX_VALUE - 2;

    /**
     * 裁剪图片requestCode
     */
    public static final int CROP_PHOTO_REQUEST_CODE = Short.MAX_VALUE - 3;

    /**
     * 打开相册
     * 
     * @param activity
     *            调用相册的页面
     */
    public static void openGallery(Activity activity) {
        if (activity == null) {
            return;
        }

        if (!StorageUtil.hasSDCard()) {
            Toast.makeText(activity, "无SD卡", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        if (PackageHelper.isIntentAvailable(activity, intent)) {
            activity.startActivityForResult(intent, OPEN_GALLERY_REQUEST_CODE);
        }
        else {
            Toast.makeText(activity, "您的设备不支持该操作", Toast.LENGTH_LONG).show();
        }
    }

    
    /**
     * 打开相册
     * 
     * @param fragment
     *            调用相册的页面
     */
    public static void openGallery(Fragment fragment) {
        if (fragment == null) {
            return;
        }

        if (!StorageUtil.hasSDCard()) {
            Toast.makeText(fragment.getActivity(), "无SD卡", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        if (PackageHelper.isIntentAvailable(fragment.getActivity(), intent)) {
        	fragment.startActivityForResult(intent, OPEN_GALLERY_REQUEST_CODE);
        }
        else {
            Toast.makeText(fragment.getActivity(), "您的设备不支持该操作", Toast.LENGTH_LONG).show();
        }
    }
    /**
     * 打开系统相机拍照
     * 
     * @param activity
     *            调用相机的页面
     * @param outputPath
     *            输出路径
     */
    public static void openCamera(Activity activity, String outputPath) {
        if (activity == null) {
            return;
        }

        if (!StorageUtil.hasSDCard()) {
            Toast.makeText(activity, "无SD卡", Toast.LENGTH_SHORT).show();
            return;
        }
        
        File file = new File(outputPath);
        if (!file.getParentFile().exists()) {
            file.mkdirs();
        }

        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		try {
			activity.startActivityForResult(intent, OPEN_CAMERA_REQUEST_CODE);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(activity, "您的设备不支持该操作", Toast.LENGTH_LONG).show();
		}
    }

    /**
     * 打开系统相机拍照
     * 
     * @param fragment
     *            调用相机的页面
     * @param outputPath
     *            输出路径
     */
    public static void openCamera(Fragment fragment, String outputPath) {
        if (fragment == null) {
            return;
        }

        if (!StorageUtil.hasSDCard()) {
            Toast.makeText(fragment.getActivity(), "无SD卡", Toast.LENGTH_SHORT).show();
            return;
        }
        
        File file = new File(outputPath);
        if (!file.getParentFile().exists()) {
            file.mkdirs();
        }

        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		try {
			fragment.startActivityForResult(intent, OPEN_CAMERA_REQUEST_CODE);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(fragment.getActivity(), "您的设备不支持该操作", Toast.LENGTH_LONG).show();
		}
    }
    
    /**
     * 裁剪图片
     * 
     * @param activity
     *            调用图片裁剪的页面
     * @param inputUri
     *            图片Uri
     * @param width
     *            裁剪图片宽度
     * @param height
     *            裁剪图片高度
     */
    public static void cropPhoto(Activity activity, Uri inputUri, Uri outputUri, int width, int height ,int aspectX,int aspectY ) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(inputUri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        
        // 缩放
        intent.putExtra("scale", true);
        
        //防止黑边
        intent.putExtra("scaleUpIfNeeded", true);

        // 保存到uri
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        
        // 取消人脸识别
        intent.putExtra("noFaceDetection", false);
        try {
        	activity.startActivityForResult(intent, CROP_PHOTO_REQUEST_CODE);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(activity, activity.getString(R.string.common_picture_avaer_not_available), Toast.LENGTH_SHORT).show();
			
		}
        
    }
    
    /**
     * 裁剪图片
     * 
     * @param fragment
     *            调用图片裁剪的页面
     * @param inputUri
     *            图片Uri
     * @param width
     *            裁剪图片宽度
     * @param height
     *            裁剪图片高度
     */
    public static void cropPhoto(Fragment fragment, Uri inputUri, Uri outputUri, int width, int height,int aspectX,int aspectY ) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(inputUri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY",aspectY);
        
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        
        // 缩放
        intent.putExtra("scale", true);
        
        //防止黑边
        intent.putExtra("scaleUpIfNeeded", true);
        // 保存到uri
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        
        // 取消人脸识别
        intent.putExtra("noFaceDetection", false);
        try {
        	fragment.startActivityForResult(intent, CROP_PHOTO_REQUEST_CODE);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(fragment.getActivity(), fragment.getActivity().getString(R.string.common_picture_avaer_not_available), Toast.LENGTH_SHORT).show();
			
		}
        
    }

    /**
     * 通过相册中图片Uri获取图片路径
     * 
     * @param context
     *            上下文实例
     * @param uri
     *            相册中图片Uri
     * @return 图片路径
     */
    public static String getPathFromGalleryUri(Context context, Uri uri) {
        String path = null;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        }
        catch (Exception e) {
            boolean isNewVersionSystem = false;
            int indexOf = -1;
            String imgPathString = uri.toString();

            if (imgPathString.toLowerCase().contains("/sdcard/")) {
                indexOf = imgPathString.toLowerCase().indexOf("/sdcard/");
            }
            else if (imgPathString.toLowerCase().contains("/sdcard0/")) {
                indexOf = imgPathString.toLowerCase().indexOf("/sdcard0/");
            }
            else if (imgPathString.toLowerCase().contains("/storage/emulated/0/")) {
                indexOf = imgPathString.toLowerCase().indexOf("/storage/emulated/0/");
                isNewVersionSystem = true;
            }

            if (-1 != indexOf) {
                if (isNewVersionSystem) {
                    path = StorageUtil.getExternalStoragePath() + "/"
                            + imgPathString.substring(indexOf + 20);
                }
                else {
                    path = StorageUtil.getExternalStoragePath() + "/" + imgPathString.substring(indexOf + 8);
                }
            }

            else {
                indexOf = imgPathString.toLowerCase().indexOf("/data/");
                if (-1 != indexOf)
                    path = imgPathString.substring(indexOf);
            }
        }
        finally {
            if (cursor != null)
                cursor.close();
        }

        Log.v("lufengwen", "AlbumUtils.getDirFromAlbumUri-> path:" + path);
        return path;
    }
}
