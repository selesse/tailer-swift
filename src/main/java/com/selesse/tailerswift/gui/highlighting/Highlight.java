package com.selesse.tailerswift.gui.highlighting;

import com.selesse.tailerswift.gui.MainFrame;
import com.selesse.tailerswift.gui.section.FeatureContent;

import javax.swing.*;

public class Highlight implements FeatureContent {
    private HighlightView highlightView;
    private MainFrame mainFrame;

    public Highlight(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.highlightView = new HighlightView(this);
    }

    @Override
    public String getName() {
        return "Highlight";
    }

    @Override
    public JComponent getComponent() {
        return highlightView.getComponent();
    }

    public void addToHighlights(FileSetting fileSetting) {
        mainFrame.addHighlight(fileSetting);
    }
}
