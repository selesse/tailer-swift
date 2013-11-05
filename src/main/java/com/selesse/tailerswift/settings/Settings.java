package com.selesse.tailerswift.settings;

import com.google.common.collect.Lists;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.List;

public class Settings implements Serializable {
    private boolean isAlwaysOnTop;
    private List<String> absoluteFilePaths;
    private Font displayFont;
    private int focusedFileIndex = -1;

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

    public void setDisplayFont(Font font) {
        this.displayFont = font;
    }

    /**
     * Returns the display font, or the default font (same as JLabels).
     */
    public Font getDisplayFont() {
        if (displayFont == null) {
            displayFont = UIManager.getDefaults().getFont("Label.font");
        }
        return displayFont;
    }

    public int getFocusedFileIndex() {
        return focusedFileIndex;
    }

    public void setFocusedFileIndex(int focusedFileIndex) {
        this.focusedFileIndex = focusedFileIndex;
    }
}
