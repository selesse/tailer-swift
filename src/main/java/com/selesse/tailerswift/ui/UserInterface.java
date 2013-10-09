package com.selesse.tailerswift.ui;

import java.nio.file.Path;

public interface UserInterface {
    void updateFile(Path observedFile, String modificationString);
    void newFile(Path observedFile, String modificationString);
    void deleteFile(Path observedFile);
}
