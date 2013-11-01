package com.selesse.tailerswift.ui;

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
