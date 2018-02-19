package com.github.ocroquette.teamcity.autopin;

public class StringUtils {
    public static boolean isTrue(String s) {
        if (s == null)
            return false;
        return s.toLowerCase().equals("true") || s.toLowerCase().equals("yes");
    }

    public static boolean isSet(String s) {
        return s != null && ! s.isEmpty();
    }
}
