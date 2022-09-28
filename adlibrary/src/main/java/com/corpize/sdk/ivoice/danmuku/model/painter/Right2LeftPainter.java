package com.corpize.sdk.ivoice.danmuku.model.painter;


import com.corpize.sdk.ivoice.danmuku.model.DanMuModel;
import com.corpize.sdk.ivoice.danmuku.model.channel.DanMuChannel;

/**
 * author ：yh
 * date : 2020-12-22 18:31
 * description :从右到左动画绘制的增加减少
 */
public class Right2LeftPainter extends DanMuPainter {

    @Override
    protected void layout (DanMuModel danMuView, DanMuChannel danMuChannel, boolean isPauseAll) {
        if (danMuView.getX() - danMuView.getSpeed() <= -danMuView.getWidth()) {
            danMuView.setAlive(false);
            return;
        }
        //LogUtils.e("L2RPainter=绘制的变小x=" + danMuView.getX());

        if (isPauseAll || danMuView.isClick) {
            danMuView.setStartPositionX(danMuView.getX());
        } else {
            danMuView.setStartPositionX(danMuView.getX() - danMuView.getSpeed());
        }

    }
}

