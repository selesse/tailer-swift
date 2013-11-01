package com.selesse.tailerswift.ui;

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
    private JFrame jFrame;
    private JTabbedPane jTabbedPane;
    private JPanel jBottomPanel;
    private JPanel jFeatureButtonPanel;
    private FeaturePanel jFeatureViewPanel;
    private Feature searchFeature;
    private Feature filterFeature;
    private JButton searchButton;
    private JButton filterButton;
    private Map<String, JTextArea> fileTextAreaMap;
    private Map<String, Thread> fileThreadMap;
    private File currentlyFocusedFile;

    public MainFrame() {
        jFrame = new JFrame();
        fileTextAreaMap = Maps.newHashMap();
        fileThreadMap = Maps.newHashMap();
        // if we setIconImage in OS X, it throws some command line errors, so let's not try this on a Mac
        if (Program.getInstance().getOperatingSystem() != OperatingSystem.MAC) {
            jFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(Resources.getResource("icon.png")));
        }

        jFrame.setDropTarget(createFileDropTarget());
    }

    // if we ever drag and drop a file into the GUI, start watching the file
    private DropTarget createFileDropTarget() {
        return new DropTarget() {

            @SuppressWarnings("unchecked")
            public synchronized void drop(DropTargetDropEvent event) {
                event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
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

        jFeatureViewPanel = new FeaturePanel();
        jFeatureButtonPanel = new JPanel();

        jBottomPanel.add(jFeatureViewPanel, BorderLayout.NORTH);
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

        //add buttons
        jFeatureButtonPanel.add(searchButton);
        jFeatureButtonPanel.add(filterButton);

        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    private void loadSettings() {
        Settings settings = Program.getInstance().getSettings();
        jFrame.setAlwaysOnTop(settings.isAlwaysOnTop());
        for (String filePath : Lists.reverse(settings.getAbsoluteFilePaths())) {
            currentlyFocusedFile = new File(filePath);
            startWatching(currentlyFocusedFile);
        }

    }

    public void addTab(String title, JTextArea textArea) {
        JScrollPane scrollPane = new JScrollPane(textArea);
        jTabbedPane.addTab(title, scrollPane);
        jTabbedPane.setSelectedComponent(scrollPane);
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

    @Override
    public void run() {
        initializeGui();
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

        addTab(chosenFile.getName(), textArea);

        Thread fileWatcherThread = new Thread(createFileWatcherFor(chosenFile));
        fileWatcherThread.start();

        fileTextAreaMap.put(chosenFile.getAbsolutePath(), textArea);
        fileThreadMap.put(chosenFile.getAbsolutePath(), fileWatcherThread);

        updateSettings();

        currentlyFocusedFile = chosenFile;
    }

    private void focusTabToAlreadyOpen(File chosenFile) {
        JTextArea textArea = fileTextAreaMap.get(chosenFile.getAbsolutePath());
        for (int i = 0; i < jTabbedPane.getTabCount(); i++) {
            Component c = jTabbedPane.getComponentAt(i);
            if (c instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) c;
                if (scrollPane.getViewport() == textArea.getParent()) {
                    jTabbedPane.setSelectedComponent(scrollPane);
                    return;
                }
            }
        }
    }

    private FileWatcher createFileWatcherFor(File chosenFile) {
        return new FileWatcher(new UserInterface() {
            private StringBuilder stringBuilder = new StringBuilder();

            @Override
            public void updateFile(Path observedFile, String modificationString) {
                JTextArea jTextArea = fileTextAreaMap.get(observedFile.toFile().getAbsolutePath());
                stringBuilder.append(modificationString);
                jTextArea.setText(stringBuilder.toString());
            }

            @Override
            public void newFile(Path observedFile, String modificationString) {
                JTextArea jTextArea = fileTextAreaMap.get(observedFile.toFile().getAbsolutePath());
                stringBuilder = new StringBuilder();
                stringBuilder.append(modificationString);
                jTextArea.setText(stringBuilder.toString());
            }

            @Override
            public void deleteFile(Path observedFile) {
                JTextArea jTextArea = fileTextAreaMap.get(observedFile.toFile().getAbsolutePath());
                stringBuilder = new StringBuilder();
                jTextArea.setText(stringBuilder.toString());
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
        Component component = jTabbedPane.getSelectedComponent();
        if (component != null) {
            jTabbedPane.remove(component);
            if (currentlyFocusedFile != null) {
                removeFile(currentlyFocusedFile);
                // TODO currentlyFocusedFile = jTabbedPane.getSelectedComponent();
            }
        }
    }

    public void removeFile(File file) {
        Thread associatedThread = fileThreadMap.get(file.getAbsolutePath());
        fileTextAreaMap.remove(file.getAbsolutePath());
        associatedThread.interrupt();

        updateSettings();
    }

    private void updateSettings() {
        Program.getInstance().setWatchedFiles(fileTextAreaMap.keySet());
    }
}
