package com.github.catvod.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;

import com.github.catvod.spider.Init;

public class Prefers {
    /*SharedPreferences使用 xml格式为Android应用提供一种永久数据存贮方式，并且使用键值对的方式来存储数据的。
    相对于一个Android应用而言，目录/data/data/your_app_package_name/shared_prefs/下，可以被处在同一个应用中的所有
    Activity访问。Android提供了相关的API来处理这些数据而不需要程序员直接操作这些文件或者考虑数据同步的问题。

    SharedPreferences本身是一个接口，程序无法直接创建SharedPreferences的实例，
    只能通过Context提供的getSharedPreferences（String name，int mode）方法来获取SharedPreferences的实例，
    其中有两个参数：第一个参数用于指定SharedPreferences文件的名称（格式为xml文件），如果该名称的文件不存在则会创建一个。
    第二个参数用于指定操作的模式，如下：
    MODE_PRIVATE：默认操作模式，只有本应用程序才可以对这个SharedPreferences文件进行读写。
    MODE_WORLD_READABLE：其他应用对这个SharedPreferences文件只能读不能修改。
    MODE_WORLD_WRITEABLE：这个 SharedPreferences 文件能被其他的应用读写。
    MODE_MULTI_PROCESS：这个模式在 Android2.3 之后已经弃之不用了，可以省略。
    此方法用于获取SharedPreferences的实例。
    */
    private static SharedPreferences getPrefers() {
        return Init.context().getSharedPreferences(Init.context().getPackageName() + "_preferences", MODE_PRIVATE);
    }

    /*返回String键值对
    *
    * */
    public static String getString(String key, String defaultValue) {
        return getPrefers().getString(key, defaultValue);
    }

    public static String getString(String key) {
        return getString(key, "");
    }

    /*
     *返回Int键值对
     * */
    public static int getInt(String key, int defaultValue) {
        return getPrefers().getInt(key, defaultValue);
    }

    public static int getInt(String key) {
        return getInt(key, 0);
    }

    /*
     *返回Boolean键值对
     * */
    public static boolean getBoolean(String key, boolean defaultValue) {
        return getPrefers().getBoolean(key, defaultValue);
    }

    public static boolean getBoolean(String key) {
        return getPrefers().getBoolean(key, false);
    }

    /*
     *存放各类型键值对
     * */
    public static void put(String key, Object obj) {
        if (obj == null) return;
        if (obj instanceof String) {
            getPrefers().edit().putString(key, (String) obj).apply();
        } else if (obj instanceof Boolean) {
            getPrefers().edit().putBoolean(key, (Boolean) obj).apply();
        } else if (obj instanceof Float) {
            getPrefers().edit().putFloat(key, (Float) obj).apply();
        } else if (obj instanceof Integer) {
            getPrefers().edit().putInt(key, (Integer) obj).apply();
        } else if (obj instanceof Long) {
            getPrefers().edit().putLong(key, (Long) obj).apply();
        }
    }
}
