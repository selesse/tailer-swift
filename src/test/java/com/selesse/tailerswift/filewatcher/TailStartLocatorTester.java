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

public class TailStartLocatorTester {
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
    public void emptyFileLocatesAtStart() throws IOException {
        File file = writeLines();

        long offset = new TailStartLocator(file).locate(10);

        assertEquals(0, offset);
    }

    @Test
    public void fileWithFewerLinesThanDesiredLocatesAtStart() throws IOException {
        File file = writeLines("one", "two", "three");

        long offset = new TailStartLocator(file).locate(10);

        assertEquals(0, offset);
    }

    @Test
    public void fileWithExactlyDesiredLinesLocatesAtStart() throws IOException {
        File file = writeLines("one", "two", "three");

        long offset = new TailStartLocator(file).locate(3);

        assertEquals(0, offset);
    }

    @Test
    public void fileWithMoreLinesThanDesiredSkipsTheOldestOnes() throws IOException {
        File file = writeLines("one", "two", "three", "four", "five");

        long offset = new TailStartLocator(file).locate(2);

        assertEquals("four\nfive\n", readFrom(file, offset));
    }

    @Test
    public void desiredOfOneKeepsOnlyTheLastLine() throws IOException {
        File file = writeLines("one", "two", "three");

        long offset = new TailStartLocator(file).locate(1);

        assertEquals("three\n", readFrom(file, offset));
    }

    @Test
    public void unterminatedFinalLineIsKeptAlongWithTheRequestedCompleteLines() throws IOException {
        File file = writeRaw("one\ntwo\nthree\npartial-no-newline");

        long offset = new TailStartLocator(file).locate(1);

        assertEquals("three\npartial-no-newline", readFrom(file, offset));
    }

    @Test
    public void scanningAcrossMultipleBlocksFindsTheCorrectOffset() throws IOException {
        // A tiny scan block size forces the backward scan to cross several block boundaries,
        // exercising the same logic a real (much larger) block size would hit on a huge file.
        File file = writeLines("aaaa", "bbbb", "cccc", "dddd", "eeee", "ffff");

        long offset = new TailStartLocator(file, 3).locate(2);

        assertEquals("eeee\nffff\n", readFrom(file, offset));
    }

    @Test
    public void desiredLinesOfZeroOrLessLocatesAtStart() throws IOException {
        File file = writeLines("one", "two");

        assertEquals(0, new TailStartLocator(file).locate(0));
        assertEquals(0, new TailStartLocator(file).locate(-1));
    }

    private File writeLines(String... lines) throws IOException {
        StringBuilder content = new StringBuilder();
        for (String line : lines) {
            content.append(line).append('\n');
        }
        return writeRaw(content.toString());
    }

    private File writeRaw(String content) throws IOException {
        File file = new File(testDirectory, "tail-start-locator-test.log");
        java.nio.file.Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
        return file;
    }

    private String readFrom(File file, long offset) throws IOException {
        byte[] allBytes = java.nio.file.Files.readAllBytes(file.toPath());
        byte[] fromOffset = new byte[(int) (allBytes.length - offset)];
        System.arraycopy(allBytes, (int) offset, fromOffset, 0, fromOffset.length);
        return new String(fromOffset, StandardCharsets.UTF_8);
    }
}
