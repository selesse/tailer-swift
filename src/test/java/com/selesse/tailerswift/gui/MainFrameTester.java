package com.selesse.tailerswift.gui;

import com.selesse.tailerswift.settings.Settings;
import org.junit.Test;

import javax.swing.*;

public class MainFrameTester extends AbstractMainFrameTester {
    @Override
    public void setup() {
        // do nothing to prevent default behavior
    }

    @Test
    public void testAlwaysOnTopSettingIsLoaded() {
        final Settings settings = new Settings();
        settings.setAlwaysOnTop(true);

        startFrameWithSettings(settings);

        System.out.println(window.menuItem("Always on top").targetCastedTo(JMenuItem.class).isSelected());
    }
}
