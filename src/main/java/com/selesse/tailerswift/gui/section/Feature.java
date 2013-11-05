package com.selesse.tailerswift.gui.section;

import javax.swing.*;
import java.awt.*;

public class Feature {
    private FeatureContent content;
    private JComponent mainPanel;
    private boolean isVisible;

    public Feature(FeatureContent content) {
        this.content = content;
        this.mainPanel = new JPanel();
        this.mainPanel.add(content.getViewComponent());
        this.isVisible = false;
     }

    public void changeVisibility() {
        isVisible = !isVisible;
        mainPanel.setVisible(isVisible);
    }

    public Component getComponent() {
        return mainPanel;
    }
}
