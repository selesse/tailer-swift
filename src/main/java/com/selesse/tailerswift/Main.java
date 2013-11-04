package com.selesse.tailerswift;

import com.selesse.tailerswift.cli.CliTailerSwift;
import com.selesse.tailerswift.filewatcher.FileWatcher;
import com.selesse.tailerswift.gui.GuiTailerSwift;

public class Main {
    public static void main(String[] args) {
        Thread thread;

        // if there are args, command line version. otherwise, GUI
        if (args.length == 0) {
            thread = new Thread(new GuiTailerSwift());
        }
        else {
            thread = new Thread(new FileWatcher(new CliTailerSwift(), args[0]));
        }

        thread.start();
    }
}
