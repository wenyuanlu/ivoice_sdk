package com.corpize.sdk.ivoice.danmuku;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.corpize.sdk.ivoice.danmuku.control.DanMuController;
import com.corpize.sdk.ivoice.danmuku.control.speed.SpeedController;
import com.corpize.sdk.ivoice.danmuku.model.DanMuModel;
import com.corpize.sdk.ivoice.danmuku.view.IDanMuParent;
import com.corpize.sdk.ivoice.danmuku.view.OnDanMuParentViewTouchCallBackListener;
import com.corpize.sdk.ivoice.danmuku.view.OnDanMuViewTouchListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_ls on 2016/12/7.
 */
public class DanMuView extends View implements IDanMuParent {

    private          DanMuController                        danMuController;
    private volatile ArrayList<OnDanMuViewTouchListener>    onDanMuViewTouchListenerList;
    private          OnDanMuParentViewTouchCallBackListener onDanMuParentViewTouchCallBackListener;
    private          boolean                                drawFinished = false;

    private Object lock = new Object();

    public DanMuView (Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public void jumpQueue (List<DanMuModel> danMuViews) {
        if (null == danMuController) {
            return;
        }

        danMuController.jumpQueue(danMuViews);
    }

    @Override
    public void addAllTouchListener (List<DanMuModel> onDanMuTouchCallBackListeners) {
        this.onDanMuViewTouchListenerList.addAll(onDanMuTouchCallBackListeners);
    }

    public DanMuView (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 初始化
     */
    private void init () {
        onDanMuViewTouchListenerList = new ArrayList<>();
        if (danMuController == null) {
            danMuController = new DanMuController(this);
        }
    }

    public void prepare () {
        prepare(null);
    }

    public void prepare (SpeedController speedController) {
        if (danMuController == null) {
            init();
        }
        if (danMuController != null) {
            danMuController.setSpeedController(speedController);
            danMuController.prepare();
        }
    }

    public void release () {
        onDetectHasCanTouchedDanMusListener = null;
        onDanMuParentViewTouchCallBackListener = null;
        clear();
        if (danMuController != null) {
            danMuController.release();
        }
        danMuController = null;
    }

    @Override
    public void setChannelHeight (int avatarSize) {
        if(null != danMuController){
            danMuController.setChannelHeight(avatarSize);
        }
    }

    private void addDanMuView (final DanMuModel danMuView) {
        if (danMuView == null) {
            return;
        }

        if (danMuController != null) {
            if (danMuView.enableTouch()) {
                onDanMuViewTouchListenerList.add(danMuView);
            }
            danMuController.addDanMuView(-1, danMuView);
        }
    }

    public void setOnDanMuParentViewTouchCallBackListener (OnDanMuParentViewTouchCallBackListener onDanMuParentViewTouchCallBackListener) {
        this.onDanMuParentViewTouchCallBackListener = onDanMuParentViewTouchCallBackListener;
    }

    @Override
    public boolean hasCanTouchDanMus () {
        if (null == onDanMuViewTouchListenerList) {
            return false;
        }

        return onDanMuViewTouchListenerList.size() > 0;
    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        if (hasCanTouchDanMus()) {
            getParent().requestDisallowInterceptTouchEvent(true);
        } else {
            return false;
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            //为了适配滑动事件,把点击放到了ACTION_DOWN中,不拦截点击事件
            case MotionEvent.ACTION_DOWN:
                //LogUtils.e("点击ACTION_DOWN返回getX=" + event.getX() + "|getY=" + event.getY());
                int size = onDanMuViewTouchListenerList.size();
                for (int i = 0; i < size; i++) {
                    OnDanMuViewTouchListener onDanMuViewTouchListener = onDanMuViewTouchListenerList.get(i);
                    boolean                  onTouched                = onDanMuViewTouchListener.onTouch(event.getX(), event.getY());
                    if (((DanMuModel) onDanMuViewTouchListener).getOnTouchCallBackListener() != null && onTouched) {
                        //LogUtils.e("点击返回getX=" + event.getX() + "|getY=" + event.getY());
                        //LogUtils.e("点击返回getRawX=" + event.getRawX() + "|getRawY=" + event.getRawY());
                        float distanceY = event.getRawY() - event.getY();
                        ((DanMuModel) onDanMuViewTouchListener).getOnTouchCallBackListener()
                                .callBack((DanMuModel) onDanMuViewTouchListener, (int) event.getRawX(), (int) distanceY);
                        return true;
                    } else {
                        // super.onTouchEvent(event);
                    }
                }
                if (!hasCanTouchDanMus()) {
                    if (onDanMuParentViewTouchCallBackListener != null) {
                        onDanMuParentViewTouchCallBackListener.callBack();
                    }
                } else {
                    if (onDanMuParentViewTouchCallBackListener != null) {
                        onDanMuParentViewTouchCallBackListener.hideControlPanel();
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        super.onTouchEvent(event);
        return false;
        //return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent (MotionEvent event) {
        super.dispatchTouchEvent(event);
        return false;
    }

    @Override
    public void add (DanMuModel danMuView) {
        danMuView.enableMoving(true);
        addDanMuView(danMuView);
    }

    @Override
    public void lockDraw () {
        if (null == danMuController) {
            return;
        }

        if (!danMuController.isChannelCreated()) {
            return;
        }

        if(null == lock){
            return;
        }

        synchronized (lock) {
            if (Build.VERSION.SDK_INT >= 16) {
                this.postInvalidateOnAnimation();
            } else {
                this.postInvalidate();
            }
            if ((!drawFinished)) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                }
            }
            drawFinished = false;
        }
    }

    @Override
    public void forceSleep () {
        if (null == danMuController) {
            return;
        }

        danMuController.forceSleep();
    }

    @Override
    public void forceWake () {
        if (null == danMuController) {
            return;
        }

        danMuController.forceWake();
    }

    private void unLockDraw () {
        if(null == lock){
            return;
        }
        
        synchronized (lock) {
            drawFinished = true;
            lock.notifyAll();
        }
    }

    @Override
    public void clear () {
        if (onDanMuViewTouchListenerList != null) {
            onDanMuViewTouchListenerList.clear();
        }
    }

    @Override
    public void remove (DanMuModel danMuView) {
        if (onDanMuViewTouchListenerList != null) {
            onDanMuViewTouchListenerList.remove(danMuView);
        }
    }

    public interface OnDetectHasCanTouchedDanMusListener {
        void hasNoCanTouchedDanMus (boolean hasDanMus);
    }

    public void detectHasCanTouchedDanMus () {
        if (null == onDanMuViewTouchListenerList || onDanMuViewTouchListenerList.isEmpty()) {
            return;
        }

        for (int i = 0; i < onDanMuViewTouchListenerList.size(); i++) {
            if (!((DanMuModel) onDanMuViewTouchListenerList.get(i)).isAlive()) {
                onDanMuViewTouchListenerList.remove(i);
                i--;
            }
        }
        if (onDanMuViewTouchListenerList.size() == 0) {
            if (onDetectHasCanTouchedDanMusListener != null) {
                onDetectHasCanTouchedDanMusListener.hasNoCanTouchedDanMus(false);
            }
        } else {
            if (onDetectHasCanTouchedDanMusListener != null) {
                onDetectHasCanTouchedDanMusListener.hasNoCanTouchedDanMus(true);
            }
        }
    }

    @Override
    public void hideNormalDanMuView (boolean hide) {
        if (null == danMuController) {
            return;
        }

        danMuController.hide(hide);
    }

    @Override
    public void hideAllDanMuView (boolean hideAll) {
        if (null == danMuController) {
            return;
        }

        danMuController.hideAll(hideAll);
    }

    @Override
    public void pauseAllDanMuView () {
        if (null == danMuController) {
            return;
        }

        danMuController.pauseAllDanMuView();
    }

    @Override
    public void continueAllDanMuView () {
        if (null == danMuController) {
            return;
        }

        danMuController.continueAllDanMuView();
    }

    @Override
    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
        detectHasCanTouchedDanMus();
        if (danMuController != null) {
            danMuController.initChannels(canvas);
            danMuController.draw(canvas);
        }
        unLockDraw();
    }

    @Override
    public void add (int index, DanMuModel danMuView) {
        if (null == danMuController) {
            return;
        }

        danMuController.addDanMuView(index, danMuView);
    }

    public OnDetectHasCanTouchedDanMusListener onDetectHasCanTouchedDanMusListener;

    public void setOnDanMuExistListener (OnDetectHasCanTouchedDanMusListener onDetectHasCanTouchedDanMusListener) {
        this.onDetectHasCanTouchedDanMusListener = onDetectHasCanTouchedDanMusListener;
    }
}
