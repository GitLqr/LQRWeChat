package com.lqr.wechat.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import io.rong.imlib.model.UserInfo;

/**
 * Created by AMing on 16/4/6.
 * Company RongCloud
 */
public class RongGenerate {

    //    private static String SAVEADDRESS = "/data/data/cn.rongcloud.im/temp";
    private static String SAVEADDRESS = UIUtils.getContext().getFilesDir().getPath();// /data/data/<application package>/files
    private static final String SCHEMA = "file://";


    public static String generateDefaultAvatar(String username, String userid) {

        String s = null;
        if (!TextUtils.isEmpty(username)) {
            s = String.valueOf(username.charAt(0));
        }
        if (s == null) {
            s = "A";
        }
        String color = getColorRGB(userid);
        String string = getAllFirstLetter(username);
        createDir(SAVEADDRESS);
        File f = new File(SAVEADDRESS, string + "_" + userid);
        if (f.exists()) {
            return SCHEMA + f.getPath();
        }
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(220);
        paint.setAntiAlias(true);
        int width = 480;
        int height = 480;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.parseColor(color));
        Rect rect = new Rect();
        paint.getTextBounds(s, 0, s.length(), rect);
        Paint.FontMetrics fm = paint.getFontMetrics();
        int textLeft = (int) ((width - paint.measureText(s)) / 2);
        int textTop = (int) (height - width / 2 + Math.abs(fm.ascent) / 2 - 25);
        canvas.drawText(s, textLeft, textTop, paint);
        return saveBitmap(bitmap, string + "_" + userid);
    }

    public static String generateDefaultAvatar(UserInfo userInfo) {
        if (userInfo == null)
            return null;
        else
            return generateDefaultAvatar(userInfo.getName(), userInfo.getUserId());
    }

    private static void createDir(String saveaddress) {
        boolean b;
        String status = Environment.getExternalStorageState();
        b = status.equals(Environment.MEDIA_MOUNTED);
        if (b) {
            File destDir = new File(saveaddress);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
        }
    }

    private static String saveBitmap(Bitmap bm, String imageUrlName) {
        File f = new File(SAVEADDRESS, imageUrlName);
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return SCHEMA + f.getPath();
    }

    private static String getColorRGB(String userId) {
        String[] portraitColors = {"#e97ffb", "#00b8d4", "#82b2ff", "#f3db73", "#f0857c"};
        if (TextUtils.isEmpty(userId)) {
            return portraitColors[0];
        }
        int i = getAscii(userId.charAt(0)) % 5;

        return portraitColors[i];
    }


    private static int getAscii(char cn) {
        byte[] bytes = (String.valueOf(cn)).getBytes();
        if (bytes.length == 1) { //单字节字符
            return bytes[0];
        } else if (bytes.length == 2) { //双字节字符
            int hightByte = 256 + bytes[0];
            int lowByte = 256 + bytes[1];
            int ascii = (256 * hightByte + lowByte) - 256 * 256;
            return ascii;
        } else {
            return 0; //错误
        }
    }

    /**
     * 生成 view 的截图
     *
     * @param view
     * @return
     */
    public static Bitmap takeScreenShot(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return view.getDrawingCache();
    }


    private final static int[] li_SecPosValue = {1601, 1637, 1833, 2078, 2274,
            2302, 2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858,
            4027, 4086, 4390, 4558, 4684, 4925, 5249, 5590
    };
    private final static String[] lc_FirstLetter = {"a", "b", "c", "d", "e",
            "f", "g", "h", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "w", "x", "y", "z"
    };

    /**
     * 取得给定汉字串的首字母串,即声母串
     *
     * @param str 给定汉字串
     * @return 声母串
     */
    private static String getAllFirstLetter(String str) {
        if (str == null || str.trim().length() == 0) {
            return "";
        }

        String _str = "";
        for (int i = 0; i < str.length(); i++) {
            _str = _str + getFirstLetter(str.substring(i, i + 1));
        }

        return _str;
    }

    /**
     * 取得给定汉字的首字母,即声母
     *
     * @param chinese 给定的汉字
     * @return 给定汉字的声母
     */
    private static String getFirstLetter(String chinese) {
        if (chinese == null || chinese.trim().length() == 0) {
            return "";
        }
        chinese = conversionStr(chinese, "GB2312", "ISO8859-1");

        if (chinese.length() > 1) // 判断是不是汉字
        {
            int li_SectorCode = (int) chinese.charAt(0); // 汉字区码
            int li_PositionCode = (int) chinese.charAt(1); // 汉字位码
            li_SectorCode = li_SectorCode - 160;
            li_PositionCode = li_PositionCode - 160;
            int li_SecPosCode = li_SectorCode * 100 + li_PositionCode; // 汉字区位码
            if (li_SecPosCode > 1600 && li_SecPosCode < 5590) {
                for (int i = 0; i < 23; i++) {
                    if (li_SecPosCode >= li_SecPosValue[i]
                            && li_SecPosCode < li_SecPosValue[i + 1]) {
                        chinese = lc_FirstLetter[i];
                        break;
                    }
                }
            } else // 非汉字字符,如图形符号或ASCII码
            {
                chinese = conversionStr(chinese, "ISO8859-1", "GB2312");
                chinese = chinese.substring(0, 1);
            }
        }

        return chinese;
    }

    /**
     * 字符串编码转换
     *
     * @param str           要转换编码的字符串
     * @param charsetName   原来的编码
     * @param toCharsetName 转换后的编码
     * @return 经过编码转换后的字符串
     */
    private static String conversionStr(String str, String charsetName, String toCharsetName) {
        try {
            str = new String(str.getBytes(charsetName), toCharsetName);
        } catch (UnsupportedEncodingException ex) {
            System.out.println("字符串编码转换异常：" + ex.getMessage());
        }
        return str;
    }

    private static String generateRandomCharacter() {
        final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final int length = alphabet.length();
        Random random = new Random();
        String randomChar = String.valueOf(alphabet.charAt(random.nextInt(length)));
        return randomChar;
    }
}
