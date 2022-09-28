package com.corpize.sdk.ivoice.danmuku.model.channel;

import com.corpize.sdk.ivoice.danmuku.model.DanMuModel;

import java.util.Random;

/**
 * author ：yh
 * date : 2020-12-22 18:34
 * description :弹幕速度的设置,航道的设置
 */
public class DanMuChannel {

    public int speed = 3;//最低速度,可用户设置
    public int width;
    public int height;
    public int topY;
    public int space = 60;

    public DanMuModel r2lReferenceView;
    public DanMuModel l2rReferenceView;

    public void dispatch (DanMuModel danMuView) {
        if (danMuView.isAttached()) {
            return;
        }

        //danMuView.setSpeed(speed);
        speed = danMuView.getUserSpeed();
        danMuView.setSpeed(getRandomSpeed(5));
        if (danMuView.getDisplayType() == DanMuModel.RIGHT_TO_LEFT) {
            int mDeltaX = 0;
            if (r2lReferenceView != null) {
                mDeltaX = (int) (width - r2lReferenceView.getX() - r2lReferenceView.getWidth());
            }
            if (r2lReferenceView == null || !r2lReferenceView.isAlive() || mDeltaX > space) {
                danMuView.setAttached(true);
                r2lReferenceView = danMuView;
            }
        } else if (danMuView.getDisplayType() == DanMuModel.LEFT_TO_RIGHT) {
            int mDeltaX = 0;
            if (l2rReferenceView != null) {
                mDeltaX = (int) l2rReferenceView.getX();
            }
            if (l2rReferenceView == null || !l2rReferenceView.isAlive() || mDeltaX > space) {
                danMuView.setAttached(true);
                l2rReferenceView = danMuView;
            }
        }
    }

    /**
     * 随机弹幕的速度
     *
     * @param maxSpeed
     * @retur
     */
    private float getRandomSpeed (int maxSpeed) {
        Random random = new Random();
        float  i      = (float) (random.nextInt(maxSpeed) * 0.3) + speed;
        return i;
    }

}
