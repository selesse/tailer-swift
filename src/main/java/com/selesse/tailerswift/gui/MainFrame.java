package com.selesse.tailerswift.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.selesse.tailerswift.UserInterface;
import com.selesse.tailerswift.filewatcher.FileWatcher;
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
import com.selesse.tailerswift.settings.Settings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MainFrame implements Runnable {
    private JFrame frame;
    private JTabbedPane tabbedPane;
    private JLabel absoluteFilePathLabel;
    private Map<String, JTextComponent> fileTextComponentMap;
    private Map<String, Thread> fileThreadMap;
    private List<File> watchedFiles;

    public MainFrame() {
        frame = new JFrame();
        fileTextComponentMap = Maps.newHashMap();
        fileThreadMap = Maps.newHashMap();
        watchedFiles = Lists.newArrayList();
        // if we setIconImage in OS X, it throws some command line errors, so let's not try this on a Mac
        if (Program.getInstance().getOperatingSystem() != OperatingSystem.MAC) {
            frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Resources.getResource("icon.png")));
        }

        frame.setDropTarget(createFileDropTarget());
    }

    @Override
    public void run() {
        initializeGui();
    }

    private void initializeGui() {
        frame.setTitle(Program.getInstance().getProgramName());
        frame.setLayout(new BorderLayout());
        frame.setBackground(null);
        frame.setJMenuBar(createMenuBar());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // add tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                int selectedIndex = tabbedPane.getSelectedIndex();
                try {
                    absoluteFilePathLabel.setText(watchedFiles.get(selectedIndex).getAbsolutePath());
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

        loadSettings();

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
        frame.setAlwaysOnTop(settings.isAlwaysOnTop());
        for (String filePath : settings.getAbsoluteFilePaths()) {
            File file = new File(filePath);
            startWatching(file);
        }
    }

    public void addTab(File file, JTextComponent textComponent) {
        JScrollPane scrollPane = new JScrollPane(textComponent);
        tabbedPane.addTab(file.getName(), scrollPane);
        tabbedPane.setSelectedComponent(scrollPane);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new SmartScroller(scrollPane));

        absoluteFilePathLabel.setText(file.getAbsolutePath());
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        FileMenu fileMenu = new FileMenu(this);
        WindowMenu windowMenu = new WindowMenu(this);
        SettingsMenu settingsMenu = new SettingsMenu();
        HelpMenu helpMenu = new HelpMenu();

        menuBar.add(fileMenu.getMenu());
        menuBar.add(settingsMenu.getMenu());
        menuBar.add(windowMenu.getMenu());
        menuBar.add(helpMenu.getMenu());

        return menuBar;
    }

    public void toggleAlwaysOnTop() {
        boolean isAlwaysOnTop = Program.getInstance().getSettings().isAlwaysOnTop();
        frame.setAlwaysOnTop(!isAlwaysOnTop);
        Program.getInstance().getSettings().setAlwaysOnTop(!isAlwaysOnTop);
    }

    public JFrame getJFrame() {
        return frame;
    }

    public void startWatching(File chosenFile) {
        if (fileTextComponentMap.containsKey(chosenFile.getAbsolutePath())) {
            focusTabToAlreadyOpen(chosenFile);
            return;
        }
        JTextComponent textComponent = createWatcherTextComponent();

        addTab(chosenFile, textComponent);

        Thread fileWatcherThread = new Thread(createFileWatcherFor(chosenFile));
        fileWatcherThread.start();

        fileTextComponentMap.put(chosenFile.getAbsolutePath(), textComponent);
        fileThreadMap.put(chosenFile.getAbsolutePath(), fileWatcherThread);
        watchedFiles.add(chosenFile);

        updateSettings();
    }

    private void focusTabToAlreadyOpen(File focusFile) {
        for (int i = 0; i < watchedFiles.size(); i++) {
            File file = watchedFiles.get(i);
            if (file.getAbsolutePath().equals(focusFile.getAbsolutePath())) {
                tabbedPane.setSelectedIndex(i);
                break;
            }
        }
    }

    private FileWatcher createFileWatcherFor(final File chosenFile) {
        return new FileWatcher(new UserInterface() {
            private StringBuilder stringBuilder = new StringBuilder();

            @Override
            public void updateFile(Path observedPath, String modificationString) {
                JTextComponent textComponent = fileTextComponentMap.get(observedPath.toFile().getAbsolutePath());
                stringBuilder.append(modificationString);
                textComponent.setText(stringBuilder.toString());

                Thread highlightingThread = new Thread(new HighlightingThread(stringBuilder, textComponent));
                highlightingThread.start();
            }

            @Override
            public void newFile(Path observedPath, String modificationString) {
                JTextComponent textComponent = fileTextComponentMap.get(observedPath.toFile().getAbsolutePath());
                stringBuilder = new StringBuilder();
                stringBuilder.append(modificationString);
                textComponent.setText(stringBuilder.toString());
            }

            @Override
            public void deleteFile(Path observedPath) {
                JTextComponent textComponent = fileTextComponentMap.get(observedPath.toFile().getAbsolutePath());
                stringBuilder = new StringBuilder();
                textComponent.setText(stringBuilder.toString());
            }
        }, chosenFile.getAbsolutePath());
    }

    private JTextComponent createWatcherTextComponent() {
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setDropTarget(createFileDropTarget());

        return textPane;
    }

    public Collection<Thread> getAllThreads() {
        return fileThreadMap.values();
    }

    public void closeCurrentTab() {
        int currentlyFocusedFileIndex = tabbedPane.getSelectedIndex();
        if (currentlyFocusedFileIndex != -1) {
            tabbedPane.remove(currentlyFocusedFileIndex);
            if (!watchedFiles.isEmpty()) {
                removeFile(watchedFiles.get(currentlyFocusedFileIndex));
                if (!watchedFiles.isEmpty()) {
                    File file = watchedFiles.get(tabbedPane.getSelectedIndex());
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
        fileTextComponentMap.remove(file.getAbsolutePath());
        associatedThread.interrupt();

        watchedFiles.remove(file);

        updateSettings();
    }

    private void updateSettings() {
        Program.getInstance().setWatchedFiles(fileTextComponentMap.keySet());
    }
}
