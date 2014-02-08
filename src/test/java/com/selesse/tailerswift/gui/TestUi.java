package com.selesse.tailerswift.gui;

import com.selesse.tailerswift.UserInterface;
import com.selesse.tailerswift.cli.CliTailerSwift;

import java.nio.file.Path;

public class TestUi extends CliTailerSwift implements UserInterface {
    private StringBuilder stringBuilder;

    public TestUi(int numberOfTails) {
        super(numberOfTails);

        stringBuilder = new StringBuilder();
    }

    @Override
    public void updateFile(Path observedPath, String modificationString) {
        super.updateFile(observedPath, modificationString);

        stringBuilder.append(modificationString);
    }

    @Override
    public void newFile(Path observedPath, String modificationString) {
        super.newFile(observedPath, modificationString);

        stringBuilder = new StringBuilder(modificationString);
    }

    @Override
    public void deleteFile(Path observedPath) {
        super.deleteFile(observedPath);

        stringBuilder = new StringBuilder();
    }

    public String getBufferContents() {
        return stringBuilder.toString();
    }
}
