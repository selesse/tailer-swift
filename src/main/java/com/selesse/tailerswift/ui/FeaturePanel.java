package com.selesse.tailerswift.ui;

import javax.swing.*;

public class FeaturePanel extends JPanel {
    public void setFeature(Feature feature) {
        this.removeAll();
        this.add(feature.getComponent());
        this.revalidate();
        this.repaint();
    }
}
