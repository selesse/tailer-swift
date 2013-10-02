package com.selesse.tailerswift.ui;

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

        JPanel panel = new Panel();


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
}
