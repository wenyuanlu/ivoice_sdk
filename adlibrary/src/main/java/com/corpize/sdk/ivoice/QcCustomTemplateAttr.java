package com.corpize.sdk.ivoice;

/**
 * author : xpSun
 * date : 12/8/21
 * description :
 */
public class QcCustomTemplateAttr {

    //封面样式
    private CoverStyle coverStyle;
    //左下角icon 样式
    private IconStyle  iconStyle;
    //是否启用跳过,默认true
    private boolean isEnableSkip = true;
    //倒计时结束后是否启用自动关闭广告
    private boolean skipAutoClose;
    //启用跳过时按钮文案
    private String skipTipValue;

    public CoverStyle getCoverStyle () {
        return coverStyle;
    }

    public void setCoverStyle (CoverStyle coverStyle) {
        this.coverStyle = coverStyle;
    }

    public IconStyle getIconStyle () {
        return iconStyle;
    }

    public void setIconStyle (IconStyle iconStyle) {
        this.iconStyle = iconStyle;
    }

    public boolean isEnableSkip () {
        return isEnableSkip;
    }

    public void setEnableSkip (boolean enableSkip) {
        isEnableSkip = enableSkip;
    }

    public boolean isSkipAutoClose () {
        return skipAutoClose;
    }

    public void setSkipAutoClose (boolean skipAutoClose) {
        this.skipAutoClose = skipAutoClose;
    }

    public String getSkipTipValue () {
        return skipTipValue;
    }

    public void setSkipTipValue (String skipTipValue) {
        this.skipTipValue = skipTipValue;
    }

    public static class CommonSizeStyle {
        private int width;
        private int height;
        private int radius;

        public int getWidth () {
            return width;
        }

        public void setWidth (int width) {
            this.width = width;
        }

        public int getHeight () {
            return height;
        }

        public void setHeight (int height) {
            this.height = height;
        }

        public int getRadius () {
            return radius;
        }

        public void setRadius (int radius) {
            this.radius = radius;
        }
    }

    public static class CoverStyle extends CommonSizeStyle {

    }

    public static class IconStyle extends CommonSizeStyle {

        private boolean isEnableMargin;//是否启用外部边距
        private int marginLeft;
        private int marginTop;
        private int marginRight;
        private int marginBottom;
        private int layoutGravity;//位于父布局内的位置

        public boolean isEnableMargin () {
            return isEnableMargin;
        }

        public void setEnableMargin (boolean enableMargin) {
            isEnableMargin = enableMargin;
        }

        public int getMarginLeft () {
            return marginLeft;
        }

        public void setMarginLeft (int marginLeft) {
            this.marginLeft = marginLeft;
        }

        public int getMarginTop () {
            return marginTop;
        }

        public void setMarginTop (int marginTop) {
            this.marginTop = marginTop;
        }

        public int getMarginRight () {
            return marginRight;
        }

        public void setMarginRight (int marginRight) {
            this.marginRight = marginRight;
        }

        public int getMarginBottom () {
            return marginBottom;
        }

        public void setMarginBottom (int marginBottom) {
            this.marginBottom = marginBottom;
        }

        public int getLayoutGravity () {
            return layoutGravity;
        }

        public void setLayoutGravity (int layoutGravity) {
            this.layoutGravity = layoutGravity;
        }
    }
}
