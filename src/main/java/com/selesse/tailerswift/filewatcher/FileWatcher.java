package com.selesse.tailerswift.filewatcher;

import com.google.common.collect.Lists;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher implements Runnable {
    private WatchService watcher;
    private Path observedDirectory;
    private Path observedFile;
    private WatchKey key;
    private FileObserver fileObserver;
    private List<String> bufferedFileContents;
    private long bufferedFileSize;
    private BufferedReader bufferedReader;

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

        try {
            bufferedReader = new BufferedReader(new FileReader(observedFile));
            bufferedFileContents = Files.readAllLines(observedFile.toPath(), Charset.defaultCharset());
            bufferedFileSize = Files.size(observedFile.toPath());
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            // we like this, it's easy
            bufferedFileSize = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.fileObserver = new FileObserver() {
            @Override
            public synchronized void onModify() {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(observedFile));
                    bufferedReader.skip(bufferedFileSize);

                    char[] buffer = new char[1024];
                    int bytesRead;

                    while ((bytesRead = bufferedReader.read(buffer)) != -1) {
                        for (int i = 0; i < bytesRead; i++) {
                            System.out.print(buffer[i]);
                        }
                    }

                    bufferedFileContents = Files.readAllLines(observedFile.toPath(), Charset.defaultCharset());
                    bufferedFileSize = Files.size(observedFile.toPath());
                    bufferedReader.close();
                } catch (FileNotFoundException fileNotFoundException) {
                    // we like this, it's easy
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public synchronized void onCreate() {
                bufferedFileContents = Lists.newArrayList();
                bufferedFileSize = 0;

                try {
                    bufferedReader = new BufferedReader(new FileReader(observedFile));
                    bufferedFileSize = Files.size(observedFile.toPath());

                    char[] buffer = new char[1024];
                    int bytesRead;

                    while ((bytesRead = bufferedReader.read(buffer)) != -1) {
                        for (int i = 0; i < bytesRead; i++) {
                            System.out.print(buffer[i]);
                        }
                    }

                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                notifyObserver(ENTRY_CREATE);
            }

            @Override
            public synchronized void onDelete() {
            }
        };
    }

    private void notifyObserver(WatchEvent.Kind<Path> kind) {
        if (kind == ENTRY_CREATE) {

        } else if (kind == ENTRY_MODIFY) {

        } else if (kind == ENTRY_DELETE) {

        }
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

    public List<String> getBufferedFileContents() {
        return bufferedFileContents;
    }

    public void setBufferedFileContents(List<String> bufferedFileContents) {
        this.bufferedFileContents = bufferedFileContents;
    }
}
