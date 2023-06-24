package com.happidreampets.app.utils;

public class Utils {
    public static boolean isStringLong(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
