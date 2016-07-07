package com.dttandroid.dttlibrary.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Gallery;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午9:38:17
 * @Description:
 */
@SuppressWarnings("deprecation")
public class GalleryViewer extends Gallery {
	public interface OnSingleTapConfirmedListener {
		public void onTapped();
	}

	private final int SCREEN_WIDTH = getResources().getDisplayMetrics().widthPixels;
	private final int SCREEN_HEIGHT = getResources().getDisplayMetrics().heightPixels;

	private GestureDetector mGestureDetector;
	private GalleryImageView mImageView;
	private OnSingleTapConfirmedListener mOnSingleTapConfirmedCallback;

	private PointF mStartPt = new PointF();

	public GalleryViewer(Context context) {
		super(context);
	}

	public GalleryViewer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public GalleryViewer(Context context, AttributeSet attrs) {
		super(context, attrs);

		mGestureDetector = new GestureDetector(new SimpleGestureImpl());
		this.setOnTouchListener(new OnTouchListener() {

			float baseValue;
			float originalScale;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				View view = GalleryViewer.this.getSelectedView();
				if (view instanceof GalleryImageView) {
					mImageView = (GalleryImageView) view;

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						baseValue = 0;
						originalScale = mImageView.getScale();
						mStartPt.set(event.getX(), event.getY());
					}
					if (event.getAction() == MotionEvent.ACTION_MOVE) {
						if (event.getPointerCount() == 2) {
							float x = event.getX(0) - event.getX(1);
							float y = event.getY(0) - event.getY(1);
							float value = (float) Math.sqrt(x * x + y * y);// 计算两点的距离
							// System.out.println("value:" + value);
							if (baseValue == 0) {
								baseValue = value;
							}
							else {
								float scale = value / baseValue;// 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
								// scale the image
								mImageView.zoomTo(originalScale * scale, x + event.getX(1), y + event.getY(1));

							}
						}
					}
				}
				return false;
			}

		});
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		View view = GalleryViewer.this.getSelectedView();
		if (view instanceof GalleryImageView) {
			mImageView = (GalleryImageView) view;

			float v[] = new float[9];
			Matrix m = mImageView.getImageMatrix();
			m.getValues(v);
			// 图片实时的上下左右坐标
			float left, right;
			// 图片的实时宽，高
			float width, height;
			width = mImageView.getScale() * mImageView.getImageWidth();
			height = mImageView.getScale() * mImageView.getImageHeight();
			// 一下逻辑为移动图片和滑动gallery换屏的逻辑。如果没对整个框架了解的非常清晰，改动以下的代码前请三思！！！！！！
			if ((int) width <= SCREEN_WIDTH && (int) height <= SCREEN_HEIGHT)// 如果图片当前大小<屏幕大小，直接处理滑屏事件
			{
				super.onScroll(e1, e2, distanceX, distanceY);
			}
			else {
				left = v[Matrix.MTRANS_X];
				right = left + width;
				Rect r = new Rect();
				mImageView.getGlobalVisibleRect(r);

				if (distanceX > 0)// 向左滑动
				{
					if (r.left > 0) {// 判断当前ImageView是否显示完全
						super.onScroll(e1, e2, distanceX, distanceY);
					}
					else if (right < SCREEN_WIDTH) {
						super.onScroll(e1, e2, distanceX, distanceY);
					}
					else {
						mImageView.postTranslate(-distanceX, -distanceY);
					}
				}
				else if (distanceX < 0)// 向右滑动
				{
					if (r.right < SCREEN_WIDTH) {
						super.onScroll(e1, e2, distanceX, distanceY);
					}
					else if (left > 0) {
						super.onScroll(e1, e2, distanceX, distanceY);
					}
					else {
						mImageView.postTranslate(-distanceX, -distanceY);
					}
				}

			}

		}
		else {
			super.onScroll(e1, e2, distanceX, distanceY);
		}
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		int kEvent;
		if (isScrollingLeft(e1, e2)) {
			kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
		}
		else {
			kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		onKeyDown(kEvent, null);
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		/*switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			// 判断上下边界是否越界
			View view = GalleryViewer.this.getSelectedView();
			if (view instanceof GalleryViewerImage) {
				imageView = (GalleryViewerImage) view;
				float width = imageView.getScale() * imageView.getImageWidth();
				float height = imageView.getScale() * imageView.getImageHeight();
				if ((int) width <= SCREEN_WIDTH && (int) height <= SCREEN_HEIGHT)// 如果图片当前大小<屏幕大小，判断边界
				{
					break;
				}
				float v[] = new float[9];
				Matrix m = imageView.getImageMatrix();
				m.getValues(v);
				float top = v[Matrix.MTRANS_Y];
				float bottom = top + height;
				Log.v("lufengwen", "GalleryView onTouchEvent ACTION_UP-> top:" + top + " bottom:" + bottom);
				if (top > 0) {
					imageView.postTranslateDur(-top, 200f);
				}
				if (bottom < SCREEN_HEIGHT) {
					imageView.postTranslateDur(SCREEN_HEIGHT - bottom, 200f);
				}

			}
			break;
		}*/
		return super.onTouchEvent(event);
	}

	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
		return e2.getX() > e1.getX();
	}

	public void setOnSingleTapConfirmedListener(OnSingleTapConfirmedListener callback) {
		mOnSingleTapConfirmedCallback = callback;
	}

	private class SimpleGestureImpl extends SimpleOnGestureListener {
		// 按两下的第二下Touch down时触发
		public boolean onDoubleTap(MotionEvent e) {
			View view = GalleryViewer.this.getSelectedView();
			if (view instanceof GalleryImageView) {
				mImageView = (GalleryImageView) view;
				if (mImageView.getScale() > mImageView.getScaleRate()) {
					mImageView.zoomTo(mImageView.getScaleRate(), SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2, 200f);
					mImageView.center(true, true);
					// imageView.layoutToCenter();
				}
				else {
					mImageView.zoomTo(1.0f, SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2, 200f);
				}

			}
			else {

			}
			// return super.onDoubleTap(e);
			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (mOnSingleTapConfirmedCallback != null) {
				mOnSingleTapConfirmedCallback.onTapped();
			}
			return super.onSingleTapConfirmed(e);
		}
	}
}
