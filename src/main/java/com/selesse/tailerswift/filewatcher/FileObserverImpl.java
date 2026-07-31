package com.selesse.tailerswift.filewatcher;

import com.google.common.base.Charsets;
import com.google.common.io.CountingInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

public class FileObserverImpl implements FileObserver {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileObserverImpl.class);
    private static final int CHUNK_SIZE = 1024 * 64; // 64KB chunks for fewer, larger updates

    // How far back to seek when we first start watching a file, so opening a huge file
    // costs about the same as opening a small one - this should stay in sync with
    // TailPane's ring buffer capacity, since reading more than that just gets evicted
    // immediately without ever being shown.
    private static final int INITIAL_TAIL_LINES = 100_000;

    private final File observedFile;
    private long bufferedFileSize;
    private CountingInputStream countingInputStream;
    private Reader reader;

    public FileObserverImpl(File observedFile) {
        this.observedFile = observedFile;
    }

    @Override
    public String onModify() {
        try {
            long currentFileSize = Files.size(observedFile.toPath());

            if (currentFileSize < bufferedFileSize) {
                LOGGER.info("[{}] : File shrank ({} > {}), reopening from the start", observedFile.getAbsolutePath(),
                        bufferedFileSize, currentFileSize);
                reopen(0);
            }

            if (bufferedFileSize == currentFileSize) {
                return null;
            }

            if (reader == null) {
                reopen(bufferedFileSize);
            }

            char[] buffer = new char[CHUNK_SIZE];
            int totalCharsRead = 0;
            int charsRead;
            while (totalCharsRead < CHUNK_SIZE
                    && (charsRead = reader.read(buffer, totalCharsRead, CHUNK_SIZE - totalCharsRead)) != -1) {
                totalCharsRead += charsRead;
            }

            bufferedFileSize = countingInputStream.getCount();

            if (totalCharsRead == 0) {
                return null;
            }

            return new String(buffer, 0, totalCharsRead);
        }
        catch (NoSuchFileException | FileNotFoundException ignored) {
            // we like this, do nothing
        }
        catch (IOException e) {
            LOGGER.error("[{}] : Error updating during onModify", observedFile.toPath(), e);
        }

        return null;
    }

    @Override
    public void onCreate() {
        try {
            long startOffset = new TailStartLocator(observedFile).locate(INITIAL_TAIL_LINES);
            reopen(startOffset);
        } catch (IOException e) {
            LOGGER.error("[{}] : Error onCreate", observedFile.getAbsolutePath(), e);
        }
    }

    @Override
    public void onDelete() {
        closeQuietly();
    }

    /**
     * (Re)opens the file for reading starting at {@code byteOffset}, using a raw byte-level
     * skip (an O(1) file seek) rather than decoding-and-discarding characters. The resulting
     * reader is kept open and read forward incrementally by {@link #onModify()} - unlike the
     * old implementation, we never reopen and re-skip from scratch on every call, which was
     * O(bytes already read) per call and made tailing a long-running file quadratically slow.
     */
    private void reopen(long byteOffset) throws IOException {
        closeQuietly();

        FileInputStream fileInputStream = new FileInputStream(observedFile);
        long skipped = fileInputStream.skip(byteOffset);
        if (skipped < byteOffset) {
            LOGGER.info("[{}] : Didn't skip the entire requested offset, only skipped {} of {} bytes",
                    observedFile.getAbsolutePath(), skipped, byteOffset);
        }

        countingInputStream = new CountingInputStream(fileInputStream);
        reader = new BufferedReader(new InputStreamReader(countingInputStream, Charsets.UTF_8));
        bufferedFileSize = byteOffset;
    }

    private void closeQuietly() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException ignored) {
                // nothing sensible to do
            }
            reader = null;
        }
    }
}
