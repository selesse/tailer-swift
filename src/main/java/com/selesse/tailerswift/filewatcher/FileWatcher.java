package com.selesse.tailerswift.filewatcher;

import com.selesse.tailerswift.ui.UserInterface;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher implements Runnable {
    private UserInterface ui;
    private WatchService watcher;
    private Path observedDirectory;
    private Path observedFilePath;
    private WatchKey key;
    private FileObserver fileObserver;

    public FileWatcher(UserInterface ui, String filePath) {
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            System.out.println("According to the Javadoc, this is impossible! Why would the Javadoc lie?");
            e.printStackTrace();
        }

        this.ui = ui;
        final File observedFile = new File(filePath);
        this.observedFilePath = observedFile.toPath();
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
        ui.newFile(observedFilePath, fileObserver.onCreate());
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
                if (ev.context().getFileName().equals(observedFilePath.getFileName())) {
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
            String modificationString = fileObserver.onModify();
            ui.updateFile(observedFilePath, modificationString);
        } else if (kind == ENTRY_CREATE) {
            String modificationString = fileObserver.onCreate();
            ui.newFile(observedFilePath, modificationString);
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
