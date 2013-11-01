package com.selesse.tailerswift.settings;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Set;

public class Program {
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

        settings = new Settings(); // TODO load settings?
    }

    public OperatingSystem getOperatingSystem() {
        return operatingSystem;
    }

    public String getProgramName() {
        return "Tailer Swift";
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public void setWatchedFiles(Set<String> watchedFiles) {
        List<String> absoluteFilePaths = Lists.newArrayList();
        absoluteFilePaths.addAll(watchedFiles);
        this.settings.setAbsoluteFilePaths(absoluteFilePaths);
    }
}
