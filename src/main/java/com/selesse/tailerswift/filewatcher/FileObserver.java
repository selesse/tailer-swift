package com.selesse.tailerswift.filewatcher;

public interface FileObserver {
    String onModify();
    String onCreate();
    void onDelete();
}
