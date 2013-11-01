package com.selesse.tailerswift.ui;

import java.awt.*;

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
