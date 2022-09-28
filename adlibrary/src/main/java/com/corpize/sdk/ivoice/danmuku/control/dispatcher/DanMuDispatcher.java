package com.corpize.sdk.ivoice.danmuku.control.dispatcher;

import android.content.Context;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.corpize.sdk.ivoice.danmuku.model.DanMuModel;
import com.corpize.sdk.ivoice.danmuku.model.channel.DanMuChannel;
import com.corpize.sdk.ivoice.danmuku.model.utils.PaintUtils;
import com.corpize.sdk.ivoice.utils.LogUtils;

import java.util.Random;

/**
 * Created by android_ls on 2016/12/7.
 */
public class DanMuDispatcher implements IDanMuDispatcher {

    //private Context context;
    protected TextPaint paint;
    private   Random    random             = new Random();
    private   int       lastFirstPosition  = -1;//上一个位置
    private   int       lastSecondPosition = -1;//倒数第二个位置
    private   int       lastThirdPosition  = -1;//倒数第三个位置
    private   int       lastFourPosition   = -1;//倒数第四个位置
    private   int       lastFivePosition   = -1;//倒数第五个位置
    private   int       lastSixPosition    = -1;//倒数第六个位置
    private   int       lastSevenPosition  = -1;//倒数第七个位置
    private   int       lastEightPosition  = -1;//倒数第八个位置
    private   int       lastNinePosition   = -1;//倒数第九个位置
    private   int       lastTenPosition    = -1;//倒数第十个位置

    public DanMuDispatcher (Context context) {
        //this.context = context;
        paint = PaintUtils.getPaint();
    }

    @Override
    public synchronized void dispatch (DanMuModel danMuView, DanMuChannel[] danMuChannels) {
        if (!danMuView.isAttached() && danMuChannels != null && danMuChannels.length > 0) {
            int index = selectChannelRandomly(danMuChannels);
            danMuView.selectChannel(index);
            DanMuChannel danMuChannel = danMuChannels[index];
            if (danMuChannel == null) {
                return;
            }

            measure(danMuView, danMuChannel);
        }
    }

    /**
     * 判断显示在哪一行,前后显示不是一行
     *
     * @param danMuChannels
     * @return
     */
    private int selectChannelRandomly (DanMuChannel[] danMuChannels) {
        int selectPosition = random.nextInt(danMuChannels.length);
        //LogUtils.e("返回的弹幕加载位置=" + selectPosition);
        if (danMuChannels.length == 1) {
            //一排弹幕
            return selectPosition;
        } else if (danMuChannels.length == 2) {
            //两排弹幕
            if (selectPosition != lastFirstPosition) {
                lastFirstPosition = selectPosition;
                return selectPosition;
            } else {
                return selectChannelRandomly(danMuChannels);

            }
        } else if (danMuChannels.length == 3) {
            //三排弹幕
            if (selectPosition != lastFirstPosition
                    && selectPosition != lastSecondPosition) {
                lastSecondPosition = lastFirstPosition;
                lastFirstPosition = selectPosition;
                return selectPosition;
            } else {
                return selectChannelRandomly(danMuChannels);
            }
        } else if (danMuChannels.length == 4) {
            //四排弹幕
            if (selectPosition != lastFirstPosition
                    && selectPosition != lastSecondPosition
                    && selectPosition != lastThirdPosition) {
                lastThirdPosition = lastSecondPosition;
                lastSecondPosition = lastFirstPosition;
                lastFirstPosition = selectPosition;
                return selectPosition;
            } else {
                return selectChannelRandomly(danMuChannels);
            }
        } else if (danMuChannels.length == 5) {
            //五排弹幕
            if (selectPosition != lastFirstPosition
                    && selectPosition != lastSecondPosition
                    && selectPosition != lastThirdPosition
                    && selectPosition != lastFourPosition) {
                lastFourPosition = lastThirdPosition;
                lastThirdPosition = lastSecondPosition;
                lastSecondPosition = lastFirstPosition;
                lastFirstPosition = selectPosition;
                return selectPosition;
            } else {
                return selectChannelRandomly(danMuChannels);
            }
        } else if (danMuChannels.length == 6) {
            //六排弹幕
            if (selectPosition != lastFirstPosition
                    && selectPosition != lastSecondPosition
                    && selectPosition != lastThirdPosition
                    && selectPosition != lastFourPosition
                    && selectPosition != lastFivePosition) {
                lastFivePosition = lastFourPosition;
                lastFourPosition = lastThirdPosition;
                lastThirdPosition = lastSecondPosition;
                lastSecondPosition = lastFirstPosition;
                lastFirstPosition = selectPosition;
                return selectPosition;
            } else {
                return selectChannelRandomly(danMuChannels);
            }
        } else if (danMuChannels.length == 7) {
            //七排弹幕
            if (selectPosition != lastFirstPosition
                    && selectPosition != lastSecondPosition
                    && selectPosition != lastThirdPosition
                    && selectPosition != lastFourPosition
                    && selectPosition != lastFivePosition
                    && selectPosition != lastSixPosition) {
                lastSixPosition = lastFivePosition;
                lastFivePosition = lastFourPosition;
                lastFourPosition = lastThirdPosition;
                lastThirdPosition = lastSecondPosition;
                lastSecondPosition = lastFirstPosition;
                lastFirstPosition = selectPosition;
                return selectPosition;
            } else {
                return selectChannelRandomly(danMuChannels);
            }
        } else if (danMuChannels.length == 8) {
            //八排弹幕
            if (selectPosition != lastFirstPosition
                    && selectPosition != lastSecondPosition
                    && selectPosition != lastThirdPosition
                    && selectPosition != lastFourPosition
                    && selectPosition != lastFivePosition
                    && selectPosition != lastSixPosition
                    && selectPosition != lastSevenPosition) {
                lastSevenPosition = lastSixPosition;
                lastSixPosition = lastFivePosition;
                lastFivePosition = lastFourPosition;
                lastFourPosition = lastThirdPosition;
                lastThirdPosition = lastSecondPosition;
                lastSecondPosition = lastFirstPosition;
                lastFirstPosition = selectPosition;
                return selectPosition;
            } else {
                return selectChannelRandomly(danMuChannels);
            }
        } else if (danMuChannels.length == 9) {
            //九排弹幕
            if (selectPosition != lastFirstPosition
                    && selectPosition != lastSecondPosition
                    && selectPosition != lastThirdPosition
                    && selectPosition != lastFourPosition
                    && selectPosition != lastFivePosition
                    && selectPosition != lastSixPosition
                    && selectPosition != lastSevenPosition
                    && selectPosition != lastEightPosition) {
                lastEightPosition = lastSevenPosition;
                lastSevenPosition = lastSixPosition;
                lastSixPosition = lastFivePosition;
                lastFivePosition = lastFourPosition;
                lastFourPosition = lastThirdPosition;
                lastThirdPosition = lastSecondPosition;
                lastSecondPosition = lastFirstPosition;
                lastFirstPosition = selectPosition;
                return selectPosition;
            } else {
                return selectChannelRandomly(danMuChannels);
            }
        } else {
            //超过九排弹幕
            if (selectPosition != lastFirstPosition
                    && selectPosition != lastSecondPosition
                    && selectPosition != lastThirdPosition
                    && selectPosition != lastFourPosition
                    && selectPosition != lastFivePosition
                    && selectPosition != lastSixPosition
                    && selectPosition != lastSevenPosition
                    && selectPosition != lastEightPosition
                    && selectPosition != lastNinePosition) {
                lastNinePosition = lastEightPosition;
                lastEightPosition = lastSevenPosition;
                lastSevenPosition = lastSixPosition;
                lastSixPosition = lastFivePosition;
                lastFivePosition = lastFourPosition;
                lastFourPosition = lastThirdPosition;
                lastThirdPosition = lastSecondPosition;
                lastSecondPosition = lastFirstPosition;
                lastFirstPosition = selectPosition;
                return selectPosition;
            } else {
                return selectChannelRandomly(danMuChannels);
            }
        }

    }

    private void measure (DanMuModel danMuView, DanMuChannel danMuChannel) {
        if (danMuView.isMeasured()) {
            return;
        }

        CharSequence text = danMuView.text;
        if (!TextUtils.isEmpty(text)) {
            paint.setTextSize(danMuView.textSize);
            StaticLayout staticLayout = new StaticLayout(text,
                    paint,
                    (int) Math.ceil(StaticLayout.getDesiredWidth(text, paint)),
                    Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);

            float textWidth = danMuView.getX()
                    + danMuView.marginLeft
                    + danMuView.avatarWidth
                    + danMuView.levelMarginLeft
                    + danMuView.levelBitmapWidth
                    + danMuView.textMarginLeft
                    + staticLayout.getWidth()
                    + danMuView.textBackgroundPaddingRight;
            danMuView.setWidth((int) textWidth);

            float textHeight = staticLayout.getHeight()
                    + danMuView.textBackgroundPaddingTop
                    + danMuView.textBackgroundPaddingBottom;
            if (danMuView.avatar != null && danMuView.avatarHeight > textHeight) {
                danMuView.setHeight((int) (danMuView.getY() + danMuView.avatarHeight));
            } else {
                danMuView.setHeight((int) (danMuView.getY() + textHeight));
            }
        }

        if (danMuView.getDisplayType() == DanMuModel.RIGHT_TO_LEFT) {
            danMuView.setStartPositionX(danMuChannel.width);
        } else if (danMuView.getDisplayType() == DanMuModel.LEFT_TO_RIGHT) {
            danMuView.setStartPositionX(-danMuView.getWidth());
        }

        danMuView.setMeasured(true);
        danMuView.setStartPositionY(danMuChannel.topY);
        danMuView.setAlive(true);
    }

    public void release () {
        //context = null;
    }
}
