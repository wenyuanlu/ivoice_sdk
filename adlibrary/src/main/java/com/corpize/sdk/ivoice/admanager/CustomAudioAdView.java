package com.corpize.sdk.ivoice.admanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.corpize.sdk.R;
import com.corpize.sdk.ivoice.AdAttr;
import com.corpize.sdk.ivoice.AdLayout;
import com.corpize.sdk.ivoice.CoverType;
import com.corpize.sdk.ivoice.QCiVoiceSdk;
import com.corpize.sdk.ivoice.bean.AdCommentBean;
import com.corpize.sdk.ivoice.bean.AdCommentItemBean;
import com.corpize.sdk.ivoice.bean.AdMusicBean;
import com.corpize.sdk.ivoice.bean.AdResponseBean;
import com.corpize.sdk.ivoice.bean.UpVoiceResultBean;
import com.corpize.sdk.ivoice.bean.UserBean;
import com.corpize.sdk.ivoice.bean.response.AdAudioBean;
import com.corpize.sdk.ivoice.bean.response.EventBean;
import com.corpize.sdk.ivoice.bean.response.InteractiveBean;
import com.corpize.sdk.ivoice.bean.response.RemindBean;
import com.corpize.sdk.ivoice.common.CommonHandler;
import com.corpize.sdk.ivoice.common.Constants;
import com.corpize.sdk.ivoice.danmuku.DanMuPopupUtil;
import com.corpize.sdk.ivoice.danmuku.DanMuView;
import com.corpize.sdk.ivoice.danmuku.model.DanMuModel;
import com.corpize.sdk.ivoice.danmuku.model.utils.DimensionUtil;
import com.corpize.sdk.ivoice.danmuku.view.OnDanMuTouchCallBackListener;
import com.corpize.sdk.ivoice.listener.AudioCustomQcAdListener;
import com.corpize.sdk.ivoice.listener.CountDownCallback;
import com.corpize.sdk.ivoice.listener.CustomViewListener;
import com.corpize.sdk.ivoice.listener.EditDialogCallback;
import com.corpize.sdk.ivoice.listener.OnVolumeEndListener;
import com.corpize.sdk.ivoice.utils.Base64;
import com.corpize.sdk.ivoice.utils.CommonShakeEventUtils;
import com.corpize.sdk.ivoice.utils.CommonSplicingResourceUtils;
import com.corpize.sdk.ivoice.utils.CustomMonitorVolumeUtils;
import com.corpize.sdk.ivoice.utils.DeviceUtil;
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
import com.corpize.sdk.ivoice.utils.ThreadManagerUtil;
import com.corpize.sdk.ivoice.utils.VoiceInteractiveUtil;
import com.corpize.sdk.ivoice.utils.countdown.CustomCountDownUtils;
import com.corpize.sdk.ivoice.utils.countdown.OnCustomCountDownListener;
import com.corpize.sdk.ivoice.view.QcadImageView;
import com.corpize.sdk.ivoice.view.RoundImageView;
import com.corpize.sdk.ivoice.view.VerticalImageSpan;
import com.qichuang.annotation.NonNull;
import com.qichuang.annotation.Nullable;
import com.qichuang.annotation.RequiresApi;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.corpize.sdk.ivoice.utils.StartMusicUtils.getReStartMusic;

/**
 * author ：yh
 * date : 2020-02-16 02:52
 * description :企创单图信息流
 */
public class CustomAudioAdView extends FrameLayout implements View.OnClickListener {

    private View        mView;
    private Context     mContext;
    private Activity    mActivity;
    private AdAudioBean mResponse;//显示的素材
    private AdAudioBean normalBean;//正常素材
    private AdAudioBean chimeBean;//整点报时素材
    private AdAttr      mAdAttr;
    private String      mImgAudioUrl = "";

    private boolean mHaveSnake      = false;   //是否摇一摇曝光
    private boolean mHaveSnakeClick = false;   //是否摇一摇曝光的点击事件

    private float                   mClickX;
    private float                   mClickY;
    private int                     mPositionX;
    private int                     mPositionY;
    private boolean                 mHaveGetFirstDanmu          = false;//是否发送获取第一批弹幕的请求
    private boolean                 mHaveSendViewShow           = false;//是否发送展示曝光请求
    private boolean                 mHaveSendAudioShow          = false;//是否发送音频展示曝光请求
    private boolean                 mHaveSendClick              = false;//是否发送点击曝光请求
    private boolean                 mHaveSendDeep               = false;//是否发送deeplink曝光请求
    private boolean                 mHaveDownStart              = false;//是否发送开始下载曝光请求
    private boolean                 mHaveDownComplete           = false;//是否发送完成下载曝光请求
    private boolean                 mHaveDownInstall            = false;//是否发送开始安装曝光请求
    private boolean                 mHaveFirstShow              = false;//是否第一次展示
    private boolean                 mHaveMusicStartPlay         = false;//是否音频播放开始监听发送
    private boolean                 mHaveMusicMidpointPlay      = false;//是否音频播放中间监听发送
    private boolean                 mHaveMusicFirstQuartilePlay = false;//是否音频播放四分之一监听发送
    private boolean                 mHaveMusicThirdQuartilePlay = false;//是否音频播放四分之三监听发送
    private boolean                 mHaveMusicCompletePlay      = false;//是否音频播放完成监听发送
    private boolean                 mHaveMusicClosePlay         = false;//是否音频播放跳过监听发送
    private boolean                 mHaveShowIcon               = false;//是否展示icon
    private boolean                 mHaveShowCover              = false;//是否展示cover
    private int                     mAllTime                    = 0;//第一个广告音频的播放的总时长
    private boolean                 mHaveShowLeftInfo           = false;//是否展示左下角的info
    private AudioCustomQcAdListener mListener;
    private CustomViewListener      mCustomViewListener;
    private int                     mWidth;
    private int                     mHeight;
    private int                     mCurrentTime                = 0;

    private ImageView      mPraiseImage;
    private TextView       mPraiseNumber;
    private ImageView      mBarrageImage;
    private TextView       mBarrageNumber;
    private TextView       mTvTitle;
    private TextView       mTvContent;
    private int            mContentAdSize = 10;//广告字样的大小
    private TextView       mTitleInfo;
    private TextView       mContentInfo;
    private RoundImageView mLogoInfo;
    private TextView       mClickInfo;
    private ImageView      mCloseInfo;
    private ImageView      mAdIcon;
    private RelativeLayout mRlInfoSmall;
    private QcadImageView  mHeadLogo;
    private ImageView      headLink;
    private QcadImageView  mCoverImage;
    private ImageView      mMusicBt;
    private RelativeLayout mLastLargeRl;
    private QcadImageView  mLastLargeLogo;
    private TextView       mLastLargeTitle;
    private TextView       mLastLargeContent;
    private LinearLayout   mLastLargeDetails;
    private TextView       mLastLargeClick;
    private RelativeLayout mLastSmallRl;
    private LinearLayout   mLastSmallLl;
    private RoundImageView mLastSmallLogo;
    private TextView       mLastSmallTitle;
    private TextView       mLastSmallContent;
    private TextView       mLastSmallClick;
    private RelativeLayout mSkipLayout;
    private TextView       skipTimer;
    private View           skipLinear;
    private TextView       skipView;

    private boolean   mLastShowLarge = true;
    private DanMuView mDanMuView;

    private Drawable                 musicPlayDrawable;
    private Drawable                 musicPauseDrawable;
    private Drawable                 priseChooseDrawable;
    private Drawable                 priseDefaultDrawable;
    private GestureDetector          mGestureDetector;
    private List<AdCommentItemBean>  mCommentList           = new ArrayList<>();
    private int                      mPraiseNum             = 0;//返回的点赞数量
    private int                      mCommentNum            = 0;//返回的评论数量
    private int                      mCommentStartTime      = 0;//请求评论的开始时间,秒
    private int                      mCommentIntervalTime   = 10;//请求评论的开始时间,秒
    private int                      mCommentLinenumber     = 10;//请求评论的行数
    private boolean                  mInGetComment          = false;//是否获取评论请求中
    private ImageView                mIvBackground;
    private int                      mBarrageContentSize    = 0;
    private List<String>             mBarrageContentColor   = new ArrayList<>();
    private int                      mBarrageHeight         = 0;
    private int                      mBarrageHeadSize       = 0;
    private String                   mBarrageBackColor;
    private String                   mLastDanmuContentColor = "";//上一次的弹幕的展示颜色
    private boolean                  mShowBarrageFromSet    = true;//是否展示弹幕,来自用户设置
    private int                      mIntervalTime          = 0;//互动结束后摇一摇时间
    private int                      callbackType           = 0;//播放结束后继续操作的类型
    private ShakeUtils               mShakeUtils;
    private int                      mPosition;
    private boolean                  isFirstCallBack        = true;//是否是第一次回调
    private CustomMonitorVolumeUtils customMonitorVolumeUtils;

    private int remindsTime;//重试次数

    private CustomCountDownUtils adAutoCloseCountDown;
    private int                  adAutoCloseCountDownNumber         = 0;
    private CustomCountDownUtils adAutoCloseShowSkip;
    private int                  adAutoCloseCountDownShowSkipNumber = 0;

    public CustomAudioAdView (@NonNull Context context) {
        this(context, null);
    }

    public CustomAudioAdView (@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView(context);
    }

    public CustomAudioAdView (@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public CustomAudioAdView (@NonNull Context context, @Nullable AttributeSet attrs, Activity activity,
                              AdResponseBean.AdmBean bean, AdAttr adAttr, @NonNull AudioCustomQcAdListener listener, int position) {
        super(context, attrs);
        this.mContext = context;
        this.mActivity = activity;
        this.chimeBean = bean.getChime();
        this.normalBean = bean.getNormal();
        this.mResponse = chimeBean != null ? chimeBean : normalBean;
        this.mAdAttr = adAttr;
        this.mListener = listener;
        this.mPosition = position;
        initView(context);
    }

    @Override
    protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        LogUtils.e("进入自定义控件的onLayout");
        mWidth = getWidth();
        mHeight = getHeight();

        if (!mHaveGetFirstDanmu && mShowBarrageFromSet) {
            //计算弹幕的行数
            int allDanMuHeight = mHeight;
            if (mAdAttr.isSetBarrageMagin()) {
                int danmutop    = DeviceUtil.dip2px(mContext, mAdAttr.getBarrageMaginTop());
                int danmubottom = DeviceUtil.dip2px(mContext, mAdAttr.getBarrageMaginBottom());
                allDanMuHeight = allDanMuHeight - danmutop - danmubottom;
            }
            int danmuHeight  = (int) (mBarrageHeadSize * 1.8);
            int singleHeight = DimensionUtil.dpToPx(mContext, danmuHeight);
            mCommentLinenumber = (allDanMuHeight - singleHeight / 3) / singleHeight;
            LogUtils.e("弹幕的行数2=" + mCommentLinenumber);

            //获取第一批弹幕数据
            mHaveGetFirstDanmu = true;
            mCommentList.clear();
            getComment(mAdAttr.getMid(), mResponse.getCreativeid(), 0, mCommentStartTime + mCommentIntervalTime);
        }

        int[] position = new int[2];
        getLocationOnScreen(position);
        mPositionX = position[0]; // view距离 屏幕左边的距离（即x轴方向）
        mPositionY = position[1]; // view距离 屏幕顶边的距离（即y轴方向）
        int lastLargeHeight = DeviceUtil.dip2px(mContext, 200);
        if (mHeight >= lastLargeHeight) {
            mLastShowLarge = true;
        } else {
            mLastShowLarge = false;
        }

        //左下角的info弹窗的展示
        if (!mHaveShowLeftInfo) {
            mHaveShowLeftInfo = true;
            showLeftInfoView();
        }

        //加载的时候再曝光,不需要区分是否有音频广告
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
     * 界面数据的初始化
     */
    private void initData () {
        mHaveSendViewShow = false;//是否发送展示曝光请求
        mHaveSendClick = false;//是否发送点击曝光请求
        mHaveSendDeep = false;//是否发送deeplink曝光请求
        mHaveDownStart = false;//是否发送开始下载曝光请求
        mHaveDownComplete = false;//是否发送完成下载曝光请求
        mHaveDownInstall = false;//是否发送开始安装曝光请求
        mHaveFirstShow = false;//是否第一次展示
        mHaveMusicStartPlay = false;//是否音频播放开始监听发送
        mHaveMusicMidpointPlay = false;//是否音频播放中间监听发送
        mHaveMusicFirstQuartilePlay = false;//是否音频播放四分之一监听发送
        mHaveMusicThirdQuartilePlay = false;//是否音频播放四分之三监听发送
        mHaveMusicCompletePlay = false;//是否音频播放完成监听发送
        mHaveMusicClosePlay = false;//是否音频播放跳过监听发送
        mAllTime = 0;//第一个广告音频的播放的总时长
        mHaveShowLeftInfo = false;//是否展示左下角的info
        mLastShowLarge = true;
    }

    /**
     * 初始化数据
     */
    private void initView (final Context context) {
        if (mResponse != null) {//先获取音频地址
            mImgAudioUrl = mResponse.getAudiourl();
        }
        mView = LayoutInflater.from(context).inflate(R.layout.qcad_custom_audio_layout, null);//左图右文

        /**
         * 背景的处理
         */
        mIvBackground = mView.findViewById(R.id.iv_qcad_background);
        RelativeLayout.LayoutParams backgroundParams = (RelativeLayout.LayoutParams) mIvBackground.getLayoutParams();
        if (mAdAttr.isSetBackgroundSize()) {
            int backgroundHeight = mAdAttr.getBackgroundHeight();
            int backgroundWidth  = mAdAttr.getBackgroundWidth();
            backgroundParams.width = backgroundWidth;
            backgroundParams.height = backgroundHeight;
            mIvBackground.setLayoutParams(backgroundParams);
        }

        if (mAdAttr.isSetBackgroundMagin()) {
            int left   = DeviceUtil.dip2px(context, mAdAttr.getBackgroundMaginLeft());
            int top    = DeviceUtil.dip2px(context, mAdAttr.getBackgroundMaginTop());
            int right  = DeviceUtil.dip2px(context, mAdAttr.getBackgroundMaginRight());
            int bottom = DeviceUtil.dip2px(context, mAdAttr.getBackgroundMaginBottom());
            backgroundParams.setMargins(left, top, right, bottom);
            mIvBackground.setLayoutParams(backgroundParams);
        }

        if (mAdAttr.getBackgroundLayout() != null && mAdAttr.getBackgroundLayout().size() > 0) {
            for (AdLayout adLayout : mAdAttr.getBackgroundLayout()) {
                setRule(backgroundParams, adLayout);
            }
            mIvBackground.setLayoutParams(backgroundParams);
        }

        // 标题的处理
        mTvTitle = mView.findViewById(R.id.qcad_custom_title);
        if (mAdAttr.getTitleColor() != 0) {
            mTvTitle.setTextColor(mAdAttr.getTitleColor());
        }
        if (mAdAttr.getTitleSize() != 0) {
            mTvTitle.setTextSize(mAdAttr.getTitleSize());
        }
        RelativeLayout.LayoutParams titleParams = (RelativeLayout.LayoutParams) mTvTitle.getLayoutParams();
        if (mAdAttr.getTitleLayout() != null && mAdAttr.getTitleLayout().size() > 0) {
            for (AdLayout adLayout : mAdAttr.getTitleLayout()) {
                setRule(titleParams, adLayout);
            }
            mTvTitle.setLayoutParams(titleParams);
        }
        if (mAdAttr.isSetTitleMagin()) {
            int left   = DeviceUtil.dip2px(context, mAdAttr.getTitleMaginLeft());
            int top    = DeviceUtil.dip2px(context, mAdAttr.getTitleMaginTop());
            int right  = DeviceUtil.dip2px(context, mAdAttr.getTitleMaginRight());
            int bottom = DeviceUtil.dip2px(context, mAdAttr.getTitleMaginBottom());
            titleParams.setMargins(left, top, right, bottom);
            mTvTitle.setLayoutParams(titleParams);
        }
        mTvTitle.setOnClickListener(this);

        if (0 != mAdAttr.getTitleTextMaxSize()) {
            mTvTitle.setMaxEms(mAdAttr.getTitleTextMaxSize());
        }

        if (0 != mAdAttr.getTitleTextMaxLines()) {
            mTvTitle.setMaxLines(mAdAttr.getTitleTextMaxLines());
        }

        //内容的设置
        mTvContent = mView.findViewById(R.id.qcad_custom_content);
        if (mAdAttr.getContentColor() != 0) {
            mTvContent.setTextColor(mAdAttr.getContentColor());
        }
        mContentAdSize = DeviceUtil.sp2px(context, 10);
        if (mAdAttr.getContentSize() != 0) {
            mTvContent.setTextSize(mAdAttr.getContentSize());
            if (mAdAttr.getContentSize() <= 12) {
                mContentAdSize = DeviceUtil.sp2px(context, 8);
            }
        }
        mTvContent.setOnClickListener(this);

        RelativeLayout.LayoutParams contentParams = (RelativeLayout.LayoutParams) mTvContent.getLayoutParams();
        if (mAdAttr.getContentLayout() != null && mAdAttr.getContentLayout().size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                contentParams.removeRule(RelativeLayout.BELOW);
            } else {
                contentParams.addRule(RelativeLayout.BELOW, 0);
            }
            for (AdLayout adLayout : mAdAttr.getContentLayout()) {
                setRule(contentParams, adLayout);
            }
            mTvContent.setLayoutParams(contentParams);
        }
        if (mAdAttr.isSetContentMagin()) {
            int left   = DeviceUtil.dip2px(context, mAdAttr.getContentMaginLeft());
            int top    = DeviceUtil.dip2px(context, mAdAttr.getContentMaginTop());
            int right  = DeviceUtil.dip2px(context, mAdAttr.getContentMaginRight());
            int bottom = DeviceUtil.dip2px(context, mAdAttr.getContentMaginBottom());
            contentParams.setMargins(left, top, right, bottom);
            mTvContent.setLayoutParams(contentParams);
        }

        //左下角弹窗位置的设置
        mRlInfoSmall = mView.findViewById(R.id.rl_qcad_info_small);
        RelativeLayout.LayoutParams infoParams = (RelativeLayout.LayoutParams) mRlInfoSmall.getLayoutParams();
        if (mAdAttr.getInfoLayout() != null && mAdAttr.getInfoLayout().size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                infoParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            } else {
                infoParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            }
            for (AdLayout adLayout : mAdAttr.getInfoLayout()) {
                setRule(infoParams, adLayout);
            }
            mRlInfoSmall.setLayoutParams(infoParams);
        }
        if (mAdAttr.isSetInfoMagin()) {
            int left   = DeviceUtil.dip2px(mContext, mAdAttr.getInfoMaginLeft());
            int top    = DeviceUtil.dip2px(mContext, mAdAttr.getInfoMaginTop());
            int right  = DeviceUtil.dip2px(mContext, mAdAttr.getInfoMaginRight());
            int bottom = DeviceUtil.dip2px(mContext, mAdAttr.getInfoMaginBottom());
            infoParams.setMargins(left, top, right, bottom);
            mRlInfoSmall.setLayoutParams(infoParams);
        }
        //下载按钮的点击
        mRlInfoSmall.setOnClickListener(this);

        /**
         * 左下角类 弹窗的展示 需要在计算完屏幕的大小后,再决定是否展示
         */
        mLogoInfo = mView.findViewById(R.id.qcad_info_small_logo);
        mTitleInfo = mView.findViewById(R.id.qcad_info_small_title);
        mContentInfo = mView.findViewById(R.id.qcad_info_small_content);
        mClickInfo = mView.findViewById(R.id.qcad_info_small_click);
        mCloseInfo = mView.findViewById(R.id.qcad_info_small_close);
        mAdIcon = mView.findViewById(R.id.qcad_info_small_ad);
        ImageUtils.loadImage(mActivity, Constants.AD_ICON, mAdIcon);

        //头像按钮,头像链接的设置
        RelativeLayout rlHead = mView.findViewById(R.id.qcad_head_rl);
        mHeadLogo = mView.findViewById(R.id.qcad_head_logo);
        headLink = mView.findViewById(R.id.qcad_head_link);
        if (mAdAttr.getAdHeadSize() != 0) {
            int adHeadSize = mAdAttr.getAdHeadSize();
            //头像按钮
            RelativeLayout.LayoutParams headLogoParams = (RelativeLayout.LayoutParams) mHeadLogo.getLayoutParams();
            headLogoParams.width = DeviceUtil.dip2px(context, adHeadSize);
            headLogoParams.height = DeviceUtil.dip2px(context, adHeadSize);
            mHeadLogo.setLayoutParams(headLogoParams);

            //头像链接
            RelativeLayout.LayoutParams headLinkParams = (RelativeLayout.LayoutParams) headLink.getLayoutParams();
            int                         headLinkSize   = adHeadSize * 18 / 40;
            int                         margintop      = adHeadSize - adHeadSize * 10 / 40;
            headLinkParams.width = DeviceUtil.dip2px(context, headLinkSize);
            headLinkParams.height = DeviceUtil.dip2px(context, headLinkSize);
            headLinkParams.setMargins(0, DeviceUtil.dip2px(context, margintop), 0, 0);
            headLink.setLayoutParams(headLinkParams);
        }
        if (mAdAttr.getAdHeadType() == CoverType.OVAL) {
            mHeadLogo.setRadius(20);
        }
        if (!mAdAttr.isShowHeadLinkImage()) {
            headLink.setVisibility(GONE);
        }
        RelativeLayout.LayoutParams rlHeadParams = (RelativeLayout.LayoutParams) rlHead.getLayoutParams();
        if (mAdAttr.getAdHeadLayout() != null && mAdAttr.getAdHeadLayout().size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                rlHeadParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                rlHeadParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            } else {
                rlHeadParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                rlHeadParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            }
            for (AdLayout adLayout : mAdAttr.getAdHeadLayout()) {
                setRule(rlHeadParams, adLayout);
            }
            rlHead.setLayoutParams(rlHeadParams);
        }
        if (mAdAttr.isSetHeadMagin()) {
            int left   = DeviceUtil.dip2px(context, mAdAttr.getAdHeadMaginLeft());
            int top    = DeviceUtil.dip2px(context, mAdAttr.getAdHeadMaginTop());
            int right  = DeviceUtil.dip2px(context, mAdAttr.getAdHeadMaginRight());
            int bottom = DeviceUtil.dip2px(context, mAdAttr.getAdHeadMaginBottom());
            rlHeadParams.setMargins(left, top, right, bottom);
            rlHead.setLayoutParams(rlHeadParams);
        }
        mHeadLogo.setOnClickListener(this);
        rlHead.setOnClickListener(this);

        /**
         * 点赞按钮,点赞数量的设置
         */
        mPraiseImage = mView.findViewById(R.id.qcad_praise_image);
        mPraiseNumber = mView.findViewById(R.id.qcad_praise_number);
        mPraiseImage.setOnClickListener(this);
        mPraiseNumber.setOnClickListener(this);
        priseChooseDrawable = getResources().getDrawable(R.drawable.qcad_icon_praise_choose);
        priseDefaultDrawable = getResources().getDrawable(R.drawable.qcad_icon_praise_default);
        //点赞按钮的设置
        RelativeLayout.LayoutParams praiseImageParams = (RelativeLayout.LayoutParams) mPraiseImage.getLayoutParams();
        if (mAdAttr.getPraiseChooseImage() != null) {
            priseChooseDrawable = mAdAttr.getPraiseChooseImage();
        }
        if (mAdAttr.getPraiseDefaultImage() != null) {
            priseDefaultDrawable = mAdAttr.getPraiseDefaultImage();
        }

        mPraiseImage.setImageDrawable(priseDefaultDrawable);

        if (mAdAttr.getPraiseImageSize() != 0) {
            int praiseImageSize = mAdAttr.getPraiseImageSize();
            praiseImageParams.width = DeviceUtil.dip2px(context, praiseImageSize);
            praiseImageParams.height = DeviceUtil.dip2px(context, praiseImageSize);
            mPraiseImage.setLayoutParams(praiseImageParams);
        }
        if (mAdAttr.getPraiseImageLayout() != null && mAdAttr.getPraiseImageLayout().size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                praiseImageParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                praiseImageParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            } else {
                praiseImageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                praiseImageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            }
            for (AdLayout adLayout : mAdAttr.getPraiseImageLayout()) {
                setRule(praiseImageParams, adLayout);
            }
            mPraiseImage.setLayoutParams(praiseImageParams);
        }
        if (mAdAttr.isSetPraiseImageMagin()) {
            int left   = DeviceUtil.dip2px(context, mAdAttr.getPraiseImageMaginLeft());
            int top    = DeviceUtil.dip2px(context, mAdAttr.getPraiseImageMaginTop());
            int right  = DeviceUtil.dip2px(context, mAdAttr.getPraiseImageMaginRight());
            int bottom = DeviceUtil.dip2px(context, mAdAttr.getPraiseImageMaginBottom());
            praiseImageParams.setMargins(left, top, right, bottom);
            mPraiseImage.setLayoutParams(praiseImageParams);
        }

        //点赞数量设置
        if (mAdAttr.getPraiseNumberColor() != 0) {
            mPraiseNumber.setTextColor(mAdAttr.getPraiseNumberColor());
        }
        if (mAdAttr.getPraiseNumberSize() != 0) {
            mPraiseNumber.setTextSize(mAdAttr.getPraiseNumberSize());
        }
        RelativeLayout.LayoutParams praiseNumberParams = (RelativeLayout.LayoutParams) mPraiseNumber.getLayoutParams();
        if (mAdAttr.getPraiseNumberWidth() != 0) {
            //宽度设置
            int praiseImageWidth = mAdAttr.getPraiseNumberWidth();
            praiseNumberParams.width = DeviceUtil.dip2px(context, praiseImageWidth);
            mPraiseNumber.setLayoutParams(praiseNumberParams);
        }

        if (mAdAttr.getPraiseNumberLayout() != null && mAdAttr.getPraiseNumberLayout().size() > 0) {
            //位置设置
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                praiseNumberParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                praiseNumberParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            } else {
                praiseNumberParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                praiseNumberParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            }
            for (AdLayout adLayout : mAdAttr.getPraiseNumberLayout()) {
                setRule(praiseNumberParams, adLayout);
            }
            mPraiseNumber.setLayoutParams(praiseNumberParams);
        }

        if (mAdAttr.isSetPraiseNumberMagin()) {
            //Magin设置
            int left   = DeviceUtil.dip2px(context, mAdAttr.getPraiseNumberMaginLeft());
            int top    = DeviceUtil.dip2px(context, mAdAttr.getPraiseNumberMaginTop());
            int right  = DeviceUtil.dip2px(context, mAdAttr.getPraiseNumberMaginRight());
            int bottom = DeviceUtil.dip2px(context, mAdAttr.getPraiseNumberMaginBottom());
            praiseNumberParams.setMargins(left, top, right, bottom);
            mPraiseNumber.setLayoutParams(praiseNumberParams);
        }

        /**
         * 弹幕按钮,弹幕数量的设置
         */
        mBarrageImage = mView.findViewById(R.id.qcad_barrage_image);
        mBarrageNumber = mView.findViewById(R.id.qcad_barrage_number);
        mBarrageImage.setOnClickListener(this);
        mBarrageNumber.setOnClickListener(this);
        mShowBarrageFromSet = mAdAttr.isShowBarrage();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
            mShowBarrageFromSet = false;//版本太小,弹幕显示有问题,服务器广告已限制了5.0一下的广告的获取
        }
        //弹幕按钮的设置
        RelativeLayout.LayoutParams barrageImageParams = (RelativeLayout.LayoutParams) mBarrageImage.getLayoutParams();
        if (mAdAttr.getBarrageImageSize() != 0) {
            int barrageImageSize = mAdAttr.getBarrageImageSize();
            barrageImageParams.width = DeviceUtil.dip2px(context, barrageImageSize);
            barrageImageParams.height = DeviceUtil.dip2px(context, barrageImageSize);
            mBarrageImage.setLayoutParams(barrageImageParams);
        }

        if (mAdAttr.getBarrageImageLayout() != null && mAdAttr.getBarrageImageLayout().size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                barrageImageParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                barrageImageParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            } else {
                barrageImageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                barrageImageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            }
            for (AdLayout adLayout : mAdAttr.getBarrageImageLayout()) {
                setRule(barrageImageParams, adLayout);
            }
            mBarrageImage.setLayoutParams(barrageImageParams);
        }
        if (mAdAttr.isSetBarrageImageMagin()) {
            int left   = DeviceUtil.dip2px(context, mAdAttr.getBarrageImageMaginLeft());
            int top    = DeviceUtil.dip2px(context, mAdAttr.getBarrageImageMaginTop());
            int right  = DeviceUtil.dip2px(context, mAdAttr.getBarrageImageMaginRight());
            int bottom = DeviceUtil.dip2px(context, mAdAttr.getBarrageImageMaginBottom());
            barrageImageParams.setMargins(left, top, right, bottom);
            mBarrageImage.setLayoutParams(barrageImageParams);
        }

        if (mAdAttr.getBarrageImage() != null) {
            mBarrageImage.setImageDrawable(mAdAttr.getBarrageImage());
        }

        if (TextUtils.isEmpty(mImgAudioUrl) || !mShowBarrageFromSet) {
            mBarrageImage.setVisibility(View.GONE);
        }

        //弹幕数量的设置
        if (mAdAttr.getBarrageNumberColor() != 0) {
            mBarrageNumber.setTextColor(mAdAttr.getBarrageNumberColor());
        }
        if (mAdAttr.getBarrageNumberSize() != 0) {
            mBarrageNumber.setTextSize(mAdAttr.getBarrageNumberSize());
        }
        RelativeLayout.LayoutParams barrageNumberParams = (RelativeLayout.LayoutParams) mBarrageNumber.getLayoutParams();
        if (mAdAttr.getBarrageNumberWidth() != 0) {
            //宽度设置
            int barrageImageWidth = mAdAttr.getBarrageNumberWidth();
            barrageNumberParams.width = DeviceUtil.dip2px(context, barrageImageWidth);
            mBarrageNumber.setLayoutParams(barrageNumberParams);
        }
        if (mAdAttr.getBarrageNumberLayout() != null && mAdAttr.getBarrageNumberLayout().size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                barrageNumberParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                barrageNumberParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            } else {
                barrageNumberParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                barrageNumberParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            }
            for (AdLayout adLayout : mAdAttr.getBarrageNumberLayout()) {
                setRule(barrageNumberParams, adLayout);
            }
            mBarrageNumber.setLayoutParams(barrageNumberParams);
        }
        if (mAdAttr.isSetBarrageNumberMagin()) {
            int left   = DeviceUtil.dip2px(context, mAdAttr.getBarrageNumberMaginLeft());
            int top    = DeviceUtil.dip2px(context, mAdAttr.getBarrageNumberMaginTop());
            int right  = DeviceUtil.dip2px(context, mAdAttr.getBarrageNumberMaginRight());
            int bottom = DeviceUtil.dip2px(context, mAdAttr.getBarrageNumberMaginBottom());
            barrageNumberParams.setMargins(left, top, right, bottom);
            mBarrageNumber.setLayoutParams(barrageNumberParams);
        }

        if (TextUtils.isEmpty(mImgAudioUrl) || !mShowBarrageFromSet) {
            mBarrageNumber.setVisibility(GONE);
        }

        /**
         * 中间封面的设置
         */
        //封面和播放按钮父控件的设置
        mCoverImage = mView.findViewById(R.id.qcad_cover_image);
        RelativeLayout              musicRl          = mView.findViewById(R.id.qcad_music_rl);
        RelativeLayout.LayoutParams coverImageParams = (RelativeLayout.LayoutParams) mCoverImage.getLayoutParams();
        RelativeLayout.LayoutParams musicRlParams    = (RelativeLayout.LayoutParams) musicRl.getLayoutParams();
        if (mAdAttr.getCoverSize() != 0) {
            int coverImageSize = mAdAttr.getCoverSize();
            //封面设置
            coverImageParams.width = DeviceUtil.dip2px(context, coverImageSize);
            coverImageParams.height = DeviceUtil.dip2px(context, coverImageSize);
            mCoverImage.setLayoutParams(coverImageParams);

            //播放按钮父控件设置
            musicRlParams.width = DeviceUtil.dip2px(context, coverImageSize);
            musicRlParams.height = DeviceUtil.dip2px(context, coverImageSize);
            musicRl.setLayoutParams(musicRlParams);
        }

        if (mAdAttr.getCoverType() == CoverType.OVAL) {
            mCoverImage.setRadius(20);
        }

        if (mAdAttr.getCoverLayout() != null && mAdAttr.getCoverLayout().size() > 0) {
            //封面设置 播放按钮父控件设置
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                coverImageParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
                musicRlParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
            } else {
                coverImageParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                musicRlParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
            }
            for (AdLayout adLayout : mAdAttr.getCoverLayout()) {
                setRule(coverImageParams, adLayout);
                setRule(musicRlParams, adLayout);
            }
            mCoverImage.setLayoutParams(coverImageParams);
            musicRl.setLayoutParams(musicRlParams);
        }

        if (mAdAttr.isSetCoverMagin()) {
            int left   = DeviceUtil.dip2px(context, mAdAttr.getCoverMaginLeft());
            int top    = DeviceUtil.dip2px(context, mAdAttr.getCoverMaginTop());
            int right  = DeviceUtil.dip2px(context, mAdAttr.getCoverMaginRight());
            int bottom = DeviceUtil.dip2px(context, mAdAttr.getCoverMaginBottom());
            //封面设置
            coverImageParams.setMargins(left, top, right, bottom);
            mCoverImage.setLayoutParams(coverImageParams);

            //播放按钮父控件设置
            musicRlParams.setMargins(left, top, right, bottom);
            musicRl.setLayoutParams(musicRlParams);
        }

        /**
         * 封面播放按钮设置
         */
        mMusicBt = mView.findViewById(R.id.qcad_music_bt);
        musicPlayDrawable = getResources().getDrawable(R.drawable.qcad_icon_music_play);
        musicPauseDrawable = getResources().getDrawable(R.drawable.qcad_icon_music_pause);
        RelativeLayout.LayoutParams musicBtParams = (RelativeLayout.LayoutParams) mMusicBt.getLayoutParams();
        if (mAdAttr.getMusicBtPlayImage() != null) {
            musicPlayDrawable = mAdAttr.getMusicBtPlayImage();
        }
        if (mAdAttr.getMusicBtPauseImage() != null) {
            musicPauseDrawable = mAdAttr.getMusicBtPauseImage();
        }
        mMusicBt.setImageDrawable(musicPlayDrawable);

        if (mAdAttr.getMusicBtSize() != 0) {
            int musicBtSize = mAdAttr.getMusicBtSize();
            //封面播放按钮设置
            musicBtParams.width = DeviceUtil.dip2px(context, musicBtSize);
            musicBtParams.height = DeviceUtil.dip2px(context, musicBtSize);
            mMusicBt.setLayoutParams(musicBtParams);
            //不基于封面,以整个屏幕定位
            if (!mAdAttr.isToCover()) {
                //修改播放按钮父控件设置
                musicRlParams.width = DeviceUtil.dip2px(context, musicBtSize);
                musicRlParams.height = DeviceUtil.dip2px(context, musicBtSize);
                musicRl.setLayoutParams(musicRlParams);
            }
        }

        if (mAdAttr.getMusicBtLayout() != null && mAdAttr.getMusicBtLayout().size() > 0) {
            //封面播放按钮设置
            if (mAdAttr.isToCover()) {
                //基于封面,则设置按钮的位置
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    musicBtParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
                } else {
                    musicBtParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                }
                for (AdLayout adLayout : mAdAttr.getMusicBtLayout()) {
                    setRule(musicBtParams, adLayout);
                }
                mMusicBt.setLayoutParams(musicBtParams);
            } else {
                //不基于封面,以整个屏幕定位,需要设置父控件
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    musicRlParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
                    musicRlParams.removeRule(RelativeLayout.CENTER_VERTICAL);
                    musicRlParams.removeRule(RelativeLayout.CENTER_HORIZONTAL);
                    musicRlParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    musicRlParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    musicRlParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    musicRlParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
                } else {
                    musicRlParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                    musicRlParams.addRule(RelativeLayout.CENTER_VERTICAL, 0);
                    musicRlParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
                    musicRlParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    musicRlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                    musicRlParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                    musicRlParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                }
                for (AdLayout adLayout : mAdAttr.getMusicBtLayout()) {
                    setRule(musicRlParams, adLayout);
                }
                musicRl.setLayoutParams(musicRlParams);
            }
        }

        if (mAdAttr.isSetMusicBtMagin()) {
            int left   = DeviceUtil.dip2px(context, mAdAttr.getMusicBtMaginLeft());
            int top    = DeviceUtil.dip2px(context, mAdAttr.getMusicBtMaginTop());
            int right  = DeviceUtil.dip2px(context, mAdAttr.getMusicBtMaginRight());
            int bottom = DeviceUtil.dip2px(context, mAdAttr.getMusicBtMaginBottom());
            if (mAdAttr.isToCover()) {
                //基于封面,则设置按钮的位置
                musicBtParams.setMargins(left, top, right, bottom);
                mMusicBt.setLayoutParams(musicBtParams);
            } else {
                //不基于封面,以整个屏幕定位,需要设置父控件
                musicRlParams.setMargins(left, top, right, bottom);
                musicRl.setLayoutParams(musicRlParams);
            }
        }

        if (TextUtils.isEmpty(mImgAudioUrl)) {//无音频,播放按钮不展示
            mMusicBt.setVisibility(View.GONE);
        } else {
            mMusicBt.setOnClickListener(this);
        }

        /**
         * 播放完成的结束页面(大)
         */
        mLastLargeRl = mView.findViewById(R.id.qcad_last_large_rl);
        mLastLargeLogo = mView.findViewById(R.id.qcad_last_large_logo);
        mLastLargeTitle = mView.findViewById(R.id.qcad_last_large_title);
        mLastLargeContent = mView.findViewById(R.id.qcad_last_large_content);
        mLastLargeDetails = mView.findViewById(R.id.qcad_last_large_details);
        mLastLargeClick = mView.findViewById(R.id.qcad_last_large_click);
        mLastLargeDetails.setOnClickListener(this);
        mLastLargeClick.setOnClickListener(this);
        mLastLargeRl.setOnClickListener(this);//为了防止点击穿透到后方的播放按钮
        mLastLargeDetails.setBackground(getRoundRectDrawable(10, Color.parseColor("#FFFFFFFF"), true, 10));

        /**
         * 播放完成的结束页面(小)
         */
        mLastSmallRl = mView.findViewById(R.id.qcad_last_small_rl);
        mLastSmallLl = mView.findViewById(R.id.qcad_last_small_ll);
        mLastSmallLogo = mView.findViewById(R.id.qcad_last_small_logo);
        mLastSmallTitle = mView.findViewById(R.id.qcad_last_small_title);
        mLastSmallContent = mView.findViewById(R.id.qcad_last_small_content);
        mLastSmallClick = mView.findViewById(R.id.qcad_last_small_click);
        mLastSmallLl.setOnClickListener(this);

        /**
         * 跳过
         */
        mSkipLayout = mView.findViewById(R.id.qcad_info_skip_layout);
        skipTimer = mView.findViewById(R.id.qcad_info_skip_timer);
        skipLinear = mView.findViewById(R.id.qcad_info_skip_linear);
        skipView = mView.findViewById(R.id.qcad_info_skip);

        try {
            if (null != mAdAttr &&
                    mAdAttr.getSkipIsEnable() &&
                    null != mResponse &&
                    0 != mResponse.getDuration()
            ) {
                mSkipLayout.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mSkipLayout.getLayoutParams();
                layoutParams.addRule(mAdAttr.getSkipGravity());
                layoutParams.setMargins(
                        ScreenUtils.dp2px(mContext, mAdAttr.getSkipMarginLeft()),
                        ScreenUtils.dp2px(mContext, mAdAttr.getSkipMarginTop()),
                        ScreenUtils.dp2px(mContext, mAdAttr.getSkipMarginRight()),
                        ScreenUtils.dp2px(mContext, mAdAttr.getSkipMarginBottom())
                );

                adAutoCloseCountDownNumber = mResponse.getDuration();
                skipTimer.setText(String.valueOf(adAutoCloseCountDownNumber));
                mSkipLayout.setLayoutParams(layoutParams);
                skipView.setOnClickListener(this);
            } else {
                mSkipLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null != mAdAttr) {
            boolean isEnableRightView = mAdAttr.isEnableRightView();
            mBarrageImage.setVisibility(isEnableRightView ? View.VISIBLE : View.GONE);
            mBarrageNumber.setVisibility(isEnableRightView ? View.VISIBLE : View.GONE);
            mPraiseImage.setVisibility(isEnableRightView ? View.VISIBLE : View.GONE);
            mPraiseNumber.setVisibility(isEnableRightView ? View.VISIBLE : View.GONE);
            mHeadLogo.setVisibility(isEnableRightView ? View.VISIBLE : View.GONE);
            headLink.setVisibility(isEnableRightView ? View.VISIBLE : View.GONE);
            rlHead.setVisibility(isEnableRightView ? View.VISIBLE : View.GONE);
        }

        /**
         * 滚动弹幕的设置
         */
        mDanMuView = mView.findViewById(R.id.qcad_danmu_view);
        if (mShowBarrageFromSet) {
            //内容大小
            mBarrageContentSize = mAdAttr.getBarrageContentSize();
            if (mBarrageContentSize == 0) {
                mBarrageContentSize = 14;
            }
            //头像大小
            mBarrageHeadSize = mAdAttr.getBarrageHeadSize();
            if (mBarrageHeadSize <= 0) {
                mBarrageHeadSize = mBarrageContentSize + 6;
            } else if (mBarrageHeadSize < mBarrageContentSize) {
                mBarrageHeadSize = mBarrageContentSize + 2;
            }

            //内容颜色
            List<String> barrageContentColor = mAdAttr.getBarrageContentColor();
            if (barrageContentColor != null && barrageContentColor.size() > 0) {
                mBarrageContentColor.addAll(barrageContentColor);
            } else {
                mBarrageContentColor.add("#CE608C");
                mBarrageContentColor.add("#BC7D2F");
                mBarrageContentColor.add("#5C72D5");
                mBarrageContentColor.add("#23A69E");
            }

            //背景颜色
            mBarrageBackColor = mAdAttr.getBarrageBackColor();
            if (TextUtils.isEmpty(mBarrageBackColor)) {
                mBarrageBackColor = "#3F000000";
            }
            //位置
            RelativeLayout.LayoutParams danmuParams = (RelativeLayout.LayoutParams) mDanMuView.getLayoutParams();
            if (mAdAttr.isSetBarrageMagin()) {
                int left   = DeviceUtil.dip2px(context, mAdAttr.getBarrageMaginLeft());
                int top    = DeviceUtil.dip2px(context, mAdAttr.getBarrageMaginTop());
                int right  = DeviceUtil.dip2px(context, mAdAttr.getBarrageMaginRight());
                int bottom = DeviceUtil.dip2px(context, mAdAttr.getBarrageMaginBottom());
                //弹幕设置
                danmuParams.setMargins(left, top, right, bottom);
                mDanMuView.setLayoutParams(danmuParams);
            }

            //准备弹幕
            mDanMuView.prepare();
        } else {
            mDanMuView.setVisibility(GONE);
        }

        mView.setOnTouchListener(mOnTouchListener);
        //构建手势探测器
        mGestureDetector = new GestureDetector(mOnGestureListener);

        //渲染填充
        render();
    }

    private void adAutoCloseCountDown (long millisInFuture, long countDownInterval) {
        try {
            if (null != adAutoCloseCountDown) {
                adAutoCloseCountDown.cancel();
            }

            adAutoCloseCountDown = new CustomCountDownUtils(millisInFuture, countDownInterval);
            adAutoCloseCountDown.setOnCustomCountDownListener(new OnCustomCountDownListener() {
                @Override
                public void onTick (long millisUntilFinished) {
                    try {
                        adAutoCloseCountDownNumber = Long.valueOf(millisUntilFinished / 1000).intValue();
                        skipTimer.setText(String.format("%s", adAutoCloseCountDownNumber));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFinish () {
                    try {
                        adAutoCloseCountDownNumber = 0;
                        skipTimer.setText(String.valueOf(adAutoCloseCountDownNumber));

                        skipTimer.setVisibility(View.GONE);
                        skipLinear.setVisibility(View.GONE);

                        if (mAdAttr.getSkipAutoClose()) {
                            skipAd();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void adAutoCloseShowSkip (final long millisInFuture, long countDownInterval) {
        try {
            if (null != adAutoCloseShowSkip) {
                adAutoCloseShowSkip.cancel();
            }
            
            adAutoCloseShowSkip = new CustomCountDownUtils(millisInFuture, countDownInterval);
            adAutoCloseShowSkip.setOnCustomCountDownListener(new OnCustomCountDownListener() {
                @Override
                public void onTick (long millisUntilFinished) {
                    adAutoCloseCountDownShowSkipNumber = Long.valueOf(millisUntilFinished / 1000).intValue();
                }

                @Override
                public void onFinish () {
                    if (0 == adAutoCloseCountDownShowSkipNumber) {
                        skipLinear.setVisibility(View.VISIBLE);
                        skipView.setVisibility(View.VISIBLE);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取评论的具体内容的接口
     *
     * @param mid
     * @param creativeId
     * @param startSecond
     * @param endSecond
     */
    private void getComment (String mid, String creativeId, int startSecond, int endSecond) {
        if (!mInGetComment) {
            mInGetComment = true;
            QcHttpUtil.getComment(mid, creativeId, mCommentLinenumber, startSecond, endSecond,
                    new QcHttpUtil.QcHttpOnListener<AdCommentBean>() {
                        @Override
                        public void OnQcCompletionListener (AdCommentBean bean) {
                            mInGetComment = false;
                            if (bean != null) {
                                mCommentStartTime = mCommentStartTime + mCommentIntervalTime;
                                List<AdCommentItemBean> commentList = bean.getContent();
                                if (commentList != null && commentList.size() > 0) {
                                    mCommentList.addAll(commentList);
                                }
                                mPraiseNum = bean.getUpvote();
                                mCommentNum = bean.getContents();
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run () {
                                        mPraiseNumber.setText(String.valueOf(mPraiseNum));
                                        mBarrageNumber.setText(String.valueOf(mCommentNum));
                                    }
                                });
                            }
                        }

                        @Override
                        public void OnQcErrorListener (String erro, int code) {
                            mInGetComment = false;
                        }
                    });
        }
    }

    /**
     * 左下角类 弹窗的展示 需要在计算完屏幕的大小后,再决定是否展示
     */
    private void showLeftInfoView () {
        int infoWidth  = DeviceUtil.dip2px(mContext, 315);
        int infoHeight = DeviceUtil.dip2px(mContext, 90);
        //实际宽度大于info的宽度,实际高度大于info高度的两倍,则展示info信息
        if (mWidth >= infoWidth && mHeight >= infoHeight * 2) {
            mRlInfoSmall.setVisibility(VISIBLE);

            if (mAdAttr.getInfoTitleColor() != 0) {
                mTitleInfo.setTextColor(mAdAttr.getInfoTitleColor());
            }
            if (mAdAttr.getInfoContentColor() != 0) {
                mContentInfo.setTextColor(mAdAttr.getInfoContentColor());
            }
            if (mAdAttr.getInfoButtonColor() != 0) {
                mClickInfo.setTextColor(mAdAttr.getInfoButtonColor());
                mLastSmallClick.setTextColor(mAdAttr.getInfoButtonColor());
                mLastLargeClick.setTextColor(mAdAttr.getInfoButtonColor());
            }
            //下载按钮背景的切换 最后结束的下载按钮背景的切换
            if (mAdAttr.getInfoButtonBackgroundColor() != 0) {
                mClickInfo.setBackground(getRoundRectDrawable(50, mAdAttr.getInfoButtonBackgroundColor(), true, 10));
                mLastSmallClick.setBackground(getRoundRectDrawable(50, mAdAttr.getInfoButtonBackgroundColor(), true, 10));
                mLastLargeClick.setBackground(getRoundRectDrawable(50, mAdAttr.getInfoButtonBackgroundColor(), true, 10));

            } else {
                mClickInfo.setBackground(getRoundRectDrawable(50, Color.parseColor("#FFFF42A1"), true, 10));
            }

            //关闭按钮的点击
            mCloseInfo.setOnClickListener(this);

        } else {
            mRlInfoSmall.setVisibility(GONE);
        }
    }

    /**
     * 渲染界面
     */
    public void render () {
        setData();
        if (mView != null) {
            addView(mView);
        }
    }

    /**
     * 初始化数据
     */
    private void setData () {
        if (mResponse != null) {
            //弹幕评论的展示
            mImgAudioUrl = mResponse.getAudiourl();
            String backdrop = mResponse.getCompanion() != null ? mResponse.getCompanion().getUrl() : "";
            mBarrageNumber.setText(String.valueOf(mCommentNum));
            mPraiseNumber.setText(String.valueOf(mPraiseNum));
            if (SpUtils.getBoolean(mResponse.getCreativeid())) {
                mPraiseImage.setImageDrawable(priseChooseDrawable);
            } else {
                mPraiseImage.setImageDrawable(priseDefaultDrawable);
            }

            //封面展示
            ImageUtils.loadImage(mActivity, mResponse.getFirstimg(), mCoverImage);
            //背景图片的展示
            ImageUtils.loadImage(mActivity, backdrop, mIvBackground);
            String title  = mResponse.getTitle();
            String desc   = mResponse.getDesc();
            int    action = mResponse.getAction();

            //其他数据的展示
            if (null != mResponse.getIcon() && !TextUtils.isEmpty(mResponse.getIcon().getUrl())) {
                //多位置LOGO展示
                String mIcon = mResponse.getIcon().getUrl();
                mLogoInfo.setVisibility(VISIBLE);
                ImageUtils.loadImage(mActivity, mIcon, mLogoInfo);
                ImageUtils.loadImage(mActivity, mIcon, mHeadLogo);
                ImageUtils.loadImage(mActivity, mIcon, mLastLargeLogo);
                ImageUtils.loadImage(mActivity, mIcon, mLastSmallLogo);
                LogUtils.e("init中onLayout中未加载icon=" + mIcon);
            } else {
                mLogoInfo.setVisibility(GONE);
                LogUtils.e("init中onLayout未获取到图片资源");
            }

            //多位置标题展示
            if (!TextUtils.isEmpty(title)) {
                mTvTitle.setText(title);
                mTitleInfo.setText(title);
                mLastLargeTitle.setText(title);
                mLastSmallTitle.setText(title);
            } else {
                mLastLargeTitle.setVisibility(View.GONE);
                mLastSmallTitle.setVisibility(View.GONE);
            }

            //多位置内容展示
            if (!TextUtils.isEmpty(desc)) {
                //设置最后面广告的样式
                final String content = desc + "   ";

                ThreadManagerUtil.getDefaultProxy().execute(new Runnable() {
                    @Override
                    public void run () {
                        SpannableString   string      = new SpannableString(content);
                        Bitmap            imageBitMap = getImageBitMap(Constants.AD_ICON);
                        VerticalImageSpan imageSpan   = new VerticalImageSpan(mContext, imageBitMap);
                        string.setSpan(imageSpan, content.length() - 1, content.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        final SpannableString strings = string;
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run () {
                                mTvContent.setText(strings);
                            }
                        });
                    }
                });

                mTvContent.setText(content);
                mContentInfo.setText(desc);
                mLastLargeContent.setText(desc);
                mLastSmallContent.setText(desc);
            } else {
                mLastLargeContent.setVisibility(View.GONE);
                mLastSmallContent.setVisibility(View.GONE);
            }

            //整点报时转成正常素材需要重新展示按钮
            mLastSmallClick.setVisibility(VISIBLE);
            mLastLargeClick.setVisibility(VISIBLE);
            mClickInfo.setVisibility(VISIBLE);
            mLastLargeDetails.setVisibility(VISIBLE);

            //根据action显示不同的按钮的额名称
            if (action == 6) {//下载
                mLastSmallClick.setText("立即下载");
                mLastLargeClick.setText("立即下载");
                mClickInfo.setText("立即下载");
            } else if (action == 1 || action == 2) {
                mLastSmallClick.setText("点击打开");
                mLastLargeClick.setText("点击打开");
                mClickInfo.setText("点击打开");
            } else if (action == 7) {
                mLastSmallClick.setText("查看详情");
                mLastLargeClick.setText("查看详情");
                mClickInfo.setText("查看详情");
            } else if (action == 3) {
                mLastSmallClick.setText("拨打电话");
                mLastLargeClick.setText("拨打电话");
                mClickInfo.setText("拨打电话");
            } else if (action == 8) {
                mLastSmallClick.setText("领优惠券");
                mLastLargeClick.setText("领优惠券");
                mClickInfo.setText("领优惠券");
            } else if (action == 0) {
                mLastSmallClick.setVisibility(GONE);
                mLastLargeClick.setVisibility(GONE);
                mClickInfo.setVisibility(GONE);
                mLastLargeDetails.setVisibility(GONE);
            }
        }
    }

    /**
     * 点击事件
     */
    @Override
    public void onClick (View view) {
        int id = view.getId();
        if (id == R.id.qcad_last_large_details || id == R.id.qcad_last_large_click
                || id == R.id.rl_qcad_info_small || id == R.id.qcad_head_rl
                || id == R.id.qcad_head_logo || id == R.id.qcad_last_small_ll
                || id == R.id.qcad_custom_title || id == R.id.qcad_custom_content) {
            //最后结束展示页面的点击事件,左下方按钮点击,头像的点击
            if (null != mResponse) {
                setClick(mActivity, mResponse);
            }
        } else if (id == R.id.qcad_info_small_close) {
            //左下方的关闭按钮的点击事件
            mRlInfoSmall.setVisibility(GONE);
        } else if (id == R.id.qcad_praise_image || id == R.id.qcad_praise_number) {
            try {
                //点赞按钮,数量按钮的点击
                if (!SpUtils.getBoolean(mResponse.getCreativeid())) {
                    QcHttpUtil.upPraise(mAdAttr.getMid(), mResponse.getCreativeid(), new QcHttpUtil.QcHttpOnListener<String>() {
                        @Override
                        public void OnQcCompletionListener (String response) {
                            LogUtils.e("正常返回数据code=200");
                            SpUtils.saveBoolean(mResponse.getCreativeid(), true);
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run () {
                                    mPraiseImage.setImageDrawable(priseChooseDrawable);
                                    mPraiseNum++;
                                    mPraiseNumber.setText(mPraiseNum + "");
                                }
                            });
                        }

                        @Override
                        public void OnQcErrorListener (String error, int code) {

                        }
                    });
                } else {
                    SpUtils.saveBoolean(mResponse.getCreativeid(), false);
                    mPraiseImage.setImageDrawable(priseDefaultDrawable);
                    mPraiseNum--;
                    if (0 > mPraiseNum) {
                        mPraiseNum = 0;
                    }
                    mPraiseNumber.setText(mPraiseNum + "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.qcad_barrage_image || id == R.id.qcad_barrage_number) {
            //弹幕按钮,数量按钮的点击
            //音频停止
            MediaPlayerUtil.getInstance().pausePlay();
            mMusicBt.setImageDrawable(musicPlayDrawable);
            if (mDanMuView != null && mShowBarrageFromSet) {//弹幕停止
                mDanMuView.pauseAllDanMuView();
            }
            //输入弹窗
            DialogUtils.showEditDialog(mActivity, new EditDialogCallback() {
                @Override
                public void sendMsg (final String content) {
                    if (TextUtils.isEmpty(content)) {
                        Toast.makeText(mContext, "输入内容不能为空", Toast.LENGTH_SHORT).show();
                    } else {
                        //继续播放
                        DialogUtils.dismissEditDialog();
                        UserBean userInfo = QCiVoiceSdk.get().getUserInfo();
                        String   userId   = "";
                        String   avater   = "";
                        if (userInfo != null) {
                            userId = userInfo.getUserId();
                            avater = userInfo.getAvatar();
                        }
                        final String finalUserId = userId;
                        final String finalAvater = avater;
                        QcHttpUtil.upComment(mAdAttr.getMid(), mResponse.getCreativeid(), mCurrentTime,
                                content, avater, userId, new QcHttpUtil.QcHttpOnListener<String>() {
                                    @Override
                                    public void OnQcCompletionListener (String response) {
                                        LogUtils.e("正常返回数据code=200");
                                        //把弹幕发送出去
                                        if (mDanMuView != null && mShowBarrageFromSet) {//弹幕继续
                                            sendDanMu(content, finalUserId, finalAvater, true);
                                        }
                                        mCommentNum++;
                                        mBarrageNumber.setText(mCommentNum + "");
                                    }

                                    @Override
                                    public void OnQcErrorListener (String erro, int code) {

                                    }
                                });
                    }
                }

                @Override
                public void inputDismiss () {
                    MediaPlayerUtil.getInstance().resumePlay();
                    mMusicBt.setImageDrawable(musicPauseDrawable);
                    if (mDanMuView != null && mShowBarrageFromSet) {//弹幕继续
                        mDanMuView.continueAllDanMuView();
                    }
                }
            });
        } else if (id == R.id.qcad_music_bt) {
            //播放按钮的点击
            if (MediaPlayerUtil.getInstance().currentPlayStatue == MediaPlayerUtil.getInstance().STOP) {
                //是停止状态,则开始播放,显示暂停的按钮
                playAd();
                mMusicBt.setImageDrawable(musicPauseDrawable);

                MediaPlayerUtil.getInstance().setUserClickStop(0);
            } else if (MediaPlayerUtil.getInstance().currentPlayStatue == MediaPlayerUtil.getInstance().PAUSE) {
                //是停止状态,则继续播放,显示暂停的按钮
                MediaPlayerUtil.getInstance().resumePlay();
                mMusicBt.setImageDrawable(musicPauseDrawable);
                if (mDanMuView != null && mShowBarrageFromSet) {//弹幕继续
                    mDanMuView.continueAllDanMuView();
                }
                MediaPlayerUtil.getInstance().setUserClickStop(0);

                skipOnResume();
            } else if (MediaPlayerUtil.getInstance().currentPlayStatue == MediaPlayerUtil.getInstance().PLAY) {
                //是播放状态,则暂停播放,显示播放按钮
                MediaPlayerUtil.getInstance().pausePlay();
                mMusicBt.setImageDrawable(musicPlayDrawable);
                if (mDanMuView != null && mShowBarrageFromSet) {//弹幕停止
                    mDanMuView.pauseAllDanMuView();
                }
                MediaPlayerUtil.getInstance().setUserClickStop(1);
                skipOnPause();
            }
        } else if (id == R.id.qcad_info_skip) {
            skipAd();

            if (mLastShowLarge) {
                mLastLargeRl.setVisibility(View.VISIBLE);
            } else {
                mLastSmallRl.setVisibility(View.VISIBLE);
            }

            mSkipLayout.setVisibility(View.GONE);

            if (mListener != null) {
                mListener.onAdCompletion();
            }

            clearShake();
            initShakeUtils();
        }
    }

    private void skipOnResume () {
        try {
            if (View.VISIBLE == mLastLargeRl.getVisibility()
                    || View.VISIBLE == mLastSmallRl.getVisibility()) {
                return;
            }

            if (mAdAttr.getSkipIsEnable()) {
                mSkipLayout.setVisibility(View.VISIBLE);

                if (0 != adAutoCloseCountDownNumber) {
                    if (adAutoCloseCountDown != null) {
                        adAutoCloseCountDown.cancel();
                    }

                    adAutoCloseCountDown(adAutoCloseCountDownNumber * 1000, 1000);
                    adAutoCloseCountDown.start();
                }

                if (0 != adAutoCloseCountDownShowSkipNumber) {
                    if (adAutoCloseShowSkip != null) {
                        adAutoCloseShowSkip.cancel();
                    }

                    adAutoCloseShowSkip(adAutoCloseCountDownShowSkipNumber * 1000, 1000);
                    adAutoCloseShowSkip.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void skipOnPause () {
        if (mAdAttr.getSkipIsEnable()) {
            if (adAutoCloseCountDown != null) {
                adAutoCloseCountDown.cancel();
            }

            if (adAutoCloseShowSkip != null) {
                adAutoCloseShowSkip.cancel();
                adAutoCloseShowSkip.onFinish();
            }
        }
    }

    /**
     * 获取规则位置
     */
    private void setRule (RelativeLayout.LayoutParams params, AdLayout adLayout) {
        if (adLayout == AdLayout.CENTER_HORIZONTAL) {
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        } else if (adLayout == AdLayout.CENTER_VERTICAL) {
            params.addRule(RelativeLayout.CENTER_VERTICAL);
        } else if (adLayout == AdLayout.CENTER_IN_PARENT) {
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else if (adLayout == AdLayout.TOP_LEFT) {
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else if (adLayout == AdLayout.TOP_RIGHT) {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        } else if (adLayout == AdLayout.BOTTOM_LEFT) {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else if (adLayout == AdLayout.BOTTOM_RIGHT) {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
    }

    /**
     * 动态设置控件圆角背景
     *
     * @param radius
     * @param color
     * @param isFill
     * @param strokeWidth
     * @return
     */
    public static GradientDrawable getRoundRectDrawable (int radius, int color, boolean isFill, int strokeWidth) {
        //左上、右上、右下、左下的圆角半径
        float[]          radiuss  = {radius, radius, radius, radius, radius, radius, radius, radius};
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadii(radiuss);
        drawable.setColor(isFill ? color : Color.TRANSPARENT);
        drawable.setStroke(isFill ? 0 : strokeWidth, color);
        return drawable;
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
                    MediaPlayerUtil.getInstance().playVoiceList(mContext, voiceList, mMediaMoreListener);
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
     * 判断权限
     */
    private int checkNeedPermissions () {
        return PermissionUtil.checkAudioAndWritePermissions(mContext);
    }

    /**
     * 初始化录音操作
     */
    private void initRecorderOperation () {
        customMonitorVolumeUtils = new CustomMonitorVolumeUtils(mContext);
        VoiceInteractiveUtil.getInstance().initRecorderOperation(mActivity, customMonitorVolumeUtils, new OnVolumeEndListener() {
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
        VoiceInteractiveUtil.getInstance().setRecordListener(mActivity,
                new QcHttpUtil.QcHttpOnListener<UpVoiceResultBean>() {
                    @Override
                    public void OnQcCompletionListener (UpVoiceResultBean response) {
                        //1:肯定，0:否定，999: 无法识别
                        if (UpVoiceResultBean.FAIl == response.getCode()) {
                            // 否定，结束交互
                            onAdCompleteCallBack();
                        } else if (UpVoiceResultBean.SUCCESS == response.getCode()) {
                            //肯定，有互动的时候
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
            //结束互动
            return;
        }
        final int             volume       = bean.getVolume();
        final InteractiveBean interactive  = bean.getInteractive();
        int                   perMis       = PermissionUtil.checkAudioAndWritePermissions(mContext);
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
                    mMusicBt.setImageDrawable(musicPauseDrawable);
                    if (mDanMuView != null && mShowBarrageFromSet) {//弹幕继续
                        mDanMuView.continueAllDanMuView();
                    }
                } else {
                    if (MediaPlayerUtil.getInstance().currentPlayStatue == MediaPlayerUtil.getInstance().PLAY) {
                        //是播放状态,则暂停播放,显示播放按钮
                        MediaPlayerUtil.getInstance().pausePlay();
                        mMusicBt.setImageDrawable(musicPlayDrawable);
                        if (mDanMuView != null && mShowBarrageFromSet) {//弹幕停止
                            mDanMuView.pauseAllDanMuView();
                        }
                    }
                }
            }
        });
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
                MediaPlayerUtil.getInstance().setMinVolume(volume);
                MediaPlayerUtil.getInstance().playVoice(
                        mActivity,
                        startMusic,
                        new MediaPlayerUtil.MediaOnListener() {
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
                                    mMusicBt.setImageDrawable(musicPauseDrawable);
                                    if (mDanMuView != null && mShowBarrageFromSet) {//弹幕继续
                                        mDanMuView.continueAllDanMuView();
                                    }
                                } else {
                                    if (MediaPlayerUtil.getInstance().currentPlayStatue == MediaPlayerUtil.getInstance().PLAY) {
                                        //是播放状态,则暂停播放,显示播放按钮
                                        MediaPlayerUtil.getInstance().pausePlay();
                                        mMusicBt.setImageDrawable(musicPlayDrawable);
                                        if (mDanMuView != null && mShowBarrageFromSet) {//弹幕停止
                                            mDanMuView.pauseAllDanMuView();
                                        }
                                    }
                                }
                            }
                        });
            } else {
                //无互动音频的时候,隐藏弹幕,隐藏弹幕按钮,隐藏播放按钮,无结束页
                if (mDanMuView != null) {
                    mDanMuView.setVisibility(GONE);
                    mDanMuView.release();
                }
                mBarrageImage.setVisibility(GONE);
                mBarrageNumber.setVisibility(GONE);
                mMusicBt.setVisibility(GONE);
            }
        } else {
            //无互动音频的时候,隐藏弹幕,隐藏弹幕按钮,隐藏播放按钮,无结束页
            if (mDanMuView != null) {
                mDanMuView.setVisibility(GONE);
                mDanMuView.release();
            }
            mBarrageImage.setVisibility(GONE);
            mBarrageNumber.setVisibility(GONE);
            mMusicBt.setVisibility(GONE);
        }
    }


    /**
     * 按钮的点击事件
     */
    public void setClick (Activity activity, AdAudioBean bean) {
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
                            pauseMusicAndDanMu();
                        } else {
                            resumeMusicAndDanMu();
                        }
                    }
                });
    }

    public void pause () {
        pauseMusicAndDanMu();
        skipOnPause();
    }

    public void resume () {
        resumeMusicAndDanMu();
        skipOnResume();
    }

    /**
     * 暂停播放,停止弹幕
     */
    public void pauseMusicAndDanMu () {
        if (mAdAttr != null && mAdAttr.isCanPause()) {
            closeCommonHandler();
            MediaPlayerUtil.getInstance().pausePlay();
            mMusicBt.setImageDrawable(musicPlayDrawable);
            if (mDanMuView != null && mShowBarrageFromSet) {//弹幕停止
                mDanMuView.pauseAllDanMuView();
            }
        }
    }

    /**
     * 继续播放,继续弹幕
     */
    public void resumeMusicAndDanMu () {
        if (mAdAttr != null && mAdAttr.isCanPause()) {
            MediaPlayerUtil.getInstance().resumePlay();
            mMusicBt.setImageDrawable(musicPauseDrawable);
            if (mDanMuView != null && mShowBarrageFromSet) {//弹幕停止
                mDanMuView.continueAllDanMuView();
            }
        }
    }

    /**
     * 点击广告操作后回调
     */
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == mPosition) {
            //为了解决弹窗选择浏览器框实际并未跳转
            if (!SpUtils.getBoolean("show")) {
                SpUtils.saveBoolean("show", true);
                if (mLastShowLarge) {
                    mLastLargeRl.setVisibility(View.VISIBLE);
                } else {
                    mLastSmallRl.setVisibility(View.VISIBLE);
                }

                skipOnPause();
                stopAndReleaseAd();

                mSkipLayout.setVisibility(View.GONE);

                if (mListener != null && isFirstCallBack) {
                    isFirstCallBack = false;
                    mListener.onAdCompletion();
                }
            } else {
                resumeMusicAndDanMu();
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
    }

    /**
     * 获取点击时候的坐标值
     * 注意返回值
     * true：view继续响应Touch操作
     * false：view不再响应Touch操作，故此处若为false，只能显示起始位置，不能显示实时位置和结束位置
     */
    @SuppressLint("ClickableViewAccessibility")
    public void getClickXYPosition (View view) {
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch (View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:   //点击的开始位置
                        mClickX = event.getX();
                        mClickY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:   //触屏实时位置
                        break;
                    case MotionEvent.ACTION_UP:     //离开屏幕的位置
                        break;
                    default:
                        break;
                }
                return false;
            }

        });
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
     * 广告位点击请求(企创广告)
     */
    public void sendClickExposure (final List<String> list) {
        if (!mHaveSendClick) {
            mHaveSendClick = true;
            if (list != null && !list.isEmpty()) {
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
                    if (url.contains("__POSITION_X__")) {//抬起X轴的替换
                        url = url.replace("__POSITION_X__", mPositionX + "");
                    }
                    if (url.contains("__POSITION_Y__")) {//抬起Y轴的替换
                        url = url.replace("__POSITION_Y__", mPositionY + "");
                    }
                    if (url.contains("__WIDTH__")) {//宽度替换
                        url = url.replace("__WIDTH__", mWidth + "");
                    }
                    if (url.contains("__HEIGHT__")) {//高度替换
                        url = url.replace("__HEIGHT__", mHeight + "");
                    }
                    if (url.contains("__TIME_STAMP__")) {//时间戳的替换
                        url = url.replace("__TIME_STAMP__", time + "");
                    }

                    QcHttpUtil.sendAdExposure(url);
                }
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

    /**
     * 播放音频
     */
    public void playAd () {
        int[] position = new int[2];
        getLocationOnScreen(position);
        mPositionX = position[0]; // view距离 屏幕左边的距离（即x轴方向）
        mPositionY = position[1]; // view距离 屏幕顶边的距离（即y轴方向）
        LogUtils.e("广告区域左上角坐标2X=" + mPositionX + "|Y=" + mPositionY);
        if (mResponse != null) {
            //准备弹幕
            if (mDanMuView != null && mShowBarrageFromSet) {
                mDanMuView.setVisibility(VISIBLE);
                mDanMuView.prepare();
            }
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run () {
                    mLastLargeRl.setVisibility(View.GONE);
                    mLastSmallRl.setVisibility(View.GONE);
                    mRlInfoSmall.setVisibility(VISIBLE);
                    isFirstCallBack = true;
                }
            });

            if (mMusicBt != null && musicPauseDrawable != null) {
                mMusicBt.setImageDrawable(musicPauseDrawable);
            }

            //整点报时
            setChime();

            if (null != mResponse &&
                    null != mAdAttr &&
                    mAdAttr.getSkipIsEnable() &&
                    0 != mResponse.getDuration() &&
                    0 != mResponse.getSkip()
            ) {
                try {
                    mSkipLayout.setVisibility(View.VISIBLE);
                    skipTimer.setVisibility(View.VISIBLE);
                    skipLinear.setVisibility(View.GONE);
                    skipView.setVisibility(View.GONE);

                    adAutoCloseCountDownNumber = mResponse.getDuration();
                    adAutoCloseCountDown(adAutoCloseCountDownNumber * 1000, 1000);

                    adAutoCloseCountDownShowSkipNumber = mResponse.getSkip();
                    adAutoCloseShowSkip(adAutoCloseCountDownShowSkipNumber * 1000, 1000);

                    if (adAutoCloseCountDown != null) {
                        adAutoCloseCountDown.start();
                    }

                    if (adAutoCloseShowSkip != null) {
                        adAutoCloseShowSkip.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            callbackType = null == mResponse.getRendering_config() ? 0 : mResponse.getRendering_config().getStop_playing_mode();

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
     * 设置整点报时：
     */
    private void setChime () {
        if (chimeBean != null && chimeBean.getSection() != null) {
            long currentTime = System.currentTimeMillis();
            //没到整点报时时间显示正常素材
            if (currentTime < chimeBean.getSection().getS() && mResponse != normalBean) {
                mResponse = normalBean;
                setData();
                //过了整点报时时间重置正常素材
            } else if (currentTime > chimeBean.getSection().getE() && mResponse != normalBean) {
                chimeBean = null;
                mResponse = normalBean;
                setData();
            } else {
                //显示整点报时素材
                if (mResponse != chimeBean) {
                    mResponse = chimeBean;
                    setData();
                }
            }
        }
    }

    /**
     * 初始化摇一摇操作
     */
    private void initShakeUtils () {
        //摇一摇
        if (mResponse != null) {
            mShakeUtils = new ShakeUtils(mContext);
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
     * 重新播放音频
     */
    private void resumePlayAd () {
        MediaPlayerUtil.getInstance().resumePlay();
        mMusicBt.setImageDrawable(musicPauseDrawable);
    }

    /**
     * 停止播放音频
     */
    public void stopAd () {
        MediaPlayerUtil.getInstance().stopPlay();
        mMusicBt.setImageDrawable(musicPlayDrawable);
    }

    /**
     * 跳过广告 停止音频播放 给外部调用 需要添加close的曝光监听
     */
    public void skipAd () {
        stopAndReleaseAd();

        try {
            if (mAdAttr.getSkipIsEnable()) {
                if (adAutoCloseCountDown != null) {
                    adAutoCloseCountDown.cancel();
                }

                if (adAutoCloseShowSkip != null) {
                    adAutoCloseShowSkip.cancel();
                }

                adAutoCloseCountDownNumber = 0;
                skipTimer.setText(String.valueOf(adAutoCloseCountDownNumber));
                skipTimer.setVisibility(View.GONE);
                skipLinear.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放音频
     */
    private void stopAndReleaseAd () {
        MediaPlayerUtil.getInstance().stopAndRelease();
        mMusicBt.setImageDrawable(musicPlayDrawable);
        musicPlayExposure(0, 3);
        if (mDanMuView != null && mShowBarrageFromSet) {
            mDanMuView.release();
            mDanMuView.setVisibility(GONE);
        }
        DialogUtils.closeCountDownTime();
        closeCommonHandler();
        clearShake();
    }

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
                //处理弹幕的播放
                managerDanMu(currentTime);
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
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run () {
                        //改动界面
                        if (mLastShowLarge) {
                            LogUtils.e("CustomAudioAdView:" + "height:" + mLastLargeRl.getHeight() + ",weight:" + mLastLargeRl.getWidth());
                            mLastLargeRl.setVisibility(View.VISIBLE);
                        } else {
                            mLastSmallRl.setVisibility(View.VISIBLE);
                        }

                        skipOnPause();

                        mSkipLayout.setVisibility(View.GONE);
                    }
                });

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
                mMusicBt.setImageDrawable(musicPauseDrawable);
                if (mDanMuView != null && mShowBarrageFromSet) {//弹幕继续
                    mDanMuView.continueAllDanMuView();
                }
            } else {
                if (MediaPlayerUtil.getInstance().currentPlayStatue == MediaPlayerUtil.getInstance().PLAY) {
                    //是播放状态,则暂停播放,显示播放按钮
                    MediaPlayerUtil.getInstance().pausePlay();
                    mMusicBt.setImageDrawable(musicPlayDrawable);
                    if (mDanMuView != null && mShowBarrageFromSet) {//弹幕停止
                        mDanMuView.pauseAllDanMuView();
                    }
                }
            }
        }
    };

    /**
     * 处理弹幕的播放
     *
     * @param currentTime
     */
    private void managerDanMu (int currentTime) {
        if (mShowBarrageFromSet) {
            if (currentTime + 2 >= mCommentStartTime) {
                getComment(mAdAttr.getMid(), mResponse.getCreativeid(), mCommentStartTime + 1,
                        mCommentStartTime + mCommentIntervalTime);
            }
            if (mCommentList != null && mCommentList.size() > 0) {
                for (AdCommentItemBean bean : mCommentList) {
                    if (bean != null) {
                        int    time          = bean.getTime();
                        String base64Content = bean.getContent();
                        String fromBase64    = Base64.getFromBase64(base64Content);
                        String userid        = bean.getUserid();
                        String avatar        = bean.getAvatar();
                        if (time == currentTime) {
                            sendDanMu(fromBase64, userid, avatar, false);
                        }
                    }
                }
            }
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
            //处理弹幕的播放
            managerDanMu(currentTime);
        }

        @Override
        public void onPlayCompletionListener () {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run () {
                    //改动界面
                    if (mLastShowLarge) {
                        mLastLargeRl.setVisibility(View.VISIBLE);
                    } else {
                        mLastSmallRl.setVisibility(View.VISIBLE);
                    }

                    skipOnPause();

                    mSkipLayout.setVisibility(View.GONE);
                }
            });
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
                mMusicBt.setImageDrawable(musicPauseDrawable);
                if (mDanMuView != null && mShowBarrageFromSet) {//弹幕继续
                    mDanMuView.continueAllDanMuView();
                }
            } else {
                if (MediaPlayerUtil.getInstance().currentPlayStatue == MediaPlayerUtil.getInstance().PLAY) {
                    //是播放状态,则暂停播放,显示播放按钮
                    MediaPlayerUtil.getInstance().pausePlay();
                    mMusicBt.setImageDrawable(musicPlayDrawable);
                    if (mDanMuView != null && mShowBarrageFromSet) {//弹幕停止
                        mDanMuView.pauseAllDanMuView();
                    }
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
        LogUtils.e("qcsdk,clearShake========");
        if (mShakeUtils != null) {
            mShakeUtils.onPause();
            mShakeUtils.clear();
            mShakeUtils = null;
        }
    }

    /**
     * 发送弹幕
     *
     * @param content
     * @param userId
     * @param avater
     * @param isOneself 是否是自己发送的消息
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void sendDanMu (String content, final String userId, final String avater, boolean isOneself) {
        DanMuModel danMuView = new DanMuModel();
        danMuView.setDisplayType(DanMuModel.RIGHT_TO_LEFT);
        danMuView.setPriority(DanMuModel.NORMAL);
        if (mAdAttr != null) {
            int barrageSpeed = mAdAttr.getBarrageSpeed();
            danMuView.setUserSpeed(barrageSpeed);
        }
        danMuView.marginLeft = DimensionUtil.dpToPx(mActivity, 0);

        int    textSize   = mBarrageContentSize;
        int    avatarSize = mBarrageHeadSize;
        String textColor  = getTextColor();
        if (isOneself) {
            textColor = "#FFFFFF";
        }
        // 显示的头像
        danMuView.avatar = BitmapFactory.decodeResource(getResources(), R.drawable.qcad_barrage_head);
        danMuView.avatarWidth = DimensionUtil.dpToPx(mActivity, avatarSize);
        danMuView.avatarHeight = DimensionUtil.dpToPx(mActivity, avatarSize);

        // 显示的文本内容
        danMuView.textSize = DimensionUtil.spToPx(mActivity, textSize);
        danMuView.textMarginLeft = DimensionUtil.dpToPx(mActivity, 4);
        danMuView.text = content;

        danMuView.textColor = Color.parseColor(textColor);

        // 弹幕文本背景
        GradientDrawable drawable = null;
        drawable = (GradientDrawable) getContext().getResources().getDrawable(R.drawable.qcad_shape_barrage);
        drawable.setColor(Color.parseColor(mBarrageBackColor));
        drawable.setStroke(1, Color.parseColor(textColor));
        danMuView.textBackground = drawable;
        danMuView.textBackgroundMarginLeft = DimensionUtil.dpToPx(mActivity, 14);
        danMuView.textBackgroundPaddingTop = DimensionUtil.dpToPx(mActivity, 3);
        danMuView.textBackgroundPaddingBottom = DimensionUtil.dpToPx(mActivity, 3);
        danMuView.textBackgroundPaddingRight = DimensionUtil.dpToPx(mActivity, 14);

        //点击事件
        danMuView.enableTouch(false);
        danMuView.setOnTouchCallBackListener(new OnDanMuTouchCallBackListener() {
            @Override
            public void callBack (final DanMuModel danMuView, int x, int distanceY) {
                if (!danMuView.isClick()) {
                    danMuView.isClick = true;
                    danMuView.setPriority(DanMuModel.NORMALCLICK);
                    DanMuPopupUtil.showBarrageDialog(mActivity, mDanMuView, danMuView, avater,
                            (int) danMuView.getxLeftPosition(), (int) danMuView.getyLeftPosition() + distanceY,
                            false, new DanMuPopupUtil.OnBarragePopupCallBack() {
                                @Override
                                public void onDismiss () {
                                    LogUtils.e("指定的弹窗消失了");
                                    danMuView.setPriority(DanMuModel.NORMAL);
                                    danMuView.isClick = false;
                                }

                                @Override
                                public void onUserInfo () {
                                    if (mListener != null) {
                                        mListener.onAdUserInfo(userId, avater);
                                    }
                                }
                            });
                }
            }
        });

        //更新弹幕的大小 ,防止弹幕太大的时候,行与行之间被覆盖
        mDanMuView.setChannelHeight(avatarSize);
        if (isOneself) {
            List<DanMuModel> danMuViews = new ArrayList<>();
            danMuViews.add(danMuView);
            mDanMuView.jumpQueue(danMuViews);
        } else {
            mDanMuView.add(danMuView);
        }

    }

    /**
     * 获取弹幕的颜色
     */
    private String getTextColor () {
        Random random      = new Random();
        int    i           = random.nextInt(mBarrageContentColor.size());
        String selectColor = mBarrageContentColor.get(i);
        if (mBarrageContentColor.size() == 1) {
            mLastDanmuContentColor = selectColor;
            return selectColor;
        } else {
            if (!selectColor.equals(mLastDanmuContentColor)) {
                mLastDanmuContentColor = selectColor;
                return selectColor;
            } else {
                return getTextColor();
            }
        }
    }

    /**
     * 网络图片转换成drawable
     *
     * @param imageUrl
     * @return
     */
    private Drawable loadImageFromNetwork (String imageUrl) {
        Drawable drawable = null;
        try {
            // 可以在这里通过文件名来判断，是否本地有此图片
            drawable = Drawable.createFromStream(
                    new URL(imageUrl).openStream(), "image.jpg");
        } catch (IOException e) {
            Log.d("test", e.getMessage());
        }
        if (drawable == null) {
            Log.d("test", "null drawable");
        } else {
            Log.d("test", "not null drawable");
        }

        return drawable;
    }

    /**
     * 获取网络图片的bitmap
     *
     * @param Url
     * @return
     * @throws Exception
     */
    public Bitmap getImageBitMap (String Url) {
        try {
            URL    url          = new URL(Url);
            String responseCode = url.openConnection().getHeaderField(0);
            //if (responseCode.indexOf("200") < 0)
            //Log.d("test", "图片文件不存在或路径错误，错误代码：" + responseCode);
            return BitmapFactory.decodeStream(url.openStream());
        } catch (IOException e) {
            Log.d("test", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean performClick () {
        return super.performClick();
    }

    /**
     * 记录按下的坐标点（起始点）
     **/
    private float mPosX    = 0;
    private float mPosY    = 0;
    /**
     * 记录移动后抬起坐标点（终点）
     **/
    private float mCurPosX = 0;
    private float mCurPosY = 0;

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:   //点击的开始位置
                mPosX = event.getX();
                mPosY = event.getY();
                mCurPosX = mPosX;
                mCurPosY = mPosY;
                break;
            case MotionEvent.ACTION_MOVE:   //触屏实时位置
                mCurPosX = event.getX();
                mCurPosY = event.getY();
                break;
            case MotionEvent.ACTION_UP:     //离开屏幕的位置
                if (mCurPosX - mPosX < 0 && (Math.abs(mCurPosX - mPosX) > 300)) {
                    //从右往左滑动
                    if (mResponse != null) {
                        setClick(mActivity, mResponse);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL://按钮弹起逻辑
                break;
            default:
                break;
        }
        if (mAdAttr != null) {
            return mAdAttr.isCanLeftTouch();
        } else {
            return false;
        }
    }

    /**
     * 滑动点击监听
     */
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch (View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:   //点击的开始位置
                    mClickX = event.getX();
                    mClickY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:   //触屏实时位置
                    break;
                case MotionEvent.ACTION_UP:     //离开屏幕的位置
                    break;
                case MotionEvent.ACTION_CANCEL://按钮弹起逻辑
                    break;
                default:
                    break;
            }

            if (mGestureDetector != null) {
                return false;
            } else {
                return false;
            }
        }
    };

    /**
     * 手势的监听
     */
    private GestureDetector.OnGestureListener mOnGestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown (MotionEvent e) {
            LogUtils.e("进入了onDown");
            return false;
        }

        @Override
        public void onShowPress (MotionEvent e) {
            LogUtils.e("进入了onShowPress");
        }

        @Override
        public boolean onSingleTapUp (MotionEvent e) {
            LogUtils.e("进入了onSingleTapUp");
            return false;
        }

        /*
         * 屏幕拖动事件，如果按下的时间过长，调用了onLongPress，再拖动屏幕不会触发onScroll。拖动屏幕会多次触发
         * @param e1 开始拖动的第一次按下down操作,也就是第一个ACTION_DOWN
         * @parem e2 触发当前onScroll方法的ACTION_MOVE
         * @param distanceX 当前的x坐标与最后一次触发scroll方法的x坐标的差值。
         * @param diastancY 当前的y坐标与最后一次触发scroll方法的y坐标的差值。
         */
        @Override
        public boolean onScroll (MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            LogUtils.e("进入了onScroll,velocityX=" + distanceX + "|velocityY=" + distanceY);
            return false;
        }

        /*
         * 长按。在down操作之后，过一个特定的时间触发
         */
        @Override
        public void onLongPress (MotionEvent e) {
            LogUtils.e("进入了onLongPress");
        }

        /*
         * 按下屏幕，在屏幕上快速滑动后松开，由一个down,多个move,一个up触发
         * @param e1 开始快速滑动的第一次按下down操作,也就是第一个ACTION_DOWN
         * @parem e2 触发当前onFling方法的move操作,也就是最后一个ACTION_MOVE
         * @param velocityX：X轴上的移动速度，像素/秒
         * @parram velocityY：Y轴上的移动速度，像素/秒
         */
        @Override
        public boolean onFling (MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            LogUtils.e("进入了onFling,velocityX=" + velocityX + "|velocityY=" + velocityY);
            return false;
        }
    };
}
