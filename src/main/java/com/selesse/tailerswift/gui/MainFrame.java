package com.selesse.tailerswift.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.selesse.tailerswift.gui.filter.FilterResults;
import com.selesse.tailerswift.gui.highlighting.FileSetting;
import com.selesse.tailerswift.gui.search.SearchResults;
import com.selesse.tailerswift.gui.view.MainFrameView;
import com.selesse.tailerswift.settings.Program;
import com.selesse.tailerswift.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Controller for {@link MainFrameView}. Thin shell that handles all the actions that are non-trivial or not related
 * to the user interface. Mostly, this implementation is in charge of handling the {@link Thread}s for files. This
 * implementation also delegates appropriate methods to the view.
 */
public class MainFrame {
    private static Logger LOGGER = LoggerFactory.getLogger(MainFrame.class);

    private MainFrameView mainFrameView;
    private Map<String, Thread> fileThreadMap;
    private List<File> watchedFiles;

    public MainFrame(final Settings settings) {
        mainFrameView = new MainFrameView(this);
        fileThreadMap = Maps.newHashMap();
        watchedFiles = Lists.newArrayList();
        LOGGER.info("MainFrame turn on");

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                loadUISettings(settings);
                mainFrameView.initializeGui();
            }
        });
    }

    // Allows people to drag files from a file explorer into the program
    public DropTarget createFileDropTarget() {
        return new DropTarget() {

            @SuppressWarnings("unchecked")
            public synchronized void drop(DropTargetDropEvent event) {
                event.acceptDrop(DnDConstants.ACTION_LINK);
                try {
                    List<File> droppedFiles = (List<File>)
                            event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File file : droppedFiles) {
                        LOGGER.info("[{}] : Just got drag & dropped into the app", file.getAbsolutePath());
                        startWatching(file);
                    }
                } catch (UnsupportedFlavorException | IOException e) {
                    LOGGER.error("Exception thrown while files were dropped", e);
                }
            }
        };
    }

    private void loadUISettings(Settings settings) {
        LOGGER.debug("Settings: Loading from settings");

        mainFrameView.getTabbedPane().setVisible(false);
        mainFrameView.setAlwaysOnTop(settings.isAlwaysOnTop());

        for (String filePath : settings.getAbsoluteFilePaths()) {
            File file = new File(filePath);
            startWatching(file);
            LOGGER.info("Settings: Resuming watch of [{}]", file.getAbsolutePath());
        }

        int focusedFileIndex = settings.getFocusedFileIndex();
        if (focusedFileIndex >= 0 && focusedFileIndex < watchedFiles.size()) {
            mainFrameView.focusTabToAlreadyOpen(watchedFiles.get(focusedFileIndex));
        }
        else {
            // There are no watched files, tell the user to drag and drop a file!
            // TODO
        }
        mainFrameView.getTabbedPane().setVisible(true);

        if (focusedFileIndex != -1) {
            LOGGER.info("Settings: Focusing on tab index {}", focusedFileIndex);
        }
        LOGGER.debug("Settings: Load complete");
    }

    public Collection<Thread> getAllThreads() {
        return fileThreadMap.values();
    }

    private void updateSettings() {
        Program.getInstance().setWatchedFiles(watchedFiles);
    }

    public Frame getFrame() {
        return mainFrameView.getFrame();
    }

    public void toggleAlwaysOnTop() {
        mainFrameView.toggleAlwaysOnTop();
        LOGGER.debug("Setting always on top to {}", mainFrameView.isAlwaysOnTop());
        Program.getInstance().getSettings().setAlwaysOnTop(mainFrameView.isAlwaysOnTop());
    }

    /**
     * Close the current tab (and terminate its file watching thread).
     * Also updates the settings
     */
    public void closeCurrentTab() {
        String focusedTabName = mainFrameView.getFocusedTabName();
        LOGGER.info("[{}] : Closing file", focusedTabName);
        if (!Strings.isNullOrEmpty(focusedTabName)) {
            File file = new File(focusedTabName);
            Thread associatedThread = fileThreadMap.get(file.getAbsolutePath());
            associatedThread.interrupt();

            watchedFiles.remove(file);

            updateSettings();

            mainFrameView.closeCurrentTab();
        }
    }

    public synchronized void startWatching(File file) {
        if (watchedFiles.contains(file)) {
            LOGGER.info("[{}] : Focusing, it's already open", file.getAbsolutePath());
            mainFrameView.focusTabToAlreadyOpen(file);
            return;
        }
        LOGGER.info("[{}] : Starting to watch", file.getAbsolutePath());
        // create a new tab in the view for this file
        mainFrameView.addTab(file);

        // initialize and start a thread to watch the file, add it to our thread map
        Thread fileWatcherThread = new Thread(mainFrameView.createFileWatcherFor(file));
        fileWatcherThread.start();
        fileThreadMap.put(file.getAbsolutePath(), fileWatcherThread);

        // master list of watched files
        watchedFiles.add(file);

        // add this new file to our settings
        updateSettings();
    }

    public void setFont(Font font) {
        LOGGER.info("Setting font to " + font);
        Program.getInstance().getSettings().setDisplayFont(font);
        mainFrameView.setFont(font);
    }

    public void addHighlight(FileSetting fileSetting) {
        mainFrameView.addAndDoHighlight(fileSetting);
    }

    public SearchResults searchFor(String text) {
        return mainFrameView.runSearchQuery(text);
    }

    public FilterResults filter(String text) {
        return mainFrameView.filter(text);
    }

    public String getFocusedFile() {
        return watchedFiles.get(mainFrameView.getFocusedTabIndex()).getAbsolutePath();
    }
}
