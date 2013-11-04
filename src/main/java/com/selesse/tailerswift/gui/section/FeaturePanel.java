package com.selesse.tailerswift.gui.section;

import javax.swing.*;

public class FeaturePanel extends JPanel {
    public void setFeature(Feature feature) {
        this.removeAll();
        this.add(feature.getComponent());
        this.revalidate();
        this.repaint();
    }
}
