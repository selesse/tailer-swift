package com.selesse.tailerswift.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 02/10/13
 * Time: 7:20 PM
 */
public class Filter implements FeatureContent, Observer {

    JComponent mainComponent;
    String name;

    public Filter() {

        name = "Filter";
        mainComponent = new JLabel("filter feature");
    }

    @Override
    public String getName() {
        return name;  //To change body of implemented methods use File | Settings | File Templates.
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
