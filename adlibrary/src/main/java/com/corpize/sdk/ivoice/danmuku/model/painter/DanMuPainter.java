package com.corpize.sdk.ivoice.danmuku.model.painter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.corpize.sdk.ivoice.danmuku.model.DanMuModel;
import com.corpize.sdk.ivoice.danmuku.model.channel.DanMuChannel;
import com.corpize.sdk.ivoice.danmuku.model.utils.PaintUtils;

/**
 * author ：yh
 * date : 2020-12-23 13:53
 * description : 界面绘制
 */
public class DanMuPainter extends IDanMuPainter {

    protected static TextPaint paint;
    protected static RectF     rectF;

    private boolean hide;
    private boolean hideAll;//是否隐藏所有动画
    private boolean isPauseAll = false;//是否暂停所有动画

    private int otherTopY = 0;

    static {
        paint = PaintUtils.getPaint();
        rectF = new RectF();
    }

    public DanMuPainter () {
    }

    protected void layout (DanMuModel danMuView, DanMuChannel danMuChannel, boolean isPauseAll) {
    }

    @Override
    public void requestLayout () {
    }

    @Override
    public void setAlpha (int alpha) {
    }

    @Override
    public void hideNormal (boolean hide) {
        this.hide = hide;
    }

    @Override
    public void hideAll (boolean hideAll) {
        this.hideAll = hideAll;
    }

    @Override
    public void pauseAllDanMuView () {
        this.isPauseAll = true;
    }

    @Override
    public void continueAllDanMuView () {
        this.isPauseAll = false;
    }

    @Override
    public void execute (Canvas canvas, DanMuModel danMuView, DanMuChannel danMuChannel) {
        if ((int) danMuView.getSpeed() == 0) {
            danMuView.setAlive(false);
        }

        //计算弹幕的位置 位置的实时变更
        onLayout(danMuView, danMuChannel, isPauseAll);

        //隐藏全部
        if (hideAll) {
            return;
        }

        //隐藏NORMAL
        if ((danMuView.getPriority() == DanMuModel.NORMAL) && hide) {
            return;
        }

        //隐藏NORMALClick
        if (danMuView.getPriority() == DanMuModel.NORMALCLICK) {
            return;
        }

        draw(canvas, danMuView, danMuChannel);
    }

    /**
     * 计算弹幕的位置 位置的实时变更
     *
     * @param danMuView
     * @param danMuChannel
     * @param isPauseAll
     */
    private void onLayout (DanMuModel danMuView, DanMuChannel danMuChannel, boolean isPauseAll) {
        if (danMuView.isMoving()) {
            layout(danMuView, danMuChannel, isPauseAll);
        }
    }

    protected void draw (Canvas canvas, DanMuModel danMuView, DanMuChannel danMuChannel) {
        otherTopY = (int) danMuView.otherTopY;
        if (danMuView.textBackground != null) {
            drawTextBackground(danMuView, canvas, danMuChannel);
        }

        /*if (danMuView.avatar != null) {
            drawAvatar(danMuView, canvas, danMuChannel);
        }*/

        /*if (danMuView.avatarStrokes) {
            drawAvatarStrokes(danMuView, canvas, danMuChannel);
        }

        if (danMuView.levelBitmap != null) {
            drawLevel(danMuView, canvas, danMuChannel);
        }

        if (!TextUtils.isEmpty(danMuView.levelText)) {
            drawLevelText(danMuView, canvas, danMuChannel);
        }*/

        if (!TextUtils.isEmpty(danMuView.text)) {
            drawText(danMuView, canvas, danMuChannel);
        }
    }

    /**
     * 绘制头像
     *
     * @param danMuView
     * @param canvas
     * @param danMuChannel
     */
    protected void drawAvatar (DanMuModel danMuView, Canvas canvas, DanMuChannel danMuChannel) {
        float top = (int) (danMuView.getY()) + danMuChannel.height / 2 - danMuView.avatarHeight / 2 + otherTopY;
        float x   = danMuView.getX() + danMuView.marginLeft;

        rectF.set((int) x, top,
                (int) (x + danMuView.avatarWidth),
                top + danMuView.avatarHeight);
        canvas.drawBitmap(danMuView.avatar, null, rectF, paint);
    }

    /**
     * 绘制头像的边框
     *
     * @param danMuView
     * @param canvas
     * @param danMuChannel
     */
    protected void drawAvatarStrokes (DanMuModel danMuView, Canvas canvas, DanMuChannel danMuChannel) {
        float x   = danMuView.getX() + danMuView.marginLeft + danMuView.avatarWidth / 2;
        float top = danMuView.getY() + danMuChannel.height / 2 + otherTopY;

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle((int) x, (int) top, danMuView.avatarHeight / 2, paint);
    }

    /**
     * 绘制文字
     *
     * @param danMuView
     * @param canvas
     * @param danMuChannel
     */
    protected void drawText (DanMuModel danMuView, Canvas canvas, DanMuChannel danMuChannel) {
        if (TextUtils.isEmpty(danMuView.text)) {
            return;
        }

        paint.setTextSize(danMuView.textSize);
        paint.setColor(danMuView.textColor);
        paint.setStyle(Paint.Style.FILL);

        CharSequence text = danMuView.text;
        StaticLayout staticLayout = new StaticLayout(text,
                paint,
                (int) Math.ceil(StaticLayout.getDesiredWidth(text, paint)),
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);

        float x = danMuView.getX()
                + danMuView.marginLeft
                + danMuView.avatarWidth
                + danMuView.levelMarginLeft
                + danMuView.levelBitmapWidth
                + danMuView.textMarginLeft;

        float top = (int) (danMuView.getY())
                + danMuChannel.height / 2
                - staticLayout.getHeight() / 2 + otherTopY;

        canvas.save();
        canvas.translate((int) x, top);
        //LogUtils.e("移动x=" + x + "|y=" + top);

        staticLayout.draw(canvas);
        canvas.restore();
    }

    /**
     * 绘制整体背景
     *
     * @param danMuView
     * @param canvas
     * @param danMuChannel
     */
    protected void drawTextBackground (DanMuModel danMuView, Canvas canvas, DanMuChannel danMuChannel) {
        CharSequence text = danMuView.text;
        StaticLayout staticLayout = new StaticLayout(text,
                paint,
                (int) Math.ceil(StaticLayout.getDesiredWidth(text, paint)),
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);

        int textBackgroundHeight = staticLayout.getHeight()
                + danMuView.textBackgroundPaddingTop
                + danMuView.textBackgroundPaddingBottom;

        float top = danMuView.getY()
                + (danMuChannel.height - textBackgroundHeight) / 2 + otherTopY;

        float x = danMuView.getX()
                + danMuView.marginLeft
                + danMuView.avatarWidth
                - danMuView.textBackgroundMarginLeft;

        Rect rectF = new Rect((int) x,
                (int) top,
                (int) (x + danMuView.levelMarginLeft
                        + danMuView.levelBitmapWidth
                        + danMuView.textMarginLeft
                        + danMuView.textBackgroundMarginLeft
                        + staticLayout.getWidth()
                        + danMuView.textBackgroundPaddingRight),
                (int) (top + textBackgroundHeight));

        //LogUtils.e("背景移动x=" + x + "|y=" + top);
        danMuView.setxLeftPosition(x);
        danMuView.setyLeftPosition(top);
        danMuView.textBackground.setBounds(rectF);
        danMuView.textBackground.draw(canvas);
    }

    /**
     * 绘制等级
     *
     * @param danMuView
     * @param canvas
     * @param danMuChannel
     */
    protected void drawLevel (DanMuModel danMuView, Canvas canvas, DanMuChannel danMuChannel) {
        float top = (int) (danMuView.getY()) + danMuChannel.height / 2 - danMuView.levelBitmapHeight / 2 + otherTopY;

        float x = danMuView.getX()
                + danMuView.marginLeft
                + danMuView.avatarWidth
                + danMuView.levelMarginLeft;

        rectF.set((int) x, top,
                (int) (x + danMuView.levelBitmapWidth),
                top + danMuView.levelBitmapHeight);
        canvas.drawBitmap(danMuView.levelBitmap, null, rectF, paint);
    }

    /**
     * 绘制等级的文字
     *
     * @param danMuView
     * @param canvas
     * @param danMuChannel
     */
    protected void drawLevelText (DanMuModel danMuView, Canvas canvas, DanMuChannel danMuChannel) {
        if (TextUtils.isEmpty(danMuView.levelText)) {
            return;
        }

        paint.setTextSize(danMuView.levelTextSize);
        paint.setColor(danMuView.levelTextColor);
        paint.setStyle(Paint.Style.FILL);

        float top = (int) danMuView.getY()
                + danMuChannel.height / 2
                - paint.ascent() / 2
                - paint.descent() / 2 + otherTopY;

        float x = danMuView.getX()
                + danMuView.marginLeft
                + danMuView.avatarWidth
                + danMuView.levelMarginLeft
                + danMuView.levelBitmapWidth / 2;

        canvas.drawText(danMuView.levelText.toString(), (int) x, top, paint);
    }

}
