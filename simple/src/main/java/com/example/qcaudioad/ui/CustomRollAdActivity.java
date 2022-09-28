package com.example.qcaudioad.ui;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.corpize.sdk.ivoice.AdRollAttr;
import com.corpize.sdk.ivoice.QCiVoiceSdk;
import com.corpize.sdk.ivoice.admanager.QcAdManager;
import com.corpize.sdk.ivoice.listener.QcRollAdViewListener;
import com.example.qcaudioad.R;
import com.example.qcaudioad.base.BaseActivity;
import com.example.qcaudioad.common.ADIDConstants;

/**
 * author : xpSun
 * date : 11/22/21
 * description :
 */
public class CustomRollAdActivity extends BaseActivity {

    private static final String      TAG = "CustomVoiceActivity";
    private              QcAdManager qcAdManager;

    private TextView     loadAd;
    private TextView     showAd;
    private TextView     stopAd;
    private TextView     clearAd;
    private TextView     status;
    private LinearLayout bottomLayout;

    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roll_ad_layout);
        setTitle("贴片");

        loadAd = findViewById(R.id.roll_ad_load);
        showAd = findViewById(R.id.roll_ad_show);
        stopAd = findViewById(R.id.roll_ad_stop);
        clearAd = findViewById(R.id.roll_ad_clear);
        status = findViewById(R.id.roll_ad_status);
        bottomLayout = findViewById(R.id.roll_bottom_layout);

        loadAd.setOnClickListener(this);
        showAd.setOnClickListener(this);
        stopAd.setOnClickListener(this);
        clearAd.setOnClickListener(this);
    }

    @Override
    public void onClick (View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.roll_ad_load:
                loadAd();
                break;
            case R.id.roll_ad_show:
                if (null == qcAdManager) {
                    showLoadAdErrorToast();
                    return;
                }

                qcAdManager.startPlayAd();
                status.setText(String.format("当前广告状态:%s", "startPlayAd"));
                break;
            case R.id.roll_ad_stop:
                if (null == qcAdManager) {
                    showLoadAdErrorToast();
                    return;
                }

                qcAdManager.skipPlayAd();
                status.setText(String.format("当前广告状态:%s", "skipPlayAd"));
                break;
            case R.id.roll_ad_clear:
                if (null == qcAdManager) {
                    showLoadAdErrorToast();
                    return;
                }

                qcAdManager.skipPlayAd();
                qcAdManager.destroy();
                qcAdManager = null;
                status.setText(String.format("当前广告状态:%s", "destroy"));
                break;
            default:
                break;
        }
    }

    private void loadAd () {
        String     voiceADID  = QCiVoiceSdk.get().isDebug() ? ADIDConstants.TestEnum.INFO_ADID : ADIDConstants.ReleaseEnum.INFO_ADID;
        AdRollAttr adRollAttr = new AdRollAttr();

        adRollAttr.setAdWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        adRollAttr.setAdHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        adRollAttr.setBackgroundColor(Color.WHITE);

        AdRollAttr.ImageStyle imageStyle = new AdRollAttr.ImageStyle();
        imageStyle.setWidth(100);
        imageStyle.setHeight(100);
        imageStyle.setMarginLeft(15);
        imageStyle.setMarginTop(15);
        imageStyle.setMarginRight(15);
        imageStyle.setMarginBottom(15);
        adRollAttr.setLeftImageStyle(imageStyle);

        AdRollAttr.TitleStyle titleStyle = new AdRollAttr.TitleStyle();
        titleStyle.setMarginTop(20);
        titleStyle.setMarginRight(15);
        titleStyle.setTextColor(Color.parseColor("#333333"));
        titleStyle.setTextSize(16);
        titleStyle.setTextStyle(Typeface.BOLD);
        adRollAttr.setTitleStyle(titleStyle);

        AdRollAttr.DescStyle descStyle = new AdRollAttr.DescStyle();
        descStyle.setTextColor(Color.parseColor("#cccccc"));
        descStyle.setTextSize(12);
        descStyle.setMarginLeft(10);
        adRollAttr.setDescStyle(descStyle);

        AdRollAttr.RightBottomIconStyle rightBottomIconStyle = new AdRollAttr.RightBottomIconStyle();
        rightBottomIconStyle.setMarginRight(15);
        rightBottomIconStyle.setMarginBottom(15);
        adRollAttr.setRightBottomIconStyle(rightBottomIconStyle);

        QCiVoiceSdk.get().addRollAd(
                this,
                voiceADID,
                adRollAttr,
                new QcRollAdViewListener() {
                    @Override
                    public void onAdExposure () {//贴片广告曝光
                        Log.e(TAG, "onAdExposure");
                        status.setText(String.format("当前广告状态:%s", "onAdExposure"));
                    }

                    @Override
                    public void onRollAdClickClose () {//点击了关闭广告
                        Log.e(TAG, "onRollAdClickClose");
                        status.setText(String.format("当前广告状态:%s", "onRollAdClickClose"));
                    }

                    @Override
                    public void onRollAdDialogShow () {//展示dialog
                        Log.e(TAG, "onRollAdDialogShow");
                        status.setText(String.format("当前广告状态:%s", "onRollAdDialogShow"));
                    }

                    @Override
                    public void onRollAdDialogDismiss () {//dialog dismiss
                        Log.e(TAG, "onRollAdDialogDismiss");
                        status.setText(String.format("当前广告状态:%s", "onRollAdDialogDismiss"));
                    }

                    @Override
                    public void onRollVolumeChanger (int status) {//音频点击修改 0静音,1有声音
                        Log.e(TAG, "onRollVolumeChanger");
                        CustomRollAdActivity.this.status.setText(String.format("当前广告状态:%s", "onRollVolumeChanger" + status));
                    }

                    @Override
                    public void onAdReceive (QcAdManager manager, View view) {
                        Log.e(TAG, "onAdReceive");
                        status.setText(String.format("当前广告状态:%s", "onAdReceive"));
                        qcAdManager = manager;

                        if (null != bottomLayout) {
                            bottomLayout.removeAllViews();
                            bottomLayout.addView(view);
                        }
                    }

                    @Override
                    public void onAdClick () {//此处为触发摇一摇的回调
                        Log.e(TAG, "onAdClick");
                        status.setText(String.format("当前广告状态:%s", "onAdClick"));
                    }

                    @Override
                    public void onAdCompletion () {
                        Log.e(TAG, "onAdCompletion");
                        status.setText(String.format("当前广告状态:%s", "onAdCompletion"));
                    }

                    @Override
                    public void onAdError (String fail) {
                        Log.e(TAG, "onAdError:" + fail);
                        status.setText(String.format("当前广告状态:%s", "onAdError:" + fail));
                    }
                });
    }

    @Override
    protected void onResume () {
        super.onResume();
        //必须调用
        QCiVoiceSdk.get().onResume();
    }

    @Override
    protected void onPause () {
        super.onPause();
        //必须调用
        QCiVoiceSdk.get().onPause();
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        //释放内存
        QCiVoiceSdk.get().onDestroy();
    }
}
