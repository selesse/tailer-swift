package com.selesse.tailerswift.filewatcher;

public interface FileObserver {
    String onModify();
    void onCreate();
    void onDelete();
}
