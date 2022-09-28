package com.example.qcaudioad.utils;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.example.qcaudioad.base.BaseApplication;

public class SpUtils {
    private static final String            SP_NAME  = "simple_app_config";//文件名
    private static       SharedPreferences mSp;
    private static final Application       mContext = BaseApplication.getInstance();

    private static SharedPreferences getSharedPreferences () {
        if (mSp == null) {
            mSp = mContext.getSharedPreferences(SP_NAME, mContext.MODE_PRIVATE);
        }
        return mSp;
    }

    private static Editor getEditor () {
        SharedPreferences sharedPreferences = getSharedPreferences();
        Editor            edit              = sharedPreferences.edit();
        return edit;
    }

    public static void saveString (String key, String value) {
        Editor edit = getEditor();
        edit.putString(key, value);
        edit.commit();
    }

    public static String getString (String key) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        String            result            = sharedPreferences.getString(key, null);
        return result;
    }

    public static void saveBoolean (String key, boolean value) {
        Editor edit = getEditor();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public static Boolean getBoolean (String key) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        boolean           result            = sharedPreferences.getBoolean(key, false);
        return result;
    }

    public static Boolean getBoolean (String key,boolean defValue) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        boolean           result            = sharedPreferences.getBoolean(key, defValue);
        return result;
    }

    public static void saveInt (String key, int value) {
        Editor edit = getEditor();
        edit.putInt(key, value);
        edit.commit();
    }

    public static int getInt (String key) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        int               result            = sharedPreferences.getInt(key, 0);
        return result;
    }

    public static int getInt (String key, int defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        int               result            = sharedPreferences.getInt(key, defaultValue);
        return result;
    }

    public static void saveLong (String key, long value) {
        Editor edit = getEditor();
        edit.putLong(key, value);
        edit.commit();
    }

    public static long getLong (String key) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        long              result            = sharedPreferences.getLong(key, 0);
        return result;
    }
}
