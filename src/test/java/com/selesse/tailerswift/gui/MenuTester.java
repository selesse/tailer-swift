package com.selesse.tailerswift.gui;

import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.KeyPressInfo;
import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.security.ExitCallHook;
import org.fest.swing.security.NoExitSecurityManagerInstaller;
import org.fest.swing.util.Platform;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import java.awt.event.KeyEvent;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings({"ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD"})
public class MenuTester extends AbstractMainFrameTester {
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

    @Override
    public void setup() {
        super.setup();

        exitedCleanly = false;
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
        assumeTrue(weShouldRunUiTests());
        window.menuItem("Exit").click();
        assertTrue(exitedCleanly);
    }

    @Test
    public void testPressingF1BringsUpHelp() {
        KeyPressInfo keyPressInfo = KeyPressInfo.keyCode(KeyEvent.VK_F1);
        window.pressAndReleaseKey(keyPressInfo);

        FrameFixture aboutFrame = WindowFinder.findFrame("About").using(window.robot);
        aboutFrame.requireVisible();
    }

    @Test
    public void testClickingOnAboutBringsUpAbout() {
        window.menuItem("About").click();

        FrameFixture aboutFrame = WindowFinder.findFrame("About").using(window.robot);
        aboutFrame.requireVisible();
    }

    @Test
    public void testClickingOnAlwaysOnTopTogglesIt() {
        boolean isSetToAlwaysOnTop = Program.getInstance().getSettings().isAlwaysOnTop();

        window.menuItem(new GenericTypeMatcher<JMenuItem>(JMenuItem.class) {
            @Override
            protected boolean isMatching(JMenuItem component) {
                return (component instanceof JCheckBoxMenuItem);
            }
        }).click();

        assertTrue(isSetToAlwaysOnTop != Program.getInstance().getSettings().isAlwaysOnTop());
    }

    @Test
    public void testClickingOnWatchFileOpensTheDialog() {
        window.menuItem("Open/watch file...").click();
        window.fileChooser("File chooser").requireVisible();
    }

}
