package com.corpize.sdk.ivoice.admanager;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.corpize.sdk.ivoice.QCiVoiceSdk;
import com.corpize.sdk.ivoice.bean.AdMusicBean;
import com.corpize.sdk.ivoice.bean.AdResponseBean;
import com.corpize.sdk.ivoice.bean.UserPlayInfoBean;
import com.corpize.sdk.ivoice.bean.response.AdAudioBean;
import com.corpize.sdk.ivoice.bean.response.CompanionBean;
import com.corpize.sdk.ivoice.bean.response.EventBean;
import com.corpize.sdk.ivoice.bean.response.InteractiveBean;
import com.corpize.sdk.ivoice.bean.response.RemindBean;
import com.corpize.sdk.ivoice.common.CommonHandler;
import com.corpize.sdk.ivoice.common.ErrorUtil;
import com.corpize.sdk.ivoice.listener.AudioQcAdListener;
import com.corpize.sdk.ivoice.listener.CountDownCallback;
import com.corpize.sdk.ivoice.listener.DialogCallback;
import com.corpize.sdk.ivoice.listener.DialogSizeCallback;
import com.corpize.sdk.ivoice.utils.CommonShakeEventUtils;
import com.corpize.sdk.ivoice.utils.CommonSplicingResourceUtils;
import com.corpize.sdk.ivoice.utils.DialogUtils;
import com.corpize.sdk.ivoice.utils.LogUtils;
import com.corpize.sdk.ivoice.utils.MediaPlayerUtil;
import com.corpize.sdk.ivoice.utils.QcHttpUtil;
import com.corpize.sdk.ivoice.utils.ShakeInteractiveUtil;
import com.corpize.sdk.ivoice.utils.ShakeUtils;

import java.util.List;

/**
 * author: yh
 * date: 2020-02-11 23:35
 * description: 音频广告
 */
public class AudioAdManager extends QcAdManager {

    private static AudioAdManager    sQcAudioAd;
    private        AudioQcAdListener mListener;
    private        boolean           mHasPlayAd                  = false;
    private        boolean           mHaveSnake                  = false;       //是否摇一摇曝光
    private        boolean           mHaveSnakeClick             = false;       //是否摇一摇曝光的点击事件
    private        boolean           mHaveDownStart              = false;       //是否发送开始下载曝光请求
    private        boolean           mHaveDownComplete           = false;       //是否发送完成下载曝光请求
    private        boolean           mHaveDownInstall            = false;       //是否发送开始安装曝光请求
    private        boolean           mHaveSendViewShow           = false;       //是否发送展示曝光请求
    private        boolean           mHaveMusicStartPlay         = false;       //是否音频播放开始监听发送
    private        boolean           mHaveMusicMidpointPlay      = false;       //是否音频播放中间监听发送
    private        boolean           mHaveMusicFirstQuartilePlay = false;       //是否音频播放四分之一监听发送
    private        boolean           mHaveMusicThirdQuartilePlay = false;       //是否音频播放四分之三监听发送
    private        boolean           mHaveMusicCompletePlay      = false;       //是否音频播放完成监听发送
    private        boolean           mHaveMusicClosePlay         = false;       //是否音频播放跳过监听发送
    private        int               mAllTime                    = 0;           //第一个广告音频的播放的总时长
    private        int               mCurrentTime                = 0;
    private        int               mWidth                      = 0;
    private        int               mHeight                     = 0;
    private        float             mPositionX                  = 0;           //企创 左上角X
    private        float             mPositionY                  = 0;           //企创 左上角Y
    private        String            mUrl;
    private        Activity          mActivity;
    private        List<String>      mImptrackers;
    private        AdAudioBean       mAdAudioBean;
    private        ShakeUtils        mShakeUtils;

    private int callbackType = 0;//播放结束后继续操作的类型

    /**
     * 单例模式
     */
    public static AudioAdManager get () {
        if (sQcAudioAd == null) {
            sQcAudioAd = new AudioAdManager();
        }
        return sQcAudioAd;
    }

    /**
     * 清除广告
     */
    @Override
    public void destroy () {
        mListener = null;
        mActivity = null;
        mImptrackers = null;
        mAdAudioBean = null;
        sQcAudioAd = null;
    }

    @Override
    public void onResume () {
        super.onResume();
    }

    @Override
    public void onPause () {
        super.onPause();
    }

    @Override
    public void startPlayAd () {
        super.startPlayAd();
        showAd();
    }

    public AudioQcAdListener getListener () {
        return mListener;
    }

    /**
     * 展示企创音频广告
     */
    public void showQcAd (final Activity activity, String adId, List<UserPlayInfoBean> labels, final AudioQcAdListener listener) {
        mListener = listener;
        mActivity = activity;
        mHasPlayAd = false;
        mHaveSnake = false;
        mHaveDownStart = false;
        mHaveDownComplete = false;
        mHaveDownInstall = false;
        mHaveSendViewShow = false;
        mHaveMusicStartPlay = false;
        mHaveMusicMidpointPlay = false;
        mHaveMusicFirstQuartilePlay = false;
        mHaveMusicThirdQuartilePlay = false;
        mHaveMusicCompletePlay = false;
        mHaveMusicClosePlay = false;
        mAllTime = 0;
        mCurrentTime = 0;
        //发送请求获取广告信息
        QcHttpUtil.getAudioAd(activity, adId, 2, labels, new QcHttpUtil.QcHttpOnListener<AdResponseBean>() {
            @Override
            public void OnQcCompletionListener (AdResponseBean response) {
                if (response != null && response.getAdm() != null) {
                    mAdAudioBean = response.getAdm().getNormal();
                    if (mAdAudioBean != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run () {
                                if (listener != null) {
                                    listener.onAdReceive(sQcAudioAd);
                                }
                            }
                        });
                    }
                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run () {
                            if (mListener != null) {
                                mListener.onAdError(ErrorUtil.NOAD);
                            }
                        }
                    });
                }
            }

            @Override
            public void OnQcErrorListener (final String erro, final int code) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run () {
                        if (mListener != null) {
                            mListener.onAdError(erro + code);
                        }
                    }
                });
            }
        });
    }

    /**
     * 正式展示广告
     */
    private void showAd () {
        if (mAdAudioBean != null) {
            CompanionBean companion = mAdAudioBean.getCompanion();
            //有视频,默认跳转视频页
            if (!QCiVoiceSdk.get().isBackground() && companion != null && companion.getVideo() != null
                    && !TextUtils.isEmpty(companion.getVideo().getUrl())) {
                InteractiveBean interactive = mAdAudioBean.getInteractive();
                Intent          intent      = new Intent(mActivity, QcAdVideoActivity.class);
                intent.putExtra("dataBean", mAdAudioBean);
                intent.putExtra("interactiveBean", interactive);
                intent.putExtra("intervalTime", null == interactive ? 0 : interactive.getWait());
                intent.putExtra("volum", mAdAudioBean.getVolume());
                intent.putExtra("callBackType", null == mAdAudioBean.getRendering_config() ? 0 : mAdAudioBean.getRendering_config().getStop_playing_mode());
                mActivity.startActivity(intent);
                return;
            }

            if (null != mAdAudioBean) {
                callbackType = null == mAdAudioBean.getRendering_config() ? 0 : mAdAudioBean.getRendering_config().getStop_playing_mode();
                //无视频,播放音频广告
                String audiourl = mAdAudioBean.getAudiourl();
                if (!TextUtils.isEmpty(audiourl)) {
                    mUrl = audiourl;
                    managerAdMusic(audiourl, mAdAudioBean);
                } else {
                    managerAdWithOutMusic(mAdAudioBean);
                }
            }
        }
    }

    /**
     * 处理并加载音频广告
     *
     * @param adUrl
     * @param bean
     */
    private void managerAdMusic (String adUrl, final AdAudioBean bean) {
        final int intervalTime = null == bean.getInteractive() ? 0 : bean.getInteractive().getWait();
        final int volume        = bean.getVolume();
        int       startTime    = 0;
        if (bean.getInteractive() != null) {
            //有互动的时候
            InteractiveBean interactive = bean.getInteractive();
            final int       waitTime    = null == interactive ? 0 : interactive.getWait();

            CommonSplicingResourceUtils.getInstance().splicingStartAdResource(
                    adUrl,
                    bean,
                    new CommonSplicingResourceUtils
                            .OnSplicingStartAdResourceListener() {
                @Override
                public void onSplicingStartAdResource (
                        List<AdMusicBean> voiceList,
                        String downMusic,
                        String startMusic,
                        int startTime
                ) {
                    interactionPlayVoiceList(
                            bean,
                            volume,
                            startTime,
                            waitTime,
                            voiceList
                    );
                }
            });
        } else {
            notInteractionPlayer(
                    bean,
                    volume,
                    startTime,
                    intervalTime
            );
        }
    }

    //有互动的时候
    private void interactionPlayVoiceList (
            final AdAudioBean bean,
            int volume,
            final int startTime,
            final int waitTime,
            List<AdMusicBean> voiceList
    ) {
        MediaPlayerUtil.getInstance().setMinVolume(volume);
        final int finalStarttime = startTime;
        MediaPlayerUtil.getInstance()
                .playVoiceList(
                        mActivity,
                        voiceList,
                        new MediaPlayerUtil.MediaMoreOnListener() {
            @Override
            public void onPlayStartListener (int position, final int allTime) {
                if (position == 0) {
                    mAllTime = allTime;
                    mCurrentTime = 0;
                    //展示弹窗
                    CompanionBean companion = bean.getCompanion();
                    if (companion != null &&
                            !TextUtils.isEmpty(companion.getUrl())
                            || !TextUtils.isEmpty(bean.getFirstimg())) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run () {
                                //同时弹出弹窗
                                DialogUtils.showImageDialog(
                                        mActivity,
                                        bean,
                                        allTime,
                                        finalStarttime,
                                        mListener,
                                        new DialogSizeCallback() {
                                            @Override
                                            public void getSize (int x, int y) {
                                                mWidth = x;
                                                mHeight = y;
                                            }

                                            @Override
                                            public void getLeftPosition (int x, int y) {
                                                mPositionX = x;
                                                mPositionY = y;
                                            }
                                        }, new DialogCallback() {
                                            @Override
                                            public void dialogDismiss () {
                                                closeCommonHandler();
                                            }
                                        });

                                if (!mHaveSendViewShow && !QCiVoiceSdk.get().isBackground()) {
                                    mHaveSendViewShow = true;
                                    if (mListener != null) {
                                        mListener.onAdExposure();
                                    }
                                }
                            }
                        });
                    }
                    //开始曝光监听
                    musicPlayExposure(0, 0);
                }

            }

            @Override
            public void onPlayStatusChangeListener (int position, int status) {
                if (position == 0) {
                }
            }

            @Override
            public void onPlayCurrentTimeListener (int position, int currentTime) {
                //播放时间的返回
                if (position == 0) {
                    mCurrentTime = currentTime;
                    LogUtils.e("音频返回的播放时长More=" + currentTime);
                    //播放进度曝光监听
                    musicPlayExposure(currentTime, 1);
                }
            }

            @Override
            public void onPlayCompletionListener () {
                DialogUtils.noInteractiveCountDownTime(waitTime, mListener);
                onAdCompleteCallBack();
            }

            @Override
            public void onPlayCenterPositionListener (int position) {
                if (position == 0) {
                    mShakeUtils = new ShakeUtils(mActivity);
                    ShakeInteractiveUtil.setOnShakeListener(mActivity, mShakeUtils, new ShakeUtils.OnShakeListener() {
                        @Override
                        public void onShake () {
                            setShakeClick(mActivity, bean);
                        }
                    });

                    //播放完成曝光监听
                    musicPlayExposure(0, 2);
                }
            }

            @Override
            public void onPlayErrorListener (int code, String msg) {
                if (mListener != null) {
                    //mListener.onADManager(sQcAudioAd);
                    mListener.onAdError("广告获取成功,播放失败," + msg);
                }
            }

            @Override
            public void onAudioFocusChange (int focusChange) {

            }
        });
    }

    //无互动的时候
    private void notInteractionPlayer(
            final AdAudioBean bean,
            int volume,
            final int startTime,
            final int intervalTime
    ){
        final int finalStarttime = startTime;
        MediaPlayerUtil.getInstance().setMinVolume(volume);
        MediaPlayerUtil.getInstance().playVoice(mActivity, mUrl, new MediaPlayerUtil.MediaOnListener() {
            @Override
            public void onPlayStartListener (final int allTime) {
                mAllTime = allTime;
                mCurrentTime = 0;
                //展示弹窗
                CompanionBean companion = bean.getCompanion();
                if (companion != null && !TextUtils.isEmpty(companion.getUrl())) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run () {
                            //TODO:同时弹出弹窗
                            DialogUtils.showImageDialog(
                                    mActivity,
                                    bean,
                                    allTime,
                                    finalStarttime,
                                    mListener,
                                    new DialogSizeCallback() {
                                        @Override
                                        public void getSize (int x, int y) {
                                            mWidth = x;
                                            mHeight = y;
                                        }

                                        @Override
                                        public void getLeftPosition (int x, int y) {
                                            mPositionX = x;
                                            mPositionY = y;
                                        }
                                    }, new DialogCallback() {
                                        @Override
                                        public void dialogDismiss () {
                                            closeCommonHandler();
                                        }
                                    });

                            if (!mHaveSendViewShow && !QCiVoiceSdk.get().isBackground()) {
                                mHaveSendViewShow = true;
                                if (mListener != null) {
                                    mListener.onAdExposure();
                                }
                            }
                        }
                    });

                }
                //开始曝光监听
                musicPlayExposure(0, 0);
            }

            @Override
            public void onPlayCurrentTimeListener (int currentTime) {
                mCurrentTime = currentTime;
                //播放时间的返回
                musicPlayExposure(currentTime, 1);
            }

            @Override
            public void onPlayCompletionListener () {
                DialogUtils.noInteractiveCountDownTime(intervalTime, mListener);
                onAdCompleteCallBack();
                //播放完成曝光监听
                musicPlayExposure(0, 2);
            }

            @Override
            public void onPlayErrorListener (int code, String msg) {
                if (mListener != null) {
                    mListener.onAdError("广告获取成功,播放失败," + msg);
                }
            }

            @Override
            public void onAudioFocusChange (int focusChange) {

            }
        });
    }

    /**
     * 广告播完回调逻辑处理
     */
    private CommonHandler mHandler;

    private void onAdCompleteCallBack () {
        if (mListener != null) {
            if (callbackType == 1) {
                closeCommonHandler();
                MediaPlayerUtil.getInstance().stopAndRelease();
                mListener.onAdCompletion();
            } else {
                if (mHandler == null) {
                    mHandler = new CommonHandler<>(this);
                }
                mHandler.postDelay(5000, new CommonHandler.PostDelayCallBack() {
                    @Override
                    public void callBack () {
                        if (callbackType == 2) {
                            startPlayAd();
                            callbackType = 1;
                        } else if (callbackType == 3) {
                            closeCommonHandler();
                            MediaPlayerUtil.getInstance().stopAndRelease();
                            mListener.onAdCompletion();
                        }
                    }
                });
            }
        }
    }

    /**
     * 结束Handler，防止内存泄漏
     */
    private void closeCommonHandler () {
        if (mHandler != null) {
            mHandler.releaseHandler();
            mHandler = null;
        }
    }

    /**
     * 处理并加载音频广告(没有主音频的时候)
     */
    private void managerAdWithOutMusic (final AdAudioBean bean) {
        final int intervalTime = null == bean.getInteractive() ? 0 : bean.getInteractive().getWait();
        final int volum        = bean.getVolume();
        int       startTime    = 0;
        if (bean.getInteractive() != null) {
            managerAdWithOutMusicInteraction(bean,volum,intervalTime);
        } else {
            CompanionBean companion = bean.getCompanion();
            if (companion != null && companion.getVideo() != null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run () {
                        //同时弹出弹窗,没有倒计时
                        DialogUtils.showImageWithOutDialog(mActivity, bean, mListener,
                                new DialogSizeCallback() {
                                    @Override
                                    public void getSize (int x, int y) {
                                        mWidth = x;
                                        mHeight = y;
                                    }

                                    @Override
                                    public void getLeftPosition (int x, int y) {
                                        mPositionX = x;
                                        mPositionY = y;

                                    }
                                }, null);

                        if (!mHaveSendViewShow && !QCiVoiceSdk.get().isBackground()) {
                            mHaveSendViewShow = true;
                            if (mListener != null) {
                                mListener.onAdExposure();
                            }
                        }
                    }
                });
            }
        }
    }

    private void managerAdWithOutMusicInteraction(
            final AdAudioBean bean,
            final int volum,
            final int intervalTime){
        String startMusic = CommonSplicingResourceUtils.getInstance().managerAdWithOutMusic(bean);

        //有互动音频的时候
        if (!TextUtils.isEmpty(startMusic)) {
            MediaPlayerUtil.getInstance().setMinVolume(volum);
            MediaPlayerUtil.getInstance().playVoice(mActivity, startMusic, new MediaPlayerUtil.MediaOnListener() {
                @Override
                public void onPlayStartListener (final int allTime) {
                    //展示弹窗
                    CompanionBean companion = bean.getCompanion();
                    if (companion != null && companion.getVideo() != null) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run () {
                                //同时弹出弹窗
                                DialogUtils.showImageDialog(mActivity, bean, 0, allTime,
                                        mListener, new DialogSizeCallback() {
                                            @Override
                                            public void getSize (int x, int y) {
                                                mWidth = x;
                                                mHeight = y;
                                            }

                                            @Override
                                            public void getLeftPosition (int x, int y) {
                                                mPositionX = x;
                                                mPositionY = y;
                                            }
                                        }, null);

                                if (!mHaveSendViewShow && !QCiVoiceSdk.get().isBackground()) {
                                    mHaveSendViewShow = true;
                                    if (mListener != null) {
                                        mListener.onAdExposure();
                                    }
                                }
                            }
                        });

                        //摇一摇
                        mShakeUtils = new ShakeUtils(mActivity);
                        ShakeInteractiveUtil.setOnShakeListener(mActivity, mShakeUtils, new ShakeUtils.OnShakeListener() {
                            @Override
                            public void onShake () {
                                setShakeClick(mActivity, bean);
                            }
                        });
                    }
                }

                @Override
                public void onPlayCurrentTimeListener (int currentTime) {
                }

                @Override
                public void onPlayCompletionListener () {
                    //互动的等待倒计时
                    if (mShakeUtils != null) {
                        DialogUtils.noInteractiveCountDownTime(intervalTime, false, mListener, new CountDownCallback() {
                            @Override
                            public void close () {
                                //结束后要清理摇一摇
                                if (mShakeUtils != null) {
                                    mShakeUtils.onPause();
                                    mShakeUtils.clear();
                                }
                            }
                        });
                    }
                    MediaPlayerUtil.getInstance().stopAndRelease();
                }

                @Override
                public void onPlayErrorListener (int code, String msg) {
                    if (mListener != null) {
                        mListener.onAdError("广告获取成功,播放失败," + msg);
                    }
                }

                @Override
                public void onAudioFocusChange (int focusChange) {

                }
            });
        } else {
            //无互动音频的时候,只跳出弹窗
            CompanionBean companion = bean.getCompanion();
            if (companion != null && companion.getVideo() != null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run () {
                        //同时弹出弹窗,没有倒计时
                        DialogUtils.showImageWithOutDialog(mActivity, bean, mListener,
                                new DialogSizeCallback() {
                                    @Override
                                    public void getSize (int x, int y) {
                                        mWidth = x;
                                        mHeight = y;
                                    }

                                    @Override
                                    public void getLeftPosition (int x, int y) {
                                        mPositionX = x;
                                        mPositionY = y;
                                    }
                                }, null);

                        if (!mHaveSendViewShow && !QCiVoiceSdk.get().isBackground()) {
                            mHaveSendViewShow = true;
                            if (mListener != null) {
                                mListener.onAdExposure();
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * 发送曝光,计算了宽高及时间戳
     */
    private void sendShowExposure (List<String> imgList) {
        long time = System.currentTimeMillis();

        if (imgList != null && imgList.size() > 0) {
            for (int i = 0; i < imgList.size(); i++) {
                String urlOld = imgList.get(i);
                String url    = urlOld;
                if (url.contains("__WIDTH__")) {//宽度替换
                    url = url.replace("__WIDTH__", mWidth + "");
                }
                if (url.contains("__HEIGHT__")) {//高度替换
                    url = url.replace("__HEIGHT__", mHeight + "");
                }
                if (url.contains("__POSITION_X__")) {//抬起X轴的替换
                    url = url.replace("__POSITION_X__", 0 + "");
                }
                if (url.contains("__POSITION_Y__")) {//抬起Y轴的替换
                    url = url.replace("__POSITION_Y__", 0 + "");
                }
                if (url.contains("__TIME_STAMP__")) {//时间戳的替换
                    url = url.replace("__TIME_STAMP__", time + "");
                }

                QcHttpUtil.sendAdExposure(url);
            }
        }
    }

    /**
     * 摇一摇的点击事件,区分息屏和亮屏
     */
    private void setShakeClick (Activity activity, AdAudioBean bean) {
        CommonShakeEventUtils commonShakeEventUtils = new CommonShakeEventUtils(
                mWidth,
                mHeight,
                mHaveSnake,
                mHaveSnakeClick,
                mHaveDownStart,
                mHaveDownComplete,
                mHaveDownInstall
        );

        commonShakeEventUtils.onShakeEvent(
                activity,
                bean,
                0,
                mListener,
                new CommonShakeEventUtils.onPlayerStatusChangerListener() {
                    @Override
                    public void onPlayerStatusChanger (boolean pause) {
                        if (pause) {
                            MediaPlayerUtil.getInstance().stopAndRelease();
                        }
                    }
                });
    }

    /**
     * 音频广告播放的监听
     *
     * @param currentTime
     * @param place       0是开始,1是中间,2是结束,3是跳过
     */
    private void musicPlayExposure (int currentTime, int place) {
        if (mAdAudioBean != null && mAdAudioBean.getEvent() != null) {
            EventBean event = mAdAudioBean.getEvent();

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
                    if (mListener != null) {
                        mListener.onAdExposure();
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

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 释放摇一摇控件
     */
    private void clearShake () {
        LogUtils.e("qcsdk,clearShake========");
        if (mShakeUtils != null) {
            mShakeUtils.onPause();
            mShakeUtils.clear();
            mShakeUtils = null;
        }
    }
}
