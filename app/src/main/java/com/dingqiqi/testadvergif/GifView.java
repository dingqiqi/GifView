package com.dingqiqi.testadvergif;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

/**
 * GIF图片播放控件
 *
 * @author 丁奇奇
 */
public class GifView extends View {
    private Context mContext;
    /**
     * 用于播放gif图片
     */
    private Movie mMovie;
    /**
     * 开始播放时间
     */
    private long mStartTime = 0;
    /**
     * 图片总时间
     */
    private int mAllTime = 2000;
    /**
     * 图片宽高
     */
    private int mWidth, mHeight;
    /**
     * 非gif图片资源
     */
    private Bitmap mBitmap;
    /**
     * gif图片相关
     */
    private Canvas mGifCanvas;
    private Bitmap mGifBitmap;
    private Matrix mMatrix;
    /**
     * 文字相关
     */
    private String mText;
    private Rect mTextBounds;
    private Paint mPaint;
    private int mTextSize;
    /**
     * 移动长度
     */
    private int mTransLength;

    private Handler mHandler=new Handler();

    public GifView(Context context) {
        super(context, null);
        mContext = context;
    }

    public GifView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    /**
     * 设置GIF图片资源
     *
     * @param id
     */
    public void setGifImage(int id) {
        mMovie = Movie.decodeStream(getResources().openRawResource(id));

        next();
    }

    /**
     * 设置GIF图片资源
     *
     * @param stream
     */
    public void setGifStream(InputStream stream) {
        mMovie = Movie.decodeStream(stream);

        next();
    }

    /**
     * gif图片初始化
     */
    public void next() {
        if (mMovie == null) {
            throw new IllegalArgumentException("请传正确的图片id");
        }

        mWidth = mMovie.width();
        mHeight = mMovie.height();

        if (mMovie != null) {
            mStartTime = 0;
            mAllTime = mMovie.duration();
            requestLayout();
        }
    }

    /**
     * 设置非GIF图片资源
     *
     * @param bitmap
     */
    public void setImage(Bitmap bitmap) {
        if (bitmap == null) {
            throw new IllegalArgumentException("资源bitmap不能为空!");
        }
        mBitmap = bitmap;
        invalidate();
    }

    /**
     * 设置文字
     *
     * @param text
     */
    public void setText(String text) {
        mText = text;

        mBitmap = null;
        if (mTextBounds == null) {
            mTextBounds = new Rect();

            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.BLACK);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setTextSize(dp2px(16));

            mPaint.getTextBounds(mText, 0, mText.length(), mTextBounds);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mMovie != null) {
            long time = System.currentTimeMillis();
            if (mStartTime == 0) {
                mStartTime = time;
            }
            int curIndex = (int) ((time - mStartTime) % mAllTime);
            mMovie.setTime(curIndex);

            createBitmap();
            float width = mGifBitmap.getWidth() * 1f;
            float height = mGifBitmap.getHeight() * 1f;
            //获取最小缩放倍数
            float scale = Math.min(getMeasuredWidth() / width, getMeasuredHeight() / height);
            if (mMatrix == null) {
                mMatrix = new Matrix();
            }
            mMatrix.postScale(scale, scale);
            canvas.drawBitmap(mGifBitmap, mMatrix, null);

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            },50);
        } else {
            // 不是gif图片
            if (mBitmap != null) {
                float width = mBitmap.getWidth() * 1f;
                float height = mBitmap.getHeight() * 1f;
                //获取最小缩放倍数
                float scale = Math.min(getMeasuredWidth() / width, getMeasuredHeight() / height);

                Matrix  matrix = new Matrix();
                matrix.postScale(scale, scale);
                //bitmap 缩放
                mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
                        mBitmap.getHeight(), matrix, true);

                canvas.drawBitmap(mBitmap, (getMeasuredWidth() - mBitmap.getWidth()) / 2, (getMeasuredHeight() - mBitmap.getHeight()) / 2, null);
            } else {
                //画文字
                // canvas.save();
                //canvas.translate(mTransLength, 0);
                canvas.drawText(mText, (getMeasuredWidth() - mTextBounds.width()) / 2, (getMeasuredHeight() + mTextBounds.height()) / 2, mPaint);
                // canvas.restore();
            }
        }
    }

    /**
     * 将动态图画在bitmap上，返回bitmap（用于缩放）
     *
     * @return
     */
    public Bitmap createBitmap() {
        if (mGifBitmap == null) {
            mGifBitmap = Bitmap.createBitmap(mMovie.width(), mMovie.height(), Bitmap.Config.ARGB_4444);
            mGifCanvas = new Canvas(mGifBitmap);
        }
        mMovie.draw(mGifCanvas, 0, 0);

        return mGifBitmap;
    }

    /**
     * dp 转px
     *
     * @param dp
     * @return
     */
    private int dp2px(int dp) {
        return (int) (mContext.getResources().getDisplayMetrics().density * dp + 0.5);
    }

}
