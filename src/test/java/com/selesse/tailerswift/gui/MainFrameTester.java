package com.selesse.tailerswift.gui;

import org.fest.swing.edt.FailOnThreadViolationRepaintManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class MainFrameTester {
    private FrameFixture window;

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Before
    public void setUp() {
        MainFrame testFrame = GuiActionRunner.execute(new GuiQuery<MainFrame>() {
            @Override
            protected MainFrame executeInEDT() throws Throwable {
                return new MainFrame();
            }
        });
        Thread thread = new Thread(testFrame);
        thread.start();

        window = new FrameFixture(testFrame.getFrame());
        window.show();
    }

    @After
    public void tearDown() {
        window.cleanUp();
    }

    @Test
    public void testClickingOnWatchFileOpensTheDialog() {
        window.menuItem("Open/watch file...").click();
        window.dialog("File dialog").requireVisible();
    }
}
