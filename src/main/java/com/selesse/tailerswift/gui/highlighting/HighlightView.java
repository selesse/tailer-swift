package com.selesse.tailerswift.gui.highlighting;

import com.selesse.tailerswift.gui.view.FeatureView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HighlightView extends FeatureView {
    public HighlightView(final Highlight highlight) {
        this.panel = new JPanel();

        JLabel textFieldLabel = new JLabel("String to highlight");
        final JTextField textField = new JTextField(30);
        JButton foregroundButton = new JButton("Foreground color");
        JButton backgroundButton = new JButton("Background color");

        panel.add(textFieldLabel);
        panel.add(textField);
        panel.add(foregroundButton);
        panel.add(backgroundButton);

        final JColorChooser foregroundColorChooser = new JColorChooser();
        final JColorChooser backgroundColorChooser = new JColorChooser();
        foregroundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = JColorChooser.createDialog(panel, "Pick a foreground color", true, foregroundColorChooser,
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                               // on "OK"
                            }
                        }, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // on "Cancel"
                            }
                        });
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            }
        });
        backgroundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = JColorChooser.createDialog(panel, "Pick a background color", true, backgroundColorChooser,
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // on "OK"
                            }
                        }, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                // on "Cancel"
                            }
                        }
                );
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            }
        });

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color foregroundColor = foregroundColorChooser.getColor();
                Color backgroundColor = backgroundColorChooser.getColor();
                String highlightString = textField.getText();

                FileSetting fileSetting = new FileSetting(highlightString, false, true, new HighlightSettings(foregroundColor, backgroundColor, false, false, false));
                highlight.addToHighlights(fileSetting);
            }
        });
    }
}
