package com.work.pamper.utils;

public class TimeUtils {
    public static String getCurrentTimeString() {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        return now.format(formatter);
    }
}
