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

    private boolean mHaveSnake      = false;   //?????????????????????
    private boolean mHaveSnakeClick = false;   //????????????????????????????????????

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
    private int     mAllTime                    = 0;//??????????????????????????????????????????
    private int     mCurrentTime                = 0;

    private int     mIntervalTime = 0;//??????????????????????????????
    private int     callbackType  = 0;//????????????????????????????????????
    private int     remindsTime;//????????????
    private boolean currentIsMute = false;//??????????????????

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
            //????????????
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
     * ????????????
     */
    private void playAd () {
        int[] position = new int[2];
        rootView.getLocationOnScreen(position);
        mPositionX = position[0]; // view?????? ???????????????????????????x????????????
        mPositionY = position[1]; // view?????? ???????????????????????????y????????????
        if (mResponse != null) {
            callbackType = null == mResponse.getRendering_config() ? 0 : mResponse.getRendering_config().getStop_playing_mode();

            //????????????
            int perMis = checkNeedPermissions();

            //?????????????????????????????????
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
     * ???????????????????????????
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

            //?????????????????????????????????
            //????????????
            switch (perMis) {
                //???????????????
                case PermissionUtil.PERMISSION_CODE_NOPERMISSION:
                    //??????????????????
                    setMediaVolume(volume);
                    MediaPlayerUtil.getInstance().playVoice(activity, adUrl, mMediaOnListener);
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
            //??????????????????
            setMediaVolume(volume);
            MediaPlayerUtil.getInstance().playVoice(activity, adUrl, mMediaOnListener);
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
            InteractiveBean interactive = bean.getInteractive();
            remindsTime = bean.getInteractive().getReminds();
            String startMusic = CommonSplicingResourceUtils.getInstance().managerAdWithOutMusic(bean);
            if (!TextUtils.isEmpty(startMusic)) {
                //????????????,??????????????????,??????????????????,??????????????????,????????????
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
                        //??????action ????????????????????????
                        int perMis = checkNeedPermissions();
                        if (mShakeUtils != null) {
                            //????????????????????????
                            DialogUtils.noInteractiveCountDownTime(
                                    mIntervalTime,
                                    false,
                                    null,
                                    new CountDownCallback() {
                                        @Override
                                        public void close () {
                                            //???????????????????????????
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
     * ????????????
     */
    private int checkNeedPermissions () {
        return PermissionUtil.checkAudioAndWritePermissions(activity);
    }

    /**
     * ?????????????????????
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
        VoiceInteractiveUtil.getInstance().setRecordListener(activity,
                new QcHttpUtil.QcHttpOnListener<UpVoiceResultBean>() {
                    @Override
                    public void OnQcCompletionListener (UpVoiceResultBean response) {
                        //1:?????????0:?????????999: ????????????
                        if (UpVoiceResultBean.FAIl == response.getCode()) {
                            // ?????????????????????
                            onAdCompleteCallBack();
                        } else if (UpVoiceResultBean.SUCCESS == response.getCode()) {
                            //???????????????????????????
                            setShakeClick(activity, mResponse);
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
     * ????????????????????????
     */
    private void initShakeUtils () {
        //?????????
        if (mResponse != null) {
            mShakeUtils = new ShakeUtils(activity);
            ShakeInteractiveUtil.setOnShakeListener(activity, mShakeUtils,
                    customMonitorVolumeUtils, new ShakeUtils.OnShakeListener() {
                        @Override
                        public void onShake () {
                            //??????????????????
                            setShakeClick(activity, mResponse);
                        }
                    });
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
    public MediaPlayerUtil.MediaMoreOnListener mMediaMoreListener = new MediaPlayerUtil.MediaMoreOnListener() {
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
     * ??????????????????????????????
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
                            stopAndReleaseAd();
                        }
                    }
                });

        dismissDialog();
    }

    /**
     * ??????????????????
     */
    private void playRestartMusic (AdAudioBean bean) {
        remindsTime--;
        LogUtils.e("remindsTime,playRestartMusic======" + remindsTime);
        if (remindsTime < 0) {
            onAdCompleteCallBack();
            //????????????
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
                //??????????????????????????????????????????????????????????????????????????????
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
                        //???????????????,???????????????,??????????????????
                        MediaPlayerUtil.getInstance().pausePlay();
                    }
                }
            }
        });
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
            }
        }
    }

    /**
     * ??????????????????
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
     * ??????Handler?????????????????????
     */
    private void closeCommonHandler () {
        if (mHandler != null) {
            mHandler.releaseHandler();
            mHandler = null;
        }
    }

    /**
     * ?????????????????????
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
                LogUtils.d("????????????=");
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
