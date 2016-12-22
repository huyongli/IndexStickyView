package cn.ittiger.indexlist.helper;

import com.github.promeg.pinyinhelper.Pinyin;

import java.util.regex.Pattern;

/**
 * @author: laohu on 2016/12/17
 * @site: http://ittiger.cn
 */
public class PinYinHelper {
    private static final String PATTERN_LETTER = "^[a-zA-Z]+$";

    /**
     * 将中文转换为pinyin
     */
    public static String getPingYin(String inputString) {

        char[] input = inputString.trim().toCharArray();
        String output = "";
        for (int i = 0; i < input.length; i++) {
            output += Pinyin.toPinyin(input[i]);
        }
        return output.toLowerCase();
    }

    /**
     * 是否为字母
     * @param inputString
     * @return
     */
    public static boolean isLetter(String inputString) {

        return Pattern.matches(PATTERN_LETTER, inputString);
    }
}
