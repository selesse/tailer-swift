package com.selesse.tailerswift.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: epatcra
 * Date: 03/10/13
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ButtonActionListener implements ActionListener {

    JComponent displayArea;
    Feature feature;

    public ButtonActionListener(JComponent displayArea, Feature feature) {
        this.displayArea = displayArea;
        this.feature = feature;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
       if(feature.isVisible()) {
           //do something
           feature.changeVisibility();
       }   else {
           //do something
           feature.changeVisibility();
       }
    }
}
