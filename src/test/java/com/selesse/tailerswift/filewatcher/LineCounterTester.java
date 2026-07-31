package com.selesse.tailerswift.filewatcher;

import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class LineCounterTester {
    private File testDirectory;

    @Before
    public void setup() {
        testDirectory = Files.createTempDir();
    }

    @After
    public void tearDown() {
        try {
            FileUtils.deleteDirectory(testDirectory);
        } catch (IOException ignored) {
            System.err.println("Failed to delete " + testDirectory.getAbsolutePath());
        }
    }

    @Test
    public void emptyFileHasNoLines() throws IOException {
        File file = writeRaw("");

        assertEquals(0, new LineCounter(file).countLinesBefore(0));
    }

    @Test
    public void zeroByteLimitCountsNothingEvenIfFileHasContent() throws IOException {
        File file = writeRaw("one\ntwo\nthree\n");

        assertEquals(0, new LineCounter(file).countLinesBefore(0));
    }

    @Test
    public void countsNewlinesUpToAnExactLineBoundary() throws IOException {
        File file = writeRaw("one\ntwo\nthree\n");

        long boundary = "one\ntwo\n".getBytes(StandardCharsets.UTF_8).length;

        assertEquals(2, new LineCounter(file).countLinesBefore(boundary));
    }

    @Test
    public void countsNewlinesUpToAMidLineOffset() throws IOException {
        File file = writeRaw("one\ntwo\nthree\n");

        long midThirdLine = "one\ntwo\ntwo".getBytes(StandardCharsets.UTF_8).length;

        assertEquals(2, new LineCounter(file).countLinesBefore(midThirdLine));
    }

    @Test
    public void countingThroughTheWholeFileMatchesTotalLines() throws IOException {
        File file = writeRaw("one\ntwo\nthree\nfour\nfive\n");

        long fileLength = file.length();

        assertEquals(5, new LineCounter(file).countLinesBefore(fileLength));
    }

    @Test
    public void byteLimitBeyondFileLengthJustStopsAtEof() throws IOException {
        File file = writeRaw("one\ntwo\n");

        assertEquals(2, new LineCounter(file).countLinesBefore(file.length() + 1000));
    }

    @Test
    public void unterminatedFinalLineIsNotCountedUntilItsNewlineArrives() throws IOException {
        File file = writeRaw("one\ntwo\npartial-no-newline");

        assertEquals(2, new LineCounter(file).countLinesBefore(file.length()));
    }

    @Test
    public void scansAcrossMultipleReadBufferBoundaries() throws IOException {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 5000; i++) {
            content.append("line-").append(i).append('\n');
        }
        File file = writeRaw(content.toString());

        assertEquals(5000, new LineCounter(file).countLinesBefore(file.length()));
    }

    private File writeRaw(String content) throws IOException {
        File file = new File(testDirectory, "line-counter-test.log");
        java.nio.file.Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
        return file;
    }
}
