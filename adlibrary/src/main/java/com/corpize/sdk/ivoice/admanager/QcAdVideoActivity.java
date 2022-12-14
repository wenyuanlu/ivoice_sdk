package com.corpize.sdk.ivoice.admanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.corpize.sdk.R;
import com.corpize.sdk.ivoice.bean.AdAudioVideoBean;
import com.corpize.sdk.ivoice.bean.response.AdAudioBean;
import com.corpize.sdk.ivoice.bean.response.EventtrackersBean;
import com.corpize.sdk.ivoice.bean.response.InteractiveBean;
import com.corpize.sdk.ivoice.bean.response.RemindBean;
import com.corpize.sdk.ivoice.common.CommonHandler;
import com.corpize.sdk.ivoice.common.CommonUtils;
import com.corpize.sdk.ivoice.common.Constants;
import com.corpize.sdk.ivoice.listener.AudioQcAdListener;
import com.corpize.sdk.ivoice.listener.CountDownCallback;
import com.corpize.sdk.ivoice.listener.ScreenListener;
import com.corpize.sdk.ivoice.utils.DeviceUtil;
import com.corpize.sdk.ivoice.utils.DialogUtils;
import com.corpize.sdk.ivoice.utils.ImageUtils;
import com.corpize.sdk.ivoice.utils.LogUtils;
import com.corpize.sdk.ivoice.utils.MediaPlayerUtil;
import com.corpize.sdk.ivoice.utils.QcHttpUtil;
import com.corpize.sdk.ivoice.utils.ShakeInteractiveUtil;
import com.corpize.sdk.ivoice.utils.ShakeUtils;
import com.corpize.sdk.ivoice.utils.SpUtils;
import com.corpize.sdk.ivoice.utils.downloadinstaller.DonwloadSaveImg;
import com.corpize.sdk.ivoice.utils.downloadinstaller.DownloadInstaller;
import com.corpize.sdk.ivoice.utils.downloadinstaller.DownloadProgressCallBack;
import com.corpize.sdk.ivoice.video.CustomCountDownTimer;
import com.corpize.sdk.ivoice.video.MyTextureView;
import com.corpize.sdk.ivoice.video.ThirdAppUtils;
import com.corpize.sdk.ivoice.view.QcadImageView;

import java.util.List;

/**
 * author ???yh
 * date : 2020-02-15 22:11
 * description : ???????????????????????????
 */
public class QcAdVideoActivity extends Activity implements View.OnClickListener {

    private AdAudioBean       mAdmBean;
    private InteractiveBean   mInteractiveBean;
    private int               mMinVolum     = 0;//????????????
    private int               mIntervalTime = 0;//???????????????
    private AudioQcAdListener mListener;
    private String            mUrl;

    private RelativeLayout mRlPlay;
    private MyTextureView  mTextureView;
    private ImageView      mIvPreview;
    private ImageView      mIvClose;
    private ProgressBar    mLoading;
    private TextView       mTvTimeDown;
    private LinearLayout   mLlTimeDown;
    private TextView       mTvRefresh;

    private LinearLayout mBottonLl;
    private TextView     mBtDownApk;

    private LinearLayout mLastLl;
    private TextView     mLastBtDownApk;

    private ImageView     mIvIcon;
    private TextView      mTvTitle;
    private TextView      mTvContent;
    private QcadImageView mIvIconLast;
    private TextView      mTvTitleLast;
    private TextView      mTvContentLast;

    private CountDownTimer       mCountDownPreview;
    private CustomCountDownTimer mCountDownTime;
    private boolean              mIsHavePlayComplate        = false;  //??????????????? ?????????, ?????????????????????
    private boolean              mIsHaveTextureDestroy      = false;  //????????????????????????(????????????????????????????????????)
    private int                  mVideoAllTime              = 30000;  //????????????????????????,??????
    private int                  mBackVideoAllTime          = 30000;  //????????????????????????,??????
    private int                  mVideoSecondTime           = 30;     //????????????????????????,??????
    private boolean              mHaveSendStartPlay         = false;  //??????????????????????????????????????????
    private boolean              mHaveSendMidpointPlay      = false;  //??????????????????????????????????????????
    private boolean              mHaveSendFirstQuartilePlay = false;  //????????????????????????????????????????????????
    private boolean              mHaveSendThirdQuartilePlay = false;  //????????????????????????????????????????????????
    private boolean              mHaveSendCompletePlay      = false;  //??????????????????????????????????????????
    private boolean              mHaveSendClosePlay         = false;  //??????????????????????????????????????????
    private boolean              mHaveDownStart             = false;  //????????????????????????????????????
    private boolean              mHaveDownComplete          = false;  //????????????????????????????????????
    private boolean              mHaveDownInstall           = false;  //????????????????????????????????????
    private boolean              mHaveClicked               = false;  //?????????????????????
    private boolean              isFirstInActivity          = true;   //????????????????????????activity
    private boolean              isFirstPlayVideo           = true;   //??????????????????????????????
    private boolean              mHaveSnake                 = false;  //?????????????????????
    private boolean              isFront;                        //??????????????????
    private int                  mScreenWidth;
    private int                  mScreenHeight;
    private int                  mSkipTime                  = 0;//??????????????????????????????
    private float                mClickX;                     //?????? ????????????X
    private float                mClickY;                     //?????? ????????????Y
    private ImageView            mIvAdShow;

    private ShakeUtils mShakeUtils;
    private boolean    isBackground = true;//???????????????

    private ScreenListener screenListener;            //??????????????????
    private boolean        isFirstCallBack = true;//????????????????????????

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = newConfig.orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LogUtils.e("-------------??????-------------");
            if (mTextureView != null) {
                mTextureView.updateTextureViewSizeCenter(true);
            }
        } else {
            LogUtils.e("-------------??????-------------");
            if (mTextureView != null) {
                mTextureView.updateTextureViewSizeCenter(false);
            }
        }
        LogUtils.e("???????????????onConfigurationChanged: " + orientation);
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //??????????????????
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //????????????
        setContentView(R.layout.activity_qcad_video_paly);

        mListener = AudioAdManager.get().getListener();
        mAdmBean = (AdAudioBean) getIntent().getSerializableExtra("dataBean");
        mInteractiveBean = (InteractiveBean) getIntent().getSerializableExtra("interactiveBean");
        mMinVolum = getIntent().getIntExtra("volum", 0);
        mIntervalTime = getIntent().getIntExtra("intervalTime", 0);
        MediaPlayerUtil.getInstance().setMinVolume(mMinVolum);
        if (mAdmBean != null) {
            //???????????????
            initView();
            //???????????????
            initData();
            //????????????
            play(mUrl);
        }

        setScreenListener();

    }

    private void setScreenListener () {
        screenListener = new ScreenListener(QcAdVideoActivity.this);
        screenListener.begin(new ScreenListener.ScreenStateListener() {
            @Override
            public void onScreenOn () {
                Log.e("qcvideo", "???????????????");
            }

            @Override
            public void onScreenOff () {
                Log.e("qcvideo", "???????????????");
                //??????????????????,???????????????,????????????
                if (mTextureView != null) {
                    mTextureView.pause();
                }
            }

            @Override
            public void onUserPresent () {
                Log.e("qcvideo", "?????????");

            }
        });

    }

    /**
     * ????????????????????????
     */
    private void initView () {
        if (null == mAdmBean) {
            return;
        }

        int width  = mAdmBean.getWidth();
        int height = mAdmBean.getHeight();

        if (null != mAdmBean.getCompanion()
                && null != mAdmBean.getCompanion().getVideo()
                && !TextUtils.isEmpty(mAdmBean.getCompanion().getVideo().getUrl())) {
            mUrl = mAdmBean.getCompanion().getVideo().getUrl();
        }

        mRlPlay = findViewById(R.id.dialog_ad_rl_paly);
        mTextureView = findViewById(R.id.dialog_ad_textureview);
        mIvPreview = findViewById(R.id.dialog_ad_iv_preview);
        mLoading = findViewById(R.id.dialog_ad_loading);
        mLlTimeDown = findViewById(R.id.ll_ad_time_down);
        mTvTimeDown = findViewById(R.id.dialog_ad_time_down);

        //??????????????????
        mIvClose = findViewById(R.id.dialog_ad_iv_close);
        mTvRefresh = findViewById(R.id.dialog_ad_refresh);
        mBtDownApk = findViewById(R.id.bt_ad_download);
        mLastBtDownApk = findViewById(R.id.bt_ad_download_last);

        //????????????????????????
        mIvIcon = findViewById(R.id.ad_icon);
        mTvTitle = findViewById(R.id.tv_ad_title);
        mTvContent = findViewById(R.id.tv_ad_content);
        mIvIconLast = findViewById(R.id.ad_icon_last);
        mTvTitleLast = findViewById(R.id.tv_ad_title_last);
        mTvContentLast = findViewById(R.id.tv_ad_content_last);

        //??????????????????
        mLastLl = findViewById(R.id.dialog_ad_ll_last);
        mBottonLl = findViewById(R.id.dialog_ad_ll_bottom);

        //????????????
        mIvAdShow = (ImageView) findViewById(R.id.iv_ad_show);
        ImageUtils.loadImage(this, Constants.AD_ICON, mIvAdShow);
        mTvRefresh.setOnClickListener(this);
        mLlTimeDown.setOnClickListener(this);
        mIvClose.setOnClickListener(this);
        mBottonLl.setOnClickListener(this);
        mLastBtDownApk.setOnClickListener(this);

        getClickXYPosition1(mBottonLl);
        getClickXYPosition2(mLastBtDownApk);

        // ????????????????????????????????????????????????
        mScreenWidth = DeviceUtil.getScreenWidth(this);
        mScreenHeight = DeviceUtil.getRealyScreenHeight(this);
        //RelativeLayout.LayoutParams mVideoParams = (RelativeLayout.LayoutParams) mRlPlay.getLayoutParams();
        //mVideoParams.width = mScreenWidth;
        //mVideoParams.height = mScreenWidth * height / width;
        //mVideoParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        // ???????????????????????????????????????
        LinearLayout.LayoutParams mLastBtDownApkParams = (LinearLayout.LayoutParams) mLastBtDownApk.getLayoutParams();
        int                       lastBtDownApkWidth   = mScreenWidth * 280 / 375;
        mLastBtDownApkParams.width = lastBtDownApkWidth;
        mLastBtDownApkParams.height = lastBtDownApkWidth * 40 / 280;
        mLastBtDownApk.setLayoutParams(mLastBtDownApkParams);

        // ?????????????????????????????????
        RelativeLayout.LayoutParams mBottonLlParams = (RelativeLayout.LayoutParams) mBottonLl.getLayoutParams();
        mBottonLlParams.width = mScreenWidth * 360 / 375;
        mBottonLl.setLayoutParams(mBottonLlParams);

    }

    /**
     * ?????????????????????
     */
    private void initData () {
        //???????????????????????????,??????
        if (mAdmBean != null) {//deepling (ldp???)
            String title    = mAdmBean.getTitle();
            String desc     = mAdmBean.getDesc();
            String ldp      = mAdmBean.getLdp();
            String firstimg = mAdmBean.getFirstimg();
            int    action   = mAdmBean.getAction();
            int    wigth    = mAdmBean.getWidth();
            int    higth    = mAdmBean.getHeight();
            mSkipTime = mAdmBean.getSkip();//????????????
            mBackVideoAllTime = mAdmBean.getDuration() * 1000;//?????????????????????????????????
            mVideoSecondTime = mAdmBean.getDuration();//??????????????????????????????

            if (!TextUtils.isEmpty(title)) {
                mTvTitle.setText(title);
                mTvTitleLast.setText(title);
                mTvTitle.setVisibility(View.VISIBLE);
                mTvTitleLast.setVisibility(View.VISIBLE);
            } else {
                mTvTitle.setVisibility(View.INVISIBLE);
                mTvTitleLast.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(desc)) {
                mTvContent.setText(desc);
                mTvContentLast.setText(desc);
                mTvContent.setVisibility(View.VISIBLE);
                mTvContentLast.setVisibility(View.VISIBLE);
            } else {
                mTvContent.setVisibility(View.GONE);
                mTvContentLast.setVisibility(View.INVISIBLE);
            }
            if (action == 6) {//??????
                mBtDownApk.setText("????????????");
                mLastBtDownApk.setText("????????????");
            } else if (action == 1 || action == 2) {
                mBtDownApk.setText("????????????");
                mLastBtDownApk.setText("????????????");
            } else if (action == 7) {
                mBtDownApk.setText("????????????");
                mLastBtDownApk.setText("????????????");
            } else if (action == 3) {
                mBtDownApk.setText("????????????");
                mLastBtDownApk.setText("????????????");
            } else if (action == 8) {
                mBtDownApk.setText("????????????");
                mLastBtDownApk.setText("????????????");
            }
            if (!TextUtils.isEmpty(firstimg)) {
                ImageUtils.loadImage(this, firstimg, mIvPreview);
                //??????????????????????????????
                sendShowExposure(mAdmBean.getImps(), mScreenWidth, mScreenWidth * higth / wigth);
            }

            String iconUrl = mAdmBean.getLogo();
            if (!TextUtils.isEmpty(iconUrl)) {
                ImageUtils.loadImage(this, iconUrl, mIvIcon);
                ImageUtils.loadImage(this, iconUrl, mIvIconLast);
            } else {
                mIvIcon.setVisibility(View.GONE);
                mIvIconLast.setVisibility(View.GONE);
            }

            if (mListener != null) {
                mListener.onAdExposure();
            }
        }

    }

    /**
     * ????????????
     */
    public void play (String url) {
        mHaveSendStartPlay = false;
        mHaveSendMidpointPlay = false;
        mHaveSendFirstQuartilePlay = false;
        mHaveSendThirdQuartilePlay = false;
        mHaveSendCompletePlay = false;
        mHaveSendClosePlay = false;
        mHaveClicked = false;
        mIsHavePlayComplate = false;
        mIsHaveTextureDestroy = false;
        mTextureView.setOnVideoListener(new MyTextureView.MyTextureViewOnListener() {
            @Override
            public void OnPreparedListener (MediaPlayer mp, int duration, int secondtime) {
                //??????????????????
                if (!mIsHavePlayComplate) {
                    mVideoAllTime = duration;
                    //mVideoSecondTime = secondtime;
                    mTextureView.start();
                    LogUtils.e("?????????????????????");
                    if (!isFirstPlayVideo) {
                        //showPreviewOrDownTime(false);//??????????????????,????????????????????????
                    }
                }

                //???????????????????????????
                if (!mHaveSendStartPlay) {
                    mHaveSendStartPlay = true;
                    if (mAdmBean != null && mAdmBean.getEvent() != null) {
                        sendShowExposure(mAdmBean.getEvent().getStart());
                    }
                }

            }

            @Override
            public void OnVideoPreparedListener (MediaPlayer mp) {
                if (isFirstPlayVideo) {//???????????????????????????????????????
                    //isFirstPlayVoide = false;
                    showPreviewOrDownTime(false);//??????????????????,????????????????????????
                }
            }

            @Override
            public void onInfoListener (MediaPlayer mp, int what, int extra) {
                if (what == 804 && extra == -1004) {//???????????????????????????
                    mTvRefresh.setVisibility(View.VISIBLE);
                    mLoading.setVisibility(View.GONE);
                }
            }

            @Override
            public void OnCompletionListener (MediaPlayer mp) {//??????????????????
                LogUtils.d("??????????????????");
                //???????????????,????????????,????????????,???????????????????????????????????????
                //videoCompletion();

            }

            @Override
            public void OnErrorListener (MediaPlayer mp, int what, int extra) {
                LogUtils.d("????????????" + what + extra);
                mTvRefresh.setVisibility(View.VISIBLE);
                mLoading.setVisibility(View.GONE);

            }

            @Override
            public void OnTextureDestroyedListener () {
                LogUtils.d("?????????????????????");
                if (!mIsHavePlayComplate) {//?????????????????????
                    showPreviewOrDownTime(true);
                    //?????????????????????,????????????????????????
                    if (mCountDownTime != null) {
                        mCountDownTime.cancel();
                    }
                    mIsHaveTextureDestroy = true;
                }
            }

            @Override
            public void OnBitmapListener (Bitmap bitmap) {//???????????????????????????
                if (bitmap != null) {
                    //mIvPreview.setImageBitmap(bitmap);
                }
            }
        });

        mTextureView.setVideoPath(url);

    }

    /**
     * ???????????????,????????????,????????????
     */
    private void videoCompletion (boolean isStartMusic) {
        mIsHavePlayComplate = true;

        //????????????
        mIvPreview.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.GONE);
        mTvTimeDown.setVisibility(View.GONE);
        mLlTimeDown.setVisibility(View.VISIBLE);
        mLlTimeDown.setClickable(true);
        mIvClose.setVisibility(View.VISIBLE);
        //???????????????
        closeCountDownTime();

        //?????????????????????
        mBottonLl.setVisibility(View.GONE);
        mLastLl.setVisibility(View.VISIBLE);
        //???????????????????????????
        //startViewAnimation(mLastBtDownApk);

        //?????????????????????????????????
        if (!mHaveSendCompletePlay) {
            mHaveSendCompletePlay = true;
            if (mAdmBean != null && mAdmBean.getEvent() != null) {
                sendShowExposure(mAdmBean.getEvent().getComplete());
            }
        }

        if (mListener != null) {
            //mListener.onAdCompletion();
        }

        if (isStartMusic) {
            startInteractive();
        }
    }

    /**
     * ??????????????????
     */
    private void startInteractive () {
        if (mInteractiveBean != null) {
            //??????????????????
            int          action     = mAdmBean.getAction();
            String       ldp        = mAdmBean.getLdp();
            final int    waitTime   = null == mInteractiveBean ? 0 : mInteractiveBean.getWait();
            String       tpnumber   = mAdmBean.getTpnumber();
            List<String> clks       = mAdmBean.getClks();
            RemindBean   remind     = mInteractiveBean.getRemind();
            String       startMusic = "";
            String       downMusic  = "";
            if (remind != null && remind.getDownload() != null && remind.getDownload().getShakeme() != null) {
                downMusic = remind.getDownload().getShakeme().getStart();
            }
            if (action == 1) {
                //App webview ????????????
                if (remind != null && remind.getOpenldp() != null && remind.getOpenldp().getShakeme() != null) {
                    startMusic = remind.getOpenldp().getShakeme().getStart();
                }
            } else if (action == 2) {
                //???????????????????????????
                if (remind != null && remind.getOpenldp() != null && remind.getOpenldp().getShakeme() != null) {
                    startMusic = remind.getOpenldp().getShakeme().getStart();
                }
            } else if (action == 3) {
                //????????????
                if (remind != null && remind.getPhone() != null && remind.getPhone().getShakeme() != null) {
                    startMusic = remind.getPhone().getShakeme().getStart();
                }
            } else if (action == 6) {
                //??????
                if (remind != null && remind.getDownload() != null && remind.getDownload().getShakeme() != null) {
                    downMusic = remind.getDownload().getShakeme().getStart();
                    startMusic = downMusic;
                }
            } else if (action == 7) {
                //deeplink
                if (remind != null && remind.getDeeplink() != null && remind.getDeeplink().getShakeme() != null) {
                    startMusic = remind.getDeeplink().getShakeme().getStart();
                }
            } else if (action == 8) {
                //?????????
                if (remind != null && remind.getOpenldp() != null && remind.getOpenldp().getShakeme() != null) {
                    startMusic = remind.getOpenldp().getShakeme().getStart();
                }
            }

            if (!TextUtils.isEmpty(startMusic)) {
                //????????????,??????????????????,??????????????????,??????????????????,????????????
                MediaPlayerUtil.getInstance().playVoice(this, startMusic, new MediaPlayerUtil.MediaOnListener() {
                    @Override
                    public void onPlayStartListener (int allTime) {
                        //?????????
                        if (mInteractiveBean != null) {
                            mShakeUtils = new ShakeUtils(QcAdVideoActivity.this);
                            ShakeInteractiveUtil.setOnShakeListener(QcAdVideoActivity.this, mShakeUtils, new ShakeUtils.OnShakeListener() {
                                @Override
                                public void onShake () {
                                    clearShake();
                                    //??????????????????
                                    setShakeClick(QcAdVideoActivity.this, mAdmBean);
                                }
                            });
                        }
                    }

                    @Override
                    public void onPlayCurrentTimeListener (int currentTime) {

                    }

                    @Override
                    public void onPlayCompletionListener () {
                        if (mShakeUtils != null) {
                            //????????????????????????
                            DialogUtils.noInteractiveCountDownTime(mIntervalTime, false, null, new CountDownCallback() {
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

                    }
                });


            }
        }
    }

    /**
     * ??????????????????????????????
     */
    private CommonHandler mHandler;

    private void onAdCompleteCallBack () {
        if (mListener != null) {
            closeCommonHandler();
            stopMusic();
            stopVideo();
            mListener.onAdCompletion();
        }
    }

    /**
     * ??????
     */
    private void replay () {
        mIvPreview.setVisibility(View.GONE);
        mLastLl.setVisibility(View.GONE);
        mBottonLl.setVisibility(View.VISIBLE);
        mTextureView.start();
        mIsHavePlayComplate = false;
        startViewClickTimeDown();
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
     */
    private void clearShake () {
        if (mShakeUtils != null) {
            mShakeUtils.onPause();
            mShakeUtils.clear();
            mShakeUtils = null;
        }
    }

    /**
     * ???????????????????????????
     */
    private void startViewAnimation (final TextView lastBtDownApk) {
        final Animation shake    = AnimationUtils.loadAnimation(this, R.anim.qcad_shake_ad);//????????????????????????
        final Animation shakeEnd = AnimationUtils.loadAnimation(this, R.anim.qcad_shake_ad_end);//????????????????????????
        lastBtDownApk.startAnimation(shake); //???????????????????????????
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart (Animation animation) {
            }

            @Override
            public void onAnimationEnd (Animation animation) {
                lastBtDownApk.startAnimation(shakeEnd); //???????????????????????????
            }

            @Override
            public void onAnimationRepeat (Animation animation) {
            }
        });

        shakeEnd.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart (Animation animation) {
            }

            @Override
            public void onAnimationEnd (Animation animation) {
                try {
                    Thread.sleep(800);
                    startViewAnimation(lastBtDownApk);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAnimationRepeat (Animation animation) {
            }
        });
    }

    /**
     * ???????????????
     */
    private void closeCountDownTime () {
        if (mCountDownTime != null) {
            mCountDownTime.cancel();
            mCountDownTime = null;
        }
    }

    /**
     * ???????????????????????????
     */
    public void showPreviewOrDownTime (Boolean isShow) {
        if (isShow) {
            mIvPreview.setVisibility(View.VISIBLE);
            mLoading.setVisibility(View.VISIBLE);
        } else {
            startPreviewTimeDown(100);//??????
            //mIvPreview.setVisibility(GONE);
        }
    }

    /**
     * ?????????????????????????????????
     */
    private void startPreviewTimeDown (int time) {
        if (mCountDownPreview != null) {
            mCountDownPreview.cancel();
        }
        mCountDownPreview = new CountDownTimer(time, 800) {
            @Override
            public void onTick (long millisUntilFinished) {
            }

            @Override
            public void onFinish () {
                mIvPreview.setVisibility(View.GONE);
                mLoading.setVisibility(View.GONE);
                if (!mIsHavePlayComplate) {
                    mLlTimeDown.setVisibility(View.VISIBLE);
                    mTvTimeDown.setVisibility(View.VISIBLE);
                    startViewClickTimeDown();
                } else {
                    //???????????????????????????,????????????????????????
                    mIvClose.setVisibility(View.VISIBLE);
                }
            }
        };

        mCountDownPreview.start();
    }

    /**
     * ???????????????????????????
     */
    private void startViewClickTimeDown () {
        if (mCountDownTime != null) {
            mCountDownTime.cancel();
            mCountDownTime = null;
        }
        int currentPosition = mTextureView.getCurrentPosition();
        int distanceTime    = mBackVideoAllTime - currentPosition;
        LogUtils.d("?????????????????????=" + currentPosition + "???????????????=" + distanceTime + "???????????????=" + mBackVideoAllTime);
        mCountDownTime = new CustomCountDownTimer(distanceTime, 1000) {
            @Override
            public void onTick (long millisUntilFinished) {
                //???????????????????????????,????????????,??????,????????????
                musicPlayExposure(false);

                //????????????????????????
                long time = 0;
                if (millisUntilFinished > 0) {
                    time = millisUntilFinished / 1000;
                    if (millisUntilFinished % 1000 > 0) {
                        time = time + 1;
                    }
                }
                LogUtils.d("??????=" + time + " ?????????=" + millisUntilFinished);
                if (time == 0) {
                    mLlTimeDown.setClickable(true);
                    mTvTimeDown.setVisibility(View.GONE);
                    mIvClose.setVisibility(View.VISIBLE);
                    if (!mIsHavePlayComplate) {
                        //TODO:???????????????,????????????,????????????
                        videoCompletion(true);
                    }
                } else {
                    //??????????????????????????????????????????,????????????,????????????????????????
                    if (mSkipTime >= mVideoSecondTime) {
                        mLlTimeDown.setClickable(false);
                        mTvTimeDown.setText(time + "s");
                        mIvClose.setVisibility(View.GONE);
                    } else {
                        if (mVideoSecondTime - time >= mSkipTime) {
                            mLlTimeDown.setClickable(true);
                            mTvTimeDown.setText(time + "s");
                            mIvClose.setVisibility(View.VISIBLE);
                        } else {
                            mLlTimeDown.setClickable(false);
                            mTvTimeDown.setText(time + "s | " + (mSkipTime + time - mVideoSecondTime) + "?????????????????????");
                            mIvClose.setVisibility(View.GONE);
                        }
                    }

                    mTvTimeDown.setVisibility(View.VISIBLE);
                    mLlTimeDown.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFinish () {
                mTvTimeDown.setVisibility(View.GONE);
                mIvClose.setVisibility(View.VISIBLE);
                if (!mIsHavePlayComplate) {
                    //TODO:???????????????,????????????,????????????
                    videoCompletion(true);
                }
            }
        };

        mCountDownTime.start();
    }

    /**
     * ???????????????????????????
     */
    private void musicPlayExposure (boolean isClose) {
        if (isClose) {
            //???????????????(???????????????????????????)
            if (!mHaveSendCompletePlay) {
                LogUtils.d("??????");
                mHaveSendCompletePlay = true;
                if (mAdmBean != null && mAdmBean.getEvent() != null) {
                    sendShowExposure(mAdmBean.getEvent().getClose());
                }
            }
            return;
        }
        if (mTextureView != null) {
            //?????????????????????
            if (mTextureView.getCurrentPosition() * 4 >= mVideoAllTime) {
                if (!mHaveSendFirstQuartilePlay) {
                    LogUtils.d("??????????????????=" + mTextureView.getCurrentPosition());
                    mHaveSendFirstQuartilePlay = true;
                    if (mAdmBean != null && mAdmBean.getEvent() != null) {
                        sendShowExposure(mAdmBean.getEvent().getFirstQuartile());
                    }
                }
            }

            //???????????????
            if (mTextureView.getCurrentPosition() * 2 >= mVideoAllTime) {
                if (!mHaveSendMidpointPlay) {
                    LogUtils.d("????????????=" + mTextureView.getCurrentPosition());
                    mHaveSendMidpointPlay = true;
                    if (mAdmBean != null && mAdmBean.getEvent() != null) {
                        sendShowExposure(mAdmBean.getEvent().getMidpoint());
                    }
                }
            }

            //????????????
            if (mTextureView.getCurrentPosition() * 4 >= mVideoAllTime * 3) {
                if (!mHaveSendThirdQuartilePlay) {
                    LogUtils.d("??????????????????=" + mTextureView.getCurrentPosition());
                    mHaveSendThirdQuartilePlay = true;
                    if (mAdmBean != null && mAdmBean.getEvent() != null) {
                        sendShowExposure(mAdmBean.getEvent().getThirdQuartile());
                    }
                }
            }
        }
    }

    @Override
    protected void onResume () {
        super.onResume();
        isFront = true;

        if (!isFirstInActivity) {
            //???????????????
            LogUtils.i("????????????????????????");
            if (!mIsHavePlayComplate) {//?????????????????????,?????????????????????
                if (!mIsHaveTextureDestroy) {//?????????????????????,?????????????????????
                    //??????????????????,???????????????
                    mTextureView.start();
                    startViewClickTimeDown();
                }
            }
        } else {
            isFirstInActivity = false;
        }
    }

    @Override
    protected void onPause () {
        super.onPause();
        isFront = false;
        LogUtils.i("????????????????????????");
        if (!mIsHavePlayComplate) {
            if (!mIsHaveTextureDestroy) {//????????????????????????????????????,???????????????
                //??????????????????,???????????????,????????????
                mTextureView.pause();
                if (mCountDownTime != null) {
                    mCountDownTime.cancel();
                    mCountDownTime = null;
                }
            }
        }
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        closeCommonHandler();
        screenListener.unregisterListener();//??????????????????
    }

    @Override
    public boolean dispatchKeyEvent (KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //do something.
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //???????????????????????????
            return true;
        }
        return super.onKeyDown(keyCode, event);//????????????????????????????????????
    }

    @Override
    public void onBackPressed () {

    }

    @Override
    public void onClick (View view) {
        int id = view.getId();
        if (id == R.id.dialog_ad_refresh) {
            //????????????????????????
            mTextureView.resumeStart();
            mTvRefresh.setVisibility(View.GONE);
            mLoading.setVisibility(View.VISIBLE);

        } else if (id == R.id.dialog_ad_iv_close || id == R.id.ll_ad_time_down) {
            //??????dialog?????????
            stopVideo();
            if (mCountDownTime != null) {
                //mCountDownTime.onFinish();
                mCountDownTime.cancel();
                mCountDownTime = null;
            }
            if (mCountDownPreview != null) {
                mCountDownPreview.cancel();
                mCountDownPreview = null;
            }

            if (mListener != null) {
                mListener.onAdClose();
            }

            stopMusic();
            finish();

        } else if (id == R.id.dialog_ad_ll_bottom || id == R.id.bt_ad_download_last) {
            //????????????,??????apk
            if (mAdmBean != null) {
                //2021-06-02 ????????????,???????????????????????????
                //????????????
                //http://zentao.corpize.com/zentao/bug-view-121.html
                if (mTextureView != null) {
                    mTextureView.pause();
                }

                //????????????
                if (!mHaveClicked) {
                    mHaveClicked = true;
                    sendClickExposure(mAdmBean.getClks());
                }

                if (mListener != null) {
                    mListener.onAdClick();
                }

                //??????????????????????????????????????????
                String deeplink = mAdmBean.getDeeplink();
                if (!TextUtils.isEmpty(deeplink)
                        && ThirdAppUtils.openLinkApp(this, deeplink, 0)) {
                    //Action=7?????????,???????????? deep link
                    //ThirdAppUtils??????deeplink
                    LogUtils.d("?????????deeplink??????=" + deeplink);
                    mIsHavePlayComplate = true;
                } else {
                    /**
                     * 1 - App Webview ????????????
                     * 2 - ??????????????? ????????????
                     * 4 - ???????????? ???
                     * 6 - ??????APP ??????
                     * 7 - deeplink ??????
                     */
                    int    action = mAdmBean.getAction();
                    String ldp    = mAdmBean.getLdp();
                    if (1 == action) {              // 1 - App webview ????????????
                        if (!TextUtils.isEmpty(ldp)) {
                            SpUtils.saveBoolean("show", false);//???????????????????????????
                            mIsHavePlayComplate = true;
                            Intent intent = new Intent(this, QcAdDetailActivity.class);
                            intent.putExtra("url", ldp);
                            startActivityForResult(intent, 0);
                        }
                    } else if (2 == action) {       // 2 - ???????????????????????????
                        if (!TextUtils.isEmpty(ldp)) {
                            mIsHavePlayComplate = true;
                            Uri    uri      = Uri.parse(ldp);
                            Intent intent11 = new Intent(Intent.ACTION_VIEW, uri);
                            startActivityForResult(intent11, 0);
                        }
                    } else if (3 == action) {       // 4 - ????????????
                        mIsHavePlayComplate = true;
                        CommonUtils.callPhone(this, ldp, 0);
                    } else if (6 == action) {       // 6 - ??????APP
                        if (!TextUtils.isEmpty(ldp)) {
                            LogUtils.d("????????????=" + ldp);
                            final EventtrackersBean eventtrackers = mAdmBean.getEventtrackers();
                            new DownloadInstaller(0, this, ldp, new DownloadProgressCallBack() {
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
                    } else if (8 == action) {       // 8 - ?????????????????????
                        if (!TextUtils.isEmpty(ldp)) {
                            DialogUtils.showWebDialog(this, ldp);
                        }

                        //????????????
                        String coupon = mAdmBean.getLdp();
                        if (!TextUtils.isEmpty(coupon)) {
                            DonwloadSaveImg.donwloadImg(this, coupon);
                        }
                    }
                }
            }
        }
    }

    /**
     * ????????????????????????,?????????????????????
     */
    private void setShakeClick (Activity activity, AdAudioBean bean) {
        if (bean == null) {
            return;
        }

        List<String> clks   = bean.getClks();
        int          action = bean.getAction();
        String       ldp    = bean.getLdp();
        //????????????????????????
        if (!mHaveSnake) {
            mHaveSnake = true;
            sendShowExposure(clks);
        }

        //????????????????????????
        if (mListener != null) {
            mListener.onAdClick();
        }

        if (!isFront) {
            //????????????,?????????????????????
            downApk(activity, bean);
        } else {
            //????????????
            if (1 == action) {   // 1 - App webview ????????????
                if (!TextUtils.isEmpty(ldp)) {
                    stopMusic();
                    Intent intent = new Intent(activity, QcAdDetailActivity.class);
                    intent.putExtra("url", ldp);
                    activity.startActivity(intent);
                }
            } else if (2 == action) {   // 2 - ???????????????????????????
                if (!TextUtils.isEmpty(ldp)) {
                    stopMusic();
                    Uri    uri      = Uri.parse(ldp);
                    Intent intent11 = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivity(intent11);
                }
            } else if (3 == action) {   // 4 - ????????????
                stopMusic();
                CommonUtils.callPhone(activity, ldp);
            } else if (6 == action) {   // 6 - ??????APP
                downApk(activity, bean);
            } else if (7 == action) {   // 7 - deeplink ??????
                String deeplink = ldp;
                if (null != deeplink && ThirdAppUtils.openLinkApp(activity, deeplink)) {
                    stopMusic();
                } else {
                    if (!TextUtils.isEmpty(ldp)) {
                        stopMusic();
                        Uri    uri      = Uri.parse(ldp);
                        Intent intent11 = new Intent(Intent.ACTION_VIEW, uri);
                        activity.startActivity(intent11);
                    }
                }
            } else if (8 == action) {
                if (!TextUtils.isEmpty(ldp)) {
                    stopMusic();
                    DialogUtils.showWebDialog(activity, ldp);
                }

                //????????????
                String coupon = bean.getLdp();
                if (!TextUtils.isEmpty(coupon)) {
                    DonwloadSaveImg.donwloadImg(activity, coupon);
                }
            } else {
                if (!TextUtils.isEmpty(ldp)) {
                    stopMusic();
                    Uri    uri      = Uri.parse(ldp);
                    Intent intent11 = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivity(intent11);
                }
            }
        }
    }

    /**
     * ?????????????????????
     */
    private void stopMusic () {
        MediaPlayerUtil.getInstance().stopAndRelease();
    }

    /**
     * ?????????????????????
     */
    private void stopVideo () {
        mTextureView.release();
    }

    /**
     * ?????????????????????
     *
     * @param activity
     * @param bean
     */
    private void downApk (Activity activity, AdAudioBean bean) {
        if (null != bean && !TextUtils.isEmpty(bean.getLdp())) {
            String                  downUrl       = bean.getLdp();
            final EventtrackersBean eventtrackers = bean.getEventtrackers();
            new DownloadInstaller(0, activity, downUrl, new DownloadProgressCallBack() {
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
    }


    /**
     * ????????????,???????????????????????????
     */
    private void sendShowExposure (List<String> imgList) {
        sendShowExposure(imgList, mScreenWidth, mScreenHeight);
    }

    /**
     * ????????????,???????????????????????????
     */
    private void sendShowExposure (List<String> imgList, int wigth, int heigth) {
        long time = System.currentTimeMillis();

        if (imgList != null && imgList.size() > 0) {
            for (int i = 0; i < imgList.size(); i++) {
                String urlOld = imgList.get(i);
                String url    = urlOld;
                if (url.contains("__WIDTH__")) {//????????????
                    url = url.replace("__WIDTH__", wigth + "");
                }
                if (url.contains("__HEIGHT__")) {//????????????
                    url = url.replace("__HEIGHT__", heigth + "");
                }
                if (url.contains("__POSITION_X__")) {//??????X????????????
                    url = url.replace("__POSITION_X__", 0 + "");
                }
                if (url.contains("__POSITION_Y__")) {//??????Y????????????
                    url = url.replace("__POSITION_Y__", 0 + "");
                }
                if (url.contains("__TIME_STAMP__")) {//??????????????????
                    url = url.replace("__TIME_STAMP__", time + "");
                }

                QcHttpUtil.sendAdExposure(url);
            }
        }
    }

    /**
     * ?????????????????????(????????????)
     */
    public void sendClickExposure (final List<String> list) {

        if (list != null && list.size() > 0) {
            long time = System.currentTimeMillis();

            for (int i = 0; i < list.size(); i++) {
                String urlOld = list.get(i);
                String url    = urlOld;
                if (url.contains("__DOWN_X__")) {//??????X????????????
                    url = url.replace("__DOWN_X__", mClickX + "");
                }
                if (url.contains("__DOWN_Y__")) {//??????Y????????????
                    url = url.replace("__DOWN_Y__", mClickY + "");
                }
                if (url.contains("__UP_X__")) {//??????X????????????
                    url = url.replace("__UP_X__", mClickX + "");
                }
                if (url.contains("__UP_Y__")) {//??????Y????????????
                    url = url.replace("__UP_Y__", mClickY + "");
                }
                if (url.contains("__WIDTH__")) {//????????????
                    url = url.replace("__WIDTH__", mScreenWidth + "");
                }
                if (url.contains("__HEIGHT__")) {//????????????
                    url = url.replace("__HEIGHT__", mScreenHeight + "");
                }
                if (url.contains("__POSITION_X__")) {//??????X????????????
                    url = url.replace("__POSITION_X__", 0 + "");
                }
                if (url.contains("__POSITION_Y__")) {//??????Y????????????
                    url = url.replace("__POSITION_Y__", 0 + "");
                }
                if (url.contains("__TIME_STAMP__")) {//??????????????????
                    url = url.replace("__TIME_STAMP__", time + "");
                }

                QcHttpUtil.sendAdExposure(url);
            }
        }
    }


    /**
     * onTouch()??????(????????????)
     * ???????????????
     * true??? view????????????Touch?????????
     * false???view????????????Touch????????????????????????false?????????????????????????????????????????????????????????????????????
     */
    public void getClickXYPosition1 (View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch (View view, MotionEvent event) {
                switch (event.getAction()) {
                    //?????????????????????
                    case MotionEvent.ACTION_DOWN:
                        //tvTouchShowStart.setText("???????????????(" + event.getX() + "," + event.getY());
                        mClickX = event.getX();
                        mClickY = event.getY();
                        break;

                    //??????????????????
                    case MotionEvent.ACTION_MOVE:
                        //tvTouchShow.setText("???????????????(" + event.getX() + "," + event.getY());
                        break;

                    //?????????????????????
                    case MotionEvent.ACTION_UP:
                        //tvTouchShow.setText("???????????????(" + event.getX() + "," + event.getY());
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
    }

    public void getClickXYPosition2 (View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch (View view, MotionEvent event) {
                switch (event.getAction()) {
                    //?????????????????????
                    case MotionEvent.ACTION_DOWN:
                        //tvTouchShowStart.setText("???????????????(" + event.getX() + "," + event.getY());
                        mClickX = event.getX();
                        mClickY = event.getY();
                        break;
                    //??????????????????
                    case MotionEvent.ACTION_MOVE:
                        //tvTouchShow.setText("???????????????(" + event.getX() + "," + event.getY());
                        break;
                    //?????????????????????
                    case MotionEvent.ACTION_UP:
                        //tvTouchShow.setText("???????????????(" + event.getX() + "," + event.getY());
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    public static int dip2px (Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && !SpUtils.getBoolean("show")) {
            SpUtils.saveBoolean("show", true);
            videoCompletion(false);
            stopVideo();
            stopMusic();
            closeCommonHandler();
            clearShake();
            if (isFirstCallBack) {
                isFirstCallBack = false;
                mListener.onAdCompletion();
            }
        }
    }
}
