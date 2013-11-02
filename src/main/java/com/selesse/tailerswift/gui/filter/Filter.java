package com.selesse.tailerswift.gui.filter;

import com.selesse.tailerswift.gui.section.FeatureContent;

import javax.swing.*;
import java.util.Observable;
import java.util.Observer;

public class Filter implements FeatureContent, Observer {
    private JComponent mainComponent;
    private String name;

    public Filter() {
        name = "Filter";
        mainComponent = new JLabel("filter feature");
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
