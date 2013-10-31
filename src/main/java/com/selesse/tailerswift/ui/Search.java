package com.selesse.tailerswift.ui;

import javax.swing.*;
import java.util.Observable;
import java.util.Observer;

public class Search implements FeatureContent, Observer {
    private JComponent mainComponent;
    private String name;

    public Search() {
        name = "Search";
        mainComponent = new JLabel("search feature");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public JComponent getComponent() {
        return mainComponent;
    }

    @Override
    public void update(Observable observable, Object o) {
    }
}
