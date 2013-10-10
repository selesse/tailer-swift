package com.selesse.tailerswift.ui;

import com.google.common.io.Resources;
import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;
import com.selesse.tailerswift.ui.menu.FileMenu;
import com.selesse.tailerswift.ui.menu.HelpMenu;
import com.selesse.tailerswift.ui.menu.SettingsMenu;
import com.selesse.tailerswift.ui.menu.WindowMenu;

import javax.swing.*;
import java.awt.*;

public class MainFrame implements Runnable {
    private JFrame jFrame;
    private JTabbedPane jTabbedPane;
    private JMenuBar jMenuBar;
    private JPanel jBottomPanel;
    private JPanel jFeatureButtonPanel;
    private JPanel jFeatureViewPanel;
    private Feature searchFeature;
    private Feature filterFeature;
    private JButton searchButton;
    private JButton filterButton;

    public MainFrame() {
        jFrame = new JFrame();
        // if we setIconImage in OS X, it throws some command line errors, so let's not try this on a Mac
        if (Program.getInstance().getOperatingSystem() != OperatingSystem.MAC) {
            jFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(Resources.getResource("icon.png")));
        }
    }

    private void initializeGui() {
        jFrame.setTitle(Program.getInstance().getProgramName());
        jFrame.setLayout(new BorderLayout());
        jFrame.setBackground(null);
        jFrame.setJMenuBar(createJMenuBar());
        jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // add tabbed pane
        jTabbedPane = new JTabbedPane();
        jFrame.add(jTabbedPane, BorderLayout.CENTER);

        // add bottom panel
        jBottomPanel = new JPanel();
        jBottomPanel.setLayout(new BorderLayout());
        jBottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        jFeatureViewPanel = new JPanel();
        jFeatureButtonPanel = new JPanel();

        jBottomPanel.add(jFeatureViewPanel, BorderLayout.NORTH);
        jBottomPanel.add(jFeatureButtonPanel, BorderLayout.SOUTH);

        jFrame.add(jBottomPanel, BorderLayout.SOUTH);

        // TODO load previous tabs, via Settings object

        //add features
        searchFeature = new Feature(new Search());
        filterFeature = new Feature(new Filter());

        //create buttons
        searchButton = new JButton("apples");
        filterButton = new JButton("pears");

        searchButton.addActionListener(new ButtonActionListener(jFeatureViewPanel, searchFeature));
        filterButton.addActionListener(new ButtonActionListener(jFeatureViewPanel, filterFeature));

        //add buttons
        jFeatureButtonPanel.add(searchButton);
        jFeatureButtonPanel.add(filterButton);

        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    public void addTab(String title, Component content) {
        jTabbedPane.addTab(title, content);
    }

    private JMenuBar createJMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        FileMenu fileMenu = new FileMenu(this);
        WindowMenu windowMenu = new WindowMenu(this);
        SettingsMenu settingsMenu = new SettingsMenu();
        HelpMenu helpMenu = new HelpMenu();

        menuBar.add(fileMenu.getJMenu());
        menuBar.add(settingsMenu.getJMenu());
        menuBar.add(windowMenu.getJMenu());
        menuBar.add(helpMenu.getJMenu());
        return menuBar;
    }

    public void toggleAlwaysOnTop() {
        boolean isAlwaysOnTop = Program.getInstance().getSettings().isAlwaysOnTop();
        jFrame.setAlwaysOnTop(!isAlwaysOnTop);
        Program.getInstance().getSettings().setAlwaysOnTop(!isAlwaysOnTop);
    }

    public void displayFeature() {
    }

    @Override
    public void run() {
        initializeGui();
    }
}
