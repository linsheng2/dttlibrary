package com.dttandroid.dttlibrary.graphics;

import android.content.res.Resources;
import android.graphics.Bitmap;

/**
 * @Author: lufengwen
 * @Date: 2015年6月19日 上午9:14:38
 * @Description:
 */
public class ImageOptions {
    private boolean mIsRounded;
    private RoundedType mRoundedType;
    private float mRoundedRadius;
    private float mRoundedBorderWidth;
    private int mRoundedBorderColor;
    private boolean mIsBackground;
    private Bitmap mImageOnLoading;
    private Bitmap mImageOnFail;
    private Bitmap mCachedImageOnLoading;
    private Bitmap mCachedImageOnFail;
    private int mImageResOnLoading;
    private int mImageResOnFail;
    private boolean mIsGrayscale;
    private boolean mIsIgnoreImageView;
    private boolean mIsBlur;
    private boolean mIsResetView;
    private int mBlurRadius;
    private Runnable mOnLoadBegin;
    private Runnable mOnLoadEnd;
    
    private boolean mIsProcessAsync;
    private OnProcessingListener mOnProcessing;

    private OnHandlerImage mOnHandlerImage;

    public ImageOptions(Builder builder) {
        mIsRounded = builder.isRounded;
        mRoundedType = builder.roundedType;
        mRoundedRadius = builder.roundedRadius;
        mRoundedBorderWidth = builder.roundedBorderWidth;
        mRoundedBorderColor = builder.roundedBorderColor;
        mIsBackground = builder.isBackground;
        mImageOnLoading = builder.imageOnLoading;
        mImageOnFail = builder.imageOnFail;
        mImageResOnLoading = builder.imageResOnLoading;
        mImageResOnFail = builder.imageResOnFail;
        mIsGrayscale = builder.isGrayscale;
        mIsIgnoreImageView = builder.isIgnoreImageView;
        mIsBlur = builder.isBlur;
        mBlurRadius = builder.blurRadius;
        mIsResetView = builder.isResetView;
        mOnLoadBegin = builder.onLoadBegin;
        mOnLoadEnd = builder.onLoadEnd;
        mIsProcessAsync = builder.isProcessAsync;
        mOnProcessing = builder.onProcessing;
        mOnHandlerImage = builder.onHandlerImage;
        if (mIsRounded && mRoundedType == null) {
            mRoundedType = RoundedType.Full;
        }
    }

    public boolean isRounded() {
        return mIsRounded;
    }
    
    public RoundedType getRoundedType() {
        return mRoundedType;
    }
    
    public float getRoundedRadius() {
        return mRoundedRadius;
    }
    
    public float getRoundedBorderWidth() {
        return mRoundedBorderWidth;
    }
    
    public int getRoundedBorderColor() {
        return mRoundedBorderColor;
    }
    
    public boolean isBackground() {
        return mIsBackground;
    }
    
    public boolean isGrayscale() {
        return mIsGrayscale;
    }
    
    public Bitmap getImageOnLoading(Resources res) {
        if (mCachedImageOnLoading == null) {
            Bitmap bitmap = mImageResOnLoading != 0 ? BitmapGenerator.decodeResource(res, mImageResOnLoading) : mImageOnLoading;
            mCachedImageOnLoading = ImageHelper.roundedIfNeeded(res, bitmap, this);
        }
        
        return mCachedImageOnLoading;
    }
    
    public Bitmap getImageOnFail(Resources res) {
        if (mCachedImageOnFail == null) {
            Bitmap bitmap = mImageResOnFail != 0 ? BitmapGenerator.decodeResource(res, mImageResOnFail) : mImageOnFail;
            mCachedImageOnFail = ImageHelper.roundedIfNeeded(res, bitmap, this);
        }
        
        return mCachedImageOnFail;
    }
    
    public boolean isIgnoreImageView() {
        return mIsIgnoreImageView;
    }
    
    public boolean isBlur() {
        return mIsBlur;
    }
    
    public boolean isResetView() {
        return mIsResetView;
    }
    
    public int getBlurRadius() {
        return mBlurRadius;
    }
    
    public Runnable onLoadBegin() {
        return mOnLoadBegin;
    }
    
    public Runnable onLoadEnd() {
        return mOnLoadEnd;
    }
    
    public boolean isProcessAsync() {
        return mIsProcessAsync;
    }
    
    public OnProcessingListener getOnProcessingListener() {
        return mOnProcessing;
    }

    public OnHandlerImage getOnHandlerImage() {
        return mOnHandlerImage;
    }

    public static class Builder{
        private boolean isRounded;
        private RoundedType roundedType;
        private float roundedRadius;
        private float roundedBorderWidth;
        private int roundedBorderColor;
        private boolean isBackground;
        private Bitmap imageOnLoading;
        private Bitmap imageOnFail;
        private int imageResOnLoading;
        private int imageResOnFail;
        private boolean isGrayscale;
        private boolean isIgnoreImageView;
        private boolean isBlur;
        private boolean isResetView = true;
        private int blurRadius;
        private Runnable onLoadBegin;
        private Runnable onLoadEnd;
        private OnHandlerImage  onHandlerImage;
        
        private boolean isProcessAsync;
        private OnProcessingListener onProcessing;
        
        public void isRounded(boolean isRounded) {
            this.isRounded = isRounded;
        }
        
        public void RoundedType(RoundedType roundedType) {
            this.roundedType = roundedType;
        }
        
        public void RoundedRadius(float roundedRadius) {
            this.roundedRadius = roundedRadius;
        }
        
        public void RoundedBorderWidth(float roundedBorderWidth) {
            this.roundedBorderWidth = roundedBorderWidth;
        }
        
        public void RoundedBorderColor(int roundedBorderColor) {
            this.roundedBorderColor = roundedBorderColor;
        }
        
        public Builder isBackground(boolean isBackground) {
            this.isBackground = isBackground;
            return this;
        }
        
        public void isGrayscale(boolean isGrayscale) {
            this.isGrayscale = isGrayscale;
        }
        
        public void showImageOnLoading(Bitmap bitmap) {
            this.imageOnLoading = bitmap;
        }
        
        public void showImageOnFail(Bitmap bitmap) {
            this.imageOnFail = bitmap;
        }
        
        public void showImageOnLoading(int imageRes) {
            this.imageResOnLoading = imageRes;
        }
        
        public void showImageOnFail(int imageRes) {
            this.imageResOnFail = imageRes;
        }
        
        public void isIgnoreImageView(boolean isIgnore) {
            this.isIgnoreImageView = isIgnore;
        }
        
        public void isBlur(boolean isBlur) {
            this.isBlur = isBlur;
        }
        
        public void isResetView(boolean isResetView) {
            this.isResetView = isResetView;
        }
        
        public void blurRadius(int blurRadius) {
            this.blurRadius = blurRadius;
        }
        
        public void onLoadBegin(Runnable runnable) {
            this.onLoadBegin = runnable;
        }
        
        public void onLoadEnd(Runnable runnable) {
            this.onLoadEnd = runnable;
        }
        
        public void isProcessAsync(boolean isProcessAsync) {
            this.isProcessAsync = isProcessAsync;
        }
        
        public void onProcessing(OnProcessingListener listener) {
            this.onProcessing = listener;
        }
        public void onHandler(OnHandlerImage handler) {
            this.onHandlerImage = handler;
        }


        public ImageOptions build() {
            return new ImageOptions(this);
        }
    }
    
    public static enum RoundedType {
        Full, Corner
    }
    
    public interface OnProcessingListener {
        public Bitmap onProcessing(Bitmap srcBitmap);
    }

    public interface  OnHandlerImage {

        Bitmap onHandler(Bitmap srcBitmap);

    }
}

