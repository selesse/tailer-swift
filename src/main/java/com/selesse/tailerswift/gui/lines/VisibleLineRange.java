package com.selesse.tailerswift.gui.lines;

/**
 * The span of line indices (0-based, {@link #lastExclusive()} exclusive) that need to be
 * painted for a given scroll position. Pure arithmetic, no Swing dependency, so the
 * "what do I need to draw" question can be tested without a display.
 */
public final class VisibleLineRange {
    private final int firstInclusive;
    private final int lastExclusive;

    private VisibleLineRange(int firstInclusive, int lastExclusive) {
        this.firstInclusive = firstInclusive;
        this.lastExclusive = lastExclusive;
    }

    /**
     * @param clipTop      top of the region needing repaint, in pixels from the top of the full (virtual) content
     * @param clipHeight   height of the region needing repaint, in pixels
     * @param lineHeight   height of a single line, in pixels
     * @param totalLines   total number of lines currently in the buffer
     * @param overscanLines extra lines to include above/below the clip, to reduce flicker/re-layout on small scrolls
     */
    public static VisibleLineRange compute(int clipTop, int clipHeight, int lineHeight, int totalLines,
                                            int overscanLines) {
        if (totalLines <= 0 || lineHeight <= 0) {
            return new VisibleLineRange(0, 0);
        }

        int first = clipTop / lineHeight - overscanLines;
        int last = (clipTop + clipHeight + lineHeight - 1) / lineHeight + overscanLines;

        first = clamp(first, 0, totalLines);
        last = clamp(last, 0, totalLines);

        return new VisibleLineRange(first, last);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public int firstInclusive() {
        return firstInclusive;
    }

    public int lastExclusive() {
        return lastExclusive;
    }

    public boolean isEmpty() {
        return firstInclusive >= lastExclusive;
    }
}
