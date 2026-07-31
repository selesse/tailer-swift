package com.selesse.tailerswift.gui.lines;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LineSplitterTester {
    @Test
    public void chunkWithNoNewlineYieldsNoLinesYet() {
        LineSplitter splitter = new LineSplitter();

        List<String> lines = splitter.append("no newline here");

        assertTrue(lines.isEmpty());
        assertEquals("no newline here", splitter.pendingLine());
    }

    @Test
    public void singleChunkWithMultipleLines() {
        LineSplitter splitter = new LineSplitter();

        List<String> lines = splitter.append("one\ntwo\nthree\n");

        assertEquals(java.util.Arrays.asList("one", "two", "three"), lines);
        assertEquals("", splitter.pendingLine());
    }

    @Test
    public void partialLineIsStitchedOntoNextChunk() {
        LineSplitter splitter = new LineSplitter();

        List<String> firstResult = splitter.append("hello wor");
        List<String> secondResult = splitter.append("ld\nnext line is partial");

        assertTrue(firstResult.isEmpty());
        assertEquals(java.util.Arrays.asList("hello world"), secondResult);
        assertEquals("next line is partial", splitter.pendingLine());
    }

    @Test
    public void crlfLineEndingsAreTreatedLikeLf() {
        LineSplitter splitter = new LineSplitter();

        List<String> lines = splitter.append("windows\r\nstyle\r\n");

        assertEquals(java.util.Arrays.asList("windows", "style"), lines);
    }

    @Test
    public void loneCarriageReturnDoesNotTerminateALine() {
        LineSplitter splitter = new LineSplitter();

        List<String> lines = splitter.append("a\rb\n");

        assertEquals(java.util.Arrays.asList("a\rb"), lines);
    }

    @Test
    public void newlineSplitAcrossChunkBoundaryIsHandled() {
        LineSplitter splitter = new LineSplitter();

        List<String> firstResult = splitter.append("line one\r");
        List<String> secondResult = splitter.append("\nline two\n");

        assertTrue(firstResult.isEmpty());
        assertEquals(java.util.Arrays.asList("line one", "line two"), secondResult);
    }

    @Test
    public void resetDropsPendingPartialLine() {
        LineSplitter splitter = new LineSplitter();
        splitter.append("partial");

        splitter.reset();

        assertEquals("", splitter.pendingLine());
    }

    @Test
    public void emptyChunkIsANoOp() {
        LineSplitter splitter = new LineSplitter();

        List<String> lines = splitter.append("");

        assertTrue(lines.isEmpty());
        assertEquals("", splitter.pendingLine());
    }
}
