package com.selesse.tailerswift.filewatcher;

import com.selesse.tailerswift.TailUserInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileWatcher.class);

    private TailUserInterface ui;
    private WatchService watcher;
    private Path observedDirectory;
    private Path observedFilePath;
    private FileObserver fileObserver;
    private long lastObservedFileSize;

    public FileWatcher(TailUserInterface ui, String filePath) {
        try {
            watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            LOGGER.error("Error creating new watch service instance", e);
        }

        this.ui = ui;
        final File observedFile = new File(filePath);
        this.observedFilePath = observedFile.toPath();
        try {
            lastObservedFileSize = Files.size(observedFilePath);
        } catch (IOException e) {
            lastObservedFileSize = 0;
        }
        File parentFile = observedFile.getAbsoluteFile().getParentFile();
        if (parentFile != null) {
            this.observedDirectory = parentFile.toPath();
            register(observedDirectory);

            this.fileObserver = new FileObserverImpl(observedFile);
        }
        else {
            LOGGER.error("[{}] : Error registering parent", observedFile.getAbsolutePath());
        }
    }

    private void register(Path observedDirectory) {
        try {
            observedDirectory.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        } catch (IOException e) {
            LOGGER.error("[{}] : Error registering directory", observedDirectory.toAbsolutePath());
        }
    }

    @Override
    public void run() {
        if (observedFilePath.toFile().exists()) {
            performKindBasedAction(ENTRY_CREATE, observedFilePath.toFile());
        }
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
                    LOGGER.warn("Kind was overflow?");
                    continue;
                }

                WatchEvent<Path> ev = cast(event);

                // If this is actually the file we're looking at, do something.
                if (ev.context().getFileName().equals(observedFilePath.getFileName())) {
                    LOGGER.debug("[{}] : Activity of kind {}", observedFilePath, ev.kind().name());
                    performKindBasedAction(ev.kind(), observedFilePath.toFile());
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                LOGGER.error("Error: WatchKey broke.");
                break;
            }
        }
    }

    private void performKindBasedAction(WatchEvent.Kind<Path> kind, File file) {
        if (fileHasBeenDeletedAndRecreated()) {
            LOGGER.info("[{}] : File has been recreated, restarting the watch", file.getAbsolutePath());
            handleFileRecreation();
            return;
        }

        if (kind == ENTRY_MODIFY) {
            updateFile(ui);
        } else if (kind == ENTRY_CREATE) {
            fileObserver.onCreate();
            updateFile(ui);
        } else if (kind == ENTRY_DELETE) {
            ui.deleteFile(observedFilePath);
        }
    }

    private boolean fileHasBeenDeletedAndRecreated() {
        if (!observedFilePath.toFile().exists()) {
            return false;
        }

        try {
            return Files.size(observedFilePath) < lastObservedFileSize;
        } catch (IOException e) {
            LOGGER.error("[{}] : Error determining file size", e);
            return true;
        }
    }

    private void handleFileRecreation() {
        long currentFileSize = 0;
        try {
            if (observedFilePath.toFile().exists()) {
                currentFileSize = Files.size(observedFilePath);
            }
        } catch (IOException e) {
            LOGGER.error("[{}] : Error determining size", observedFilePath);
        }
        lastObservedFileSize = currentFileSize;
        ui.deleteFile(observedFilePath);
        fileObserver.onCreate();
        updateFile(ui);
    }

    public void updateFile(TailUserInterface ui) {
        String modification;
        while ((modification = fileObserver.onModify()) != null) {
            ui.updateFile(observedFilePath, modification);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }
}
