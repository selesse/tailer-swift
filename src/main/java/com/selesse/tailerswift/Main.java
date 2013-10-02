package com.selesse.tailerswift;

import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;
import com.selesse.tailerswift.ui.GuiTailerSwift;

public class Main {
    public static void main(String[] args) {
        Thread thread;
        if (args.length == 0) {
            // GUI version?
            if (Program.getInstance().getOperatingSystem() == OperatingSystem.MAC) {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
            }
            thread = new Thread(new GuiTailerSwift());
        }
        // command line version
        else {
            thread = new Thread(new TailerSwift(args));
        }

        thread.start();
    }
}
