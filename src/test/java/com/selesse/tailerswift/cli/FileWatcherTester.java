package com.selesse.tailerswift.cli;

import com.google.common.io.Files;
import com.selesse.tailerswift.TestUtil;
import com.selesse.tailerswift.filewatcher.FileWatcher;
import com.selesse.tailerswift.gui.TestUi;
import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;
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
        if (!testDirectory.delete()) {
            System.err.println("Was not able to delete " + testDirectory);
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

        TestUtil.deleteInOrder(tempFile);
    }

    @Test
    public void testCanWatchNonExistentFile() {
        String filename = "" + System.currentTimeMillis();
        File tempFile = new File(testDirectory.getAbsolutePath() + File.separator + filename);

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

        TestUtil.deleteInOrder(tempFile);
    }

    private File createFileWithContents(String filename, String contents) {
        File file = new File(testDirectory + File.separator + filename);

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
