package com.selesse.tailerswift.gui;

import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;
import org.apache.commons.io.FileUtils;
import org.fest.swing.data.Index;
import com.google.common.io.Files;
import org.junit.After;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;

import static org.junit.Assume.assumeTrue;

public class TabTester extends AbstractMainFrameTester {
    private File tempDirectory;
    private File tempFile;
    private File tempFile2;
    private File tempFile3;

    @After
    public void tearDownTempDirectory() {
        if (tempDirectory != null && tempDirectory.exists()) {
            try {
                FileUtils.deleteDirectory(tempDirectory);
            } catch (IOException ignored) {
                System.err.println("Failed to delete " + tempDirectory.getAbsolutePath());
            }
        }

    }

    private void createThreeTempFiles() {
        tempDirectory = Files.createTempDir();
        tempFile = new File(tempDirectory, "foo");
        tempFile2 = new File(tempDirectory, "foo2");
        tempFile3 = new File(tempDirectory, "foo3");
    }

    @Test
    public void testOpeningAFileCreatesProperTabTitle() {
        // For some reason, on OS X, "approve"ing the file below throws an exception
        assumeTrue(weShouldRunUiTests());

        // set up the temp directory and file for the test
        File tempDirectory = Files.createTempDir();
        File tempFile = new File(tempDirectory, "temp.txt");

        window.menuItem("Open/watch file...").click();
        window.fileChooser("File chooser").setCurrentDirectory(tempDirectory);
        window.fileChooser("File chooser").selectFile(tempFile);
        window.fileChooser("File chooser").approve();

        window.tabbedPane("Tabbed pane").requireVisible();
        window.tabbedPane("Tabbed pane").requireTabTitles(tempFile.getName());
    }

    @Test
    public void testCanOpenMultipleFiles() {
        // For some reason, on OS X, "approve"ing the files below throws an exception
        assumeTrue(weShouldRunUiTests());

        createThreeTempFiles();
        simulateChoosingThreeTempFiles();

        window.tabbedPane("Tabbed pane").requireVisible();
        window.tabbedPane("Tabbed pane").requireTabTitles(tempFile.getName(), tempFile2.getName(), tempFile3.getName());
    }

    @Test
    public void testClosingMiddleTabPreservesTitleHistory() {
        // For some reason, on OS X, "approve"ing the files below throws an exception
        assumeTrue(weShouldRunUiTests());

        createThreeTempFiles();
        simulateChoosingThreeTempFiles();

        String[] assumedTabTitles = new String[] { tempFile.getName(), tempFile2.getName(), tempFile3.getName() };
        assumeTrue(Arrays.deepEquals(window.tabbedPane("Tabbed pane").tabTitles(), assumedTabTitles));

        window.tabbedPane("Tabbed pane").selectTab(1);
        window.menuItem("Close current file").click();

        window.tabbedPane("Tabbed pane").requireTabTitles(tempFile.getName(), tempFile3.getName());
    }

    @Test
    public void testClosingLastTabPreservesTitleHistory() {
        // For some reason, on OS X, "approve"ing the files below throws an exception
        assumeTrue(weShouldRunUiTests());

        createThreeTempFiles();
        simulateChoosingThreeTempFiles();

        String[] assumedTabTitles = new String[] { tempFile.getName(), tempFile2.getName(), tempFile3.getName() };
        assumeTrue(Arrays.deepEquals(window.tabbedPane("Tabbed pane").tabTitles(), assumedTabTitles));

        window.tabbedPane("Tabbed pane").selectTab(2);
        window.menuItem("Close current file").click();

        window.tabbedPane("Tabbed pane").requireTabTitles(tempFile.getName(), tempFile2.getName());
    }

    @Test
    public void testClosingFirstTabPreservesTitleHistory() {
        // For some reason, on OS X, "approve"ing the files below throws an exception
        assumeTrue(weShouldRunUiTests());

        createThreeTempFiles();
        simulateChoosingThreeTempFiles();

        String[] assumedTabTitles = new String[] { tempFile.getName(), tempFile2.getName(), tempFile3.getName() };
        assumeTrue(Arrays.deepEquals(window.tabbedPane("Tabbed pane").tabTitles(), assumedTabTitles));

        window.tabbedPane("Tabbed pane").selectTab(0);
        window.menuItem("Close current file").click();

        window.tabbedPane("Tabbed pane").requireTabTitles(tempFile2.getName(), tempFile3.getName());
    }

    @Test
    public void testTitleChangesWhenContentIsAdded() throws FileNotFoundException, InterruptedException, UnsupportedEncodingException {
        // For some reason, on OS X, "approve"ing the files below throws an exception
        assumeTrue(weShouldRunUiTests());

        File tempWriteDirectory = Files.createTempDir();
        File tempWriteFile = new File(tempWriteDirectory, "zzz");

        createThreeTempFiles();

        simulateChoosingThreeTempFiles();
        simulateChoosingThreeTempFiles();

        window.menuItem("Open/watch file...").click();
        window.fileChooser("File chooser").setCurrentDirectory(tempWriteDirectory);
        window.fileChooser("File chooser").selectFile(tempWriteFile);
        window.fileChooser("File chooser").approve();

        window.tabbedPane("Tabbed pane").requireTabTitles(tempFile.getName(), tempFile2.getName(), tempFile3.getName(),
                tempWriteFile.getName());

        window.tabbedPane("Tabbed pane").selectTab(3);
        Thread.sleep(100);
        window.tabbedPane("Tabbed pane").selectTab(0);

        PrintWriter printWriter = new PrintWriter(tempWriteFile, "UTF-8");
        printWriter.println("Hello, world!");
        printWriter.flush();
        printWriter.close();

        threadSleepBasedOnOperatingSystem();

        window.tabbedPane("Tabbed pane").requireTabTitles(tempFile.getName(), tempFile2.getName(), tempFile3.getName(),
                "* " + tempWriteFile.getName());

        try {
            FileUtils.deleteDirectory(tempWriteDirectory);
        } catch (IOException e) {
            System.err.println("Failed to delete " + tempWriteDirectory.getAbsolutePath());
        }
    }

    private void simulateChoosingThreeTempFiles() {
        window.menuItem("Open/watch file...").click();
        window.fileChooser("File chooser").setCurrentDirectory(tempDirectory);
        window.fileChooser("File chooser").selectFiles(tempFile, tempFile2, tempFile3);
        window.fileChooser("File chooser").approve();
    }

    @Test
    public void testModificationHintDisappearsProperly() throws FileNotFoundException, InterruptedException, UnsupportedEncodingException {
        // We choose 3 (empty) temp files
        createThreeTempFiles();
        simulateChoosingThreeTempFiles();

        window.tabbedPane("Tabbed pane").selectTab(0);

        // While we write to the first tab, we don't expect the title to change
        PrintWriter printWriter = new PrintWriter(tempFile, "UTF-8");
        printWriter.println("hello, world!");
        printWriter.flush();

        window.tabbedPane("Tabbed pane").selectTab(0);
        window.tabbedPane("Tabbed pane").requireTitle(tempFile.getName(), Index.atIndex(0));

        // Move to another tab, write to the first tab
        window.tabbedPane("Tabbed pane").selectTab(1);
        threadSleepBasedOnOperatingSystem();
        printWriter.println("Give me attention");
        printWriter.flush();
        printWriter.close();

        threadSleepBasedOnOperatingSystem();

        // The first tab should now have the modification hint
        window.tabbedPane("Tabbed pane").requireTitle("* " + tempFile.getName(), Index.atIndex(0));

        // And once we focus it, it should go away
        window.tabbedPane("Tabbed pane").selectTab(0);
        threadSleepBasedOnOperatingSystem();
        window.tabbedPane("Tabbed pane").requireTitle(tempFile.getName(), Index.atIndex(0));
    }

    private void threadSleepBasedOnOperatingSystem() throws InterruptedException {
        if (Program.getInstance().getOperatingSystem() == OperatingSystem.MAC) {
            Thread.sleep(4000);
        }
        else {
            Thread.sleep(2000);
        }
    }
}
