package com.selesse.tailerswift.gui;

import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;
import com.selesse.tailerswift.settings.Settings;

import javax.swing.*;
import java.io.*;

public class GuiTailerSwift implements Runnable {
    public GuiTailerSwift() {
        if (Program.getInstance().getOperatingSystem() == OperatingSystem.MAC) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
    }

    @Override
    public void run() {
        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();

        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            // Very unlikely to throw an exception since it's SystemLookAndFeel
            e.printStackTrace();
        }

        final MainFrame mainFrame = new MainFrame();
        Thread uiThread = new Thread(mainFrame);
        uiThread.start();

        // stop watching all the files
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                for (Thread thread : mainFrame.getAllThreads()) {
                    thread.interrupt();
                }
            }
        });
    }
}
