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

    private boolean mHaveSendViewShow           = false;//??????????????????????????????
    private boolean mHaveSendAudioShow          = false;//????????????????????????????????????
    private boolean mHaveSendClick              = false;//??????????????????????????????
    private boolean mHaveSendDeep               = false;//????????????deeplink????????????
    private boolean mHaveDownStart              = false;//????????????????????????????????????
    private boolean mHaveDownComplete           = false;//????????????????????????????????????
    private boolean mHaveDownInstall            = false;//????????????????????????????????????
    private boolean mHaveFirstShow              = false;//?????????????????????
    private boolean mHaveMusicStartPlay         = false;//????????????????????????????????????
    private boolean mHaveMusicMidpointPlay      = false;//????????????????????????????????????
    private boolean mHaveMusicFirstQuartilePlay = false;//??????????????????????????????????????????
    private boolean mHaveMusicThirdQuartilePlay = false;//??????????????????????????????????????????
    private boolean mHaveMusicCompletePlay      = false;//????????????????????????????????????
    private boolean mHaveMusicClosePlay         = false;//????????????????????????????????????
    private boolean mHaveShowIcon               = false;//????????????icon
    private boolean mHaveShowCover              = false;//????????????cover
    private boolean mHaveSnake                  = false;//?????????????????????
    private boolean mHaveSnakeClick             = false;//????????????????????????????????????
    private boolean isFirstCallBack             = true;//????????????????????????

    private int mAllTime     = 0;//??????????????????????????????????????????
    private int mCurrentTime = 0;

    private int mWidth;
    private int mHeight;
    private int mPositionX;
    private int mPositionY;

    private int mIntervalTime = 0;//??????????????????????????????
    private int callbackType  = 0;//????????????????????????????????????
    private int mPosition;
    private int remindsTime;//????????????

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
     * ??????????????????????????????
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
            skipView.setText("??????");
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
            skipView.setText(String.format("%ss?????????", millisUntilFinished / 1000));
        }

        @Override
        public void onFinish () {
            skipView.setText("??????");
            closeTimer = 0;

            if (mListener != null) {
                mListener.onFirstVoiceAdCountDownCompletion();
            }
        }
    }

    /**
     * ?????????????????????
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

        LogUtils.e("????????????????????????onLayout");
        mWidth = getWidth();
        mHeight = getHeight();

        int[] position = new int[2];
        getLocationOnScreen(position);
        mPositionX = position[0]; // view?????? ???????????????????????????x????????????
        mPositionY = position[1]; // view?????? ???????????????????????????y????????????

        if (null != mResponse.getImps() && !mHaveSendViewShow) {
            mHaveSendViewShow = true;
            sendShowExposure(mResponse.getImps());//???????????????
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
     * ????????????,???????????????????????????
     */
    private void sendShowExposure (List<String> imgList) {
        long time = System.currentTimeMillis();

        if (imgList != null && imgList.size() > 0) {
            for (int i = 0; i < imgList.size(); i++) {
                String urlOld = imgList.get(i);
                String url    = urlOld;
                if (url.contains("__WIDTH__")) {//????????????
                    url = url.replace("__WIDTH__", mWidth + "");
                }
                if (url.contains("__HEIGHT__")) {//????????????
                    url = url.replace("__HEIGHT__", mHeight + "");
                }
                if (url.contains("__POSITION_X__")) {//??????X????????????
                    url = url.replace("__POSITION_X__", mPositionX + "");
                }
                if (url.contains("__POSITION_Y__")) {//??????Y????????????
                    url = url.replace("__POSITION_Y__", mPositionY + "");
                }
                if (url.contains("__TIME_STAMP__")) {//??????????????????
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
     * ????????????
     */
    public void playAd () {
        int[] position = new int[2];
        getLocationOnScreen(position);
        mPositionX = position[0]; // view?????? ???????????????????????????x????????????
        mPositionY = position[1]; // view?????? ???????????????????????????y????????????
        LogUtils.e("???????????????????????????2X=" + mPositionX + "|Y=" + mPositionY);

        isFirstCallBack = true;

        if (mResponse != null) {
            callbackType = null == mResponse.getRendering_config() ? 1 : mResponse.getRendering_config().getStop_playing_mode();

            //????????????
            int perMis = checkNeedPermissions();

            //?????????????????????????????????
            if (!TextUtils.isEmpty(mResponse.getAudiourl())) {
                managerAdMusic(mResponse.getAudiourl(), mResponse, perMis);
            } else {
                managerAdWithOutMusic(mResponse, perMis);
            }

            setCloseTimer(mResponse.getSkip());
        }
    }


    /**
     * ???????????????????????????
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

            //?????????????????????????????????
            //????????????
            switch (perMis) {
                //???????????????
                case PermissionUtil.PERMISSION_CODE_NOPERMISSION:
                    //??????????????????
                    MediaPlayerUtil.getInstance().setMinVolume(volume);
                    MediaPlayerUtil.getInstance().playVoice(mActivity, adUrl, mMediaOnListener);
                    break;
                //????????????????????????
                case PermissionUtil.PERMISSION_CODE_SHAKE:
                    //???????????????????????????
                case PermissionUtil.PERMISSION_CODE_SHAKE_RECORD:
                    //???????????????????????????????????????
                case PermissionUtil.PERMISSION_CODE_SHAKE_RECORD_WRITE:
                    //????????????????????????
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
            //??????????????????
            MediaPlayerUtil.getInstance().setMinVolume(volume);
            MediaPlayerUtil.getInstance().playVoice(mActivity, adUrl, mMediaOnListener);
        }
    }

    /**
     * ??????????????????
     */
    private MediaPlayerUtil.MediaOnListener mMediaOnListener = new MediaPlayerUtil.MediaOnListener() {
        @Override
        public void onPlayStartListener (final int allTime) {
            mAllTime = allTime;
            mCurrentTime = 0;
            //??????????????????
            musicPlayExposure(0, 0);
        }

        @Override
        public void onPlayCurrentTimeListener (int currentTime) {
            mCurrentTime = currentTime;
            //?????????????????????
            musicPlayExposure(currentTime, 1);
        }

        @Override
        public void onPlayCompletionListener () {
            onAdCompleteCallBack();
            //????????????????????????
            musicPlayExposure(0, 2);
        }

        @Override
        public void onPlayErrorListener (int code, String msg) {
            if (mListener != null) {
                mListener.onAdError("??????????????????,????????????," + msg);
            }
        }

        @Override
        public void onAudioFocusChange (int focusChange) {
            if (AudioManager.AUDIOFOCUS_GAIN == focusChange && 1 != MediaPlayerUtil.getInstance().getUserClickStop()) {
                MediaPlayerUtil.getInstance().resumePlay();
            } else {
                if (MediaPlayerUtil.getInstance().currentPlayStatue == MediaPlayerUtil.getInstance().PLAY) {
                    //???????????????,???????????????,??????????????????
                    MediaPlayerUtil.getInstance().pausePlay();
                }
            }
        }
    };

    /**
     * ??????????????????
     */
    private MediaPlayerUtil.MediaMoreOnListener mMediaMoreListener = new MediaPlayerUtil.MediaMoreOnListener() {
        @Override
        public void onPlayStartListener (int position, final int allTime) {
            if (position == 0) {
                mAllTime = allTime;
                mCurrentTime = 0;
                //??????????????????
                musicPlayExposure(0, 0);
            }
        }

        @Override
        public void onPlayStatusChangeListener (int position, int status) {

        }

        @Override
        public void onPlayCurrentTimeListener (int position, int currentTime) {
            //?????????????????????
            if (position == 0) {
                mCurrentTime = currentTime;
                LogUtils.e("???????????????????????????More=" + currentTime);
                //????????????????????????
                musicPlayExposure(currentTime, 1);
            }
        }

        @Override
        public void onPlayCompletionListener () {
            //??????????????????????????????????????????????????????
            //????????????
            int perMis = checkNeedPermissions();
            switch (perMis) {
                //???????????????
                case PermissionUtil.PERMISSION_CODE_NOPERMISSION:
                    break;
                //????????????????????????
                case PermissionUtil.PERMISSION_CODE_SHAKE:
                    clearShake();
                    initShakeUtils();
                    //???????????????????????????
                case PermissionUtil.PERMISSION_CODE_SHAKE_RECORD:
                    //????????????????????????
                    if (mShakeUtils != null) {
                        DialogUtils.noInteractiveCountDownTime(
                                mIntervalTime,
                                false,
                                null,
                                new CountDownCallback() {
                                    @Override
                                    public void close () {
                                        //???????????????????????????
                                        clearShake();
                                        onAdCompleteCallBack();
                                    }
                                });
                    } else {
                        onAdCompleteCallBack();
                    }
                    break;
                //???????????????????????????????????????
                case PermissionUtil.PERMISSION_CODE_SHAKE_RECORD_WRITE:
                    clearShake();
                    initShakeUtils();
                    initRecorderOperation();
                    LogUtils.e("====???????????????????????????????????????=");
                    break;
                //????????????????????????
                case PermissionUtil.PERMISSION_CODE_RECORD_WRITE:
                    initRecorderOperation();
                    break;
                default:
                    break;
            }
        }

        //???????????????????????????????????????
        @Override
        public void onPlayCenterPositionListener (int position) {
            LogUtils.e("?????????OnPlayCenterPositionListener???position=" + position);
            if (position == 0) {
                //????????????????????????
                musicPlayExposure(0, 2);
            }
        }

        @Override
        public void onPlayErrorListener (int code, String msg) {
            if (mListener != null) {
                mListener.onAdError("??????????????????,????????????," + msg);
            }
        }

        @Override
        public void onAudioFocusChange (int focusChange) {
            if (AudioManager.AUDIOFOCUS_GAIN == focusChange && 1 != MediaPlayerUtil.getInstance().getUserClickStop()) {
                MediaPlayerUtil.getInstance().resumePlay();
            } else {
                if (MediaPlayerUtil.getInstance().currentPlayStatue == MediaPlayerUtil.getInstance().PLAY) {
                    //???????????????,???????????????,??????????????????
                    MediaPlayerUtil.getInstance().pausePlay();
                }
            }
        }
    };

    /**
     * ????????????
     */
    private int checkNeedPermissions () {
        return PermissionUtil.checkAudioAndWritePermissions(getContext());
    }

    /**
     * ?????????????????????
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
                                //????????????????????????????????????????????????????????????????????????
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
     * ??????????????????????????????
     */
    private void setRecordListener () {
        VoiceInteractiveUtil.getInstance()
                .setRecordListener(mActivity,
                        new QcHttpUtil.QcHttpOnListener<UpVoiceResultBean>() {
                            @Override
                            public void OnQcCompletionListener (UpVoiceResultBean response) {
                                //1:?????????0:?????????999: ????????????
                                if (UpVoiceResultBean.FAIl == response.getCode()) {
                                    // ?????????????????????
                                    onAdCompleteCallBack();
                                } else if (UpVoiceResultBean.SUCCESS == response.getCode()) {
                                    //???????????????????????????
                                    InteractiveBean interactive = mResponse.getInteractive();
                                    setShakeClick(mActivity, mResponse);
                                } else if (UpVoiceResultBean.UN_KNOW == response.getCode()) {
                                    //?????????????????????????????????????????????
                                    playRestartMusic(mResponse);
                                }
                            }

                            @Override
                            public void OnQcErrorListener (String error, int code) {
                                callbackType = 1;
                                // ?????????????????????
                                onAdCompleteCallBack();
                            }
                        });
    }

    /**
     * ??????????????????
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
                //??????????????????????????????????????????????????????????????????????????????
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
                        //???????????????,???????????????,??????????????????
                        MediaPlayerUtil.getInstance().pausePlay();
                    }
                }
            }
        });
    }

    /**
     * ????????????????????????
     */
    private void initShakeUtils () {
        //?????????
        if (mResponse != null) {
            mShakeUtils = new ShakeUtils(getContext());
            ShakeInteractiveUtil.setOnShakeListener(mActivity, mShakeUtils,
                    customMonitorVolumeUtils, new ShakeUtils.OnShakeListener() {

                        @Override
                        public void onShake () {
                            //??????????????????
                            setShakeClick(mActivity, mResponse);
                        }
                    });
        }
    }

    /**
     * ?????????????????????
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
     * ??????????????????
     */
    public void resumePlayAd () {
        MediaPlayerUtil.getInstance().resumePlay();
    }

    /**
     * ??????????????????
     */
    private void stopAd () {
        MediaPlayerUtil.getInstance().stopPlay();
        removeView();
    }

    /**
     * ???????????? ?????????????????? ??????????????? ????????????close???????????????
     */
    public void skipAd () {
        stopAndReleaseAd();
    }

    /**
     * ??????????????????
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
     * ???????????????????????????
     */
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == mPosition) {
            //??????????????????????????????????????????????????????
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
     * ???????????????????????????
     *
     * @param bean
     */
    private void managerAdWithOutMusic (final AdAudioBean bean, int perMis) {
        mIntervalTime = null == bean.getInteractive() ? 0 : bean.getInteractive().getWait();
        final int volume = bean.getVolume();
        if (bean.getInteractive() != null) {
            //??????????????????
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
                //App webview ????????????
                if (remind != null && remind.getOpenldp() != null && remind.getOpenldp().getShakeme() != null) {
                    startMusic = getStartMusic(perMis, remind, action);
                }
            } else if (action == 2) {
                //???????????????????????????
                if (remind != null && remind.getOpenldp() != null && remind.getOpenldp().getShakeme() != null) {
                    startMusic = getStartMusic(perMis, remind, action);
                }
            } else if (action == 3) {
                //????????????
                if (remind != null && remind.getPhone() != null && remind.getPhone().getShakeme() != null) {
                    startMusic = getStartMusic(perMis, remind, action);
                }
            } else if (action == 6) {
                //??????
                if (remind != null && remind.getDownload() != null && remind.getDownload().getShakeme() != null) {
                    startMusic = getStartMusic(perMis, remind, action);
                }
            } else if (action == 7) {
                //deeplink
                if (remind != null && remind.getDeeplink() != null && remind.getDeeplink().getShakeme() != null) {
                    startMusic = getStartMusic(perMis, remind, action);
                }
            } else if (action == 8) {
                //?????????
                if (remind != null && remind.getOpenldp() != null && remind.getOpenldp().getShakeme() != null) {
                    startMusic = getStartMusic(perMis, remind, action);
                }
            }
            if (!TextUtils.isEmpty(startMusic)) {
                //????????????,??????????????????,??????????????????,??????????????????,????????????
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
                        //??????action ????????????????????????
                        int perMis = checkNeedPermissions();

                        if (mShakeUtils != null) {
                            //????????????????????????
                            DialogUtils.noInteractiveCountDownTime(mIntervalTime, false, null, new CountDownCallback() {
                                @Override
                                public void close () {
                                    //???????????????????????????
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
                                //???????????????,???????????????,??????????????????
                                MediaPlayerUtil.getInstance().pausePlay();
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * ???????????????????????????
     *
     * @param currentTime
     * @param place       0?????????,1?????????,2?????????,3?????????
     */
    private void musicPlayExposure (int currentTime, int place) {
        if (mResponse != null && mResponse.getEvent() != null) {
            EventBean event = mResponse.getEvent();

            if (place == 0) {
                List<String> start = event.getStart();
                //???????????????
                if (start != null && !mHaveMusicStartPlay) {
                    mHaveMusicStartPlay = true;
                    sendShowExposure(start);
                }

                //???????????????,???????????????????????????,????????????????????????,???????????????
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
                //??????????????????
                if (firstQuartile != null && !mHaveMusicFirstQuartilePlay && currentTime * 4 > mAllTime) {
                    mHaveMusicFirstQuartilePlay = true;
                    sendShowExposure(firstQuartile);
                }

                //????????????
                if (midpoint != null && !mHaveMusicMidpointPlay && currentTime * 2 > mAllTime) {
                    mHaveMusicMidpointPlay = true;
                    sendShowExposure(midpoint);
                }

                //??????????????????
                if (thirdQuartile != null && !mHaveMusicThirdQuartilePlay && currentTime * 4 > mAllTime * 3) {
                    mHaveMusicThirdQuartilePlay = true;
                    sendShowExposure(thirdQuartile);
                }
            } else if (place == 2) {
                //???????????????
                List<String> complete = event.getComplete();
                if (complete != null && !mHaveMusicCompletePlay) {
                    mHaveMusicCompletePlay = true;
                    sendShowExposure(complete);
                }
            } else if (place == 3) {
                //?????????????????????,?????????????????????????????????,??????????????????????????????
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
     * ??????Handler?????????????????????
     */
    private void closeCommonHandler () {
        if (mHandler != null) {
            mHandler.releaseHandler();
            mHandler = null;
        }
    }

    /**
     * ????????????????????????,?????????????????????
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
