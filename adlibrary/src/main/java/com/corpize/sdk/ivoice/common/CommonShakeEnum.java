package com.corpize.sdk.ivoice.common;

/**
 * author : xpSun
 * date : 2022/2/25
 * description :
 */
public enum CommonShakeEnum {
    COMMON_ENUM_WEBVIEW(1,"App webview 打开链接"),
    COMMON_ENUM_SYSTEM_WEB(2,"系统浏览器打开链接"),
    COMMON_ENUM_PHONE(3,"拨打电话"),
    COMMON_ENUM_DOWNLOAD(6,"下载APP"),
    COMMON_ENUM_DEEPLINK(7,"deeplink 链接"),
    COMMON_ENUM_COUPON(8,"优惠券"),
    COMMON_ENUM_BRAND(10,"品牌推广"),
    ;

    private int action;
    private String value;

    CommonShakeEnum (int action, String value) {
        this.action = action;
        this.value = value;
    }

    public int getAction () {
        return action;
    }

    public void setAction (int action) {
        this.action = action;
    }

    public String getValue () {
        return value;
    }

    public void setValue (String value) {
        this.value = value;
    }
}
