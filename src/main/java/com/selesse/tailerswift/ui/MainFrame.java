package com.selesse.tailerswift.ui;

import com.selesse.tailerswift.Program;
import com.selesse.tailerswift.ui.menu.FileMenu;
import com.selesse.tailerswift.ui.menu.HelpMenu;
import com.selesse.tailerswift.ui.menu.SettingsMenu;
import com.selesse.tailerswift.ui.menu.WindowMenu;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class MainFrame extends JFrame {

    private JTabbedPane jTabbedPane;
    private JMenuBar    jMenuBar;
    private Feature     searchFeature;
    private Feature     filterFeature;

    public MainFrame() {
        initializeGui();
    }

    private void initializeGui() {
        this.setTitle("Tailer Swift");
        this.setLayout(new BorderLayout());
        this.setBackground(null);

        //add tabbed pane
        jTabbedPane = new JTabbedPane();
        this.add(jTabbedPane, BorderLayout.CENTER);

        //add menu bar
        //jMenuBar = createJMenuBar();
        //this.add(jMenuBar, BorderLayout.NORTH);

        searchFeature = new Search();
        filterFeature = new Filter();

        JButton jButton;

        JPanel panel = new JPanel();


        jButton = searchFeature.getButton();
        jButton.setSize(new Dimension(40,20));
        this.add(jButton, BorderLayout.SOUTH);

        jButton = filterFeature.getButton();
        jButton.setSize(new Dimension(40,20));
        this.add(jButton, BorderLayout.SOUTH);

        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        pack();
        setLocationRelativeTo( null );
        setVisible( true );
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
}
