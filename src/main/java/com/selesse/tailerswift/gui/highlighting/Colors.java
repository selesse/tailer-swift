package com.selesse.tailerswift.gui.highlighting;

import java.awt.*;

/**
 * Custom list of non-standard {@link Color}s.
 */
public enum Colors {
    DARK_GREEN(0, 100, 0);

    private Color color;

    private Colors(int red, int green, int blue) {
        this.color = new Color(red, green, blue);
    }

    public Color toColor() {
        return color;
    }
}
