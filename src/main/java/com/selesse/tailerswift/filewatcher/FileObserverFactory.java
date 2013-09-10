package com.selesse.tailerswift.filewatcher;

import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

public class FileObserverFactory {
    public static FileObserver newFileObserver(final File observedFile, final FileWatcher fileWatcher) {
        return new FileObserver() {
            @Override
            public void onModify() {
            }

            @Override
            public void onCreate() {
                List<String> fileContents = fileWatcher.getBufferedFileContents();
                fileContents = Lists.newArrayList();

                try {
                    fileContents = Files.readAllLines(observedFile.toPath(), Charset.defaultCharset());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                fileWatcher.setBufferedFileContents(fileContents);
            }

            @Override
            public void onDelete() { }
        };
    }
}
