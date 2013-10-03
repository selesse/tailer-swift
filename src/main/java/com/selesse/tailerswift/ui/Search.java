package com.selesse.tailerswift.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 *
 */
public class Search implements FeatureContent, Observer {

    JComponent mainComponent;
    String name;

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
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
