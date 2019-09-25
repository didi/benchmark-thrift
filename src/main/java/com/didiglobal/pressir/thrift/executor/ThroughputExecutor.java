package com.didiglobal.pressir.thrift.executor;

import com.didiglobal.pressir.thrift.generator.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.IntSupplier;

/**
 * @ClassName ThroughputExecutor
 * @Description QPS执行器
 * @Author pressir
 * @Date 2019-09-02 14:49
 */
public class ThroughputExecutor extends PressureExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThroughputExecutor.class);

    private final LinkedBlockingQueue<List<Runnable>> queue = new LinkedBlockingQueue<>();

    private final Vector<ScheduledFuture<?>> futures = new Vector<>(2);

    ThroughputExecutor(Generator generator, IntSupplier throughput) {
        super(throughput, generator, newPreparer(), newExecutor());
    }

    private static ScheduledThreadPoolExecutor newPreparer() {
        return new ScheduledThreadPoolExecutor(2, new CustomThreadFactory("preparer", Thread.MAX_PRIORITY));
    }

    private static ThreadPoolExecutor newExecutor() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 15L, TimeUnit.SECONDS, new SynchronousQueue<>(), new CustomThreadFactory("com/didi/pressir/executor", Thread.MAX_PRIORITY));
    }

    @Override
    public final void start(long delay) {
        if (this.futures.isEmpty()) {
            synchronized (this.futures) {
                if (this.shutdown) {
                    throw new RuntimeException("Executor has been shutdown");
                }
                if (this.futures.isEmpty()) {
                    this.futures.add(this.preparer.scheduleAtFixedRate(this::generate, Math.max(delay - 1, 0), 1, TimeUnit.SECONDS));
                    this.futures.add(this.preparer.scheduleAtFixedRate(() -> ThroughputExecutor.execute(this.queue, this.executor), Math.max(delay, 1) * 1000, 1, TimeUnit.MILLISECONDS));
                    this.await();
                }
            }
        }
    }

    @Override
    protected final void cancel() {
        if (!this.futures.isEmpty()) {
            synchronized (this.futures) {
                if (!this.futures.isEmpty()) {
                    this.futures.forEach(future -> future.cancel(true));
                    this.futures.clear();
                }
            }
        }
    }

    /**
     * Generate tasks
     */
    private void generate() {
        int num = this.getLimit();
        LOGGER.debug("ThreadPool Stat: executor(core={},maximum={},workers={},queue={},active={},completed={}), preparer(core={},maximum={},workers={},queue={},active={},completed={}), Limit={}",
                this.executor.getCorePoolSize(), this.executor.getMaximumPoolSize(), this.executor.getPoolSize(), this.executor.getQueue().size(), this.executor.getActiveCount(), this.executor.getCompletedTaskCount(),
                this.preparer.getCorePoolSize(), this.preparer.getMaximumPoolSize(), this.executor.getPoolSize(), this.preparer.getQueue().size(), this.preparer.getActiveCount(), this.preparer.getCompletedTaskCount(),
                num);
        if (num <= 0) {
            return;
        }
        List<Runnable> tasks;
        long beginTime = System.nanoTime();
        try {
            tasks = this.generator.generate(num);
        } catch (Exception e) {
            LOGGER.error("generate tasks error: {}, {}", e.getMessage(), e);
            return;
        }
        long endTime = System.nanoTime();
        LOGGER.debug("prepared tasks: {}, latency: {} ns. current limit: {}", tasks.size(), (endTime - beginTime), num);
        ThroughputExecutor.offer(this.queue, tasks);
    }

    /**
     * Poll task from queue and do execute
     *
     * @param queue
     * @param executor
     */
    private static  void execute(LinkedBlockingQueue<List<Runnable>> queue, ExecutorService executor) {
        List<Runnable> tasks = queue.poll();
        if (tasks == null || tasks.isEmpty()) {
            return;
        }
        for (Runnable task : tasks) {
            if (task == null) {
                continue;
            }
            executor.submit(task);
        }
    }

    /**
     * Split and insert elements into queue
     *
     * @param queue
     * @param elements
     */
    private static void offer(LinkedBlockingQueue<List<Runnable>> queue, List<Runnable> elements) {
        if (elements.isEmpty()) {
            return;
        }
        int size = elements.size();
        int sizePerShard = size / 1000;
        int[] ints = splitRemainder(size % 1000);
        int random = new Random().nextInt(1000);
        for (int i = 0, fromIndex = 0, toIndex; i < 1000; i++) {
            int delta = sizePerShard + ints[(i + random) % 1000];
            if (delta == 0) {
                queue.offer(Collections.emptyList());
            } else {
                toIndex = fromIndex + delta;
                queue.offer(elements.subList(fromIndex, toIndex));
                fromIndex = toIndex;
            }
        }
    }

    /**
     * split remainder
     *
     * @param remainder
     * @return
     */
    private static int[] splitRemainder(int remainder) {
        int[] ints = new int[1000];
        if (remainder > 0) {
            float delta = (float) 1000 / remainder;
            for (int i = 0; i < remainder; i++) {
                ints[Math.round(delta * i)] = 1;
            }
        }
        return ints;
    }
}
