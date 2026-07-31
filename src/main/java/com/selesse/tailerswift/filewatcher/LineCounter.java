package com.selesse.tailerswift.filewatcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Counts newlines in the first {@code byteLimit} bytes of a file via a raw byte scan - no
 * character decoding, just counting {@code '\n'} bytes (safe even for multi-byte UTF-8
 * content, since 0x0A never appears as part of a multi-byte sequence).
 *
 * <p>This is the same trick {@code wc -l} uses to be fast: a single linear scan of a file
 * already in the page cache is cheap (bounded by memory bandwidth, not disk). It's
 * <em>redundant</em> re-scanning - reading the same bytes over and over - that's
 * expensive, which is what made {@link FileObserverImpl} slow before it was fixed to use
 * a persistent reader. Meant to be run off the EDT since it's still O(byteLimit).
 */
public class LineCounter {
    private static final int READ_BUFFER_SIZE = 1024 * 1024;

    private final File file;

    public LineCounter(File file) {
        this.file = file;
    }

    public long countLinesBefore(long byteLimit) throws IOException {
        long remaining = byteLimit;
        long count = 0;
        byte[] buffer = new byte[READ_BUFFER_SIZE];

        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            while (remaining > 0) {
                int toRead = (int) Math.min(buffer.length, remaining);
                int bytesRead = inputStream.read(buffer, 0, toRead);
                if (bytesRead == -1) {
                    break;
                }

                for (int i = 0; i < bytesRead; i++) {
                    if (buffer[i] == '\n') {
                        count++;
                    }
                }

                remaining -= bytesRead;
            }
        }

        return count;
    }
}
