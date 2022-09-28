package com.corpize.sdk.ivoice.danmuku.model.collection;

import android.content.Context;
import android.graphics.Canvas;


import com.corpize.sdk.ivoice.danmuku.control.speed.SpeedController;
import com.corpize.sdk.ivoice.danmuku.model.DanMuModel;
import com.corpize.sdk.ivoice.danmuku.model.channel.DanMuChannel;
import com.corpize.sdk.ivoice.danmuku.model.painter.DanMuPainter;
import com.corpize.sdk.ivoice.danmuku.model.painter.Left2RightPainter;
import com.corpize.sdk.ivoice.danmuku.model.painter.Right2LeftPainter;
import com.corpize.sdk.ivoice.danmuku.model.utils.DimensionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by android_ls on 2016/12/7.
 */
public final class DanMuConsumedPool {

    private final static int MAX_COUNT_IN_SCREEN = 30;

    private static int DEFAULT_SINGLE_CHANNEL_HEIGHT = 40;

    private HashMap<Integer, DanMuPainter> danMuPainterHashMap = new HashMap<>();

    private volatile ArrayList<DanMuModel> mixedDanMuViewQueue = new ArrayList<>();

    private boolean isDrawing;

    private DanMuChannel[] danMuChannels;

    private SpeedController speedController;

    private Context context;

    public DanMuConsumedPool (Context c) {
        context = c.getApplicationContext();
        initDefaultPainters();
        hide(false);
    }

    public void setSpeedController (SpeedController speedController) {
        this.speedController = speedController;
    }

    public void addPainter (DanMuPainter danMuPainter, int key) {
        if (danMuPainter == null) {
            return;
        }
        if (!danMuPainterHashMap.containsKey(key)) {
            danMuPainterHashMap.put(key, danMuPainter);
        } else {
            throw new IllegalArgumentException("Already has the key of painter");
        }
    }

    public void hide (boolean hide) {
        Set<Integer>      danMuPainters = danMuPainterHashMap.keySet();
        Iterator<Integer> iterator      = danMuPainters.iterator();
        while (iterator.hasNext()) {
            Integer key = iterator.next();
            danMuPainterHashMap.get(key).hideNormal(hide);
        }
    }

    public void hideAll (boolean hide) {
        Set<Integer>      danMuPainters = danMuPainterHashMap.keySet();
        Iterator<Integer> iterator      = danMuPainters.iterator();
        while (iterator.hasNext()) {
            Integer key = iterator.next();
            danMuPainterHashMap.get(key).hideAll(hide);
        }
    }

    /**
     * 暂停所有动画
     */
    public void pauseAllDanMuView () {
        Set<Integer>      danMuPainters = danMuPainterHashMap.keySet();
        Iterator<Integer> iterator      = danMuPainters.iterator();
        while (iterator.hasNext()) {
            Integer key = iterator.next();
            danMuPainterHashMap.get(key).pauseAllDanMuView();
        }
    }

    /**
     * 继续所有动画
     */
    public void continueAllDanMuView () {
        Set<Integer>      danMuPainters = danMuPainterHashMap.keySet();
        Iterator<Integer> iterator      = danMuPainters.iterator();
        while (iterator.hasNext()) {
            Integer key = iterator.next();
            danMuPainterHashMap.get(key).continueAllDanMuView();
        }
    }

    public boolean isDrawnQueueEmpty () {
        if (mixedDanMuViewQueue == null || mixedDanMuViewQueue.size() == 0) {
            isDrawing = false;
            return true;
        }
        return false;
    }

    public void put (ArrayList<DanMuModel> danMuViews) {
        //        if (!isDrawing) { // 这里的判断是为了控制弹幕的发送数量
        if (danMuViews != null && danMuViews.size() > 0) {
            mixedDanMuViewQueue.addAll(danMuViews);
        }
        //        }
    }

    public void draw (Canvas canvas) {
        drawEveryElement(mixedDanMuViewQueue, canvas);
    }

    private synchronized void drawEveryElement (ArrayList<DanMuModel> danMuViewQueue, Canvas canvas) {
        isDrawing = true;
        if (danMuViewQueue == null || danMuViewQueue.size() == 0) {
            return;
        }

        for (int i = 0; i < (danMuViewQueue.size() > MAX_COUNT_IN_SCREEN ? MAX_COUNT_IN_SCREEN : danMuViewQueue.size()); i++) {
            DanMuModel danMuView = danMuViewQueue.get(i);
            try {
                if (danMuView.isAlive()) {
                    DanMuPainter danMuPainter = getPainter(danMuView);
                    DanMuChannel danMuChannel = danMuChannels[danMuView.getChannelIndex()];
                    danMuChannel.dispatch(danMuView);
                    if (danMuView.isAttached()) {
                        performDraw(danMuView, danMuPainter, canvas, danMuChannel);
                    }
                } else {
                    danMuViewQueue.remove(i);
                    i--;
                }
            } catch (Exception e) {
                //TODO:
                //e.printStackTrace();
            }
        }
        isDrawing = false;
    }


    private void initDefaultPainters () {
        Right2LeftPainter r2LPainter = new Right2LeftPainter();
        Left2RightPainter l2RPainter = new Left2RightPainter();
        danMuPainterHashMap.put(DanMuModel.LEFT_TO_RIGHT, l2RPainter);
        danMuPainterHashMap.put(DanMuModel.RIGHT_TO_LEFT, r2LPainter);
    }

    private DanMuPainter getPainter (DanMuModel danMuView) {
        int painterType = danMuView.getDisplayType();
        return danMuPainterHashMap.get(painterType);
    }

    /**
     * 执行画笔的绘制
     *
     * @param danMuView
     * @param danMuPainter
     * @param canvas
     * @param danMuChannel
     */
    private void performDraw (DanMuModel danMuView, DanMuPainter danMuPainter, Canvas canvas, DanMuChannel danMuChannel) {
        danMuPainter.execute(canvas, danMuView, danMuChannel);
    }

    public void divide (int width, int height) {
        int singleHeight = DimensionUtil.dpToPx(context, DEFAULT_SINGLE_CHANNEL_HEIGHT);
        int count        = (height - singleHeight / 3) / singleHeight;

        danMuChannels = new DanMuChannel[count];
        for (int i = 0; i < count; i++) {
            DanMuChannel danMuChannel = new DanMuChannel();
            danMuChannel.width = width;
            danMuChannel.height = singleHeight;
            // danMuChannel.speed = speedController.getSpeed();

            danMuChannel.topY = i * singleHeight + singleHeight / 3;
            // danMuChannel.space = selectSpaceRandomly();
            danMuChannels[i] = danMuChannel;
        }
    }

    private int selectSpaceRandomly () {
        return (int) (Math.random() * 20 + 15);
    }

    public void setChannelHeight (int avatarSize) {
        DEFAULT_SINGLE_CHANNEL_HEIGHT = (int) (avatarSize * 2);
    }
}
