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
     * Advances as lines are evicted.
     */
    int getFirstLineNumber();

    List<String> asList();
}
