package com.corpize.sdk.ivoice;

/**
 * author : xpSun
 * date : 11/25/21
 * description :
 */
public class AdRollAttr {

    private int adWidth;
    private int adHeight;
    private int backgroundColor;
    private ImageStyle leftImageStyle;
    private TitleStyle titleStyle;
    private DescStyle descStyle;
    private RightBottomIconStyle rightBottomIconStyle;

    public int getAdWidth () {
        return adWidth;
    }

    public void setAdWidth (int adWidth) {
        this.adWidth = adWidth;
    }

    public int getAdHeight () {
        return adHeight;
    }

    public void setAdHeight (int adHeight) {
        this.adHeight = adHeight;
    }

    public int getBackgroundColor () {
        return backgroundColor;
    }

    public void setBackgroundColor (int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public ImageStyle getLeftImageStyle () {
        return leftImageStyle;
    }

    public void setLeftImageStyle (ImageStyle leftImageStyle) {
        this.leftImageStyle = leftImageStyle;
    }

    public TitleStyle getTitleStyle () {
        return titleStyle;
    }

    public void setTitleStyle (TitleStyle titleStyle) {
        this.titleStyle = titleStyle;
    }

    public DescStyle getDescStyle () {
        return descStyle;
    }

    public void setDescStyle (DescStyle descStyle) {
        this.descStyle = descStyle;
    }

    public RightBottomIconStyle getRightBottomIconStyle () {
        return rightBottomIconStyle;
    }

    public void setRightBottomIconStyle (RightBottomIconStyle rightBottomIconStyle) {
        this.rightBottomIconStyle = rightBottomIconStyle;
    }

    private static class CommonSizeStyle{
        private int width;
        private int height;
        private int marginLeft;
        private int marginTop;
        private int marginRight;
        private int marginBottom;

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
    }

    private static class CommonImageStyle extends CommonSizeStyle {
        private int defaultSource;

        public int getDefaultSource () {
            return defaultSource;
        }

        public void setDefaultSource (int defaultSource) {
            this.defaultSource = defaultSource;
        }
    }

    private static class CommonTextStyle extends CommonSizeStyle{
        private int textSize;
        private int textColor;
        private int textStyle;

        public int getTextSize () {
            return textSize;
        }

        public void setTextSize (int textSize) {
            this.textSize = textSize;
        }

        public int getTextColor () {
            return textColor;
        }

        public void setTextColor (int textColor) {
            this.textColor = textColor;
        }

        public int getTextStyle () {
            return textStyle;
        }

        public void setTextStyle (int textStyle) {
            this.textStyle = textStyle;
        }
    }

    public static class TitleStyle extends CommonTextStyle {

    }

    public static class DescStyle extends CommonTextStyle {

    }

    public static class ImageStyle extends CommonImageStyle {

    }

    public static class RightBottomIconStyle extends CommonImageStyle {

    }
}
