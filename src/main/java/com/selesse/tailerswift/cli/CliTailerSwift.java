package com.selesse.tailerswift.cli;

import com.selesse.tailerswift.UserInterface;

import java.nio.file.Path;
import java.util.Date;

public class CliTailerSwift implements UserInterface {
    /**
     * The number of tails we'll be following. This is important for the UI to know because if
     * there's more than 1, we should be printing out the filename as well.
     */
    private int numberOfTails = 1;

    public CliTailerSwift(int numberOfTails) {
        this.numberOfTails = numberOfTails;
    }

    @Override
    public void updateFile(Path observedPath, String modificationString) {
        if (numberOfTails > 1) {
            printMessage(observedPath.toAbsolutePath());
        }
        System.out.print(modificationString);
    }

    @Override
    public void newFile(Path observedPath, String modificationString) {
        if (numberOfTails > 1) {
            printMessage(observedPath.toAbsolutePath());
        }
        System.out.print(modificationString);
    }

    @Override
    public void deleteFile(Path observedPath) {
        printMessage(observedPath.toAbsolutePath() + " was deleted on " + new Date());
    }

    private void printMessage(Object o) {
        System.out.println(String.format("*** %s ***", o.toString()));
    }
}
