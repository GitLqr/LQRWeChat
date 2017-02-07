package com.lqr.wechat.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.lqr.wechat.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * @创建者 CSDN_LQR
 * @描述 可以网络图片的ImageView
 */
public class NetworkImageView extends ImageView {

    private Paint mPaint;
    private Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private Bitmap mMaskBitmap;

    private static final int BODER_RADIUS_DEFAULT = 0;     //圆角默认大小值
    private int mBorderRadius;                  //圆角大小

    private String mUrl;

    public static final DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.mipmap.ic_launcher)//设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.mipmap.ic_launcher)  //设置图片加载/解码过程中错误时候显示的图片
            .cacheInMemory(true)//设置下载的图片是否缓存在内存中
            .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
            .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
            //.delayBeforeLoading(int delayInMillis)//int delayInMillis为你设置的下载前的延迟时间
            //设置图片加入缓存前，对bitmap进行设置
            //.preProcessor(BitmapProcessor preProcessor)
            .build();//构建完成

    public NetworkImageView(Context context) {
        super(context);
    }

    public NetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        //取出attrs中我们为View设置的相关值
        TypedArray tArray = context.obtainStyledAttributes(attrs, R.styleable.NetworkImageView);
        mBorderRadius = tArray.getDimensionPixelSize(R.styleable.NetworkImageView_radius, BODER_RADIUS_DEFAULT);
        tArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public NetworkImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageUrl(String url) {
        mUrl = url;
        ImageLoader.getInstance().displayImage(mUrl, this, defaultOptions);
        invalidate();
    }

    public void setImageUrl(String url, DisplayImageOptions options) {
        mUrl = url;
        if (options == null)
            ImageLoader.getInstance().displayImage(mUrl, this, defaultOptions);
        else
            ImageLoader.getInstance().displayImage(mUrl, this, options);
        invalidate();
    }

    public String getImageURL() {
        return mUrl;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBorderRadius == 0 || mPaint == null) {
            super.onDraw(canvas);
            return;
        }
        //在缓存中取出bitmap
        Bitmap bitmap = null;
        //获取图片宽高
        Drawable drawable = getDrawable();
        if (drawable != null) {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas drawCanvas = new Canvas(bitmap);
            float scale;
            scale = Math.max(getWidth() * 1.0f / width, getHeight()
                    * 1.0f / height);
            //根据缩放比例，设置bounds，相当于缩放图片了
            drawable.setBounds(0, 0, (int) (scale * width),
                    (int) (scale * height));

            drawable.draw(drawCanvas);
            if (mMaskBitmap == null || mMaskBitmap.isRecycled()) {
                mMaskBitmap = getBitmap();
            }

            mPaint.reset();
            mPaint.setFilterBitmap(false);
            mPaint.setXfermode(mXfermode);

            //绘制形状
            drawCanvas.drawBitmap(mMaskBitmap, 0, 0, mPaint);

            //绘制图片
            canvas.drawBitmap(bitmap, 0, 0, null);
            mPaint.setXfermode(null);
        }
        if (bitmap != null) {
            mPaint.setXfermode(null);
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, mPaint);
        }
    }

    //缓存Bitmap，避免每次OnDraw都重新分配内存与绘图
    @Override
    public void invalidate() {
        super.invalidate();
    }

    //定义一个绘制形状的方法
    private Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);   //抗锯齿
        paint.setColor(Color.BLACK);
        canvas.drawRoundRect(
                new RectF(
                        (bitmap.getWidth() - getWidth()) / 2, (bitmap.getHeight() - getHeight()) / 2,
                        getWidth() + (bitmap.getWidth() - getWidth()) / 2, getHeight() + (bitmap.getHeight() - getHeight()) / 2
                ),
                mBorderRadius, mBorderRadius, paint
        );
        return bitmap;
    }
}
