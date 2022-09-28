package com.example.qcaudioad.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.corpize.sdk.ivoice.QCiVoiceSdk;
import com.corpize.sdk.ivoice.admanager.QcAdManager;
import com.corpize.sdk.ivoice.bean.UserPlayInfoBean;
import com.corpize.sdk.ivoice.listener.QcAutoRotationListener;
import com.example.qcaudioad.R;
import com.example.qcaudioad.base.BaseActivity;
import com.example.qcaudioad.common.ADIDConstants;
import com.example.qcaudioad.utils.CommonLabelUtils;

import java.util.List;

/**
 * author : xpSun
 * date : 2022/2/22
 * description :
 */
public class CustomAutoRotationAdStyleActivity extends BaseActivity {

    private FrameLayout rootView;
    private QcAdManager mQcCustomAdManager;
    private TextView    status;
    private TextView    isEnableDestroyTvShow;

    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_rotation_ad_style_layout);

        TextView load  = findViewById(R.id.custom_auto_play_ad_load);
        TextView show  = findViewById(R.id.custom_auto_play_ad_show);
        TextView stop  = findViewById(R.id.custom_auto_play_ad_stop);
        TextView clear = findViewById(R.id.custom_auto_play_ad_clear);

        status = findViewById(R.id.custom_auto_play_ad_status);
        rootView = findViewById(R.id.custom_auto_play_root_view);

        load.setOnClickListener(this);
        show.setOnClickListener(this);
        stop.setOnClickListener(this);
        clear.setOnClickListener(this);

        isShowActionBar(false);
    }

    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.custom_auto_play_ad_load:
                initInfoVoice();
                break;
            case R.id.custom_auto_play_ad_show:
                if (mQcCustomAdManager == null) {
                    showLoadAdErrorToast();
                    return;
                }
                mQcCustomAdManager.startPlayAd();
                status.setText(String.format("当前广告状态:%s", "startPlayAd"));
                break;
            case R.id.custom_auto_play_ad_stop:
                if (mQcCustomAdManager == null) {
                    showLoadAdErrorToast();
                    return;
                }
                mQcCustomAdManager.skipPlayAd();
                status.setText(String.format("当前广告状态:%s", "skipPlayAd"));
                break;
            case R.id.custom_auto_play_ad_clear:
                if (mQcCustomAdManager == null) {
                    showLoadAdErrorToast();
                    return;
                }
                mQcCustomAdManager.skipPlayAd();
                mQcCustomAdManager.destroy();
                rootView.removeAllViews();
                mQcCustomAdManager = null;
                status.setText(String.format("当前广告状态:%s", "destroy"));
                break;
            default:
                break;
        }
    }


    private void initInfoVoice () {
        //设置广告的adId和自定义的参数
        List<UserPlayInfoBean> userPlayInfoBeans = CommonLabelUtils.getCommonLabels();

        String adId =  QCiVoiceSdk.get().isDebug() ?
                ADIDConstants.TestEnum.AUTO_ROTATION_ADID :
                ADIDConstants.ReleaseEnum.AUTO_ROTATION_ADID;
        QCiVoiceSdk.get().addAutoRotationAd(
                this,
                adId,
                userPlayInfoBeans,
                new QcAutoRotationListener() {
            @Override
            public void onAdReceive (QcAdManager manager, View adView) {
                mQcCustomAdManager = manager;
                rootView.removeAllViews();
                rootView.addView(adView);
            }

            @Override
            public void onAdClick () {
                status.setText(String.format("当前广告状态:%s", "onAdClick"));
            }

            @Override
            public void onAdCompletion () {
                status.setText(String.format("当前广告状态:%s", "onAdCompletion"));
            }

            @Override
            public void onAdError (String fail) {
                status.setText(String.format("当前广告状态:%s", "onAdError:" + fail));
            }

            @Override
            public void onAdExposure () {
                status.setText(String.format("当前广告状态:%s", "onAdExposure"));
            }
        });
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (QCiVoiceSdk.get() != null) {
            QCiVoiceSdk.get().onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConfigurationChanged (@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume () {
        super.onResume();
        //必须调用
        if (QCiVoiceSdk.get() != null) {
            QCiVoiceSdk.get().onResume();
        }
    }

    @Override
    protected void onPause () {
        super.onPause();
        //必须调用
        if (QCiVoiceSdk.get() != null) {
            QCiVoiceSdk.get().onPause();
        }
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        //释放内存
        if (QCiVoiceSdk.get() != null) {
            QCiVoiceSdk.get().onDestroy();
        }
    }
}
