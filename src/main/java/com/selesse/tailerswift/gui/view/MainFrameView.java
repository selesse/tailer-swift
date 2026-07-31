package com.selesse.tailerswift.gui.view;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.selesse.tailerswift.TailUserInterface;
import com.selesse.tailerswift.filewatcher.FileWatcher;
import com.selesse.tailerswift.gui.MainFrame;
import com.selesse.tailerswift.gui.SmartScroller;
import com.selesse.tailerswift.gui.filter.Filter;
import com.selesse.tailerswift.gui.filter.FilterMatches;
import com.selesse.tailerswift.gui.filter.FilterResults;
import com.selesse.tailerswift.gui.filter.FilterThread;
import com.selesse.tailerswift.gui.highlighting.Colors;
import com.selesse.tailerswift.gui.highlighting.FileSetting;
import com.selesse.tailerswift.gui.highlighting.Highlight;
import com.selesse.tailerswift.gui.lines.TailPane;
import com.selesse.tailerswift.gui.menu.FileMenu;
import com.selesse.tailerswift.gui.menu.HelpMenu;
import com.selesse.tailerswift.gui.menu.SettingsMenu;
import com.selesse.tailerswift.gui.menu.WindowMenu;
import com.selesse.tailerswift.gui.search.Search;
import com.selesse.tailerswift.gui.search.SearchMatches;
import com.selesse.tailerswift.gui.search.SearchResults;
import com.selesse.tailerswift.gui.search.SearchThread;
import com.selesse.tailerswift.gui.section.ButtonActionListener;
import com.selesse.tailerswift.gui.section.Feature;
import com.selesse.tailerswift.gui.section.FeaturePanel;
import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * All the nitty-gritty GUI-related functions for the {@link MainFrame}.
 */
public class MainFrameView {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainFrameView.class);

    private JFrame frame;
    private JTabbedPane tabbedPane;
    private JLabel absoluteFilePathLabel;
    private Map<String, TailPane> tailPaneMap;
    private List<String> watchedFileNames;
    private MainFrame mainFrame;
    private List<FileSetting> fileSettings;
    private boolean isInitialized = false;

    public MainFrameView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        // initialize UI components, but do not make them visible yet
        frame = new JFrame();
        tabbedPane = new JTabbedPane();
        tabbedPane.setName("Tabbed pane");
        absoluteFilePathLabel = new JLabel();

        tailPaneMap = Maps.newHashMap();
        watchedFileNames = Lists.newArrayList();
        fileSettings = Lists.newArrayList();

        // Apple has incompatible UI-specific customizations: "About", and icon handling
        if (Program.getInstance().getOperatingSystem() == OperatingSystem.MAC) {
            doAppleSpecificUiCustomizations();
        }
        else {
            frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Resources.getResource("icon.png")));
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Program program = Program.getInstance();
                if (!program.getSettings().isTest()) {
                    program.getSettings().setFocusedFileIndex(getFocusedTabIndex());
                    program.saveSettings();
                }
            }
        });
    }

    public void initializeGui() {
        frame.setTitle(Program.getInstance().getProgramName());
        frame.setLayout(new BorderLayout());
        frame.setBackground(null);
        frame.setJMenuBar(createMenuBar());
        // TODO do something better than this
        frame.setMinimumSize(new Dimension(400, 400));
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDropTarget(mainFrame.createFileDropTarget());

        // add tabbed pane
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                int selectedIndex = tabbedPane.getSelectedIndex();
                if (selectedIndex != -1) {
                    absoluteFilePathLabel.setText(watchedFileNames.get(selectedIndex));
                    TailPane tailPane = tailPaneMap.get(watchedFileNames.get(selectedIndex));
                    if (tailPane != null) {
                        tailPane.requestScrollFocus();
                    }
                }
                normalizeTabTitle();
            }
        });
        frame.add(tabbedPane, BorderLayout.CENTER);

        // add bottom panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        FeaturePanel featurePanel = new FeaturePanel();
        JPanel featureButtonPanel = new JPanel();

        absoluteFilePathLabel.setForeground(Colors.DARK_GREEN.toColor());
        // pad a bit on the right so it looks prettier
        absoluteFilePathLabel.setBorder(new EmptyBorder(0, 0, 0, 10));

        bottomPanel.add(featurePanel, BorderLayout.NORTH);
        bottomPanel.add(absoluteFilePathLabel, BorderLayout.EAST);
        bottomPanel.add(featureButtonPanel, BorderLayout.SOUTH);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        // add features
        Feature searchFeature = new Feature(new Search(mainFrame));
        Feature filterFeature = new Feature(new Filter(mainFrame));
        Feature highlightFeature = new Feature(new Highlight(mainFrame));

        // create buttons
        JButton searchButton = new JButton("Search");
        JButton filterButton = new JButton("Filter");
        JButton highlightButton = new JButton("Highlight");
        searchButton.setName("Search");
        filterButton.setName("Filter");
        highlightButton.setName("Highlight");

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

        isInitialized = true;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        FileMenu fileMenu = new FileMenu(mainFrame);
        WindowMenu windowMenu = new WindowMenu(mainFrame);
        SettingsMenu settingsMenu = new SettingsMenu(mainFrame);
        HelpMenu helpMenu = new HelpMenu();

        menuBar.add(fileMenu.getMenu());
        menuBar.add(settingsMenu.getMenu());
        menuBar.add(windowMenu.getMenu());
        menuBar.add(helpMenu.getMenu());

        return menuBar;
    }

    public Frame getFrame() {
        return frame;
    }

    public void toggleAlwaysOnTop() {
        frame.setAlwaysOnTop(!frame.isAlwaysOnTop());
    }

    public void setAlwaysOnTop(boolean alwaysOnTop) {
        frame.setAlwaysOnTop(alwaysOnTop);
    }

    public synchronized void addTab(File file) {
        TailPane tailPane = new TailPane(fileSettings, Program.getInstance().getSettings().getDisplayFont(),
                mainFrame.createFileDropTarget());

        tailPaneMap.put(file.getAbsolutePath(), tailPane);
        watchedFileNames.add(file.getAbsolutePath());

        tabbedPane.addTab(file.getName(), tailPane.getComponent());
        tabbedPane.setSelectedComponent(tailPane.getComponent());
        tailPane.requestScrollFocus();

        absoluteFilePathLabel.setText(file.getAbsolutePath());
    }

    public void closeCurrentTab() {
        int currentlyFocusedFileIndex = tabbedPane.getSelectedIndex();
        if (currentlyFocusedFileIndex != -1) {
            tabbedPane.remove(currentlyFocusedFileIndex);
            String removeName = watchedFileNames.remove(currentlyFocusedFileIndex);
            tailPaneMap.remove(removeName);
            if (watchedFileNames.isEmpty()) {
                absoluteFilePathLabel.setText("");
            }
            else {
                normalizeTabTitle();
            }
        }
    }

    public void focusTabToAlreadyOpen(File chosenFile) {
        int fileIndex = watchedFileNames.indexOf(chosenFile.getAbsolutePath());
        if (fileIndex != -1) {
            tabbedPane.setSelectedIndex(fileIndex);
        }
        else {
            LOGGER.error("[{}] : Error, got -1 file index", chosenFile.getAbsolutePath());
        }
    }

    public String getFocusedTabName() {
        if (watchedFileNames.isEmpty()) {
            return "";
        }
        return watchedFileNames.get(tabbedPane.getSelectedIndex());
    }

    public FileWatcher createFileWatcher(final File chosenFile) {
        return new FileWatcher(new TailUserInterface() {
            @Override
            public void updateFile(Path observedPath, final String modificationString) {
                // TODO fix the real issue
                if (Strings.isNullOrEmpty(modificationString)) {
                    return;
                }
                LOGGER.debug("[{}] : Updating with {} bytes of data", observedPath.toFile().getAbsolutePath(),
                        modificationString.length());
                String absolutePath = observedPath.toFile().getAbsolutePath();

                tailPaneMap.get(absolutePath).appendChunk(modificationString);

                int fileIndex = watchedFileNames.indexOf(absolutePath);
                if (fileIndex != getFocusedTabIndex()) {
                    showModificationHint(fileIndex, observedPath.toFile());
                }
            }

            @Override
            public void newFile(Path observedPath, String modificationString) {
                String absolutePath = observedPath.toFile().getAbsolutePath();

                tailPaneMap.get(absolutePath).setText(modificationString);
            }

            @Override
            public void deleteFile(Path observedPath) {
                String absolutePath = observedPath.toFile().getAbsolutePath();

                tailPaneMap.get(absolutePath).setText("");

                String absoluteFilePathLabelText = absoluteFilePathLabel.getText();
                absoluteFilePathLabel.setText(absoluteFilePathLabelText);
                absoluteFilePathLabel.setForeground(Colors.DARK_RED.toColor());
            }
        }, chosenFile.getAbsolutePath());
    }

    /**
     * Make sure that if the tab title isn't what it's supposed to be, reset it.
     */
    private void normalizeTabTitle() {
        int index = tabbedPane.getSelectedIndex();
        if (index >= 0) {
            File file = new File(watchedFileNames.get(index));

            String currentTitle = tabbedPane.getTitleAt(index);
            String expectedTitle = file.getName();

            if (!currentTitle.equals(expectedTitle)) {
                tabbedPane.setTitleAt(index, expectedTitle);
            }
        }
    }

    private void showModificationHint(int index, File name) {
        if (isInitialized) {
            LOGGER.info("[{}] : Showing modification hint", name.getAbsolutePath());
            tabbedPane.setTitleAt(index, "* " + name.getName());
        }
    }

    public void setFont(Font font) {
        for (TailPane tailPane : tailPaneMap.values()) {
            tailPane.setFont(font);
        }
    }

    /**
     * Highlighting is resolved per-line at paint time (see {@link com.selesse.tailerswift.gui.lines.LineHighlighter}),
     * so adding a setting just needs to make it visible to future resolutions and repaint what's on screen now.
     */
    public void addAndDoHighlight(FileSetting fileSetting) {
        fileSettings.add(fileSetting);
        for (TailPane tailPane : tailPaneMap.values()) {
            tailPane.repaintHighlights();
        }
    }

    public SearchResults runSearchQuery(final String text) {
        final SearchResults searchResults = new SearchResults();
        for (final String filePaths : tailPaneMap.keySet()) {
            final TailPane tailPane = tailPaneMap.get(filePaths);

            SwingWorker worker = new SwingWorker<SearchMatches, Void>() {
                private SearchMatches searchMatches;

                @Override
                protected SearchMatches doInBackground() throws Exception {
                    SearchThread searchingThread = new SearchThread(tailPane::getText, text);

                    Thread searchThread = new Thread(searchingThread);
                    searchThread.start();
                    searchThread.join();

                    searchMatches = searchingThread.getResults();

                    return searchMatches;
                }

                @Override
                public void done() {
                    searchResults.addMatch(filePaths, searchMatches);
                }
            };
            worker.run();
        }

        return searchResults;
    }

    public int getFocusedTabIndex() {
        return tabbedPane.getSelectedIndex();
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public FilterResults filter(final String text) {
        final FilterResults filterResults = new FilterResults();
        for (final String filePaths : tailPaneMap.keySet()) {
            final TailPane tailPane = tailPaneMap.get(filePaths);

            SwingWorker worker = new SwingWorker<FilterMatches, Void>() {
                private FilterMatches filterMatches;

                @Override
                protected FilterMatches doInBackground() throws Exception {
                    FilterThread filteringThread = new FilterThread(tailPane::getText, text);

                    Thread filterThread = new Thread(filteringThread);
                    filterThread.start();
                    filterThread.join();

                    filterMatches = filteringThread.getResults();

                    return filterMatches;
                }

                @Override
                public void done() {
                    filterResults.addMatch(filePaths, filterMatches);
                }
            };
            worker.run();
        }

        return filterResults;
    }

    public boolean isAlwaysOnTop() {
        return frame.isAlwaysOnTop();
    }

    /**
     * Set the AboutHandler and dock image.
     */
    private void doAppleSpecificUiCustomizations() {
        // Use reflection because we want to compile under 1 source. :(
        Image image = Toolkit.getDefaultToolkit().createImage(Resources.getResource("icon.png"));

        // The following code uses reflection to perform the following equivalent code:
        //
        // Application application = com.apple.eawt.Application.getApplication();
        // application.setDockImage(image);
        // application.setAboutHandler(new AboutHandler());
        try {
            Class appleApplicationClass = Class.forName("com.apple.eawt.Application");
            @SuppressWarnings("unchecked") Method method = appleApplicationClass.getMethod("getApplication");
            Object applicationInstance = method.invoke(null);
            method = applicationInstance.getClass().getMethod("setDockIconImage", java.awt.Image.class);

            // Application application = com.apple.eawt.Application.getApplication();
            // application.setDockImage(image);
            method.invoke(applicationInstance, image);

            // application.setAboutHandler(new AboutHandler());
            Class<?> aboutHandlerClass = Class.forName("com.apple.eawt.AboutHandler");
            Object proxyInstance = Proxy.newProxyInstance(aboutHandlerClass.getClassLoader(),
                    new Class[]{aboutHandlerClass}, new AboutListener());
            applicationInstance.getClass().getMethod("setAboutHandler",
                    new Class[]{aboutHandlerClass}).invoke(applicationInstance, proxyInstance);
        } catch (ReflectiveOperationException e) {
            LOGGER.error("UI reflection failed for OS X", e);
        }

    }

    private static class AboutListener implements InvocationHandler {
        private JFrame aboutFrame;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (aboutFrame == null) {
                aboutFrame = new AboutFrame();
            }
            aboutFrame.setVisible(true);

            return null;
        }
    }
}
