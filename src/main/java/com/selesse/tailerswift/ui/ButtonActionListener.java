package com.selesse.tailerswift.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonActionListener implements ActionListener {
    private JComponent displayArea;
    private Feature feature;

    public ButtonActionListener(JComponent displayArea, Feature feature) {
        this.displayArea = displayArea;
        this.feature = feature;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (feature.isVisible()) {
            //do something
            feature.changeVisibility();
        } else {
            //do something
            feature.changeVisibility();
        }
    }
}
