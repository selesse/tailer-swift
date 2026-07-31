package com.selesse.tailerswift.filewatcher;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Finds the byte offset to start tailing a file from, so that opening a huge file costs
 * about the same as opening a small one. Reading forward from the returned offset to the
 * end of the file yields approximately the last {@code desiredLines} lines, without ever
 * reading the (possibly enormous) portion of the file before it.
 *
 * <p>Scans backward from the end of the file in fixed-size blocks, counting newlines, and
 * stops as soon as enough have been found - so the cost is bounded by how much of the file
 * makes up {@code desiredLines} worth of content, never by the file's total size.
 *
 * <p>This trades away exact absolute line numbers for content skipped this way (knowing
 * "this is line 1,847,293" requires counting every newline before it, which is exactly the
 * full-file scan this class exists to avoid) in exchange for open time that no longer
 * depends on file size.
 */
public class TailStartLocator {
    private static final int DEFAULT_SCAN_BLOCK_SIZE = 1024 * 64;

    private final File file;
    private final int scanBlockSize;

    public TailStartLocator(File file) {
        this(file, DEFAULT_SCAN_BLOCK_SIZE);
    }

    /** @param scanBlockSize exposed for tests that want to exercise multi-block scanning without huge fixtures. */
    TailStartLocator(File file, int scanBlockSize) {
        this.file = file;
        this.scanBlockSize = scanBlockSize;
    }

    /**
     * @return the byte offset to start reading from. Reading forward from this offset to
     *         the end of the file yields at most {@code desiredLines} lines. Returns 0
     *         (start of file) if the file has {@code desiredLines} lines or fewer.
     */
    public long locate(int desiredLines) throws IOException {
        long fileSize = file.length();
        if (fileSize == 0 || desiredLines <= 0) {
            return 0;
        }

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            long position = fileSize;
            int newlinesFound = 0;
            byte[] block = new byte[scanBlockSize];

            while (position > 0) {
                int blockLength = (int) Math.min(scanBlockSize, position);
                position -= blockLength;
                randomAccessFile.seek(position);
                randomAccessFile.readFully(block, 0, blockLength);

                for (int i = blockLength - 1; i >= 0; i--) {
                    if (block[i] == '\n') {
                        newlinesFound++;
                        if (newlinesFound > desiredLines) {
                            return position + i + 1;
                        }
                    }
                }
            }
            return 0;
        }
    }
}
