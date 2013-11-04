package com.selesse.tailerswift.gui.highlighting;

import javax.swing.*;

public class HighlightView {
    private JPanel panel;

    public HighlightView() {
        this.panel = new JPanel();

        JLabel textFieldLabel = new JLabel("String to highlight");
        JTextField textField = new JTextField(30);
        JLabel foregroundColor = new JLabel("Foreground color");
        JColorChooser foregroundColorChooser = new JColorChooser();
        JLabel backgroundColor = new JLabel("Foreground color");
        JColorChooser backgroundColorChooser = new JColorChooser();

        panel.add(textFieldLabel);
        panel.add(textField);
        panel.add(foregroundColor);
        panel.add(foregroundColorChooser);
        panel.add(backgroundColor);
        panel.add(backgroundColorChooser);
    }

    public JComponent getComponent() {
        return panel;
    }
}
