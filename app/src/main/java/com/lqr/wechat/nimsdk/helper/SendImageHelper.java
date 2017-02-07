package com.lqr.wechat.nimsdk.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.lqr.imagepicker.bean.ImageItem;
import com.lqr.wechat.nimsdk.utils.AttachmentStore;
import com.lqr.wechat.nimsdk.utils.ImageUtil;
import com.lqr.wechat.nimsdk.utils.StorageType;
import com.lqr.wechat.nimsdk.utils.StorageUtils;
import com.lqr.wechat.utils.FileUtils;
import com.lqr.wechat.utils.MD5Utils;
import com.lqr.wechat.utils.UIUtils;

import java.io.File;

/**
 * @创建者 CSDN_LQR
 * @描述 网易云信发送图片工具类
 */
public class SendImageHelper {

    public interface Callback {
        void sendImage(File file, boolean isOrig);
    }

    public static class SendImageTask extends AsyncTask<Void, Void, File> {

        private Context mContext;
        private boolean mIsOrig;
        private ImageItem mImageItem;
        private Callback mCallback;

        public SendImageTask(Context context, boolean isOrig, ImageItem imageItem, Callback callback) {
            mContext = context;
            mIsOrig = isOrig;
            mImageItem = imageItem;
            mCallback = callback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected File doInBackground(Void... params) {
            String path = mImageItem.path;
            if (TextUtils.isEmpty(path)) {
                return null;
            }

            if (mIsOrig) {
                // 把原图按md5存放
                String origMD5 = MD5Utils.decode32(path);
                String extension = FileUtils.getExtensionName(path);
                String origMD5Path = StorageUtils.getWritePath(origMD5 + "."
                        + extension, StorageType.TYPE_IMAGE);
                AttachmentStore.copy(path, origMD5Path);
                // 生成缩略图
                File imageFile = new File(origMD5Path);
                ImageUtil.makeThumbnail(mContext, imageFile);

                return new File(origMD5Path);
            } else {
                File imageFile = new File(path);
                String mimeType = FileUtils.getExtensionName(path);
                imageFile = ImageUtil.getScaledImageFileWithMD5(imageFile, mimeType);
                if (imageFile == null) {
                    UIUtils.postTaskSafely(new Runnable() {
                        @Override
                        public void run() {
                            UIUtils.showToast("获取图片出错");
                        }
                    });
                    return null;
                } else {
                    ImageUtil.makeThumbnail(mContext, imageFile);
                }
                return imageFile;
            }
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);

            if (file != null) {
                if (mCallback != null) {
                    mCallback.sendImage(file, mIsOrig);
                }
            }
        }
    }

}
