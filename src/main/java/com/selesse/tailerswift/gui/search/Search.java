package com.selesse.tailerswift.gui.search;

import com.selesse.tailerswift.gui.MainFrame;
import com.selesse.tailerswift.gui.section.FeatureContent;

import javax.swing.*;
import java.util.Observable;
import java.util.Observer;

public class Search implements FeatureContent, Observer {
    private SearchView searchView;
    private String name;
    private MainFrame mainFrame;

    public Search(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.searchView = new SearchView(this);
        this.name = "Search";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public JComponent getComponent() {
        return searchView.getComponent();
    }

    @Override
    public void update(Observable observable, Object o) {
    }

    public SearchResults searchFor(String text) {
        return mainFrame.searchFor(text);
    }
}
