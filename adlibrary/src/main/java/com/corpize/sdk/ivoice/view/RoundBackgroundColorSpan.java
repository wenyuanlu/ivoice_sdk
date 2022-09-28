package com.corpize.sdk.ivoice.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;

/**
 * author ：yh
 * date : 2020-12-14 18:18
 * description : 富文本的含有圆角背景的文字设置
 */
public class RoundBackgroundColorSpan extends ReplacementSpan {
    private int mTextSize    = 30;//文字大小
    private int mBgColor     = Color.parseColor("#33000000");//背景颜色
    private int mTextColor   = Color.parseColor("#FFFFFF");//文字颜色
    private int mStrokeColor = Color.parseColor("#00000000");//边框颜色
    private int mSize;

    public RoundBackgroundColorSpan () {
        super();
    }

    public RoundBackgroundColorSpan (int textSize) {
        super();
        this.mTextSize = textSize;
    }

    public RoundBackgroundColorSpan (int bgColor, int textColor) {
        super();
        this.mBgColor = bgColor;
        this.mTextColor = textColor;
    }

    public RoundBackgroundColorSpan (int bgColor, int textColor, int strokeColor) {
        super();
        this.mBgColor = bgColor;
        this.mTextColor = textColor;
        this.mStrokeColor = strokeColor;
    }

    public RoundBackgroundColorSpan (int bgColor, int textColor, int strokeColor, int textSize) {
        super();
        this.mBgColor = bgColor;
        this.mTextColor = textColor;
        this.mStrokeColor = strokeColor;
        this.mTextSize = textSize;
    }

    @Override
    public int getSize (Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        //return ((int) paint.measureText(text, start, end) + 60);
        mSize = (int) (paint.measureText(text, start, end) + 2 * mTextSize);
        //mSize就是span的宽度，span有多宽，开发者可以在这里随便定义规则
        //我的规则：这里text传入的是SpannableString，start，end对应setSpan方法相关参数
        //可以根据传入起始截至位置获得截取文字的宽度，最后加上左右两个圆角的半径得到span宽度
        return mSize;
    }

    @Override
    public void draw (Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        int originalColor = paint.getColor();
        //设置背景颜色
        paint.setTextSize(this.mTextSize);
        paint.setAntiAlias(true);// 设置画笔的锯齿效果
        paint.setColor(this.mBgColor);

        //The recommended distance above the baseline for singled spaced text
        //基准线上方
        //The recommended distance below the baseline for singled spaced text
        //基准线下方
        float centHeight = paint.descent() - paint.ascent();
        float diffHeight = mTextSize - centHeight;  //这个是后面的字体的大小的高度 -  这个画上的字体的大小的高度  这个地方要设置圆角居中显示
        //RectF oval = new RectF(x, y + paint.ascent() - diffHeight, x + mSize, y + paint.descent());

        canvas.drawRoundRect(new RectF(x, top + 1, x + ((int) paint.measureText(text, start, end) + this.mTextSize), bottom - 1), 20, 20, paint);
        //设置文字颜色
        paint.setColor(this.mTextColor);
        canvas.drawText(text, start, end, x + (this.mTextSize >> 1), y + diffHeight / 2, paint);
        //设置边框颜色
        paint.setColor(this.mStrokeColor);
        paint.setStrokeWidth(1.5f);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(new RectF(x, top + 1, x + ((int) paint.measureText(text, start, end) + this.mTextSize), bottom - 1), 20, 20, paint);

        //颜色初始化
        paint.setColor(originalColor);
    }
}

