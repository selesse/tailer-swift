package com.selesse.tailerswift.gui;

import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;
import com.selesse.tailerswift.threads.WorkerThreads;

import javax.swing.*;

public class GuiTailerSwift {
    public GuiTailerSwift() {
        if (Program.getInstance().getOperatingSystem() == OperatingSystem.MAC) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }

        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();

        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            // Very unlikely to throw an exception since it's SystemLookAndFeel
            e.printStackTrace();
        }

        new MainFrame(Program.getInstance().getSettings());

        // stop watching all the files
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                WorkerThreads.shutdown();
            }
        });
    }
}
