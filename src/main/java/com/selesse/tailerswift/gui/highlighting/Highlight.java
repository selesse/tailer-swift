package com.selesse.tailerswift.gui.highlighting;

import com.selesse.tailerswift.gui.MainFrame;
import com.selesse.tailerswift.gui.section.AbstractFeature;

import javax.swing.*;

public class Highlight extends AbstractFeature {
    private HighlightView highlightView;

    public Highlight(MainFrame mainFrame) {
        super("Highlight", mainFrame);
        this.highlightView = new HighlightView(this);
    }

    @Override
    public JComponent getViewComponent() {
        return highlightView.getComponent();
    }

    public void addToHighlights(FileSetting fileSetting) {
        mainFrame.addHighlight(fileSetting);
    }
}
