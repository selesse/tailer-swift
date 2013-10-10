package com.selesse.tailerswift.ui;

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
import java.nio.file.Path;

public class MainFrame extends JFrame {

    private JTabbedPane jTabbedPane;
    private JMenuBar    jMenuBar;
    private JPanel      jBottomPanel;

    private JPanel      jFeatureButtonPanel;
    private JPanel      jFeatureViewPanel;

    private Feature     searchFeature;
    private Feature     filterFeature;

    private JButton     searchButton;
    private JButton     filterButton;
    private JTextArea textArea; // ccccombo-breaker

    public MainFrame() {
        initializeGui();
    }

    private void initializeGui() {
        this.setTitle(Program.getInstance().getProgramName());
        this.setLayout(new BorderLayout());
        this.setBackground(null);
        this.setJMenuBar(createJMenuBar());
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        //add tabbed pane
        jTabbedPane = new JTabbedPane();
        this.add(jTabbedPane, BorderLayout.CENTER);

        //tab added for testing purposes
        this.addTab("Thingy", new JLabel("Thingy"));
        textArea = new JTextArea();
        textArea.setEditable(false);
        this.addTab("Thingy2", textArea);

        // if we setIconImage in OS X, it throws some command line errors, so let's not try this on a Mac
        if (Program.getInstance().getOperatingSystem() != OperatingSystem.MAC) {
            this.setIconImage(Toolkit.getDefaultToolkit().getImage(Resources.getResource("icon.png")));
        }

        jTabbedPane.setSelectedIndex(1); // deal with it!

        //add bottom panel
        jBottomPanel = new JPanel();
        jBottomPanel.setLayout(new BorderLayout());
        jBottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        jFeatureViewPanel = new JPanel();
        jFeatureButtonPanel = new JPanel();

        jBottomPanel.add(jFeatureViewPanel, BorderLayout.NORTH);
        jBottomPanel.add(jFeatureButtonPanel, BorderLayout.SOUTH);

        this.add(jBottomPanel, BorderLayout.SOUTH);

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

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        FileWatcher fileWatcher = new FileWatcher(new UserInterface() {
            @Override
            public void updateFile(Path observedFile, String modificationString) {
                textArea.setText(textArea.getText() + modificationString);
            }

            @Override
            public void newFile(Path observedFile, String modificationString) {
                textArea.setText(modificationString);
            }

            @Override
            public void deleteFile(Path observedFile) {
                textArea.setText("");
            }
        }, "./a.txt");
        Thread thread = new Thread(fileWatcher);
        thread.start();
    }

    public void addTab(String title, Component content) {
        jTabbedPane.addTab(title, content);
    }

    private JMenuBar createJMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        FileMenu fileMenu = new FileMenu();
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
        this.setAlwaysOnTop(!isAlwaysOnTop);
        Program.getInstance().getSettings().setAlwaysOnTop(!isAlwaysOnTop);
    }

    public void displayFeature() {
    }
}
