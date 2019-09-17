package com.pressir.monitor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName Monitor
 * @Description TODO
 * @Author didi
 * @Date 2019-08-30 17:25
 */
public class Monitor {
    private static Map<String, Statistic> map = new ConcurrentHashMap<>();

    public static void init(String keyword, int interval) {
        if (map.get(keyword) == null) {
            map.put(keyword, new Statistic(interval));
        }
    }

    public static void onSend(String keyword) {
        map.get(keyword).onSend();
    }

    public static void onError(String keyword, Exception e, String msg) {
        map.get(keyword).onError(e, msg);
    }

    public static void onReceived(String keyword, int timeSpent) {
        map.get(keyword).onReceived(timeSpent);
    }

    public static void onStop() {
        map.values().forEach(Statistic::onStop);
    }

    public static void onConnect(String keyword) {
        map.get(keyword).onConnect();
    }
}
