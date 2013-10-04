package com.selesse.tailerswift.ui;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 02/10/13
 * Time: 7:20 PM
 */
public class Filter implements Feature {

    JButton jButton;
    boolean isVisible;

    public Filter() {

        jButton = new JButton("Filter");

    }

    @Override
    public JButton getButton() {
        return jButton;
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }
}
