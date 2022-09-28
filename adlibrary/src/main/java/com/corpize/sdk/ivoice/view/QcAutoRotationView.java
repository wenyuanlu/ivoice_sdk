package com.corpize.sdk.ivoice.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.corpize.sdk.R;
import com.corpize.sdk.ivoice.QCiVoiceSdk;
import com.corpize.sdk.ivoice.adapter.CustomViewPagerAdapter;
import com.corpize.sdk.ivoice.base.CustomAdBaseView;
import com.corpize.sdk.ivoice.bean.response.AdAudioBean;
import com.corpize.sdk.ivoice.common.CommonShakeEnum;
import com.corpize.sdk.ivoice.listener.OnPlayerAdListener;
import com.corpize.sdk.ivoice.listener.QcAutoRotationListener;
import com.corpize.sdk.ivoice.utils.CommonPlayerAdUtils;
import com.corpize.sdk.ivoice.utils.CommonSendShowExposureUtils;
import com.corpize.sdk.ivoice.utils.FileUtils;
import com.corpize.sdk.ivoice.utils.ImageUtils;
import com.corpize.sdk.ivoice.utils.LogUtils;
import com.corpize.sdk.ivoice.utils.MediaPlayerUtil;
import com.corpize.sdk.ivoice.utils.countdown.CustomCountDownUtils;
import com.corpize.sdk.ivoice.utils.countdown.OnCustomCountDownListener;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * author : xpSun
 * date : 2022/2/25
 * description :
 */
public class QcAutoRotationView extends CustomAdBaseView {
    private boolean mHaveSendViewShow = false;//是否发送展示曝光请求

    private View rootView;
    private ViewPager viewPager;
    private TextView tipView;
    private ProgressBar progressBar;
    private ImageView bottomRightIcon;
    //    private TextView       iconTip;
    private ImageView coverIcon;
    //    private TextView       coverTip;
    private RelativeLayout coverLayout;
    private View coverCircular;
    private ImageView coverAnimation;
    private LinearLayout iconLayout;

    private CustomViewPagerAdapter adapter;
    private QcAutoRotationListener qcAutoRotationListener;
    private AdAudioBean adAudioBean;

    private static final int HANDLER_MESSAGE_WHAT = 0x1001;
    private int currentPosition = 0;

    private CustomCountDownUtils adAutoCloseCountDown;
    private int adAutoCloseCountDownNumber = 0;
    private int interactionTimer = 0;
    private int interactionWaitTimer = 0;

    private WeakReference<Activity> activityWeakReference;
    private CommonPlayerAdUtils commonPlayerAdUtils;
    private boolean isAutoRotationCoverEnable = false;
    private int autoRotationInterval = 3;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if (isAutoRotationCoverEnable) {
                return;
            }

            currentPosition += 1;
            viewPager.setCurrentItem(currentPosition);
            handler.sendEmptyMessageDelayed(autoRotationInterval * 1000, HANDLER_MESSAGE_WHAT);
        }
    };

    private AnimationDrawable animationDrawable;
    private List<String> images;
    private Bitmap qrBitmap;

    public QcAutoRotationView(@NonNull Context context) {
        this(context, null);

        if (null != context && context instanceof Activity) {
            Activity activity = (Activity) context;
            activityWeakReference = new WeakReference<>(activity);
            if (null == activityWeakReference) {
                return;
            }

            commonPlayerAdUtils = new CommonPlayerAdUtils(activityWeakReference.get());
            commonPlayerAdUtils.setOnPlayerAdListener(new OnPlayerAdListener() {
                @Override
                public void onAdCompleteCallBack() {
                    skipPlayAd();

                    if (qcAutoRotationListener != null) {
                        qcAutoRotationListener.onAdCompletion();
                    }

                    clearAnimationStatus();
                }

                @Override
                public void onAdExposure() {
                    if (qcAutoRotationListener != null) {
                        qcAutoRotationListener.onAdExposure();
                    }
                }

                @Override
                public void onAdError(String msg) {
                    if (qcAutoRotationListener != null) {
                        qcAutoRotationListener.onAdError(msg);
                    }
                }

                @Override
                public void sendAdExposure(String url) {

                }

                @Override
                public void onAdClick() {
                    if (qcAutoRotationListener != null) {
                        qcAutoRotationListener.onAdClick();
                    }
                }

                @Override
                public void onPlayCenterPositionListener(int position) {
                    if (0 == position) {
                        showCover();
                    }

                    if (adAutoCloseCountDown != null) {
                        adAutoCloseCountDown.cancel();
                    }
                }

                @Override
                public void onPlayStartListener(int position, int allTime) {
                    adAutoCloseCountDown(adAutoCloseCountDownNumber * 1000, 1000);

                    if (adAutoCloseCountDown != null) {
                        adAutoCloseCountDown.start();
                    }
                }
            });
        }
    }

    public QcAutoRotationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QcAutoRotationView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setQcAutoRotationListener(QcAutoRotationListener qcAutoRotationListener) {
        this.qcAutoRotationListener = qcAutoRotationListener;
    }

    public void setAdAudioBean(AdAudioBean adAudioBean) {
        if (null == adAudioBean) {
            return;
        }

        this.adAudioBean = adAudioBean;

        if (commonPlayerAdUtils != null) {
            commonPlayerAdUtils.setResponse(adAudioBean);
        }

//        try {
//            if (qrBitmap != null) {
//                qrBitmap.recycle();
//                qrBitmap = null;
//            }
//
//            ThreadManagerUtil.getDefaultProxy().execute(() -> {
//                try {
//                    String logoUrl    = adAudioBean.getIcon().getUrl();
//                    Bitmap logoBitmap = Glide.with(getContext()).asBitmap().load(logoUrl).submit(150, 150).get();
//
//                    Activity activity = activityWeakReference.get();
//                    if (null != activity) {
//                        activity.runOnUiThread(() -> {
//                            qrBitmap = QrUtil.createQRImage("https://www.baidu.com", 300, 300, logoBitmap);
//                            bottomRightIcon.setImageBitmap(qrBitmap);
//                        });
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        ImageUtils.loadImage(getContext(), adAudioBean.getQrcode(), bottomRightIcon);

//        if (!TextUtils.isEmpty(adAudioBean.getQr_title())) {
//            iconTip.setVisibility(View.VISIBLE);
//            iconTip.setText(adAudioBean.getQr_title());
//        } else {
//            iconTip.setVisibility(View.GONE);
//        }

        autoRotationInterval = adAudioBean.getSpan();

        images = new ArrayList<>();
        if (!TextUtils.isEmpty(adAudioBean.getFirstimg())) {
            images.add(adAudioBean.getFirstimg());
        }

        if (null != adAudioBean.getCoverext() && !adAudioBean.getCoverext().isEmpty()) {
            images.addAll(adAudioBean.getCoverext());
        }

        adapter.setImages(images);
        viewPager.setOffscreenPageLimit(images.size());
    }

    private void init() {
        rootView = LayoutInflater.from(getContext())
                .inflate(R.layout.view_auto_rotation_layout, null, false);
        viewPager = rootView.findViewById(R.id.view_pager_auto_rotation_viewpager);
        tipView = rootView.findViewById(R.id.view_pager_auto_rotation_ad_tip);
        progressBar = rootView.findViewById(R.id.view_pager_auto_rotation_progress);
        bottomRightIcon = rootView.findViewById(R.id.view_pager_auto_rotation_icon);
//        iconTip = rootView.findViewById(R.id.view_pager_auto_rotation_icon_tip);
        coverIcon = rootView.findViewById(R.id.view_pager_auto_rotation_cover_icon);
//        coverTip = rootView.findViewById(R.id.view_pager_auto_rotation_cover_tip);
        coverLayout = rootView.findViewById(R.id.view_pager_auto_rotation_cover_layout);
        coverCircular = rootView.findViewById(R.id.view_pager_auto_rotation_cover_circular);
        coverAnimation = rootView.findViewById(R.id.view_pager_auto_rotation_cover_animation);
        iconLayout = rootView.findViewById(R.id.view_pager_auto_rotation_icon_layout);
        addView(rootView);

        adapter = new CustomViewPagerAdapter();
        viewPager.setAdapter(adapter);
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
                        tipView.setText(String.format("广告 %ss", adAutoCloseCountDownNumber));

                        BigDecimal countDecimal = new BigDecimal(String.valueOf(adAutoCloseCountDownNumber));
                        int allTimer = adAudioBean.getDuration() + interactionTimer + interactionWaitTimer;
                        BigDecimal durationDecimal = new BigDecimal(String.valueOf(allTimer));
                        double progress = (countDecimal.divide(durationDecimal, 3, BigDecimal.ROUND_HALF_UP).doubleValue()) * 100;
                        int progressValue = Double.valueOf(progress).intValue();
                        progressBar.setProgress(100 - progressValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFinish() {
                    try {
                        tipView.setText("");
                        tipView.setVisibility(View.GONE);

                        adAutoCloseCountDownNumber = 0;
                        progressBar.setProgress(100);

                        clearAnimationStatus();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCover() {
        isAutoRotationCoverEnable = true;
        iconLayout.setVisibility(View.GONE);
        coverLayout.setVisibility(View.VISIBLE);

        if (null != adAudioBean) {
//            if (qrBitmap != null) {
//                coverIcon.setImageBitmap(qrBitmap);
//            }

            ImageUtils.loadImage(getContext(), adAudioBean.getQrcode(), coverIcon);

//            coverTip.setText(TextUtils.isEmpty(adAudioBean.getQr_title()) ? "" : adAudioBean.getQr_title());
//            if (!TextUtils.isEmpty(adAudioBean.getQr_title())) {
//                coverTip.setVisibility(View.VISIBLE);
//                coverTip.setText(adAudioBean.getQr_title());
//            } else {
//                coverTip.setVisibility(View.GONE);
//            }
        }

        initZoomAnimation();

        handlerDestroy();
    }

    private void initZoomAnimation() {
        coverAnimation.setImageBitmap(FileUtils.readBitMap(getContext(), R.drawable.auto_rotation_cover_drawable));
        animationDrawable = (AnimationDrawable) coverAnimation.getBackground();
        if (animationDrawable != null) {
            animationDrawable.start();
        }

        coverCircular.setVisibility(View.VISIBLE);
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0.1f, 10, 0.1f, 10,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);

        scaleAnimation.setDuration(1000);
        scaleAnimation.setRepeatCount(1);
        scaleAnimation.setRepeatMode(ScaleAnimation.RESTART);
        coverCircular.startAnimation(scaleAnimation);

        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                coverCircular.clearAnimation();
                coverCircular.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void clearAnimationStatus() {
        if (animationDrawable != null) {
            animationDrawable.stop();
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

        if (null != adAudioBean &&
                null != adAudioBean.getImps() &&
                !mHaveSendViewShow) {
            mHaveSendViewShow = true;
            sendShowExposure(adAudioBean.getImps());//广告位曝光
            if (qcAutoRotationListener != null) {
                qcAutoRotationListener.onAdExposure();
            }
        }
    }

    /**
     * 发送曝光,计算了宽高及时间戳
     */
    private void sendShowExposure(List<String> imgList) {
        if (adAudioBean != null) {
            CommonSendShowExposureUtils.getInstance().sendShowExposure(adAudioBean.getImps());
        }
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void startPlayAd() {
        if (null != images && !images.isEmpty() && 1 != images.size()) {
            handler.sendEmptyMessageDelayed(autoRotationInterval * 1000, HANDLER_MESSAGE_WHAT);
        }

        if (commonPlayerAdUtils != null) {
            commonPlayerAdUtils.playerAd();
        }

        adAutoCloseCountDownNumber = (null == adAudioBean ? 0 : adAudioBean.getDuration());

        if (null != adAudioBean && CommonShakeEnum.COMMON_ENUM_BRAND.getAction() == adAudioBean.getAction()) {
            if (null != adAudioBean.getInteractive() &&
                    null != adAudioBean.getInteractive().getRemind() &&
                    null != adAudioBean.getInteractive().getRemind().getBp() &&
                    null != adAudioBean.getInteractive().getRemind().getBp().getCar()
            ) {
                interactionTimer = adAudioBean.getInteractive().getRemind().getBp().getCar().getTimes();
                interactionWaitTimer = null == adAudioBean.getInteractive() ? 0 : adAudioBean.getInteractive().getWait();
                adAutoCloseCountDownNumber = adAutoCloseCountDownNumber + interactionTimer + interactionWaitTimer;
            }
        }

        initPlayAdStatus();
    }

    private void initPlayAdStatus() {
        isAutoRotationCoverEnable = false;
        tipView.setText(String.format("广告 %ss", adAutoCloseCountDownNumber));

        tipView.setVisibility(View.VISIBLE);
        iconLayout.setVisibility(View.VISIBLE);
        coverLayout.setVisibility(View.GONE);
    }

    @Override
    public void skipPlayAd() {
        if (commonPlayerAdUtils != null) {
            commonPlayerAdUtils.stopAndReleaseAd();
        }

        if (animationDrawable != null) {
            animationDrawable.stop();
        }

        if (adAutoCloseCountDown != null) {
            adAutoCloseCountDown.cancel();
        }

        isAutoRotationCoverEnable = true;

        handlerDestroy();
    }

    @Override
    public void destroy() {
        skipPlayAd();

        if (qrBitmap != null) {
            qrBitmap.recycle();
            qrBitmap = null;
        }
    }

    private void handlerDestroy() {
        handler.removeMessages(HANDLER_MESSAGE_WHAT);
        handler.removeCallbacks(null);
    }

    @Override
    public void resumePlayAd() {
        MediaPlayerUtil.getInstance().resumePlay();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}
