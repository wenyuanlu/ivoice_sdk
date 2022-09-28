package com.corpize.sdk.ivoice.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.corpize.sdk.R;
import com.corpize.sdk.ivoice.admanager.QcAdDetailActivity;
import com.corpize.sdk.ivoice.bean.response.AdAudioBean;
import com.corpize.sdk.ivoice.bean.response.EventtrackersBean;
import com.corpize.sdk.ivoice.common.CommonUtils;
import com.corpize.sdk.ivoice.common.Constants;
import com.corpize.sdk.ivoice.listener.AudioQcAdListener;
import com.corpize.sdk.ivoice.listener.CountDownCallback;
import com.corpize.sdk.ivoice.listener.DialogCallback;
import com.corpize.sdk.ivoice.listener.DialogSizeCallback;
import com.corpize.sdk.ivoice.listener.EditDialogCallback;
import com.corpize.sdk.ivoice.utils.downloadinstaller.DonwloadSaveImg;
import com.corpize.sdk.ivoice.utils.downloadinstaller.DownloadInstaller;
import com.corpize.sdk.ivoice.utils.downloadinstaller.DownloadProgressCallBack;
import com.corpize.sdk.ivoice.video.CustomCountDownTimer;
import com.corpize.sdk.ivoice.video.ThirdAppUtils;
import com.qichuang.annotation.Nullable;
import com.qichuang.annotation.RequiresApi;

import java.util.List;

/**
 * author ：yh
 * date : 2020-11-24 04:06
 * description : 插屏弹窗
 */
public class DialogUtils {

    private static Dialog               mInsertDialog;
    private static Dialog               mWebDialog;
    private static Dialog               mEditTextDialog;
    private static boolean              mHaveClick        = false;      //是否点击过
    private static boolean              mHaveExposure     = false;      //是否曝光
    private static boolean              mHaveDeepExposure = false;      //是否deep曝光
    private static boolean              mHaveDownStart    = false;      //是否发送开始下载曝光请求
    private static boolean              mHaveDownComplete = false;      //是否发送完成下载曝光请求
    private static boolean              mHaveDownInstall  = false;      //是否发送开始安装曝光请求
    private static float                mClickDownX;                    //企创 点击位置X
    private static float                mClickDownY;                    //企创 点击位置Y
    private static float                mClickUpX;                      //企创 点击位置X
    private static float                mClickUpY;                      //企创 点击位置Y
    private static float                mPositionX;                     //企创 左上角X
    private static float                mPositionY;                     //企创 左上角Y
    private static CustomCountDownTimer mSkipCountDown;                 //跳过的倒计时
    private static CustomCountDownTimer mNoInteractiveCountDown;        //无互动的界面消失倒计时
    private static CustomCountDownTimer mHaveInteractiveCountDown;      //有互动的界面消失倒计时

    /**
     * 展示随播的音频广告(没有倒计时)
     */
    public static void showImageWithOutDialog (final Activity activity, final AdAudioBean responseBean,
                                               final AudioQcAdListener listener, final DialogSizeCallback callBack, final DialogCallback dialogCallback) {
        showImageDialog(activity, responseBean, 0, 0, listener, callBack, dialogCallback);
    }


    /**
     * 展示随播的音频广告
     * allTime 广告音频总时长
     * interactiveTime 互动的音频时长
     */
    public static void showImageDialog (final Activity activity, final AdAudioBean responseBean,
                                        final int allTime, final int interactiveTime, final AudioQcAdListener listener,
                                        final DialogSizeCallback callBack, final DialogCallback dialogCallback) {
        if (mInsertDialog != null && mInsertDialog.isShowing()) {
            mInsertDialog.dismiss();
        }
        //结束倒计时
        closeCountDownTime();

        //重置数据
        mHaveClick = false;
        mHaveExposure = false;
        mHaveDeepExposure = false;
        mHaveDownStart = false;
        mHaveDownComplete = false;
        mHaveDownInstall = false;
        mPositionX = 0;
        mPositionY = 0;

        mInsertDialog = new Dialog(activity, R.style.QcNoBackGroundDialog);
        mInsertDialog.setContentView(R.layout.qcad_image_dialog_layout);

        //获取控件
        RelativeLayout rl         = mInsertDialog.findViewById(R.id.qcad_image_rl);
        ImageView      adIcon     = mInsertDialog.findViewById(R.id.qcad_image_ad);
        ImageView      close      = mInsertDialog.findViewById(R.id.qcad_image_close);
        ImageView      adShow     = mInsertDialog.findViewById(R.id.qcad_ad_icon);
        TextView       tvDownTime = DialogUtils.mInsertDialog.findViewById(R.id.qcad_image_down_time);

        if (responseBean != null && !TextUtils.isEmpty(responseBean.getLdp())) {
            int       screenWidth  = DeviceUtil.getScreenWidth(activity);
            int       screenHeight = DeviceUtil.getRealyScreenHeight(activity);
            final int myWidth      = screenWidth * 7 / 10;
            final int myHeight     = responseBean.getWidth() == 0 ? 0 : myWidth * responseBean.getHeight() / responseBean.getWidth();
            //设置控件的大小
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) adIcon.getLayoutParams();
            int                         thisDp = DeviceUtil.dip2px(activity, 5);
            params.width = myWidth + thisDp;
            params.height = myHeight + thisDp;

            RelativeLayout.LayoutParams paramBs = (RelativeLayout.LayoutParams) rl.getLayoutParams();
            int                         otherDp = DeviceUtil.dip2px(activity, 17);
            paramBs.width = myWidth + otherDp;
            paramBs.height = myHeight + otherDp;
            paramBs.addRule(RelativeLayout.CENTER_VERTICAL);

            adIcon.setLayoutParams(params);
            rl.setLayoutParams(paramBs);

            if (callBack != null) {
                callBack.getSize(myWidth + otherDp, myHeight + otherDp);
                int positionX = (screenWidth - myWidth - otherDp) / 2;
                int positionY = (screenHeight - myHeight - otherDp) / 2;
                mPositionX = positionX;
                mPositionY = positionY;
                callBack.getLeftPosition(positionX, positionY);
            }

            //显示图片
            if (null != responseBean.getCompanion() && !TextUtils.isEmpty(responseBean.getCompanion().getUrl())) {
                ImageUtils.loadImage(activity, responseBean.getCompanion().getUrl(), adIcon);
            } else if (responseBean.getFirstimg() != null) {
                ImageUtils.loadImage(activity, responseBean.getFirstimg(), adIcon);
            }

            //广告标识的展示
            ImageUtils.loadImage(activity, Constants.AD_ICON, adShow);
            //点击事件位置监听
            getClickPosition(adIcon);

            //广告曝光
            if (!mHaveExposure) {
                mHaveExposure = true;
                sendShowExposure(responseBean.getImps(), myWidth, myHeight);
            }

            //点击事件
            adIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    try {
                        if (listener != null) {
                            listener.onAdClick();
                        }

                        //Ext 广告位点击
                        if (!mHaveClick) {
                            mHaveClick = true;
                            sendClickExposure(responseBean.getClks(), myWidth, myHeight);
                        }

                        setClick(activity, responseBean, myWidth, myHeight);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            //跳过倒计时
            skipCountDownTime(responseBean, close, tvDownTime, allTime, interactiveTime);
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                DialogUtils.mInsertDialog.dismiss();
                closeCountDownTime();
                MediaPlayerUtil.getInstance().stopAndRelease();
                MediaPlayerUtil.getInstance().releaseVolumeBack();
                if (listener != null) {
                    listener.onAdClose();
                }
            }
        });

        mInsertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss (DialogInterface dialog) {
                dialogCallback.dialogDismiss();
            }
        });

        DialogUtils.mInsertDialog.setCancelable(false);
        DialogUtils.mInsertDialog.show();

    }

    /**
     * 展示WebView的弹窗
     * type 1是自定义的时候,音频暂停,2是弹窗,音频停止
     */
    public static void showWebDialog (Activity activity, String url) {
        showWebDialog(activity, url, 1, null);
    }

    /**
     * 展示WebView的弹窗
     * type 1是自定义的时候,音频暂停,2是弹窗,音频停止
     */
    public static void showWebDialog (Activity activity, String url, int type) {
        showWebDialog(activity, url, type, null);
    }

    public static void showWebDialog (final Activity activity, String url, final DialogCallback callBack) {
        showWebDialog(activity, url, 1, callBack);
    }

    /**
     * 展示WebView的弹窗
     * type 1是自定义的时候,音频暂停,2是弹窗,音频停止
     */
    public static void showWebDialog (final Activity activity, String url, int type, final DialogCallback callBack) {
        if (mWebDialog != null && mWebDialog.isShowing()) {
            mWebDialog.dismiss();
        }
        //结束倒计时
        //closeCountDownTime();
        if (type == 1) {
            MediaPlayerUtil.getInstance().pausePlay();
        } else if (type == 2) {
            MediaPlayerUtil.getInstance().stopAndRelease();
        }

        mWebDialog = new Dialog(activity, R.style.QcDialog);
        mWebDialog.setContentView(R.layout.qcad_webview_dialog_layout);

        ImageView    close   = mWebDialog.findViewById(R.id.qcad_web_close);
        LinearLayout ll      = mWebDialog.findViewById(R.id.qcad_web_ll);
        WebView      webView = mWebDialog.findViewById(R.id.qcad_web);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                DialogUtils.mWebDialog.dismiss();
                if (callBack != null) {
                    callBack.dialogDismiss();
                } else {
                    MediaPlayerUtil.getInstance().resumePlay();
                }
            }
        });

        //设置控件的大小
        int       screenWidth = DeviceUtil.getScreenWidth(activity);
        final int myWidth     = screenWidth * 10 / 10;
        final int myHeight    = myWidth;

        LinearLayout.LayoutParams webParam = (LinearLayout.LayoutParams) webView.getLayoutParams();
        int                       otherDp  = DeviceUtil.dip2px(activity, 50);
        webParam.width = myWidth;
        webParam.height = LinearLayout.LayoutParams.WRAP_CONTENT;

        //设置控件的大小
        //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ll.getLayoutParams();
        //int                         thisDp = DeviceUtil.dip2px(activity, 50);
        //params.width = myWidth;
        //params.height = myHeight + thisDp;

        //ll.setLayoutParams(params);
        webView.setLayoutParams(webParam);

        //TODO:WebView的配置
        webView.setBackgroundColor(0); // 设置背景色
        webView.getBackground().setAlpha(0); // 设置填充透明度 范围：0-255
        //属性设置
        WebSettings settings = webView.getSettings();
        settings.setAllowFileAccess(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//适应屏幕，内容将自动缩放
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setDatabaseEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setJavaScriptEnabled(true);

        // webview在安卓5.0之前默认允许其加载混合网络协议内容
        // 在安卓5.0之后，默认不允许加载http与https混合内容，需要设置webview允许其加载混合网络协议内容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setVerticalScrollBarEnabled(false);//设置不显示滚动条
        webView.setHorizontalScrollBarEnabled(false);
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        //JS回调方法,需要自定方法的数据
        webView.addJavascriptInterface(new JavaMethod(activity), "android");
        //处理各种通知、请求时间的
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();  // 接受所有网站的证书  解决https拦截问题
            }

            @Override
            public void onPageStarted (WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished (final WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest (WebView view, WebResourceRequest request) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    String url = request.getUrl().getPath();
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest (WebView view, String url) {
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading (WebView view, String url) {
                //调用webview本身的loadUrl方法
                view.loadUrl(url);
                return true;
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading (WebView view, WebResourceRequest request) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    String url = request.getUrl().getPath();
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        webView.loadUrl(url);

        DialogUtils.mWebDialog.setCancelable(false);
        DialogUtils.mWebDialog.show();
    }

    /**
     * 展示输入框的弹窗
     */
    public static void showEditDialog (final Activity activity, final EditDialogCallback callBack) {
        if (mEditTextDialog != null && mEditTextDialog.isShowing()) {
            mEditTextDialog.dismiss();
        }

        mEditTextDialog = new Dialog(activity, R.style.QcNoBackGroundDialog);
        mEditTextDialog.setContentView(R.layout.qcad_edit_dialog_layout);
        mEditTextDialog.setCanceledOnTouchOutside(true);
        mEditTextDialog.setCancelable(true);

        Window window = mEditTextDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);//dialog底部弹出
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

        final int      MAX_NUM   = 20;
        final EditText mEditText = mEditTextDialog.findViewById(R.id.qcad_edit);
        TextView       mSendBt   = mEditTextDialog.findViewById(R.id.qcad_edit_send);
        new Handler().postDelayed(new Runnable() {
            public void run () {
                mEditText.requestFocus();
                KeyBoardUtils.showSystemKeyBoard(activity, mEditText);
            }
        }, 100);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged (Editable s) {
                if (s.length() > MAX_NUM) {
                    s.delete(MAX_NUM, s.length());
                }
            }
        });

        mSendBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (!TextUtils.isEmpty(mEditText.getText().toString())) {
                    KeyBoardUtils.hideSystemKeyBoard(activity, mEditText);
                }

                if (callBack != null) {
                    callBack.sendMsg(mEditText.getText().toString());
                }
            }
        });

        mEditTextDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey (DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    return false;
                } else if (keyCode == KeyEvent.KEYCODE_DEL) {//删除键
                    return false;
                } else {
                    return true;
                }
            }
        });

        mEditTextDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss (DialogInterface dialog) {
                KeyBoardUtils.closeBoard(activity);
                if (callBack != null) {
                    callBack.inputDismiss();
                }
            }
        });
        mEditTextDialog.show();
    }

    /**
     * 输入框的弹窗的消失
     */
    public static void dismissEditDialog () {
        if (mEditTextDialog != null && mEditTextDialog.isShowing()) {
            mEditTextDialog.dismiss();
        }
    }

    /**
     * 无互动弹窗消失的倒计时
     *
     * @param intervalTime
     * @param listener
     */
    public static void noInteractiveCountDownTime (int intervalTime, final AudioQcAdListener listener) {
        noInteractiveCountDownTime(intervalTime, true, listener, null);
    }

    /**
     * 无互动弹窗消失的倒计时
     *
     * @param intervalTime
     * @param isClose      是否关闭弹窗
     * @param listener
     */
    public static void noInteractiveCountDownTime (int intervalTime, final boolean isClose,
                                                   final AudioQcAdListener listener, final CountDownCallback callBack) {
        if (mNoInteractiveCountDown != null) {
            mNoInteractiveCountDown.cancel();
            mNoInteractiveCountDown = null;
        }
        mNoInteractiveCountDown = new CustomCountDownTimer(intervalTime * 1000, 1000) {
            @Override
            public void onTick (long millisUntilFinished) {
            }

            @Override
            public void onFinish () {
                LogUtils.d("无互动的弹窗消失");
                if (isClose) {
                    if (mInsertDialog != null && mInsertDialog.isShowing()) {
                        mInsertDialog.dismiss();
                        if (listener != null) {
                            listener.onAdClose();
                        }
                    }
                }

                closeCountDownTime();

                if (callBack != null) {
                    callBack.close();
                }

                if (mNoInteractiveCountDown != null) {
                    mNoInteractiveCountDown.cancel();
                    mNoInteractiveCountDown = null;
                }
            }
        };
        mNoInteractiveCountDown.start();
    }

    /**
     * 清理无互动的倒计时
     */
    public static void closeNoInteractiveCountDownTime () {
        try {
            if (mNoInteractiveCountDown != null) {
                mNoInteractiveCountDown.cancel();
                mNoInteractiveCountDown = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳过的倒计时
     *
     * @param responseBean
     * @param close
     * @param tvDownTime
     * @param allTime      音频实际总时长
     */
    private static void skipCountDownTime (AdAudioBean responseBean, final ImageView close,
                                           final TextView tvDownTime, int allTime, int interactiveTime) {
        final int skipTime     = responseBean.getSkip();
        int       durationTime = responseBean.getDuration();
        if (durationTime > allTime) {
            durationTime = allTime;
        }
        final int countDownTime = durationTime + interactiveTime;
        //当没有音频,没有互动的时候,显示关闭按钮
        if (countDownTime <= 0) {
            close.setVisibility(View.VISIBLE);
            tvDownTime.setVisibility(View.INVISIBLE);
            return;
        }
        //总时长大于0,说明有主体的广告,执行跳过方法
        if (allTime > 0) {
            if (skipTime > 0) {
                close.setVisibility(View.INVISIBLE);
            } else {
                close.setVisibility(View.VISIBLE);
            }
        } else {
            //总时长小于0,无主体广告,不需要跳过的关闭隐藏
            close.setVisibility(View.VISIBLE);
        }

        mSkipCountDown = new CustomCountDownTimer(countDownTime * 1000, 1000) {
            @Override
            public void onTick (long millisUntilFinished) {
                long time = 0;
                if (millisUntilFinished > 0) {
                    time = millisUntilFinished / 1000;
                    if (millisUntilFinished % 1000 > 0) {
                        time = time + 1;
                    }
                }
                LogUtils.d("时间=" + time + " 原时间=" + millisUntilFinished);
                tvDownTime.setText(time + "s");
                if (countDownTime - time > skipTime) {
                    close.setVisibility(View.VISIBLE);
                } else {
                    //close.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFinish () {
                close.setVisibility(View.VISIBLE);
                tvDownTime.setVisibility(View.INVISIBLE);
                if (mSkipCountDown != null) {
                    mSkipCountDown.cancel();
                    mSkipCountDown = null;
                }
            }
        };
        mSkipCountDown.start();
    }


    /**
     * 清理多个倒计时
     */
    public static void closeCountDownTime () {
        if (mSkipCountDown != null) {
            mSkipCountDown.cancel();
            mSkipCountDown = null;
        }

        if (mNoInteractiveCountDown != null) {
            mNoInteractiveCountDown.cancel();
            mNoInteractiveCountDown = null;
        }

        if (mHaveInteractiveCountDown != null) {
            mHaveInteractiveCountDown.cancel();
            mHaveInteractiveCountDown = null;
        }
    }

    /**
     * 获取点击时的x,y轴坐标 onTouch()事件
     * 注意返回值
     * true： view继续响应Touch操作；
     * false：view不再响应Touch操作，故此处若为false，只能显示起始位置，不能显示实时位置和结束位置
     */
    private static void getClickPosition (View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch (View view, MotionEvent event) {
                switch (event.getAction()) {
                    //点击的开始位置
                    case MotionEvent.ACTION_DOWN:
                        //LogUtils.e("起始位置：(" + event.getX() + "," + event.getY());
                        mClickDownX = event.getX();
                        mClickDownY = event.getY();
                        break;

                    //触屏实时位置
                    case MotionEvent.ACTION_MOVE:
                        //LogUtils.e("实时位置：(" + event.getX() + "," + event.getY());
                        break;

                    //离开屏幕的位置
                    case MotionEvent.ACTION_UP:
                        //LogUtils.e("结束位置：(" + event.getX() + "," + event.getY());
                        //mClickUpX = event.getX();
                        //mClickUpY = event.getY();
                        break;

                    default:
                        break;
                }

                return false;
            }
        });

    }

    /**
     * 发送曝光,计算了宽高及时间戳
     */
    private static void sendShowExposure (List<String> imgList, int width, int height) {
        long time = System.currentTimeMillis();

        if (imgList != null && imgList.size() > 0) {
            for (int i = 0; i < imgList.size(); i++) {
                String urlOld = imgList.get(i);
                String url    = urlOld;
                if (url.contains("__WIDTH__")) {//宽度替换
                    url = url.replace("__WIDTH__", width + "");
                }
                if (url.contains("__HEIGHT__")) {//高度替换
                    url = url.replace("__HEIGHT__", height + "");
                }
                if (url.contains("__POSITION_X__")) {//抬起X轴的替换
                    url = url.replace("__POSITION_X__", mPositionX + "");
                }
                if (url.contains("__POSITION_Y__")) {//抬起Y轴的替换
                    url = url.replace("__POSITION_Y__", mPositionY + "");
                }
                if (url.contains("__TIME_STAMP__")) {//时间戳的替换
                    url = url.replace("__TIME_STAMP__", time + "");
                }

                QcHttpUtil.sendAdExposure(url);
            }
        }
    }

    /**
     * 广告位点击请求(企创广告)
     */
    private static void sendClickExposure (final List<String> list, int width, int height) {
        if (list != null && list.size() > 0) {
            long time = System.currentTimeMillis();

            for (int i = 0; i < list.size(); i++) {
                String urlOld = list.get(i);
                String url    = urlOld;
                if (url.contains("__DOWN_X__")) {//点击X轴的替换
                    url = url.replace("__DOWN_X__", mClickDownX + "");
                }
                if (url.contains("__DOWN_Y__")) {//点击Y轴的替换
                    url = url.replace("__DOWN_Y__", mClickDownY + "");
                }
                if (url.contains("__UP_X__")) {//抬起X轴的替换
                    url = url.replace("__UP_X__", mClickDownX + "");
                }
                if (url.contains("__UP_Y__")) {//抬起Y轴的替换
                    url = url.replace("__UP_Y__", mClickDownY + "");
                }
                if (url.contains("__WIDTH__")) {//宽度替换
                    url = url.replace("__WIDTH__", width + "");
                }
                if (url.contains("__HEIGHT__")) {//高度替换
                    url = url.replace("__HEIGHT__", height + "");
                }
                if (url.contains("__POSITION_X__")) {//抬起X轴的替换
                    url = url.replace("__POSITION_X__", mPositionX + "");
                }
                if (url.contains("__POSITION_Y__")) {//抬起Y轴的替换
                    url = url.replace("__POSITION_Y__", mPositionY + "");
                }
                if (url.contains("__TIME_STAMP__")) {//时间戳的替换
                    url = url.replace("__TIME_STAMP__", time + "");
                }

                QcHttpUtil.sendAdExposure(url);
            }
        }
    }

    /**
     * 点击事件
     */
    private static void setClick (Activity activity, AdAudioBean bean, final int width, final int height) {
        if (bean == null) {
            return;
        }
        int    action = bean.getAction();
        String url    = bean.getLdp();
        if (0 == action) {          // 0 - 未确认
        } else if (1 == action) {   // 1 - App webview 打开链接
            if (!TextUtils.isEmpty(url)) {
                MediaPlayerUtil.getInstance().stopAndRelease();
                Intent intent = new Intent(activity, QcAdDetailActivity.class);
                intent.putExtra("url", url);
                activity.startActivityForResult(intent, 0);
            }
        } else if (2 == action) {   // 2 - 系统浏览器打开链接
            if (!TextUtils.isEmpty(url)) {
                MediaPlayerUtil.getInstance().stopAndRelease();
                Uri    uri      = Uri.parse(url);
                Intent intent11 = new Intent(Intent.ACTION_VIEW, uri);
                activity.startActivityForResult(intent11, 0);
            }
        } else if (4 == action) {   // 4 - 拨打电话
            MediaPlayerUtil.getInstance().stopAndRelease();
            CommonUtils.callPhone(activity, bean.getTpnumber(), 0);
        } else if (6 == action) {   // 6 - 下载APP
            if (!TextUtils.isEmpty(url)) {
                final EventtrackersBean eventtrackers = bean.getEventtrackers();
                new DownloadInstaller(activity, url, new DownloadProgressCallBack() {
                    @Override
                    public void downloadProgress (int progress) {
                        if (!mHaveDownStart && eventtrackers != null) {
                            mHaveDownStart = true;
                            sendShowExposure(eventtrackers.getStartdownload(), width, height);
                        }

                        if (progress == 100) {
                            if (!mHaveDownComplete && eventtrackers != null) {
                                mHaveDownComplete = true;
                                sendShowExposure(eventtrackers.getCompletedownload(), width, height);
                            }
                        }
                    }

                    @Override
                    public void downloadException (Exception e) {
                    }

                    @Override
                    public void onInstallStart () {
                        LogUtils.d("开始安装=");
                        if (!mHaveDownStart && eventtrackers != null) {
                            mHaveDownStart = true;
                            sendShowExposure(eventtrackers.getStartdownload(), width, height);
                        }
                        if (!mHaveDownComplete && eventtrackers != null) {
                            mHaveDownComplete = true;
                            sendShowExposure(eventtrackers.getCompletedownload(), width, height);
                        }
                        if (!mHaveDownInstall && eventtrackers != null) {
                            mHaveDownInstall = true;
                            sendShowExposure(eventtrackers.getStartinstall(), width, height);
                        }
                    }
                }).start();
            }
        } else if (7 == action) {   // 7 - deeplink 链接
            if (!mHaveDeepExposure) {
                mHaveDeepExposure = true;
                sendShowExposure(bean.getImps(), width, height);
            }
            String deeplink = bean.getDeeplink();
            if (null != deeplink && ThirdAppUtils.openLinkApp(activity, deeplink, 0)) {
                MediaPlayerUtil.getInstance().stopAndRelease();
            } else {
                if (!TextUtils.isEmpty(url)) {
                    MediaPlayerUtil.getInstance().stopAndRelease();
                    Uri    uri      = Uri.parse(url);
                    Intent intent11 = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivityForResult(intent11, 0);
                }
            }
        } else if (8 == action) {   // 8 --打开优惠券弹窗
            MediaPlayerUtil.getInstance().stopAndRelease();
            if (!TextUtils.isEmpty(url)) {
                DialogUtils.showWebDialog(activity, url, 2);
            }

            //TODO:下载图文
            String coupon = bean.getLdp();
            if (!TextUtils.isEmpty(coupon)) {
                DonwloadSaveImg.donwloadImg(activity, coupon);
            }
        } else {
            try {
                if (!TextUtils.isEmpty(url)) {
                    MediaPlayerUtil.getInstance().stopAndRelease();
                    Uri    uri      = Uri.parse(url);
                    Intent intent11 = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivityForResult(intent11, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * START  三方方法回调
     */
    public static class JavaMethod {

        private Activity mActivity;

        public JavaMethod (Activity activity) {
            mActivity = activity;
        }

        /**
         * 跳转到webview的方法
         */
        @JavascriptInterface
        public void goto_list_test (String url) {
            Log.e("TAG", "url:" + url);
            if (!TextUtils.isEmpty(url)) {
                MediaPlayerUtil.getInstance().stopAndRelease();
                Intent intent = new Intent(mActivity, QcAdDetailActivity.class);
                intent.putExtra("url", url);
                mActivity.startActivity(intent);
            }
        }

        /**
         * 跳转到webview的方法
         */
        @JavascriptInterface
        public void goto_list (String url) {
            Log.e("TAG", "url:" + url);
            if (!TextUtils.isEmpty(url)) {
                MediaPlayerUtil.getInstance().stopAndRelease();
                Intent intent = new Intent(mActivity, QcAdDetailActivity.class);
                intent.putExtra("url", url);
                mActivity.startActivity(intent);
            }
        }

        /**
         * 调用改方法去发送短信
         *
         * @param phoneNumber 手机号码
         * @param message     短信内容
         **/
        @JavascriptInterface
        public void sendMessage (String phoneNumber, String message) {
            // 注册广播 发送消息
            //发送短信并且到发送短信页面
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
            intent.putExtra("sms_body", message);
            mActivity.startActivity(intent);
        }

        /**
         * 协议下载(留存)
         */
        @JavascriptInterface
        public void download (final String link) {
            try {
                ImageUtils.downLoad(mActivity, link);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
