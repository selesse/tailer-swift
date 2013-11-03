package com.selesse.tailerswift.gui.view;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.selesse.tailerswift.UserInterface;
import com.selesse.tailerswift.filewatcher.FileWatcher;
import com.selesse.tailerswift.gui.MainFrame;
import com.selesse.tailerswift.gui.SmartScroller;
import com.selesse.tailerswift.gui.filter.Filter;
import com.selesse.tailerswift.gui.highlighting.Colors;
import com.selesse.tailerswift.gui.highlighting.Feature;
import com.selesse.tailerswift.gui.highlighting.Highlight;
import com.selesse.tailerswift.gui.highlighting.HighlightingThread;
import com.selesse.tailerswift.gui.menu.FileMenu;
import com.selesse.tailerswift.gui.menu.HelpMenu;
import com.selesse.tailerswift.gui.menu.SettingsMenu;
import com.selesse.tailerswift.gui.menu.WindowMenu;
import com.selesse.tailerswift.gui.search.Search;
import com.selesse.tailerswift.gui.section.ButtonActionListener;
import com.selesse.tailerswift.gui.section.FeaturePanel;
import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * All the nitty-gritty GUI-related functions for the {@link MainFrame}.
 */
public class MainFrameView {
    private JFrame frame;
    private JTabbedPane tabbedPane;
    private JLabel absoluteFilePathLabel;
    private Map<String, JTextComponent> stringTextComponentMap;
    private List<String> watchedFileNames;
    private MainFrame mainFrame;

    public MainFrameView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        frame = new JFrame();
        stringTextComponentMap = Maps.newHashMap();
        watchedFileNames = Lists.newArrayList();

        // if we setIconImage in OS X, it throws some command line errors, so let's not try this on a Mac
        if (Program.getInstance().getOperatingSystem() != OperatingSystem.MAC) {
            frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Resources.getResource("icon.png")));
        }
    }

    public void initializeGui() {
        frame.setTitle(Program.getInstance().getProgramName());
        frame.setLayout(new BorderLayout());
        frame.setBackground(null);
        frame.setJMenuBar(createMenuBar());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDropTarget(mainFrame.createFileDropTarget());

        // add tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                int selectedIndex = tabbedPane.getSelectedIndex();
                try {
                    absoluteFilePathLabel.setText(watchedFileNames.get(selectedIndex));
                }
                // this will only be an issue on startup
                catch (IndexOutOfBoundsException e) {
                    absoluteFilePathLabel.setText("");
                }
            }
        });
        frame.add(tabbedPane, BorderLayout.CENTER);

        // add bottom panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        FeaturePanel featurePanel = new FeaturePanel();
        JPanel featureButtonPanel = new JPanel();

        absoluteFilePathLabel = new JLabel();
        absoluteFilePathLabel.setForeground(Colors.DARK_GREEN.toColor());
        // pad a bit on the right so it looks prettier
        absoluteFilePathLabel.setBorder(new EmptyBorder(0, 0, 0, 10));

        bottomPanel.add(featurePanel, BorderLayout.NORTH);
        bottomPanel.add(absoluteFilePathLabel, BorderLayout.EAST);
        bottomPanel.add(featureButtonPanel, BorderLayout.SOUTH);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        // add features
        Feature searchFeature = new Feature(new Search());
        Feature filterFeature = new Feature(new Filter());
        Feature highlightFeature = new Feature(new Highlight());

        // create buttons
        JButton searchButton = new JButton("Search");
        JButton filterButton = new JButton("Filter");
        JButton highlightButton = new JButton("Highlight");

        searchButton.addActionListener(new ButtonActionListener(featurePanel, searchFeature));
        filterButton.addActionListener(new ButtonActionListener(featurePanel, filterFeature));
        highlightButton.addActionListener(new ButtonActionListener(featurePanel, highlightFeature));

        // add buttons
        featureButtonPanel.add(searchButton);
        featureButtonPanel.add(filterButton);
        featureButtonPanel.add(highlightButton);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /****************************************************************************************************************
     *  Component creation section.                                                                                 *
     ****************************************************************************************************************
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        FileMenu fileMenu = new FileMenu(mainFrame);
        WindowMenu windowMenu = new WindowMenu(mainFrame);
        SettingsMenu settingsMenu = new SettingsMenu();
        HelpMenu helpMenu = new HelpMenu();

        menuBar.add(fileMenu.getMenu());
        menuBar.add(settingsMenu.getMenu());
        menuBar.add(windowMenu.getMenu());
        menuBar.add(helpMenu.getMenu());

        return menuBar;
    }

    private JTextComponent createWatcherTextComponent() {
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setDropTarget(mainFrame.createFileDropTarget());

        return textPane;
    }

    /****************************************************************************************************************
     *  Getters and setters.                                                                                        *
     ****************************************************************************************************************
     */
    public Frame getFrame() {
        return frame;
    }

    /****************************************************************************************************************
     *  View functionality.                                                                                         *
     ****************************************************************************************************************
     */
    public void toggleAlwaysOnTop() {
        frame.setAlwaysOnTop(!frame.isAlwaysOnTop());
    }

    public void setAlwaysOnTop(boolean alwaysOnTop) {
        frame.setAlwaysOnTop(alwaysOnTop);
    }

    public void addTab(File file) {
        JTextComponent textComponent = createWatcherTextComponent();

        stringTextComponentMap.put(file.getAbsolutePath(), textComponent);
        watchedFileNames.add(file.getAbsolutePath());

        JScrollPane scrollPane = new JScrollPane(textComponent);
        tabbedPane.addTab(file.getName(), scrollPane);
        tabbedPane.setSelectedComponent(scrollPane);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new SmartScroller(scrollPane));

        absoluteFilePathLabel.setText(file.getAbsolutePath());
    }

    public void closeCurrentTab() {
        int currentlyFocusedFileIndex = tabbedPane.getSelectedIndex();
        if (currentlyFocusedFileIndex != -1) {
            tabbedPane.remove(currentlyFocusedFileIndex);
            String removeName = watchedFileNames.remove(currentlyFocusedFileIndex);
            stringTextComponentMap.remove(removeName);
            if (watchedFileNames.isEmpty()) {
                absoluteFilePathLabel.setText("");
            }
            else {
                absoluteFilePathLabel.setText(watchedFileNames.get(tabbedPane.getSelectedIndex()));
            }
        }
    }

    public void focusTabToAlreadyOpen(File chosenFile) {
        int fileIndex = watchedFileNames.indexOf(chosenFile.getAbsolutePath());
        if (fileIndex != -1) {
            tabbedPane.setSelectedIndex(fileIndex);
        }
        else {
            System.err.println("Error, got -1 file index for " + chosenFile.getAbsolutePath());
        }
    }

    public String getFocusedTabName() {
        if (watchedFileNames.isEmpty()) {
            return null;
        }
        return watchedFileNames.get(tabbedPane.getSelectedIndex());
    }

    public FileWatcher createFileWatcherFor(File chosenFile) {
        return new FileWatcher(new UserInterface() {
            private StringBuilder stringBuilder = new StringBuilder();

            @Override
            public void updateFile(Path observedPath, String modificationString) {
                String absolutePath = observedPath.toFile().getAbsolutePath();

                JTextComponent textComponent = stringTextComponentMap.get(absolutePath);
                stringBuilder.append(modificationString);
                textComponent.setText(stringBuilder.toString());

                Thread highlightingThread = new Thread(new HighlightingThread(stringBuilder, textComponent));
                highlightingThread.start();
            }

            @Override
            public void newFile(Path observedPath, String modificationString) {
                String absolutePath = observedPath.toFile().getAbsolutePath();

                JTextComponent textComponent = stringTextComponentMap.get(absolutePath);
                stringBuilder = new StringBuilder();
                stringBuilder.append(modificationString);
                textComponent.setText(stringBuilder.toString());
            }

            @Override
            public void deleteFile(Path observedPath) {
                String absolutePath = observedPath.toFile().getAbsolutePath();

                JTextComponent textComponent = stringTextComponentMap.get(absolutePath);
                stringBuilder = new StringBuilder();
                textComponent.setText(stringBuilder.toString());
            }
        }, chosenFile.getAbsolutePath());
    }
}