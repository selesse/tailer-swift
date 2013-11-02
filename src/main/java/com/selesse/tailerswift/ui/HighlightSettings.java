package com.selesse.tailerswift.ui;

import java.awt.*;

public class HighlightSettings {
    private Color foregroundColor;
    private Color backgroundColor;
    private boolean isBold;
    private boolean isItalic;
    private boolean isUnderline;

    // TODO static constructor builder
    public HighlightSettings(Color foregroundColor, Color backgroundColor, boolean bold, boolean italic, boolean underline) {
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        isBold = bold;
        isItalic = italic;
        isUnderline = underline;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean bold) {
        isBold = bold;
    }

    public boolean isItalic() {
        return isItalic;
    }

    public void setItalic(boolean italic) {
        isItalic = italic;
    }

    public boolean isUnderline() {
        return isUnderline;
    }

    public void setUnderline(boolean underline) {
        isUnderline = underline;
    }
}
