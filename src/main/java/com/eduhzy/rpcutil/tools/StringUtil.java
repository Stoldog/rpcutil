package com.eduhzy.rpcutil.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhongHG
 * @date 2018-11-30
 */
public class StringUtil {

    private static final Pattern LINE_PATTERN = Pattern.compile("_(\\w)");

    /**
     * 填空格
     *
     * @param str
     * @param length
     * @return
     */
    public static String fillBlank(String str, int length) {
        if (str.length() >= length) {
            return str;
        }
        int size = length - str.length();
        for (int i = 0; i < size; i++) {
            str += " ";
        }
        return str;
    }

    /**
     * 下划线转驼峰
     *
     * @param string str
     * @return str
     */
    public static String toCamelCase(String string) {
        Matcher matcher = LINE_PATTERN.matcher(string);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


}
