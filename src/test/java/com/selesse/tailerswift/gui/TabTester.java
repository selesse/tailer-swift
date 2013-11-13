package com.selesse.tailerswift.gui;

import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;
import org.fest.util.Files;
import org.junit.*;

import java.io.File;

import static org.junit.Assume.assumeTrue;

public class TabTester extends AbstractMainFrameTester {
    @Test
    public void testOpeningAFileCreatesProperTabTitle() {
        // For some reason, on OS X, "approve"ing the file below throws an exception
        assumeTrue(Program.getInstance().getOperatingSystem() != OperatingSystem.MAC);

        // set up the temp directory and file for the test
        File tempDirectory = Files.newTemporaryFolder();
        File tempFile = Files.newFile(tempDirectory.getAbsolutePath() + File.separator + "temp.txt");
        tempDirectory.deleteOnExit();
        tempFile.deleteOnExit();

        window.menuItem("Open/watch file...").click();
        window.fileChooser("File chooser").setCurrentDirectory(Files.newTemporaryFolder());
        window.fileChooser("File chooser").selectFile(tempFile);
        window.fileChooser("File chooser").approve();

        window.tabbedPane("Tabbed pane").requireVisible();
        window.tabbedPane("Tabbed pane").requireTabTitles(tempFile.getName());
    }

    @Test
    public void testCanOpenMultipleFiles() {
        // For some reason, on OS X, "approve"ing the files below throws an exception
        assumeTrue(Program.getInstance().getOperatingSystem() != OperatingSystem.MAC);

        File tempDirectory = Files.newTemporaryFolder();
        File tempFile = Files.newFile(tempDirectory.getAbsolutePath() + File.separator + "foo");
        File tempFile2 = Files.newFile(tempDirectory.getAbsolutePath() + File.separator + "foo2");
        File tempFile3 = Files.newFile(tempDirectory.getAbsolutePath() + File.separator + "foo3");
        tempDirectory.deleteOnExit();
        tempFile.deleteOnExit();
        tempFile2.deleteOnExit();
        tempFile3.deleteOnExit();

        window.menuItem("Open/watch file...").click();
        window.fileChooser("File chooser").setCurrentDirectory(Files.newTemporaryFolder());
        window.fileChooser("File chooser").selectFiles(tempFile, tempFile2, tempFile3);
        window.fileChooser("File chooser").approve();

        window.tabbedPane("Tabbed pane").requireVisible();
        window.tabbedPane("Tabbed pane").requireTabTitles(tempFile.getName(), tempFile2.getName(), tempFile3.getName());
    }
}
