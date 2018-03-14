package com.shenmao.chuhe.commons;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DatetimeHelper {
    public static String getFriendlyTime(Date date, String def) {

        if (date == null) return def;
        String result = null;
        Date now = new Date();

        long diff = now.getTime() - date.getTime();
        String format = (new SimpleDateFormat("HH:mm")).format(date);

        long diffSeconds = diff / 1000;
        int diffMinutes = Integer.parseInt((diff / (60 * 1000)) + "");;
        int diffHours = Integer.parseInt((diff / (60 * 60 * 1000)) + "");

        if (diffSeconds < 60) {
            result = "刚刚 " + format;
        } else if (diffMinutes >= 1 && diffMinutes < 60) {
            result = getCountNumber(diffMinutes, "分钟前, ") + format;
        }  else if (diffHours >= 1 && diffHours < 24) {
            result = getCountNumber(diffHours, "小时前, ") + format;
        } else if (diffHours >= 24 && diffHours < 720) {
            result = getCountNumber(diffHours / 24, "天前, ") + format;
        } else if (diffHours >= 720 && diffHours < 4320) {
            result = getCountNumber(diffHours / 720, "个月, ") + format;
        } else {
            result =  (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(date);
        }

        return result;
    }

    private static String getCountNumber(int number, String s) {

        String result = null;

        switch (number) {
            case 1:
                result = "一" + s;
                break;
            case 2:
                result = "两" + s;
                break;
            case 3:
                result = "三" + s;
                break;
            case 4:
                result = "四" + s;
                break;
            case 5:
                result = "五" + s;
                break;
            case 6:
                result = "六" + s;
                break;
            case 7:
                result = "七" + s;
                break;
            case 8:
                result = "八" + s;
                break;
            case 9:
                result = "九" + s;
                break;
            case 10:
                result = "十" + s;
                break;
            case 11:
                result = "十一" + s;
                break;
            case 12:
                result = "十二" + s;
                break;
            default:
                result = number + s;
        }

        return result;
    }

}
