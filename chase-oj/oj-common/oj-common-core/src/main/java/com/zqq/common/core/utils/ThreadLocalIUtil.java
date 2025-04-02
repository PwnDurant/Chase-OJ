package com.zqq.common.core.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * ThreadLocalIUtil 这个类的作用是基于 TransmittableThreadLocal 实现线程本地变量存储，
 * 并且支持在子线程中传递上下文信息，用于存储和获取线程范围内的数据
 */
public class ThreadLocalIUtil {


    /**
     * TransmittableThreadLocal 是 阿里巴巴开源的 TTL（TransmittableThreadLocal）库 提供的类，
     * 它能够在 线程池 场景下正确传递 ThreadLocal 变量，解决 ThreadLocal 在线程池中丢失的问题。
     * 通过 THREAD_LOCAL 维护一个 Map<String, Object>，用于存储多个键值对的数据，而不是单个值。
     */
    private static final TransmittableThreadLocal<Map<String,Object>> THREAD_LOCAL=new TransmittableThreadLocal<>();

    /**
     * 	•	set(String key, Object value): 存入数据
     * 	•	get(String key, Class<T> clazz): 获取数据，并进行类型转换
     * 	•	getLocalMap(): 获取当前线程的 Map<String, Object>，如果为空则初始化
     * 	•	remove(): 清除当前线程的上下文
     * */

    public static void set(String key,Object value){
        Map<String,Object> map=getLocalMap();
        map.put(key,value==null? StrUtil.EMPTY:value);
    }

    public static <T> T get (String key,Class<T> clazz){
        Map<String,Object> map=getLocalMap();
        return (T)map.getOrDefault(key,null);
    }

    public static Map<String, Object> getLocalMap() {
        Map<String, Object> map = THREAD_LOCAL.get();
        if (map == null) {
            map = new ConcurrentHashMap<String, Object>();
            THREAD_LOCAL.set(map);
        }
        return map;
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }

}
