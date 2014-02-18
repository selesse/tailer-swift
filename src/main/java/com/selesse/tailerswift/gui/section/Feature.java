package com.selesse.tailerswift.gui.section;

import javax.swing.*;
import java.awt.*;

public class Feature {
    private JComponent mainPanel;
    private boolean isVisible;

    public Feature(FeatureContent content) {
        this.mainPanel = new JPanel();
        this.mainPanel.add(content.getViewComponent());
     }

    public Component getComponent() {
        return mainPanel;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
        mainPanel.setVisible(isVisible);
    }

    public boolean getVisibility() {
        return isVisible;
    }
}
