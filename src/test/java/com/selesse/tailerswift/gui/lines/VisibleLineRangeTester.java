package com.selesse.tailerswift.gui.lines;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VisibleLineRangeTester {
    @Test
    public void topOfDocumentWithNoOverscan() {
        // 10px lines, 100px viewport => 10 lines visible, starting at line 0
        VisibleLineRange range = VisibleLineRange.compute(0, 100, 10, 1_000_000, 0);

        assertEquals(0, range.firstInclusive());
        assertEquals(10, range.lastExclusive());
    }

    @Test
    public void scrolledMidDocument() {
        // scrolled down 500px, still a 100px viewport of 10px lines => lines [50, 60)
        VisibleLineRange range = VisibleLineRange.compute(500, 100, 10, 1_000_000, 0);

        assertEquals(50, range.firstInclusive());
        assertEquals(60, range.lastExclusive());
    }

    @Test
    public void overscanExpandsBothEdges() {
        VisibleLineRange range = VisibleLineRange.compute(500, 100, 10, 1_000_000, 5);

        assertEquals(45, range.firstInclusive());
        assertEquals(65, range.lastExclusive());
    }

    @Test
    public void clampsToStartOfBuffer() {
        VisibleLineRange range = VisibleLineRange.compute(0, 100, 10, 1_000_000, 5);

        assertEquals(0, range.firstInclusive());
    }

    @Test
    public void clampsToEndOfBuffer() {
        // Only 12 lines total, viewport asks for way more than that
        VisibleLineRange range = VisibleLineRange.compute(0, 1000, 10, 12, 0);

        assertEquals(0, range.firstInclusive());
        assertEquals(12, range.lastExclusive());
    }

    @Test
    public void emptyBufferYieldsEmptyRange() {
        VisibleLineRange range = VisibleLineRange.compute(0, 100, 10, 0, 0);

        assertTrue(range.isEmpty());
    }

    @Test
    public void partialTrailingLineIsIncluded() {
        // 100px viewport, 30px lines => 3 full lines + 1 partially visible line = 4
        VisibleLineRange range = VisibleLineRange.compute(0, 100, 30, 1_000_000, 0);

        assertEquals(0, range.firstInclusive());
        assertEquals(4, range.lastExclusive());
    }
}
