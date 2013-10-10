package com.selesse.tailerswift.ui.displayoptions;

import javax.swing.*;
import java.awt.*;

public class DisplayPreferencesFrame {
    private JFrame jFrame;

    public DisplayPreferencesFrame() {
        jFrame = new JFrame("Display preferences");
        jFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        Dimension currentScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension preferredDimension = new Dimension( (int) currentScreenSize.getWidth() / 2, (int) currentScreenSize.getHeight() / 2);
        jFrame.setPreferredSize(preferredDimension);

        JLabel jLabel = new JLabel("Patrick please add details");
        jFrame.add(jLabel);

        jFrame.pack();
        jFrame.setVisible(true);
    }

    public void focus() {
        jFrame.toFront();
        jFrame.setVisible(true);
        jFrame.setLocationRelativeTo(null);
    }
}

