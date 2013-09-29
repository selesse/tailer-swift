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
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(500, 500));
        JTextArea jTextArea = new JTextArea();
        jTextArea.setText("Hello, world!");
        JScrollPane jScrollPane = new JScrollPane(jTextArea);
        jScrollPane.setPreferredSize(new Dimension(500, 500));
        panel.add(jScrollPane);

        JTabbedPane jTabbedPane = new JTabbedPane();
        jTabbedPane.addTab("Default Tab", panel);

        mainFrame.add(jTabbedPane);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
}
