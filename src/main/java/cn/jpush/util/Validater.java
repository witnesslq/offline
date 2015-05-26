package cn.jpush.util;

/**
 * Created by fengwu on 15/5/22.
 */
public abstract class Validater {

    public static boolean isValidPlatform(String platform) {
        return platform.toUpperCase().equals("A") || platform.toUpperCase().equals("I")
                || platform.toUpperCase().equals("W");
    }

    public static boolean isValidAppKey(String appkey) {
        return appkey.toLowerCase().matches("[0-9a-f]{24}");
    }
}
