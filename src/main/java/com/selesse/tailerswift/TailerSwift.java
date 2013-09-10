package com.selesse.tailerswift;

import com.google.common.collect.Maps;
import com.selesse.tailerswift.filewatcher.FileWatcher;

import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.util.Map;

public class TailerSwift implements Runnable {
    private Map<WatchKey, Path> keys;
    private String[] args;

    public TailerSwift(String[] args) {
        keys = Maps.newHashMap();
        this.args = args;
    }

    @Override
    public void run() {
        FileWatcher fileWatcher = new FileWatcher(args[0]);
        keys.put(fileWatcher.getKey(), fileWatcher.getPath());
        fileWatcher.run();
    }
}
