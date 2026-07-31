package com.selesse.tailerswift.gui;

import com.selesse.tailerswift.settings.Program;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.KeyPressInfo;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.Test;

import javax.swing.*;
import java.awt.event.KeyEvent;

import static org.junit.Assert.assertTrue;

/**
 * Deliberately does not cover the "Exit"/Cmd+Q menu actions: verifying those actually call
 * {@code System.exit()} requires trapping real JVM exit via a SecurityManager, which both
 * FEST and AssertJ-Swing implement the same fragile way - and SecurityManager is being
 * removed from the JDK entirely, so that mechanism was already broken on JDK 17+. The menu
 * items themselves (existence, accelerators) aren't worth testing separately from what
 * Swing itself guarantees once wired up.
 */
public class MenuTester extends AbstractMainFrameTester {
    @Test
    public void testPressingF1BringsUpHelp() {
        KeyPressInfo keyPressInfo = KeyPressInfo.keyCode(KeyEvent.VK_F1);
        window.pressAndReleaseKey(keyPressInfo);

        FrameFixture aboutFrame = WindowFinder.findFrame("About").using(window.robot());
        aboutFrame.requireVisible();
    }

    @Test
    public void testClickingOnAboutBringsUpAbout() {
        window.menuItem("About").click();

        FrameFixture aboutFrame = WindowFinder.findFrame("About").using(window.robot());
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
