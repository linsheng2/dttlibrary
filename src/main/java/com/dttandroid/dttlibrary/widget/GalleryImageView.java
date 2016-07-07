package com.dttandroid.dttlibrary.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;

import com.dttandroid.dttlibrary.debug.AppLogger;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午9:13:19
 * @Description:
 */
@SuppressWarnings("deprecation")
public class GalleryImageView extends ImageView {
    protected Matrix mBaseMatrix = new Matrix();
    protected Matrix mSuppMatrix = new Matrix();
    private final Matrix mDisplayMatrix = new Matrix();
    private final float[] mMatrixValues = new float[9];
    protected Bitmap mBitmap = null;

    private int mThisWidth = -1;
    private int mThisHeight = -1;

    private float mMaxZoom = 5.0f;// 最大缩放比例
    private float mMinZoom = 0.0f;// 最小缩放比例

    private int mImageWidth;// 图片的原始宽度
    private int mImageHeight;// 图片的原始高度
    private float mScaleRate;// 图片适应屏幕的缩放比例
    private int SCREEN_WIDTH = getResources().getDisplayMetrics().widthPixels;
    private int SCREEN_HEIGHT = getResources().getDisplayMetrics().heightPixels;

    public GalleryImageView(Context context) {
        super(context);
        init();
    }

    public GalleryImageView(Context context, int m_imageWidth, int m_imageHeight) {
        super(context);
        this.mImageHeight = m_imageHeight;
        this.mImageWidth = m_imageWidth;
        init();
    }

    public GalleryImageView(Context context, AttributeSet attrs, int m_imageWidth, int m_imageHeight) {
        super(context, attrs);
        this.mImageHeight = m_imageHeight;
        this.mImageWidth = m_imageWidth;
        init();
    }

    /**
     * 计算图片要适应屏幕需要缩放的比例
     */
    private void arithm_scaleRate() {
        float scaleWidth = SCREEN_WIDTH / (float) mImageWidth;
        float scaleHeight = SCREEN_HEIGHT / (float) mImageHeight;
        mScaleRate = Math.min(scaleWidth, scaleHeight);
    }

    public float getScaleRate() {
        return mScaleRate;
    }

    public int getImageWidth() {
        return mImageWidth;
    }

    public void setImageWidth(int m_imageWidth) {
        this.mImageWidth = m_imageWidth;
    }

    public int getImageHeight() {
        return mImageHeight;
    }

    public void setImageHeight(int m_imageHeight) {
        this.mImageHeight = m_imageHeight;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking() && !event.isCanceled()) {
            if (getScale() > 1.0f) {
                zoomTo(1.0f);
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    protected Handler mHandler = new Handler();

    @Override
    public void setImageDrawable(Drawable drawable) {
        BitmapDrawable dr = (BitmapDrawable) drawable;
        
        if (dr != null && dr.getBitmap() != null) {
            mBitmap = dr.getBitmap();
            if (mImageWidth == 0 || mImageHeight == 0) {
                mImageWidth = mBitmap.getWidth();
                mImageHeight = mBitmap.getHeight();
                init();
            }

            AppLogger.d("lufengwen", "w:" + mImageWidth + " h:" + mImageHeight, false);
            
            // 计算适应屏幕的比例
            arithm_scaleRate();
            // 缩放到屏幕大小
            zoomTo(mScaleRate, SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f);
        }
        
        super.setImageDrawable(drawable);
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            mBitmap = bitmap;
            if (mImageWidth == 0 || mImageHeight == 0) {
                mImageWidth = bitmap.getWidth();
                mImageHeight = bitmap.getHeight();
                init();
            }

            // 计算适应屏幕的比例
            arithm_scaleRate();
            // 缩放到屏幕大小
            zoomTo(mScaleRate, SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f);
        }

        super.setImageBitmap(bitmap);
    }

    protected void center(boolean horizontal, boolean vertical) {
        if (mBitmap == null) {
            return;
        }

        Matrix m = getImageViewMatrix();

        RectF rect = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        // RectF rect = new RectF(0, 0, m_imageWidth*getScale(),
        // m_imageHeight*getScale());

        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        if (vertical) {
            int viewHeight = SCREEN_HEIGHT;
            if (height < viewHeight) {
                deltaY = (viewHeight - height) / 2 - rect.top;
            }
            else if (rect.top > 0) {
                deltaY = -rect.top;
            }
            else if (rect.bottom < viewHeight) {
                deltaY = viewHeight - rect.bottom;
            }
        }

        if (horizontal) {
            int viewWidth = SCREEN_WIDTH;
            if (width < viewWidth) {
                deltaX = (viewWidth - width) / 2 - rect.left;
            }
            else if (rect.left > 0) {
                deltaX = -rect.left;
            }
            else if (rect.right < viewWidth) {
                deltaX = viewWidth - rect.right;
            }
        }

        postTranslate(deltaX, deltaY);
    }

    private void init() {
        setScaleType(ScaleType.MATRIX);
        setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }

    /**
     * 设置图片居中显示
     */
    public void layoutToCenter() {
        // 正在显示的图片实际宽高
        float width = mImageWidth * getScale();
        float height = mImageHeight * getScale();

        // 空白区域宽高
        float fill_width = SCREEN_WIDTH - width;
        float fill_height = SCREEN_HEIGHT - height;

        // 需要移动的距离
        float tran_width = 0f;
        float tran_height = 0f;

        if (fill_width > 0)
            tran_width = fill_width / 2;
        if (fill_height > 0)
            tran_height = fill_height / 2;

        postTranslate(tran_width, tran_height);
    }

    protected float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        mMinZoom = (SCREEN_WIDTH / 2f) / mImageWidth;

        return mMatrixValues[whichValue];
    }

    protected float getScale(Matrix matrix) {
        return getValue(matrix, Matrix.MSCALE_X);
    }

    protected float getScale() {
        return getScale(mSuppMatrix);
    }

    protected Matrix getImageViewMatrix() {
        mDisplayMatrix.set(mBaseMatrix);
        mDisplayMatrix.postConcat(mSuppMatrix);
        return mDisplayMatrix;
    }

    static final float SCALE_RATE = 1.25F;

    protected float maxZoom() {
        if (mBitmap == null) {
            return 1F;
        }

        float fw = (float) mBitmap.getWidth() / (float) mThisWidth;
        float fh = (float) mBitmap.getHeight() / (float) mThisHeight;
        float max = Math.max(fw, fh) * 4;
        return max;
    }

    protected void zoomTo(float scale, float centerX, float centerY) {
        float oldScale = getScale();

        if (scale > mMaxZoom) {
            scale = mMaxZoom;
        }
        else if (scale < mMinZoom) {
            scale = mMinZoom;
        }

        if (Float.isNaN(scale) || Float.isInfinite(scale)) {
            return;
        }

        float deltaScale = scale / oldScale;

        mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
        center(true, true);
    }

    protected void zoomTo(final float scale, final float centerX, final float centerY,
            final float durationMs) {
        final float incrementPerMs = (scale - getScale()) / durationMs;
        final float oldScale = getScale();
        final long startTime = System.currentTimeMillis();

        mHandler.post(new Runnable() {
            public void run() {
                long now = System.currentTimeMillis();
                float currentMs = Math.min(durationMs, now - startTime);
                float target = oldScale + (incrementPerMs * currentMs);
                zoomTo(target, centerX, centerY);
                if (currentMs < durationMs) {
                    mHandler.post(this);
                }
            }
        });
    }

    protected void zoomTo(float scale) {
        float cx = getWidth() / 2F;
        float cy = getHeight() / 2F;

        zoomTo(scale, cx, cy);
    }

    // protected void zoomToPoint(float scale, float pointX, float pointY)
    // {
    // float cx = getWidth() / 2F;
    // float cy = getHeight() / 2F;
    //
    // panBy(cx - pointX, cy - pointY);
    // zoomTo(scale, cx, cy, true);
    // }

    protected void zoomIn() {
        zoomIn(SCALE_RATE);
    }

    protected void zoomOut() {
        zoomOut(SCALE_RATE);
    }

    protected void zoomIn(float rate) {
        if (getScale() >= mMaxZoom) {
            return; // Don't let the user zoom into the molecular level.
        }
        else if (getScale() <= mMinZoom) {
            return;
        }
        if (mBitmap == null) {
            return;
        }

        float cx = getWidth() / 2F;
        float cy = getHeight() / 2F;

        mSuppMatrix.postScale(rate, rate, cx, cy);
        setImageMatrix(getImageViewMatrix());
    }

    protected void zoomOut(float rate) {
        if (mBitmap == null) {
            return;
        }

        float cx = getWidth() / 2F;
        float cy = getHeight() / 2F;

        Matrix tmp = new Matrix(mSuppMatrix);
        tmp.postScale(1F / rate, 1F / rate, cx, cy);

        if (getScale(tmp) < 1F) {
            mSuppMatrix.setScale(1F, 1F, cx, cy);
        }
        else {
            mSuppMatrix.postScale(1F / rate, 1F / rate, cx, cy);
        }
        center(true, true);
        setImageMatrix(getImageViewMatrix());
    }

    public void postTranslate(float dx, float dy) {
        mSuppMatrix.postTranslate(dx, dy);
        setImageMatrix(getImageViewMatrix());
    }

    float _dy = 0.0f;

    protected void postTranslateDur(final float dy, final float durationMs) {
        _dy = 0.0f;
        final float incrementPerMs = dy / durationMs;
        final long startTime = System.currentTimeMillis();
        mHandler.post(new Runnable() {
            public void run() {
                long now = System.currentTimeMillis();
                float currentMs = Math.min(durationMs, now - startTime);

                postTranslate(0, incrementPerMs * currentMs - _dy);
                // setImageMatrix(getImageViewMatrix());
                _dy = incrementPerMs * currentMs;

                if (currentMs < durationMs) {
                    mHandler.post(this);
                }
            }
        });
    }

    // protected void panBy(float dx, float dy)
    // {
    // postTranslate(dx, dy);
    // setImageMatrix(getImageViewMatrix());
    // }
}
