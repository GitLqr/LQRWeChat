package com.lqr.wechat.util;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

/**
 * @创建者 CSDN_LQR
 * @描述 视频缩略图加载工具
 */
public class VideoThumbLoader {
    private ImageView imgView;
    private String path;

    static VideoThumbLoader instance;

    public static VideoThumbLoader getInstance() {
        if (instance == null) {
            synchronized (VideoThumbLoader.class) {
                if (instance == null) {
                    instance = new VideoThumbLoader();
                }
            }
        }
        return instance;
    }

    // 创建cache
    private LruCache<String, Bitmap> lruCache;

    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {

            if (imgView.getTag().equals(path)) {
                Bitmap btp = (Bitmap) msg.obj;
                imgView.setImageBitmap(btp);
            }
        }
    };

    // @SuppressLint("NewApi")
    private VideoThumbLoader() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();// 获取最大的运行内存
        int maxSize = maxMemory / 8;
        lruCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                // 这个方法会在每次存入缓存的时候调用
                // return value.getByteCount();
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    private void addVideoThumbToCache(String path, Bitmap bitmap) {
        if (getVideoThumbToCache(path) == null && bitmap != null) {
            // 当前地址没有缓存时，就添加
            lruCache.put(path, bitmap);
        }
    }

    private Bitmap getVideoThumbToCache(String path) {

        return lruCache.get(path);

    }

    public void showThumb(String path, ImageView imgview, int width, int height) {

        if (getVideoThumbToCache(path) == null) {
            // 异步加载
            imgview.setTag(path);
            new MyBobAsynctack(imgview, path, width, height).execute(path);
        } else {
            imgview.setImageBitmap(getVideoThumbToCache(path));
        }

    }

    class MyBobAsynctack extends AsyncTask<String, Void, Bitmap> {
        private ImageView imgView;
        private String path;
        private int width;
        private int height;

        public MyBobAsynctack(ImageView imageView, String path, int width,
                              int height) {
            this.imgView = imageView;
            this.path = path;
            this.width = width;
            this.height = height;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = createVideoThumbnail(params[0], width, height,
                    MediaStore.Video.Thumbnails.MICRO_KIND);
            // 加入缓存中
            if (getVideoThumbToCache(params[0]) == null && bitmap != null) {
                addVideoThumbToCache(path, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imgView.getTag() != null && imgView.getTag().equals(path)) {
                imgView.setImageBitmap(bitmap);
            }
        }
    }

    private void showDateByThread(ImageView imageview, final String path,
                                  final int width, final int height) {
        imgView = imageview;
        this.path = path;
        new Thread(new Runnable() {

            @Override
            public void run() {
                Bitmap bitmap = createVideoThumbnail(path, width, height,
                        MediaStore.Video.Thumbnails.MICRO_KIND);
                Message msg = new Message();
                msg.obj = bitmap;
                msg.what = 1001;
                mHandler.sendMessage(msg);
            }
        }).start();

    }

    private static Bitmap createVideoThumbnail(String vidioPath, int width,
                                               int height, int kind) {
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(vidioPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
}
