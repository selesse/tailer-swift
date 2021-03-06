package com.selesse.tailerswift.filewatcher;


import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.ClosedChannelException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

public class FileObserverImpl implements FileObserver {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileObserverImpl.class);
    private long bufferedFileSize;
    private File observedFile;
    private BufferedReader bufferedReader;
    private static final int CHUNK_SIZE = 1024 * 5;

    public FileObserverImpl(File observedFile) {
        this.observedFile = observedFile;
    }

    @Override
    public String onModify() {
        StringBuilder modificationBuffered = new StringBuilder(CHUNK_SIZE);

        try {
            long currentFileSize = Files.size(observedFile.toPath());
            if (currentFileSize < bufferedFileSize) {
                bufferedFileSize = 0;
            }

            if (bufferedFileSize == currentFileSize) {
                return null;
            }
            if (bufferedFileSize > currentFileSize) {
                LOGGER.error("[{}] : bufferedFileSize is greater than current file size: {} > {}", observedFile,
                        bufferedFileSize, currentFileSize);
                return null;
            }

            bufferedReader = Files.newBufferedReader(observedFile.toPath(), Charsets.UTF_8);
            try {
                long skippedBytes = bufferedReader.skip(bufferedFileSize);
                if (skippedBytes < bufferedFileSize) {
                    LOGGER.info("[{}] : Didn't skip the entire file, only skipped {} bytes",
                            observedFile.getAbsolutePath(), skippedBytes);
                }

                char[] buffer = new char[CHUNK_SIZE];
                int bytesRead = 0;

                while (bytesRead < CHUNK_SIZE && bytesRead != -1) {
                    bytesRead = bufferedReader.read(buffer);
                    for (int i = 0; i < bytesRead; i++) {
                        modificationBuffered.append(buffer[i]);
                    }
                }

                if (bytesRead == -1) {
                    bufferedFileSize += modificationBuffered.toString().length();
                }
                else {
                    bufferedFileSize += bytesRead;
                }
                bufferedReader.close();
            } catch (ClosedChannelException e) {
                return null;
            }
        }
        catch (NoSuchFileException | FileNotFoundException ignored) {
            // we like this, do nothing
        }
        catch (IOException e) {
            LOGGER.error("[{}] : Error updating during onModify", observedFile.toPath(), e);
        }

        return modificationBuffered.toString();
    }

    @Override
    public void onCreate() {
        bufferedFileSize = 0;
        try {
            bufferedReader = Files.newBufferedReader(observedFile.toPath(), Charsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("[{}] : Error onCreate", observedFile.getAbsolutePath(), e);
        }
    }

    @Override
    public void onDelete() {
    }
}
