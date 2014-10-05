package com.selesse.tailerswift.threads;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.selesse.tailerswift.filewatcher.FileWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WorkerThreads {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerThreads.class);

    private static WorkerThreads instance;
    private final ExecutorService executorService;
    private final Map<File, Future<?>> fileWatcherMap;

    private WorkerThreads() {
        executorService = Executors.newCachedThreadPool();
        fileWatcherMap = Maps.newConcurrentMap();
    }

    public static WorkerThreads getInstance() {
        if (instance == null) {
            instance = new WorkerThreads();
        }
        return instance;
    }

    public static void execute(Callable<?> callable) throws InterruptedException {
        getInstance().executorService.invokeAll(Lists.newArrayList(callable));
    }

    public static void execute(Runnable runnable) throws InterruptedException {
        Future<?> future = getInstance().executorService.submit(runnable);

        if (runnable instanceof FileWatcher) {
            FileWatcher fileWatcher = (FileWatcher) runnable;
            File file = fileWatcher.getObservedFile();

            LOGGER.debug("[{}] : Created file watcher thread", file);
            getInstance().fileWatcherMap.put(file, future);
        }
    }

    public static void stopWatching(File file) {
        Future<?> future = getInstance().fileWatcherMap.remove(file);
        if (future == null) {
            LOGGER.error("[{}] : Couldn't find associated future, can't cancel thread", file);
        }
        else {
            LOGGER.info("[{}] : Canceling the worker thread", file);
            future.cancel(true);
        }
    }

    public static void shutdown() {
        getInstance().executorService.shutdownNow();
    }
}
