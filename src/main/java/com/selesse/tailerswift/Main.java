package com.selesse.tailerswift;

import com.selesse.tailerswift.cli.CliTailerSwift;
import com.selesse.tailerswift.filewatcher.FileWatcher;
import com.selesse.tailerswift.gui.GuiTailerSwift;

public class Main {
    public static void main(String[] args) {
        // if there are args, command line version. otherwise, GUI
        if (args.length == 0) {
            new GuiTailerSwift();
        }
        else {
            CliTailerSwift cliTailerSwift = new CliTailerSwift(args.length);

            for (String filePath : args) {
                FileWatcher fileWatcher = new FileWatcher(cliTailerSwift, filePath);
                Thread fileWatcherThread = new Thread(fileWatcher);

                fileWatcherThread.start();
            }
        }
    }
}
