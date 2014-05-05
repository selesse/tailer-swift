package com.selesse.tailerswift.gui;

import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;
import com.selesse.tailerswift.settings.Settings;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;

import java.awt.GraphicsEnvironment;

import static org.junit.Assume.assumeTrue;

public class AbstractMainFrameTester {
    protected FrameFixture window;

    @Before
    public void setup() {
        assumeTrue(weShouldRunUiTests());
        startFrameWithSettings(new Settings());
    }

    protected boolean weShouldRunUiTests() {
        return !GraphicsEnvironment.isHeadless() && (Program.getInstance().getOperatingSystem() != OperatingSystem.MAC);
    }

    protected void startFrameWithSettings(final Settings settings) {
        settings.setTest(true);
        Program.getInstance().setSettings(settings);

        MainFrame testFrame = GuiActionRunner.execute(new GuiQuery<MainFrame>() {
            @Override
            protected MainFrame executeInEDT() throws Throwable {
                return new MainFrame(settings);
            }
        });

        window = new FrameFixture(testFrame.getFrame());
        window.show();
    }

    @After
    public void tearDown() {
        if (window != null) {
            window.cleanUp();
        }
    }

}
