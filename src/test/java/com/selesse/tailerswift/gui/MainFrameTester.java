package com.selesse.tailerswift.gui;

import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;
import com.selesse.tailerswift.settings.Settings;
import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.security.ExitCallHook;
import org.fest.swing.security.NoExitSecurityManagerInstaller;
import org.fest.swing.util.Platform;
import org.fest.util.Files;
import org.junit.*;

import java.awt.event.KeyEvent;
import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;


public class MainFrameTester {
    private FrameFixture window;
    private static NoExitSecurityManagerInstaller noExitSecurityManagerInstaller;
    private static boolean exitedCleanly;

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
        noExitSecurityManagerInstaller = NoExitSecurityManagerInstaller.installNoExitSecurityManager(new ExitCallHook() {
            @Override
            public void exitCalled(int status) {
                if (status == 0) {
                    exitedCleanly = true;
                }
            }
        });
    }

    @AfterClass
    public static void tearDownOnce() {
        noExitSecurityManagerInstaller.uninstall();
    }

    @Before
    public void setUp() {
        exitedCleanly = false;

        MainFrame testFrame = GuiActionRunner.execute(new GuiQuery<MainFrame>() {
            @Override
            protected MainFrame executeInEDT() throws Throwable {
                return new MainFrame(new Settings());
            }
        });

        window = new FrameFixture(testFrame.getFrame());
        window.show();
    }

    @After
    public void tearDown() {
        window.cleanUp();
    }

    @Test
    public void testPressingExitKeyboardShortcutExits() {
        KeyPressInfo keyPressInfo = KeyPressInfo.keyCode(KeyEvent.VK_Q).modifiers(Platform.controlOrCommandMask());
        window.pressAndReleaseKey(keyPressInfo);
        assertTrue(exitedCleanly);
    }

    @Test
    public void testClickingFileThenQuitExits() {
        // we don't have an "Exit" option on OS X
        assumeTrue(Program.getInstance().getOperatingSystem() != OperatingSystem.MAC);
        window.menuItem("Exit").click();
        assertTrue(exitedCleanly);
    }

    @Test
    public void testClickingOnWatchFileOpensTheDialog() {
        window.menuItem("Open/watch file...").click();
        window.fileChooser("File chooser").requireVisible();
    }

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
