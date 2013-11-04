package com.selesse.tailerswift.cli;

import com.selesse.tailerswift.UserInterface;

import java.nio.file.Path;
import java.util.Date;

public class CliTailerSwift implements UserInterface {
    @Override
    public void updateFile(Path observedFile, String modificationString) {
        System.out.print(modificationString);
    }

    @Override
    public void newFile(Path observedFile, String modificationString) {
        System.out.print(modificationString);
    }

    @Override
    public void deleteFile(Path observedFile) {
        System.out.println("***** " + observedFile.getFileName() + " was deleted on " + new Date() + " *****");
    }
}
