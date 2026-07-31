package com.selesse.tailerswift.gui.lines;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns a stream of raw text chunks (as produced by reading a growing file in fixed-size
 * reads) into a stream of complete lines. Chunk boundaries rarely line up with line
 * boundaries, so a trailing partial line from one chunk is held back and stitched onto
 * the front of the next chunk.
 *
 * <p>Line-ending agnostic like the rest of the app: {@code \r\n} and {@code \n} both
 * terminate a line, a lone {@code \r} does not.
 */
public class LineSplitter {
    private final StringBuilder pending = new StringBuilder();

    /**
     * @return the lines completed by this chunk, in order. A chunk with no newline in it
     *         yields no lines - its content is held until a future chunk completes it.
     */
    public List<String> append(String chunk) {
        List<String> completedLines = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < chunk.length(); i++) {
            if (chunk.charAt(i) == '\n') {
                pending.append(chunk, start, i);
                completedLines.add(stripTrailingCarriageReturn(pending.toString()));
                pending.setLength(0);
                start = i + 1;
            }
        }
        pending.append(chunk, start, chunk.length());
        return completedLines;
    }

    /**
     * Returns the partial line accumulated since the last completed line, without
     * consuming it. Callers who want to render "everything so far" (e.g. showing a
     * file's current tail before its final line has been terminated by a newline)
     * can use this; it is left untouched by future {@link #append(String)} calls
     * until they themselves complete it.
     */
    public String pendingLine() {
        return pending.toString();
    }

    public void reset() {
        pending.setLength(0);
    }

    private static String stripTrailingCarriageReturn(String line) {
        if (line.endsWith("\r")) {
            return line.substring(0, line.length() - 1);
        }
        return line;
    }
}
