package com.selesse.tailerswift.gui.section;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonActionListener implements ActionListener {
    private FeaturePanel featurePanel;
    private Feature feature;

    public ButtonActionListener(FeaturePanel featurePanel, Feature feature) {
        this.featurePanel = featurePanel;
        this.feature = feature;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (featurePanel.getFeature() == feature && feature.getVisibility()) {
            feature.setVisible(false);
        }
        else {
            featurePanel.setFeature(feature);
            feature.setVisible(true);
        }
    }
}
