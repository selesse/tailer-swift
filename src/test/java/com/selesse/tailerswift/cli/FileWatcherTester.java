package com.selesse.tailerswift.cli;

import com.google.common.io.Files;
import com.selesse.tailerswift.filewatcher.FileWatcher;
import com.selesse.tailerswift.gui.TestUi;
import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

public class FileWatcherTester {
    private File testDirectory;

    @Before
    public void setup() {
        testDirectory = Files.createTempDir();
    }

    @After
    public void tearDown() {
        testDirectory.delete();
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
    }

    @Test
    public void testCanWatchNonExistentFile() {
        assumeTrue(Program.getInstance().getOperatingSystem() != OperatingSystem.MAC);
        String filename = "" + System.currentTimeMillis();
        File tempFile = new File(testDirectory.getAbsolutePath() + File.separator + filename);
        tempFile.deleteOnExit();

        assertFalse(tempFile.exists());

        TestUi testUi = new TestUi(1);
        FileWatcher fileWatcher = new FileWatcher(testUi, tempFile.getAbsolutePath());
        Thread thread = new Thread(fileWatcher);
        thread.start();

        try {
            tempFile.createNewFile();
            assertTrue(tempFile.exists());

            PrintWriter printWriter = new PrintWriter(tempFile);
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
    }

    private File createFileWithContents(String filename, String contents) {
        File file = new File(testDirectory + File.separator + filename);
        file.deleteOnExit();

        try {
            PrintWriter printWriter = new PrintWriter(file);
            printWriter.print(contents);
            printWriter.flush();
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return file;
    }

    private long getSleepTime() {
        if (Program.getInstance().getOperatingSystem() == OperatingSystem.MAC) {
            return 5000;
        }

        return 1000;
    }
}
