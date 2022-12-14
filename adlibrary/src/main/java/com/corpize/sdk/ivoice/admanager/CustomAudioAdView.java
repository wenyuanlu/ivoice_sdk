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
 * author ???yh
 * date : 2020-02-16 02:52
 * description :?????????????????????
 */
public class CustomAudioAdView extends FrameLayout implements View.OnClickListener {

    private View        mView;
    private Context     mContext;
    private Activity    mActivity;
    private AdAudioBean mResponse;//???????????????
    private AdAudioBean normalBean;//????????????
    private AdAudioBean chimeBean;//??????????????????
    private AdAttr      mAdAttr;
    private String      mImgAudioUrl = "";

    private boolean mHaveSnake      = false;   //?????????????????????
    private boolean mHaveSnakeClick = false;   //????????????????????????????????????

    private float                   mClickX;
    private float                   mClickY;
    private int                     mPositionX;
    private int                     mPositionY;
    private boolean                 mHaveGetFirstDanmu          = false;//??????????????????????????????????????????
    private boolean                 mHaveSendViewShow           = false;//??????????????????????????????
    private boolean                 mHaveSendAudioShow          = false;//????????????????????????????????????
    private boolean                 mHaveSendClick              = false;//??????????????????????????????
    private boolean                 mHaveSendDeep               = false;//????????????deeplink????????????
    private boolean                 mHaveDownStart              = false;//????????????????????????????????????
    private boolean                 mHaveDownComplete           = false;//????????????????????????????????????
    private boolean                 mHaveDownInstall            = false;//????????????????????????????????????
    private boolean                 mHaveFirstShow              = false;//?????????????????????
    private boolean                 mHaveMusicStartPlay         = false;//????????????????????????????????????
    private boolean                 mHaveMusicMidpointPlay      = false;//????????????????????????????????????
    private boolean                 mHaveMusicFirstQuartilePlay = false;//??????????????????????????????????????????
    private boolean                 mHaveMusicThirdQuartilePlay = false;//??????????????????????????????????????????
    private boolean                 mHaveMusicCompletePlay      = false;//????????????????????????????????????
    private boolean                 mHaveMusicClosePlay         = false;//????????????????????????????????????
    private boolean                 mHaveShowIcon               = false;//????????????icon
    private boolean                 mHaveShowCover              = false;//????????????cover
    private int                     mAllTime                    = 0;//??????????????????????????????????????????
    private boolean                 mHaveShowLeftInfo           = false;//????????????????????????info
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
    private int            mContentAdSize = 10;//?????????????????????
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
    private int                      mPraiseNum             = 0;//?????????????????????
    private int                      mCommentNum            = 0;//?????????????????????
    private int                      mCommentStartTime      = 0;//???????????????????????????,???
    private int                      mCommentIntervalTime   = 10;//???????????????????????????,???
    private int                      mCommentLinenumber     = 10;//?????????????????????
    private boolean                  mInGetComment          = false;//???????????????????????????
    private ImageView                mIvBackground;
    private int                      mBarrageContentSize    = 0;
    private List<String>             mBarrageContentColor   = new ArrayList<>();
    private int                      mBarrageHeight         = 0;
    private int                      mBarrageHeadSize       = 0;
    private String                   mBarrageBackColor;
    private String                   mLastDanmuContentColor = "";//?????????????????????????????????
    private boolean                  mShowBarrageFromSet    = true;//??????????????????,??????????????????
    private int                      mIntervalTime          = 0;//??????????????????????????????
    private int                      callbackType           = 0;//????????????????????????????????????
    private ShakeUtils               mShakeUtils;
    private int                      mPosition;
    private boolean                  isFirstCallBack        = true;//????????????????????????
    private CustomMonitorVolumeUtils customMonitorVolumeUtils;

    private int remindsTime;//????????????

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
        LogUtils.e("????????????????????????onLayout");
        mWidth = getWidth();
        mHeight = getHeight();

        if (!mHaveGetFirstDanmu && mShowBarrageFromSet) {
            //?????????????????????
            int allDanMuHeight = mHeight;
            if (mAdAttr.isSetBarrageMagin()) {
                int danmutop    = DeviceUtil.dip2px(mContext, mAdAttr.getBarrageMaginTop());
                int danmubottom = DeviceUtil.dip2px(mContext, mAdAttr.getBarrageMaginBottom());
                allDanMuHeight = allDanMuHeight - danmutop - danmubottom;
            }
            int danmuHeight  = (int) (mBarrageHeadSize * 1.8);
            int singleHeight = DimensionUtil.dpToPx(mContext, danmuHeight);
            mCommentLinenumber = (allDanMuHeight - singleHeight / 3) / singleHeight;
            LogUtils.e("???????????????2=" + mCommentLinenumber);

            //???????????????????????????
            mHaveGetFirstDanmu = true;
            mCommentList.clear();
            getComment(mAdAttr.getMid(), mResponse.getCreativeid(), 0, mCommentStartTime + mCommentIntervalTime);
        }

        int[] position = new int[2];
        getLocationOnScreen(position);
        mPositionX = position[0]; // view?????? ???????????????????????????x????????????
        mPositionY = position[1]; // view?????? ???????????????????????????y????????????
        int lastLargeHeight = DeviceUtil.dip2px(mContext, 200);
        if (mHeight >= lastLargeHeight) {
            mLastShowLarge = true;
        } else {
            mLastShowLarge = false;
        }

        //????????????info???????????????
        if (!mHaveShowLeftInfo) {
            mHaveShowLeftInfo = true;
            showLeftInfoView();
        }

        //????????????????????????,????????????????????????????????????
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
     * ????????????????????????
     */
    private void initData () {
        mHaveSendViewShow = false;//??????????????????????????????
        mHaveSendClick = false;//??????????????????????????????
        mHaveSendDeep = false;//????????????deeplink????????????
        mHaveDownStart = false;//????????????????????????????????????
        mHaveDownComplete = false;//????????????????????????????????????
        mHaveDownInstall = false;//????????????????????????????????????
        mHaveFirstShow = false;//?????????????????????
        mHaveMusicStartPlay = false;//????????????????????????????????????
        mHaveMusicMidpointPlay = false;//????????????????????????????????????
        mHaveMusicFirstQuartilePlay = false;//??????????????????????????????????????????
        mHaveMusicThirdQuartilePlay = false;//??????????????????????????????????????????
        mHaveMusicCompletePlay = false;//????????????????????????????????????
        mHaveMusicClosePlay = false;//????????????????????????????????????
        mAllTime = 0;//??????????????????????????????????????????
        mHaveShowLeftInfo = false;//????????????????????????info
        mLastShowLarge = true;
    }

    /**
     * ???????????????
     */
    private void initView (final Context context) {
        if (mResponse != null) {//?????????????????????
            mImgAudioUrl = mResponse.getAudiourl();
        }
        mView = LayoutInflater.from(context).inflate(R.layout.qcad_custom_audio_layout, null);//????????????

        /**
         * ???????????????
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

        // ???????????????
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

        //???????????????
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

        //??????????????????????????????
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
        //?????????????????????
        mRlInfoSmall.setOnClickListener(this);

        /**
         * ???????????? ??????????????? ????????????????????????????????????,?????????????????????
         */
        mLogoInfo = mView.findViewById(R.id.qcad_info_small_logo);
        mTitleInfo = mView.findViewById(R.id.qcad_info_small_title);
        mContentInfo = mView.findViewById(R.id.qcad_info_small_content);
        mClickInfo = mView.findViewById(R.id.qcad_info_small_click);
        mCloseInfo = mView.findViewById(R.id.qcad_info_small_close);
        mAdIcon = mView.findViewById(R.id.qcad_info_small_ad);
        ImageUtils.loadImage(mActivity, Constants.AD_ICON, mAdIcon);

        //????????????,?????????????????????
        RelativeLayout rlHead = mView.findViewById(R.id.qcad_head_rl);
        mHeadLogo = mView.findViewById(R.id.qcad_head_logo);
        headLink = mView.findViewById(R.id.qcad_head_link);
        if (mAdAttr.getAdHeadSize() != 0) {
            int adHeadSize = mAdAttr.getAdHeadSize();
            //????????????
            RelativeLayout.LayoutParams headLogoParams = (RelativeLayout.LayoutParams) mHeadLogo.getLayoutParams();
            headLogoParams.width = DeviceUtil.dip2px(context, adHeadSize);
            headLogoParams.height = DeviceUtil.dip2px(context, adHeadSize);
            mHeadLogo.setLayoutParams(headLogoParams);

            //????????????
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
         * ????????????,?????????????????????
         */
        mPraiseImage = mView.findViewById(R.id.qcad_praise_image);
        mPraiseNumber = mView.findViewById(R.id.qcad_praise_number);
        mPraiseImage.setOnClickListener(this);
        mPraiseNumber.setOnClickListener(this);
        priseChooseDrawable = getResources().getDrawable(R.drawable.qcad_icon_praise_choose);
        priseDefaultDrawable = getResources().getDrawable(R.drawable.qcad_icon_praise_default);
        //?????????????????????
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

        //??????????????????
        if (mAdAttr.getPraiseNumberColor() != 0) {
            mPraiseNumber.setTextColor(mAdAttr.getPraiseNumberColor());
        }
        if (mAdAttr.getPraiseNumberSize() != 0) {
            mPraiseNumber.setTextSize(mAdAttr.getPraiseNumberSize());
        }
        RelativeLayout.LayoutParams praiseNumberParams = (RelativeLayout.LayoutParams) mPraiseNumber.getLayoutParams();
        if (mAdAttr.getPraiseNumberWidth() != 0) {
            //????????????
            int praiseImageWidth = mAdAttr.getPraiseNumberWidth();
            praiseNumberParams.width = DeviceUtil.dip2px(context, praiseImageWidth);
            mPraiseNumber.setLayoutParams(praiseNumberParams);
        }

        if (mAdAttr.getPraiseNumberLayout() != null && mAdAttr.getPraiseNumberLayout().size() > 0) {
            //????????????
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
            //Magin??????
            int left   = DeviceUtil.dip2px(context, mAdAttr.getPraiseNumberMaginLeft());
            int top    = DeviceUtil.dip2px(context, mAdAttr.getPraiseNumberMaginTop());
            int right  = DeviceUtil.dip2px(context, mAdAttr.getPraiseNumberMaginRight());
            int bottom = DeviceUtil.dip2px(context, mAdAttr.getPraiseNumberMaginBottom());
            praiseNumberParams.setMargins(left, top, right, bottom);
            mPraiseNumber.setLayoutParams(praiseNumberParams);
        }

        /**
         * ????????????,?????????????????????
         */
        mBarrageImage = mView.findViewById(R.id.qcad_barrage_image);
        mBarrageNumber = mView.findViewById(R.id.qcad_barrage_number);
        mBarrageImage.setOnClickListener(this);
        mBarrageNumber.setOnClickListener(this);
        mShowBarrageFromSet = mAdAttr.isShowBarrage();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
            mShowBarrageFromSet = false;//????????????,?????????????????????,???????????????????????????5.0????????????????????????
        }
        //?????????????????????
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

        //?????????????????????
        if (mAdAttr.getBarrageNumberColor() != 0) {
            mBarrageNumber.setTextColor(mAdAttr.getBarrageNumberColor());
        }
        if (mAdAttr.getBarrageNumberSize() != 0) {
            mBarrageNumber.setTextSize(mAdAttr.getBarrageNumberSize());
        }
        RelativeLayout.LayoutParams barrageNumberParams = (RelativeLayout.LayoutParams) mBarrageNumber.getLayoutParams();
        if (mAdAttr.getBarrageNumberWidth() != 0) {
            //????????????
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
         * ?????????????????????
         */
        //???????????????????????????????????????
        mCoverImage = mView.findViewById(R.id.qcad_cover_image);
        RelativeLayout              musicRl          = mView.findViewById(R.id.qcad_music_rl);
        RelativeLayout.LayoutParams coverImageParams = (RelativeLayout.LayoutParams) mCoverImage.getLayoutParams();
        RelativeLayout.LayoutParams musicRlParams    = (RelativeLayout.LayoutParams) musicRl.getLayoutParams();
        if (mAdAttr.getCoverSize() != 0) {
            int coverImageSize = mAdAttr.getCoverSize();
            //????????????
            coverImageParams.width = DeviceUtil.dip2px(context, coverImageSize);
            coverImageParams.height = DeviceUtil.dip2px(context, coverImageSize);
            mCoverImage.setLayoutParams(coverImageParams);

            //???????????????????????????
            musicRlParams.width = DeviceUtil.dip2px(context, coverImageSize);
            musicRlParams.height = DeviceUtil.dip2px(context, coverImageSize);
            musicRl.setLayoutParams(musicRlParams);
        }

        if (mAdAttr.getCoverType() == CoverType.OVAL) {
            mCoverImage.setRadius(20);
        }

        if (mAdAttr.getCoverLayout() != null && mAdAttr.getCoverLayout().size() > 0) {
            //???????????? ???????????????????????????
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
            //????????????
            coverImageParams.setMargins(left, top, right, bottom);
            mCoverImage.setLayoutParams(coverImageParams);

            //???????????????????????????
            musicRlParams.setMargins(left, top, right, bottom);
            musicRl.setLayoutParams(musicRlParams);
        }

        /**
         * ????????????????????????
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
            //????????????????????????
            musicBtParams.width = DeviceUtil.dip2px(context, musicBtSize);
            musicBtParams.height = DeviceUtil.dip2px(context, musicBtSize);
            mMusicBt.setLayoutParams(musicBtParams);
            //???????????????,?????????????????????
            if (!mAdAttr.isToCover()) {
                //?????????????????????????????????
                musicRlParams.width = DeviceUtil.dip2px(context, musicBtSize);
                musicRlParams.height = DeviceUtil.dip2px(context, musicBtSize);
                musicRl.setLayoutParams(musicRlParams);
            }
        }

        if (mAdAttr.getMusicBtLayout() != null && mAdAttr.getMusicBtLayout().size() > 0) {
            //????????????????????????
            if (mAdAttr.isToCover()) {
                //????????????,????????????????????????
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
                //???????????????,?????????????????????,?????????????????????
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
                //????????????,????????????????????????
                musicBtParams.setMargins(left, top, right, bottom);
                mMusicBt.setLayoutParams(musicBtParams);
            } else {
                //???????????????,?????????????????????,?????????????????????
                musicRlParams.setMargins(left, top, right, bottom);
                musicRl.setLayoutParams(musicRlParams);
            }
        }

        if (TextUtils.isEmpty(mImgAudioUrl)) {//?????????,?????????????????????
            mMusicBt.setVisibility(View.GONE);
        } else {
            mMusicBt.setOnClickListener(this);
        }

        /**
         * ???????????????????????????(???)
         */
        mLastLargeRl = mView.findViewById(R.id.qcad_last_large_rl);
        mLastLargeLogo = mView.findViewById(R.id.qcad_last_large_logo);
        mLastLargeTitle = mView.findViewById(R.id.qcad_last_large_title);
        mLastLargeContent = mView.findViewById(R.id.qcad_last_large_content);
        mLastLargeDetails = mView.findViewById(R.id.qcad_last_large_details);
        mLastLargeClick = mView.findViewById(R.id.qcad_last_large_click);
        mLastLargeDetails.setOnClickListener(this);
        mLastLargeClick.setOnClickListener(this);
        mLastLargeRl.setOnClickListener(this);//????????????????????????????????????????????????
        mLastLargeDetails.setBackground(getRoundRectDrawable(10, Color.parseColor("#FFFFFFFF"), true, 10));

        /**
         * ???????????????????????????(???)
         */
        mLastSmallRl = mView.findViewById(R.id.qcad_last_small_rl);
        mLastSmallLl = mView.findViewById(R.id.qcad_last_small_ll);
        mLastSmallLogo = mView.findViewById(R.id.qcad_last_small_logo);
        mLastSmallTitle = mView.findViewById(R.id.qcad_last_small_title);
        mLastSmallContent = mView.findViewById(R.id.qcad_last_small_content);
        mLastSmallClick = mView.findViewById(R.id.qcad_last_small_click);
        mLastSmallLl.setOnClickListener(this);

        /**
         * ??????
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
         * ?????????????????????
         */
        mDanMuView = mView.findViewById(R.id.qcad_danmu_view);
        if (mShowBarrageFromSet) {
            //????????????
            mBarrageContentSize = mAdAttr.getBarrageContentSize();
            if (mBarrageContentSize == 0) {
                mBarrageContentSize = 14;
            }
            //????????????
            mBarrageHeadSize = mAdAttr.getBarrageHeadSize();
            if (mBarrageHeadSize <= 0) {
                mBarrageHeadSize = mBarrageContentSize + 6;
            } else if (mBarrageHeadSize < mBarrageContentSize) {
                mBarrageHeadSize = mBarrageContentSize + 2;
            }

            //????????????
            List<String> barrageContentColor = mAdAttr.getBarrageContentColor();
            if (barrageContentColor != null && barrageContentColor.size() > 0) {
                mBarrageContentColor.addAll(barrageContentColor);
            } else {
                mBarrageContentColor.add("#CE608C");
                mBarrageContentColor.add("#BC7D2F");
                mBarrageContentColor.add("#5C72D5");
                mBarrageContentColor.add("#23A69E");
            }

            //????????????
            mBarrageBackColor = mAdAttr.getBarrageBackColor();
            if (TextUtils.isEmpty(mBarrageBackColor)) {
                mBarrageBackColor = "#3F000000";
            }
            //??????
            RelativeLayout.LayoutParams danmuParams = (RelativeLayout.LayoutParams) mDanMuView.getLayoutParams();
            if (mAdAttr.isSetBarrageMagin()) {
                int left   = DeviceUtil.dip2px(context, mAdAttr.getBarrageMaginLeft());
                int top    = DeviceUtil.dip2px(context, mAdAttr.getBarrageMaginTop());
                int right  = DeviceUtil.dip2px(context, mAdAttr.getBarrageMaginRight());
                int bottom = DeviceUtil.dip2px(context, mAdAttr.getBarrageMaginBottom());
                //????????????
                danmuParams.setMargins(left, top, right, bottom);
                mDanMuView.setLayoutParams(danmuParams);
            }

            //????????????
            mDanMuView.prepare();
        } else {
            mDanMuView.setVisibility(GONE);
        }

        mView.setOnTouchListener(mOnTouchListener);
        //?????????????????????
        mGestureDetector = new GestureDetector(mOnGestureListener);

        //????????????
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
     * ????????????????????????????????????
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
     * ???????????? ??????????????? ????????????????????????????????????,?????????????????????
     */
    private void showLeftInfoView () {
        int infoWidth  = DeviceUtil.dip2px(mContext, 315);
        int infoHeight = DeviceUtil.dip2px(mContext, 90);
        //??????????????????info?????????,??????????????????info???????????????,?????????info??????
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
            //??????????????????????????? ??????????????????????????????????????????
            if (mAdAttr.getInfoButtonBackgroundColor() != 0) {
                mClickInfo.setBackground(getRoundRectDrawable(50, mAdAttr.getInfoButtonBackgroundColor(), true, 10));
                mLastSmallClick.setBackground(getRoundRectDrawable(50, mAdAttr.getInfoButtonBackgroundColor(), true, 10));
                mLastLargeClick.setBackground(getRoundRectDrawable(50, mAdAttr.getInfoButtonBackgroundColor(), true, 10));

            } else {
                mClickInfo.setBackground(getRoundRectDrawable(50, Color.parseColor("#FFFF42A1"), true, 10));
            }

            //?????????????????????
            mCloseInfo.setOnClickListener(this);

        } else {
            mRlInfoSmall.setVisibility(GONE);
        }
    }

    /**
     * ????????????
     */
    public void render () {
        setData();
        if (mView != null) {
            addView(mView);
        }
    }

    /**
     * ???????????????
     */
    private void setData () {
        if (mResponse != null) {
            //?????????????????????
            mImgAudioUrl = mResponse.getAudiourl();
            String backdrop = mResponse.getCompanion() != null ? mResponse.getCompanion().getUrl() : "";
            mBarrageNumber.setText(String.valueOf(mCommentNum));
            mPraiseNumber.setText(String.valueOf(mPraiseNum));
            if (SpUtils.getBoolean(mResponse.getCreativeid())) {
                mPraiseImage.setImageDrawable(priseChooseDrawable);
            } else {
                mPraiseImage.setImageDrawable(priseDefaultDrawable);
            }

            //????????????
            ImageUtils.loadImage(mActivity, mResponse.getFirstimg(), mCoverImage);
            //?????????????????????
            ImageUtils.loadImage(mActivity, backdrop, mIvBackground);
            String title  = mResponse.getTitle();
            String desc   = mResponse.getDesc();
            int    action = mResponse.getAction();

            //?????????????????????
            if (null != mResponse.getIcon() && !TextUtils.isEmpty(mResponse.getIcon().getUrl())) {
                //?????????LOGO??????
                String mIcon = mResponse.getIcon().getUrl();
                mLogoInfo.setVisibility(VISIBLE);
                ImageUtils.loadImage(mActivity, mIcon, mLogoInfo);
                ImageUtils.loadImage(mActivity, mIcon, mHeadLogo);
                ImageUtils.loadImage(mActivity, mIcon, mLastLargeLogo);
                ImageUtils.loadImage(mActivity, mIcon, mLastSmallLogo);
                LogUtils.e("init???onLayout????????????icon=" + mIcon);
            } else {
                mLogoInfo.setVisibility(GONE);
                LogUtils.e("init???onLayout????????????????????????");
            }

            //?????????????????????
            if (!TextUtils.isEmpty(title)) {
                mTvTitle.setText(title);
                mTitleInfo.setText(title);
                mLastLargeTitle.setText(title);
                mLastSmallTitle.setText(title);
            } else {
                mLastLargeTitle.setVisibility(View.GONE);
                mLastSmallTitle.setVisibility(View.GONE);
            }

            //?????????????????????
            if (!TextUtils.isEmpty(desc)) {
                //??????????????????????????????
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

            //??????????????????????????????????????????????????????
            mLastSmallClick.setVisibility(VISIBLE);
            mLastLargeClick.setVisibility(VISIBLE);
            mClickInfo.setVisibility(VISIBLE);
            mLastLargeDetails.setVisibility(VISIBLE);

            //??????action?????????????????????????????????
            if (action == 6) {//??????
                mLastSmallClick.setText("????????????");
                mLastLargeClick.setText("????????????");
                mClickInfo.setText("????????????");
            } else if (action == 1 || action == 2) {
                mLastSmallClick.setText("????????????");
                mLastLargeClick.setText("????????????");
                mClickInfo.setText("????????????");
            } else if (action == 7) {
                mLastSmallClick.setText("????????????");
                mLastLargeClick.setText("????????????");
                mClickInfo.setText("????????????");
            } else if (action == 3) {
                mLastSmallClick.setText("????????????");
                mLastLargeClick.setText("????????????");
                mClickInfo.setText("????????????");
            } else if (action == 8) {
                mLastSmallClick.setText("????????????");
                mLastLargeClick.setText("????????????");
                mClickInfo.setText("????????????");
            } else if (action == 0) {
                mLastSmallClick.setVisibility(GONE);
                mLastLargeClick.setVisibility(GONE);
                mClickInfo.setVisibility(GONE);
                mLastLargeDetails.setVisibility(GONE);
            }
        }
    }

    /**
     * ????????????
     */
    @Override
    public void onClick (View view) {
        int id = view.getId();
        if (id == R.id.qcad_last_large_details || id == R.id.qcad_last_large_click
                || id == R.id.rl_qcad_info_small || id == R.id.qcad_head_rl
                || id == R.id.qcad_head_logo || id == R.id.qcad_last_small_ll
                || id == R.id.qcad_custom_title || id == R.id.qcad_custom_content) {
            //???????????????????????????????????????,?????????????????????,???????????????
            if (null != mResponse) {
                setClick(mActivity, mResponse);
            }
        } else if (id == R.id.qcad_info_small_close) {
            //???????????????????????????????????????
            mRlInfoSmall.setVisibility(GONE);
        } else if (id == R.id.qcad_praise_image || id == R.id.qcad_praise_number) {
            try {
                //????????????,?????????????????????
                if (!SpUtils.getBoolean(mResponse.getCreativeid())) {
                    QcHttpUtil.upPraise(mAdAttr.getMid(), mResponse.getCreativeid(), new QcHttpUtil.QcHttpOnListener<String>() {
                        @Override
                        public void OnQcCompletionListener (String response) {
                            LogUtils.e("??????????????????code=200");
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
            //????????????,?????????????????????
            //????????????
            MediaPlayerUtil.getInstance().pausePlay();
            mMusicBt.setImageDrawable(musicPlayDrawable);
            if (mDanMuView != null && mShowBarrageFromSet) {//????????????
                mDanMuView.pauseAllDanMuView();
            }
            //????????????
            DialogUtils.showEditDialog(mActivity, new EditDialogCallback() {
                @Override
                public void sendMsg (final String content) {
                    if (TextUtils.isEmpty(content)) {
                        Toast.makeText(mContext, "????????????????????????", Toast.LENGTH_SHORT).show();
                    } else {
                        //????????????
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
                                        LogUtils.e("??????????????????code=200");
                                        //?????????????????????
                                        if (mDanMuView != null && mShowBarrageFromSet) {//????????????
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
                    if (mDanMuView != null && mShowBarrageFromSet) {//????????????
                        mDanMuView.continueAllDanMuView();
                    }
                }
            });
        } else if (id == R.id.qcad_music_bt) {
            //?????????????????????
            if (MediaPlayerUtil.getInstance().currentPlayStatue == MediaPlayerUtil.getInstance().STOP) {
                //???????????????,???????????????,?????????????????????
                playAd();
                mMusicBt.setImageDrawable(musicPauseDrawable);

                MediaPlayerUtil.getInstance().setUserClickStop(0);
            } else if (MediaPlayerUtil.getInstance().currentPlayStatue == MediaPlayerUtil.getInstance().PAUSE) {
                //???????????????,???????????????,?????????????????????
                MediaPlayerUtil.getInstance().resumePlay();
                mMusicBt.setImageDrawable(musicPauseDrawable);
                if (mDanMuView != null && mShowBarrageFromSet) {//????????????
                    mDanMuView.continueAllDanMuView();
                }
                MediaPlayerUtil.getInstance().setUserClickStop(0);

                skipOnResume();
            } else if (MediaPlayerUtil.getInstance().currentPlayStatue == MediaPlayerUtil.getInstance().PLAY) {
                //???????????????,???????????????,??????????????????
                MediaPlayerUtil.getInstance().pausePlay();
                mMusicBt.setImageDrawable(musicPlayDrawable);
                if (mDanMuView != null && mShowBarrageFromSet) {//????????????
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
     * ??????????????????
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
     * ??????????????????????????????
     *
     * @param radius
     * @param color
     * @param isFill
     * @param strokeWidth
     * @return
     */
    public static GradientDrawable getRoundRectDrawable (int radius, int color, boolean isFill, int strokeWidth) {
        //????????????????????????????????????????????????
        float[]          radiuss  = {radius, radius, radius, radius, radius, radius, radius, radius};
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadii(radiuss);
        drawable.setColor(isFill ? color : Color.TRANSPARENT);
        drawable.setStroke(isFill ? 0 : strokeWidth, color);
        return drawable;
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
                    MediaPlayerUtil.getInstance().playVoiceList(mContext, voiceList, mMediaMoreListener);
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
     * ????????????
     */
    private int checkNeedPermissions () {
        return PermissionUtil.checkAudioAndWritePermissions(mContext);
    }

    /**
     * ?????????????????????
     */
    private void initRecorderOperation () {
        customMonitorVolumeUtils = new CustomMonitorVolumeUtils(mContext);
        VoiceInteractiveUtil.getInstance().initRecorderOperation(mActivity, customMonitorVolumeUtils, new OnVolumeEndListener() {
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
        VoiceInteractiveUtil.getInstance().setRecordListener(mActivity,
                new QcHttpUtil.QcHttpOnListener<UpVoiceResultBean>() {
                    @Override
                    public void OnQcCompletionListener (UpVoiceResultBean response) {
                        //1:?????????0:?????????999: ????????????
                        if (UpVoiceResultBean.FAIl == response.getCode()) {
                            // ?????????????????????
                            onAdCompleteCallBack();
                        } else if (UpVoiceResultBean.SUCCESS == response.getCode()) {
                            //???????????????????????????
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
            //????????????
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
                    mMusicBt.setImageDrawable(musicPauseDrawable);
                    if (mDanMuView != null && mShowBarrageFromSet) {//????????????
                        mDanMuView.continueAllDanMuView();
                    }
                } else {
                    if (MediaPlayerUtil.getInstance().currentPlayStatue == MediaPlayerUtil.getInstance().PLAY) {
                        //???????????????,???????????????,??????????????????
                        MediaPlayerUtil.getInstance().pausePlay();
                        mMusicBt.setImageDrawable(musicPlayDrawable);
                        if (mDanMuView != null && mShowBarrageFromSet) {//????????????
                            mDanMuView.pauseAllDanMuView();
                        }
                    }
                }
            }
        });
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
                                    mMusicBt.setImageDrawable(musicPauseDrawable);
                                    if (mDanMuView != null && mShowBarrageFromSet) {//????????????
                                        mDanMuView.continueAllDanMuView();
                                    }
                                } else {
                                    if (MediaPlayerUtil.getInstance().currentPlayStatue == MediaPlayerUtil.getInstance().PLAY) {
                                        //???????????????,???????????????,??????????????????
                                        MediaPlayerUtil.getInstance().pausePlay();
                                        mMusicBt.setImageDrawable(musicPlayDrawable);
                                        if (mDanMuView != null && mShowBarrageFromSet) {//????????????
                                            mDanMuView.pauseAllDanMuView();
                                        }
                                    }
                                }
                            }
                        });
            } else {
                //????????????????????????,????????????,??????????????????,??????????????????,????????????
                if (mDanMuView != null) {
                    mDanMuView.setVisibility(GONE);
                    mDanMuView.release();
                }
                mBarrageImage.setVisibility(GONE);
                mBarrageNumber.setVisibility(GONE);
                mMusicBt.setVisibility(GONE);
            }
        } else {
            //????????????????????????,????????????,??????????????????,??????????????????,????????????
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
     * ?????????????????????
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
     * ????????????,????????????
     */
    public void pauseMusicAndDanMu () {
        if (mAdAttr != null && mAdAttr.isCanPause()) {
            closeCommonHandler();
            MediaPlayerUtil.getInstance().pausePlay();
            mMusicBt.setImageDrawable(musicPlayDrawable);
            if (mDanMuView != null && mShowBarrageFromSet) {//????????????
                mDanMuView.pauseAllDanMuView();
            }
        }
    }

    /**
     * ????????????,????????????
     */
    public void resumeMusicAndDanMu () {
        if (mAdAttr != null && mAdAttr.isCanPause()) {
            MediaPlayerUtil.getInstance().resumePlay();
            mMusicBt.setImageDrawable(musicPauseDrawable);
            if (mDanMuView != null && mShowBarrageFromSet) {//????????????
                mDanMuView.continueAllDanMuView();
            }
        }
    }

    /**
     * ???????????????????????????
     */
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == mPosition) {
            //??????????????????????????????????????????????????????
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
    }

    /**
     * ??????????????????????????????
     * ???????????????
     * true???view????????????Touch??????
     * false???view????????????Touch????????????????????????false?????????????????????????????????????????????????????????????????????
     */
    @SuppressLint("ClickableViewAccessibility")
    public void getClickXYPosition (View view) {
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch (View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:   //?????????????????????
                        mClickX = event.getX();
                        mClickY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:   //??????????????????
                        break;
                    case MotionEvent.ACTION_UP:     //?????????????????????
                        break;
                    default:
                        break;
                }
                return false;
            }

        });
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
     * ?????????????????????(????????????)
     */
    public void sendClickExposure (final List<String> list) {
        if (!mHaveSendClick) {
            mHaveSendClick = true;
            if (list != null && !list.isEmpty()) {
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
                    if (url.contains("__POSITION_X__")) {//??????X????????????
                        url = url.replace("__POSITION_X__", mPositionX + "");
                    }
                    if (url.contains("__POSITION_Y__")) {//??????Y????????????
                        url = url.replace("__POSITION_Y__", mPositionY + "");
                    }
                    if (url.contains("__WIDTH__")) {//????????????
                        url = url.replace("__WIDTH__", mWidth + "");
                    }
                    if (url.contains("__HEIGHT__")) {//????????????
                        url = url.replace("__HEIGHT__", mHeight + "");
                    }
                    if (url.contains("__TIME_STAMP__")) {//??????????????????
                        url = url.replace("__TIME_STAMP__", time + "");
                    }

                    QcHttpUtil.sendAdExposure(url);
                }
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

    /**
     * ????????????
     */
    public void playAd () {
        int[] position = new int[2];
        getLocationOnScreen(position);
        mPositionX = position[0]; // view?????? ???????????????????????????x????????????
        mPositionY = position[1]; // view?????? ???????????????????????????y????????????
        LogUtils.e("???????????????????????????2X=" + mPositionX + "|Y=" + mPositionY);
        if (mResponse != null) {
            //????????????
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

            //????????????
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

            //????????????
            int perMis = checkNeedPermissions();

            //?????????????????????????????????
            if (!TextUtils.isEmpty(mResponse.getAudiourl())) {
                managerAdMusic(mResponse.getAudiourl(), mResponse, perMis);
            } else {
                managerAdWithOutMusic(mResponse, perMis);
            }
        }
    }

    /**
     * ?????????????????????
     */
    private void setChime () {
        if (chimeBean != null && chimeBean.getSection() != null) {
            long currentTime = System.currentTimeMillis();
            //??????????????????????????????????????????
            if (currentTime < chimeBean.getSection().getS() && mResponse != normalBean) {
                mResponse = normalBean;
                setData();
                //??????????????????????????????????????????
            } else if (currentTime > chimeBean.getSection().getE() && mResponse != normalBean) {
                chimeBean = null;
                mResponse = normalBean;
                setData();
            } else {
                //????????????????????????
                if (mResponse != chimeBean) {
                    mResponse = chimeBean;
                    setData();
                }
            }
        }
    }

    /**
     * ????????????????????????
     */
    private void initShakeUtils () {
        //?????????
        if (mResponse != null) {
            mShakeUtils = new ShakeUtils(mContext);
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
     * ??????????????????
     */
    private void resumePlayAd () {
        MediaPlayerUtil.getInstance().resumePlay();
        mMusicBt.setImageDrawable(musicPauseDrawable);
    }

    /**
     * ??????????????????
     */
    public void stopAd () {
        MediaPlayerUtil.getInstance().stopPlay();
        mMusicBt.setImageDrawable(musicPlayDrawable);
    }

    /**
     * ???????????? ?????????????????? ??????????????? ????????????close???????????????
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
     * ??????????????????
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
                //?????????????????????
                managerDanMu(currentTime);
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
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run () {
                        //????????????
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
                mMusicBt.setImageDrawable(musicPauseDrawable);
                if (mDanMuView != null && mShowBarrageFromSet) {//????????????
                    mDanMuView.continueAllDanMuView();
                }
            } else {
                if (MediaPlayerUtil.getInstance().currentPlayStatue == MediaPlayerUtil.getInstance().PLAY) {
                    //???????????????,???????????????,??????????????????
                    MediaPlayerUtil.getInstance().pausePlay();
                    mMusicBt.setImageDrawable(musicPlayDrawable);
                    if (mDanMuView != null && mShowBarrageFromSet) {//????????????
                        mDanMuView.pauseAllDanMuView();
                    }
                }
            }
        }
    };

    /**
     * ?????????????????????
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
            //?????????????????????
            managerDanMu(currentTime);
        }

        @Override
        public void onPlayCompletionListener () {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run () {
                    //????????????
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
                mMusicBt.setImageDrawable(musicPauseDrawable);
                if (mDanMuView != null && mShowBarrageFromSet) {//????????????
                    mDanMuView.continueAllDanMuView();
                }
            } else {
                if (MediaPlayerUtil.getInstance().currentPlayStatue == MediaPlayerUtil.getInstance().PLAY) {
                    //???????????????,???????????????,??????????????????
                    MediaPlayerUtil.getInstance().pausePlay();
                    mMusicBt.setImageDrawable(musicPlayDrawable);
                    if (mDanMuView != null && mShowBarrageFromSet) {//????????????
                        mDanMuView.pauseAllDanMuView();
                    }
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
        LogUtils.e("qcsdk,clearShake========");
        if (mShakeUtils != null) {
            mShakeUtils.onPause();
            mShakeUtils.clear();
            mShakeUtils = null;
        }
    }

    /**
     * ????????????
     *
     * @param content
     * @param userId
     * @param avater
     * @param isOneself ??????????????????????????????
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
        // ???????????????
        danMuView.avatar = BitmapFactory.decodeResource(getResources(), R.drawable.qcad_barrage_head);
        danMuView.avatarWidth = DimensionUtil.dpToPx(mActivity, avatarSize);
        danMuView.avatarHeight = DimensionUtil.dpToPx(mActivity, avatarSize);

        // ?????????????????????
        danMuView.textSize = DimensionUtil.spToPx(mActivity, textSize);
        danMuView.textMarginLeft = DimensionUtil.dpToPx(mActivity, 4);
        danMuView.text = content;

        danMuView.textColor = Color.parseColor(textColor);

        // ??????????????????
        GradientDrawable drawable = null;
        drawable = (GradientDrawable) getContext().getResources().getDrawable(R.drawable.qcad_shape_barrage);
        drawable.setColor(Color.parseColor(mBarrageBackColor));
        drawable.setStroke(1, Color.parseColor(textColor));
        danMuView.textBackground = drawable;
        danMuView.textBackgroundMarginLeft = DimensionUtil.dpToPx(mActivity, 14);
        danMuView.textBackgroundPaddingTop = DimensionUtil.dpToPx(mActivity, 3);
        danMuView.textBackgroundPaddingBottom = DimensionUtil.dpToPx(mActivity, 3);
        danMuView.textBackgroundPaddingRight = DimensionUtil.dpToPx(mActivity, 14);

        //????????????
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
                                    LogUtils.e("????????????????????????");
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

        //????????????????????? ,???????????????????????????,????????????????????????
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
     * ?????????????????????
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
     * ?????????????????????drawable
     *
     * @param imageUrl
     * @return
     */
    private Drawable loadImageFromNetwork (String imageUrl) {
        Drawable drawable = null;
        try {
            // ??????????????????????????????????????????????????????????????????
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
     * ?????????????????????bitmap
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
            //Log.d("test", "??????????????????????????????????????????????????????" + responseCode);
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
     * ???????????????????????????????????????
     **/
    private float mPosX    = 0;
    private float mPosY    = 0;
    /**
     * ??????????????????????????????????????????
     **/
    private float mCurPosX = 0;
    private float mCurPosY = 0;

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:   //?????????????????????
                mPosX = event.getX();
                mPosY = event.getY();
                mCurPosX = mPosX;
                mCurPosY = mPosY;
                break;
            case MotionEvent.ACTION_MOVE:   //??????????????????
                mCurPosX = event.getX();
                mCurPosY = event.getY();
                break;
            case MotionEvent.ACTION_UP:     //?????????????????????
                if (mCurPosX - mPosX < 0 && (Math.abs(mCurPosX - mPosX) > 300)) {
                    //??????????????????
                    if (mResponse != null) {
                        setClick(mActivity, mResponse);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL://??????????????????
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
     * ??????????????????
     */
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch (View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:   //?????????????????????
                    mClickX = event.getX();
                    mClickY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:   //??????????????????
                    break;
                case MotionEvent.ACTION_UP:     //?????????????????????
                    break;
                case MotionEvent.ACTION_CANCEL://??????????????????
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
     * ???????????????
     */
    private GestureDetector.OnGestureListener mOnGestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown (MotionEvent e) {
            LogUtils.e("?????????onDown");
            return false;
        }

        @Override
        public void onShowPress (MotionEvent e) {
            LogUtils.e("?????????onShowPress");
        }

        @Override
        public boolean onSingleTapUp (MotionEvent e) {
            LogUtils.e("?????????onSingleTapUp");
            return false;
        }

        /*
         * ????????????????????????????????????????????????????????????onLongPress??????????????????????????????onScroll??????????????????????????????
         * @param e1 ??????????????????????????????down??????,??????????????????ACTION_DOWN
         * @parem e2 ????????????onScroll?????????ACTION_MOVE
         * @param distanceX ?????????x???????????????????????????scroll?????????x??????????????????
         * @param diastancY ?????????y???????????????????????????scroll?????????y??????????????????
         */
        @Override
        public boolean onScroll (MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            LogUtils.e("?????????onScroll,velocityX=" + distanceX + "|velocityY=" + distanceY);
            return false;
        }

        /*
         * ????????????down?????????????????????????????????????????????
         */
        @Override
        public void onLongPress (MotionEvent e) {
            LogUtils.e("?????????onLongPress");
        }

        /*
         * ????????????????????????????????????????????????????????????down,??????move,??????up??????
         * @param e1 ????????????????????????????????????down??????,??????????????????ACTION_DOWN
         * @parem e2 ????????????onFling?????????move??????,?????????????????????ACTION_MOVE
         * @param velocityX???X??????????????????????????????/???
         * @parram velocityY???Y??????????????????????????????/???
         */
        @Override
        public boolean onFling (MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            LogUtils.e("?????????onFling,velocityX=" + velocityX + "|velocityY=" + velocityY);
            return false;
        }
    };
}
