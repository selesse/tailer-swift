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
            Thread thread = new Thread(new FileWatcher(new CliTailerSwift(), args[0]));
            thread.start();
        }
    }
}
