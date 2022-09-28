package com.example.qcaudioad.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.corpize.sdk.ivoice.AdAttr;
import com.corpize.sdk.ivoice.QCiVoiceSdk;
import com.corpize.sdk.ivoice.admanager.QcAdManager;
import com.corpize.sdk.ivoice.listener.AudioQcAdListener;
import com.example.qcaudioad.BuildConfig;
import com.example.qcaudioad.R;
import com.example.qcaudioad.base.BaseActivity;
import com.example.qcaudioad.common.ADIDConstants;
import com.example.qcaudioad.utils.PermissionUtil;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private final String   TAG = "MainActivity";
    private       TextView mTvGotoCustom;
    private       TextView mTvInitDelay;
    private       TextView mTvInit;
    private       TextView mFirstVoice;
    private       TextView mVoice;
    private       TextView mCustomTemplate;
    private       TextView mRollAd;
    private       TextView mViewPager;

    private static final int      PERMISSION_RECORD_CODE  = 1001;
    private static final String[] PERMISSION_RECORD_WRITE = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO
    };//读写,状态,定位权限

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intiView();
        initData();
        checkPermission(this);
    }

    private void intiView () {
        mTvInit = findViewById(R.id.tv_init);
        mTvInitDelay = findViewById(R.id.tv_init_delay);
        TextView mTvInsetCustom = findViewById(R.id.tv_inset_custom);
        mTvGotoCustom = findViewById(R.id.tv_goto_custom);
        mFirstVoice = findViewById(R.id.tv_first_voice);
        mVoice = findViewById(R.id.tv_voice_custom);
        mCustomTemplate = findViewById(R.id.tv_custom_template);
        mRollAd = findViewById(R.id.tv_roll_ad);
        mViewPager = findViewById(R.id.tv_custom_viewpager_auto_player);

        mTvInit.setOnClickListener(this);
        mTvInitDelay.setOnClickListener(this);
        mTvInsetCustom.setOnClickListener(this);
        mTvGotoCustom.setOnClickListener(this);
        mFirstVoice.setOnClickListener(this);
        mVoice.setOnClickListener(this);
        mCustomTemplate.setOnClickListener(this);
        mRollAd.setOnClickListener(this);
        mViewPager.setOnClickListener(this);
    }

    private void initData () {
        //如果要使用弹幕功能,可以选择传递当前用户的userId和头像地址,sdk会在弹幕点击的时候,展示头像,返回userId,
        //方便后续客户端添加自定的用户界面的跳转操作.
        setAdUserInfo("10000", "http://resource.corpize.com/image/shufujia_202.jpeg");
    }

    @Override
    public boolean showLeftGoBackView () {
        return false;
    }

    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.tv_init:
                initDialogVoice();
                break;
            case R.id.tv_init_delay:
                initDelayVoice();
                break;
            case R.id.tv_inset_custom://信息流
                startAdActivity(CustomInfoActivity.class);
                break;
            case R.id.tv_goto_custom://自定义嵌入式(列表)
                startAdActivity(CustomListActivity.class);
                break;
            case R.id.tv_first_voice://首听
                startAdActivity(QCFirstVoiceActivity.class);
                break;
            case R.id.tv_voice_custom://冠名
                startAdActivity(CustomVoiceActivity.class);
                break;
            case R.id.tv_custom_template://自定义模板
                startAdActivity(CustomTemplateActivity.class);
                break;
            case R.id.tv_roll_ad:
                startAdActivity(CustomRollAdActivity.class);
                break;
            case R.id.tv_custom_viewpager_auto_player:
                startAdActivity(CustomAutoRotationAdStyleActivity.class);
                break;
            default:
                break;
        }
    }

    private void startAdActivity (Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    //获取广告
    private void initDialogVoice () {
        //第一步,设置广告的adId
        String initADID = QCiVoiceSdk.get().isDebug() ? ADIDConstants.TestEnum.INFO_ADID : ADIDConstants.ReleaseEnum.DIALOG_ADID;
        AdAttr adAttr   = AdAttr.newBuild().setAdid(initADID);
        //第二步,创建广告调用,必须调用
        QCiVoiceSdk.get().createAdNative(this);
        //第三步,获取广告
        QCiVoiceSdk.get().addAudioAd(adAttr, new AudioQcAdListener() {
            @Override
            public void onAdReceive (QcAdManager manager) {
                Log.e(TAG, "onAdReceive");
                //第四步,展示广告
                if (manager != null) {
                    manager.startPlayAd();
                }
            }

            @Override
            public void onAdExposure () {
                Log.e(TAG, "onAdExposure");
            }

            @Override
            public void onAdClick () {
                Log.e(TAG, "onAdClick");
            }

            @Override
            public void onAdCompletion () {
                Log.e(TAG, "onAdCompletion");
            }

            @Override
            public void onAdClose () {
                Log.e(TAG, "onAdClose");
            }

            @Override
            public void onAdError (String fail) {
                Log.e(TAG, "onAdError=" + fail);
            }
        });
    }

    //延迟获取广告
    private void initDelayVoice () {
        new Handler().postDelayed(new Runnable() {
            public void run () {
                initDialogVoice();
            }
        }, 3000);
    }

    /**
     * 传递用户的信息,用于弹幕的点击操作
     *
     * @param userId
     * @param avater
     */
    private void setAdUserInfo (String userId, String avater) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        map.put("avatar", avater);
        if (QCiVoiceSdk.get() != null) {
            QCiVoiceSdk.get().setUserInfo(map);
        }
    }

    /**
     * 检查权限 开启广告
     */
    public static void checkPermission (Context context) {
        PermissionUtil.checkAndRequestMorePermissions(context, PERMISSION_RECORD_WRITE, PERMISSION_RECORD_CODE,
                new PermissionUtil.PermissionRequestSuccessCallBack() {
                    @Override
                    public void onHasPermission () {

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

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (inflater != null) {
            inflater.inflate(R.menu.menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item) {
        if (item.getItemId() == R.id.simple_about) {
            showDialog(TextUtils.isEmpty(item.getTitle()) ? "提示" : item.getTitle().toString());
            return true;
        }else if (item.getItemId() == R.id.simple_setting) {
            startAdActivity(SettingActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog (String title) {
        String  version = QCiVoiceSdk.get().getSdkVersion();
        boolean isDebug = QCiVoiceSdk.get().isDebug();

        showDialog(title, version, isDebug);
    }

    private void showDialog (String title, String version, boolean isDebug) {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
        normalDialog.setTitle(title);
        normalDialog.setMessage(String.format(
                "当前sdk版本: %s \n当前sdk环境: %s \n当前simple版本%s",
                version, isDebug ? "测试" : "正式", BuildConfig.VERSION_NAME
        ));
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick (DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // 显示
        normalDialog.show();
    }
}
