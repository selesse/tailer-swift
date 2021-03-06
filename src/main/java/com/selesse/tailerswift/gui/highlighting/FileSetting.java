package com.selesse.tailerswift.gui.highlighting;

public class FileSetting {
    private final String string;
    private final boolean isIgnoreCase;
    private final boolean contains;
    private final HighlightSettings highlightSettings;
    private final String associatedFile;

    // TODO make this static constructor builder pattern
    public FileSetting(String string, boolean isIgnoreCase, boolean contains, HighlightSettings highlightSettings,
                       String associatedFile) {
        this.string = string;
        this.isIgnoreCase = isIgnoreCase;
        this.contains = contains;
        this.highlightSettings = highlightSettings;
        this.associatedFile = associatedFile;
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

    public HighlightSettings getHighlightSettings() {
        return highlightSettings;
    }

    public boolean matchesHighlight(String matchString) {
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
        else if (isContains()) {
            return matchString.contains(string);
        }
        return matchString.equals(string);
    }

    public String getAssociatedFile() {
        return associatedFile;
    }
}
