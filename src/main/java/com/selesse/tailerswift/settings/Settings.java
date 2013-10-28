package com.selesse.tailerswift.settings;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class Settings implements Serializable {
    private boolean isAlwaysOnTop;
    private List<String> absoluteFilePaths;

    public Settings() {
        this.isAlwaysOnTop = false;
        absoluteFilePaths = Lists.newArrayList();
    }

    public boolean isAlwaysOnTop() {
        return isAlwaysOnTop;
    }

    public void setAlwaysOnTop(boolean alwaysOnTop) {
        isAlwaysOnTop = alwaysOnTop;
    }

    public List<String> getAbsoluteFilePaths() {
        return absoluteFilePaths;
    }

    public void setAbsoluteFilePaths(List<String> absoluteFilePaths) {
        this.absoluteFilePaths = absoluteFilePaths;
    }
}
