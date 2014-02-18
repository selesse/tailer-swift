package com.selesse.tailerswift;

import java.io.File;

public class TestUtil {
    public static void deleteInOrder(File... files) {
        for (File file : files) {
            if (!file.delete()) {
                System.err.println("Failed to delete " + file);
            }
        }
    }
}
