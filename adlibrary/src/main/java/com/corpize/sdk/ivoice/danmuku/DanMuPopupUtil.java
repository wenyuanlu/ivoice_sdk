package com.corpize.sdk.ivoice.danmuku;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.corpize.sdk.R;
import com.corpize.sdk.ivoice.danmuku.model.DanMuModel;
import com.corpize.sdk.ivoice.utils.DeviceUtil;
import com.corpize.sdk.ivoice.utils.ImageUtils;
import com.corpize.sdk.ivoice.utils.LogUtils;

/**
 * author ：yh
 * date : 2020-11-24 04:06
 * description : 点击弹幕的弹唱
 */
public class DanMuPopupUtil {

    /**
     * 展示随播的音频广告
     *
     * @param activity
     * @param view      父view
     * @param danMuView 参数
     * @param leftX     view点击时X轴位置左上角
     * @param leftY     view点击时Y的位置左上角
     * @param isCenter  是否居中显示,false的时候,点击位置的显示
     * @param callBack
     */
    public static void showBarrageDialog (Activity activity, View view, DanMuModel danMuView, String avater,
            int leftX, int leftY, boolean isCenter, final OnBarragePopupCallBack callBack) {
        View         contentView = LayoutInflater.from(activity).inflate(R.layout.qcad_barrage_dialog_layout, null, false);
        LinearLayout barrageLl   = (LinearLayout) contentView.findViewById(R.id.qcad_barrage_ll);

        ///动态设置背景颜色和边框
        GradientDrawable drawable = (GradientDrawable) barrageLl.getBackground();
        //drawable.setColor(Color.parseColor("#000000"));
        drawable.setStroke(1, danMuView.textColor);

        //设置TextView
        TextView tvContent = contentView.findViewById(R.id.qcad_barrage_content);
        tvContent.setText(danMuView.text);
        tvContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, danMuView.textSize);
        tvContent.setTextColor(danMuView.textColor);

        //设置imageView
        ImageView ivHead = contentView.findViewById(R.id.qcad_barrage_head);
        if (!TextUtils.isEmpty(avater)) {
            ImageUtils.loadImage(activity, avater, ivHead, R.drawable.qcad_barrage_head);
        } else {
            ivHead.setImageResource(R.drawable.qcad_barrage_head);
        }
        LinearLayout.LayoutParams headParams = (LinearLayout.LayoutParams) ivHead.getLayoutParams();
        headParams.width = danMuView.avatarWidth;
        headParams.height = danMuView.avatarHeight;
        headParams.setMargins(3, 0, 0, 0);
        ivHead.setLayoutParams(headParams);
        ivHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                //头像点击
                if (callBack != null) {
                    callBack.onUserInfo();
                }
            }
        });

        PopupWindow mPopupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(false);
        //mPopupWindow.setClippingEnabled(false);
        mPopupWindow.setBackgroundDrawable(null);
        mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch (View v, MotionEvent event) {
                return false;
            }
        });
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss () {
                if (callBack != null) {
                    callBack.onDismiss();
                }
            }
        });

        //计算view实际的宽高
        int screenWidth = DeviceUtil.getScreenWidth(activity);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth  = contentView.getMeasuredWidth();    //  获取测量后的宽度
        int popupHeight = contentView.getMeasuredHeight();  //获取测量后的高度

        int releayX = leftX;
        int releayY = leftY;

        //判断X轴是左边,中间还是右边
        if (releayX < 0) {
            releayX = 0;
        } else if (screenWidth - releayX >= popupWidth) {
            releayX = leftX;
        } else if (screenWidth - releayX < popupWidth) {
            releayX = screenWidth - popupWidth > 0 ? screenWidth - popupWidth : 0;
        }

        //判断是否中间展示
        if (isCenter) {
            releayX = screenWidth - popupWidth > 0 ? (screenWidth - popupWidth) / 2 : 0;
        }

        //动态设置popup的宽度
        if (screenWidth - popupWidth <= 0) {
            mPopupWindow.setWidth(screenWidth);
            //popupWidth = screenWidth;
        } else {
            mPopupWindow.setWidth(popupWidth);
        }


        //设置动画,中间是缩放,左边右边的是位移动画(根据左上角的坐标,以及长度进行判断)
        if (isCenter) {
            //展示PopupWindow动画
            mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, releayX, releayY);
            AnimationEffect.setScaleAnimation(contentView, screenWidth, 1);
        } else {
            //展示PopupWindow动画
            mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, releayX, releayY);
            int transX = popupWidth;
            if (screenWidth - popupWidth <= 0) {
                transX = screenWidth;
            }
            LogUtils.e("view的宽度=" + popupWidth + "|view的高度=" + popupHeight);
            LogUtils.e("屏幕宽度=" + screenWidth + "|X轴坐标=" + leftX + "|" + releayX);
            boolean isScaleAnimal = false;//是否左右用缩放动画
            if (leftX <= 0) {
                //LogUtils.e("左侧的弹窗-位移");
                if (isScaleAnimal) {
                    AnimationEffect.setScaleAnimation(contentView, screenWidth, 0);
                } else {
                    AnimationEffect.setTransAnimation(contentView, -transX, 0, 0, 0);
                }
            } else if ((screenWidth - leftX) < popupWidth) {
                //LogUtils.e("右侧的弹窗-位移");
                if (isScaleAnimal) {
                    AnimationEffect.setScaleAnimation(contentView, screenWidth, 2);
                } else {
                    AnimationEffect.setTransAnimation(contentView, transX, 0, 0, 0);
                }
            } else {
                //LogUtils.e("中间的弹窗-缩放");
                if (isScaleAnimal) {
                    AnimationEffect.setScaleAnimation(contentView, screenWidth, 1);
                } else {
                    AnimationEffect.setScaleAnimation(contentView, screenWidth, 1);
                }
            }
        }

    }

    public interface OnBarragePopupCallBack {

        void onDismiss ();

        void onUserInfo ();

    }
}
