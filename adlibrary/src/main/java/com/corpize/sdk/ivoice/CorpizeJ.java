package com.corpize.sdk.ivoice;

/**
 * author: yh
 * date: 2020-12-18 15:44
 * description: TODO:
 */
public class CorpizeJ {
    static {
        System.loadLibrary("corpize-lib");
    }

    public native String handleA (String a);

    public native String handleB (String a, String b);

}
