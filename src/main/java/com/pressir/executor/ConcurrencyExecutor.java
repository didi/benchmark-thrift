package com.pressir.executor;

import com.pressir.constant.Constants;
import com.pressir.generator.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.IntSupplier;

/**
 * @ClassName ConcurrencyExecutor
 * @Description 并发执行器
 * @Author pressir
 * @Date 2019-09-02 14:47
 */
public class ConcurrencyExecutor extends PressureExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrencyExecutor.class);

    private volatile ScheduledFuture<?> future;

    private volatile int timestamp;

    ConcurrencyExecutor(Generator generator, IntSupplier concurrency) {
        super(concurrency, generator, newPreparer(), newInitExecutor());
    }

    private static ScheduledThreadPoolExecutor newPreparer() {
        return new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("preparer", Thread.MAX_PRIORITY));
    }

    private static ThreadPoolExecutor newInitExecutor() {
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new CustomThreadFactory("com/didi/pressir/executor", Thread.MAX_PRIORITY));
    }

    @Override
    public final void start(long delay) {
        if (this.future == null) {
            synchronized (this) {
                if (this.shutdown) {
                    throw new RuntimeException("Executor has been shutdown");
                }
                if (this.future == null) {
                    this.future = this.preparer.scheduleWithFixedDelay(this::prepare, Math.max(delay, 1) * 1000, 1, TimeUnit.MILLISECONDS);
                    this.await();
                }
            }
        }
    }

    @Override
    protected final void cancel() {
        if (this.future != null) {
            synchronized (this) {
                if (this.future != null) {
                    this.future.cancel(true);
                    this.future = null;
                }
            }
        }
    }

    /**
     * Prepare tasks
     */
    private void prepare() {
        for (; ; ) {
            int timestamp = (int) (System.currentTimeMillis() / Constants.TIME_CONVERT_BASE);
            int nThreads = this.getLimit();
            if (timestamp > this.timestamp) {
                this.timestamp = timestamp;
                LOGGER.debug("Stat: executor(core={},maximum={},workers={},queue={},active={},completed={}), Limit={}",
                        this.executor.getCorePoolSize(), this.executor.getMaximumPoolSize(), this.executor.getPoolSize(), this.executor.getQueue().size(), this.executor.getActiveCount(), this.executor.getCompletedTaskCount(), nThreads);

            }
            if (nThreads <= 0) {
                this.executor.purge();
                return;
            }
            if (nThreads != this.executor.getCorePoolSize()) {
                this.setPoolSize(nThreads);
            }
            int batchSize = Math.max(nThreads, 10);
            int queueSize = this.executor.getQueue().size();
            if (queueSize > batchSize / 2) {
                break;
            }
            long beginTime = System.nanoTime();
            List<Runnable> tasks;
            try {
                tasks = this.generator.generate(batchSize);
            } catch (Exception e) {
                LOGGER.error("generate tasks error: {}, {}", e.getMessage(), e);
                break;
            }
            long endTime = System.nanoTime();
            LOGGER.debug("submit tasks: {}, latency: {} ns. Current nThreads: {}, queueSize: {}", tasks.size(), (endTime - beginTime), nThreads, queueSize);
            for (Runnable task : tasks) {
                if (task == null) {
                    continue;
                }
                this.executor.submit(task);
            }
        }
    }

    /**
     * set concurrent threads
     *
     * @param nThreads
     */
    private void setPoolSize(int nThreads) {
        if (nThreads <= 0) {
            throw new IllegalArgumentException("input pool size " + nThreads + " must not be lte 0");
        }
        synchronized (this) {
            int current = this.executor.getCorePoolSize();
            if (current == nThreads) {
                return;
            }
            LOGGER.debug("pool size(core={}, max={}) change to {}!", current, this.executor.getMaximumPoolSize(), nThreads);
            if (current < nThreads) {
                this.executor.setMaximumPoolSize(nThreads);
                this.executor.setCorePoolSize(nThreads);
            } else {
                this.executor.setCorePoolSize(nThreads);
                this.executor.setMaximumPoolSize(nThreads);
            }
        }
    }
}
