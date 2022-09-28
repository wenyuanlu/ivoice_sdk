package com.corpize.sdk.ivoice;

import android.graphics.drawable.Drawable;

import com.corpize.sdk.ivoice.bean.UserPlayInfoBean;
import com.qichuang.annotation.ColorInt;

import java.util.ArrayList;
import java.util.List;

public class AdAttr {
    private static AdAttr  sAdAttr;
    private        String  adid;
    private        String  mid;
    private        boolean isCanLeftTouch = false;//是否支持往左滑动打开落地页下载等操作
    private        boolean isCanPause     = true;//是否支持暂停

    private boolean        isSetBackgroundSize   = false;
    private int            backgroundWidth       = 0;//背景宽度
    private int            backgroundHeight      = 0;//背景高度
    private List<AdLayout> backgroundLayout      = new ArrayList<>();//标题的位置
    private boolean        isSetBackgroundMagin  = false;
    private int            backgroundMaginLeft   = 0;
    private int            backgroundMaginRight  = 0;
    private int            backgroundMaginTop    = 0;
    private int            backgroundMaginBottom = 0;

    private int            titleColor        = 0;//标题颜色
    private int            titleSize         = 0;//标题大小
    private List<AdLayout> titleLayout       = new ArrayList<>();//标题的位置
    private boolean        isSetTitleMagin   = false;
    private int            titleMaginLeft    = 0;
    private int            titleMaginRight   = 0;
    private int            titleMaginTop     = 0;
    private int            titleMaginBottom  = 0;
    private int            titleTextMaxSize  = 0;
    private int            titleTextMaxLines = 0;

    private int            contentColor       = 0;//内容颜色
    private int            contentSize        = 0;//内容大小
    private int            contentMaxLine     = 2;//内容最多的行数
    private List<AdLayout> contentLayout      = new ArrayList<>();//内容的位置
    private boolean        isSetContentMagin  = false;
    private int            contentMaginLeft   = 0;
    private int            contentMaginRight  = 0;
    private int            contentMaginTop    = 0;
    private int            contentMaginBottom = 0;

    private int            infoTitleColor            = 0;//信息标题颜色
    private int            infoTitleSize             = 0;//信息标题大小
    private int            infoContentColor          = 0;//信息内容颜色
    private int            infoContentSize           = 0;//信息内容大小
    private int            infoButtonColor           = 0;//信息按钮颜色
    private int            infoButtonBackgroundColor = 0;//信息按钮背景颜色
    private List<AdLayout> infoLayout                = new ArrayList<>();//信息整体的位置
    private boolean        isSetInfoMagin            = false;
    private int            infoMaginLeft             = 0;
    private int            infoMaginRight            = 0;
    private int            infoMaginTop              = 0;
    private int            infoMaginBottom           = 0;

    private int            coverSize        = 0;//中间封面的大小,宽高一样
    private CoverType      coverType        = CoverType.ROUND;//中间封面的类型,圆角或者原型,OVAL是圆角,ROUND是圆形
    private List<AdLayout> coverLayout      = new ArrayList<>();//中间封面的位置
    private boolean        isSetCoverMagin  = false;
    private int            coverMaginLeft   = 0;
    private int            coverMaginRight  = 0;
    private int            coverMaginTop    = 0;
    private int            coverMaginBottom = 0;

    private Drawable       musicBtPlayImage;//中间封面--播放按钮的本地图片地址
    private Drawable       musicBtPauseImage;//中间封面--暂停按钮的本丢图片地址
    private int            musicBtSize        = 0;//中间封面--播放暂停按钮的宽度高度
    private List<AdLayout> musicBtLayout      = new ArrayList<>();//中间封面--播放暂停按钮的位置
    private boolean        isToCover          = true;//是否基于封面设置按钮的位置
    private boolean        isSetMusicBtMagin  = false;
    private int            musicBtMaginLeft   = 0;
    private int            musicBtMaginRight  = 0;
    private int            musicBtMaginTop    = 0;
    private int            musicBtMaginBottom = 0;

    private int            adHeadSize          = 0;//logo的大小,宽高一样
    private CoverType      adHeadType          = CoverType.ROUND;//logo的类型,圆角或者原型,OVAL是圆角,ROUND是圆形
    private List<AdLayout> adHeadLayout        = new ArrayList<>();//logo的位置
    private boolean        isShowHeadLinkImage = true;
    private boolean        isSetHeadMagin      = false;
    private int            adHeadMaginLeft     = 0;
    private int            adHeadMaginRight    = 0;
    private int            adHeadMaginTop      = 0;
    private int            adHeadMaginBottom   = 0;

    private Drawable       praiseChooseImage;//点赞的图片
    private Drawable       praiseDefaultImage;//未点赞的图片
    private int            praiseImageSize        = 0;//点赞的大小,宽高一样
    private List<AdLayout> praiseImageLayout      = new ArrayList<>();//点赞图片的位置
    private boolean        isSetPraiseImageMagin  = false;
    private int            praiseImageMaginLeft   = 0;
    private int            praiseImageMaginRight  = 0;
    private int            praiseImageMaginTop    = 0;
    private int            praiseImageMaginBottom = 0;

    private int            praiseNumberSize        = 0;//点赞数量的尺寸
    private int            praiseNumberWidth       = 0;//点赞数量控件的宽度
    private int            praiseNumberColor       = 0;//点赞数量的颜色
    private List<AdLayout> praiseNumberLayout      = new ArrayList<>();//点赞数量的位置
    private boolean        isSetPraiseNumberMagin  = false;
    private int            praiseNumberMaginLeft   = 0;
    private int            praiseNumberMaginRight  = 0;
    private int            praiseNumberMaginTop    = 0;
    private int            praiseNumberMaginBottom = 0;

    private Drawable       barrageImage;           //弹幕的图片
    private int            barrageImageSize        = 0;//弹幕图片的大小,宽高一样
    private List<AdLayout> barrageImageLayout      = new ArrayList<>();//弹幕图片按钮的位置
    private boolean        isSetBarrageImageMagin  = false;
    private int            barrageImageMaginLeft   = 0;
    private int            barrageImageMaginRight  = 0;
    private int            barrageImageMaginTop    = 0;
    private int            barrageImageMaginBottom = 0;

    private int            barrageNumberSize        = 0;//弹幕数量的尺寸
    private int            barrageNumberWidth       = 0;//弹幕数量控件的宽度
    private int            barrageNumberColor       = 0;//弹幕数量的颜色
    private List<AdLayout> barrageNumberLayout      = new ArrayList<>();//弹幕数量的位置
    private boolean        isSetBarrageNumberMagin  = false;
    private int            barrageNumberMaginLeft   = 0;
    private int            barrageNumberMaginRight  = 0;
    private int            barrageNumberMaginTop    = 0;
    private int            barrageNumberMaginBottom = 0;

    private boolean                isShowBarrage       = true;//是否展示滚动弹幕
    private int                    barrageContentSize  = 0;//滚动弹幕内容大小
    private List<String>           barrageContentColor = new ArrayList<>();//滚动弹幕内容颜色
    private String                 barrageBackColor;//滚动弹幕背景颜色
    private int                    barrageHeadSize     = 0;//滚动弹幕头像大小
    private int                    barrageHeight       = 0;//滚动弹幕的高度
    private int                    barrageSpeed        = 3;//滚动弹幕的速度
    private boolean                isSetBarrageMagin   = false;
    private int                    barrageMaginLeft    = 0;
    private int                    barrageMaginRight   = 0;
    private int                    barrageMaginTop     = 0;
    private int                    barrageMaginBottom  = 0;
    private List<UserPlayInfoBean> label;//播放广告前用户播放的音频信息

    private boolean skipIsEnable     = false;//是否启用跳过
    private int     skipGravity      = 0;//跳过位置
    private int     skipMarginLeft   = 0;
    private int     skipMarginTop    = 0;
    private int     skipMarginRight  = 0;
    private int     skipMarginBottom = 0;
    private boolean skipAutoClose    = false;//倒计时结束是否自动关闭该广告

    private boolean isEnableRightView = true;//设置是否启用右侧菜单

    //私有的构造方法，防止随意创建实例
    private AdAttr () {
    }

    /**
     * newBuild创建实例
     */
    public static AdAttr newBuild () {
        sAdAttr = new AdAttr();
        return sAdAttr;
    }

    public AdAttr setAdid (String adid) {
        this.adid = adid;
        return this;
    }

    public String getAdid () {
        return adid;
    }

    public AdAttr setMid (String mid) {
        this.mid = mid;
        return this;
    }

    public String getMid () {
        return mid;
    }

    public int getBackgroundWidth () {
        return backgroundWidth;
    }

    public AdAttr setBackgroundSize (int backgroundWidth, int backgroundHeight) {
        this.backgroundWidth = backgroundWidth;
        this.backgroundHeight = backgroundHeight;
        isSetBackgroundSize = true;
        return this;
    }

    public boolean isSetBackgroundSize () {
        return isSetBackgroundSize;
    }

    public int getBackgroundHeight () {
        return backgroundHeight;
    }

    public List<AdLayout> getBackgroundLayout () {
        return backgroundLayout;
    }

    public AdAttr setBackgroundLayout (AdLayout backgroundLayout) {
        this.backgroundLayout.add(backgroundLayout);
        return this;
    }

    public AdAttr setBackgroundMagin (int left, int top, int right, int bottom) {
        this.backgroundMaginLeft = left;
        this.backgroundMaginRight = right;
        this.backgroundMaginTop = top;
        this.backgroundMaginBottom = bottom;
        isSetBackgroundMagin = true;
        return this;
    }

    public boolean isSetBackgroundMagin () {
        return isSetBackgroundMagin;
    }

    public int getBackgroundMaginLeft () {
        return backgroundMaginLeft;
    }

    public int getBackgroundMaginRight () {
        return backgroundMaginRight;
    }

    public int getBackgroundMaginTop () {
        return backgroundMaginTop;
    }

    public int getBackgroundMaginBottom () {
        return backgroundMaginBottom;
    }

    public int getTitleColor () {
        return titleColor;
    }

    public AdAttr setTitleColor (@ColorInt int titleColor) {
        this.titleColor = titleColor;
        return this;
    }

    public int getContentColor () {
        return contentColor;
    }

    public AdAttr setContentColor (@ColorInt int contentColor) {
        this.contentColor = contentColor;
        return this;
    }

    public int getTitleSize () {
        return titleSize;
    }

    public AdAttr setTitleSize (int titleSize) {
        if (titleSize > 18) {
            this.titleSize = 18;
        } else if (titleSize < 10) {
            this.titleSize = 10;
        } else {
            this.titleSize = titleSize;
        }
        return this;
    }

    public List<AdLayout> getTitleLayout () {
        return titleLayout;
    }

    public AdAttr setTitleLayout (AdLayout titleLayout) {
        this.titleLayout.add(titleLayout);
        return this;
    }

    public AdAttr setTitleMagin (int left, int top, int right, int bottom) {
        this.titleMaginLeft = left;
        this.titleMaginRight = right;
        this.titleMaginTop = top;
        this.titleMaginBottom = bottom;
        isSetTitleMagin = true;
        return this;
    }

    public int getTitleMaginLeft () {
        return titleMaginLeft;
    }

    public int getTitleMaginRight () {
        return titleMaginRight;
    }

    public int getTitleMaginTop () {
        return titleMaginTop;
    }

    public int getTitleMaginBottom () {
        return titleMaginBottom;
    }

    public boolean isSetTitleMagin () {
        return isSetTitleMagin;
    }

    public int getTitleTextMaxSize () {
        return titleTextMaxSize;
    }

    public AdAttr setTitleTextMaxSize (int titleTextSize) {
        this.titleTextMaxSize = titleTextSize;
        return this;
    }

    public int getTitleTextMaxLines () {
        return titleTextMaxLines;
    }

    public AdAttr setTitleTextMaxLines (int titleTextMaxLines) {
        this.titleTextMaxLines = titleTextMaxLines;
        return this;
    }

    public int getContentSize () {
        return contentSize;
    }

    public AdAttr setContentSize (int contentSize) {
        if (contentSize > 14) {
            this.contentSize = 14;
        } else if (contentSize < 10) {
            this.contentSize = 10;
        } else {
            this.contentSize = contentSize;
        }
        return this;
    }

    public int getContentMaxLine () {
        return contentMaxLine;
    }

    private AdAttr setContentMaxLine (int contentMaxLine) {
        this.contentMaxLine = contentMaxLine;
        return this;
    }

    public List<AdLayout> getContentLayout () {
        return contentLayout;
    }

    public AdAttr setContentLayout (AdLayout contentLayout) {
        this.contentLayout.add(contentLayout);
        return this;
    }

    public AdAttr setContentMagin (int left, int top, int right, int bottom) {
        this.contentMaginLeft = left;
        this.contentMaginTop = top;
        this.contentMaginRight = right;
        this.contentMaginBottom = bottom;
        isSetContentMagin = true;
        return this;
    }

    public int getContentMaginLeft () {
        return contentMaginLeft;
    }

    public int getContentMaginRight () {
        return contentMaginRight;
    }

    public int getContentMaginTop () {
        return contentMaginTop;
    }

    public int getContentMaginBottom () {
        return contentMaginBottom;
    }

    public boolean isSetContentMagin () {
        return isSetContentMagin;
    }

    public int getInfoTitleColor () {
        return infoTitleColor;
    }

    public AdAttr setInfoTitleColor (@ColorInt int infoTitleColor) {
        this.infoTitleColor = infoTitleColor;
        return this;
    }

    public int getInfoTitleSize () {
        return infoTitleSize;
    }

    private AdAttr setInfoTitleSize (int infoTitleSize) {
        this.infoTitleSize = infoTitleSize;
        return this;
    }

    public int getInfoContentColor () {
        return infoContentColor;
    }

    public AdAttr setInfoContentColor (@ColorInt int infoContentColor) {
        this.infoContentColor = infoContentColor;
        return this;
    }

    public int getInfoContentSize () {
        return infoContentSize;
    }

    private AdAttr setInfoContentSize (int infoContentSize) {
        this.infoContentSize = infoContentSize;
        return this;
    }

    public int getInfoButtonColor () {
        return infoButtonColor;
    }

    public AdAttr setInfoButtonColor (@ColorInt int infoButtonColor) {
        this.infoButtonColor = infoButtonColor;
        return this;
    }

    public int getInfoButtonBackgroundColor () {
        return infoButtonBackgroundColor;
    }

    public AdAttr setInfoButtonBackgroundColor (@ColorInt int infoButtonBackgroundColor) {
        this.infoButtonBackgroundColor = infoButtonBackgroundColor;
        return this;
    }

    public List<AdLayout> getInfoLayout () {
        return infoLayout;
    }

    public AdAttr setInfoLayout (AdLayout titleLayout) {
        this.infoLayout.add(titleLayout);
        return this;
    }

    public boolean isSetInfoMagin () {
        return isSetInfoMagin;
    }

    public int getInfoMaginLeft () {
        return infoMaginLeft;
    }

    public int getInfoMaginRight () {
        return infoMaginRight;
    }

    public int getInfoMaginTop () {
        return infoMaginTop;
    }

    public int getInfoMaginBottom () {
        return infoMaginBottom;
    }

    public AdAttr setInfoMagin (int left, int top, int right, int bottom) {
        this.infoMaginLeft = left;
        this.infoMaginTop = top;
        this.infoMaginRight = right;
        this.infoMaginBottom = bottom;
        isSetInfoMagin = true;
        return this;
    }


    public int getAdHeadSize () {
        return adHeadSize;
    }

    public AdAttr setAdHeadSize (int adHeadSize) {
        this.adHeadSize = adHeadSize;
        return this;
    }

    public CoverType getAdHeadType () {
        return adHeadType;
    }

    public AdAttr setAdHeadType (CoverType adHeadType) {
        this.adHeadType = adHeadType;
        return this;
    }

    public List<AdLayout> getAdHeadLayout () {
        return adHeadLayout;
    }

    public AdAttr setAdHeadLayout (AdLayout adHeadLayout) {
        this.adHeadLayout.add(adHeadLayout);
        return this;
    }

    public boolean isShowHeadLinkImage () {
        return isShowHeadLinkImage;
    }

    public AdAttr setShowHeadLinkImage (boolean showHeadLinkImage) {
        isShowHeadLinkImage = showHeadLinkImage;
        return this;
    }

    public boolean isSetHeadMagin () {
        return isSetHeadMagin;
    }

    public int getAdHeadMaginLeft () {
        return adHeadMaginLeft;
    }

    public int getAdHeadMaginRight () {
        return adHeadMaginRight;
    }

    public int getAdHeadMaginTop () {
        return adHeadMaginTop;
    }

    public int getAdHeadMaginBottom () {
        return adHeadMaginBottom;
    }

    public AdAttr setAdHeadMagin (int left, int top, int right, int bottom) {
        this.adHeadMaginLeft = left;
        this.adHeadMaginTop = top;
        this.adHeadMaginRight = right;
        this.adHeadMaginBottom = bottom;
        isSetHeadMagin = true;
        return this;
    }

    public boolean isSetPraiseImageMagin () {
        return isSetPraiseImageMagin;
    }

    public int getPraiseImageMaginLeft () {
        return praiseImageMaginLeft;
    }

    public int getPraiseImageMaginRight () {
        return praiseImageMaginRight;
    }

    public int getPraiseImageMaginTop () {
        return praiseImageMaginTop;
    }

    public int getPraiseImageMaginBottom () {
        return praiseImageMaginBottom;
    }

    public AdAttr setPraiseImageMagin (int left, int top, int right, int bottom) {
        this.praiseImageMaginLeft = left;
        this.praiseImageMaginTop = top;
        this.praiseImageMaginRight = right;
        this.praiseImageMaginBottom = bottom;
        isSetPraiseImageMagin = true;
        return this;
    }

    public Drawable getPraiseChooseImage () {
        return praiseChooseImage;
    }

    public AdAttr setPraiseChooseImage (Drawable praiseChooseImage) {
        this.praiseChooseImage = praiseChooseImage;
        return this;
    }

    public Drawable getPraiseDefaultImage () {
        return praiseDefaultImage;
    }

    public AdAttr setPraiseDefaultImage (Drawable praiseDefaultImage) {
        this.praiseDefaultImage = praiseDefaultImage;
        return this;
    }

    public int getPraiseImageSize () {
        return praiseImageSize;
    }

    public AdAttr setPraiseImageSize (int praiseImageSize) {
        this.praiseImageSize = praiseImageSize;
        return this;
    }

    public List<AdLayout> getPraiseImageLayout () {
        return praiseImageLayout;
    }

    public AdAttr setPraiseImageLayout (AdLayout praiseImageLayout) {
        this.praiseImageLayout.add(praiseImageLayout);
        return this;
    }

    public int getPraiseNumberSize () {
        return praiseNumberSize;
    }

    public AdAttr setPraiseNumberSize (int praiseNumberSize) {
        this.praiseNumberSize = praiseNumberSize;
        return this;
    }

    public int getPraiseNumberWidth () {
        return praiseNumberWidth;
    }

    public AdAttr setPraiseNumberWidth (int praiseNumberWidth) {
        this.praiseNumberWidth = praiseNumberWidth;
        return this;
    }

    public int getPraiseNumberColor () {
        return praiseNumberColor;
    }

    public AdAttr setPraiseNumberColor (@ColorInt int praiseNumberColor) {
        this.praiseNumberColor = praiseNumberColor;
        return this;
    }

    public boolean isSetPraiseNumberMagin () {
        return isSetPraiseNumberMagin;
    }

    public int getPraiseNumberMaginLeft () {
        return praiseNumberMaginLeft;
    }

    public int getPraiseNumberMaginRight () {
        return praiseNumberMaginRight;
    }

    public int getPraiseNumberMaginTop () {
        return praiseNumberMaginTop;
    }

    public int getPraiseNumberMaginBottom () {
        return praiseNumberMaginBottom;
    }

    public AdAttr setPraiseNumberMagin (int left, int top, int right, int bottom) {
        this.praiseNumberMaginLeft = left;
        this.praiseNumberMaginTop = top;
        this.praiseNumberMaginRight = right;
        this.praiseNumberMaginBottom = bottom;
        isSetPraiseNumberMagin = true;
        return this;
    }

    public List<AdLayout> getPraiseNumberLayout () {
        return praiseNumberLayout;
    }

    public AdAttr setPraiseNumberLayout (AdLayout praiseLayout) {
        this.praiseNumberLayout.add(praiseLayout);
        return this;
    }

    public Drawable getBarrageImage () {
        return barrageImage;
    }

    public AdAttr setBarrageImage (Drawable barrageImage) {
        this.barrageImage = barrageImage;
        return this;
    }

    public int getBarrageImageSize () {
        return barrageImageSize;
    }

    public AdAttr setBarrageImageSize (int barrageImageSize) {
        this.barrageImageSize = barrageImageSize;
        return this;
    }

    public List<AdLayout> getBarrageImageLayout () {
        return barrageImageLayout;
    }

    public AdAttr setBarrageImageLayout (AdLayout barrageImageLayout) {
        this.barrageImageLayout.add(barrageImageLayout);
        return this;
    }

    public boolean isSetBarrageImageMagin () {
        return isSetBarrageImageMagin;
    }

    public int getBarrageImageMaginLeft () {
        return barrageImageMaginLeft;
    }

    public int getBarrageImageMaginRight () {
        return barrageImageMaginRight;
    }

    public int getBarrageImageMaginTop () {
        return barrageImageMaginTop;
    }

    public int getBarrageImageMaginBottom () {
        return barrageImageMaginBottom;
    }

    public AdAttr setBarrageImageMagin (int left, int top, int right, int bottom) {
        this.barrageImageMaginLeft = left;
        this.barrageImageMaginTop = top;
        this.barrageImageMaginRight = right;
        this.barrageImageMaginBottom = bottom;
        isSetBarrageImageMagin = true;
        return this;
    }

    public int getBarrageNumberSize () {
        return barrageNumberSize;
    }

    public AdAttr setBarrageNumberSize (int barrageNumberSize) {
        this.barrageNumberSize = barrageNumberSize;
        return this;
    }

    public int getBarrageNumberWidth () {
        return barrageNumberWidth;
    }

    public AdAttr setBarrageNumberWidth (int barrageNumberWidth) {
        this.barrageNumberWidth = barrageNumberWidth;
        return this;
    }

    public int getBarrageNumberColor () {
        return barrageNumberColor;
    }

    public AdAttr setBarrageNumberColor (@ColorInt int barrageNumberColor) {
        this.barrageNumberColor = barrageNumberColor;
        return this;
    }

    public List<AdLayout> getBarrageNumberLayout () {
        return barrageNumberLayout;
    }

    public AdAttr setBarrageNumberLayout (AdLayout barrageNumberLayout) {
        this.barrageNumberLayout.add(barrageNumberLayout);
        return this;
    }

    public boolean isSetBarrageNumberMagin () {
        return isSetBarrageNumberMagin;
    }

    public int getBarrageNumberMaginLeft () {
        return barrageNumberMaginLeft;
    }

    public int getBarrageNumberMaginRight () {
        return barrageNumberMaginRight;
    }

    public int getBarrageNumberMaginTop () {
        return barrageNumberMaginTop;
    }

    public int getBarrageNumberMaginBottom () {
        return barrageNumberMaginBottom;
    }

    public AdAttr setBarrageNumberMagin (int left, int top, int right, int bottom) {
        this.barrageNumberMaginLeft = left;
        this.barrageNumberMaginTop = top;
        this.barrageNumberMaginRight = right;
        this.barrageNumberMaginBottom = bottom;
        isSetBarrageNumberMagin = true;
        return this;
    }

    public int getCoverSize () {
        return coverSize;
    }

    public AdAttr setCoverSize (int coverSize) {
        this.coverSize = coverSize;
        return this;
    }

    public CoverType getCoverType () {
        return coverType;
    }

    public AdAttr setCoverType (CoverType coverType) {
        this.coverType = coverType;
        return this;
    }

    public List<AdLayout> getCoverLayout () {
        return coverLayout;
    }

    public AdAttr setCoverLayout (AdLayout coverLayout) {
        this.coverLayout.add(coverLayout);
        return this;
    }

    public boolean isSetCoverMagin () {
        return isSetCoverMagin;
    }

    public int getCoverMaginLeft () {
        return coverMaginLeft;
    }

    public int getCoverMaginRight () {
        return coverMaginRight;
    }

    public int getCoverMaginTop () {
        return coverMaginTop;
    }

    public int getCoverMaginBottom () {
        return coverMaginBottom;
    }

    public AdAttr setCoverMagin (int left, int top, int right, int bottom) {
        this.coverMaginLeft = left;
        this.coverMaginTop = top;
        this.coverMaginRight = right;
        this.coverMaginBottom = bottom;
        isSetCoverMagin = true;
        return this;
    }

    public Drawable getMusicBtPlayImage () {
        return musicBtPlayImage;
    }

    public AdAttr setMusicBtPlayImage (Drawable musicBtPlayImage) {
        this.musicBtPlayImage = musicBtPlayImage;
        return this;
    }

    public Drawable getMusicBtPauseImage () {
        return musicBtPauseImage;
    }

    public AdAttr setMusicBtPauseImage (Drawable musicBtPauseImage) {
        this.musicBtPauseImage = musicBtPauseImage;
        return this;
    }

    public int getMusicBtSize () {
        return musicBtSize;
    }

    public AdAttr setMusicBtSize (int musicBtSize) {
        this.musicBtSize = musicBtSize;
        return this;
    }

    public List<AdLayout> getMusicBtLayout () {
        return musicBtLayout;
    }

    public AdAttr setMusicBtLayout (AdLayout musicBtLayout) {
        this.musicBtLayout.add(musicBtLayout);
        return this;
    }

    public AdAttr setMusicBtLayout (AdLayout musicBtLayout, boolean isToCover) {
        this.musicBtLayout.add(musicBtLayout);
        this.isToCover = isToCover;
        return this;
    }

    public boolean isToCover () {
        return isToCover;
    }

    public boolean isSetMusicBtMagin () {
        return isSetMusicBtMagin;
    }

    public int getMusicBtMaginLeft () {
        return musicBtMaginLeft;
    }

    public int getMusicBtMaginRight () {
        return musicBtMaginRight;
    }

    public int getMusicBtMaginTop () {
        return musicBtMaginTop;
    }

    public int getMusicBtMaginBottom () {
        return musicBtMaginBottom;
    }

    public AdAttr setMusicBtMagin (int left, int top, int right, int bottom) {
        this.musicBtMaginLeft = left;
        this.musicBtMaginTop = top;
        this.musicBtMaginRight = right;
        this.musicBtMaginBottom = bottom;
        isSetMusicBtMagin = true;
        return this;
    }

    public boolean isShowBarrage () {
        return isShowBarrage;
    }

    public AdAttr setShowBarrage (boolean showBarrage) {
        isShowBarrage = showBarrage;
        return this;
    }

    public int getBarrageContentSize () {
        return barrageContentSize;
    }

    public AdAttr setBarrageContentSize (int barrageContentSize) {
        this.barrageContentSize = barrageContentSize;
        return this;
    }

    public List<String> getBarrageContentColor () {
        return barrageContentColor;
    }

    public AdAttr setBarrageContentColor (List<String> barrageContentColor) {
        this.barrageContentColor = barrageContentColor;
        return this;
    }

    public int getBarrageHeadSize () {
        return barrageHeadSize;
    }

    public AdAttr setBarrageHeadSize (int barrageHeadSize) {
        this.barrageHeadSize = barrageHeadSize;
        return this;
    }

    public int getBarrageHeight () {
        return barrageHeight;
    }

    private AdAttr setBarrageHeight (int barrageHeight) {
        this.barrageHeight = barrageHeight;
        return this;
    }

    public String getBarrageBackColor () {
        return barrageBackColor;
    }

    public AdAttr setBarrageBackColor (String barrageBackColor) {
        this.barrageBackColor = barrageBackColor;
        return this;
    }

    public AdAttr setBarrageMagin (int left, int top, int right, int bottom) {
        this.barrageMaginLeft = left;
        this.barrageMaginTop = top;
        this.barrageMaginRight = right;
        this.barrageMaginBottom = bottom;
        isSetBarrageMagin = true;
        return this;
    }

    public boolean isSetBarrageMagin () {
        return isSetBarrageMagin;
    }

    public int getBarrageMaginLeft () {
        return barrageMaginLeft;
    }

    public int getBarrageMaginRight () {
        return barrageMaginRight;
    }

    public int getBarrageMaginTop () {
        return barrageMaginTop;
    }

    public int getBarrageMaginBottom () {
        return barrageMaginBottom;
    }

    public AdAttr setCanLeftTouch (boolean isCanLrTouch) {
        this.isCanLeftTouch = isCanLrTouch;
        return this;
    }

    public boolean isCanLeftTouch () {
        return isCanLeftTouch;
    }

    public boolean isCanPause () {
        return isCanPause;
    }

    public AdAttr setCanPause (boolean canPause) {
        isCanPause = canPause;
        return this;
    }

    public int getBarrageSpeed () {
        return barrageSpeed;
    }

    public AdAttr setBarrageSpeed (int barrageSpeed) {
        this.barrageSpeed = barrageSpeed;
        return this;
    }

    public List<UserPlayInfoBean> getLabel () {
        return label;
    }

    public AdAttr setLabel (List<UserPlayInfoBean> label) {
        this.label = label;
        return this;
    }

    public boolean getSkipIsEnable () {
        return skipIsEnable;
    }

    public AdAttr setSkipIsEnable (boolean skipIsEnable) {
        this.skipIsEnable = skipIsEnable;
        return this;
    }

    public int getSkipGravity () {
        return skipGravity;
    }

    public AdAttr setSkipGravity (int skipGravity) {
        this.skipGravity = skipGravity;
        return this;
    }

    public AdAttr setSkipMargin (int left, int top, int right, int bottom) {
        this.skipMarginLeft = left;
        this.skipMarginTop = top;
        this.skipMarginRight = right;
        this.skipMarginBottom = bottom;
        return this;
    }

    public int getSkipMarginLeft () {
        return skipMarginLeft;
    }

    public int getSkipMarginTop () {
        return skipMarginTop;
    }

    public int getSkipMarginRight () {
        return skipMarginRight;
    }

    public int getSkipMarginBottom () {
        return skipMarginBottom;
    }

    public boolean getSkipAutoClose () {
        return skipAutoClose;
    }

    public AdAttr setSkipAutoClose (boolean skipAutoClose) {
        this.skipAutoClose = skipAutoClose;
        return this;
    }

    public boolean isEnableRightView () {
        return isEnableRightView;
    }

    public AdAttr setEnableRightView (boolean enableRightView) {
        isEnableRightView = enableRightView;
        return this;
    }
}
