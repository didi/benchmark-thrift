package com.didiglobal.pressir.thrift.executor;

import com.didiglobal.pressir.thrift.generator.Generator;

import java.io.Closeable;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.IntSupplier;

/**
 * @ClassName PressureExecutor
 * @Description 压力执行器
 * @Author pressir
 * @Date 2019-09-02 14:43
 */
public abstract class PressureExecutor implements Closeable {
    /**
     * Pressure Getter
     */
    private final IntSupplier pressureGetter;

    /**
     * Task generator
     */
    final Generator generator;

    /**
     * Task preparer
     */
    final ScheduledThreadPoolExecutor preparer;

    /**
     * Task executor
     */
    final ThreadPoolExecutor executor;

    volatile boolean shutdown = false;

    PressureExecutor(IntSupplier pressureGetter, Generator generator, ScheduledThreadPoolExecutor preparer, ThreadPoolExecutor executor) {
        this.pressureGetter = Objects.requireNonNull(pressureGetter);
        this.generator = Objects.requireNonNull(generator);
        this.preparer = Objects.requireNonNull(preparer);
        this.executor = Objects.requireNonNull(executor);
    }

    /**
     * start the pressure after delay seconds
     * @param delay
     */
    public abstract void start(long delay);

    /**
     * cancel the pressure
     */
    protected abstract void cancel();

    @Override
    public synchronized void close() {
        try {
            this.cancel();
        } catch (Exception ignored) {
        }
        this.shutdown = true;
        this.executor.shutdownNow();
        this.preparer.shutdownNow();
    }

    final int getLimit() {
        int limit = this.pressureGetter.getAsInt();
        if (limit < 0) {
            synchronized (this) {
                this.notify();
            }
        }
        return limit;
    }

    void await() {
        do {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException ignored) {
                }
            }
        } while (this.pressureGetter.getAsInt() >= 0);
        this.cancel();
    }

    public static  ConcurrencyExecutor concurrency(Generator generator, IntSupplier concurrency) {
        return new ConcurrencyExecutor(generator, concurrency);
    }

    public static  ThroughputExecutor throughput(Generator generator, IntSupplier throughput) {
        return new ThroughputExecutor(generator, throughput);
    }
}
