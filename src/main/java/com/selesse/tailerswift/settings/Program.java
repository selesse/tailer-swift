package com.selesse.tailerswift.settings;

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
}
