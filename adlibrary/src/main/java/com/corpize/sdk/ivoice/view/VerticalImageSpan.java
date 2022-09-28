package com.corpize.sdk.ivoice.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

/**
 * author ：yh
 * date : 2020-12-14 18:18
 * description : 富文本的含有图片且居中的设置
 */
public class VerticalImageSpan extends ImageSpan {
    private int mBitmapSize;

    public VerticalImageSpan (Context context, Bitmap bitmap) {
        super(context, bitmap);
    }

    public VerticalImageSpan (Context context, int drawable) {
        super(context, drawable);
    }

    public VerticalImageSpan (Context context, Bitmap bitmap, int size) {
        super(context, bitmap);
        mBitmapSize = size;
    }

    public VerticalImageSpan (Context context, int drawable, int size) {
        super(context, drawable);
        mBitmapSize = size;
    }

    @Override
    public int getSize (Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {

        Drawable drawable = getDrawable();
        Rect     rect     = drawable.getBounds();
        int      size     = rect.right;
        if (fm != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            //获得原始的文字、图片高度
            int fontHeight = fmPaint.bottom - fmPaint.top;//文字高度
            int drHeight   = rect.bottom - rect.top;//图片高度
            //设置图片高度为文字高度的三分之二
            rect.bottom = fmPaint.bottom * 2 / 3;
            rect.top = fmPaint.top * 2 / 3;
            rect.right = rect.right * fontHeight * 2 / (drHeight * 3);
            size = rect.right * fontHeight * 2 / (drHeight * 3);

            //获取修改后的图片高度
            int drNowHeight = rect.bottom - rect.top;//图片高度
            //int top         = drNowHeight / 2 - fontHeight / 4;
            //int bottom      = drNowHeight / 2 + fontHeight / 4;
            int top    = fontHeight - fontHeight / 6;
            int bottom = fontHeight / 6;

            fm.ascent = -bottom;
            fm.top = -bottom;
            fm.bottom = top;
            fm.descent = top;
        }
        return size;
    }

    @Override
    public void draw (Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        Drawable drawable = getDrawable();
        canvas.save();
        int transY = 0;
        //获得将要显示的文本高度 - 图片高度除2 = 居中位置+top(换行情况)
        transY = ((bottom - top) - drawable.getBounds().bottom) / 2 + top;
        //偏移画布后开始绘制
        canvas.translate(x, transY);
        drawable.draw(canvas);
        canvas.restore();
    }

}

