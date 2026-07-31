package com.selesse.tailerswift.gui.lines;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RingLineBufferTester {
    @Test
    public void newBufferIsEmptyAndStartsAtLineOne() {
        RingLineBuffer buffer = new RingLineBuffer(3);

        assertEquals(0, buffer.size());
        assertEquals(1, buffer.getFirstLineNumber());
        assertTrue(buffer.asList().isEmpty());
    }

    @Test
    public void appendedLinesAreReadableInOrder() {
        RingLineBuffer buffer = new RingLineBuffer(3);

        buffer.append("a");
        buffer.append("b");

        assertEquals(2, buffer.size());
        assertEquals("a", buffer.get(0));
        assertEquals("b", buffer.get(1));
        assertEquals(1, buffer.getFirstLineNumber());
    }

    @Test
    public void exceedingCapacityEvictsOldestAndAdvancesFirstLineNumber() {
        RingLineBuffer buffer = new RingLineBuffer(3);

        buffer.append("a");
        buffer.append("b");
        buffer.append("c");
        buffer.append("d");

        assertEquals(3, buffer.size());
        assertEquals("b", buffer.get(0));
        assertEquals("c", buffer.get(1));
        assertEquals("d", buffer.get(2));
        assertEquals(2, buffer.getFirstLineNumber());
    }

    @Test
    public void continuesEvictingCorrectlyAcrossManyWrapArounds() {
        RingLineBuffer buffer = new RingLineBuffer(4);

        for (int i = 1; i <= 100; i++) {
            buffer.append("line" + i);
        }

        assertEquals(4, buffer.size());
        assertEquals(97, buffer.getFirstLineNumber());
        assertEquals("line97", buffer.get(0));
        assertEquals("line98", buffer.get(1));
        assertEquals("line99", buffer.get(2));
        assertEquals("line100", buffer.get(3));
    }

    @Test
    public void clearResetsToInitialState() {
        RingLineBuffer buffer = new RingLineBuffer(3);
        buffer.append("a");
        buffer.append("b");
        buffer.append("c");
        buffer.append("d");

        buffer.clear();

        assertEquals(0, buffer.size());
        assertEquals(1, buffer.getFirstLineNumber());

        buffer.append("fresh");
        assertEquals("fresh", buffer.get(0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getOutOfBoundsThrows() {
        RingLineBuffer buffer = new RingLineBuffer(3);
        buffer.append("a");

        buffer.get(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void zeroCapacityIsRejected() {
        new RingLineBuffer(0);
    }

    @Test
    public void asListReflectsCurrentWindowOnly() {
        RingLineBuffer buffer = new RingLineBuffer(2);
        buffer.append("a");
        buffer.append("b");
        buffer.append("c");

        assertEquals(java.util.Arrays.asList("b", "c"), buffer.asList());
    }
}
