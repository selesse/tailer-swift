package com.selesse.tailerswift.filewatcher;


import java.io.*;
import java.nio.file.Files;

public class FileObserverImpl implements FileObserver {
    private long bufferedFileSize;
    private File observedFile;
    private BufferedReader bufferedReader;
    private final int CHUNK_SIZE = 1024;

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

            bufferedReader = new BufferedReader(new FileReader(observedFile));
            bufferedReader.skip(bufferedFileSize);

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
        } catch (FileNotFoundException fileNotFoundException) {
            // we like this, it's easy
        } catch (IOException e) {
            e.printStackTrace();
        }

        return modificationBuffered.toString();
    }

    @Override
    public void onCreate() {
        bufferedFileSize = 0;
        try {
            bufferedReader = new BufferedReader(new FileReader(observedFile));
        } catch (FileNotFoundException e) {
            // do nothing
        }
    }

    @Override
    public void onDelete() {
    }
}
