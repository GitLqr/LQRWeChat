package com.lqr.wechat.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import com.lqr.wechat.R;
import com.lqr.wechat.utils.Bimp;
import com.lqr.wechat.utils.SDCardUtils;
import com.lqr.wechat.utils.StringUtils;
import com.lqr.wechat.view.ZoomImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.UUID;

/**
 * @创建者 CSDN_LQR
 * @描述 查看图片的Activity
 * <p>
 * 使用：
 * Intent intent = new Intent(getActivity(), PhotoActivity.class);
 * intent.putExtra("flag", 2);//1是拍照，2是从相册选取图片
 * intent.putExtra("noZoom", 1);//不压缩
 * intent.putExtra("noSurePic", 1);//在选取完图片后，不需要确定图片
 * getActivity().startActivityForResult(intent, MainActivity.SELECT_BAR_CODE_PHOTO);
 */
public class PhotoActivity extends Activity {

    private View cancelBtn;
    private View sendBtn;
    private ZoomImageView img;

    private static final int TAKE_PHOTO = 110;
    private static final int SELECT_PHOTO = 111;
    private static final int CUT_PHOTO_REQUEST_CODE = 112;

    int flag = 0;
    int noZoom = 0;//0是要zoom，1是不zoom
    int noSurePic = 0;//0是要显示确定图片，1是不需要显示确定图片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        initView();

        flag = getIntent().getIntExtra("flag", 0);
        noZoom = getIntent().getIntExtra("noZoom", 0);
        noSurePic = getIntent().getIntExtra("noSurePic", 0);

        if (flag == 0) {
            String imgUrl = getIntent().getStringExtra("imgUrl");
            if (!StringUtils.isEmpty(imgUrl)) {
                ImageLoader.getInstance().displayImage(imgUrl, img);
            } else {
                this.finish();
            }
        } else if (flag == 1) {
            photo();
        } else if (flag == 2) {
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, SELECT_PHOTO);
        }

    }

    private void initView() {
        cancelBtn = findViewById(R.id.photo_cancel);
        sendBtn = findViewById(R.id.photo_send);
        img = (ZoomImageView) findViewById(R.id.photo_img);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoActivity.this.finish();
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPic();
            }
        });
    }

    /**
     * 选取好图片后发送图片
     */
    private void sendPic() {
        Bundle b = new Bundle();
        b.putString("imgPath", filePath);
        Intent result = new Intent();
        result.putExtras(b);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && (null != data || requestCode == TAKE_PHOTO)) {
            switch (requestCode) {//拍照返回
                case TAKE_PHOTO:
                    if (photoUri != null) {
                        if (noZoom == 0) {
                            startPhotoZoom(photoUri);
                        } else {
                            filePath = getRealFilePath(PhotoActivity.this, photoUri);
                            if (noSurePic == 0) {
                                Bitmap bitmap = Bimp.zoomForFilePath(PhotoActivity.this, filePath);
                                img.setImageBitmap(bitmap);
                            } else {
                                sendPic();
                            }

                        }
                    } else {
                        PhotoActivity.this.finish();
                    }
                    break;
                case SELECT_PHOTO:// 相册返回
                    Uri uri = data.getData();
                    if (uri != null) {
                        if (noZoom == 0) {
                            startPhotoZoom(uri);
                        } else {
                            filePath = getRealFilePath(PhotoActivity.this, uri);
                            if (noSurePic == 0) {
                                Bitmap bitmap = Bimp.zoomForFilePath(PhotoActivity.this, filePath);
                                img.setImageBitmap(bitmap);
                            } else {
                                sendPic();
                            }

                        }
                    } else {
                        PhotoActivity.this.finish();
                    }
                    break;
                case CUT_PHOTO_REQUEST_CODE:// 裁剪返回
                    Bitmap bitmap = Bimp.zoomForFilePath(PhotoActivity.this, filePath);
                    img.setImageBitmap(bitmap);
                    break;
            }
        } else {
            PhotoActivity.this.finish();
        }
    }

    private String filePath;

    private void startPhotoZoom(Uri uri) {
        try {
            String address = UUID.randomUUID() + "";
            File destDir = new File(SDCardUtils.getSDCardPath() + "/CSDN_LQR/img");
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
//            Toast.makeText(PhotoActivity.this, uri.getPath(), Toast.LENGTH_LONG).show();
            Uri imageUri = Uri.parse("file:///sdcard/CSDN_LQR/img/" + address + ".jpg");
            filePath = imageUri.getPath();

            final Intent intent = new Intent("com.android.camera.action.CROP");

            // 照片URL地址
            intent.setDataAndType(uri, "image/*");

            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 720);
            intent.putExtra("outputY", 720);
            // 输出路径
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            // 输出格式
            intent.putExtra("outputFormat",
                    Bitmap.CompressFormat.JPEG.toString());
            // 不启用人脸识别
            intent.putExtra("noFaceDetection", false);
            intent.putExtra("return-data", false);
            startActivityForResult(intent, CUT_PHOTO_REQUEST_CODE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String path;
    private Uri photoUri;

    public void photo() {
        Intent openCameraIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);

        String sdcardState = Environment.getExternalStorageState();
        String sdcardPathDir = Environment
                .getExternalStorageDirectory().getPath() + "/CSDN_LQR/img/";
        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
            File fileDir = new File(sdcardPathDir);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            file = new File(sdcardPathDir + System.currentTimeMillis() + ".jpg");
        }
        if (file != null) {
            path = file.getPath();
//            photoUri = Uri.fromFile(file);
            photoUri = Uri.parse("file://"+file.getAbsolutePath());
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

            startActivityForResult(openCameraIntent, TAKE_PHOTO);
        }
    }

    /**
     * 得到图片文件的真实路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

}
