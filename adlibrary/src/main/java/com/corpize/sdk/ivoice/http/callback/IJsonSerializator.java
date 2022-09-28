package com.corpize.sdk.ivoice.http.callback;

/**
 * author ：yh
 * date : 2019-11-28 17:37
 * description :
 */
public interface IJsonSerializator {
    <T> T transform (String bean, Class<T> classOfT);
}
