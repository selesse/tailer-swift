package com.selesse.tailerswift.gui.section;

import javax.swing.*;

public class FeaturePanel extends JPanel {
    private Feature feature;

    public void setFeature(Feature feature) {
        this.feature = feature;
        this.removeAll();
        this.add(feature.getComponent());
        this.revalidate();
        this.repaint();
    }

    public Feature getFeature() {
        return feature;
    }
}
