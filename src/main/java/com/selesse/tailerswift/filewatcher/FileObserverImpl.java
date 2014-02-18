package com.selesse.tailerswift.filewatcher;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

public class FileObserverImpl implements FileObserver {
    private static final Logger logger = LoggerFactory.getLogger(FileObserverImpl.class);
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

            if (bufferedFileSize == Files.size(observedFile.toPath())) {
                return null;
            }

            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(observedFile), "UTF-8"));
            long skippedBytes = bufferedReader.skip(bufferedFileSize);
            if (skippedBytes < bufferedFileSize) {
                logger.info("Didn't skip the entire file, only skipped {} bytes: {}", skippedBytes,
                        observedFile.getAbsolutePath());
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
        } catch (NoSuchFileException | FileNotFoundException e) {
            // we like this, do nothing
        } catch (IOException e) {
            e.printStackTrace();
        }

        return modificationBuffered.toString();
    }

    @Override
    public void onCreate() {
        bufferedFileSize = 0;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(observedFile), "UTF-8"));
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            logger.error("Error onCreate for {}: {}", observedFile.getAbsolutePath(), e);
        }
    }

    @Override
    public void onDelete() {
    }
}
