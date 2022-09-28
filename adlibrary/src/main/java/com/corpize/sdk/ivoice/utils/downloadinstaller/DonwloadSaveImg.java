package com.corpize.sdk.ivoice.utils.downloadinstaller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.corpize.sdk.ivoice.utils.ThreadManagerUtil;
import com.corpize.sdk.ivoice.utils.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * author ：yh
 * date : 2020-12-29 10:59
 * description : 下载图片
 */
public class DonwloadSaveImg {

    private static Context mContext;
    private static String  mImageUrl;
    private static Bitmap  mBitmap;
    private static String  mSaveMessage = "失败";
    //private static ProgressDialog mSaveDialog  = null;

    /**
     * 下载
     *
     * @param context
     * @param filePaths
     */
    public static void donwloadImg (Context context, String filePaths) {
        mContext = context;
        mImageUrl = filePaths;
        LogUtils.e("下载的图片地址=" + filePaths);
        //mSaveDialog = ProgressDialog.show(mContext, "保存图片", "图片正在保存中，请稍等...", true);
        if (!TextUtils.isEmpty(mImageUrl)) {
            ThreadManagerUtil.getDefaultProxy().execute(mSaveFileRunnable);
        }
    }

    /**
     * 下载线程
     */
    private static Runnable mSaveFileRunnable = new Runnable() {
        @Override
        public void run () {
            try {
                if (!TextUtils.isEmpty(mImageUrl)) { //网络图片
                    // 对资源链接
                    URL url = new URL(mImageUrl);
                    //打开输入流
                    InputStream inputStream = url.openStream();
                    //对网上资源进行下载转换位图图片
                    mBitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                }
                //保存图片
                saveFile(mBitmap);
                mSaveMessage = "图片保存成功！";
            } catch (IOException e) {
                mSaveMessage = "图片保存失败！";
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //messageHandler.sendMessage(messageHandler.obtainMessage());
        }
    };

    /*
    @SuppressLint ("HandlerLeak")
    private static Handler messageHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            mSaveDialog.dismiss();
            Log.d(TAG, mSaveMessage);
            Toast.makeText(mContext, mSaveMessage, Toast.LENGTH_SHORT).show();
        }
    };*/

    /**
     * 保存图片
     *
     * @param bitmap
     * @throws IOException
     */
    public static void saveFile (Bitmap bitmap) throws IOException {
        //设置获取文件的名称
        String applicationID        = mContext.getPackageName();
        String downloadImageMd5Name = getUpperMD5Str16(mImageUrl + applicationID) + ".jpg";
        String authority            = applicationID + ".QcDownloadProvider";//如果有通知栏,需要这个
        String storagePrefix        = mContext.getExternalFilesDir("qc_ad_download").getAbsolutePath() + "/";
        String storageImagePath     = storagePrefix + downloadImageMd5Name;
        LogUtils.e("保存的地址=" + storageImagePath);

        //获取文件大小
        URL               url  = new URL(mImageUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.connect();
        int length = conn.getContentLength();

        //文件夹
        File dirFile = new File(storagePrefix);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }

        //判断文件是否存在
        File imageFile = new File(storageImagePath);
        if (imageFile.exists()) {
            long fileLength = imageFile.length();
            if (fileLength == length) {
                //已经下载了
                LogUtils.e("优惠券已下载过");
                try {
                    bitmap.recycle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        //开始获取下载
        FileOutputStream fos = new FileOutputStream(imageFile);
        byte[]           buf = new byte[1024 * 4];

        InputStream is  = conn.getInputStream();
        int         len = -1;
        while ((len = is.read(buf)) != -1) {
            fos.write(buf, 0, len);     //在这里使用另一个重载，防止流写入的问题.
        }
        fos.flush();
        fos.close();
        is.close();

        //把图片保存后声明这个广播事件通知系统相册有新图片到来
        //其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(mContext.getContentResolver(), imageFile.getAbsolutePath(), downloadImageMd5Name, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 通知图库更新
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            MediaScannerConnection.scanFile(mContext, new String[]{imageFile.getAbsolutePath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted (String path, Uri uri) {
                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            mediaScanIntent.setData(uri);
                            mContext.sendBroadcast(mediaScanIntent);
                        }
                    });
        } else {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri    uri    = Uri.fromFile(imageFile);
            intent.setData(uri);
            mContext.sendBroadcast(intent);
        }


    }

    /**
     * 保存图片
     *
     * @param bitmap
     */
    public static void saveFile (Bitmap bitmap, Context context) {
        //设置获取文件的名称
        long   totalMilliSeconds    = System.currentTimeMillis();
        String downloadImageMd5Name = getUpperMD5Str16(totalMilliSeconds + "") + ".png";
        String storagePrefix        = context.getExternalFilesDir("qc_ad_download").getAbsolutePath() + "/";
        String storageImagePath     = storagePrefix + downloadImageMd5Name;
        LogUtils.e("截图保存的地址=" + storageImagePath);


        //文件夹
        File dirFile = new File(storagePrefix);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }

        //判断文件是否存在
        File imageFile = new File(storageImagePath);
        if (imageFile.exists()) {
            LogUtils.e("截图已保存过");
            return;
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            if (null != fos) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*//开始获取下载
        FileOutputStream fos = new FileOutputStream(imageFile);
        byte[]           buf = new byte[1024 * 4];

        InputStream is  = conn.getInputStream();
        int         len = -1;
        while ((len = is.read(buf)) != -1) {
            fos.write(buf, 0, len);     //在这里使用另一个重载，防止流写入的问题.
        }
        fos.flush();
        fos.close();
        is.close();*/

        //把图片保存后声明这个广播事件通知系统相册有新图片到来
        //其次把文件插入到系统图库
        /*try {
            MediaStore.Images.Media.insertImage(mContext.getContentResolver(), imageFile.getAbsolutePath(), downloadImageMd5Name, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 通知图库更新
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            MediaScannerConnection.scanFile(mContext, new String[]{imageFile.getAbsolutePath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted (String path, Uri uri) {
                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            mediaScanIntent.setData(uri);
                            mContext.sendBroadcast(mediaScanIntent);
                        }
                    });
        } else {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri    uri    = Uri.fromFile(imageFile);
            intent.setData(uri);
            mContext.sendBroadcast(intent);
        }*/


    }

    /**
     * 获取16位的MD5 值，大写
     *
     * @param str
     * @return
     */
    private static String getUpperMD5Str16 (String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            LogUtils.d("NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[]       byteArray  = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            } else {
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
            }
        }
        return md5StrBuff.toString().toUpperCase().substring(8, 24);
    }
}
