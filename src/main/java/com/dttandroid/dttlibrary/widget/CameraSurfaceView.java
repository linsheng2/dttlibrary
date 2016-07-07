package com.dttandroid.dttlibrary.widget;

import java.io.IOException;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.dttandroid.dttlibrary.debug.AppLogger;
import com.dttandroid.dttlibrary.device.CameraHelper;
import com.dttandroid.dttlibrary.device.HardwareHelper;
import com.dttandroid.dttlibrary.device.ScreenHelper;
import com.dttandroid.dttlibrary.setting.LibSettings;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午12:32:42
 * @Description:
 */
@SuppressWarnings("deprecation")
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder mHolder;
	private Camera mCamera;
	private OnCameraStatusListener mOnCameraStatusListener;
	private int mNumberOfCameras;
	private boolean mIsFrontCamera;
	private boolean mIsTakingPicture;

	public CameraSurfaceView(Context context) {
		super(context);
		init();
	}

	public CameraSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CameraSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@TargetApi(9)
	private void init() {
		mHolder = getHolder();
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mHolder.addCallback(this);
		mNumberOfCameras = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD ? Camera.getNumberOfCameras() : 1;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		openCamera(mIsFrontCamera);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mCamera != null) {
			mCamera.release();
		}
	}

	public boolean canTakePictureFromFrontCamera() {
		return mNumberOfCameras > 1;
	}

	public boolean isFrontCamera() {
		return mIsFrontCamera;
	}

	public boolean isTakingPicture() {
		return mIsTakingPicture;
	}

	public boolean hasFlashLight() {
		return HardwareHelper.hasFlashLight(mCamera);
	}

	@TargetApi(9)
	public void switchCamera() {
		if (!canTakePictureFromFrontCamera()) {
			return;
		}

		if (mOnCameraStatusListener != null) {
			mOnCameraStatusListener.onCameraSwitching();
		}
		mIsFrontCamera = !mIsFrontCamera;
		try {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		catch (Exception e) {
			e.printStackTrace();
			if (mOnCameraStatusListener != null) {
				mOnCameraStatusListener.onError();
			}
		}
		openCamera(mIsFrontCamera);
		startPreview();
	}

	@TargetApi(9)
	private void openCamera(boolean isFrontCamera) {
		try {
			if (isFrontCamera) {
				CameraInfo cameraInfo = new CameraInfo();
				for (int cameraId = 0; cameraId < mNumberOfCameras; cameraId++) {
					Camera.getCameraInfo(cameraId, cameraInfo);
					if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
						mCamera = Camera.open(cameraId);
					}
				}
			}
			else {
				mCamera = Camera.open();
			}
		}
		catch (Exception e) {
			e.printStackTrace(); // java.lang.RuntimeException: Fail to connect to camera service
			if (mOnCameraStatusListener != null) {
				mOnCameraStatusListener.onError();
			}
			return;
		}

		try {
			mCamera.setPreviewDisplay(mHolder);
		}
		catch (IOException e) {
			if (mCamera != null) {
				mCamera.release();
				mCamera = null;
			}
			e.printStackTrace();
		}
	}

	private void startPreview() {
		int w = ScreenHelper.getWidth(getContext());
		int h = ScreenHelper.getHeight(getContext());

		try {
			Parameters params = mCamera.getParameters();
			params.set("orientation", "portrait");
			params.setRotation(90);
			if (hasFlashLight()) {
				params.setFlashMode(LibSettings.getCameraFlashMode());
			}
			params.setPictureFormat(ImageFormat.JPEG);
			params.setJpegQuality(75);

			// 相机是横屏，界面调整成了竖屏，所以这里需要反起写
			Size size = CameraHelper.getOptimalSize(params.getSupportedPictureSizes(), h, w);
			if (size != null) {
				params.setPictureSize(size.width, size.height);
			}

			size = CameraHelper.getOptimalSize(params.getSupportedPreviewSizes(), h, w);
			if (size != null) {
				params.setPreviewSize(size.width, size.height);
			}

			mCamera.setParameters(params);
			mCamera.setDisplayOrientation(90);
		}
		catch (Exception e) {
			e.printStackTrace();

			if (mOnCameraStatusListener != null) {
				mOnCameraStatusListener.onError();
			}
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					mCamera.startPreview();
					autoFocus();
					if (mOnCameraStatusListener != null) {
						mOnCameraStatusListener.onCameraSwitched();
					}
				}
				catch (Exception e) {
					e.printStackTrace();

					if (mOnCameraStatusListener != null) {
						mOnCameraStatusListener.onError();
					}
				}
			}
		}).start();
	}

	public void takePicture() {
		if (mIsTakingPicture) {
			return;
		}

		mIsTakingPicture = true;
		if (mCamera != null) {
			// 自动对焦 
			if (mOnCameraStatusListener != null) {
				mOnCameraStatusListener.onFocusing();
			}
			mCamera.autoFocus(new AutoFocusCallback() {

				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					if (mOnCameraStatusListener != null) {
						mOnCameraStatusListener.onFocused(success);
					}

					try {
						mCamera.takePicture(null, null, new PictureCallback() {

							@Override
							public void onPictureTaken(byte[] data, Camera camera) {
								mCamera.stopPreview();
								mCamera.release();
								mCamera = null;
								if (mOnCameraStatusListener != null) {
									mOnCameraStatusListener.onCameraStopped(data);
								}
							}
						});
					}
					catch (RuntimeException e) {
						e.printStackTrace();

						AppLogger.e(e.getMessage(), false);

						//                        if (mOnCameraStatusListener != null) {
						//                            mOnCameraStatusListener.onError();
						//                        }
					}
				}
			});
		}
	}

	public void autoFocus() {
		if (mCamera != null) {
			if (mOnCameraStatusListener != null) {
				mOnCameraStatusListener.onFocusing();
			}
			try {
				mCamera.autoFocus(new AutoFocusCallback() {

					@Override
					public void onAutoFocus(boolean success, Camera camera) {
						if (mOnCameraStatusListener != null) {
							mOnCameraStatusListener.onFocused(success);
						}
					}
				});
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void switchFlashMode() {
		if (!hasFlashLight()) {
			Toast.makeText(getContext(), "未找到闪光灯", Toast.LENGTH_LONG).show();
			return;
		}

		String currentMode = LibSettings.getCameraFlashMode();
		if (Parameters.FLASH_MODE_OFF.equals(currentMode)) {
			LibSettings.setCameraFlashMode(Parameters.FLASH_MODE_ON);
		}
		else if (Parameters.FLASH_MODE_ON.equals(currentMode)) {
			LibSettings.setCameraFlashMode(Parameters.FLASH_MODE_AUTO);
		}
		else if (Parameters.FLASH_MODE_AUTO.equals(currentMode)) {
			LibSettings.setCameraFlashMode(Parameters.FLASH_MODE_OFF);
		}
		else {
			LibSettings.setCameraFlashMode(Parameters.FLASH_MODE_AUTO);
		}

		if (mCamera != null) {
			Parameters params = mCamera.getParameters();
			params.setFlashMode(LibSettings.getCameraFlashMode());
			mCamera.setParameters(params);
		}
	}

	private Size getBestPictureSize(Parameters params) {
		List<Size> sizes = params.getSupportedPictureSizes();

		int screenPixels = ScreenHelper.getWidth(getContext()) * ScreenHelper.getHeight(getContext());
		int minPixelDiffValue = Integer.MAX_VALUE;
		Size bestSize = null;
		for (Size size : sizes) {
			if (bestSize == null) {
				bestSize = size;
			}
			else {

			}

			int pixelDiffValue = Math.abs(size.width * size.height - screenPixels);
			if (pixelDiffValue < minPixelDiffValue) {
				minPixelDiffValue = pixelDiffValue;
				bestSize = size;
			}
		}

		return minPixelDiffValue == Integer.MAX_VALUE ? null : bestSize;
	}

	private Size getBestPreviewSize(Parameters params) {
		List<Size> sizes = params.getSupportedPreviewSizes();
		int screenPixels = ScreenHelper.getWidth(getContext()) * ScreenHelper.getHeight(getContext());
		int minPixelDiffValue = Integer.MAX_VALUE;
		Size bestSize = null;
		for (Size size : sizes) {
			int pixelDiffValue = Math.abs(size.width * size.height - screenPixels);
			if (pixelDiffValue < minPixelDiffValue) {
				minPixelDiffValue = pixelDiffValue;
				bestSize = size;
			}
		}

		return minPixelDiffValue == Integer.MAX_VALUE ? null : bestSize;
	}

	public void setOnCameraStatusListener(OnCameraStatusListener listener) {
		mOnCameraStatusListener = listener;
	}

	public static interface OnCameraStatusListener {
		public void onCameraSwitching();

		public void onCameraSwitched();

		public void onCameraStopped(byte[] data);

		public void onFocusing();

		public void onFocused(boolean success);

		public void onError();
	}
}
