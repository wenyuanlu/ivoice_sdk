package com.corpize.sdk.ivoice.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.corpize.sdk.ivoice.admanager.QcAdDetailActivity;

/**
 * author : xpSun
 * date : 12/23/21
 * description :
 */
public class CommonClickWebViewUtils {

    public static void openCommonExternal (
            Activity activity,
            String ldp,
            int mPosition
    ) {
        try {
            if (TextUtils.isEmpty(ldp)) {
                return;
            }

            Uri    uri    = Uri.parse(ldp);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivityForResult(intent, mPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean openCommonExternal (
            Activity activity,
            String ldp
    ) {
        try {
            if (TextUtils.isEmpty(ldp)) {
                return false;
            }

            Uri    uri    = Uri.parse(ldp);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void openExternal (
            Activity activity,
            String ldp
    ) {
        try {
            if (TextUtils.isEmpty(ldp)) {
                return;
            }

            Uri    uri    = Uri.parse(ldp);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openWebView (
            Activity activity,
            String ldp,
            int mPosition
    ) {
        try {
            if (TextUtils.isEmpty(ldp)) {
                return;
            }

            Intent intent = new Intent(activity, QcAdDetailActivity.class);
            intent.putExtra("url", ldp);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivityForResult(intent, mPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openWebView (
            Activity activity,
            String ldp
    ) {
        try {
            if (TextUtils.isEmpty(ldp)) {
                return;
            }

            Intent intent = new Intent(activity, QcAdDetailActivity.class);
            intent.putExtra("url", ldp);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
