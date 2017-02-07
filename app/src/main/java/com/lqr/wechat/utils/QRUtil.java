package com.lqr.wechat.utils;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

/**
 * @创建者 CSDN_LQR
 * @描述 条形码、二维码工具类
 */
public class QRUtil {

    private static final String CODE = "utf-8";
    private static final int BLACK = 0xff000000;
    private static final int WHITE = 0xFFFFFFFF;

    /**
     * 生成RQ二维码
     *
     * @param str    内容
     * @param height 高度（px）
     * @author wuhongbo
     */
    public static Bitmap getRQ(String str, Integer height) {
        if (height == null || height < 100) {
            height = 200;
        }

        try {
            // 文字编码
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, CODE);

            BitMatrix bitMatrix = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, height, height, hints);

            return toBufferedImage(bitMatrix);

            // 输出方式
            // 网页
            // ImageIO.write(image, "png", response.getOutputStream());

            // 文件
            // ImageIO.write(image, "png", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成一维码（128）
     *
     * @param str
     * @param width
     * @param height
     * @return
     * @author wuhongbo
     */
    public static Bitmap getBarcode(String str, Integer width,
                                    Integer height) {

        if (width == null || width < 200) {
            width = 200;
        }

        if (height == null || height < 50) {
            height = 50;
        }

        try {
            // 文字编码
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, CODE);

            BitMatrix bitMatrix = new MultiFormatWriter().encode(str,
                    BarcodeFormat.CODE_128, width, height, hints);

            return toBufferedImage(bitMatrix);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 转换成图片
     *
     * @param matrix
     * @return
     * @author wuhongbo
     */
    private static Bitmap toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return bitmap;
    }

}
