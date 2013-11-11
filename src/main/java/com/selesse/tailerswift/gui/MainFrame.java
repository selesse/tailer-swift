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
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Controller for {@link MainFrameView}. Thin shell that handles all the actions that are non-trivial or not related
 * to the user interface. Mostly, this implementation is in charge of handling the {@link Thread}s for files. This
 * implementation also delegates appropriate methods to the view.
 */
public class MainFrame {
    private static Logger logger = LoggerFactory.getLogger(MainFrame.class);

    private MainFrameView mainFrameView;
    private Map<String, Thread> fileThreadMap;
    private List<File> watchedFiles;

    public MainFrame(final Settings settings) {
        mainFrameView = new MainFrameView(this);
        fileThreadMap = Maps.newHashMap();
        watchedFiles = Lists.newArrayList();
        logger.info("Main frame turn on");

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                loadSettings(settings);
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
                        startWatching(file);
                    }
                } catch (Exception e) {
                    // if we get any sort of exception, it's no big deal...
                    e.printStackTrace();
                }
            }
        };
    }

    private void loadSettings(Settings settings) {
        logger.info("Settings: Loading from settings");

        mainFrameView.getTabbedPane().setVisible(false);
        mainFrameView.setAlwaysOnTop(settings.isAlwaysOnTop());

        for (String filePath : settings.getAbsoluteFilePaths()) {
            File file = new File(filePath);
            startWatching(file);
            logger.info("Settings: Resuming watch of {}", file.getAbsolutePath());
        }

        int focusedFileIndex = settings.getFocusedFileIndex();
        if (focusedFileIndex >= 0 && focusedFileIndex < watchedFiles.size()) {
            mainFrameView.focusTabToAlreadyOpen(watchedFiles.get(focusedFileIndex));
        }
        mainFrameView.getTabbedPane().setVisible(true);

        logger.info("Settings: Focusing on tab index {}", focusedFileIndex);
        logger.info("Settings: Load complete");
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
        Program.getInstance().getSettings().setAlwaysOnTop(mainFrameView.getFrame().isAlwaysOnTop());
    }

    /**
     * Close the current tab (and terminate its file watching thread).
     * Also updates the settings
     */
    public void closeCurrentTab() {
        String focusedTabName = mainFrameView.getFocusedTabName();
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
            mainFrameView.focusTabToAlreadyOpen(file);
            return;
        }
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
}
