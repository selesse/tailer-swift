package com.selesse.tailerswift;

import com.selesse.tailerswift.ui.GuiTailerSwift;

public class Main {
    public static void main(String[] args) {
        Thread thread;

        // if there are args, command line version. otherwise, GUI
        if (args.length == 0) {
            thread = new Thread(new GuiTailerSwift());
        }
        else {
            thread = new Thread(new TailerSwift(args));
        }

        thread.start();
    }
}
