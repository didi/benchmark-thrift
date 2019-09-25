package com.didiglobal.pressir.thrift.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName CustomThreadFactory
 * @Description TODO
 * @Author pressir
 * @Date 2019-09-02 14:54
 */
public class CustomThreadFactory implements ThreadFactory {
    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);

    private final String poolName;
    private final ThreadGroup threadGroup;
    private final String threadNamePrefix;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private int threadPriority;

    public CustomThreadFactory(String poolName) {
        this(poolName, Thread.NORM_PRIORITY);
    }

    CustomThreadFactory(String poolName, int threadPriority) {
        SecurityManager securityManager = System.getSecurityManager();
        this.poolName = poolName;
        this.threadGroup = (securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.threadPriority = threadPriority;
        this.threadNamePrefix = poolName + '-' + POOL_NUMBER.getAndIncrement() + '-';
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(this.threadGroup, r, this.threadNamePrefix + this.threadNumber.getAndIncrement(), 0);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != this.threadPriority) {
            t.setPriority(this.threadPriority);
        }
        return t;
    }
}
