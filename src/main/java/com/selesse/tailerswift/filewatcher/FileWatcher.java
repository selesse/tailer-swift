package com.selesse.tailerswift.filewatcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher implements Runnable {
    private WatchService watcher;
    private Path observedDirectory;
    private Path observedFile;
    private WatchKey key;
    private FileObserver fileObserver;

    public FileWatcher(String filePath) {
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            System.out.println("According to the Javadoc, this is impossible! Why would the Javadoc lie?");
            e.printStackTrace();
        }

        final File observedFile = new File(filePath);
        this.observedFile = observedFile.toPath();
        this.observedDirectory = observedFile.getParentFile().toPath();
        registerFilesParentDirectory();

        this.fileObserver = new FileObserverImpl(observedFile);
    }

    private void registerFilesParentDirectory() {
        try {
            key = observedDirectory.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        } catch (IOException e) {
            System.out.println("Error registering directory '" + observedDirectory.toAbsolutePath() + "'");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException e) {
                return;
            }

            if (key == null) {
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                if (kind == OVERFLOW) {
                    System.out.println("Kind was overflow?");
                    continue;
                }

                WatchEvent<Path> ev = cast(event);

                // if this is actually the file we're looking at, do something
                if (ev.context().getFileName().equals(observedFile.getFileName())) {
                    performKindBasedAction(ev.kind());
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                System.out.println("Error: WatchKey broke.");
                break;
            }
        }
    }

    private void performKindBasedAction(WatchEvent.Kind<Path> kind) {
        if (kind == ENTRY_MODIFY) {
            fileObserver.onModify();
        } else if (kind == ENTRY_CREATE) {
            fileObserver.onCreate();
        } else if (kind == ENTRY_DELETE) {
            fileObserver.onDelete();
        }
    }

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    public Path getPath() {
        return observedDirectory;
    }

    public WatchKey getKey() {
        return key;
    }
}
