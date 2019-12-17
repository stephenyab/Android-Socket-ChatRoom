package com.server.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static String getCurrentTime() {
        String time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = sdf.format(new Date());
        return time;
    }

    public static String getDay() {
        String time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        time = sdf.format(new Date());
        return time;
    }

    public static String getTime() {
        String time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        time = sdf.format(new Date());
        return time;
    }
}
