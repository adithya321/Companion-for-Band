package com.pimp.companionforband.utils;

public class UIUtils {
    public static String splitCamelCase(String s) {
        String str = s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ), " ");
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
