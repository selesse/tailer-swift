package com.selesse.tailerswift;

import java.nio.file.Path;

public interface TailUserInterface {
    void updateFile(Path observedPath, String modificationString);
    void newFile(Path observedPath, String modificationString);
    void deleteFile(Path observedPath);
}
