package com.selesse.tailerswift.gui.lines;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link LineBuffer} bounded to a maximum number of lines, backed by a fixed-size
 * circular array so both {@link #append} and {@link #get} are O(1) - this is on the
 * paint hot path, so an O(n) structure like {@code ArrayDeque}'s indexed access would
 * defeat the point of virtualized rendering.
 *
 * <p>Once the capacity is exceeded, the oldest line is evicted so memory use stays
 * proportional to the capacity rather than the total size of the (possibly huge) file
 * being tailed.
 */
public class RingLineBuffer implements LineBuffer {
    private final String[] lines;
    private final int capacity;
    private int head;
    private int size;
    private long firstLineNumber;

    public RingLineBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive, was " + capacity);
        }
        this.capacity = capacity;
        this.lines = new String[capacity];
        this.firstLineNumber = 1;
    }

    @Override
    public void append(String line) {
        int writeIndex = (head + size) % capacity;
        lines[writeIndex] = line;
        if (size < capacity) {
            size++;
        }
        else {
            head = (head + 1) % capacity;
            firstLineNumber++;
        }
    }

    @Override
    public void clear() {
        head = 0;
        size = 0;
        firstLineNumber = 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index " + index + ", size " + size);
        }
        return lines[(head + index) % capacity];
    }

    @Override
    public long getFirstLineNumber() {
        return firstLineNumber;
    }

    @Override
    public void applyLineNumberOffset(long offset) {
        firstLineNumber += offset;
    }

    @Override
    public List<String> asList() {
        List<String> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(get(i));
        }
        return result;
    }
}
