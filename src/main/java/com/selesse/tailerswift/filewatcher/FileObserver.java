package com.selesse.tailerswift.filewatcher;

public interface FileObserver {
    void onModify();
    void onCreate();
    void onDelete();
}
