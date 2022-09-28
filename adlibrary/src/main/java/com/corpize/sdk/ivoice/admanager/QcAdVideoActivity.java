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
 * author ：yh
 * date : 2020-02-15 22:11
 * description : 激励视频的播放页面
 */
public class QcAdVideoActivity extends Activity implements View.OnClickListener {

    private AdAudioBean       mAdmBean;
    private InteractiveBean   mInteractiveBean;
    private int               mMinVolum     = 0;//最低音量
    private int               mIntervalTime = 0;//互动的时间
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
    private boolean              mIsHavePlayComplate        = false;  //视频倒计时 结束了, 视频播放结束了
    private boolean              mIsHaveTextureDestroy      = false;  //视频控件是否销毁(自然销毁和切换后台的销毁)
    private int                  mVideoAllTime              = 30000;  //当前视频的总时间,毫秒
    private int                  mBackVideoAllTime          = 30000;  //当前视频的总时间,毫秒
    private int                  mVideoSecondTime           = 30;     //当前视频的总时间,秒数
    private boolean              mHaveSendStartPlay         = false;  //是否发送视频播放开始监听发送
    private boolean              mHaveSendMidpointPlay      = false;  //是否发送视频播放中间监听发送
    private boolean              mHaveSendFirstQuartilePlay = false;  //是否发送视频播放四分之一监听发送
    private boolean              mHaveSendThirdQuartilePlay = false;  //是否发送视频播放四分之三监听发送
    private boolean              mHaveSendCompletePlay      = false;  //是否发送视频播放完成监听发送
    private boolean              mHaveSendClosePlay         = false;  //是否发送视频播放跳过监听发送
    private boolean              mHaveDownStart             = false;  //是否发送开始下载曝光请求
    private boolean              mHaveDownComplete          = false;  //是否发送完成下载曝光请求
    private boolean              mHaveDownInstall           = false;  //是否发送开始安装曝光请求
    private boolean              mHaveClicked               = false;  //是否点击的监听
    private boolean              isFirstInActivity          = true;   //是否是第一次进入activity
    private boolean              isFirstPlayVideo           = true;   //是否是第一次播放视频
    private boolean              mHaveSnake                 = false;  //是否摇一摇曝光
    private boolean              isFront;                        //是否处于前台
    private int                  mScreenWidth;
    private int                  mScreenHeight;
    private int                  mSkipTime                  = 0;//返回可跳过跳过的时间
    private float                mClickX;                     //企创 点击位置X
    private float                mClickY;                     //企创 点击位置Y
    private ImageView            mIvAdShow;

    private ShakeUtils mShakeUtils;
    private boolean    isBackground = true;//是否在后台

    private ScreenListener screenListener;            //屏幕锁屏监听
    private boolean        isFirstCallBack = true;//是否是第一次回调

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = newConfig.orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LogUtils.e("-------------横屏-------------");
            if (mTextureView != null) {
                mTextureView.updateTextureViewSizeCenter(true);
            }
        } else {
            LogUtils.e("-------------竖屏-------------");
            if (mTextureView != null) {
                mTextureView.updateTextureViewSizeCenter(false);
            }
        }
        LogUtils.e("横竖屏切换onConfigurationChanged: " + orientation);
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏展示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //加载界面
        setContentView(R.layout.activity_qcad_video_paly);

        mListener = AudioAdManager.get().getListener();
        mAdmBean = (AdAudioBean) getIntent().getSerializableExtra("dataBean");
        mInteractiveBean = (InteractiveBean) getIntent().getSerializableExtra("interactiveBean");
        mMinVolum = getIntent().getIntExtra("volum", 0);
        mIntervalTime = getIntent().getIntExtra("intervalTime", 0);
        MediaPlayerUtil.getInstance().setMinVolume(mMinVolum);
        if (mAdmBean != null) {
            //初始化界面
            initView();
            //初始化数据
            initData();
            //播放视频
            play(mUrl);
        }

        setScreenListener();

    }

    private void setScreenListener () {
        screenListener = new ScreenListener(QcAdVideoActivity.this);
        screenListener.begin(new ScreenListener.ScreenStateListener() {
            @Override
            public void onScreenOn () {
                Log.e("qcvideo", "屏幕打开了");
            }

            @Override
            public void onScreenOff () {
                Log.e("qcvideo", "屏幕关闭了");
                //应用切换后台,视频未停止,暂停播放
                if (mTextureView != null) {
                    mTextureView.pause();
                }
            }

            @Override
            public void onUserPresent () {
                Log.e("qcvideo", "解锁了");

            }
        });

    }

    /**
     * 内部播放器的逻辑
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

        //按钮显示控件
        mIvClose = findViewById(R.id.dialog_ad_iv_close);
        mTvRefresh = findViewById(R.id.dialog_ad_refresh);
        mBtDownApk = findViewById(R.id.bt_ad_download);
        mLastBtDownApk = findViewById(R.id.bt_ad_download_last);

        //文字图片显示控件
        mIvIcon = findViewById(R.id.ad_icon);
        mTvTitle = findViewById(R.id.tv_ad_title);
        mTvContent = findViewById(R.id.tv_ad_content);
        mIvIconLast = findViewById(R.id.ad_icon_last);
        mTvTitleLast = findViewById(R.id.tv_ad_title_last);
        mTvContentLast = findViewById(R.id.tv_ad_content_last);

        //区域显示控件
        mLastLl = findViewById(R.id.dialog_ad_ll_last);
        mBottonLl = findViewById(R.id.dialog_ad_ll_bottom);

        //广告图标
        mIvAdShow = (ImageView) findViewById(R.id.iv_ad_show);
        ImageUtils.loadImage(this, Constants.AD_ICON, mIvAdShow);
        mTvRefresh.setOnClickListener(this);
        mLlTimeDown.setOnClickListener(this);
        mIvClose.setOnClickListener(this);
        mBottonLl.setOnClickListener(this);
        mLastBtDownApk.setOnClickListener(this);

        getClickXYPosition1(mBottonLl);
        getClickXYPosition2(mLastBtDownApk);

        // 设置控件大小及图片在控件中的位置
        mScreenWidth = DeviceUtil.getScreenWidth(this);
        mScreenHeight = DeviceUtil.getRealyScreenHeight(this);
        //RelativeLayout.LayoutParams mVideoParams = (RelativeLayout.LayoutParams) mRlPlay.getLayoutParams();
        //mVideoParams.width = mScreenWidth;
        //mVideoParams.height = mScreenWidth * height / width;
        //mVideoParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        // 设置最后的按钮的控件的大小
        LinearLayout.LayoutParams mLastBtDownApkParams = (LinearLayout.LayoutParams) mLastBtDownApk.getLayoutParams();
        int                       lastBtDownApkWidth   = mScreenWidth * 280 / 375;
        mLastBtDownApkParams.width = lastBtDownApkWidth;
        mLastBtDownApkParams.height = lastBtDownApkWidth * 40 / 280;
        mLastBtDownApk.setLayoutParams(mLastBtDownApkParams);

        // 设置最下方的控件的大小
        RelativeLayout.LayoutParams mBottonLlParams = (RelativeLayout.LayoutParams) mBottonLl.getLayoutParams();
        mBottonLlParams.width = mScreenWidth * 360 / 375;
        mBottonLl.setLayoutParams(mBottonLlParams);

    }

    /**
     * 初始化界面显示
     */
    private void initData () {
        //设置广告信息的展示,广告
        if (mAdmBean != null) {//deepling (ldp和)
            String title    = mAdmBean.getTitle();
            String desc     = mAdmBean.getDesc();
            String ldp      = mAdmBean.getLdp();
            String firstimg = mAdmBean.getFirstimg();
            int    action   = mAdmBean.getAction();
            int    wigth    = mAdmBean.getWidth();
            int    higth    = mAdmBean.getHeight();
            mSkipTime = mAdmBean.getSkip();//跳过时长
            mBackVideoAllTime = mAdmBean.getDuration() * 1000;//服务端视频返回的毫秒值
            mVideoSecondTime = mAdmBean.getDuration();//服务端视频返回的秒数

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
            if (action == 6) {//下载
                mBtDownApk.setText("立即下载");
                mLastBtDownApk.setText("立即下载");
            } else if (action == 1 || action == 2) {
                mBtDownApk.setText("点击打开");
                mLastBtDownApk.setText("点击打开");
            } else if (action == 7) {
                mBtDownApk.setText("查看详情");
                mLastBtDownApk.setText("查看详情");
            } else if (action == 3) {
                mBtDownApk.setText("拨打电话");
                mLastBtDownApk.setText("拨打电话");
            } else if (action == 8) {
                mBtDownApk.setText("领优惠券");
                mLastBtDownApk.setText("领优惠券");
            }
            if (!TextUtils.isEmpty(firstimg)) {
                ImageUtils.loadImage(this, firstimg, mIvPreview);
                //首次加载的图片的曝光
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
     * 播放视频
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
                //视频准备完毕
                if (!mIsHavePlayComplate) {
                    mVideoAllTime = duration;
                    //mVideoSecondTime = secondtime;
                    mTextureView.start();
                    LogUtils.e("开始计算倒计时");
                    if (!isFirstPlayVideo) {
                        //showPreviewOrDownTime(false);//第一次倒计时,在视频缓存结束后
                    }
                }

                //开启播放的曝光监听
                if (!mHaveSendStartPlay) {
                    mHaveSendStartPlay = true;
                    if (mAdmBean != null && mAdmBean.getEvent() != null) {
                        sendShowExposure(mAdmBean.getEvent().getStart());
                    }
                }

            }

            @Override
            public void OnVideoPreparedListener (MediaPlayer mp) {
                if (isFirstPlayVideo) {//视频缓存好了可以播放的页面
                    //isFirstPlayVoide = false;
                    showPreviewOrDownTime(false);//第一次倒计时,在视频缓存结束后
                }
            }

            @Override
            public void onInfoListener (MediaPlayer mp, int what, int extra) {
                if (what == 804 && extra == -1004) {//播放过程中网络中断
                    mTvRefresh.setVisibility(View.VISIBLE);
                    mLoading.setVisibility(View.GONE);
                }
            }

            @Override
            public void OnCompletionListener (MediaPlayer mp) {//视频播放完毕
                LogUtils.d("视频播放完毕");
                //倒计时完成,视频停止,界面变更,现在放到了倒计时结束后弹出
                //videoCompletion();

            }

            @Override
            public void OnErrorListener (MediaPlayer mp, int what, int extra) {
                LogUtils.d("播放错误" + what + extra);
                mTvRefresh.setVisibility(View.VISIBLE);
                mLoading.setVisibility(View.GONE);

            }

            @Override
            public void OnTextureDestroyedListener () {
                LogUtils.d("视频控件销毁了");
                if (!mIsHavePlayComplate) {//视频未播放完成
                    showPreviewOrDownTime(true);
                    //每次界面切换时,要隐藏结束倒计时
                    if (mCountDownTime != null) {
                        mCountDownTime.cancel();
                    }
                    mIsHaveTextureDestroy = true;
                }
            }

            @Override
            public void OnBitmapListener (Bitmap bitmap) {//返回的第一帧的图片
                if (bitmap != null) {
                    //mIvPreview.setImageBitmap(bitmap);
                }
            }
        });

        mTextureView.setVideoPath(url);

    }

    /**
     * 倒计时完成,视频停止,界面变更
     */
    private void videoCompletion (boolean isStartMusic) {
        mIsHavePlayComplate = true;

        //界面显示
        mIvPreview.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.GONE);
        mTvTimeDown.setVisibility(View.GONE);
        mLlTimeDown.setVisibility(View.VISIBLE);
        mLlTimeDown.setClickable(true);
        mIvClose.setVisibility(View.VISIBLE);
        //结束倒计时
        closeCountDownTime();

        //对数据进行处理
        mBottonLl.setVisibility(View.GONE);
        mLastLl.setVisibility(View.VISIBLE);
        //开启控件的弹动动画
        //startViewAnimation(mLastBtDownApk);

        //发送视频播放完成的曝光
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
     * 开启音频互动
     */
    private void startInteractive () {
        if (mInteractiveBean != null) {
            //有互动的时候
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
                //App webview 打开链接
                if (remind != null && remind.getOpenldp() != null && remind.getOpenldp().getShakeme() != null) {
                    startMusic = remind.getOpenldp().getShakeme().getStart();
                }
            } else if (action == 2) {
                //系统浏览器打开链接
                if (remind != null && remind.getOpenldp() != null && remind.getOpenldp().getShakeme() != null) {
                    startMusic = remind.getOpenldp().getShakeme().getStart();
                }
            } else if (action == 3) {
                //拨打电话
                if (remind != null && remind.getPhone() != null && remind.getPhone().getShakeme() != null) {
                    startMusic = remind.getPhone().getShakeme().getStart();
                }
            } else if (action == 6) {
                //下载
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
                //优惠券
                if (remind != null && remind.getOpenldp() != null && remind.getOpenldp().getShakeme() != null) {
                    startMusic = remind.getOpenldp().getShakeme().getStart();
                }
            }

            if (!TextUtils.isEmpty(startMusic)) {
                //隐藏弹幕,隐藏弹幕按钮,隐藏播放按钮,开启互动播放,无结束页
                MediaPlayerUtil.getInstance().playVoice(this, startMusic, new MediaPlayerUtil.MediaOnListener() {
                    @Override
                    public void onPlayStartListener (int allTime) {
                        //摇一摇
                        if (mInteractiveBean != null) {
                            mShakeUtils = new ShakeUtils(QcAdVideoActivity.this);
                            ShakeInteractiveUtil.setOnShakeListener(QcAdVideoActivity.this, mShakeUtils, new ShakeUtils.OnShakeListener() {
                                @Override
                                public void onShake () {
                                    clearShake();
                                    //有互动的时候
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

                    }
                });


            }
        }
    }

    /**
     * 广告播完回调逻辑处理
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
     * 重播
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
     * 结束Handler，防止内存泄漏
     */
    private void closeCommonHandler () {
        if (mHandler != null) {
            mHandler.releaseHandler();
            mHandler = null;
        }
    }

    /**
     * 释放摇一摇控件
     */
    private void clearShake () {
        if (mShakeUtils != null) {
            mShakeUtils.onPause();
            mShakeUtils.clear();
            mShakeUtils = null;
        }
    }

    /**
     * 开启控件的弹动动画
     */
    private void startViewAnimation (final TextView lastBtDownApk) {
        final Animation shake    = AnimationUtils.loadAnimation(this, R.anim.qcad_shake_ad);//加载动画资源文件
        final Animation shakeEnd = AnimationUtils.loadAnimation(this, R.anim.qcad_shake_ad_end);//加载动画资源文件
        lastBtDownApk.startAnimation(shake); //给组件播放动画效果
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart (Animation animation) {
            }

            @Override
            public void onAnimationEnd (Animation animation) {
                lastBtDownApk.startAnimation(shakeEnd); //给组件播放动画效果
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
     * 结束倒计时
     */
    private void closeCountDownTime () {
        if (mCountDownTime != null) {
            mCountDownTime.cancel();
            mCountDownTime = null;
        }
    }

    /**
     * 设置预览图片的显示
     */
    public void showPreviewOrDownTime (Boolean isShow) {
        if (isShow) {
            mIvPreview.setVisibility(View.VISIBLE);
            mLoading.setVisibility(View.VISIBLE);
        } else {
            startPreviewTimeDown(100);//毫秒
            //mIvPreview.setVisibility(GONE);
        }
    }

    /**
     * 预览图片消失的的倒计时
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
                    //防止播放错误时刷新,倒计时又重新开始
                    mIvClose.setVisibility(View.VISIBLE);
                }
            }
        };

        mCountDownPreview.start();
    }

    /**
     * 右上方时间的倒计时
     */
    private void startViewClickTimeDown () {
        if (mCountDownTime != null) {
            mCountDownTime.cancel();
            mCountDownTime = null;
        }
        int currentPosition = mTextureView.getCurrentPosition();
        int distanceTime    = mBackVideoAllTime - currentPosition;
        LogUtils.d("获取当前的位置=" + currentPosition + "倒计时时间=" + distanceTime + "返回总时长=" + mBackVideoAllTime);
        mCountDownTime = new CustomCountDownTimer(distanceTime, 1000) {
            @Override
            public void onTick (long millisUntilFinished) {
                //视频进度曝光的检测,四分之一,一半,四分之三
                musicPlayExposure(false);

                //倒计时显示的计算
                long time = 0;
                if (millisUntilFinished > 0) {
                    time = millisUntilFinished / 1000;
                    if (millisUntilFinished % 1000 > 0) {
                        time = time + 1;
                    }
                }
                LogUtils.d("时间=" + time + " 原时间=" + millisUntilFinished);
                if (time == 0) {
                    mLlTimeDown.setClickable(true);
                    mTvTimeDown.setVisibility(View.GONE);
                    mIvClose.setVisibility(View.VISIBLE);
                    if (!mIsHavePlayComplate) {
                        //TODO:倒计时结束,视频关掉,界面变更
                        videoCompletion(true);
                    }
                } else {
                    //跳过的时间大于视频的实际时长,不可点击,不显示可跳过时间
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
                            mTvTimeDown.setText(time + "s | " + (mSkipTime + time - mVideoSecondTime) + "秒后可关闭视频");
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
                    //TODO:倒计时结束,视频关掉,界面变更
                    videoCompletion(true);
                }
            }
        };

        mCountDownTime.start();
    }

    /**
     * 视频进度曝光的检测
     */
    private void musicPlayExposure (boolean isClose) {
        if (isClose) {
            //关闭的监听(关闭和完成只发一个)
            if (!mHaveSendCompletePlay) {
                LogUtils.d("关闭");
                mHaveSendCompletePlay = true;
                if (mAdmBean != null && mAdmBean.getEvent() != null) {
                    sendShowExposure(mAdmBean.getEvent().getClose());
                }
            }
            return;
        }
        if (mTextureView != null) {
            //四分之一的监听
            if (mTextureView.getCurrentPosition() * 4 >= mVideoAllTime) {
                if (!mHaveSendFirstQuartilePlay) {
                    LogUtils.d("超过四分之一=" + mTextureView.getCurrentPosition());
                    mHaveSendFirstQuartilePlay = true;
                    if (mAdmBean != null && mAdmBean.getEvent() != null) {
                        sendShowExposure(mAdmBean.getEvent().getFirstQuartile());
                    }
                }
            }

            //中间的监听
            if (mTextureView.getCurrentPosition() * 2 >= mVideoAllTime) {
                if (!mHaveSendMidpointPlay) {
                    LogUtils.d("超过一半=" + mTextureView.getCurrentPosition());
                    mHaveSendMidpointPlay = true;
                    if (mAdmBean != null && mAdmBean.getEvent() != null) {
                        sendShowExposure(mAdmBean.getEvent().getMidpoint());
                    }
                }
            }

            //四分之三
            if (mTextureView.getCurrentPosition() * 4 >= mVideoAllTime * 3) {
                if (!mHaveSendThirdQuartilePlay) {
                    LogUtils.d("超过四分之三=" + mTextureView.getCurrentPosition());
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
            //首次不进入
            LogUtils.i("应用切换到了前台");
            if (!mIsHavePlayComplate) {//视频未播放完成,从后台切换回来
                if (!mIsHaveTextureDestroy) {//视频控件未销毁,从后台切换回来
                    //应用切换后台,视频未停止
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
        LogUtils.i("应用切换到了后台");
        if (!mIsHavePlayComplate) {
            if (!mIsHaveTextureDestroy) {//部分手机应用切换后台之后,控件被小伙
                //应用切换后台,视频未停止,暂停播放
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
        screenListener.unregisterListener();//释放屏幕监听
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
            //不执行父类点击事件
            return true;
        }
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }

    @Override
    public void onBackPressed () {

    }

    @Override
    public void onClick (View view) {
        int id = view.getId();
        if (id == R.id.dialog_ad_refresh) {
            //刷新重新播放视频
            mTextureView.resumeStart();
            mTvRefresh.setVisibility(View.GONE);
            mLoading.setVisibility(View.VISIBLE);

        } else if (id == R.id.dialog_ad_iv_close || id == R.id.ll_ad_time_down) {
            //关闭dialog的按钮
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
            //打开链接,下载apk
            if (mAdmBean != null) {
                //2021-06-02 产品修改,点击落地页暂停播放
                //变动地址
                //http://zentao.corpize.com/zentao/bug-view-121.html
                if (mTextureView != null) {
                    mTextureView.pause();
                }

                //点击监听
                if (!mHaveClicked) {
                    mHaveClicked = true;
                    sendClickExposure(mAdmBean.getClks());
                }

                if (mListener != null) {
                    mListener.onAdClick();
                }

                //根据不同的返回打开不同的页面
                String deeplink = mAdmBean.getDeeplink();
                if (!TextUtils.isEmpty(deeplink)
                        && ThirdAppUtils.openLinkApp(this, deeplink, 0)) {
                    //Action=7的时候,一般都是 deep link
                    //ThirdAppUtils打开deeplink
                    LogUtils.d("打开的deeplink地址=" + deeplink);
                    mIsHavePlayComplate = true;
                } else {
                    /**
                     * 1 - App Webview 打开链接
                     * 2 - 系统浏览器 打开链接
                     * 4 - 拨打电话 无
                     * 6 - 下载APP 下载
                     * 7 - deeplink 链接
                     */
                    int    action = mAdmBean.getAction();
                    String ldp    = mAdmBean.getLdp();
                    if (1 == action) {              // 1 - App webview 打开链接
                        if (!TextUtils.isEmpty(ldp)) {
                            SpUtils.saveBoolean("show", false);//手动设置切到了后台
                            mIsHavePlayComplate = true;
                            Intent intent = new Intent(this, QcAdDetailActivity.class);
                            intent.putExtra("url", ldp);
                            startActivityForResult(intent, 0);
                        }
                    } else if (2 == action) {       // 2 - 系统浏览器打开链接
                        if (!TextUtils.isEmpty(ldp)) {
                            mIsHavePlayComplate = true;
                            Uri    uri      = Uri.parse(ldp);
                            Intent intent11 = new Intent(Intent.ACTION_VIEW, uri);
                            startActivityForResult(intent11, 0);
                        }
                    } else if (3 == action) {       // 4 - 拨打电话
                        mIsHavePlayComplate = true;
                        CommonUtils.callPhone(this, ldp, 0);
                    } else if (6 == action) {       // 6 - 下载APP
                        if (!TextUtils.isEmpty(ldp)) {
                            LogUtils.d("下载地址=" + ldp);
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
                    } else if (8 == action) {       // 8 - 打开优惠券弹窗
                        if (!TextUtils.isEmpty(ldp)) {
                            DialogUtils.showWebDialog(this, ldp);
                        }

                        //下载图文
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
     * 摇一摇的点击事件,区分息屏和亮屏
     */
    private void setShakeClick (Activity activity, AdAudioBean bean) {
        if (bean == null) {
            return;
        }

        List<String> clks   = bean.getClks();
        int          action = bean.getAction();
        String       ldp    = bean.getLdp();
        //摇一摇事件的曝光
        if (!mHaveSnake) {
            mHaveSnake = true;
            sendShowExposure(clks);
        }

        //摇一摇的点击返回
        if (mListener != null) {
            mListener.onAdClick();
        }

        if (!isFront) {
            //处于后台,后台只处理下载
            downApk(activity, bean);
        } else {
            //处于前台
            if (1 == action) {   // 1 - App webview 打开链接
                if (!TextUtils.isEmpty(ldp)) {
                    stopMusic();
                    Intent intent = new Intent(activity, QcAdDetailActivity.class);
                    intent.putExtra("url", ldp);
                    activity.startActivity(intent);
                }
            } else if (2 == action) {   // 2 - 系统浏览器打开链接
                if (!TextUtils.isEmpty(ldp)) {
                    stopMusic();
                    Uri    uri      = Uri.parse(ldp);
                    Intent intent11 = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivity(intent11);
                }
            } else if (3 == action) {   // 4 - 拨打电话
                stopMusic();
                CommonUtils.callPhone(activity, ldp);
            } else if (6 == action) {   // 6 - 下载APP
                downApk(activity, bean);
            } else if (7 == action) {   // 7 - deeplink 链接
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

                //下载图文
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
     * 停止并释放音频
     */
    private void stopMusic () {
        MediaPlayerUtil.getInstance().stopAndRelease();
    }

    /**
     * 停止并释放视频
     */
    private void stopVideo () {
        mTextureView.release();
    }

    /**
     * 下载的通用方法
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
    }


    /**
     * 发送曝光,计算了宽高及时间戳
     */
    private void sendShowExposure (List<String> imgList) {
        sendShowExposure(imgList, mScreenWidth, mScreenHeight);
    }

    /**
     * 发送曝光,计算了宽高及时间戳
     */
    private void sendShowExposure (List<String> imgList, int wigth, int heigth) {
        long time = System.currentTimeMillis();

        if (imgList != null && imgList.size() > 0) {
            for (int i = 0; i < imgList.size(); i++) {
                String urlOld = imgList.get(i);
                String url    = urlOld;
                if (url.contains("__WIDTH__")) {//宽度替换
                    url = url.replace("__WIDTH__", wigth + "");
                }
                if (url.contains("__HEIGHT__")) {//高度替换
                    url = url.replace("__HEIGHT__", heigth + "");
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
     * 广告位点击请求(企创广告)
     */
    public void sendClickExposure (final List<String> list) {

        if (list != null && list.size() > 0) {
            long time = System.currentTimeMillis();

            for (int i = 0; i < list.size(); i++) {
                String urlOld = list.get(i);
                String url    = urlOld;
                if (url.contains("__DOWN_X__")) {//点击X轴的替换
                    url = url.replace("__DOWN_X__", mClickX + "");
                }
                if (url.contains("__DOWN_Y__")) {//点击Y轴的替换
                    url = url.replace("__DOWN_Y__", mClickY + "");
                }
                if (url.contains("__UP_X__")) {//抬起X轴的替换
                    url = url.replace("__UP_X__", mClickX + "");
                }
                if (url.contains("__UP_Y__")) {//抬起Y轴的替换
                    url = url.replace("__UP_Y__", mClickY + "");
                }
                if (url.contains("__WIDTH__")) {//宽度替换
                    url = url.replace("__WIDTH__", mScreenWidth + "");
                }
                if (url.contains("__HEIGHT__")) {//高度替换
                    url = url.replace("__HEIGHT__", mScreenHeight + "");
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
     * onTouch()事件(企创广告)
     * 注意返回值
     * true： view继续响应Touch操作；
     * false：view不再响应Touch操作，故此处若为false，只能显示起始位置，不能显示实时位置和结束位置
     */
    public void getClickXYPosition1 (View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch (View view, MotionEvent event) {
                switch (event.getAction()) {
                    //点击的开始位置
                    case MotionEvent.ACTION_DOWN:
                        //tvTouchShowStart.setText("起始位置：(" + event.getX() + "," + event.getY());
                        mClickX = event.getX();
                        mClickY = event.getY();
                        break;

                    //触屏实时位置
                    case MotionEvent.ACTION_MOVE:
                        //tvTouchShow.setText("实时位置：(" + event.getX() + "," + event.getY());
                        break;

                    //离开屏幕的位置
                    case MotionEvent.ACTION_UP:
                        //tvTouchShow.setText("结束位置：(" + event.getX() + "," + event.getY());
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
                    //点击的开始位置
                    case MotionEvent.ACTION_DOWN:
                        //tvTouchShowStart.setText("起始位置：(" + event.getX() + "," + event.getY());
                        mClickX = event.getX();
                        mClickY = event.getY();
                        break;
                    //触屏实时位置
                    case MotionEvent.ACTION_MOVE:
                        //tvTouchShow.setText("实时位置：(" + event.getX() + "," + event.getY());
                        break;
                    //离开屏幕的位置
                    case MotionEvent.ACTION_UP:
                        //tvTouchShow.setText("结束位置：(" + event.getX() + "," + event.getY());
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
