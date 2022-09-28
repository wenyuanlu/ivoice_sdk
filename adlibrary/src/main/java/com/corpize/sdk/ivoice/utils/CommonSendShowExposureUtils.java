package com.corpize.sdk.ivoice.utils;

import com.corpize.sdk.ivoice.QCiVoiceSdk;
import com.corpize.sdk.ivoice.bean.response.AdAudioBean;
import com.corpize.sdk.ivoice.bean.response.EventBean;

import java.util.List;

/**
 * author : xpSun
 * date : 2022/1/19
 * description : 发送曝光,计算了宽高及时间戳
 */
public class CommonSendShowExposureUtils {

    private int mWidth;
    private int mHeight;
    private int mPositionX;
    private int mPositionY;

    private boolean mHaveSendViewShow           = false;//是否发送展示曝光请求
    private boolean mHaveMusicStartPlay         = false;//是否音频播放开始监听发送
    private boolean mHaveMusicMidpointPlay      = false;//是否音频播放中间监听发送
    private boolean mHaveMusicFirstQuartilePlay = false;//是否音频播放四分之一监听发送
    private boolean mHaveMusicThirdQuartilePlay = false;//是否音频播放四分之三监听发送
    private boolean mHaveMusicCompletePlay      = false;//是否音频播放完成监听发送
    private boolean mHaveMusicClosePlay         = false;//是否音频播放跳过监听发送

    private int mAllTime = 0;//第一个广告音频的播放的总时长
    private int provider;

    private static CommonSendShowExposureUtils instance;

    public static CommonSendShowExposureUtils getInstance () {
        if (null == instance) {
            instance = new CommonSendShowExposureUtils();
        }
        return instance;
    }

    public CommonSendShowExposureUtils setProvider (int provider) {
        this.provider = provider;
        return getInstance();
    }

    private OnSendShowExposureListener onAdExposureListener;

    public interface OnSendShowExposureListener {
        void onAdExposure ();
    }

    public void setOnAdExposureListener (OnSendShowExposureListener onAdExposureListener) {
        this.onAdExposureListener = onAdExposureListener;
    }

    private CommonSendShowExposureUtils () {
        this.mWidth = QCiVoiceSdk.get().width;
        this.mHeight = QCiVoiceSdk.get().height;
        this.mPositionX = QCiVoiceSdk.get().positionX;
        this.mPositionY = QCiVoiceSdk.get().positionY;
    }

    public void initDefaultValue () {
        mHaveSendViewShow = false;//是否发送展示曝光请求
        mHaveMusicStartPlay = false;//是否音频播放开始监听发送
        mHaveMusicMidpointPlay = false;//是否音频播放中间监听发送
        mHaveMusicFirstQuartilePlay = false;//是否音频播放四分之一监听发送
        mHaveMusicThirdQuartilePlay = false;//是否音频播放四分之三监听发送
        mHaveMusicCompletePlay = false;//是否音频播放完成监听发送
        mHaveMusicClosePlay = false;//是否音频播放跳过监听发送
    }

    public void setAllTime (int mAllTime) {
        this.mAllTime = mAllTime;
    }

    public void sendShowExposure (
            List<String> imgList
    ) {
        long time = System.currentTimeMillis();

        double clickDownX = QCiVoiceSdk.get().clickDownX;
        double clickDownY = QCiVoiceSdk.get().clickDownY;
        double clickUpX   = QCiVoiceSdk.get().clickUpX;
        double clickUpY   = QCiVoiceSdk.get().clickUpY;

        if (imgList != null && imgList.size() > 0) {
            for (int i = 0; i < imgList.size(); i++) {
                String urlOld = imgList.get(i);
                String url    = urlOld;
                if (url.contains("__DOWN_X__")) {//点击X轴的替换
                    url = url.replace("__DOWN_X__", clickDownX + "");
                }
                if (url.contains("__DOWN_Y__")) {//点击Y轴的替换
                    url = url.replace("__DOWN_Y__", clickDownY + "");
                }
                if (url.contains("__UP_X__")) {//抬起X轴的替换
                    url = url.replace("__UP_X__", clickUpX + "");
                }
                if (url.contains("__UP_Y__")) {//抬起Y轴的替换
                    url = url.replace("__UP_Y__", clickUpY + "");
                }
                if (url.contains("__WIDTH__")) {//宽度替换
                    url = url.replace("__WIDTH__", mWidth + "");
                }
                if (url.contains("__HEIGHT__")) {//高度替换
                    url = url.replace("__HEIGHT__", mHeight + "");
                }
                if (url.contains("__POSITION_X__")) {//抬起X轴的替换
                    url = url.replace("__POSITION_X__", mPositionX + "");
                }
                if (url.contains("__POSITION_Y__")) {//抬起Y轴的替换
                    url = url.replace("__POSITION_Y__", mPositionY + "");
                }
                if (url.contains("__TIME_STAMP__")) {//时间戳的替换
                    url = url.replace("__TIME_STAMP__", time + "");
                }

                url = String.format("%s&provider=%s", url, provider);
                QcHttpUtil.sendAdExposure(url);
            }
        }
    }

    /**
     * 音频广告播放的监听
     *
     * @param currentTime
     * @param place       0是开始,1是中间,2是结束,3是跳过
     */
    public void musicPlayExposure (
            AdAudioBean mResponse,
            int currentTime,
            int place
    ) {
        if (mResponse != null && mResponse.getEvent() != null) {
            EventBean event = mResponse.getEvent();

            if (place == 0) {
                List<String> start = event.getStart();
                //开始的监听
                if (start != null && !mHaveMusicStartPlay) {
                    mHaveMusicStartPlay = true;
                    sendShowExposure(start);
                }

                //曝光的监听,如果只播放音频的话,则超过指定的比例,就进行曝光
                if (!mHaveSendViewShow) {
                    mHaveSendViewShow = true;
                    if (onAdExposureListener != null) {
                        onAdExposureListener.onAdExposure();
                    }
                }
            } else if (place == 1) {
                List<String> firstQuartile = event.getFirstQuartile();
                List<String> midpoint      = event.getMidpoint();
                List<String> thirdQuartile = event.getThirdQuartile();
                //四分之一监听
                if (firstQuartile != null && !mHaveMusicFirstQuartilePlay && currentTime * 4 > mAllTime) {
                    mHaveMusicFirstQuartilePlay = true;
                    sendShowExposure(firstQuartile);
                }

                //一半监听
                if (midpoint != null && !mHaveMusicMidpointPlay && currentTime * 2 > mAllTime) {
                    mHaveMusicMidpointPlay = true;
                    sendShowExposure(midpoint);
                }

                //四分之三监听
                if (thirdQuartile != null && !mHaveMusicThirdQuartilePlay && currentTime * 4 > mAllTime * 3) {
                    mHaveMusicThirdQuartilePlay = true;
                    sendShowExposure(thirdQuartile);
                }
            } else if (place == 2) {
                //结束的监听
                List<String> complete = event.getComplete();
                if (complete != null && !mHaveMusicCompletePlay) {
                    mHaveMusicCompletePlay = true;
                    sendShowExposure(complete);
                }
            } else if (place == 3) {
                //跳过关闭的监听,如果完成的监听已经发送,则跳过的监听不再发送
                List<String> close = event.getClose();
                if (close != null && !mHaveMusicClosePlay && !mHaveMusicCompletePlay) {
                    mHaveMusicClosePlay = true;
                    sendShowExposure(close);
                }
            }
        }
    }
}
