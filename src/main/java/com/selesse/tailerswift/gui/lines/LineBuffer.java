package com.selesse.tailerswift.gui.lines;

import java.util.List;

/**
 * Holds the lines of a tailed file that are currently kept in memory.
 * Implementations may evict old lines once a capacity is reached, in which case
 * {@link #getFirstLineNumber()} advances so line numbers stay meaningful.
 */
public interface LineBuffer {
    void append(String line);

    void clear();

    int size();

    /**
     * @param index 0-based index relative to the current window, i.e. {@code 0} is the oldest line still held.
     */
    String get(int index);

    /**
     * 1-based line number of the oldest line still held (i.e. {@code get(0)}'s line number).
     * Advances as lines are evicted. Relative to whatever this buffer has been fed from -
     * if it was seeded starting mid-file (see {@link #applyLineNumberOffset(long)}), this
     * is relative to the buffer's start, not the file's, until corrected.
     */
    long getFirstLineNumber();

    /**
     * One-time correction adding {@code offset} to the line-number baseline - used when a
     * file was opened by seeking straight to a tail window (skipping the lines before it
     * without counting them, to keep opening fast), and a background count of those
     * skipped lines has since finished, so displayed numbers can be upgraded from
     * relative-to-buffer to absolute-to-file.
     */
    void applyLineNumberOffset(long offset);

    List<String> asList();
}
