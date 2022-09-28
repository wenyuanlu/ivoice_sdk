package com.corpize.sdk.ivoice.danmuku.view;


import com.corpize.sdk.ivoice.danmuku.model.DanMuModel;

/**
 * Created by android_ls on 2016/12/7.
 */
public interface OnDanMuTouchCallBackListener {

    void callBack (DanMuModel danMuView, int clickX, int distanceY);

}
