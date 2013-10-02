package com.selesse.tailerswift.ui;

import javax.swing.*;

/**
 *
 */
public class Search implements Feature {

    private JButton jButton;
    private boolean isVisible;

    public Search() {

        jButton = new JButton("Search");

    }

    @Override
    public JButton getButton() {
        return jButton;  //To change body of implemented methods use File | Settings | File Templates.
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
