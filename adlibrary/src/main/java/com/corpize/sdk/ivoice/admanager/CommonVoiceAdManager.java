package com.corpize.sdk.ivoice.admanager;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.text.TextUtils;

import com.corpize.sdk.ivoice.bean.AdMusicBean;
import com.corpize.sdk.ivoice.bean.UpVoiceResultBean;
import com.corpize.sdk.ivoice.bean.response.AdAudioBean;
import com.corpize.sdk.ivoice.bean.response.EventBean;
import com.corpize.sdk.ivoice.bean.response.InteractiveBean;
import com.corpize.sdk.ivoice.bean.response.RemindBean;
import com.corpize.sdk.ivoice.common.Constants;
import com.corpize.sdk.ivoice.listener.CountDownCallback;
import com.corpize.sdk.ivoice.listener.OnVolumeEndListener;
import com.corpize.sdk.ivoice.listener.QcVoiceAdListener;
import com.corpize.sdk.ivoice.utils.CommonShakeEventUtils;
import com.corpize.sdk.ivoice.utils.CommonSplicingResourceUtils;
import com.corpize.sdk.ivoice.utils.CustomMonitorVolumeUtils;
import com.corpize.sdk.ivoice.utils.DialogUtils;
import com.corpize.sdk.ivoice.utils.LogUtils;
import com.corpize.sdk.ivoice.utils.MediaPlayerUtil;
import com.corpize.sdk.ivoice.utils.PermissionUtil;
import com.corpize.sdk.ivoice.utils.QcHttpUtil;
import com.corpize.sdk.ivoice.utils.ShakeInteractiveUtil;
import com.corpize.sdk.ivoice.utils.ShakeUtils;
import com.corpize.sdk.ivoice.utils.VoiceInteractiveUtil;

import java.util.ArrayList;
import java.util.List;

import static com.corpize.sdk.ivoice.utils.StartMusicUtils.getReStartMusic;
import static com.corpize.sdk.ivoice.utils.StartMusicUtils.getStartMusic;

/**
 * author : xpSun
 * date : 7/15/21
 * description :
 */
public class CommonVoiceAdManager extends QcAdManager {

    private Activity    mActivity;
    private AdAudioBean mResponse;
    private boolean     mHaveSendViewShow           = false;//是否发送展示曝光请求
    private boolean     mHaveSendAudioShow          = false;//是否发送音频展示曝光请求
    private boolean     mHaveSendClick              = false;//是否发送点击曝光请求
    private boolean     mHaveSendDeep               = false;//是否发送deeplink曝光请求
    private boolean     mHaveDownStart              = false;//是否发送开始下载曝光请求
    private boolean     mHaveDownComplete           = false;//是否发送完成下载曝光请求
    private boolean     mHaveDownInstall            = false;//是否发送开始安装曝光请求
    private boolean     mHaveFirstShow              = false;//是否第一次展示
    private boolean     mHaveMusicStartPlay         = false;//是否音频播放开始监听发送
    private boolean     mHaveMusicMidpointPlay      = false;//是否音频播放中间监听发送
    private boolean     mHaveMusicFirstQuartilePlay = false;//是否音频播放四分之一监听发送
    private boolean     mHaveMusicThirdQuartilePlay = false;//是否音频播放四分之三监听发送
    private boolean     mHaveMusicCompletePlay      = false;//是否音频播放完成监听发送
    private boolean     mHaveMusicClosePlay         = false;//是否音频播放跳过监听发送
    private boolean     mHaveShowIcon               = false;//是否展示icon
    private boolean     mHaveShowCover              = false;//是否展示cover
    private boolean     mHaveSnake                  = false;//是否摇一摇曝光
    private boolean     mHaveSnakeClick             = false;//是否摇一摇曝光的点击事件
    private boolean     isFirstCallBack             = true;//是否是第一次回调

    private int mAllTime     = 0;//第一个广告音频的播放的总时长
    private int mCurrentTime = 0;

    private int mIntervalTime = 0;//互动结束后摇一摇时间
    private int mPosition;
    private int remindsTime;//重试次数

    private QcVoiceAdListener        mListener;
    private CustomMonitorVolumeUtils customMonitorVolumeUtils;
    private ShakeUtils               mShakeUtils;
    private int                      isEnableLoadingDialog     = 0;
    private int                      delayPlayInteractionTimer = 0;

    public CommonVoiceAdManager (
            Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void setEnableLoadingDialog (boolean enableLoadingDialog) {
        isEnableLoadingDialog = enableLoadingDialog ? 1 : 2;
    }

    public void setDelayPlayInteractionTimer (int delayPlayInteractionTimer) {
        this.delayPlayInteractionTimer = delayPlayInteractionTimer;
    }

    public void setResponse (AdAudioBean mResponse) {
        this.mResponse = mResponse;
    }

    public void setListener (QcVoiceAdListener mListener) {
        this.mListener = mListener;
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
    public void destroy () {
        super.destroy();
        stopAndReleaseAd();
    }

    @Override
    public void startPlayAd () {
        super.startPlayAd();
        playAd();
    }

    @Override
    public void skipPlayAd () {
        super.skipPlayAd();
        stopAd();
    }

    @Override
    public void resumePlayAd () {
        super.resumePlayAd();
        resumePlay();
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 判断权限
     */
    private int checkNeedPermissions () {
        return PermissionUtil.checkAudioAndWritePermissions(mActivity);
    }

    /**
     * 播放音频
     */
    private void playAd () {
        if (Constants.INTERACTIVE_CONF_STATUS_3 != mResponse.getInteractive_conf()) {
            if (0 == delayPlayInteractionTimer) {
                delayPlayInteractionTimer = mResponse.getWaiting();
            }

            MediaPlayerUtil.getInstance().setDelayPlayInteractionTimer(delayPlayInteractionTimer);
        }

        if (mResponse != null) {
            //判断权限
            int perMis = checkNeedPermissions();

            //判断是否有主体音频广告
            if (!TextUtils.isEmpty(mResponse.getAudiourl())) {
                managerAdMusic(mResponse.getAudiourl(), mResponse, perMis);
            } else {
                managerAdWithOutMusic(mResponse, perMis);
            }
        }
    }

    /**
     * 处理并加载音频广告
     *
     * @param adUrl
     * @param bean
     */
    private void managerAdMusic (String adUrl, final AdAudioBean bean, int perMis) {
        mIntervalTime = null == bean.getInteractive() ? 0 : bean.getInteractive().getWait();
        final int volume = bean.getVolume();
        if (bean.getInteractive() != null) {
            remindsTime = bean.getInteractive().getReminds();

            List<AdMusicBean> voiceList = new ArrayList<>();
            voiceList.add(new AdMusicBean(adUrl));

            //添加广告结束后的提示音
            //判断权限
            switch (perMis) {
                //都没有权限
                case PermissionUtil.PERMISSION_CODE_NOPERMISSION:
                    //无互动的时候
                    MediaPlayerUtil.getInstance().setMinVolume(volume);
                    MediaPlayerUtil.getInstance().playVoice(mActivity, adUrl, mMediaOnListener);
                    break;
                //只有摇一摇的权限
                case PermissionUtil.PERMISSION_CODE_SHAKE:
                    //摇一摇和录音的权限
                case PermissionUtil.PERMISSION_CODE_SHAKE_RECORD:
                    //摇一摇和录音还有读写的权限
                case PermissionUtil.PERMISSION_CODE_SHAKE_RECORD_WRITE:
                    //有录音和读写权限
                case PermissionUtil.PERMISSION_CODE_RECORD_WRITE:
                    AdMusicBean adMusicBean = CommonSplicingResourceUtils.getInstance().checkAdActionForMusicPath(bean, perMis);
                    if (null != adMusicBean && !TextUtils.isEmpty(adMusicBean.getMusic())) {
                        voiceList.add(adMusicBean);
                    }

                    MediaPlayerUtil.getInstance().setMinVolume(volume);

                    MediaPlayerUtil.getInstance().playVoice(mActivity, adUrl, mMediaOnListener);
//                    if (Constants.INTERACTIVE_CONF_STATUS_3 == bean.getInteractive_conf()) {
//                        MediaPlayerUtil.getInstance().playVoice(mActivity, adUrl, mMediaOnListener);
//                    } else {
//                        MediaPlayerUtil.getInstance().playVoiceList(mActivity, voiceList, mMediaMoreListener);
//                    }
                    break;
                default:
                    break;
            }
        } else {
            //无互动的时候
            MediaPlayerUtil.getInstance().setMinVolume(volume);
            MediaPlayerUtil.getInstance().playVoice(mActivity, adUrl, mMediaOnListener);
        }
    }

    /**
     * 处理并加载音频广告
     *
     * @param bean
     */
    private void managerAdWithOutMusic (final AdAudioBean bean, int perMis) {
        mIntervalTime = null == bean.getInteractive() ? 0 : bean.getInteractive().getWait();
        final int volume = bean.getVolume();
        if (bean.getInteractive() != null) {
            //有互动的时候
            final InteractiveBean interactive = bean.getInteractive();
            remindsTime = bean.getInteractive().getReminds();
            int          action     = bean.getAction();
            String       ldp        = bean.getLdp();
            final int    waitTime   = null == interactive ? 0 : interactive.getWait();
            String       tpnumber   = bean.getTpnumber();
            List<String> clks       = bean.getClks();
            RemindBean   remind     = interactive.getRemind();
            String       startMusic = "";
            String       downMusic  = "";
            if (remind != null && remind.getDownload() != null && remind.getDownload().getShakeme() != null) {
                downMusic = remind.getDownload().getShakeme().getStart();
            }
            if (action == 1) {
                //App webview 打开链接
                if (remind != null && remind.getOpenldp() != null && remind.getOpenldp().getShakeme() != null) {
                    startMusic = getStartMusic(perMis, remind, action);
                }
            } else if (action == 2) {
                //系统浏览器打开链接
                if (remind != null && remind.getOpenldp() != null && remind.getOpenldp().getShakeme() != null) {
                    startMusic = getStartMusic(perMis, remind, action);
                }
            } else if (action == 3) {
                //拨打电话
                if (remind != null && remind.getPhone() != null && remind.getPhone().getShakeme() != null) {
                    startMusic = getStartMusic(perMis, remind, action);
                }
            } else if (action == 6) {
                //下载
                if (remind != null && remind.getDownload() != null && remind.getDownload().getShakeme() != null) {
                    startMusic = getStartMusic(perMis, remind, action);
                }
            } else if (action == 7) {
                //deeplink
                if (remind != null && remind.getDeeplink() != null && remind.getDeeplink().getShakeme() != null) {
                    startMusic = getStartMusic(perMis, remind, action);
                }
            } else if (action == 8) {
                //优惠券
                if (remind != null && remind.getOpenldp() != null && remind.getOpenldp().getShakeme() != null) {
                    startMusic = getStartMusic(perMis, remind, action);
                }
            }
            if (!TextUtils.isEmpty(startMusic)) {
                //隐藏弹幕,隐藏弹幕按钮,隐藏播放按钮,开启互动播放,无结束页
                MediaPlayerUtil.getInstance().setMinVolume(volume);
                MediaPlayerUtil.getInstance().playVoice(mActivity, startMusic, new MediaPlayerUtil.MediaOnListener() {
                    @Override
                    public void onPlayStartListener (int allTime) {

                    }

                    @Override
                    public void onPlayCurrentTimeListener (int currentTime) {

                    }

                    @Override
                    public void onPlayCompletionListener () {
                        //没有action 操作时的权限判断
                        int perMis = checkNeedPermissions();

                        if (mShakeUtils != null) {
                            //互动的等待倒计时
                            DialogUtils.noInteractiveCountDownTime(mIntervalTime, false, null, new CountDownCallback() {
                                @Override
                                public void close () {
                                    //结束后要清理摇一摇
                                    clearShake();
                                }
                            });
                        }
                        onAdCompleteCallBack();
                    }

                    @Override
                    public void onPlayErrorListener (int code, String msg) {

                    }

                    @Override
                    public void onAudioFocusChange (int focusChange) {
                        if (AudioManager.AUDIOFOCUS_GAIN == focusChange && 1 != MediaPlayerUtil.getInstance().getUserClickStop()) {
                            MediaPlayerUtil.getInstance().resumePlay();
                        } else {
                            if (MediaPlayerUtil.getInstance().currentPlayStatue == MediaPlayerUtil.getInstance().PLAY) {
                                //是播放状态,则暂停播放,显示播放按钮
                                MediaPlayerUtil.getInstance().pausePlay();
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * 初始化摇一摇操作
     */
    private void initShakeUtils () {
        //摇一摇
        if (mResponse != null) {
            mShakeUtils = new ShakeUtils(mActivity);
            ShakeInteractiveUtil.setOnShakeListener(mActivity, mShakeUtils,
                    customMonitorVolumeUtils, new ShakeUtils.OnShakeListener() {
                        @Override
                        public void onShake () {
                            //有互动的时候
                            setShakeClick(mActivity, mResponse);
                        }
                    });
        }
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

    /**
     * 单播放的监听
     */
    private MediaPlayerUtil.MediaOnListener mMediaOnListener = new MediaPlayerUtil.MediaOnListener() {
        @Override
        public void onPlayStartListener (final int allTime) {
            mAllTime = allTime;
            mCurrentTime = 0;
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
            if (AudioManager.AUDIOFOCUS_GAIN == focusChange && 1 != MediaPlayerUtil.getInstance().getUserClickStop()) {
                MediaPlayerUtil.getInstance().resumePlay();
            } else {
                if (MediaPlayerUtil.getInstance().currentPlayStatue == MediaPlayerUtil.getInstance().PLAY) {
                    //是播放状态,则暂停播放,显示播放按钮
                    MediaPlayerUtil.getInstance().pausePlay();
                }
            }
        }
    };

    /**
     * 多播放的监听
     */
    private MediaPlayerUtil.MediaMoreOnListener mMediaMoreListener = new MediaPlayerUtil.MediaMoreOnListener() {
        @Override
        public void onPlayStartListener (int position, final int allTime) {
            if (position == 0) {
                mAllTime = allTime;
                mCurrentTime = 0;
                //开始曝光监听
                musicPlayExposure(0, 0);
            }
        }

        @Override
        public void onPlayStatusChangeListener (int position, int status) {

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
            //广告播放结束后的提示音播放的操作逻辑
            //判断权限
            int perMis = checkNeedPermissions();
            switch (perMis) {
                //都没有权限
                case PermissionUtil.PERMISSION_CODE_NOPERMISSION:
                    break;
                //只有摇一摇的权限
                case PermissionUtil.PERMISSION_CODE_SHAKE:
                    clearShake();
                    initShakeUtils();
                    //摇一摇和录音的权限
                case PermissionUtil.PERMISSION_CODE_SHAKE_RECORD:
                    //互动的等待倒计时
                    if (mShakeUtils != null) {
                        DialogUtils.noInteractiveCountDownTime(
                                mIntervalTime,
                                false,
                                null,
                                new CountDownCallback() {
                                    @Override
                                    public void close () {
                                        //结束后要清理摇一摇
                                        clearShake();
                                        onAdCompleteCallBack();
                                    }
                                });
                    } else {
                        onAdCompleteCallBack();
                    }
                    break;
                //摇一摇和录音还有读写的权限
                case PermissionUtil.PERMISSION_CODE_SHAKE_RECORD_WRITE:
                    clearShake();
                    initShakeUtils();
                    initRecorderOperation();
                    LogUtils.e("====摇一摇和录音还有读写的权限=");
                    break;
                //有录音和读写权限
                case PermissionUtil.PERMISSION_CODE_RECORD_WRITE:
                    initRecorderOperation();
                    break;
                default:
                    break;
            }
        }

        //不是最后一个音频的播放完成
        @Override
        public void onPlayCenterPositionListener (int position) {
            LogUtils.e("返回了OnPlayCenterPositionListener的position=" + position);
            if (position == 0) {
                //播放完成曝光监听
                musicPlayExposure(0, 2);
                if (mListener != null) {
                    mListener.onAdPlayEndListener();
                }
            }
        }

        @Override
        public void onPlayErrorListener (int code, String msg) {
            if (mListener != null) {
                mListener.onAdError("广告获取成功,播放失败," + msg);
            }
        }

        @Override
        public void onAudioFocusChange (int focusChange) {
            if (AudioManager.AUDIOFOCUS_GAIN == focusChange && 1 != MediaPlayerUtil.getInstance().getUserClickStop()) {
                MediaPlayerUtil.getInstance().resumePlay();
            } else {
                if (MediaPlayerUtil.getInstance().currentPlayStatue == MediaPlayerUtil.getInstance().PLAY) {
                    //是播放状态,则暂停播放,显示播放按钮
                    MediaPlayerUtil.getInstance().pausePlay();
                }
            }
        }
    };

    private void onAdCompleteCallBack () {
        if (mListener != null) {
            stopAndReleaseAd();
            mListener.onAdCompletion();
        }
    }

    /**
     * 初始化录音操作
     */
    private void initRecorderOperation () {
        customMonitorVolumeUtils = new CustomMonitorVolumeUtils(mActivity);

        if (0 == isEnableLoadingDialog) {
            isEnableLoadingDialog = mResponse.getInteractive_conf();
        }

        //0:默认值,不启用对话框
        //1:启用对话框
        //2:后台或sdk外部传入不启用对话框
        boolean isEnable = 1 == isEnableLoadingDialog;

        VoiceInteractiveUtil.getInstance()
                .setEnableDialog(isEnable)
                .initRecorderOperation(mActivity, customMonitorVolumeUtils, new OnVolumeEndListener() {
                    @Override
                    public void volumeEnd () {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run () {
                                //音频采集完成，此次交互为语音交互。关闭摇一摇监听
                                clearShake();
                                customMonitorVolumeUtils.stopNoiseLevel();
                            }
                        });
                    }
                });
        customMonitorVolumeUtils.getNoiseLevel();
        setRecordListener();
    }

    /**
     * 摇一摇的点击事件,区分息屏和亮屏
     */
    private void setShakeClick (Activity activity, AdAudioBean bean) {
        CommonShakeEventUtils commonShakeEventUtils = new CommonShakeEventUtils(
                0,
                0,
                mHaveSnake,
                mHaveSnakeClick,
                mHaveDownStart,
                mHaveDownComplete,
                mHaveDownInstall
        );

        commonShakeEventUtils.onShakeEvent(
                activity,
                bean,
                mPosition,
                mListener,
                new CommonShakeEventUtils.onPlayerStatusChangerListener() {
                    @Override
                    public void onPlayerStatusChanger (boolean pause) {
                        if (pause) {
                            stopAndReleaseAd();
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
                    url = url.replace("__WIDTH__", 0 + "");
                }
                if (url.contains("__HEIGHT__")) {//高度替换
                    url = url.replace("__HEIGHT__", 0 + "");
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
     * 重新播放音频
     */
    private void resumePlay () {
        MediaPlayerUtil.getInstance().resumePlay();
    }

    /**
     * 停止播放音频
     */
    private void stopAd () {
        MediaPlayerUtil.getInstance().stopPlay();
    }

    /**
     * 跳过广告 停止音频播放 给外部调用 需要添加close的曝光监听
     */
    private void skipAd () {
        stopAndReleaseAd();
    }

    /**
     * 停止播放音频
     */
    private void stopAndReleaseAd () {
        MediaPlayerUtil.getInstance().stopAndRelease();
        musicPlayExposure(0, 3);
        DialogUtils.closeCountDownTime();
        clearShake();
    }

    /**
     * 设置录音上传结果回调
     */
    private void setRecordListener () {
        VoiceInteractiveUtil.getInstance()
                .setRecordListener(mActivity,
                        new QcHttpUtil.QcHttpOnListener<UpVoiceResultBean>() {
                            @Override
                            public void OnQcCompletionListener (UpVoiceResultBean response) {
                                //1:肯定，0:否定，999: 无法识别
                                if (UpVoiceResultBean.FAIl == response.getCode()) {
                                    // 否定，结束交互
                                    onAdCompleteCallBack();
                                } else if (UpVoiceResultBean.SUCCESS == response.getCode()) {
                                    //肯定，有互动的时候
                                    InteractiveBean interactive = mResponse.getInteractive();
                                    setShakeClick(mActivity, mResponse);
                                } else if (UpVoiceResultBean.UN_KNOW == response.getCode()) {
                                    //无法识别重新播放语音。重新采集
                                    playRestartMusic(mResponse);
                                }
                            }

                            @Override
                            public void OnQcErrorListener (String error, int code) {
                                // 否定，结束交互
                                onAdCompleteCallBack();
                            }
                        });
    }

    /**
     * 播放重试语音
     */
    private void playRestartMusic (AdAudioBean bean) {
        remindsTime--;
        LogUtils.e("remindsTime,playRestartMusic======" + remindsTime);
        if (remindsTime < 0) {
            onAdCompleteCallBack();
            return;
        }

        final int             volume       = bean.getVolume();
        final InteractiveBean interactive  = bean.getInteractive();
        int                   perMis       = checkNeedPermissions();
        int                   action       = bean.getAction();
        RemindBean            remind       = interactive.getRemind();
        String                reStartMusic = getReStartMusic(perMis, remind, action);

        MediaPlayerUtil.getInstance().setMinVolume(volume);
        MediaPlayerUtil.getInstance().playVoice(mActivity, reStartMusic, new MediaPlayerUtil.MediaOnListener() {
            @Override
            public void onPlayStartListener (int allTime) {

            }

            @Override
            public void onPlayCurrentTimeListener (int currentTime) {

            }

            @Override
            public void onPlayCompletionListener () {
                LogUtils.e("onPlayCompletionListener,playRestartMusic======");
                //已经到了语音重试，证明是语音交互。并且有录音读写权限
                initRecorderOperation();
            }

            @Override
            public void onPlayErrorListener (int code, String msg) {

            }

            @Override
            public void onAudioFocusChange (int focusChange) {
                if (AudioManager.AUDIOFOCUS_GAIN == focusChange && 1 != MediaPlayerUtil.getInstance().getUserClickStop()) {
                    MediaPlayerUtil.getInstance().resumePlay();
                } else {
                    if (MediaPlayerUtil.getInstance().currentPlayStatue == MediaPlayerUtil.getInstance().PLAY) {
                        //是播放状态,则暂停播放,显示播放按钮
                        MediaPlayerUtil.getInstance().pausePlay();
                    }
                }
            }
        });
    }
}
