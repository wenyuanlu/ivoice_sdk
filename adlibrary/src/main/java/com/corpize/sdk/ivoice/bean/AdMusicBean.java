package com.corpize.sdk.ivoice.bean;

/**
 * author ：yh
 * date : 2020-12-11 12:02
 */
public class AdMusicBean {
    private String  music;//正常播放的音乐
    private String  backMusic;//后台播放的音频
    private boolean canBackground = false;//是否后台播放需要变更音频

    //锁屏时候的音频
    private String  lockScreenMusicForFrontDesk;
    private String  lockScreenMusicForBack;

    public AdMusicBean (String music) {
        this.music = music;
        this.backMusic = "";
        this.canBackground = false;
    }

    public AdMusicBean (String music, String backMusic) {
        this.music = music;
        this.backMusic = backMusic;
        this.canBackground = true;
    }

    public AdMusicBean (String music,
                        String backMusic,
                        String lockScreenMusicForFrontDesk,
                        String lockScreenMusicForBack
    ) {
        this.music = music;
        this.backMusic = backMusic;
        this.lockScreenMusicForFrontDesk = lockScreenMusicForFrontDesk;
        this.lockScreenMusicForBack = lockScreenMusicForBack;
    }

    public String getMusic () {
        return music;
    }

    public void setMusic (String music) {
        this.music = music;
    }

    public boolean isCanBackground () {
        return canBackground;
    }

    public void setCanBackground (boolean canBackground) {
        this.canBackground = canBackground;
    }


    public String getBackMusic () {
        return backMusic;
    }

    public void setBackMusic (String backMusic) {
        this.backMusic = backMusic;
    }

    public String getLockScreenMusicForFrontDesk () {
        return lockScreenMusicForFrontDesk;
    }

    public void setLockScreenMusicForFrontDesk (String lockScreenMusicForFrontDesk) {
        this.lockScreenMusicForFrontDesk = lockScreenMusicForFrontDesk;
    }

    public String getLockScreenMusicForBack () {
        return lockScreenMusicForBack;
    }

    public void setLockScreenMusicForBack (String lockScreenMusicForBack) {
        this.lockScreenMusicForBack = lockScreenMusicForBack;
    }
}
