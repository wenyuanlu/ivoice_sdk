package com.corpize.sdk.ivoice.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.corpize.sdk.R;
import com.corpize.sdk.ivoice.utils.ScreenUtils;

/**
 * author : xpSun
 * date : 2021/5/14
 * description :
 */
public class CustomRecorderProgressDialog {

    private static final int HANDLER_MSG_WHAT_0 = 0;
    private static final int HANDLER_MSG_WHAT_1 = 1;
    private static final int HANDLER_MSG_WHAT_2 = 2;
    private static final int HANDLER_MSG_WHAT_3 = 3;
    private static final int HANDLER_MSG_WHAT_4 = 4;

    private ImageView recorderAnimation1;
    private ImageView recorderAnimation2;
    private ImageView recorderAnimation3;
    private ImageView recorderAnimation4;
    private ImageView recorderAnimation5;

    private        Dialog                      dialog;
    private static CustomRecorderProgressDialog instance;

    private CustomRecorderProgressDialog () {}

    public static CustomRecorderProgressDialog getInstance () {
        if (null == instance) {
            instance = new CustomRecorderProgressDialog();
        }
        return instance;
    }

    public CustomRecorderProgressDialog builder (Context context) {
        if (dialog == null) {
            dialog = new Dialog(context, R.style.common_dialog_style);
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.view_custom_recorder_progress_layout,
                            null,
                            false);

            initWidgets(view);
            dialog.setContentView(view);
        }

        dialog.setCancelable(false);
        return instance;
    }

    private void initWidgets (View view) {
        recorderAnimation1 = view.findViewById(R.id.recorder_progress_image_1);
        recorderAnimation2 = view.findViewById(R.id.recorder_progress_image_2);
        recorderAnimation3 = view.findViewById(R.id.recorder_progress_image_3);
        recorderAnimation4 = view.findViewById(R.id.recorder_progress_image_4);
        recorderAnimation5 = view.findViewById(R.id.recorder_progress_image_5);
    }

    private void startAnimation(){
        handler.sendEmptyMessage(HANDLER_MSG_WHAT_0);
        handler.sendEmptyMessageDelayed(HANDLER_MSG_WHAT_1, 80L);
        handler.sendEmptyMessageDelayed(HANDLER_MSG_WHAT_2, 160L);
        handler.sendEmptyMessageDelayed(HANDLER_MSG_WHAT_3, 240L);
        handler.sendEmptyMessageDelayed(HANDLER_MSG_WHAT_4, 320L);
    }

    private void clearAnimation(){
        recorderAnimation1.clearAnimation();
        recorderAnimation2.clearAnimation();
        recorderAnimation3.clearAnimation();
        recorderAnimation4.clearAnimation();
        recorderAnimation5.clearAnimation();
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage (@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_MSG_WHAT_0:
                    initRecorderAnimation(recorderAnimation1,
                            R.drawable.drawable_cylinder_animation_ffb600_shape
                    );
                    break;
                case HANDLER_MSG_WHAT_1:
                    initRecorderAnimation(recorderAnimation2,
                            R.drawable.drawable_cylinder_animation_ff7500_shape
                    );
                    break;
                case HANDLER_MSG_WHAT_2:
                    initRecorderAnimation(recorderAnimation3,
                            R.drawable.drawable_cylinder_animation_d551ac_shape
                    );
                    break;
                case HANDLER_MSG_WHAT_3:
                    initRecorderAnimation(recorderAnimation4,
                            R.drawable.drawable_cylinder_animation_005ee1_shape
                    );
                    break;
                case HANDLER_MSG_WHAT_4:
                    initRecorderAnimation(recorderAnimation5,
                            R.drawable.drawable_cylinder_animation_00f4cf_shape
                    );
                    break;
                default:
                    break;
            }
        }
    };

    private void initRecorderAnimation (ImageView imageView, int resource) {
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(resource);

        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1f, 1f, 0.8f, 1.2f,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        scaleAnimation.setDuration(400);
        scaleAnimation.setRepeatCount(-1);
        scaleAnimation.setRepeatMode(Animation.REVERSE);

        imageView.startAnimation(scaleAnimation);
    }

    public void showProgressDialog () {
        try {
            if (dialog != null) {

                if(dialog.isShowing()){
                    dialog.dismiss();
                }
                dialog.show();
            }

            if (dialog != null && dialog.getWindow() != null) {
                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
                lp.width = (int) (ScreenUtils.getScreenWidth(dialog.getContext()) * 0.9);
                lp.height = ScreenUtils.dp2px(dialog.getContext(), 260);
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
