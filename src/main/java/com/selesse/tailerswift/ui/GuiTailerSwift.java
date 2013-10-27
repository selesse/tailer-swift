package com.selesse.tailerswift.ui;

import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;

import javax.swing.*;

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
            e.printStackTrace();
        }

        final MainFrame mainFrame = new MainFrame();
        Thread uiThread = new Thread(mainFrame);
        uiThread.start();

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
