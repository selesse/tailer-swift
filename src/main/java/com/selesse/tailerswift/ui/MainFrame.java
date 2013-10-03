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

        this.addTab("Thingy", new JLabel("thingyt"));
        //add menu bar
        //jMenuBar = createJMenuBar();

        searchFeature = new Search();
        filterFeature = new Filter();

        JButton jButton;
        JPanel jFeaturePanel = new JPanel();

        jButton = searchFeature.getButton();
        jButton.setPreferredSize(new Dimension(40, 20));
        jFeaturePanel.add(jButton);

        jButton = filterFeature.getButton();
        jButton.setPreferredSize(new Dimension(40, 20));
        jFeaturePanel.add(jButton);

        this.add(jFeaturePanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo( null );
        setVisible( true );
    }

    public void addTab(String title, Component content) {
        jTabbedPane.addTab(title, content);
    }
}
