package com.lqr.wechat.util;


import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * @创建者 GitLqr
 * @描述 拼音工具（需要pinyin4j-2.5.0.jar）
 */
public class PinyinUtils {

    //Only read after init, so one shared instance is enough
    private static final HanyuPinyinOutputFormat FORMAT = new HanyuPinyinOutputFormat();

    static {
        FORMAT.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    /**
     * 根据传入的字符串(包含汉字),得到拼音
     * 沧晓 -> CANGXIAO
     * 沧  晓*& -> CANGXIAO*&
     * 沧晓f5 -> CANGXIAOf5
     *
     * @param str 字符串
     * @return the pinyin, or "" for null
     */
    public static String getPinyin(String str) {
        if (str == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            // 如果是空格, 跳过
            if (Character.isWhitespace(c)) {
                continue;
            }
            if (c < 0x4E00 || c > 0x9FA5) {
                // 肯定不是汉字
                sb.append(c);
                continue;
            }
            String[] pinyins = null;
            try {
                // 通过char得到拼音集合. 单 -> dan, shan
                pinyins = PinyinHelper.toHanyuPinyinStringArray(c, FORMAT);
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
            //Some chars in the range have no pinyin and get null; keep them as is
            if (pinyins == null || pinyins.length == 0) {
                sb.append(c);
            } else {
                sb.append(pinyins[0]);
            }
        }

        return sb.toString();
    }

}
