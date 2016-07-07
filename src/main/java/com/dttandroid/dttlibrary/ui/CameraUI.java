package com.dttandroid.dttlibrary.ui;

import java.io.FileOutputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Rect;
import android.hardware.Camera.Parameters;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.dttandroid.dttlibrary.R;
import com.dttandroid.dttlibrary.device.SensorHelper;
import com.dttandroid.dttlibrary.graphics.BitmapGenerator;
import com.dttandroid.dttlibrary.setting.LibSettings;
import com.dttandroid.dttlibrary.utils.ImageUtil;
import com.dttandroid.dttlibrary.utils.StorageUtil;
import com.dttandroid.dttlibrary.utils.ViewHelper;
import com.dttandroid.dttlibrary.widget.CameraSurfaceView;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午12:30:33
 * @Description: 相机
 */
public class CameraUI extends BaseActivity implements OnClickListener, CameraSurfaceView.OnCameraStatusListener, OnTouchListener {
	public static final String EXTRA_OUTPUT_PATH = "extra_output_path";

	private static final int MSG_HIDE_FOCUS = Integer.MAX_VALUE;

	private CameraSurfaceView mCameraSurfaceView;
	private ImageView mBack;
	private ImageView mSwitch;
	private ImageView mShutter;
	private ImageView mFlash;
	private ImageView mFocus;
	private ViewGroup mFocusLayout;

	private String mOutputPath;
	private boolean mIsFocusing;

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private SensorEventListener mSensorEventListener;
	private int mOrientation;

	@Override
	protected boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_HIDE_FOCUS:
			mFocus.setVisibility(View.GONE);
			mFocusLayout.scrollTo(0, 0);
			mIsFocusing = false;
			break;
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_camera);
		mOutputPath = getIntent().getStringExtra(EXTRA_OUTPUT_PATH);
		if (TextUtils.isEmpty(mOutputPath)) {
			finish();
			return;
		}
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		initView();
	}

	protected void initView() {
		mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.camera_surfaceview);
		mBack = (ImageView) findViewById(R.id.camera_back);
		mSwitch = (ImageView) findViewById(R.id.camera_switch);
		mShutter = (ImageView) findViewById(R.id.camera_shutter);
		mFlash = (ImageView) findViewById(R.id.camera_flash);
		mFocus = (ImageView) findViewById(R.id.camera_focus);
		mFocusLayout = (ViewGroup) findViewById(R.id.camera_focus_layout);

		refreshFlashMode();
		mSwitch.setVisibility(mCameraSurfaceView.canTakePictureFromFrontCamera() ? View.VISIBLE : View.GONE);
		mFocus.setVisibility(View.GONE);

		mCameraSurfaceView.setOnCameraStatusListener(this);
		mFocusLayout.setOnTouchListener(this);
		mBack.setOnClickListener(this);
		mSwitch.setOnClickListener(this);
		mShutter.setOnClickListener(this);
		mFlash.setOnClickListener(this);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		if (mSensor != null) {
			mSensorEventListener = new SensorEventListener() {

				@SuppressWarnings("deprecation")
				@Override
				public void onSensorChanged(SensorEvent event) {
					float x = event.values[SensorManager.DATA_X];
					float y = event.values[SensorManager.DATA_Y];
					mOrientation = SensorHelper.getOrientation(x, y);
				}

				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {

				}
			};
			mOrientation = SensorHelper.ORIENTATION_ROTATE_90;
		}
	}

	@Override
	protected void onPause() {
		if (mSensorManager != null && mSensor != null) {
			mSensorManager.unregisterListener(mSensorEventListener);
		}

		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mSensorManager != null && mSensor != null) {
			mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.camera_shutter) {
			mCameraSurfaceView.takePicture();
		}
		else if (id == R.id.camera_switch) {
			if (getHandler().hasMessages(MSG_HIDE_FOCUS)) {
				getHandler().removeMessages(MSG_HIDE_FOCUS);
			}
			mCameraSurfaceView.switchCamera();
			refreshFlashMode();
		}
		else if (id == R.id.camera_flash) {
			mCameraSurfaceView.switchFlashMode();
			refreshFlashMode();
		}
		else if (id == R.id.camera_back) {
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	@Override
	public void onCameraSwitching() {
		mSwitch.setEnabled(false);
		mShutter.setEnabled(false);
	}

	@Override
	public void onCameraSwitched() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mSwitch.setEnabled(true);
				mShutter.setEnabled(true);
			}
		});
	}

	@Override
	public void onCameraStopped(final byte[] data) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (mCameraSurfaceView.isFrontCamera()) {
					mOrientation = (mOrientation + 180) % 360;
				}

				FileOutputStream fos = null;
				Bitmap bitmap = null;
				try {
					fos = new FileOutputStream(mOutputPath);
					fos.write(data); // 原始数据写入，保留EXIF
					fos.close();

					// 因为页面使用的是portrait模式，而不是landscape模式，所以自动旋转了90度
					// 但是如三星等部分手机，不会在生成照片时旋转这90度，而是将其写入EXIF，所以
					// 需要读取EXIF来判断是否该旋转90度
					int degree = ImageUtil.readPictureDegree(mOutputPath);

					// 因为是portrait拍摄，所以在正方向，传感器默认旋转90度，而部分手机生成照片
					// 时已经旋转了，所以EXIF读取出来是0，无需旋转。需要旋转的是生成照片时没有
					// 自动旋转，而是写入EXIF的照片。如果在portrait模式下，旋转了手机拍摄，就
					// 旋转到在拍摄时旋转手机状态下预览到的样子
					int totalDegree = (degree + mOrientation - 90) % 360;

					if (totalDegree != 0) {
						bitmap = BitmapGenerator.decodeFile(mOutputPath);
						if (bitmap != null) {
							bitmap = ImageUtil.rotate(bitmap, totalDegree);
							StorageUtil.saveImage(bitmap, mOutputPath, CompressFormat.JPEG, 100, true);
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					StorageUtil.close(fos);
				}
				// 重置为0，因为前面已经纠正过图片方向，防止其他地方重复利用EXIF纠正图片方向
				ImageUtil.setPictureDegree(mOutputPath, 0);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						setResult(RESULT_OK);
						finish();
					}
				});
			}
		}).start();
	}

	@Override
	public void onFocusing() {
		if (mCameraSurfaceView.isFrontCamera()) {
			return;
		}
		mIsFocusing = true;
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (getHandler().hasMessages(MSG_HIDE_FOCUS)) {
					getHandler().removeMessages(MSG_HIDE_FOCUS);
				}

				mFocus.setVisibility(View.VISIBLE);
				mFocus.setSelected(false);
			}
		});
	}

	@Override
	public void onFocused(final boolean success) {
		if (mCameraSurfaceView.isFrontCamera()) {
			return;
		}
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mFocus.setVisibility(View.VISIBLE);
				mFocus.setSelected(success);
				getHandler().sendEmptyMessageDelayed(MSG_HIDE_FOCUS, 1000);
			}
		});
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN && !mIsFocusing && !mCameraSurfaceView.isFrontCamera()) {
			if (getHandler().hasMessages(MSG_HIDE_FOCUS)) {
				getHandler().removeMessages(MSG_HIDE_FOCUS);
			}
			Rect focusBounds = getFocusBounds();
			int x = (int) event.getX();
			int y = (int) event.getY();
			int centerX = focusBounds.centerX();
			int centerY = focusBounds.centerY();
			int xCheckResult = ViewHelper.checkOutOfBoundsX(focusBounds, x);
			int yCheckResult = ViewHelper.checkOutOfBoundsY(focusBounds, y);

			int finalX, finalY;
			if (xCheckResult == ViewHelper.OUT_OF_LEFT_BOUNDS) {
				finalX = centerX - mFocus.getWidth() / 2;
			}
			else if (xCheckResult == ViewHelper.OUT_OF_RIGHT_BOUNDS) {
				finalX = -(centerX - mFocus.getWidth() / 2);
			}
			else {
				finalX = centerX - x;
			}

			if (yCheckResult == ViewHelper.OUT_OF_TOP_BOUNDS) {
				finalY = centerY - mFocus.getHeight() / 2;
			}
			else if (yCheckResult == ViewHelper.OUT_OF_BOTTOM_BOUNDS) {
				finalY = -(centerY - mFocus.getHeight() / 2);
			}
			else {
				finalY = centerY - y;
			}

			v.scrollTo(finalX, finalY);
			mCameraSurfaceView.autoFocus();
		}
		return false;
	}

	private Rect getFocusBounds() {
		Rect focusBounds = new Rect();
		mFocusLayout.getLocalVisibleRect(focusBounds);
		int halfWidth = mFocus.getWidth() / 2;
		int halfHeight = mFocus.getHeight() / 2;
		focusBounds.left += halfWidth;
		focusBounds.top += halfHeight;
		focusBounds.right -= halfWidth;
		focusBounds.bottom -= halfHeight;

		return focusBounds;
	}

	private void refreshFlashMode() {
		String mode = LibSettings.getCameraFlashMode();
		if (Parameters.FLASH_MODE_ON.equals(mode)) {
			mFlash.setImageResource(R.drawable.icon_camera_flash_on);
		}
		else if (Parameters.FLASH_MODE_AUTO.equals(mode)) {
			mFlash.setImageResource(R.drawable.icon_camera_flash_auto);
		}
		else if (Parameters.FLASH_MODE_OFF.equals(mode)) {
			mFlash.setImageResource(R.drawable.icon_camera_flash_off);
		}
		else {
			mFlash.setImageResource(R.drawable.icon_camera_flash_auto);
		}

		mFlash.setVisibility(mCameraSurfaceView.isFrontCamera() ? View.GONE : View.VISIBLE);
	}

	public static void startActivityForResult(Activity activity, String outputPath, int requestCode) {
		Intent intent = new Intent(activity, CameraUI.class);
		intent.putExtra(EXTRA_OUTPUT_PATH, outputPath);
		activity.startActivityForResult(intent, requestCode);
	}

	public static void startActivityForResult(Fragment fragment, String outputPath, int requestCode) {
		Intent intent = new Intent(fragment.getActivity(), CameraUI.class);
		intent.putExtra(EXTRA_OUTPUT_PATH, outputPath);
		fragment.startActivityForResult(intent, requestCode);
	}

	@Override
	public void onError() {
		showToast(R.string.common_camera_error);
		finish();
	}
}
