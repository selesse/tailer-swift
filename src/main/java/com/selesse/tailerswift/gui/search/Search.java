package com.selesse.tailerswift.gui.search;

import com.selesse.tailerswift.gui.MainFrame;
import com.selesse.tailerswift.gui.section.AbstractFeature;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class Search extends AbstractFeature implements Observer {
    private SearchView searchView;

    public Search(MainFrame mainFrame) {
        super("Search", mainFrame);
        this.searchView = new SearchView(this);
    }

    @Override
    public void update(Observable observable, Object o) {
    }

    public SearchResults searchFor(String text) {
        return mainFrame.searchFor(text);
    }

    @Override
    public Component getViewComponent() {
        return searchView.getComponent();
    }
}
