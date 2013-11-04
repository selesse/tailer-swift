package com.selesse.tailerswift.gui.highlighting;

import com.selesse.tailerswift.gui.section.FeatureContent;

import javax.swing.*;

public class Highlight implements FeatureContent {
    private HighlightView highlightView;

    public Highlight() {
        this.highlightView = new HighlightView();
    }

    @Override
    public String getName() {
        return "Highlight";
    }

    @Override
    public JComponent getComponent() {
        return highlightView.getComponent();
    }
}
