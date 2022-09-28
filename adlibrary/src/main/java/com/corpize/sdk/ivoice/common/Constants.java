package com.corpize.sdk.ivoice.common;


public final class Constants {

    //TODO:每次打新包,请在build中修改版本号,默认显示
    public static       Boolean IS_SHOW_LOG      = false;                 //给logUtil使用,暴露给三方
    public static       Boolean IS_SHOW_HTTP_LOG = false;                 //给http使用,http的日志不暴露出去
    public static       Boolean IS_TEST          = false;                 //是否是测试环境的URL
    public static       String  SDK_VERSION      = "1.1.3";               //sdK的版本
    private static      String  mBaseUrl1        = "http://adx-test.corpize.com/";   //测试基础的url
    private static      String  mBaseUrl2        = "http://adx.corpize.com/";        //生产基础的url
    private static      String  mBaseUrl3        = "http://m.corpize.com/";        //防作弊生产基础的url
    private static      String  mBaseUrl4        = "http://action.corpize.com/";    //点赞评论生产基础的url
    public static final String  AD_ICON          = "http://cdn.corpize.com/img/ivoice.png";

    public static final String WIFI_LOCATION_URL = "https://resource.corpize.com/web/lct.html";//wifi定位地址

    public static final String ANDROID_OS_TYPE = "Android";
    public static final String SP_IS_DEBUG_TAG = "sp_is_debug_tag";
    public static final String SP_SDK_VERSION_TAG = "sp_sdk_version_tag";
    public static final String SP_SDK_DNT_TAG = "sp_sdk_dnt_tag";

    public static final String SP_SCREEN_WIDTH_TAG = "sp_screen_width_tag";
    public static final String SP_SCREEN_HEIGHT_TAG = "sp_screen_height_tag";

    public static final String SP_PERMISSION_LOCATION = "sp_permission_location";
    public static final String SP_PERMISSION_MICROPHONE = "sp_permission_microphone";
    public static final String SP_PERMISSION_DEVICE = "sp_permission_device";
    
    //互动相关
    //有互动，有界面
    public static final int INTERACTIVE_CONF_STATUS_1 = 1;
    //有互动，无界面
    public static final int INTERACTIVE_CONF_STATUS_2 = 2;
    //无互动，无界面
    public static final int INTERACTIVE_CONF_STATUS_3 = 3;

    public static String getBaseUrl () {
        if (IS_TEST) {
            return mBaseUrl1;
        } else {
            return mBaseUrl2;
        }
    }

    public static String getSensorBaseUrl () {
        if (IS_TEST) {
            return mBaseUrl1;
        } else {
            return mBaseUrl3;
        }
    }

    public static String getComentBaseUrl () {
        if (IS_TEST) {
            return mBaseUrl4;
        } else {
            return mBaseUrl4;
        }
    }

    public static Boolean getIsShowLog () {
        return IS_SHOW_LOG;
    }

    public static void setIsShowLog (Boolean isShowLog) {
        IS_SHOW_LOG = isShowLog;
    }

    public static String getSdkVer () {
        return SDK_VERSION;
    }

    public static void setAllLog (boolean showHttp, boolean showLog, boolean showSdk) {
        IS_SHOW_HTTP_LOG = showHttp;
        IS_SHOW_LOG = showLog;
    }
}
