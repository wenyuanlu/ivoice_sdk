package com.corpize.sdk.ivoice.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.corpize.sdk.R;
import com.corpize.sdk.ivoice.utils.CommonClickWebViewUtils;
import com.corpize.sdk.ivoice.utils.ImageUtils;
import com.corpize.sdk.ivoice.utils.ScreenUtils;

/**
 * author : xpSun
 * date : 1/17/22
 * description :
 */
public class CustomDownloadConfirmDialog {
    private ImageView downloadConfirmIconShow;
    private TextView  downloadConfirmAppName;
    private TextView  downloadConfirmAppVersion;
    private TextView  downloadConfirmAppDeveloper;
    private TextView  downloadConfirmJurisdictionDesc;
    private TextView  downloadConfirmPrivacyPolicy;
    private TextView  downloadConfirmDownload;
    private TextView  downloadConfirmCancel;

    private Dialog   dialog;
    private Activity activity;
    private View     rootView;

    private View.OnClickListener onConfirmClick;

    private String jurisdictionUrl;
    private String privacyUrl;

    public void setOnConfirmClick (View.OnClickListener onConfirmClick) {
        this.onConfirmClick = onConfirmClick;
    }

    public CustomDownloadConfirmDialog (Activity activity) {
        this.activity = activity;
        init();
    }

    private void init () {
        dialog = new Dialog(activity, R.style.common_dialog_style);
        rootView = LayoutInflater.from(activity).inflate(R.layout.view_download_confirm_layout, null, false);
        initWidget(rootView);
        dialog.setContentView(rootView);
        dialog.setCancelable(false);
    }

    private void initWidget (View rootView) {
        downloadConfirmIconShow = rootView.findViewById(R.id.download_confirm_icon_show);
        downloadConfirmAppName = rootView.findViewById(R.id.download_confirm_app_name);
        downloadConfirmAppVersion = rootView.findViewById(R.id.download_confirm_app_version);
        downloadConfirmAppDeveloper = rootView.findViewById(R.id.download_confirm_app_developer);
        downloadConfirmJurisdictionDesc = rootView.findViewById(R.id.download_confirm_jurisdiction_desc);
        downloadConfirmPrivacyPolicy = rootView.findViewById(R.id.download_confirm_privacy_policy);
        downloadConfirmDownload = rootView.findViewById(R.id.download_confirm_download);
        downloadConfirmCancel = rootView.findViewById(R.id.download_confirm_cancel);

        downloadConfirmJurisdictionDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (TextUtils.isEmpty(jurisdictionUrl)) {
                    return;
                }
                CommonClickWebViewUtils.openWebView(activity, jurisdictionUrl);
            }
        });

        downloadConfirmPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (TextUtils.isEmpty(privacyUrl)) {
                    return;
                }
                CommonClickWebViewUtils.openWebView(activity, privacyUrl);
            }
        });

        downloadConfirmDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (onConfirmClick != null) {
                    onConfirmClick.onClick(v);
                }

                dismissDialog();
            }
        });

        downloadConfirmCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                dismissDialog();
            }
        });
    }

    public void setContentValue (
            String iconUrl,
            String appName,
            String appVersion,
            String appDeveloper,
            String jurisdictionUrl,
            String privacyUrl) {
        this.jurisdictionUrl = jurisdictionUrl;
        this.privacyUrl = privacyUrl;

        if (null != activity && !activity.isDestroyed()) {
            ImageUtils.loadImage(activity, iconUrl, downloadConfirmIconShow);
        }

        downloadConfirmAppName.setText(TextUtils.isEmpty(appName) ? "" : appName);
        downloadConfirmAppVersion.setText(String.format("版本号:%s", TextUtils.isEmpty(appVersion) ? "" : appVersion));
        downloadConfirmAppDeveloper.setText(String.format("开发者:%s", TextUtils.isEmpty(appDeveloper) ? "" : appDeveloper));
    }

    public void showDialog () {
        if (dialog != null &&
                null != activity &&
                !activity.isDestroyed()
        ) {
            if (dialog != null && dialog.getWindow() != null) {
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.width = (int) (ScreenUtils.getScreenWidth(dialog.getContext()) * 0.7);
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setAttributes(lp);
            }

            dialog.show();
        }
    }

    public void dismissDialog () {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
