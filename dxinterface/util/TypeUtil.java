package com.dhxl.web.controller.dxinterface.util;

public class TypeUtil {

    /**
     * 将字符串的编码格式转换为utf-8
     *
     * @param str
     * @return Name = new
     * String(Name.getBytes("ISO-8859-1"), "utf-8");
     */
    public static String toUTF8(String str) {
        if (isEmpty(str)) {
            return "";
        }
        try {
            if (str.equals(new String(str.getBytes("GB2312"), "GB2312"))) {
                str = new String(str.getBytes("GB2312"), "utf-8");
                return str;
            }
        } catch (Exception exception) {
        }
        try {
            if (str.equals(new String(str.getBytes("ISO-8859-1"), "ISO-8859-1"))) {
                str = new String(str.getBytes("ISO-8859-1"), "utf-8");
                return str;
            }
        } catch (Exception exception1) {
        }
        try {
            if (str.equals(new String(str.getBytes("GBK"), "GBK"))) {
                str = new String(str.getBytes("GBK"), "utf-8");
                return str;
            }
        } catch (Exception exception3) {
        }
        try {
            if (str.equals(new String(str.getBytes("utf-16"), "utf-16"))) {
                str = new String(str.getBytes("utf-16"), "utf-8");
                return str;
            }

        } catch (Exception exception3) {
        }
        try {
            if (str.equals(new String(str.getBytes("utf-8"), "utf-8"))) {
                str = new String(str.getBytes("utf-8"), "utf-8");
                return str;
            }

        } catch (Exception exception3) {
        }
        return str;
    }

    /**
     * 判断是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        // 如果字符串不为null，去除空格后值不与空字符串相等的话，证明字符串有实质性的内容
        if (str != null && !str.trim().isEmpty()) {
            return false;// 不为空
        }
        return true;// 为空
    }
}
