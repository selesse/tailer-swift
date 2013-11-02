package com.selesse.tailerswift.gui.highlighting;

import com.selesse.tailerswift.gui.section.FeatureContent;

import javax.swing.*;

public class Highlight implements FeatureContent {
    @Override
    public String getName() {
        return "Highlight";
    }

    @Override
    public JComponent getComponent() {
        return new JLabel("Highlight");
    }
}
