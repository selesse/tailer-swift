package com.selesse.tailerswift.ui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.selesse.tailerswift.filewatcher.FileWatcher;
import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;
import com.selesse.tailerswift.settings.Settings;
import com.selesse.tailerswift.ui.menu.FileMenu;
import com.selesse.tailerswift.ui.menu.HelpMenu;
import com.selesse.tailerswift.ui.menu.SettingsMenu;
import com.selesse.tailerswift.ui.menu.WindowMenu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.*;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MainFrame implements Runnable {
    private JFrame jFrame;
    private JTabbedPane jTabbedPane;
    private JPanel jBottomPanel;
    private JPanel jFeatureButtonPanel;
    private FeaturePanel jFeatureViewPanel;
    private Feature searchFeature;
    private Feature filterFeature;
    private JButton searchButton;
    private JButton filterButton;
    private JLabel absoluteFilePathLabel;
    private Map<String, JTextArea> fileTextAreaMap;
    private Map<String, Thread> fileThreadMap;
    private List<File> watchedFiles;

    public MainFrame() {
        jFrame = new JFrame();
        fileTextAreaMap = Maps.newHashMap();
        fileThreadMap = Maps.newHashMap();
        watchedFiles = Lists.newArrayList();
        // if we setIconImage in OS X, it throws some command line errors, so let's not try this on a Mac
        if (Program.getInstance().getOperatingSystem() != OperatingSystem.MAC) {
            jFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(Resources.getResource("icon.png")));
        }

        jFrame.setDropTarget(createFileDropTarget());
    }

    @Override
    public void run() {
        initializeGui();
    }

    private void initializeGui() {
        jFrame.setTitle(Program.getInstance().getProgramName());
        jFrame.setLayout(new BorderLayout());
        jFrame.setBackground(null);
        jFrame.setJMenuBar(createJMenuBar());
        jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // add tabbed pane
        jTabbedPane = new JTabbedPane();
        jTabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                int selectedIndex = jTabbedPane.getSelectedIndex();
                try {
                    absoluteFilePathLabel.setText(watchedFiles.get(selectedIndex).getAbsolutePath());
                }
                // this will only be an issue on startup
                catch (IndexOutOfBoundsException e) {
                    absoluteFilePathLabel.setText("");
                }
            }
        });
        jFrame.add(jTabbedPane, BorderLayout.CENTER);

        // add bottom panel
        jBottomPanel = new JPanel();
        jBottomPanel.setLayout(new BorderLayout());
        jBottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        jFeatureViewPanel = new FeaturePanel();
        jFeatureButtonPanel = new JPanel();

        absoluteFilePathLabel = new JLabel();
        absoluteFilePathLabel.setForeground(Colors.DARK_GREEN.toColor());
        // pad a bit on the right so it looks prettier
        absoluteFilePathLabel.setBorder(new EmptyBorder(0, 0, 0, 10));

        jBottomPanel.add(jFeatureViewPanel, BorderLayout.NORTH);
        jBottomPanel.add(absoluteFilePathLabel, BorderLayout.EAST);
        jBottomPanel.add(jFeatureButtonPanel, BorderLayout.SOUTH);

        jFrame.add(jBottomPanel, BorderLayout.SOUTH);

        loadSettings();

        // add features
        searchFeature = new Feature(new Search());
        filterFeature = new Feature(new Filter());

        // create buttons
        searchButton = new JButton("Search");
        filterButton = new JButton("Filter");

        searchButton.addActionListener(new ButtonActionListener(jFeatureViewPanel, searchFeature));
        filterButton.addActionListener(new ButtonActionListener(jFeatureViewPanel, filterFeature));

        // add buttons
        jFeatureButtonPanel.add(searchButton);
        jFeatureButtonPanel.add(filterButton);

        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    // if we ever drag and drop a file into the GUI, start watching the file
    private DropTarget createFileDropTarget() {
        return new DropTarget() {

            @SuppressWarnings("unchecked")
            public synchronized void drop(DropTargetDropEvent event) {
                event.acceptDrop(DnDConstants.ACTION_LINK);
                try {
                    List<File> droppedFiles = (List<File>) event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File file : droppedFiles) {
                        startWatching(file);
                    }
                } catch (Exception e) {
                    // if we get any sort of exception, it's no big deal...
                }
            }
        };
    }

    private void loadSettings() {
        Settings settings = Program.getInstance().getSettings();
        jFrame.setAlwaysOnTop(settings.isAlwaysOnTop());
        for (String filePath : settings.getAbsoluteFilePaths()) {
            File file = new File(filePath);
            startWatching(file);
        }
    }

    public void addTab(File file, JTextArea textArea) {
        JScrollPane scrollPane = new JScrollPane(textArea);
        jTabbedPane.addTab(file.getName(), scrollPane);
        jTabbedPane.setSelectedComponent(scrollPane);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new SmartScroller(scrollPane));

        absoluteFilePathLabel.setText(file.getAbsolutePath());
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

    public JFrame getJFrame() {
        return jFrame;
    }

    public void startWatching(File chosenFile) {
        if (fileTextAreaMap.containsKey(chosenFile.getAbsolutePath())) {
            focusTabToAlreadyOpen(chosenFile);
            return;
        }
        JTextArea textArea = createWatcherTextArea();

        addTab(chosenFile, textArea);

        Thread fileWatcherThread = new Thread(createFileWatcherFor(chosenFile));
        fileWatcherThread.start();

        fileTextAreaMap.put(chosenFile.getAbsolutePath(), textArea);
        fileThreadMap.put(chosenFile.getAbsolutePath(), fileWatcherThread);
        watchedFiles.add(chosenFile);

        updateSettings();
    }

    private void focusTabToAlreadyOpen(File focusFile) {
        for (int i = 0; i < watchedFiles.size(); i++) {
            File file = watchedFiles.get(i);
            if (file.getAbsolutePath().equals(focusFile.getAbsolutePath())) {
                jTabbedPane.setSelectedIndex(i);
                break;
            }
        }
    }

    private FileWatcher createFileWatcherFor(final File chosenFile) {
        return new FileWatcher(new UserInterface() {
            private StringBuilder stringBuilder = new StringBuilder();

            @Override
            public void updateFile(Path observedPath, String modificationString) {
                JTextArea textArea = fileTextAreaMap.get(observedPath.toFile().getAbsolutePath());
                stringBuilder.append(modificationString);
                textArea.setText(stringBuilder.toString());

                Thread highlightingThread = new Thread(new HighlightingThread(stringBuilder, textArea));
                highlightingThread.start();
            }

            @Override
            public void newFile(Path observedPath, String modificationString) {
                JTextArea textArea = fileTextAreaMap.get(observedPath.toFile().getAbsolutePath());
                stringBuilder = new StringBuilder();
                stringBuilder.append(modificationString);
                textArea.setText(stringBuilder.toString());
            }

            @Override
            public void deleteFile(Path observedPath) {
                JTextArea textArea = fileTextAreaMap.get(observedPath.toFile().getAbsolutePath());
                stringBuilder = new StringBuilder();
                textArea.setText(stringBuilder.toString());
            }
        }, chosenFile.getAbsolutePath());
    }

    private JTextArea createWatcherTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setDropTarget(createFileDropTarget());

        return textArea;
    }

    public Collection<Thread> getAllThreads() {
        return fileThreadMap.values();
    }

    public void closeCurrentTab() {
        int currentlyFocusedFileIndex = jTabbedPane.getSelectedIndex();
        if (currentlyFocusedFileIndex != -1) {
            jTabbedPane.remove(currentlyFocusedFileIndex);
            if (!watchedFiles.isEmpty()) {
                removeFile(watchedFiles.get(currentlyFocusedFileIndex));
                if (!watchedFiles.isEmpty()) {
                    File file = watchedFiles.get(jTabbedPane.getSelectedIndex());
                    absoluteFilePathLabel.setText(file.getAbsolutePath());
                }
                else {
                    absoluteFilePathLabel.setText("");
                }
            }
        }
    }

    public void removeFile(File file) {
        Thread associatedThread = fileThreadMap.get(file.getAbsolutePath());
        fileTextAreaMap.remove(file.getAbsolutePath());
        associatedThread.interrupt();

        watchedFiles.remove(file);

        updateSettings();
    }

    private void updateSettings() {
        Program.getInstance().setWatchedFiles(fileTextAreaMap.keySet());
    }
}
