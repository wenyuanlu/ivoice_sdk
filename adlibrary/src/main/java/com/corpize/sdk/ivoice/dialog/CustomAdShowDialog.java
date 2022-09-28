package com.corpize.sdk.ivoice.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.corpize.sdk.R;
import com.corpize.sdk.ivoice.bean.AdMusicBean;
import com.corpize.sdk.ivoice.bean.UpVoiceResultBean;
import com.corpize.sdk.ivoice.bean.response.AdAudioBean;
import com.corpize.sdk.ivoice.bean.response.EventBean;
import com.corpize.sdk.ivoice.bean.response.EventtrackersBean;
import com.corpize.sdk.ivoice.bean.response.InteractiveBean;
import com.corpize.sdk.ivoice.bean.response.RemindBean;
import com.corpize.sdk.ivoice.common.CommonHandler;
import com.corpize.sdk.ivoice.common.Constants;
import com.corpize.sdk.ivoice.listener.CountDownCallback;
import com.corpize.sdk.ivoice.listener.OnVolumeEndListener;
import com.corpize.sdk.ivoice.listener.QcRollAdViewListener;
import com.corpize.sdk.ivoice.utils.CommonShakeEventUtils;
import com.corpize.sdk.ivoice.utils.CommonSplicingResourceUtils;
import com.corpize.sdk.ivoice.utils.CustomMonitorVolumeUtils;
import com.corpize.sdk.ivoice.utils.DialogUtils;
import com.corpize.sdk.ivoice.utils.ImageUtils;
import com.corpize.sdk.ivoice.utils.LogUtils;
import com.corpize.sdk.ivoice.utils.MediaPlayerUtil;
import com.corpize.sdk.ivoice.utils.PermissionUtil;
import com.corpize.sdk.ivoice.utils.QcHttpUtil;
import com.corpize.sdk.ivoice.utils.ScreenUtils;
import com.corpize.sdk.ivoice.utils.ShakeInteractiveUtil;
import com.corpize.sdk.ivoice.utils.ShakeUtils;
import com.corpize.sdk.ivoice.utils.SpUtils;
import com.corpize.sdk.ivoice.utils.VoiceInteractiveUtil;
import com.corpize.sdk.ivoice.utils.downloadinstaller.DownloadInstaller;
import com.corpize.sdk.ivoice.utils.downloadinstaller.DownloadProgressCallBack;

import java.util.ArrayList;
import java.util.List;

import static com.corpize.sdk.ivoice.utils.StartMusicUtils.getReStartMusic;

/**
 * author : xpSun
 * date : 11/22/21
 * description :
 */
public class CustomAdShowDialog {

    private Dialog dialog;

    private View      rootView;
    private ImageView adBackground;
    private ImageView adVolume;
    private TextView  adAutoClose;
    private ImageView adClose;
    private TextView  titleShow;
    private TextView  descShow;

    private static CustomAdShowDialog instance;
    private        Activity           activity;

    private AdAudioBean     mResponse;
    private MediaPlayerUtil mediaPlayerUtil;
    private ShakeUtils      mShakeUtils;

    private int mWidth;
    private int mHeight;
    private int mPositionX;
    private int mPositionY;

    private boolean mHaveSnake      = false;   //是否摇一摇曝光
    private boolean mHaveSnakeClick = false;   //是否摇一摇曝光的点击事件

    private boolean mHaveSendViewShow           = false;//是否发送展示曝光请求
    private boolean mHaveSendAudioShow          = false;//是否发送音频展示曝光请求
    private boolean mHaveSendClick              = false;//是否发送点击曝光请求
    private boolean mHaveSendDeep               = false;//是否发送deeplink曝光请求
    private boolean mHaveDownStart              = false;//是否发送开始下载曝光请求
    private boolean mHaveDownComplete           = false;//是否发送完成下载曝光请求
    private boolean mHaveDownInstall            = false;//是否发送开始安装曝光请求
    private boolean mHaveFirstShow              = false;//是否第一次展示
    private boolean mHaveMusicStartPlay         = false;//是否音频播放开始监听发送
    private boolean mHaveMusicMidpointPlay      = false;//是否音频播放中间监听发送
    private boolean mHaveMusicFirstQuartilePlay = false;//是否音频播放四分之一监听发送
    private boolean mHaveMusicThirdQuartilePlay = false;//是否音频播放四分之三监听发送
    private boolean mHaveMusicCompletePlay      = false;//是否音频播放完成监听发送
    private boolean mHaveMusicClosePlay         = false;//是否音频播放跳过监听发送
    private boolean mHaveShowIcon               = false;//是否展示icon
    private boolean mHaveShowCover              = false;//是否展示cover
    private int     mAllTime                    = 0;//第一个广告音频的播放的总时长
    private int     mCurrentTime                = 0;

    private int     mIntervalTime = 0;//互动结束后摇一摇时间
    private int     callbackType  = 0;//播放结束后继续操作的类型
    private int     remindsTime;//重试次数
    private boolean currentIsMute = false;//当前是否静音

    private int mPosition;

    private CustomMonitorVolumeUtils customMonitorVolumeUtils;
    private QcRollAdViewListener     mListener;
    private AutoCloseCount           autoCloseCount;

    public static CustomAdShowDialog Builder (Activity activity) {
        return new CustomAdShowDialog(activity);
    }

    public void setListener (QcRollAdViewListener mListener) {
        this.mListener = mListener;
    }

    private CustomAdShowDialog (Activity activity) {
        this.activity = activity;
        initDialog();

        mediaPlayerUtil = MediaPlayerUtil.getInstance();
    }

    private void initDialog () {
        dialog = new Dialog(activity, R.style.common_dialog_style);
        rootView = LayoutInflater.from(activity).inflate(R.layout.view_custom_dialog_ad_layout, null, false);
        initWidget(rootView);
        dialog.setContentView(rootView);
        dialog.setCancelable(false);
    }

    private void initWidget (View rootView) {
        adBackground = rootView.findViewById(R.id.custom_dialog_ad_background);
        adVolume = rootView.findViewById(R.id.custom_dialog_ad_volume);
        adAutoClose = rootView.findViewById(R.id.custom_dialog_ad_auto_close);
        adClose = rootView.findViewById(R.id.custom_dialog_ad_close);
        titleShow = rootView.findViewById(R.id.custom_dialog_ad_title);
        descShow = rootView.findViewById(R.id.custom_dialog_ad_desc);

        adVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (null == mediaPlayerUtil) {
                    return;
                }

                Log.e("mResponse.getVolume()", String.valueOf(mResponse.getVolume()));

                if (0 == mediaPlayerUtil.getCurrentVolume()) {
                    currentIsMute = false;
                } else {
                    currentIsMute = true;
                }

                if (mListener != null) {
                    mListener.onRollVolumeChanger(currentIsMute ? 0 : 1);
                }

                setMediaVolume(mResponse.getVolume());
            }
        });

        adClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                dismissDialog();
                clearShake();
            }
        });

        adBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                setShakeClick(activity, mResponse);
            }
        });
    }

    private void setMediaVolume (int volume) {
        if (currentIsMute) {
            MediaPlayerUtil.getInstance().setMinVolume(0);
            adVolume.setImageResource(R.drawable.volume_cross);
        } else {
            MediaPlayerUtil.getInstance().setMinVolume(volume);
            adVolume.setImageResource(R.drawable.volume_hight);
        }
    }

    public void setAdAudioBean (AdAudioBean mResponse) {
        this.mResponse = mResponse;
        initView();
    }

    private void initView () {
        if (null == mResponse) {
            return;
        }

        if (mediaPlayerUtil != null) {
            mediaPlayerUtil.setMinVolume(mResponse.getVolume());
        }

        String title = mResponse.getTitle();
        String desc  = mResponse.getDesc();

        if (!TextUtils.isEmpty(mResponse.getFirstimg())) {
            //封面展示
            ImageUtils.loadImage(activity, mResponse.getFirstimg(), adBackground);
        }

        titleShow.setText(TextUtils.isEmpty(title) ? "" : title);
        descShow.setText(TextUtils.isEmpty(desc) ? "" : desc);
    }

    private class AutoCloseCount extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public AutoCloseCount (long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick (long millisUntilFinished) {
            adAutoClose.setText(String.format("%ss", millisUntilFinished / 1000));
        }

        @Override
        public void onFinish () {
        }
    }

    /**
     * 播放音频
     */
    private void playAd () {
        int[] position = new int[2];
        rootView.getLocationOnScreen(position);
        mPositionX = position[0]; // view距离 屏幕左边的距离（即x轴方向）
        mPositionY = position[1]; // view距离 屏幕顶边的距离（即y轴方向）
        if (mResponse != null) {
            callbackType = null == mResponse.getRendering_config() ? 0 : mResponse.getRendering_config().getStop_playing_mode();

            //判断权限
            int perMis = checkNeedPermissions();

            //判断是否有主体音频广告
            if (!TextUtils.isEmpty(mResponse.getAudiourl())) {
                managerAdMusic(mResponse.getAudiourl(), mResponse, perMis);
            } else {
                managerAdWithOutMusic(mResponse, perMis);
            }

            if (0 != mResponse.getDuration()) {
                autoCloseCount = new AutoCloseCount(mResponse.getDuration() * 1000, 1000);
                autoCloseCount.start();
            }
        }
    }

    /**
     * 处理并加载音频广告
     *
     * @param adUrl
     * @param bean
     */
    public void managerAdMusic (String adUrl, final AdAudioBean bean, int perMis) {
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
                    setMediaVolume(volume);
                    MediaPlayerUtil.getInstance().playVoice(activity, adUrl, mMediaOnListener);
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
                    if (!TextUtils.isEmpty(adMusicBean.getMusic())) {
                        voiceList.add(adMusicBean);
                    }

                    setMediaVolume(volume);
                    MediaPlayerUtil.getInstance().playVoiceList(activity, voiceList, mMediaMoreListener);
                    break;
                default:
                    break;
            }
        } else {
            //无互动的时候
            setMediaVolume(volume);
            MediaPlayerUtil.getInstance().playVoice(activity, adUrl, mMediaOnListener);
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
            InteractiveBean interactive = bean.getInteractive();
            remindsTime = bean.getInteractive().getReminds();
            String startMusic = CommonSplicingResourceUtils.getInstance().managerAdWithOutMusic(bean);
            if (!TextUtils.isEmpty(startMusic)) {
                //隐藏弹幕,隐藏弹幕按钮,隐藏播放按钮,开启互动播放,无结束页
                setMediaVolume(volume);
                MediaPlayerUtil.getInstance().playVoice(activity, startMusic, new MediaPlayerUtil.MediaOnListener() {
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
                            DialogUtils.noInteractiveCountDownTime(
                                    mIntervalTime,
                                    false,
                                    null,
                                    new CountDownCallback() {
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
     * 判断权限
     */
    private int checkNeedPermissions () {
        return PermissionUtil.checkAudioAndWritePermissions(activity);
    }

    /**
     * 初始化录音操作
     */
    private void initRecorderOperation () {
        customMonitorVolumeUtils = new CustomMonitorVolumeUtils(activity);
        VoiceInteractiveUtil.getInstance()
                .initRecorderOperation(
                        activity,
                        customMonitorVolumeUtils,
                        new OnVolumeEndListener() {
            @Override
            public void volumeEnd () {
                activity.runOnUiThread(new Runnable() {
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
     * 设置录音上传结果回调
     */
    private void setRecordListener () {
        VoiceInteractiveUtil.getInstance().setRecordListener(activity,
                new QcHttpUtil.QcHttpOnListener<UpVoiceResultBean>() {
                    @Override
                    public void OnQcCompletionListener (UpVoiceResultBean response) {
                        //1:肯定，0:否定，999: 无法识别
                        if (UpVoiceResultBean.FAIl == response.getCode()) {
                            // 否定，结束交互
                            onAdCompleteCallBack();
                        } else if (UpVoiceResultBean.SUCCESS == response.getCode()) {
                            //肯定，有互动的时候
                            setShakeClick(activity, mResponse);
                        } else if (UpVoiceResultBean.UN_KNOW == response.getCode()) {
                            //无法识别重新播放语音。重新采集
                            playRestartMusic(mResponse);
                        }
                    }

                    @Override
                    public void OnQcErrorListener (String error, int code) {
                        callbackType = 1;
                        // 否定，结束交互
                        onAdCompleteCallBack();
                    }
                });
    }

    /**
     * 初始化摇一摇操作
     */
    private void initShakeUtils () {
        //摇一摇
        if (mResponse != null) {
            mShakeUtils = new ShakeUtils(activity);
            ShakeInteractiveUtil.setOnShakeListener(activity, mShakeUtils,
                    customMonitorVolumeUtils, new ShakeUtils.OnShakeListener() {
                        @Override
                        public void onShake () {
                            //有互动的时候
                            setShakeClick(activity, mResponse);
                        }
                    });
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
    public MediaPlayerUtil.MediaMoreOnListener mMediaMoreListener = new MediaPlayerUtil.MediaMoreOnListener() {
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

    /**
     * 广告播完回调逻辑处理
     */
    private CommonHandler mHandler;

    private void onAdCompleteCallBack () {
        if (mListener != null) {
            if (callbackType == 1) {
                stopAndReleaseAd();
                mListener.onAdCompletion();
            } else {
                if (mHandler == null) {
                    mHandler = new CommonHandler<>(this);
                }
                mHandler.postDelay(5000, new CommonHandler.PostDelayCallBack() {
                    @Override
                    public void callBack () {
                        if (callbackType == 2) {
                            playAd();
                            callbackType = 1;
                        } else if (callbackType == 3) {
                            stopAndReleaseAd();
                            mListener.onAdCompletion();
                        }
                    }
                });
            }
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

        dismissDialog();
    }

    /**
     * 播放重试语音
     */
    private void playRestartMusic (AdAudioBean bean) {
        remindsTime--;
        LogUtils.e("remindsTime,playRestartMusic======" + remindsTime);
        if (remindsTime < 0) {
            onAdCompleteCallBack();
            //结束互动
            return;
        }
        final int             volume       = bean.getVolume();
        final InteractiveBean interactive  = bean.getInteractive();
        int                   perMis       = PermissionUtil.checkAudioAndWritePermissions(activity);
        int                   action       = bean.getAction();
        RemindBean            remind       = interactive.getRemind();
        String                reStartMusic = getReStartMusic(perMis, remind, action);

        setMediaVolume(volume);
        MediaPlayerUtil.getInstance().playVoice(activity, reStartMusic, new MediaPlayerUtil.MediaOnListener() {
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

                QcHttpUtil.sendAdExposure(url);
            }
        }
    }

    /**
     * 停止播放音频
     */
    public void stopAndReleaseAd () {
        try {
            MediaPlayerUtil.getInstance().stopAndRelease();
            musicPlayExposure(0, 3);
            DialogUtils.closeCountDownTime();
            closeCommonHandler();
            clearShake();
            closeAutoCountDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeAutoCountDown () {
        if (autoCloseCount != null) {
            autoCloseCount.cancel();
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
     * 下载的通用方法
     *
     * @param activity
     * @param bean
     */
    private void downApk (Activity activity, AdAudioBean bean) {
        if (bean != null && !TextUtils.isEmpty(bean.getLdp())) {
            String            downUrl       = bean.getLdp();
            EventtrackersBean eventtrackers = bean.getEventtrackers();
            downApk(activity, eventtrackers, downUrl);
        }
    }

    private void downApk (Activity activity, final EventtrackersBean eventtrackers, String downUrl) {
        new DownloadInstaller(mPosition, activity, downUrl, new DownloadProgressCallBack() {
            @Override
            public void downloadProgress (int progress) {
                if (!mHaveDownStart && eventtrackers != null) {
                    mHaveDownStart = true;
                    sendShowExposure(eventtrackers.getStartdownload());
                }

                if (progress == 100) {
                    if (!mHaveDownComplete && eventtrackers != null) {
                        mHaveDownComplete = true;
                        sendShowExposure(eventtrackers.getCompletedownload());
                    }
                }
            }

            @Override
            public void downloadException (Exception e) {
            }

            @Override
            public void onInstallStart () {
                LogUtils.d("开始安装=");
                if (!mHaveDownStart && eventtrackers != null) {
                    mHaveDownStart = true;
                    sendShowExposure(eventtrackers.getStartdownload());
                }
                if (!mHaveDownComplete && eventtrackers != null) {
                    mHaveDownComplete = true;
                    sendShowExposure(eventtrackers.getCompletedownload());
                }
                if (!mHaveDownInstall && eventtrackers != null) {
                    mHaveDownInstall = true;
                    sendShowExposure(eventtrackers.getStartinstall());
                }
            }
        }).start();
    }

    public void showDialog () {
        try {
            if (dialog != null) {
                dialog.show();
            }

            if (dialog != null && dialog.getWindow() != null) {
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();

                int spWidth  = SpUtils.getInt(Constants.SP_SCREEN_WIDTH_TAG);
                int spHeight = SpUtils.getInt(Constants.SP_SCREEN_HEIGHT_TAG);

                if (0 == spWidth && 0 == spHeight) {
                    spWidth = (int) (ScreenUtils.getScreenWidth(dialog.getContext()) * 0.9);
                    spHeight = (int) (ScreenUtils.getScreenHeight(dialog.getContext()) * 0.6);

                    if (0 != spWidth && 0 != spHeight) {
                        SpUtils.saveInt(Constants.SP_SCREEN_WIDTH_TAG, spWidth);
                        SpUtils.saveInt(Constants.SP_SCREEN_HEIGHT_TAG, spHeight);
                    }
                }

                lp.width = spWidth;
                lp.height = spHeight;
                dialog.getWindow().setAttributes(lp);

                mWidth = spWidth;
                mHeight = spHeight;

                if (0 != spWidth && 0 != spHeight) {
                    playAd();
                } else {
                    dismissDialog();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause () {
        stopAndReleaseAd();
    }

    private void dismissDialog () {
        destroy();

        if (null != mListener) {
            mListener.onRollAdDialogDismiss();
        }
    }

    public void destroy () {
        if (dialog != null) {
            dialog.dismiss();
        }

        pause();

        clearShake();
    }
}
