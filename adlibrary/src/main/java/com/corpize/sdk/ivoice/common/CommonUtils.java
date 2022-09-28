package com.corpize.sdk.ivoice.common;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Vibrator;
import android.view.View;

import com.corpize.sdk.R;
import com.corpize.sdk.ivoice.utils.LogUtils;
import com.corpize.sdk.ivoice.utils.MediaPlayerUtil;
import com.corpize.sdk.ivoice.utils.downloadinstaller.DonwloadSaveImg;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * author ：yh
 * date : 2019-08-05 09:55
 * description : 公用类
 */
public class CommonUtils {

    private static final Application INSTANCE;
    private static       MediaPlayer mMediaPlayer;

    //获取系统音量(媒体音乐)
    public static int getSystemAudioVolume (Context context) {
        if(null == context){
            return 0;
        }
        AudioManager audioManager  = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int          currentVolume = 0;
        if (audioManager != null) {
            currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        } else {
            currentVolume = -1;
        }
        return currentVolume;
    }

    //获取全局的application
    public static Application get () {
        return INSTANCE;
    }

    static {
        Application app = null;
        try {
            app = (Application) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null);
            if (app == null)
                throw new IllegalStateException("Static initialization of Applications must be on main thread.");
        } catch (final Exception e) {
            e.printStackTrace();
            try {
                app = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null);
            } catch (final Exception ex) {
                e.printStackTrace();
            }
        } finally {
            INSTANCE = app;
        }
    }

    /**
     * 根据两个秒数 获取两个时间差
     */
    public static long getDateDistance (long lastDate, long nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        long ns = 1000;
        // 获得两个时间的秒时间差异
        long diff = nowDate - lastDate;
        // 计算差多少秒
        long sec = diff / ns;
        //输出结果
        return sec;
    }

    /**
     * 返回1到100的随机数
     */
    public static int getCompareRandow () {
        int    maxInt = 100;
        Random random = new Random();
        int    target = random.nextInt(maxInt) + 1;    //1到100的任意整数
        return target;
    }

    /**
     * 返回1到weight的随机数
     */
    public static int getCompareRandow (int weight) {
        if (weight != 0) {
            Random random = new Random();
            int    target = random.nextInt(weight) + 1;    //1到weigth的任意整数
            return target;
        } else {
            return 1;
        }
    }

    /**
     * 拨打电话（跳转到拨号界面，用户手动点击拨打）
     *
     * @param phoneNum 电话号码
     */
    public static void callPhone (Context context, String phoneNum) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            Uri    data   = Uri.parse("tel:" + phoneNum);
            intent.setData(data);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void callPhone (Activity activity, String phoneNum,int position) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            Uri    data   = Uri.parse("tel:" + phoneNum);
            intent.setData(data);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivityForResult(intent,position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放摇一摇声音
     *
     * @param context
     */
    public static void playShakeSound (Context context) {
        try {
            mMediaPlayer = MediaPlayer.create(context, R.raw.shake);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion (MediaPlayer mp) {
                    LogUtils.e("播放摇一摇完毕的监听");
                    //音量调整回去
                    MediaPlayerUtil.getInstance().releaseVolumeBack();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
            });
            mMediaPlayer.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放摇一摇声音
     *
     * @param context
     */
    public static void playSound (Context context) {
        SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 0);
        int       soundID   = soundPool.load(context, R.raw.shake, 1);
        soundPool.play(soundID, 1, 1, 0, 0, 1);
    }

    /**
     * 震动
     *
     * @param context
     * @param milliseconds
     */
    public static void vibrate (Context context, long milliseconds) {
        try {
            if(null == context){
                return;
            }
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(milliseconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取指定Activity的截屏，保存到png文件
    private static Bitmap takeScreenShot (Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        System.out.println(statusBarHeight);

        // 获取屏幕长和高
        int width  = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();

        // 去掉标题栏
        Bitmap b = Bitmap.createBitmap(bitmap, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    // 保存到sdcard
    private static void savePic (Bitmap b, String strFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void shoot (Activity activity) {
        DonwloadSaveImg.saveFile(takeScreenShot(activity),activity);
    }
}
