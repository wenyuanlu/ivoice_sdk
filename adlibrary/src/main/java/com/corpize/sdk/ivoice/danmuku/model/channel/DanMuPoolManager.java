package com.corpize.sdk.ivoice.danmuku.model.channel;

import android.content.Context;
import android.graphics.Canvas;

import com.corpize.sdk.ivoice.danmuku.control.dispatcher.IDanMuDispatcher;
import com.corpize.sdk.ivoice.danmuku.control.speed.SpeedController;
import com.corpize.sdk.ivoice.danmuku.model.DanMuModel;
import com.corpize.sdk.ivoice.danmuku.model.collection.DanMuConsumedPool;
import com.corpize.sdk.ivoice.danmuku.model.collection.DanMuConsumerThread;
import com.corpize.sdk.ivoice.danmuku.model.collection.DanMuProducedPool;
import com.corpize.sdk.ivoice.danmuku.model.collection.DanMuProducer;
import com.corpize.sdk.ivoice.danmuku.model.painter.DanMuPainter;
import com.corpize.sdk.ivoice.danmuku.view.IDanMuParent;

import java.util.List;

/**
 * Created by android_ls on 2016/12/7.
 */
public class DanMuPoolManager implements IDanMuPoolManager {

    private DanMuConsumerThread danMuConsumerThread;
    private DanMuProducer       danMuProducer;

    private DanMuConsumedPool danMuConsumedPool;
    private DanMuProducedPool danMuProducedPool;

    private boolean isStart;

    public DanMuPoolManager (Context context, IDanMuParent danMuParent) {
        danMuConsumedPool = new DanMuConsumedPool(context);
        danMuProducedPool = new DanMuProducedPool(context);
        danMuConsumerThread = new DanMuConsumerThread(danMuConsumedPool, danMuParent);
        danMuProducer = new DanMuProducer(danMuProducedPool, danMuConsumedPool);
    }

    public void forceSleep () {
        danMuConsumerThread.forceSleep();
    }

    public void releaseForce () {
        danMuConsumerThread.releaseForce();
    }

    @Override
    public void hide (boolean hide) {
        danMuConsumedPool.hide(hide);
    }

    @Override
    public void hideAll (boolean hideAll) {
        danMuConsumedPool.hideAll(hideAll);
    }

    @Override
    public void startEngine () {
        if (!isStart) {
            isStart = true;
            danMuConsumerThread.start();
            danMuProducer.start();
        }
    }

    @Override
    public void pauseAllDanMuView () {
        danMuConsumedPool.pauseAllDanMuView();
    }

    @Override
    public void continueAllDanMuView () {
        danMuConsumedPool.continueAllDanMuView();
    }

    @Override
    public void setChannelHeight (int avatarSize) {
        danMuConsumedPool.setChannelHeight(avatarSize);
        danMuProducedPool.setChannelHeight(avatarSize);
    }

    @Override
    public void setDispatcher (IDanMuDispatcher iDanMuDispatcher) {
        danMuProducedPool.setDanMuDispatcher(iDanMuDispatcher);
    }

    @Override
    public void setSpeedController (SpeedController speedController) {
        danMuConsumedPool.setSpeedController(speedController);
    }

    @Override
    public void divide (int width, int height) {
        danMuProducedPool.divide(width, height);
        danMuConsumedPool.divide(width, height);
    }

    @Override
    public void addDanMuView (int index, DanMuModel danMuView) {
        danMuProducer.produce(index, danMuView);
    }

    @Override
    public void jumpQueue (List<DanMuModel> danMuViews) {
        danMuProducer.jumpQueue(danMuViews);
    }

    public void release () {
        isStart = false;
        danMuConsumerThread.release();
        danMuProducer.release();
        danMuConsumedPool = null;
    }

    /**
     * drawing entrance
     *
     * @param canvas
     */
    public void drawDanMus (Canvas canvas) {
        danMuConsumerThread.consume(canvas);
    }

    public void addPainter (DanMuPainter danMuPainter, int key) {
        danMuConsumedPool.addPainter(danMuPainter, key);
    }

}
