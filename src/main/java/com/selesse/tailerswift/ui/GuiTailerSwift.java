package com.selesse.tailerswift.ui;

import javax.swing.*;
import java.awt.*;

public class GuiTailerSwift implements Runnable {
    @Override
    public void run() {
        JFrame mainFrame = new JFrame("Tailer-Swift");
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);

        JTabbedPane jTabbedPane = new JTabbedPane();
        JPanel panel = new JPanel();
        JTextField jTextField = new JTextField();
        jTextField.setText("Hello, world!");
        panel.add(jTextField);
        jTabbedPane.add(panel);

        mainFrame.add(jTabbedPane);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
}
