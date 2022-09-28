package com.corpize.sdk.ivoice.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.corpize.sdk.R;
import com.corpize.sdk.ivoice.utils.ScreenUtils;

/**
 * author : xpSun
 * date : 2021/5/13
 * description :
 */
public class CustomLoadingProgressDialog {

    private static final int HANDLER_MSG_WHAT_0 = 0;
    private static final int HANDLER_MSG_WHAT_1 = 1;
    private static final int HANDLER_MSG_WHAT_2 = 2;
    private static final int HANDLER_MSG_WHAT_3 = 3;
    private static final int HANDLER_MSG_WHAT_4 = 4;

    private ImageView loadingAnimation1;
    private ImageView loadingAnimation2;
    private ImageView loadingAnimation3;
    private ImageView loadingAnimation4;
    private ImageView loadingAnimation5;

    private        Dialog                      dialog;
    private static CustomLoadingProgressDialog instance;

    private CustomLoadingProgressDialog () {

    }

    public static CustomLoadingProgressDialog getInstance () {
        if (null == instance) {
            instance = new CustomLoadingProgressDialog();
        }
        return instance;
    }

    public CustomLoadingProgressDialog builder (Context context) {
        if (dialog == null) {
            dialog = new Dialog(context, R.style.common_dialog_style);
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.view_custom_loading_progress_layout,
                            null,
                            false);

            initWidgets(view);
            dialog.setContentView(view);
        }

        dialog.setCancelable(false);
        return instance;
    }

    private void initWidgets (View view) {
        loadingAnimation1 = view.findViewById(R.id.loading_progress_image_1);
        loadingAnimation2 = view.findViewById(R.id.loading_progress_image_2);
        loadingAnimation3 = view.findViewById(R.id.loading_progress_image_3);
        loadingAnimation4 = view.findViewById(R.id.loading_progress_image_4);
        loadingAnimation5 = view.findViewById(R.id.loading_progress_image_5);
    }

    private void startAnimation(){
        handler.sendEmptyMessage(HANDLER_MSG_WHAT_0);
        handler.sendEmptyMessageDelayed(HANDLER_MSG_WHAT_1, 80L);
        handler.sendEmptyMessageDelayed(HANDLER_MSG_WHAT_2, 160L);
        handler.sendEmptyMessageDelayed(HANDLER_MSG_WHAT_3, 240L);
        handler.sendEmptyMessageDelayed(HANDLER_MSG_WHAT_4, 320L);
    }

    private void clearAnimation(){
        loadingAnimation1.clearAnimation();
        loadingAnimation2.clearAnimation();
        loadingAnimation3.clearAnimation();
        loadingAnimation4.clearAnimation();
        loadingAnimation5.clearAnimation();
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage (@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_MSG_WHAT_0:
                    initLoadingAnimation(loadingAnimation1,
                            R.drawable.drawable_circular_animation_ffb600_shape
                    );
                    break;
                case HANDLER_MSG_WHAT_1:
                    initLoadingAnimation(loadingAnimation2,
                            R.drawable.drawable_circular_animation_ff7500_shape
                    );
                    break;
                case HANDLER_MSG_WHAT_2:
                    initLoadingAnimation(loadingAnimation3,
                            R.drawable.drawable_circular_animation_d551ac_shape
                    );
                    break;
                case HANDLER_MSG_WHAT_3:
                    initLoadingAnimation(loadingAnimation4,
                            R.drawable.drawable_circular_animation_005ee1_shape
                    );
                    break;
                case HANDLER_MSG_WHAT_4:
                    initLoadingAnimation(loadingAnimation5,
                            R.drawable.drawable_circular_animation_00f4cf_shape
                    );
                    break;
                default:
                    break;
            }
        }
    };

    private void initLoadingAnimation (ImageView imageView, int resource) {
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(resource);

        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0.5f, 1.5f, 0.5f, 1.5f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        scaleAnimation.setDuration(400);
        scaleAnimation.setRepeatCount(-1);
        scaleAnimation.setRepeatMode(Animation.REVERSE);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);

        alphaAnimation.setDuration(400);
        alphaAnimation.setRepeatCount(-1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);

        imageView.startAnimation(animationSet);
    }

    public void showProgressDialog () {
        try {
            if (dialog != null) {
                dialog.show();
            }

            if (dialog != null && dialog.getWindow() != null) {
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.width = (int) (ScreenUtils.getScreenWidth(dialog.getContext()) * 0.9);
                lp.height = ScreenUtils.dp2px(dialog.getContext(), 180);
                dialog.getWindow().setAttributes(lp);
            }

            startAnimation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissProgressDialog () {
        try {
            if (dialog != null) {
                dialog.dismiss();
                dialog =null;
                handler.removeCallbacksAndMessages(null);
                clearAnimation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
