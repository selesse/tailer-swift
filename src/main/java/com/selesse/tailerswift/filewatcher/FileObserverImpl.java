package com.selesse.tailerswift.filewatcher;

import com.google.common.collect.Lists;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

public class FileObserverImpl implements FileObserver {
    private long bufferedFileSize;
    private File observedFile;
    private List<String> bufferedFileContents;
    private BufferedReader bufferedReader;

    public FileObserverImpl(File observedFile) {
        this.observedFile = observedFile;
    }

    @Override
    public void onModify() {
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
    public void onCreate() {
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
    }

    @Override
    public void onDelete() {
    }
}
