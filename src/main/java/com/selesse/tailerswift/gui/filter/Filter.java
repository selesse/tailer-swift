package com.selesse.tailerswift.gui.filter;

import com.selesse.tailerswift.gui.MainFrame;
import com.selesse.tailerswift.gui.section.AbstractFeature;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class Filter extends AbstractFeature implements Observer {
    private FilterView filterView;

    public Filter(MainFrame mainFrame) {
        super("Filter", mainFrame);
        this.filterView = new FilterView(this);
    }

    @Override
    public void update(Observable observable, Object o) {
    }

    @Override
    public Component getViewComponent() {
        return filterView.getComponent();
    }

    public FilterResults filter(String text) {
        return mainFrame.filter(text);
    }
}
