package com.selesse.tailerswift.ui;

import javax.swing.*;
import java.awt.*;

public class Feature {
    private FeatureContent content;
    private String featureName;
    private JComponent mainPanel;
    private boolean isVisible;

    public Feature(FeatureContent content) {
        this.content = content;
        this.mainPanel = new JPanel();
        this.featureName = this.content.getName();
        this.mainPanel.add(this.content.getComponent());
        this.isVisible = false;
     }

    public boolean isVisible() {
        return isVisible;
    }

    public void changeVisibility() {
        isVisible = !isVisible;
    }

    public Component getComponent() {
        return mainPanel;
    }
}
