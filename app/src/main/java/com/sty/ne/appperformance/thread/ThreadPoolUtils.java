package com.sty.ne.appperformance.thread;

import android.os.Process;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @Author: tian
 * @UpdateDate: 2020/12/8 9:32 PM
 */
public class ThreadPoolUtils {
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int MAXIMUM_POOL_SIZE = 16;
    private static final int KEEP_ALIVE_SECONDS = 3;

    private static final ThreadFactory ioThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "IO #" + mCount.getAndIncrement());
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            return thread;
        }
    };

    private static final ThreadFactory cpuThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "CPU #" + mCount.getAndIncrement());
            Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);
            return thread;
        }
    };

    private static final ThreadFactory miscThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "MISC #" + mCount.getAndIncrement());
            Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
            return thread;
        }
    };

    public static Executor ioExecutor = new ThreadPoolExecutor(MAXIMUM_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS,
            TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), ioThreadFactory);

    public static Executor cpuExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, CORE_POOL_SIZE, KEEP_ALIVE_SECONDS,
            TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), cpuThreadFactory);

    public static Executor miscExecutor = Executors.newFixedThreadPool(3, miscThreadFactory);

    public static Executor getIoExecutor() {
        return ioExecutor;
    }

    public static Executor getCpuExecutor() {
        return cpuExecutor;
    }

    public static Executor getMiscExecutor() {
        return miscExecutor;
    }

    public static void ioExecute(Runnable runnable) {
        ioExecutor.execute(runnable);
    }
}
