package com.example.qcaudioad.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.corpize.sdk.ivoice.QCiVoiceSdk;
import com.corpize.sdk.ivoice.admanager.QcAdManager;
import com.corpize.sdk.ivoice.bean.UserPlayInfoBean;
import com.corpize.sdk.ivoice.listener.QcFirstVoiceAdViewListener;
import com.example.qcaudioad.R;
import com.example.qcaudioad.base.BaseActivity;
import com.example.qcaudioad.common.ADIDConstants;
import com.example.qcaudioad.utils.CommonLabelUtils;

import java.util.List;

public class QCFirstVoiceActivity extends BaseActivity {

    private TextView     loadVoice;
    private TextView     startVoice;
    private TextView     endVoice;
    private TextView     releaseVoice;
    private TextView     status;
    private LinearLayout bottomLayout;

    private QcAdManager qcAdManager;
    private View        adView;

    private static final String TAG = "QCFirstVoiceActivity";

    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_voice_layout);
        setTitle("首听");

        loadVoice = findViewById(R.id.first_voice_ad_load);
        startVoice = findViewById(R.id.first_voice_ad_show);
        endVoice = findViewById(R.id.first_voice_ad_stop);
        releaseVoice = findViewById(R.id.first_voice_ad_clear);
        status = findViewById(R.id.first_voice_ad_status);
        bottomLayout = findViewById(R.id.first_bottom_layout);

        loadVoice.setOnClickListener(this);
        startVoice.setOnClickListener(this);
        endVoice.setOnClickListener(this);
        releaseVoice.setOnClickListener(this);
    }

    private void loadFirstVoice () {
        String                 adid              = QCiVoiceSdk.get().isDebug() ? ADIDConstants.TestEnum.FIRST_VOICE_ADID : ADIDConstants.ReleaseEnum.FIRST_VOICE_ADID;
        List<UserPlayInfoBean> userPlayInfoBeans = CommonLabelUtils.getCommonLabels();

        QCiVoiceSdk.get()
                .addFirstVoiceAd(
                        this,
                        adid,
                        userPlayInfoBeans,
                        new QcFirstVoiceAdViewListener() {
                            @Override
                            public void onAdClick () {
                                Log.e(TAG, "onAdClick");
                                status.setText(String.format("当前广告状态:%s", "onAdClick"));
                            }

                            @Override
                            public void onAdCompletion () {
                                Log.e(TAG, "onAdCompletion:");
                                status.setText(String.format("当前广告状态:%s", "onAdCompletion"));
                            }

                            @Override
                            public void onAdError (String fail) {
                                Log.e(TAG, "onAdError:" + fail);
                                status.setText(String.format("当前广告状态:%s", "onAdError:" + fail));
                            }

                            @Override
                            public void onFirstVoiceAdClose () {
                                //首听关闭
                                Log.e(TAG, "onFirstVoiceAdViewClose");
                                status.setText(String.format("当前广告状态:%s", "onFirstVoiceAdViewClose"));
                                bottomLayout.setVisibility(View.GONE);
                            }

                            @Override
                            public void onFirstVoiceAdCountDownCompletion () {
                                //首听倒计时关闭
                                Log.e(TAG, "onFirstVoiceAdViewCountDownCompletion");
                                status.setText(String.format("当前广告状态:%s", "onFirstVoiceAdViewCountDownCompletion"));
                            }

                            @Override
                            public void onAdExposure () {
                                Log.e(TAG, "onAdExposure:");
                                status.setText(String.format("当前广告状态:%s", "onAdExposure"));
                            }

                            @Override
                            public void onAdReceive (QcAdManager manager, View adView) {
                                Log.e(TAG, "onFirstVoiceAdView");
                                status.setText(String.format("当前广告状态:%s", "onFirstVoiceAdView"));

                                QCFirstVoiceActivity.this.qcAdManager = manager;
                                QCFirstVoiceActivity.this.adView = adView;
                            }
                        });
    }

    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.first_voice_ad_load:
                loadFirstVoice();
                break;
            case R.id.first_voice_ad_show:
                if (null == qcAdManager || null == adView) {
                    showLoadAdErrorToast();
                    return;
                }

                bottomLayout.setVisibility(View.VISIBLE);
                bottomLayout.removeAllViews();
                bottomLayout.addView(adView);

                qcAdManager.startPlayAd();

                status.setText(String.format("当前广告状态:%s", "startPlayAd"));
                break;
            case R.id.first_voice_ad_stop:
                if (null == qcAdManager) {
                    showLoadAdErrorToast();
                    return;
                }
                qcAdManager.skipPlayAd();
                bottomLayout.removeAllViews();
                status.setText(String.format("当前广告状态:%s", "skipPlayAd"));
                break;
            case R.id.first_voice_ad_clear:
                if (null == qcAdManager) {
                    showLoadAdErrorToast();
                    return;
                }

                qcAdManager.destroy();
                qcAdManager = null;
                adView = null;
                bottomLayout.removeAllViews();
                bottomLayout.setVisibility(View.GONE);
                status.setText(String.format("当前广告状态:%s", "destroy"));
                break;
        }
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
