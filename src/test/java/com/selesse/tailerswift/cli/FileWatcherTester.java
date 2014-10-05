package com.selesse.tailerswift.cli;

import com.google.common.io.Files;
import com.selesse.tailerswift.filewatcher.FileWatcher;
import com.selesse.tailerswift.gui.TestUi;
import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class FileWatcherTester {
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
    public void testCanWatchExistentFile() {
        String filename = "" + System.currentTimeMillis();
        File tempFile = createFileWithContents(filename, "This is a stupid file");

        TestUi testUi = new TestUi(1);
        FileWatcher fileWatcher = new FileWatcher(testUi, tempFile.getAbsolutePath());
        Thread thread = new Thread(fileWatcher);
        thread.start();

        try {
            thread.join(getSleepTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals("This is a stupid file", testUi.getBufferContents());

        if (!tempFile.delete()) {
            System.err.println("Failed to delete " + tempFile.getAbsolutePath());
        }
    }

    @Test
    public void testCanWatchNonExistentFile() {
        String filename = "" + System.currentTimeMillis();
        File tempFile = new File(testDirectory, filename);

        assertFalse(tempFile.exists());

        TestUi testUi = new TestUi(1);
        FileWatcher fileWatcher = new FileWatcher(testUi, tempFile.getAbsolutePath());
        Thread thread = new Thread(fileWatcher);
        thread.start();

        try {
            if (!tempFile.createNewFile()) {
                fail("Was not able to create a new file");
            }
            assertTrue(tempFile.exists());

            PrintWriter printWriter = new PrintWriter(tempFile, "UTF-8");
            printWriter.print("some text to buffer");
            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            thread.join(getSleepTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals("some text to buffer", testUi.getBufferContents());

        if (!tempFile.delete()) {
            System.err.println("Failed to delete " + tempFile.getAbsolutePath());
        }
    }

    private File createFileWithContents(String filename, String contents) {
        File file = new File(testDirectory, filename);

        try {
            PrintWriter printWriter = new PrintWriter(file, "UTF-8");
            printWriter.print(contents);
            printWriter.flush();
            printWriter.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return file;
    }

    private long getSleepTime() {
        if (Program.getInstance().getOperatingSystem() == OperatingSystem.MAC) {
            // Yes.. it's THAT slow... :(
            return 10000;
        }

        return 1500;
    }
}
