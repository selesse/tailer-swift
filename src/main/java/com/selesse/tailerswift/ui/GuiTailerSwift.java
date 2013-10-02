package com.selesse.tailerswift.ui;

import com.selesse.tailerswift.ui.menu.FileMenu;
import com.selesse.tailerswift.ui.menu.HelpMenu;
import com.selesse.tailerswift.ui.menu.SettingsMenu;
import com.selesse.tailerswift.ui.menu.WindowMenu;

import javax.swing.*;
import java.awt.*;

public class GuiTailerSwift implements Runnable {
    private JFrame mainFrame;

    @Override
    public void run() {
        mainFrame = new JFrame("Tailer-Swift");
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();

        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
        mainFrame.setJMenuBar(createJMenuBar());

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

    private JMenuBar createJMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        FileMenu fileMenu = new FileMenu();
        WindowMenu windowMenu = new WindowMenu(mainFrame);
        SettingsMenu settingsMenu = new SettingsMenu();
        HelpMenu helpMenu = new HelpMenu();

        menuBar.add(fileMenu.getJMenu());
        menuBar.add(settingsMenu.getJMenu());
        menuBar.add(windowMenu.getJMenu());
        menuBar.add(helpMenu.getJMenu());
        return menuBar;
    }
}
