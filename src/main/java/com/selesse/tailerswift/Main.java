package com.selesse.tailerswift;

import com.selesse.tailerswift.cli.CliTailerSwift;
import com.selesse.tailerswift.filewatcher.FileWatcher;
import com.selesse.tailerswift.gui.GuiTailerSwift;
import com.selesse.tailerswift.threads.WorkerThreads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // if there are args, command line version. otherwise, GUI
        if (args.length == 0) {
            new GuiTailerSwift();
        }
        else {
            CliTailerSwift cliTailerSwift = new CliTailerSwift(args.length);

            for (String filePath : args) {
                FileWatcher fileWatcher = new FileWatcher(cliTailerSwift, filePath);

                try {
                    WorkerThreads.execute(fileWatcher);
                }
                catch (InterruptedException e) {
                    LOGGER.error("Thread interrupted", e);
                }
            }
        }
    }
}
