package com.corpize.sdk.ivoice.danmuku.model.painter;


import com.corpize.sdk.ivoice.danmuku.model.DanMuModel;
import com.corpize.sdk.ivoice.danmuku.model.channel.DanMuChannel;

/**
 * author ：yh
 * date : 2020-12-22 18:30
 * description : 从左到右的动画的增加减少
 */
public class Left2RightPainter extends DanMuPainter {

    @Override
    protected void layout (DanMuModel danMuView, DanMuChannel danMuChannel, boolean isPauseAll) {
        if (danMuView.getX() >= (danMuChannel.width + danMuView.getWidth())) {
            danMuView.setAlive(false);
            return;
        }
        danMuView.setStartPositionX(danMuView.getX() + danMuView.getSpeed() * (1 + 0.5f));
    }

}
