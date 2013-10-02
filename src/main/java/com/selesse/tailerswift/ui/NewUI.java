package com.selesse.tailerswift.ui;

import javax.swing.*;

public class NewUI {
    private JPanel panel;

    public NewUI() {
        panel = new JPanel();
        JLabel label = new JLabel("Hello, world!");
        panel.add(label);
    }

    public JPanel getPanel() {
        return panel;
    }
}
