package com.selesse.tailerswift;

import java.io.Serializable;

public class Settings implements Serializable {
    private boolean isAlwaysOnTop;

    public boolean isAlwaysOnTop() {
        return isAlwaysOnTop;
    }

    public void setAlwaysOnTop(boolean alwaysOnTop) {
        isAlwaysOnTop = alwaysOnTop;
    }
}
