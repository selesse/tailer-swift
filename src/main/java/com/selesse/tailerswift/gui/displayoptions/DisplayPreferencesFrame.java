package com.selesse.tailerswift.gui.displayoptions;

import com.selesse.tailerswift.gui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DisplayPreferencesFrame {
    private JFrame frame;

    public DisplayPreferencesFrame(final MainFrame mainFrame) {
        frame = new JFrame("Display preferences");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        Dimension currentScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension preferredDimension = new Dimension( (int) currentScreenSize.getWidth() / 2, (int) currentScreenSize.getHeight() / 2);
        frame.setPreferredSize(preferredDimension);

        JButton button = new JButton("Modify font");
        frame.add(button);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFontChooser fontChooser = new JFontChooser();
                int result = fontChooser.showDialog(frame);
                if (result == JFontChooser.OK_OPTION) {
                    mainFrame.setFont(fontChooser.getSelectedFont());
                }
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    public void focus() {
        frame.toFront();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}

