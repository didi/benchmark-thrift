package com.didiglobal.pressir.thrift.executor;

import com.didiglobal.pressir.thrift.constant.Constants;
import com.didiglobal.pressir.thrift.generator.Generator;
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
        return new ScheduledThreadPoolExecutor(
                1,
                new CustomThreadFactory("preparer", Thread.MAX_PRIORITY));
    }

    private static ThreadPoolExecutor newInitExecutor() {
        return new ThreadPoolExecutor(
                1,
                1,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new CustomThreadFactory("executor", Thread.MAX_PRIORITY));
    }

    @Override
    public final void start(long delay) {
        if (this.future == null) {
            synchronized (this) {
                if (this.shutdown) {
                    throw new RuntimeException("Executor has been shutdown");
                }
                if (this.future == null) {
                    this.future = this.preparer.scheduleWithFixedDelay(
                            this::prepare,
                            Math.max(delay, 1) * 1000,
                            1,
                            TimeUnit.MILLISECONDS);
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
            int threadNums = this.getLimit();
            if (timestamp > this.timestamp) {
                this.timestamp = timestamp;
                LOGGER.debug("Stat: executor(core={},maximum={},workers={},queue={},active={},completed={}), Limit={}",
                        this.executor.getCorePoolSize(),
                        this.executor.getMaximumPoolSize(),
                        this.executor.getPoolSize(),
                        this.executor.getQueue().size(),
                        this.executor.getActiveCount(),
                        this.executor.getCompletedTaskCount(),
                        threadNums);

            }
            if (threadNums <= 0) {
                this.executor.purge();
                return;
            }
            if (threadNums != this.executor.getCorePoolSize()) {
                this.setPoolSize(threadNums);
            }
            int batchSize = Math.max(threadNums, 10);
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
            LOGGER.debug("submit tasks: {}, latency: {} ns. Current nThreads: {}, queueSize: {}",
                    tasks.size(),
                    (endTime - beginTime),
                    threadNums,
                    queueSize);
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
     * @param threadNums
     */
    private void setPoolSize(int threadNums) {
        if (threadNums <= 0) {
            throw new IllegalArgumentException("input pool size " + threadNums + " must not be lte 0");
        }
        synchronized (this) {
            int current = this.executor.getCorePoolSize();
            if (current == threadNums) {
                return;
            }
            LOGGER.debug("pool size(core={}, max={}) change to {}!",
                    current,
                    this.executor.getMaximumPoolSize(),
                    threadNums);
            if (current < threadNums) {
                this.executor.setMaximumPoolSize(threadNums);
                this.executor.setCorePoolSize(threadNums);
            } else {
                this.executor.setCorePoolSize(threadNums);
                this.executor.setMaximumPoolSize(threadNums);
            }
        }
    }
}
