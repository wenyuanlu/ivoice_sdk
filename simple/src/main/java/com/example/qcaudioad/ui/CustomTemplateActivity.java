package com.example.qcaudioad.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.corpize.sdk.ivoice.QCiVoiceSdk;
import com.corpize.sdk.ivoice.QcCustomTemplateAttr;
import com.corpize.sdk.ivoice.admanager.QcAdManager;
import com.corpize.sdk.ivoice.bean.UserPlayInfoBean;
import com.corpize.sdk.ivoice.listener.QcCustomTemplateListener;
import com.example.qcaudioad.R;
import com.example.qcaudioad.base.BaseActivity;
import com.example.qcaudioad.common.ADIDConstants;
import com.example.qcaudioad.utils.CommonLabelUtils;

import java.util.List;

/**
 * author : xpSun
 * date : 12/8/21
 * description :
 */
public class CustomTemplateActivity extends BaseActivity {

    private final String TAG = "CustomTemplateActivity";

    private FrameLayout rootView;
    private QcAdManager mQcCustomAdManager;
    private TextView    status;
    private TextView    isEnableDestroyTvShow;
    private boolean     isEnableDestroy = true;

    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_template_layout);
        setTitle("自定义渲染模板");

        TextView load  = findViewById(R.id.custom_template_ad_load);
        TextView show  = findViewById(R.id.custom_template_ad_show);
        TextView stop  = findViewById(R.id.custom_template_ad_stop);
        TextView clear = findViewById(R.id.custom_template_ad_clear);

        status = findViewById(R.id.custom_template_ad_status);
        rootView = findViewById(R.id.custom_template_root_view);

        isEnableDestroyTvShow = findViewById(R.id.custom_template_ad_isEnable_destroy);

        load.setOnClickListener(this);
        show.setOnClickListener(this);
        stop.setOnClickListener(this);
        clear.setOnClickListener(this);
        isEnableDestroyTvShow.setOnClickListener(this);
    }

    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.custom_template_ad_load:
                initInfoVoice();
                break;
            case R.id.custom_template_ad_show:
                if (mQcCustomAdManager == null) {
                    showLoadAdErrorToast();
                    return;
                }
                mQcCustomAdManager.startPlayAd();
                status.setText(String.format("当前广告状态:%s", "startPlayAd"));
                break;
            case R.id.custom_template_ad_stop:
                if (mQcCustomAdManager == null) {
                    showLoadAdErrorToast();
                    return;
                }
                mQcCustomAdManager.skipPlayAd();
                status.setText(String.format("当前广告状态:%s", "skipPlayAd"));
                break;
            case R.id.custom_template_ad_clear:
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
            case R.id.custom_template_ad_isEnable_destroy:
                isEnableDestroy = !isEnableDestroy;

                if (isEnableDestroy) {
                    isEnableDestroyTvShow.setText("未屏蔽onDestroy");
                } else {
                    isEnableDestroyTvShow.setText("已屏蔽onDestroy");
                }
                break;
            default:
                break;
        }
    }

    private void initInfoVoice () {
        //设置广告的adId和自定义的参数
        QcCustomTemplateAttr   attr              = fetchAdAttr();
        List<UserPlayInfoBean> userPlayInfoBeans = CommonLabelUtils.getCommonLabels();

        //获取广告
        QCiVoiceSdk.get().addCustomTemplateAd(
                this,
                attr,
                QCiVoiceSdk.get().isDebug() ?
                        ADIDConstants.TestEnum.INFO_ADID :
                        ADIDConstants.ReleaseEnum.INFO_ADID,
                userPlayInfoBeans,
                new QcCustomTemplateListener() {
                    @Override
                    public void onAdReceive (QcAdManager manager, View adView) {
                        Log.e(TAG, "onAdReceive");
                        status.setText(String.format("当前广告状态:%s", "onAdReceive"));

                        mQcCustomAdManager = manager;

                        //第四步,展示广告
                        //把返回的view添加到界面控件中
                        rootView.setVisibility(View.VISIBLE);
                        rootView.removeAllViews();
                        if (adView != null) {
                            rootView.addView(adView);
                        }
                    }

                    @Override
                    public void onAdExposure () {
                        Log.e(TAG, "onAdExposure");
                        status.setText(String.format("当前广告状态:%s", "onAdExposure"));
                    }

                    @Override
                    public void fetchMainTitle (String title) {
                        Log.e(TAG, "fetchMainTitle:" + title);
                        status.setText(String.format("当前广告状态:%s", "fetchMainTitle:" + title));
                    }

                    @Override
                    public void onAdSkipClick () {
                        Log.e(TAG, "onAdSkipClick");
                        status.setText(String.format("当前广告状态:%s", "onAdSkipClick"));
                    }

                    @Override
                    public void onFetchAdContentView (
                            TextView adTipView,//左上角 广告 标识
                            LinearLayout skipLayout,//右上角跳过布局
                            TextView mainTitleView,//下方主标题
                            TextView subtitleView,//icon 右侧副标题
                            TextView understandDescView//右下角了解详情
                    ) {
//                        if (subtitleView != null) {
//                            subtitleView.setMaxEms(5);
//                            subtitleView.setMaxLines(1);
//                        }
                    }

                    @Override
                    public void onAdClick () {
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
                        Log.e(TAG, fail);
                        status.setText(String.format("当前广告状态:%s", "onAdError:" + fail));
                    }
                });
    }

    private QcCustomTemplateAttr fetchAdAttr () {
        QcCustomTemplateAttr attr = new QcCustomTemplateAttr();
        //设置封面属性,单位dp,设置MATCH_PARENT 则交由外部容器控制
        QcCustomTemplateAttr.CoverStyle coverStyle = new QcCustomTemplateAttr.CoverStyle();
        coverStyle.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        coverStyle.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        coverStyle.setRadius(50);//设置封面圆角,单位dp
        attr.setCoverStyle(coverStyle);

        //设置广告icon,单位dp
        QcCustomTemplateAttr.IconStyle iconStyle = new QcCustomTemplateAttr.IconStyle();
        iconStyle.setWidth(30);
        iconStyle.setHeight(30);
        iconStyle.setRadius(10);//设置icon圆角,单位dp
        iconStyle.setLayoutGravity(Gravity.BOTTOM);//设置icon位置,具体参见Gravity方法
        iconStyle.setEnableMargin(true);
        iconStyle.setMarginLeft(15);
        iconStyle.setMarginBottom(13);

        attr.setIconStyle(iconStyle);

        attr.setEnableSkip(true);//是否启用右上角跳过,默认启用
        attr.setSkipAutoClose(false);//设置倒计时结束后是否自动关闭广告,默认false
        attr.setSkipTipValue("关闭");//设置右上角跳过文案,默认为跳过

        return attr;
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

        if (isEnableDestroy) {
            //释放内存
            if (QCiVoiceSdk.get() != null) {
                QCiVoiceSdk.get().onDestroy();
            }
        }
    }
}
