package com.selesse.tailerswift.gui;

import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;
import com.selesse.tailerswift.settings.Settings;
import org.junit.Test;

import javax.swing.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class MainFrameTester extends AbstractMainFrameTester {
    @Override
    public void setup() {
        // do nothing to prevent default behavior
        assumeTrue(weShouldRunUiTests());
    }

    @Test
    public void testDefaultSettingsAreLoaded() {
        final Settings settings = new Settings();

        startFrameWithSettings(settings);

        assertTrue(!window.menuItem("Always on top").targetCastedTo(JMenuItem.class).isSelected());
    }

    @Test
    public void testAlwaysOnTopSettingIsLoaded() {
        final Settings settings = new Settings();
        settings.setAlwaysOnTop(true);

        startFrameWithSettings(settings);

        assertTrue(window.menuItem("Always on top").targetCastedTo(JMenuItem.class).isSelected());
    }
}
