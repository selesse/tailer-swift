package com.selesse.tailerswift.settings;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

public class Program {
    private static final Logger logger = LoggerFactory.getLogger(Program.class);
    private final String settingsName = ".tswift-settings";
    private final String homeDirectory = System.getProperty("user.home");
    private final File settingsFile = new File(homeDirectory + File.separator + settingsName);
    private OperatingSystem operatingSystem;
    private Settings settings;
    private static Program instance;

    public static Program getInstance() {
        if (instance == null) {
            instance = new Program();
        }
        return instance;
    }

    public Program() {
        if (System.getProperty("os.name").contains("Mac")) {
            operatingSystem = OperatingSystem.MAC;
        }
        else if (System.getProperty("os.name").contains("Windows")) {
            operatingSystem = OperatingSystem.WINDOWS;
        }
        else {
            operatingSystem = OperatingSystem.LINUX;
        }

        settings = loadOrInitializeSettings();
    }

    private Settings loadOrInitializeSettings() {
        Settings settings = new Settings();

        if (settingsFile.exists()) {
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(settingsFile));
                settings = (Settings) objectInputStream.readObject();
                objectInputStream.close();
            } catch (ClassNotFoundException | IOException e) {
                logger.error("Error loading settings: {}", e);
            }
        }

        return settings;
    }

    public void saveSettings() {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(settingsFile));
            objectOutputStream.writeObject(settings);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            logger.error("Error saving settings: {}", e);
        }
    }

    public OperatingSystem getOperatingSystem() {
        return operatingSystem;
    }

    public String getProgramName() {
        return "Tailer Swift";
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setWatchedFiles(List<File> watchedFiles) {
        List<String> absoluteFilePaths = Lists.newArrayList();
        for (File file : watchedFiles) {
            absoluteFilePaths.add(file.getAbsolutePath());
        }
        this.settings.setAbsoluteFilePaths(absoluteFilePaths);
    }
}
