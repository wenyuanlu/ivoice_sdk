package com.corpize.sdk.ivoice.utils;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * author: yh
 * date: 2020-12-10 15:06
 * description: GZip压缩 请求里面不需要
 */
public class GZipUtil {
    private static final String GZIP_ENCODE_UTF_8 = "UTF-8";

    private static final String GZIP_ENCODE_ISO_8859_1 = "ISO-8859-1";

    /**
     * 字符串压缩为GZIP字符
     *
     * @param str
     */
    public static String compress (String str) {
        return compress(str, GZIP_ENCODE_UTF_8);
    }

    /**
     * 字符串压缩为GZIP字符
     *
     * @param str
     */
    public static byte[] compressByte (String str) {
        return compressByte(str, GZIP_ENCODE_UTF_8);
    }

    /**
     * 字符串压缩为GZIP字符
     *
     * @param str
     * @param encoding 压缩格式,比如Utf-8
     * @return
     */
    public static String compress (String str, String encoding) {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream out  = new ByteArrayOutputStream();
        GZIPOutputStream      gzip;
        String                back = "";
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes(encoding));
            gzip.close();
            back = out.toString(GZIP_ENCODE_UTF_8);
            //back = out.toString(GZIP_ENCODE_ISO_8859_1);
        } catch (IOException e) {
            Log.e("gzip compress error.", e.getMessage());
        }
        //return out.toByteArray();
        return back;
    }

    /**
     * 字符串压缩为GZIP字符
     *
     * @param str
     * @param encoding 压缩格式,比如Utf-8
     * @return
     */
    public static byte[] compressByte (String str, String encoding) {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream out  = new ByteArrayOutputStream();
        GZIPOutputStream      gzip;
        String                back = "";
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes(encoding));
            gzip.close();
        } catch (IOException e) {
            Log.e("gzip compress error.", e.getMessage());
        }
        return out.toByteArray();
    }

    /**
     * 字符串的解压
     *
     * @param str 对字符串解压
     * @return 返回解压缩后的字符串
     */
    public static String unCompress (String str) {
        if (null == str || str.length() <= 0) {
            return str;
        }
        String back = "";
        // 创建一个新的输出流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream  in  = null;
        try {
            // 创建一个 ByteArrayInputStream，使用 buf 作为其缓冲区数组
            //in = new ByteArrayInputStream(str.getBytes(GZIP_ENCODE_ISO_8859_1));
            in = new ByteArrayInputStream(str.getBytes(GZIP_ENCODE_UTF_8));
            // 使用默认缓冲区大小创建新的输入流
            GZIPInputStream gzip   = new GZIPInputStream(in);
            byte[]          buffer = new byte[256];
            int             n      = 0;

            // 将未压缩数据读入字节数组
            while ((n = gzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            back = out.toString(GZIP_ENCODE_UTF_8);
        } catch (IOException e) {
            Log.e("gzip compress error.", e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
        return back;
    }

    /**
     * Gzip  byte[] 解压成字符串
     *
     * @param bytes
     * @return
     */
    public static String unCompressToString (byte[] bytes) {
        return unCompressToString(bytes, GZIP_ENCODE_UTF_8);
    }

    /**
     * Gzip  byte[] 解压成字符串
     *
     * @param bytes
     * @param encoding
     * @return
     */
    public static String unCompressToString (byte[] bytes, String encoding) {
        String content = null;
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream  in  = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[]          buffer = new byte[256];
            int             n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            content = out.toString(encoding);
        } catch (IOException e) {
            Log.e("gzip compress error.", e.getMessage());
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return content;
    }


    /**
     * 判断byte[]是否是Gzip格式
     *
     * @param data
     * @return
     */
    public static boolean isGzip (byte[] data) {
        int header = (int) ((data[0] << 8) | data[1] & 0xFF);
        return header == 0x1f8b;
    }

}

