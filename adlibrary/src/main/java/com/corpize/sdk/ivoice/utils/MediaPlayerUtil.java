package com.corpize.sdk.ivoice.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.corpize.sdk.ivoice.QCiVoiceSdk;
import com.corpize.sdk.ivoice.bean.AdMusicBean;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * author ：yh
 * date : 2019-12-10 13:47
 * description : 音频播放工具类
 */
public class MediaPlayerUtil {

    private static      MediaPlayerUtil            sAudioRecorderUtil;
    private             Context                    mContext;
    private             MediaPlayer                mMediaPlayer;
    private             AudioManager               mAudioManager;//其他app播放时,需要请求音频焦点
    private             OnAudioFocusChangeListener mAudioFocusChangeListener = null;
    private             int                        mCurrentPosition          = 0;//当前播放的List的位置
    private             int                        mSize                     = 0;
    private             List<AdMusicBean>          mVoiceList;
    private             boolean                    mIsRelease                = true;//播放完成后是否释放MediaPlayer(不释放则说明有两个音频连续播放)
    //    private             WifiManager.WifiLock       mWifiLock;
    private             VolumeChangeManager        mVolumeChangeManager;
    private             int                        mMinVolume                = 0;//最低音量
    private             int                        mLastVolume               = -1;//原来的音量
    private             boolean                    isCanPlay                 = true;//是否可播放,停止后,准备回调里就不再start播放
    public static final int                        STOP                      = 0;//停止状态
    public static final int                        PLAY                      = 1;//播放状态
    public static final int                        PAUSE                     = 2;//暂停状态
    public              int                        currentPlayStatue         = STOP;//当前音频播放状态
    public              int                        mLastTime                 = -1;//最后的播放时间
    private             int                        userClickStop             = -1;//是否用户手动点击暂停，0否，1是
    private             float                      currentVolume;//当前音量
    private             int                        currentPlayerProgress     = 0;

    public void setCurrentPlayerProgress (int currentPlayerProgress) {
        this.currentPlayerProgress = currentPlayerProgress;
    }

    private int      mHandlerDelayTime = 330;
    private Handler  mHandler          = new Handler();
    private Runnable mRunnable         = new Runnable() {
        @Override
        public void run () {
            if (mMediaPlayer != null && mMediaOnListener != null) {
                int currentTime = mMediaPlayer.getCurrentPosition() / 1000;//音频总时长
                if (mLastTime != currentTime) {
                    mLastTime = currentTime;
                    mMediaOnListener.onPlayCurrentTimeListener(currentTime);
                }
            }

            if (mMediaPlayer != null && mMediaMoreOnListener != null) {
                int currentTime = mMediaPlayer.getCurrentPosition() / 1000;//音频总时长
                if (mLastTime != currentTime) {
                    mLastTime = currentTime;
                    mMediaMoreOnListener.onPlayCurrentTimeListener(mCurrentPosition, currentTime);
                }
            }

            if (isCanPlay && currentPlayStatue == PLAY) {
                if (mHandler != null) {
                    mHandler.postDelayed(this, mHandlerDelayTime);
                }
            }
        }
    };

    private Handler delayTimerHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage (@NonNull Message msg) {
            super.handleMessage(msg);
            delayPlayInteractionTimer = 0;
            continuePlay();
        }
    };

    private int delayPlayInteractionTimer = 0;

    public void setDelayPlayInteractionTimer (int delayPlayInteractionTimer) {
        this.delayPlayInteractionTimer = delayPlayInteractionTimer;
    }

    //单例模式
    public static MediaPlayerUtil getInstance () {
        if (sAudioRecorderUtil == null) {
            sAudioRecorderUtil = new MediaPlayerUtil();
        }
        return sAudioRecorderUtil;
    }

    private MediaPlayerUtil () {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
    }

    private void setCurrentVolume (int volume) {
        currentVolume = new BigDecimal(volume).divide(new BigDecimal(100)).floatValue();
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(currentVolume, currentVolume);
        }
    }

    public float getCurrentVolume () {
        return currentVolume;
    }

    public int getUserClickStop () {
        return userClickStop;
    }

    public void setUserClickStop (int userClickStop) {
        this.userClickStop = userClickStop;
    }

    /**
     * 播放音频文件
     *
     * @param voicePath       地址(本地或者网络)
     * @param mediaOnListener 回调
     */
    public void playVoice (Context context, String voicePath, final MediaOnListener mediaOnListener) {
        playVoice(context, voicePath, true, mediaOnListener);
    }

    /**
     * 停止播放
     */
    public void stopPlay () {
        isCanPlay = false;
        if (mMediaPlayer != null) {
            currentPlayStatue = STOP;
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
        }
    }

    /**
     * 暂停播放
     */
    public void pausePlay () {
        isCanPlay = true;
        if (mMediaPlayer != null) {
            currentPlayStatue = PAUSE;
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        }
    }

    /**
     * 当前播放状态
     */
    public boolean isPlaying () {
        return null == mMediaPlayer ? false : mMediaPlayer.isPlaying();
    }

    /**
     * 继续播放
     */
    public void resumePlay () {
        isCanPlay = true;
        if (mMediaPlayer != null) {
            currentPlayStatue = PLAY;
            mMediaPlayer.start();
            //请求焦点
            requestAudioFocus(mContext);
            if (mHandler != null) {
                mHandler.postDelayed(mRunnable, mHandlerDelayTime);
            }
        }
    }

    /**
     * 设置音频播放时的最低音量(0-100)
     */
    public void setMinVolume (int minVolume) {
        mMinVolume = minVolume;
        setCurrentVolume(minVolume);
    }

    /**
     * 获取当前播放的位置
     */
    public int getCurrentPosition () {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 播放单个音频
     *
     * @param isRelease 是否全部释放
     */
    public void playVoice (Context context, String voicePath, boolean isRelease, final MediaOnListener mediaOnListener) {
        if (null == context) {
            return;
        }
        mContext = context;
        mMediaOnListener = mediaOnListener;
        mIsRelease = isRelease;
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        mLastTime = -1;

        if (TextUtils.isEmpty(voicePath)) {
            if (mMediaOnListener != null) {
                mMediaOnListener.onPlayErrorListener(1000, "播放音频地址为空");
            }
            return;
        }
        isCanPlay = true;

        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        } else {
            mMediaPlayer.reset();
        }

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        //请求焦点
        requestAudioFocus(context);

        //获取音量回调
        requestVolumeBack(context);

//        //在后台时,开启WiFi的活跃状态
//        if (QCiVoiceSdk.get().isBackground()) {
//            startBackWifi(mContext);
//        }

        try {
            mMediaPlayer.setDataSource(voicePath);  //指定音频文件的路径
            mMediaPlayer.prepareAsync();            //让mediaplayer进入准备状态
            mMediaPlayer.setVolume(currentVolume, currentVolume);
        } catch (IOException e) {
            LogUtils.e("音频播放MediaPlayer初始化失败");
            if (mMediaOnListener != null) {
                mMediaOnListener.onPlayErrorListener(1001, "播放音频初始化失败");
            }
            stopAndRelease();
            e.printStackTrace();
        }

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared (final MediaPlayer mp) {
                if (isCanPlay) {
                    mp.start();//开始播放
                    mp.seekTo(currentPlayerProgress * 1000);
                    currentPlayStatue = PLAY;
                    // 在播放完毕被回调
                    if (mMediaOnListener != null) {
                        int allTime = mp.getDuration() / 1000;//音频总时长
                        mMediaOnListener.onPlayStartListener(allTime);
                    }
                    if (mHandler != null) {
                        mHandler.postDelayed(mRunnable, mHandlerDelayTime);//每一秒执行一次runnable
                    }
                }
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion (MediaPlayer mp) {
                // 在播放完毕被回调
                if (mMediaOnListener != null) {
                    mMediaOnListener.onPlayCompletionListener();
                }

                if (mIsRelease) {
                    //播放完毕释放资源
                    stopAndRelease();
                } else {
                    mMediaPlayer.reset();
                }

                currentPlayerProgress = 0;
            }
        });

        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError (MediaPlayer mp, int what, int extra) {
                // 在播放完毕被回调
                if (mMediaOnListener != null) {
                    mMediaOnListener.onPlayErrorListener(1002, "音频播放错误");
                }
                LogUtils.e("音频播放错误");

                stopAndRelease();

                currentPlayerProgress = 0;
                return true;
            }
        });

        mMediaPlayer.setOnTimedTextListener(new MediaPlayer.OnTimedTextListener() {
            @Override
            public void onTimedText (MediaPlayer mp, TimedText text) {
                if (text != null) {
                    String time = text.getText();
                    LogUtils.e("音频时间返回time=" + time + "|");
                }
            }
        });
    }

    /**
     * 播放音频文件
     *
     * @param voiceList 地址集合(本地或者网络)
     */
    public void playVoiceList (Context context, List<AdMusicBean> voiceList, final MediaMoreOnListener mediaOnListener) {
        mContext = context;
        mMediaMoreOnListener = mediaOnListener;
        mVoiceList = voiceList;
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        mLastTime = -1;

        //判断播放地址
        if (voiceList == null || voiceList.isEmpty()) {
            if (mMediaMoreOnListener != null) {
                mMediaMoreOnListener.onPlayErrorListener(1000, "播放音频地址为空");
            }
            return;
        }

        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        } else {
            mMediaPlayer.reset();
        }

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        //请求焦点
        requestAudioFocus(context);

        //获取音量回调
        requestVolumeBack(context);

        //设置播放的数据
        mSize = mVoiceList.size();
        mCurrentPosition = 0;
        String voicePath = mVoiceList.get(mCurrentPosition).getMusic();

        if (mVoiceList.get(mCurrentPosition).isCanBackground()) {
            voicePath = mVoiceList.get(mCurrentPosition).getBackMusic();
        }

        //监听播放事件
        initMediaListener();

        //设置地址并播放
        startMediaPlay(voicePath);
    }

    /**
     * 设置地址并播放(多个音频的资源设置)
     */
    private void startMediaPlay (String voicePath) {
        if (TextUtils.isEmpty(voicePath)) {
            if (mMediaMoreOnListener != null) {
                mMediaMoreOnListener.onPlayErrorListener(1000, "播放音频地址为空");
            }
            return;
        }

        isCanPlay = true;

//        //在后台时,开启WiFi的活跃状态
//        if (QCiVoiceSdk.get().isBackground()) {
//            startBackWifi(mContext);
//        }

        try {
            //指定音频文件的路径
            mMediaPlayer.setDataSource(voicePath);
            //让 Mediaplayer 进入准备状态
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            LogUtils.e("音频播放MediaPlayer初始化失败");
            if (mMediaOnListener != null) {
                mMediaOnListener.onPlayErrorListener(1001, "播放音频初始化失败");
            }
            if (mMediaMoreOnListener != null) {
                mMediaMoreOnListener.onPlayErrorListener(1001, "播放音频初始化失败");
            }
            e.printStackTrace();
        }
    }

    /**
     * 监听播放事件(多个音频的监听回调)
     */
    private void initMediaListener () {
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared (final MediaPlayer mp) {
                try {
                    if (isCanPlay) {
                        mp.start();//开始播放
                        mp.seekTo(currentPlayerProgress * 1000);
                        currentPlayStatue = PLAY;
                        if (mMediaMoreOnListener != null) {
                            int allTime = mp.getDuration() / 1000;//音频总时长
                            mMediaMoreOnListener.onPlayStartListener(mCurrentPosition, allTime);
                        }
                        if (mHandler != null) {
                            mHandler.postDelayed(mRunnable, 500);//每两秒执行一次runnable
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //播放的回调
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion (MediaPlayer mp) {
                int     lock_interaction_switch = SpUtils.getInt("lock_interaction_switch");
                boolean show                    = SpUtils.getBoolean("show");
                if (!show && 1 != lock_interaction_switch) {
                    playCompletion();
                    return;
                }

                mCurrentPosition += 1;

                //判断是否所有的音频都播放完毕了
                if (mCurrentPosition < mSize) {
                    if (mMediaMoreOnListener != null) {
                        mMediaMoreOnListener.onPlayCenterPositionListener(mCurrentPosition - 1);
                    }

                    if (0 == delayPlayInteractionTimer) {
                        continuePlay();
                    } else {
                        if (delayTimerHandler != null) {
                            delayTimerHandler.sendEmptyMessageDelayed(0x1001, delayPlayInteractionTimer * 1000);
                        }
                    }
                } else {
                    playCompletion();
                }

                currentPlayerProgress = 0;
            }
        });

        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError (MediaPlayer mp, int what, int extra) {
                // 在播放完毕被回调
                if (mMediaMoreOnListener != null) {
                    mMediaMoreOnListener.onPlayErrorListener(1002, "音频播放错误");
                }
                LogUtils.e("音频播放错误");
                stopAndRelease();

                currentPlayerProgress = 0;
                return true;
            }
        });
    }

    //继续播放音频
    private void continuePlay () {
        if (null == mVoiceList || mVoiceList.isEmpty()) {
            return;
        }

        String voicePath = mVoiceList.get(mCurrentPosition).getMusic();
        QCiVoiceSdk.get().setInteractionFlag(false);
        if (0 != mCurrentPosition) {
            boolean show = SpUtils.getBoolean("show");
            if (!ScreenUtils.isScreenOnAndUnlock()) {
                LogUtils.e("lockScreen:" + show);
                QCiVoiceSdk.get().setInteractionFlag(true);
                if (show) {
                    voicePath = mVoiceList.get(mCurrentPosition).getLockScreenMusicForFrontDesk();
                } else {
                    voicePath = mVoiceList.get(mCurrentPosition).getLockScreenMusicForBack();
                }
            } else if (!show) {
                LogUtils.e("back");
                QCiVoiceSdk.get().setInteractionFlag(true);
                voicePath = mVoiceList.get(mCurrentPosition).getBackMusic();
            }
        }

        if (TextUtils.isEmpty(voicePath)) {
            playCompletion();
        }

        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        }

        startMediaPlay(voicePath);
    }

    //播放结束
    private void playCompletion () {
        // 在播放完毕被回调
        if (mMediaMoreOnListener != null) {
            mMediaMoreOnListener.onPlayCompletionListener();
        }
        if (mIsRelease) {
            //播放完毕释放资源
            stopAndRelease();
        } else {
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
            }
        }
    }

    /**
     * 请求焦点
     */
    private void requestAudioFocus (Context context) {
        if (null == context) {
            return;
        }
        //下方是和其他app音乐冲突的处理
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }

        if (mAudioFocusChangeListener == null) {
            mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange (int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        //长时间失去焦点 pausePlay();
                        if (mMediaOnListener != null) {
                            mMediaOnListener.onAudioFocusChange(focusChange);
                        }

                        if (mMediaMoreOnListener != null) {
                            mMediaMoreOnListener.onAudioFocusChange(focusChange);
                        }

                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        //获得焦点
                        LogUtils.e("获得焦点");

                        if (mMediaOnListener != null) {
                            mMediaOnListener.onAudioFocusChange(focusChange);
                        }

                        if (mMediaMoreOnListener != null) {
                            mMediaMoreOnListener.onAudioFocusChange(focusChange);
                        }
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                        //临时丢失焦点 pausePlay();
                        if (mMediaOnListener != null) {
                            mMediaOnListener.onAudioFocusChange(focusChange);
                        }

                        if (mMediaMoreOnListener != null) {
                            mMediaMoreOnListener.onAudioFocusChange(focusChange);
                        }
                    }
                }
            };
        }

        if (mAudioManager != null) {
            //请求焦点
            int ret = mAudioManager.requestAudioFocus(mAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (ret != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {//请求成功
                //LogUtils.d("请求焦点成功");
            } else {
                //LogUtils.d("请求焦点失败");
            }
        }
    }

    /**
     * 暂停、播放完成或退到后台释放音频焦点
     */
    private void releaseAudioFocus () {
        if (mAudioManager != null && mAudioFocusChangeListener != null) {
            mAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
        }
    }

    /**
     * 注册获取音量回调
     */
    public void requestVolumeBack (Context context) {
        requestVolumeBack(context, true);
    }

    /**
     * 注册获取音量回调 ,是否记录最后的音量
     */
    public void requestVolumeBack (Context context, boolean isReadLastVolume) {
        if (mVolumeChangeManager != null) {
            mVolumeChangeManager.unregisterReceiver();
            mVolumeChangeManager = null;
        }
        mVolumeChangeManager = new VolumeChangeManager(context);
        //注册音量回调
        mVolumeChangeManager.setVolumeChangeListener(new VolumeChangeManager.VolumeChangeListener() {
            @Override
            public void onVolumeChanged (int volume) {
//                //设置最低音量,低于阈值则设置为最低音量
//                if (mMinVolum > 0 && volume <= mMinVolum) {
//                    mVolumeChangeManager.setMusicVolume100(mMinVolum);
//                }
            }
        });
        mVolumeChangeManager.registerReceiver();
        //设置当前的媒体音量
        int currentMusicVolume = mVolumeChangeManager.getLastVolum();
        if (isReadLastVolume) {
            mLastVolume = currentMusicVolume;
        }

        //TODO 屏蔽播放广告时阻止音量调整
//        if (currentMusicVolume < mMinVolume) {
//            mVolumeChangeManager.setMusicVolume100(mMinVolume);
//        }
    }

    /**
     * 获取音量回调取消注册,并把音量设置会原来的音量
     */
    public void releaseVolumeBack () {
        try {
            if (mVolumeChangeManager != null) {
                mVolumeChangeManager.unregisterReceiver();
                mVolumeChangeManager = null;

                //TODO 屏蔽播放广告时阻止音量调整
//                if (mLastVolume >= 0) {
//                    mVolumeChangeManager.setMusicVolume100(mLastVolume);
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放全部资源
     */
    public void stopAndRelease () {
        isCanPlay = false;
        currentPlayStatue = STOP;
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();//资源释放 关键
            mMediaPlayer.release();
            mMediaPlayer = null;
            if (mVoiceList != null) {
                mVoiceList.clear();
            }
            mCurrentPosition = 0;
            mSize = 0;
            releaseAudioFocus();
//            releaseBackWifi();
            mMediaOnListener = null;
            mMediaMoreOnListener = null;
            if (mHandler != null) {
                mHandler.removeCallbacks(mRunnable);
            }

            userClickStop = 0;
        }
        mContext = null;
    }

    /**
     * 接口回调
     */
    private MediaOnListener     mMediaOnListener;
    private MediaMoreOnListener mMediaMoreOnListener;

    public interface MediaOnListener {
        //开始播放
        void onPlayStartListener (int allTime);

        //播放进度回调
        void onPlayCurrentTimeListener (int currentTime);

        //播放完成
        void onPlayCompletionListener ();

        //播放失败
        void onPlayErrorListener (int code, String msg);

        //焦点获取及消失
        void onAudioFocusChange (int focusChange);
    }

    public interface MediaMoreOnListener {
        //开始播放
        void onPlayStartListener (int position, int allTime);

        //播放状态更改
        void onPlayStatusChangeListener (int position, int status);

        //播放进度回调
        void onPlayCurrentTimeListener (int position, int currentTime);

        //多音频全部播放完成
        void onPlayCompletionListener ();

        //不是最后一个音频播放完成
        void onPlayCenterPositionListener (int position);

        //播放失败
        void onPlayErrorListener (int code, String msg);

        //焦点获取及消失
        void onAudioFocusChange (int focusChange);
    }
}
