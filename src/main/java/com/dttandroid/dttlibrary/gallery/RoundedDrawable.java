package com.dttandroid.dttlibrary.gallery;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午9:43:25
 * @Description:
 */
public class RoundedDrawable extends Drawable {
    private final float mCornerRadius;
    private final RectF mRect = new RectF();
    private RectF mBitmapRect = new RectF();
    private final BitmapShader mBitmapShader;
    private final Paint mPaint;
    private boolean mIsOval;

    public RoundedDrawable(Bitmap bitmap, boolean isOval, float cornerRadius) {
        mIsOval = isOval;
        mCornerRadius = cornerRadius;

        mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapRect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setShader(mBitmapShader);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mRect.set(0, 0, bounds.width(), bounds.height());
        
        // Resize the original bitmap to fit the new bound
        Matrix shaderMatrix = new Matrix();
        shaderMatrix.setRectToRect(mBitmapRect, mRect, Matrix.ScaleToFit.FILL);
        mBitmapShader.setLocalMatrix(shaderMatrix);
        
    }

    @Override
    public void draw(Canvas canvas) {
        if (mIsOval) {
            canvas.drawOval(mRect, mPaint);
        }
        else {
            canvas.drawRoundRect(mRect, mCornerRadius, mCornerRadius, mPaint);
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }
}

