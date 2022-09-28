package com.corpize.sdk.ivoice.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.corpize.sdk.R;
import com.corpize.sdk.ivoice.bean.AdMusicBean;
import com.corpize.sdk.ivoice.bean.UpVoiceResultBean;
import com.corpize.sdk.ivoice.bean.response.AdAudioBean;
import com.corpize.sdk.ivoice.bean.response.EventBean;
import com.corpize.sdk.ivoice.bean.response.InteractiveBean;
import com.corpize.sdk.ivoice.bean.response.RemindBean;
import com.corpize.sdk.ivoice.common.CommonHandler;
import com.corpize.sdk.ivoice.listener.CountDownCallback;
import com.corpize.sdk.ivoice.listener.OnVolumeEndListener;
import com.corpize.sdk.ivoice.listener.QcFirstVoiceAdViewListener;
import com.corpize.sdk.ivoice.utils.CommonShakeEventUtils;
import com.corpize.sdk.ivoice.utils.CommonSplicingResourceUtils;
import com.corpize.sdk.ivoice.utils.CustomMonitorVolumeUtils;
import com.corpize.sdk.ivoice.utils.DialogUtils;
import com.corpize.sdk.ivoice.utils.LogUtils;
import com.corpize.sdk.ivoice.utils.MediaPlayerUtil;
import com.corpize.sdk.ivoice.utils.NetUtil;
import com.corpize.sdk.ivoice.utils.PermissionUtil;
import com.corpize.sdk.ivoice.utils.QcHttpUtil;
import com.corpize.sdk.ivoice.utils.ShakeInteractiveUtil;
import com.corpize.sdk.ivoice.utils.ShakeUtils;
import com.corpize.sdk.ivoice.utils.SpUtils;
import com.corpize.sdk.ivoice.utils.VoiceInteractiveUtil;

import java.util.ArrayList;
import java.util.List;

import static com.corpize.sdk.ivoice.utils.StartMusicUtils.getReStartMusic;
import static com.corpize.sdk.ivoice.utils.StartMusicUtils.getStartMusic;

/**
 * author : xpSun
 * date : 7/9/21
 * description :
 */
public class QcFirstVoiceView extends RelativeLayout {

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
    private boolean mHaveSnake                  = false;//是否摇一摇曝光
    private boolean mHaveSnakeClick             = false;//是否摇一摇曝光的点击事件
    private boolean isFirstCallBack             = true;//是否是第一次回调

    private int mAllTime     = 0;//第一个广告音频的播放的总时长
    private int mCurrentTime = 0;

    private int mWidth;
    private int mHeight;
    private int mPositionX;
    private int mPositionY;

    private int mIntervalTime = 0;//互动结束后摇一摇时间
    private int callbackType  = 0;//播放结束后继续操作的类型
    private int mPosition;
    private int remindsTime;//重试次数

    private View                     rootView;
    private ImageView                gifView;
    private ImageView                rotateView;
    private TextView                 skipView;
    private int                      closeTimer;
    private CustomShowCloseTimer     customShowCloseTimer;
    private ShakeUtils               mShakeUtils;
    private CustomMonitorVolumeUtils customMonitorVolumeUtils;

    private QcFirstVoiceAdViewListener mListener;

    /**
     * 广告播完回调逻辑处理
     */
    private CommonHandler mHandler;

    private AdAudioBean mResponse;
    private Activity    mActivity;

    public void setAdAudioBean (AdAudioBean mResponse) {
        this.mResponse = mResponse;

        if (null != mResponse) {
            if (!TextUtils.isEmpty(mResponse.getLogo())) {
                setIconResource(mResponse.getLogo());
            } else {
                setIconResource(R.raw.voice_icon);
            }
        }
    }

    public void setResponseListener(AdAudioBean adAudioBean){
        if (mListener != null) {
            mListener.onFetchApiResponse(adAudioBean);
        }
    }

    public void setQcFirstVoiceAdViewListener (QcFirstVoiceAdViewListener mListener) {
        this.mListener = mListener;
    }

    public QcFirstVoiceView (Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QcFirstVoiceView (Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    public QcFirstVoiceView (Activity activity) {
        this(activity, null);

        this.mActivity = activity;
    }

    private void setCloseTimer (int closeTimer) {
        if (0 == closeTimer) {
            this.closeTimer = 0;
            skipView.setText("关闭");
        } else {
            this.closeTimer = closeTimer * 1000;
            String value = String.valueOf(this.closeTimer);
            customShowCloseTimer = new CustomShowCloseTimer(Long.valueOf(value), 1000);
            customShowCloseTimer.start();
        }
    }

    private void init () {
        initWidgets();
        initAnimation();
        initWidgetsEvent();
    }

    private void initWidgets () {
        rootView = LayoutInflater.from(getContext())
                .inflate(R.layout.view_first_voice_icon_layout, this, false);
        gifView = rootView.findViewById(R.id.voice_icon_gif);
        skipView = rootView.findViewById(R.id.voice_icon_skip);
        rotateView = rootView.findViewById(R.id.voice_rotate_view);

        if (rootView != null) {
            addView(rootView);
        }
    }

    private void initWidgetsEvent () {
        skipView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick (View v) {
                if (0 == closeTimer) {
                    removeView();
                    clearShake();
                }
            }
        });

        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick (View v) {
                if (null != mResponse) {
                    setClick(mActivity, mResponse);
                }
            }
        });
    }

    private void removeView () {
        try {
            stopAndReleaseAd();
            ViewGroup viewGroup = (ViewGroup) getParent();

            if (null != viewGroup) {
                viewGroup.removeAllViews();
            }

            if (mListener != null) {
                mListener.onFirstVoiceAdClose();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (mListener != null) {
                mListener.onFirstVoiceAdClose();
            }
        }
    }

    private void initAnimation () {
        AnimationSet animationSet = new AnimationSet(true);
        Animation rotateAnimation = new RotateAnimation(
                -3,
                3,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        rotateAnimation.setDuration(200);
        rotateAnimation.setRepeatCount(-1);
        rotateAnimation.setRepeatMode(Animation.REVERSE);
        rotateAnimation.setFillAfter(true);

        animationSet.addAnimation(rotateAnimation);

        Animation scaleAnimation = new ScaleAnimation(
                1f, 1.1f, 1f, 1.1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        scaleAnimation.setDuration(400);
        scaleAnimation.setRepeatCount(-1);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setFillAfter(true);
        animationSet.addAnimation(scaleAnimation);

        rotateView.startAnimation(animationSet);
    }

    private void setIconResource (int resourceId) {
        if (gifView != null) {
            Glide.with(getContext())
                    .asGif()
                    .load(resourceId)
                    .into(gifView);
        }
    }

    private void setIconResource (String resourceId) {
        if (gifView != null && !TextUtils.isEmpty(resourceId)) {
            Glide.with(getContext()).load(resourceId).into(gifView);
        }
    }

    private class CustomShowCloseTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CustomShowCloseTimer (long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick (long millisUntilFinished) {
            skipView.setText(String.format("%ss后关闭", millisUntilFinished / 1000));
        }

        @Override
        public void onFinish () {
            skipView.setText("关闭");
            closeTimer = 0;

            if (mListener != null) {
                mListener.onFirstVoiceAdCountDownCompletion();
            }
        }
    }

    /**
     * 按钮的点击事件
     */
    private void setClick (Activity activity, AdAudioBean bean) {
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
                            stopAd();
                        }
                    }
                });
    }

    @Override
    protected void onLayout (boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        LogUtils.e("进入自定义控件的onLayout");
        mWidth = getWidth();
        mHeight = getHeight();

        int[] position = new int[2];
        getLocationOnScreen(position);
        mPositionX = position[0]; // view距离 屏幕左边的距离（即x轴方向）
        mPositionY = position[1]; // view距离 屏幕顶边的距离（即y轴方向）

        if (null != mResponse.getImps() && !mHaveSendViewShow) {
            mHaveSendViewShow = true;
            sendShowExposure(mResponse.getImps());//广告位曝光
            if (mListener != null) {
                mListener.onAdExposure();
            }
        }
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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

                if (!NetUtil.isNetConnected(getContext())) {
                    if (mListener != null) {
                        mListener.onFetchAdsSendShowExposure(url);
                    }
                }
            }
        }
    }

    /**
     * 播放音频
     */
    public void playAd () {
        int[] position = new int[2];
        getLocationOnScreen(position);
        mPositionX = position[0]; // view距离 屏幕左边的距离（即x轴方向）
        mPositionY = position[1]; // view距离 屏幕顶边的距离（即y轴方向）
        LogUtils.e("广告区域左上角坐标2X=" + mPositionX + "|Y=" + mPositionY);

        isFirstCallBack = true;

        if (mResponse != null) {
            callbackType = null == mResponse.getRendering_config() ? 1 : mResponse.getRendering_config().getStop_playing_mode();

            //判断权限
            int perMis = checkNeedPermissions();

            //判断是否有主体音频广告
            if (!TextUtils.isEmpty(mResponse.getAudiourl())) {
                managerAdMusic(mResponse.getAudiourl(), mResponse, perMis);
            } else {
                managerAdWithOutMusic(mResponse, perMis);
            }

            setCloseTimer(mResponse.getSkip());
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
                    MediaPlayerUtil.getInstance().playVoiceList(getContext(), voiceList, mMediaMoreListener);
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
     * 判断权限
     */
    private int checkNeedPermissions () {
        return PermissionUtil.checkAudioAndWritePermissions(getContext());
    }

    /**
     * 初始化录音操作
     */
    private void initRecorderOperation () {
        customMonitorVolumeUtils = new CustomMonitorVolumeUtils(getContext());
        VoiceInteractiveUtil.getInstance()
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
                                callbackType = 1;
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
                LogUtils.e("onPlayStartListener,onPlayStartListener======");
            }

            @Override
            public void onPlayCurrentTimeListener (int currentTime) {
                LogUtils.e("onPlayCurrentTimeListener,onPlayCurrentTimeListener======");
            }

            @Override
            public void onPlayCompletionListener () {
                LogUtils.e("onPlayCompletionListener,playRestartMusic======");
                //已经到了语音重试，证明是语音交互。并且有录音读写权限
                initRecorderOperation();
            }

            @Override
            public void onPlayErrorListener (int code, String msg) {
                LogUtils.e("onPlayErrorListener,onPlayErrorListener======");
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
     * 初始化摇一摇操作
     */
    private void initShakeUtils () {
        //摇一摇
        if (mResponse != null) {
            mShakeUtils = new ShakeUtils(getContext());
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
     * 重新播放音频
     */
    public void resumePlayAd () {
        MediaPlayerUtil.getInstance().resumePlay();
    }

    /**
     * 停止播放音频
     */
    private void stopAd () {
        MediaPlayerUtil.getInstance().stopPlay();
        removeView();
    }

    /**
     * 跳过广告 停止音频播放 给外部调用 需要添加close的曝光监听
     */
    public void skipAd () {
        stopAndReleaseAd();
    }

    /**
     * 停止播放音频
     */
    public void stopAndReleaseAd () {
        MediaPlayerUtil.getInstance().stopAndRelease();
        musicPlayExposure(0, 3);
        DialogUtils.closeCountDownTime();
        clearShake();

        if (customShowCloseTimer != null) {
            customShowCloseTimer.cancel();
        }

        this.closeTimer = 0;
    }

    /**
     * 点击广告操作后回调
     */
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == mPosition) {
            //为了解决弹窗选择浏览器框实际并未跳转
            if (!SpUtils.getBoolean("show")) {
                SpUtils.saveBoolean("show", true);
                stopAndReleaseAd();
                if (mListener != null && isFirstCallBack) {
                    isFirstCallBack = false;
                    mListener.onAdCompletion();
                }
            } else {
                resumePlayAd();
            }
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
                                    onAdCompleteCallBack();
                                }
                            });
                        } else {
                            onAdCompleteCallBack();
                        }
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

    private void onAdCompleteCallBack () {
        if (mListener != null) {
            if (callbackType == 1) {
                removeView();
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
                            removeView();
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
                            MediaPlayerUtil.getInstance().stopAndRelease();
                        }
                    }
                });

        removeView();
    }
}
