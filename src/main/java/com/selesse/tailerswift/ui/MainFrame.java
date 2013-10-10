package com.selesse.tailerswift.ui;

import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.selesse.tailerswift.filewatcher.FileWatcher;
import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;
import com.selesse.tailerswift.ui.menu.FileMenu;
import com.selesse.tailerswift.ui.menu.HelpMenu;
import com.selesse.tailerswift.ui.menu.SettingsMenu;
import com.selesse.tailerswift.ui.menu.WindowMenu;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.Map;

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
    private Map<String, JTextArea> fileTextAreaMap;

    public MainFrame() {
        jFrame = new JFrame();
        fileTextAreaMap = Maps.newHashMap();
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
        jTabbedPane.setSelectedComponent(content);
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

    public JFrame getJFrame() {
        return jFrame;
    }

    public void startWatching(File chosenFile) {
        JTextArea jTextArea = new JTextArea();
        jTextArea.setEditable(false);

        fileTextAreaMap.put(chosenFile.getAbsolutePath(), jTextArea);

        addTab(chosenFile.getName(), jTextArea);

        FileWatcher fileWatcher = new FileWatcher(new UserInterface() {
            @Override
            public void updateFile(Path observedFile, String modificationString) {
                JTextArea jTextArea = fileTextAreaMap.get(observedFile.toFile().getAbsolutePath());
                jTextArea.setText(jTextArea.getText() + modificationString);
            }

            @Override
            public void newFile(Path observedFile, String modificationString) {
                JTextArea jTextArea = fileTextAreaMap.get(observedFile.toFile().getAbsolutePath());
                jTextArea.setText(modificationString);
            }

            @Override
            public void deleteFile(Path observedFile) {
                JTextArea jTextArea = fileTextAreaMap.get(observedFile.toFile().getAbsolutePath());
                jTextArea.setText("");
            }
        }, chosenFile.getAbsolutePath());
        Thread fileWatcherThread = new Thread(fileWatcher);
        fileWatcherThread.start();
    }
}
