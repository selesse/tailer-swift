package com.selesse.tailerswift.ui;

import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;
import com.selesse.tailerswift.settings.Settings;

import javax.swing.*;
import java.io.*;

public class GuiTailerSwift implements Runnable {
    private final String settingsName = ".tswiftrc";
    private final String homeDirectory = System.getProperty("user.home");

    public GuiTailerSwift() {
        if (Program.getInstance().getOperatingSystem() == OperatingSystem.MAC) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }

        Program.getInstance().setSettings(loadOrInitializeSettings());
    }

    private Settings loadOrInitializeSettings() {
        File settingsFile = new File(homeDirectory + File.separator + settingsName);

        Settings settings = new Settings();

        if (settingsFile.exists()) {
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(settingsFile));
                settings = (Settings) objectInputStream.readObject();
                objectInputStream.close();
            } catch (Exception e) {
                // it's okay if we screw up
            }
        }

        return settings;
    }

    private void saveSettings() {
        File settingsFile = new File(homeDirectory + File.separator + settingsName);

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(settingsFile));
            Settings settings = Program.getInstance().getSettings();
            objectOutputStream.writeObject(settings);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            // TODO figure out best course of action
            e.printStackTrace();
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
                saveSettings();

                for (Thread thread : mainFrame.getAllThreads()) {
                    thread.interrupt();
                }
            }
        });
    }
}
