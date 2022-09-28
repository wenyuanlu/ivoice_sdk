package com.corpize.sdk.ivoice.http.callback;

import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * author ：yh
 * date : 2020-12-30 14:51
 * description : 解耦网络请求
 */
public class CallBackTask<T> {

    private CallBackTask (boolean isM) {
        this.isM = isM;
        id = 0;
        map = new HashMap<>();
    }

    private boolean isM;//是否运行在多线程环境下(多线程会进行加锁操作,比较耗时)

    private long                              id;//唯一的key,依次递增
    /**
     * 存储CallBack的map
     * key为对象的内存地址+id
     * 值为对象的弱引用
     */
    private HashMap<String, WeakReference<T>> map;

    private final int ADD = 0,
            REMOVE_KEY    = 1,
            REMOVE_OBJECT = 2,
            GET           = 3,
            CLEAR         = 4;

    private static CallBackTask<BaseCallback> task = new CallBackTask<>(false);

    /**
     * 获取单实例,由于网络请求在应用中基本一直会用到,所以直接使用饿汉单例
     * ps:如果有需求需要同时存其他种类的回调,可以再创建一个这样的方法,new的泛型改一下就行
     */
    public static CallBackTask<BaseCallback> getInstance () {
        return task;
    }

    /**
     * 将回调存储在此,并返回key
     */
    public String add (T value) {
        return isM ? doubleThread(ADD, value).toString() : singleThread(ADD, value).toString();
    }

    /**
     * 根据key移除引用
     */
    public void remove (String key) {
        if (isM)
            doubleThread(REMOVE_KEY, key);
        else
            singleThread(REMOVE_KEY, key);
    }

    /**
     * 根据对象的地址移除对象
     */
    public void remove (T value) {
        if (isM)
            doubleThread(REMOVE_OBJECT, value);
        else
            singleThread(REMOVE_OBJECT, value);
    }

    /**
     * 根据key取出回调,需要判断返回值是否为null,若为null可能已经销毁(该方法的实现在get后会自动remove,如果不想如此可以自行修改方法)
     */
    public T get (String key) {
        Object t = isM ? doubleThread(GET, key) : singleThread(GET, key);
        return t == null ? null : (T) t;
    }

    /**
     * 清理所有是null值的对象
     */
    public void cleanUpNull () {
        if (isM)
            doubleThread(CLEAR, null);
        else
            singleThread(CLEAR, null);
    }

    /**
     * 单线程方法,isM为false时执行,需要调用者保证调用的都是在同一个线程,否则可能抛出ConcurrentModificationException
     */
    private Object singleThread (int state, Object obj) {
        switch (state) {
            case ADD: {
                if (obj == null)
                    return "";
                String key = obj.toString() + id;
                id++;
                map.put(key, new WeakReference<>((T) obj));
                return key;
            }
            case REMOVE_KEY: {
                if (obj == null || map.size() == 0)
                    return null;
                String stringKey = obj.toString();
                if (TextUtils.isEmpty(stringKey))
                    return null;
                map.remove(stringKey);
                break;
            }
            case REMOVE_OBJECT: {
                if (obj == null || map.size() == 0)
                    return null;
                Iterator<String> iterator = map.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    if (key.contains(obj.toString()))
                        iterator.remove();
                }
                break;
            }
            case GET: {
                if (obj == null || map.size() == 0)
                    return null;
                String stringKey = obj.toString();
                if (TextUtils.isEmpty(stringKey))
                    return null;
                WeakReference<T> tWeakReference = map.get(stringKey);
                if (tWeakReference == null) {
                    map.remove(stringKey);
                    return null;
                }
                T callBack = tWeakReference.get();
                map.remove(stringKey);
                return callBack;
            }
            case CLEAR: {
                if (map.size() == 0)
                    return null;
                Iterator<Map.Entry<String, WeakReference<T>>> iterator = map.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, WeakReference<T>> entry = iterator.next();
                    if (entry.getValue() == null)
                        iterator.remove();
                    else if (entry.getValue().get() == null)
                        iterator.remove();
                }
                break;
            }
        }
        return null;
    }

    /**
     * 多线程方法,isM为true时执行,无需担心抛异常,但是效率略低(相对于单线程方法)
     */
    private synchronized Object doubleThread (int state, Object obj) {
        return singleThread(state, obj);
    }
}
