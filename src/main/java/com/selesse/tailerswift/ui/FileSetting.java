package com.selesse.tailerswift.ui;

import java.awt.*;

public class FileSetting {
    private final String string;
    private final boolean isIgnoreCase;
    private final boolean contains;
    private final Color color;

    // TODO make this static constructor builder pattern
    public FileSetting(String string, boolean isIgnoreCase, boolean contains, Color color) {
        this.string = string;
        this.isIgnoreCase = isIgnoreCase;
        this.contains = contains;
        this.color = color;
    }

    public String getString() {
        return string;
    }

    public boolean isIgnoreCase() {
        return isIgnoreCase;
    }

    public boolean isContains() {
        return contains;
    }

    public Color getColor() {
        return color;
    }

    public boolean matches(String matchString) {
        if (matchString.trim().length() == 0) {
            return false;
        }
        // TODO this is stupid, redo later
        if (isIgnoreCase() && isContains()) {
            return matchString.toLowerCase().contains(string.toLowerCase());
        }
        else if (isIgnoreCase()) {
            return matchString.toLowerCase().equals(string.toLowerCase());
        }
        return matchString.equals(string);
    }
}
