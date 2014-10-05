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
import com.selesse.tailerswift.gui.highlighting.HighlightThread;
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
import com.selesse.tailerswift.threads.WorkerThreads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.*;
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
    private Map<String, JTextComponent> stringTextComponentMap;
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

        stringTextComponentMap = Maps.newHashMap();
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
        doHighlights();
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

    private JTextComponent createWatcherTextComponent() {
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setDropTarget(mainFrame.createFileDropTarget());
        textPane.setFont(Program.getInstance().getSettings().getDisplayFont());
        textPane.setBorder(new EmptyBorder(0, 5, 0, 0));

        return textPane;
    }

    @SuppressWarnings("UnusedDeclaration")
    private void setLineSpacing(JTextPane textPane, float modifier) {
        MutableAttributeSet mutableAttributeSet = new SimpleAttributeSet();
        StyleConstants.setLineSpacing(mutableAttributeSet, modifier);

        textPane.setParagraphAttributes(mutableAttributeSet, false);
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
        JTextComponent textComponent = createWatcherTextComponent();

        stringTextComponentMap.put(file.getAbsolutePath(), textComponent);
        watchedFileNames.add(file.getAbsolutePath());

        JScrollPane scrollPane = new JScrollPane(textComponent);
        tabbedPane.addTab(file.getName(), scrollPane);
        tabbedPane.setSelectedComponent(scrollPane);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new SmartScroller(scrollPane));
        TextLineNumber textLineNumber = new TextLineNumber(textComponent);
        scrollPane.setRowHeaderView(textLineNumber);

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
            private StringBuilder stringBuilder = new StringBuilder();

            @Override
            public void updateFile(Path observedPath, final String modificationString) {
                // TODO fix the real issue
                if (Strings.isNullOrEmpty(modificationString)) {
                    return;
                }
                LOGGER.debug("[{}] : Updating with {} bytes of data", observedPath.toFile().getAbsolutePath(),
                        modificationString.length());
                String absolutePath = observedPath.toFile().getAbsolutePath();

                JTextComponent textComponent = stringTextComponentMap.get(absolutePath);

                if (textComponent instanceof JTextPane) {
                    JTextPane textPane = (JTextPane) textComponent;
                    final StyledDocument styledDocument = textPane.getStyledDocument();

                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    styledDocument.insertString(styledDocument.getLength(), modificationString, null);
                                } catch (BadLocationException e) {
                                    LOGGER.error("[{}] : Error inserting string into doc", chosenFile.getAbsolutePath(),
                                            e);
                                }
                            }
                        });
                    } catch (InterruptedException | InvocationTargetException e) {
                        LOGGER.error("[{}] : Error waiting for swing", chosenFile.getAbsolutePath(), e);
                    }

                }
                else {
                    stringBuilder.append(modificationString);
                    textComponent.setText(stringBuilder.toString());
                }

                doHighlight(observedPath.toFile());

                int fileIndex = watchedFileNames.indexOf(absolutePath);
                if (fileIndex != getFocusedTabIndex()) {
                    showModificationHint(fileIndex, observedPath.toFile());
                }
            }

            @Override
            public void newFile(Path observedPath, String modificationString) {
                String absolutePath = observedPath.toFile().getAbsolutePath();

                JTextComponent textComponent = stringTextComponentMap.get(absolutePath);
                stringBuilder = new StringBuilder();
                stringBuilder.append(modificationString);
                textComponent.setText(stringBuilder.toString());

                doHighlight(observedPath.toFile());
            }

            @Override
            public void deleteFile(Path observedPath) {
                String absolutePath = observedPath.toFile().getAbsolutePath();

                JTextComponent textComponent = stringTextComponentMap.get(absolutePath);
                stringBuilder = new StringBuilder();
                textComponent.setText(stringBuilder.toString());

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
        for (String filePath : stringTextComponentMap.keySet()) {
            JTextComponent textComponent = stringTextComponentMap.get(filePath);
            textComponent.setFont(font);
        }
    }

    public void addAndDoHighlight(FileSetting fileSetting) {
        fileSettings.add(fileSetting);
        doHighlight(new File(fileSetting.getAssociatedFile()));
    }

    private synchronized void doHighlight(File file) {
        if (isInitialized) {
            JTextComponent textComponent = stringTextComponentMap.get(file.getAbsolutePath());

            boolean fileShouldBeHighlighted = false;
            for (FileSetting fileSetting : fileSettings) {
                if (fileSetting.getAssociatedFile().equals(file.getAbsolutePath())) {
                    fileShouldBeHighlighted = true;
                    break;
                }
            }
            if (fileShouldBeHighlighted) {
                LOGGER.info("Starting highlight thread for {}", file.getAbsolutePath());
                try {
                    WorkerThreads.execute(new HighlightThread(textComponent, fileSettings));
                }
                catch (InterruptedException e) {
                    LOGGER.error("Error interrupting thread", e);
                }
            }
        }
    }


    private synchronized void doHighlights() {
        if (stringTextComponentMap.keySet().size() > 0) {
            LOGGER.info("Making all the watched files perform the highlights");
            for (String filePaths : stringTextComponentMap.keySet()) {
                doHighlight(new File(filePaths));
            }
        }
    }


    public SearchResults runSearchQuery(final String text) {
        final SearchResults searchResults = new SearchResults();
        for (final String filePaths : stringTextComponentMap.keySet()) {
            final JTextComponent textComponent = stringTextComponentMap.get(filePaths);

            SwingWorker worker = new SwingWorker<SearchMatches, Void>() {
                private SearchMatches searchMatches;

                @Override
                protected SearchMatches doInBackground() throws Exception {
                    SearchThread searchingThread = new SearchThread(textComponent, text);

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
        for (final String filePaths : stringTextComponentMap.keySet()) {
            final JTextComponent textComponent = stringTextComponentMap.get(filePaths);

            SwingWorker worker = new SwingWorker<FilterMatches, Void>() {
                private FilterMatches filterMatches;

                @Override
                protected FilterMatches doInBackground() throws Exception {
                    FilterThread filteringThread = new FilterThread(textComponent, text);

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
