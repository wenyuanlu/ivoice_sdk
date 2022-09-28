package com.example.qcaudioad.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.corpize.sdk.ivoice.QCiVoiceSdk;
import com.corpize.sdk.ivoice.admanager.QcAdManager;
import com.corpize.sdk.ivoice.bean.UserPlayInfoBean;
import com.corpize.sdk.ivoice.listener.QcVoiceAdListener;
import com.example.qcaudioad.R;
import com.example.qcaudioad.base.BaseActivity;
import com.example.qcaudioad.common.ADIDConstants;
import com.example.qcaudioad.utils.CommonLabelUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author : xpSun
 * date : 10/28/21
 * description :冠名
 */
public class CustomVoiceActivity extends BaseActivity {

    private static final String      TAG = "CustomVoiceActivity";
    private              QcAdManager qcAdManager;

    private TextView loadAd;
    private TextView showAd;
    private TextView stopAd;
    private TextView clearAd;
    private TextView status;

    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_voice_info_layout);
        setTitle("冠名");

        loadAd = findViewById(R.id.custom_voice_ad_load);
        showAd = findViewById(R.id.custom_voice_ad_show);
        stopAd = findViewById(R.id.custom_voice_ad_stop);
        clearAd = findViewById(R.id.custom_voice_ad_clear);
        status = findViewById(R.id.custom_voice_ad_status);

        loadAd.setOnClickListener(this);
        showAd.setOnClickListener(this);
        stopAd.setOnClickListener(this);
        clearAd.setOnClickListener(this);
    }

    @Override
    public void onClick (View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.custom_voice_ad_load:
                loadAd();
                break;
            case R.id.custom_voice_ad_show:
                if (null == qcAdManager) {
                    showLoadAdErrorToast();
                    return;
                }

                qcAdManager.startPlayAd();
                status.setText(String.format("当前广告状态:%s", "startPlayAd"));
                break;
            case R.id.custom_voice_ad_stop:
                if (null == qcAdManager) {
                    showLoadAdErrorToast();
                    return;
                }

                qcAdManager.skipPlayAd();
                status.setText(String.format("当前广告状态:%s", "skipPlayAd"));
                break;
            case R.id.custom_voice_ad_clear:
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
        String                 voiceADID         = QCiVoiceSdk.get().isDebug() ? ADIDConstants.TestEnum.NAMING_ADID : ADIDConstants.ReleaseEnum.ONLY_VOICE_ADID;
        List<UserPlayInfoBean> userPlayInfoBeans = CommonLabelUtils.getCommonLabels();

        QCiVoiceSdk.get().addVoiceAd(
                this,
                voiceADID,
                userPlayInfoBeans,
                new QcVoiceAdListener() {
                    @Override
                    public void onAdExposure () {
                        Log.e(TAG, "onAdExposure");
                        status.setText(String.format("当前广告状态:%s", "onAdExposure"));
                    }

                    @Override
                    public void onAdReceive (QcAdManager manager) {
                        Log.e(TAG, "onAdReceive");
                        status.setText(String.format("当前广告状态:%s", "onAdReceive"));
                        qcAdManager = manager;
                    }

                    @Override
                    public void onAdPlayEndListener () {
                        Log.e(TAG, "onAdPlayEndListener");
                        status.setText(String.format("当前广告状态:%s", "onAdPlayEndListener"));
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
    protected void onActivityResult (int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (QCiVoiceSdk.get() != null) {
            QCiVoiceSdk.get().onActivityResult(requestCode, resultCode, data);
        }
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
