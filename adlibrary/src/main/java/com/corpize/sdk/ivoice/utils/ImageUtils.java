package com.corpize.sdk.ivoice.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

/**
 * author : xpSun
 * date : 4/28/21
 * description :
 */
public class ImageUtils {

    public static void loadImage (
            Context context,
            String filePath,
            ImageView imageView) {
        if (checkActivityDestroy(context)) return;
        Glide.with(context)
                .load(filePath)
                .into(imageView);
    }

    public static void loadImage (
            Context context,
            String filePath,
            ImageView imageView,
            int defaultImage) {
        if (checkActivityDestroy(context)) return;
        Glide.with(context)
                .load(filePath)
                .error(defaultImage)
                .into(imageView);
    }

    private static boolean checkActivityDestroy (Context context) {
        if (context instanceof Activity) {
            return ContextUtil.isDestroy((Activity) context);
        }
        return false;
    }

    public static File downLoad (
            Context context,
            String filePath) {
        File file = null;
        try {
            file = Glide.with(context)
                    .load(filePath)
                    .downloadOnly(100, 100)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }
}
