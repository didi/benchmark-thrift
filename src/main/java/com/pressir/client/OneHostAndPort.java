package com.pressir.client;

import com.google.common.net.HostAndPort;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName OneHostAndPort
 * @Description TODO
 * @Author didi
 * @Date 2019-09-17 15:21
 */
public class OneHostAndPort {

    public static List<HostAndPort> hostAndPortList;

    private static AtomicInteger index = new AtomicInteger(0);

    static HostAndPort getOne() {
        if (hostAndPortList.size() == 1) {
            return hostAndPortList.get(0);
        }
        return hostAndPortList.get(index.getAndIncrement() % hostAndPortList.size());
    }

}
