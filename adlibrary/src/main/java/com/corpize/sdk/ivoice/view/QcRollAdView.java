package com.corpize.sdk.ivoice.view;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.corpize.sdk.R;
import com.corpize.sdk.ivoice.AdRollAttr;
import com.corpize.sdk.ivoice.bean.response.AdAudioBean;
import com.corpize.sdk.ivoice.common.CommonHandler;
import com.corpize.sdk.ivoice.dialog.CustomAdShowDialog;
import com.corpize.sdk.ivoice.listener.QcRollAdViewListener;
import com.corpize.sdk.ivoice.utils.DeviceUtil;
import com.corpize.sdk.ivoice.utils.ImageUtils;
import com.corpize.sdk.ivoice.utils.LogUtils;
import com.corpize.sdk.ivoice.utils.QcHttpUtil;
import com.corpize.sdk.ivoice.utils.ShakeUtils;

import java.util.List;

/**
 * author : xpSun
 * date : 7/9/21
 * description :
 */
public class QcRollAdView extends RelativeLayout {

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

    private View           rootView;
    private RelativeLayout itemLayout;
    private ImageView      rollIvShow;
    private TextView       titleShow;
    private TextView       descShow;
    private ImageView      closeShow;

    private ShakeUtils           mShakeUtils;
    private QcRollAdViewListener mListener;

    /**
     * 广告播完回调逻辑处理
     */
    private CommonHandler mHandler;

    private AdAudioBean        mResponse;
    private Activity           mActivity;
    private CustomAdShowDialog dialog;
    private AdRollAttr         adRollAttr;

    public void setAdAudioBean (AdAudioBean mResponse) {
        this.mResponse = mResponse;
        initView();
    }

    public void setQcRollAdViewListener (QcRollAdViewListener qcRollAdViewListener) {
        this.mListener = qcRollAdViewListener;
    }

    public QcRollAdView (Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QcRollAdView (Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public QcRollAdView (Activity activity) {
        this(activity, null);
        this.mActivity = activity;
    }

    private void init () {
        initWidgets();
        initWidgetsEvent();
    }

    private void initWidgets () {
        rootView = LayoutInflater.from(getContext())
                .inflate(R.layout.view_roll_ad_layout, this, false);
        itemLayout = rootView.findViewById(R.id.roll_ad_item_layout);
        rollIvShow = rootView.findViewById(R.id.roll_ad_iv_show);
        titleShow = rootView.findViewById(R.id.roll_ad_title_show);
        descShow = rootView.findViewById(R.id.roll_ad_desc_show);
        closeShow = rootView.findViewById(R.id.roll_ad_close_show);
        addView(rootView);
    }

    private void initWidgetsEvent () {
        closeShow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick (View v) {
                removeView();

                if (mListener != null) {
                    mListener.onRollAdClickClose();
                }
            }
        });

        itemLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick (View v) {
                playAd();
            }
        });
    }

    public void playAd () {
        if (mActivity != null) {
            dialog = CustomAdShowDialog.Builder(mActivity);
            dialog.setAdAudioBean(mResponse);
            dialog.showDialog();
            dialog.setListener(mListener);

            if (mListener != null) {
                mListener.onRollAdDialogShow();
            }
        }
    }

    public void pause () {
        if (dialog != null) {
            dialog.pause();
        }
    }

    public void destroy () {
        if (dialog != null) {
            dialog.destroy();
        }
        removeView();
    }

    private void removeView () {
        try {
            ViewGroup viewGroup = (ViewGroup) getParent();

            if (null != viewGroup) {
                viewGroup.removeAllViews();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView () {
        if (null == mResponse) {
            return;
        }

        String title = mResponse.getTitle();
        String desc  = mResponse.getDesc();

        if (!TextUtils.isEmpty(mResponse.getFirstimg())) {
            //封面展示
            ImageUtils.loadImage(mActivity, mResponse.getFirstimg(), rollIvShow);
        }

        titleShow.setText(TextUtils.isEmpty(title) ? "" : title);
        descShow.setText(TextUtils.isEmpty(desc) ? "" : desc);
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
            }
        }
    }

    public void setAdRollAttr (AdRollAttr adRollAttr) {
        this.adRollAttr = adRollAttr;
        initAdAttr();
    }

    private void initAdAttr () {
        if (null == adRollAttr) {
            return;
        }

        initRollStyle();
        initLeftImageStyle();
        initTitleStyle();
        initDescStyle();
        initRightBottomStyle();
    }

    private void initRollStyle () {
        ViewGroup.LayoutParams layoutParams = rootView.getLayoutParams();
        AdRollAttr             mAdRollAttr  = adRollAttr;

        if (0 != mAdRollAttr.getAdWidth()) {
            layoutParams.width = DeviceUtil.dip2px(mActivity, mAdRollAttr.getAdWidth());
        }

        if (0 != mAdRollAttr.getAdHeight()) {
            layoutParams.height = DeviceUtil.dip2px(mActivity, mAdRollAttr.getAdHeight());
        }
        rootView.setLayoutParams(layoutParams);

        if (0 != mAdRollAttr.getBackgroundColor()) {
            rootView.setBackgroundColor(mAdRollAttr.getBackgroundColor());
        }
    }

    private void initLeftImageStyle () {
        ViewGroup.LayoutParams leftImageParams = rollIvShow.getLayoutParams();
        AdRollAttr.ImageStyle  imageStyle      = adRollAttr.getLeftImageStyle();

        if (null == imageStyle) {
            return;
        }

        if (leftImageParams instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams = (LayoutParams) leftImageParams;
            if (0 != imageStyle.getWidth()) {
                layoutParams.width = DeviceUtil.dip2px(mActivity, imageStyle.getWidth());
            }

            if (0 != imageStyle.getHeight()) {
                layoutParams.height = DeviceUtil.dip2px(mActivity, imageStyle.getHeight());
            }

            if (0 != imageStyle.getMarginLeft()) {
                layoutParams.leftMargin = DeviceUtil.dip2px(mActivity, imageStyle.getMarginLeft());
            }

            if (0 != imageStyle.getMarginTop()) {
                layoutParams.topMargin = DeviceUtil.dip2px(mActivity, imageStyle.getMarginTop());
            }

            if (0 != imageStyle.getMarginRight()) {
                layoutParams.rightMargin = DeviceUtil.dip2px(mActivity, imageStyle.getMarginRight());
            }

            if (0 != imageStyle.getMarginBottom()) {
                layoutParams.bottomMargin = DeviceUtil.dip2px(mActivity, imageStyle.getMarginBottom());
            }
            rollIvShow.setLayoutParams(layoutParams);
        }

        if (0 != imageStyle.getDefaultSource()) {
            rollIvShow.setImageResource(imageStyle.getDefaultSource());
        }
    }

    private void initTitleStyle () {
        ViewGroup.LayoutParams titleParams = titleShow.getLayoutParams();
        AdRollAttr.TitleStyle  titleStyle  = adRollAttr.getTitleStyle();

        if (null == titleStyle) {
            return;
        }

        if (titleParams instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams = (LayoutParams) titleParams;
            if (0 != titleStyle.getWidth()) {
                layoutParams.width = DeviceUtil.dip2px(mActivity, titleStyle.getWidth());
            } else {
                layoutParams.width = LayoutParams.MATCH_PARENT;
            }

            if (0 != titleStyle.getHeight()) {
                layoutParams.height = DeviceUtil.dip2px(mActivity, titleStyle.getHeight());
            } else {
                layoutParams.width = LayoutParams.WRAP_CONTENT;
            }

            if (0 != titleStyle.getMarginLeft()) {
                layoutParams.leftMargin = DeviceUtil.dip2px(mActivity, titleStyle.getMarginLeft());
            }

            if (0 != titleStyle.getMarginTop()) {
                layoutParams.topMargin = DeviceUtil.dip2px(mActivity, titleStyle.getMarginTop());
            }

            if (0 != titleStyle.getMarginRight()) {
                layoutParams.rightMargin = DeviceUtil.dip2px(mActivity, titleStyle.getMarginRight());
            }

            if (0 != titleStyle.getMarginBottom()) {
                layoutParams.bottomMargin = DeviceUtil.dip2px(mActivity, titleStyle.getMarginBottom());
            }
            titleShow.setLayoutParams(layoutParams);

            if (0 != titleStyle.getTextColor()) {
                titleShow.setTextColor(titleStyle.getTextColor());
            }

            if (0 != titleStyle.getTextSize()) {
                titleShow.setTextSize(titleStyle.getTextSize());
            }

            if (0 != titleStyle.getTextStyle()) {
                titleShow.setTypeface(null, titleStyle.getTextStyle());
            }
        }
    }

    private void initDescStyle () {
        ViewGroup.LayoutParams descParams = descShow.getLayoutParams();
        AdRollAttr.DescStyle   descStyle  = adRollAttr.getDescStyle();

        if (null == descStyle) {
            return;
        }

        if (descParams instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams = (LayoutParams) descParams;
            if (0 != descStyle.getWidth()) {
                layoutParams.width = DeviceUtil.dip2px(mActivity, descStyle.getWidth());
            }

            if (0 != descStyle.getHeight()) {
                layoutParams.height = DeviceUtil.dip2px(mActivity, descStyle.getHeight());
            }

            if (0 != descStyle.getMarginLeft()) {
                layoutParams.leftMargin = DeviceUtil.dip2px(mActivity, descStyle.getMarginLeft());
            }

            if (0 != descStyle.getMarginTop()) {
                layoutParams.topMargin = DeviceUtil.dip2px(mActivity, descStyle.getMarginTop());
            }

            if (0 != descStyle.getMarginRight()) {
                layoutParams.rightMargin = DeviceUtil.dip2px(mActivity, descStyle.getMarginRight());
            }

            if (0 != descStyle.getMarginBottom()) {
                layoutParams.bottomMargin = DeviceUtil.dip2px(mActivity, descStyle.getMarginBottom());
            }
            descShow.setLayoutParams(layoutParams);

            if (0 != descStyle.getTextColor()) {
                descShow.setTextColor(descStyle.getTextColor());
            }

            if (0 != descStyle.getTextSize()) {
                descShow.setTextSize(descStyle.getTextSize());
            }

            if (0 != descStyle.getTextStyle()) {
                descShow.setTypeface(null, descStyle.getTextStyle());
            }
        }
    }

    private void initRightBottomStyle () {
        ViewGroup.LayoutParams          closeShowLayoutParams = closeShow.getLayoutParams();
        AdRollAttr.RightBottomIconStyle rightBottomIconStyle  = adRollAttr.getRightBottomIconStyle();

        if (null == rightBottomIconStyle) {
            return;
        }

        if (closeShowLayoutParams instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams = (LayoutParams) closeShowLayoutParams;
            if (0 != rightBottomIconStyle.getWidth()) {
                layoutParams.width = DeviceUtil.dip2px(mActivity, rightBottomIconStyle.getWidth());
            }

            if (0 != rightBottomIconStyle.getHeight()) {
                layoutParams.height = DeviceUtil.dip2px(mActivity, rightBottomIconStyle.getHeight());
            }

            if (0 != rightBottomIconStyle.getMarginLeft()) {
                layoutParams.leftMargin = DeviceUtil.dip2px(mActivity, rightBottomIconStyle.getMarginLeft());
            }

            if (0 != rightBottomIconStyle.getMarginTop()) {
                layoutParams.topMargin = DeviceUtil.dip2px(mActivity, rightBottomIconStyle.getMarginTop());
            }

            if (0 != rightBottomIconStyle.getMarginRight()) {
                layoutParams.rightMargin = DeviceUtil.dip2px(mActivity, rightBottomIconStyle.getMarginRight());
            }

            if (0 != rightBottomIconStyle.getMarginBottom()) {
                layoutParams.bottomMargin = DeviceUtil.dip2px(mActivity, rightBottomIconStyle.getMarginBottom());
            }
            closeShow.setLayoutParams(layoutParams);
        }
    }
}
