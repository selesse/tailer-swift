package com.selesse.tailerswift.cli;

import com.google.common.collect.Maps;
import com.selesse.tailerswift.filewatcher.FileWatcher;

import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.util.Map;

public class CliTailerSwift implements Runnable {
    private Map<WatchKey, Path> keys;
    private String[] args;

    public CliTailerSwift(String[] args) {
        keys = Maps.newHashMap();
        this.args = args;
    }

    @Override
    public void run() {
        FileWatcher fileWatcher = new FileWatcher(new CliTailerSwiftImpl(), args[0]);
        keys.put(fileWatcher.getKey(), fileWatcher.getPath());
        fileWatcher.run();
    }
}
