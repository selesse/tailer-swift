package com.selesse.tailerswift.ui;

import javax.swing.*;

public class GuiTailerSwift implements Runnable {
    private JFrame mainFrame;

    @Override
    public void run() {
        MainFrame mainFrame = new MainFrame();
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();

        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
