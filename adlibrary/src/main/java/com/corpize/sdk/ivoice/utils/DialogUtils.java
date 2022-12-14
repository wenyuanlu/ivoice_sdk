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
 * author ???yh
 * date : 2020-11-24 04:06
 * description : ????????????
 */
public class DialogUtils {

    private static Dialog               mInsertDialog;
    private static Dialog               mWebDialog;
    private static Dialog               mEditTextDialog;
    private static boolean              mHaveClick        = false;      //???????????????
    private static boolean              mHaveExposure     = false;      //????????????
    private static boolean              mHaveDeepExposure = false;      //??????deep??????
    private static boolean              mHaveDownStart    = false;      //????????????????????????????????????
    private static boolean              mHaveDownComplete = false;      //????????????????????????????????????
    private static boolean              mHaveDownInstall  = false;      //????????????????????????????????????
    private static float                mClickDownX;                    //?????? ????????????X
    private static float                mClickDownY;                    //?????? ????????????Y
    private static float                mClickUpX;                      //?????? ????????????X
    private static float                mClickUpY;                      //?????? ????????????Y
    private static float                mPositionX;                     //?????? ?????????X
    private static float                mPositionY;                     //?????? ?????????Y
    private static CustomCountDownTimer mSkipCountDown;                 //??????????????????
    private static CustomCountDownTimer mNoInteractiveCountDown;        //?????????????????????????????????
    private static CustomCountDownTimer mHaveInteractiveCountDown;      //?????????????????????????????????

    /**
     * ???????????????????????????(???????????????)
     */
    public static void showImageWithOutDialog (final Activity activity, final AdAudioBean responseBean,
                                               final AudioQcAdListener listener, final DialogSizeCallback callBack, final DialogCallback dialogCallback) {
        showImageDialog(activity, responseBean, 0, 0, listener, callBack, dialogCallback);
    }


    /**
     * ???????????????????????????
     * allTime ?????????????????????
     * interactiveTime ?????????????????????
     */
    public static void showImageDialog (final Activity activity, final AdAudioBean responseBean,
                                        final int allTime, final int interactiveTime, final AudioQcAdListener listener,
                                        final DialogSizeCallback callBack, final DialogCallback dialogCallback) {
        if (mInsertDialog != null && mInsertDialog.isShowing()) {
            mInsertDialog.dismiss();
        }
        //???????????????
        closeCountDownTime();

        //????????????
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

        //????????????
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
            //?????????????????????
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

            //????????????
            if (null != responseBean.getCompanion() && !TextUtils.isEmpty(responseBean.getCompanion().getUrl())) {
                ImageUtils.loadImage(activity, responseBean.getCompanion().getUrl(), adIcon);
            } else if (responseBean.getFirstimg() != null) {
                ImageUtils.loadImage(activity, responseBean.getFirstimg(), adIcon);
            }

            //?????????????????????
            ImageUtils.loadImage(activity, Constants.AD_ICON, adShow);
            //????????????????????????
            getClickPosition(adIcon);

            //????????????
            if (!mHaveExposure) {
                mHaveExposure = true;
                sendShowExposure(responseBean.getImps(), myWidth, myHeight);
            }

            //????????????
            adIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    try {
                        if (listener != null) {
                            listener.onAdClick();
                        }

                        //Ext ???????????????
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

            //???????????????
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
     * ??????WebView?????????
     * type 1?????????????????????,????????????,2?????????,????????????
     */
    public static void showWebDialog (Activity activity, String url) {
        showWebDialog(activity, url, 1, null);
    }

    /**
     * ??????WebView?????????
     * type 1?????????????????????,????????????,2?????????,????????????
     */
    public static void showWebDialog (Activity activity, String url, int type) {
        showWebDialog(activity, url, type, null);
    }

    public static void showWebDialog (final Activity activity, String url, final DialogCallback callBack) {
        showWebDialog(activity, url, 1, callBack);
    }

    /**
     * ??????WebView?????????
     * type 1?????????????????????,????????????,2?????????,????????????
     */
    public static void showWebDialog (final Activity activity, String url, int type, final DialogCallback callBack) {
        if (mWebDialog != null && mWebDialog.isShowing()) {
            mWebDialog.dismiss();
        }
        //???????????????
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

        //?????????????????????
        int       screenWidth = DeviceUtil.getScreenWidth(activity);
        final int myWidth     = screenWidth * 10 / 10;
        final int myHeight    = myWidth;

        LinearLayout.LayoutParams webParam = (LinearLayout.LayoutParams) webView.getLayoutParams();
        int                       otherDp  = DeviceUtil.dip2px(activity, 50);
        webParam.width = myWidth;
        webParam.height = LinearLayout.LayoutParams.WRAP_CONTENT;

        //?????????????????????
        //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ll.getLayoutParams();
        //int                         thisDp = DeviceUtil.dip2px(activity, 50);
        //params.width = myWidth;
        //params.height = myHeight + thisDp;

        //ll.setLayoutParams(params);
        webView.setLayoutParams(webParam);

        //TODO:WebView?????????
        webView.setBackgroundColor(0); // ???????????????
        webView.getBackground().setAlpha(0); // ????????????????????? ?????????0-255
        //????????????
        WebSettings settings = webView.getSettings();
        settings.setAllowFileAccess(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//????????????????????????????????????
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setDatabaseEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setJavaScriptEnabled(true);

        // webview?????????5.0???????????????????????????????????????????????????
        // ?????????5.0??????????????????????????????http???https???????????????????????????webview???????????????????????????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setVerticalScrollBarEnabled(false);//????????????????????????
        webView.setHorizontalScrollBarEnabled(false);
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        //JS????????????,???????????????????????????
        webView.addJavascriptInterface(new JavaMethod(activity), "android");
        //????????????????????????????????????
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();  // ???????????????????????????  ??????https????????????
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
                //??????webview?????????loadUrl??????
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
     * ????????????????????????
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
        window.setGravity(Gravity.BOTTOM);//dialog????????????
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
                } else if (keyCode == KeyEvent.KEYCODE_DEL) {//?????????
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
     * ???????????????????????????
     */
    public static void dismissEditDialog () {
        if (mEditTextDialog != null && mEditTextDialog.isShowing()) {
            mEditTextDialog.dismiss();
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param intervalTime
     * @param listener
     */
    public static void noInteractiveCountDownTime (int intervalTime, final AudioQcAdListener listener) {
        noInteractiveCountDownTime(intervalTime, true, listener, null);
    }

    /**
     * ?????????????????????????????????
     *
     * @param intervalTime
     * @param isClose      ??????????????????
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
                LogUtils.d("????????????????????????");
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
     * ???????????????????????????
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
     * ??????????????????
     *
     * @param responseBean
     * @param close
     * @param tvDownTime
     * @param allTime      ?????????????????????
     */
    private static void skipCountDownTime (AdAudioBean responseBean, final ImageView close,
                                           final TextView tvDownTime, int allTime, int interactiveTime) {
        final int skipTime     = responseBean.getSkip();
        int       durationTime = responseBean.getDuration();
        if (durationTime > allTime) {
            durationTime = allTime;
        }
        final int countDownTime = durationTime + interactiveTime;
        //???????????????,?????????????????????,??????????????????
        if (countDownTime <= 0) {
            close.setVisibility(View.VISIBLE);
            tvDownTime.setVisibility(View.INVISIBLE);
            return;
        }
        //???????????????0,????????????????????????,??????????????????
        if (allTime > 0) {
            if (skipTime > 0) {
                close.setVisibility(View.INVISIBLE);
            } else {
                close.setVisibility(View.VISIBLE);
            }
        } else {
            //???????????????0,???????????????,??????????????????????????????
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
                LogUtils.d("??????=" + time + " ?????????=" + millisUntilFinished);
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
     * ?????????????????????
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
     * ??????????????????x,y????????? onTouch()??????
     * ???????????????
     * true??? view????????????Touch?????????
     * false???view????????????Touch????????????????????????false?????????????????????????????????????????????????????????????????????
     */
    private static void getClickPosition (View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch (View view, MotionEvent event) {
                switch (event.getAction()) {
                    //?????????????????????
                    case MotionEvent.ACTION_DOWN:
                        //LogUtils.e("???????????????(" + event.getX() + "," + event.getY());
                        mClickDownX = event.getX();
                        mClickDownY = event.getY();
                        break;

                    //??????????????????
                    case MotionEvent.ACTION_MOVE:
                        //LogUtils.e("???????????????(" + event.getX() + "," + event.getY());
                        break;

                    //?????????????????????
                    case MotionEvent.ACTION_UP:
                        //LogUtils.e("???????????????(" + event.getX() + "," + event.getY());
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
     * ????????????,???????????????????????????
     */
    private static void sendShowExposure (List<String> imgList, int width, int height) {
        long time = System.currentTimeMillis();

        if (imgList != null && imgList.size() > 0) {
            for (int i = 0; i < imgList.size(); i++) {
                String urlOld = imgList.get(i);
                String url    = urlOld;
                if (url.contains("__WIDTH__")) {//????????????
                    url = url.replace("__WIDTH__", width + "");
                }
                if (url.contains("__HEIGHT__")) {//????????????
                    url = url.replace("__HEIGHT__", height + "");
                }
                if (url.contains("__POSITION_X__")) {//??????X????????????
                    url = url.replace("__POSITION_X__", mPositionX + "");
                }
                if (url.contains("__POSITION_Y__")) {//??????Y????????????
                    url = url.replace("__POSITION_Y__", mPositionY + "");
                }
                if (url.contains("__TIME_STAMP__")) {//??????????????????
                    url = url.replace("__TIME_STAMP__", time + "");
                }

                QcHttpUtil.sendAdExposure(url);
            }
        }
    }

    /**
     * ?????????????????????(????????????)
     */
    private static void sendClickExposure (final List<String> list, int width, int height) {
        if (list != null && list.size() > 0) {
            long time = System.currentTimeMillis();

            for (int i = 0; i < list.size(); i++) {
                String urlOld = list.get(i);
                String url    = urlOld;
                if (url.contains("__DOWN_X__")) {//??????X????????????
                    url = url.replace("__DOWN_X__", mClickDownX + "");
                }
                if (url.contains("__DOWN_Y__")) {//??????Y????????????
                    url = url.replace("__DOWN_Y__", mClickDownY + "");
                }
                if (url.contains("__UP_X__")) {//??????X????????????
                    url = url.replace("__UP_X__", mClickDownX + "");
                }
                if (url.contains("__UP_Y__")) {//??????Y????????????
                    url = url.replace("__UP_Y__", mClickDownY + "");
                }
                if (url.contains("__WIDTH__")) {//????????????
                    url = url.replace("__WIDTH__", width + "");
                }
                if (url.contains("__HEIGHT__")) {//????????????
                    url = url.replace("__HEIGHT__", height + "");
                }
                if (url.contains("__POSITION_X__")) {//??????X????????????
                    url = url.replace("__POSITION_X__", mPositionX + "");
                }
                if (url.contains("__POSITION_Y__")) {//??????Y????????????
                    url = url.replace("__POSITION_Y__", mPositionY + "");
                }
                if (url.contains("__TIME_STAMP__")) {//??????????????????
                    url = url.replace("__TIME_STAMP__", time + "");
                }

                QcHttpUtil.sendAdExposure(url);
            }
        }
    }

    /**
     * ????????????
     */
    private static void setClick (Activity activity, AdAudioBean bean, final int width, final int height) {
        if (bean == null) {
            return;
        }
        int    action = bean.getAction();
        String url    = bean.getLdp();
        if (0 == action) {          // 0 - ?????????
        } else if (1 == action) {   // 1 - App webview ????????????
            if (!TextUtils.isEmpty(url)) {
                MediaPlayerUtil.getInstance().stopAndRelease();
                Intent intent = new Intent(activity, QcAdDetailActivity.class);
                intent.putExtra("url", url);
                activity.startActivityForResult(intent, 0);
            }
        } else if (2 == action) {   // 2 - ???????????????????????????
            if (!TextUtils.isEmpty(url)) {
                MediaPlayerUtil.getInstance().stopAndRelease();
                Uri    uri      = Uri.parse(url);
                Intent intent11 = new Intent(Intent.ACTION_VIEW, uri);
                activity.startActivityForResult(intent11, 0);
            }
        } else if (4 == action) {   // 4 - ????????????
            MediaPlayerUtil.getInstance().stopAndRelease();
            CommonUtils.callPhone(activity, bean.getTpnumber(), 0);
        } else if (6 == action) {   // 6 - ??????APP
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
                        LogUtils.d("????????????=");
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
        } else if (7 == action) {   // 7 - deeplink ??????
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
        } else if (8 == action) {   // 8 --?????????????????????
            MediaPlayerUtil.getInstance().stopAndRelease();
            if (!TextUtils.isEmpty(url)) {
                DialogUtils.showWebDialog(activity, url, 2);
            }

            //TODO:????????????
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
     * START  ??????????????????
     */
    public static class JavaMethod {

        private Activity mActivity;

        public JavaMethod (Activity activity) {
            mActivity = activity;
        }

        /**
         * ?????????webview?????????
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
         * ?????????webview?????????
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
         * ??????????????????????????????
         *
         * @param phoneNumber ????????????
         * @param message     ????????????
         **/
        @JavascriptInterface
        public void sendMessage (String phoneNumber, String message) {
            // ???????????? ????????????
            //???????????????????????????????????????
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
            intent.putExtra("sms_body", message);
            mActivity.startActivity(intent);
        }

        /**
         * ????????????(??????)
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
