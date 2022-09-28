package com.corpize.sdk.ivoice.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.corpize.sdk.R;
import com.corpize.sdk.ivoice.QCiVoiceSdk;
import com.corpize.sdk.ivoice.QcCustomTemplateAttr;
import com.corpize.sdk.ivoice.bean.response.AdAudioBean;
import com.corpize.sdk.ivoice.common.CommonHandler;
import com.corpize.sdk.ivoice.listener.OnPlayerAdListener;
import com.corpize.sdk.ivoice.listener.QcCustomTemplateListener;
import com.corpize.sdk.ivoice.utils.CommonInteractiveEventUtils;
import com.corpize.sdk.ivoice.utils.CommonPlayerAdUtils;
import com.corpize.sdk.ivoice.utils.CommonSendShowExposureUtils;
import com.corpize.sdk.ivoice.utils.CustomMonitorVolumeUtils;
import com.corpize.sdk.ivoice.utils.DeviceUtil;
import com.corpize.sdk.ivoice.utils.ImageUtils;
import com.corpize.sdk.ivoice.utils.LogUtils;
import com.corpize.sdk.ivoice.utils.MediaPlayerUtil;
import com.corpize.sdk.ivoice.utils.SpUtils;
import com.corpize.sdk.ivoice.utils.countdown.CustomCountDownUtils;
import com.corpize.sdk.ivoice.utils.countdown.OnCustomCountDownListener;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * author : xpSun
 * date : 12/8/21
 * description :
 */
public class QcCustomTemplateView extends FrameLayout implements View.OnClickListener {

    private boolean mHaveSendViewShow = false;//是否发送展示曝光请求
    private boolean isFirstCallBack = true;//是否是第一次回调

    private int mAllTime = 0;//第一个广告音频的播放的总时长

    private int callbackType = 0;//播放结束后继续操作的类型
    private int mPosition;

    /**
     * 广告播完回调逻辑处理
     */
    private CommonHandler mHandler;
    private CustomMonitorVolumeUtils customMonitorVolumeUtils;

    private WeakReference<Activity> activityWeakReference;
    private QcCustomTemplateListener mListener;
    private AdAudioBean mResponse;
    private QcCustomTemplateAttr attr;

    private View rootView;
    private QcadImageView customTemplateCover;
    private TextView customTemplateAdTip;
    private LinearLayout customTemplateSkipLayout;
    private TextView customTemplateSkipTimer;
    private View customTemplateSkipLinear;
    private TextView customTemplateSkipView;
    private TextView customTemplateMainTitle;
    private QcadImageView customTemplateAdIcon;
    private TextView customTemplateSubtitleView;
    private TextView customTemplateAdDesc;

    private CustomCountDownUtils adAutoCloseCountDown;
    private int adAutoCloseCountDownNumber = 0;
    private CustomCountDownUtils adAutoCloseShowSkip;
    private int adAutoCloseCountDownShowSkipNumber = 0;
    private boolean isDestroySkip = false;
    private Integer provider;
    private int currentProgress = 0;

    private CommonPlayerAdUtils commonPlayerAdUtils;

    public QcCustomTemplateView(Activity activity) {
        this(activity, null);
        this.activityWeakReference = new WeakReference(activity);
        commonPlayerAdUtils = new CommonPlayerAdUtils(activity);
        commonPlayerAdUtils.setOnPlayerAdListener(new OnPlayerAdListener() {
            @Override
            public void onAdCompleteCallBack() {
                callbackType = 1;
                QcCustomTemplateView.this.onAdCompleteCallBack();
            }

            @Override
            public void onAdExposure() {
                if (mListener != null) {
                    mListener.onAdExposure();
                }
            }

            @Override
            public void onAdError(String msg) {
                if (mListener != null) {
                    mListener.onAdError(msg);
                }
            }

            @Override
            public void sendAdExposure(String url) {

            }

            @Override
            public void onAdClick() {
                if (mListener != null) {
                    mListener.onAdClick();
                }
            }
        });
    }

    public QcCustomTemplateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QcCustomTemplateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    public void setProvider(Integer provider) {
        this.provider = provider;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    public void setListener(QcCustomTemplateListener listener) {
        this.mListener = listener;

        if (mListener != null) {
            mListener.onFetchAdContentView(
                    customTemplateAdTip,
                    customTemplateSkipLayout,
                    customTemplateMainTitle,
                    customTemplateSubtitleView,
                    customTemplateAdDesc
            );
        }
    }

    public void setAdAudioBean(AdAudioBean mResponse) {
        this.mResponse = mResponse;
        initData();
    }

    public void setResponseListener(AdAudioBean adAudioBean) {
        if (mListener != null) {
            mListener.onFetchApiResponse(adAudioBean);
        }
    }

    private void initData() {
        try {
            if (null == mResponse) {
                return;
            }

            if (commonPlayerAdUtils != null) {
                commonPlayerAdUtils.setResponse(mResponse);
            }

            if (null != attr &&
                    attr.isEnableSkip() &&
                    null != mResponse &&
                    0 != mResponse.getDuration()
            ) {
                customTemplateSkipLayout.setVisibility(View.VISIBLE);
                adAutoCloseCountDownNumber = mResponse.getDuration();
                customTemplateSkipTimer.setText(String.format("%ss", adAutoCloseCountDownNumber));
            } else {
                customTemplateSkipLayout.setVisibility(View.GONE);
            }

            //封面
            ImageUtils.loadImage(getContext(), mResponse.getFirstimg(), customTemplateCover);

            if (null != mResponse.getIcon() && !TextUtils.isEmpty(mResponse.getIcon().getUrl())) {
                //icon
                ImageUtils.loadImage(getContext(), mResponse.getIcon().getUrl(), customTemplateAdIcon);
            }

            customTemplateMainTitle.setText(TextUtils.isEmpty(mResponse.getTitle()) ? "" : mResponse.getTitle());
            customTemplateSubtitleView.setText(TextUtils.isEmpty(mResponse.getDesc()) ? "" : mResponse.getDesc());

            if (mListener != null) {
                mListener.fetchMainTitle(TextUtils.isEmpty(mResponse.getTitle()) ? "" : mResponse.getTitle());
            }

            //根据action显示不同的按钮的额名称
            int action = mResponse.getAction();
            if (action == 6) {//下载
                customTemplateAdDesc.setText("立即下载");
            } else if (action == 1 || action == 2) {
                customTemplateAdDesc.setText("点击打开");
            } else if (action == 7) {
                customTemplateAdDesc.setText("查看详情");
            } else if (action == 3) {
                customTemplateAdDesc.setText("拨打电话");
            } else if (action == 8) {
                customTemplateAdDesc.setText("领优惠券");
            } else if (action == 0) {
                customTemplateAdDesc.setVisibility(GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAttr(QcCustomTemplateAttr attr) {
        this.attr = attr;
        initAttr();
    }

    private void initView() {
        rootView = LayoutInflater.from(getContext())
                .inflate(R.layout.view_custom_template_layout, null, false);
        customTemplateCover = rootView.findViewById(R.id.custom_template_cover);
        customTemplateAdTip = rootView.findViewById(R.id.custom_template_ad_tip);
        customTemplateSkipLayout = rootView.findViewById(R.id.custom_template_skip_layout);
        customTemplateSkipTimer = rootView.findViewById(R.id.custom_template_skip_timer);
        customTemplateSkipLinear = rootView.findViewById(R.id.custom_template_skip_linear);
        customTemplateSkipView = rootView.findViewById(R.id.custom_template_skip_view);
        customTemplateMainTitle = rootView.findViewById(R.id.custom_template_main_title);
        customTemplateAdIcon = rootView.findViewById(R.id.custom_template_ad_icon);
        customTemplateSubtitleView = rootView.findViewById(R.id.custom_template_subtitle);
        customTemplateAdDesc = rootView.findViewById(R.id.custom_template_ad_desc);

        addView(rootView);

        customTemplateCover.setOnClickListener(this);
        customTemplateSkipView.setOnClickListener(this);
        customTemplateAdDesc.setOnClickListener(this);
    }

    private void initAttr() {
        try {
            if (null == attr) {
                return;
            }

            QcCustomTemplateAttr.CoverStyle coverStyle = attr.getCoverStyle();
            if (null != coverStyle) {
                FrameLayout.LayoutParams layoutParams = (LayoutParams) customTemplateCover.getLayoutParams();
                if (0 != coverStyle.getWidth()) {
                    layoutParams.width = coverStyle.getWidth();
                }

                if (0 != coverStyle.getHeight()) {
                    layoutParams.height = coverStyle.getHeight();
                }

                customTemplateCover.setLayoutParams(layoutParams);

                if (0 != coverStyle.getRadius()) {
                    customTemplateCover.setRadius(coverStyle.getRadius());
                }
            }

            QcCustomTemplateAttr.IconStyle iconStyle = attr.getIconStyle();
            if (null != iconStyle) {
                FrameLayout.LayoutParams layoutParams = (LayoutParams) customTemplateAdIcon.getLayoutParams();
                if (0 != iconStyle.getWidth()) {
                    layoutParams.width = DeviceUtil.dip2px(getContext(), iconStyle.getWidth());
                }

                if (0 != iconStyle.getHeight()) {
                    layoutParams.height = DeviceUtil.dip2px(getContext(), iconStyle.getHeight());
                }

                if (iconStyle.isEnableMargin()) {
                    int left = DeviceUtil.dip2px(getContext(), iconStyle.getMarginLeft());
                    int top = DeviceUtil.dip2px(getContext(), iconStyle.getMarginLeft());
                    int right = DeviceUtil.dip2px(getContext(), iconStyle.getMarginRight());
                    int bottom = DeviceUtil.dip2px(getContext(), iconStyle.getMarginBottom());

                    layoutParams.setMargins(left, top, right, bottom);
                }

                if (0 != iconStyle.getLayoutGravity()) {
                    layoutParams.gravity = iconStyle.getLayoutGravity();
                }

                customTemplateAdIcon.setLayoutParams(layoutParams);

                if (0 != iconStyle.getRadius()) {
                    customTemplateAdIcon.setRadius(iconStyle.getRadius());
                }
            }

            if (!TextUtils.isEmpty(attr.getSkipTipValue())) {
                customTemplateSkipView.setText(attr.getSkipTipValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        LogUtils.e("进入自定义控件的onLayout");
        QCiVoiceSdk.get().width = getWidth();
        QCiVoiceSdk.get().height = getHeight();

        int[] position = new int[2];
        getLocationOnScreen(position);
        QCiVoiceSdk.get().positionX = position[0]; // view距离 屏幕左边的距离（即x轴方向）
        QCiVoiceSdk.get().positionY = position[1]; // view距离 屏幕顶边的距离（即y轴方向）

        if (null != mResponse.getImps() && !mHaveSendViewShow) {
            mHaveSendViewShow = true;
            sendShowExposure(mResponse.getImps());//广告位曝光
            if (mListener != null) {
                mListener.onAdExposure();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                QCiVoiceSdk.get().clickDownX = event.getX();
                QCiVoiceSdk.get().clickDownY = event.getY();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                QCiVoiceSdk.get().clickUpX = event.getX();
                QCiVoiceSdk.get().clickUpY = event.getY();
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    /**
     * 按钮的点击事件
     */
    private void setShakeClick() {
        Activity activity = activityWeakReference.get();

        if (null == activity) {
            return;
        }

        CommonInteractiveEventUtils.getInstance()
                .setProvider(provider == null ? 0 : provider)
                .onShakeEvent(
                        activity,
                        mResponse,
                        new CommonInteractiveEventUtils.OnInteractiveEventListener() {
                            @Override
                            public void onPlayerStatusChanger(boolean pause) {
                                if (pause) {
                                    MediaPlayerUtil.getInstance().stopAndRelease();
                                }
                            }

                            @Override
                            public void onAdClick() {
                                if (mListener != null) {
                                    mListener.onAdClick();
                                }
                            }
                        });
    }

    /**
     * 发送曝光,计算了宽高及时间戳
     */
    private void sendShowExposure(List<String> imgList) {
        CommonSendShowExposureUtils.getInstance().sendShowExposure(mResponse.getImps());
    }

    /**
     * 播放音频
     */
    public void playAd() {
        int[] position = new int[2];
        getLocationOnScreen(position);
        QCiVoiceSdk.get().positionX = position[0]; // view距离 屏幕左边的距离（即x轴方向）
        QCiVoiceSdk.get().positionY = position[1]; // view距离 屏幕顶边的距离（即y轴方向）
        LogUtils.e("广告区域左上角坐标2X=" + QCiVoiceSdk.get().positionX + "|Y=" + QCiVoiceSdk.get().positionY);

        isFirstCallBack = true;

        if (mResponse != null) {
            callbackType = null == mResponse.getRendering_config() ? 1 : mResponse.getRendering_config().getStop_playing_mode();

            if (null != mResponse &&
                    null != attr &&
                    attr.isEnableSkip() &&
                    0 != mResponse.getDuration() &&
                    0 != mResponse.getSkip()
            ) {
                try {
                    customTemplateSkipLayout.setVisibility(View.VISIBLE);
                    customTemplateSkipTimer.setVisibility(View.VISIBLE);
                    customTemplateSkipLinear.setVisibility(View.GONE);
                    customTemplateSkipView.setVisibility(View.GONE);

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

            if (commonPlayerAdUtils != null) {
                commonPlayerAdUtils.playerAd();
            }
        }
    }

    /**
     * 重新播放音频
     */
    public void resumePlayAd() {
        MediaPlayerUtil.getInstance().resumePlay();
    }

    /**
     * 跳过广告 停止音频播放 给外部调用 需要添加close的曝光监听
     */
    public void skipAd() {
        stopAndReleaseAd();

        try {
            if (attr.isEnableSkip()) {
                if (adAutoCloseCountDown != null) {
                    adAutoCloseCountDown.cancel();
                }

                if (adAutoCloseShowSkip != null) {
                    adAutoCloseShowSkip.cancel();
                }

                adAutoCloseCountDownNumber = 0;
                customTemplateSkipTimer.setText(String.format("%ss", adAutoCloseCountDownNumber));

                customTemplateSkipTimer.setVisibility(View.GONE);
                customTemplateSkipLinear.setVisibility(View.GONE);
                customTemplateSkipView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止播放音频
     */
    public void stopAndReleaseAd() {
        commonPlayerAdUtils.stopAndReleaseAd();
    }

    /**
     * 点击广告操作后回调
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    private void onAdCompleteCallBack() {
        if (mListener != null) {
            if (callbackType == 1) {
                mListener.onAdCompletion();
            } else {
                if (mHandler == null) {
                    mHandler = new CommonHandler<>(this);
                }
                mHandler.postDelay(5000, new CommonHandler.PostDelayCallBack() {
                    @Override
                    public void callBack() {
                        if (callbackType == 2) {
                            playAd();
                            callbackType = 1;
                        } else if (callbackType == 3) {
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
    private void closeCommonHandler() {
        if (mHandler != null) {
            mHandler.releaseHandler();
            mHandler = null;
        }
    }

    public void pause() {
        skipOnPause();

        MediaPlayerUtil.getInstance().pausePlay();
    }

    public void resume() {
        LogUtils.e("resume");

        interactionResume();
        skipOnResume();
        MediaPlayerUtil.getInstance().resumePlay();
    }

    private void interactionResume() {
        boolean interaction = QCiVoiceSdk.get().isInteractionFlag();

        if (interaction
                && null != mResponse
                && 1 == mResponse.getLock_interaction_switch()
        ) {
            QCiVoiceSdk.get().setInteractionFlag(false);
            setShakeClick();
        }
    }

    private void skipOnResume() {
        try {
            if (attr.isEnableSkip()) {
                if (!isDestroySkip) {
                    customTemplateSkipLayout.setVisibility(View.VISIBLE);
                }

                if (!MediaPlayerUtil.getInstance().isPlaying()) {
                    return;
                }

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
                    if (adAutoCloseShowSkip != null) {
                        adAutoCloseShowSkip.start();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void skipOnPause() {
        try {
            if (attr.isEnableSkip()) {
                if (adAutoCloseCountDown != null) {
                    adAutoCloseCountDown.cancel();
                }

                if (adAutoCloseShowSkip != null) {
                    adAutoCloseShowSkip.cancel();
                    adAutoCloseShowSkip.onFinish();
                }

                customTemplateSkipLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void skipOnDestroy() {
        try {
            if (attr.isEnableSkip()) {
                if (adAutoCloseCountDown != null) {
                    adAutoCloseCountDown.cancel();
                }

                if (adAutoCloseShowSkip != null) {
                    adAutoCloseShowSkip.cancel();
                }

                adAutoCloseCountDownNumber = 0;
                adAutoCloseCountDownShowSkipNumber = 0;

                if (null != mResponse && 6 != mResponse.getAction()) {
                    isDestroySkip = true;
                }

                customTemplateSkipLayout.setVisibility(View.VISIBLE);
                customTemplateSkipTimer.setVisibility(View.GONE);
                customTemplateSkipLinear.setVisibility(View.GONE);
                customTemplateSkipView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void adAutoCloseCountDown(long millisInFuture, long countDownInterval) {
        try {
            if (null != adAutoCloseCountDown) {
                adAutoCloseCountDown.cancel();
            }

            adAutoCloseCountDown = new CustomCountDownUtils(millisInFuture, countDownInterval);
            adAutoCloseCountDown.setOnCustomCountDownListener(new OnCustomCountDownListener() {
                @Override
                public void onTick(long millisUntilFinished) {
                    try {
                        adAutoCloseCountDownNumber = Long.valueOf(millisUntilFinished / 1000).intValue();
                        customTemplateSkipTimer.setText(String.format("%ss", adAutoCloseCountDownNumber));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFinish() {
                    try {
                        customTemplateSkipTimer.setText("");
                        customTemplateSkipLayout.setVisibility(View.VISIBLE);
                        customTemplateSkipTimer.setVisibility(View.GONE);
                        customTemplateSkipLinear.setVisibility(View.GONE);
                        customTemplateSkipView.setVisibility(View.VISIBLE);

                        if (0 == adAutoCloseCountDownNumber) {
                            if (attr.isSkipAutoClose()) {
                                skipAd();
                            }
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

    private void adAutoCloseShowSkip(final long millisInFuture, long countDownInterval) {
        try {
            if (null != adAutoCloseShowSkip) {
                adAutoCloseShowSkip.cancel();
            }

            adAutoCloseShowSkip = new CustomCountDownUtils(millisInFuture, countDownInterval);
            adAutoCloseShowSkip.setOnCustomCountDownListener(new OnCustomCountDownListener() {
                @Override
                public void onTick(long millisUntilFinished) {
                    adAutoCloseCountDownShowSkipNumber = Long.valueOf(millisUntilFinished / 1000).intValue();
                }

                @Override
                public void onFinish() {
                    if (adAutoCloseCountDownShowSkipNumber == 0) {
                        customTemplateSkipLinear.setVisibility(View.VISIBLE);
                        customTemplateSkipView.setVisibility(View.VISIBLE);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.custom_template_skip_view) {
            skipAd();

            if (mListener != null) {
                mListener.onAdSkipClick();
            }

            customTemplateSkipLinear.setVisibility(View.GONE);
        } else if (v.getId() == R.id.custom_template_ad_desc ||
                v.getId() == R.id.custom_template_cover) {
            stopAndReleaseAd();
            skipOnDestroy();
            setShakeClick();
        }
    }
}
