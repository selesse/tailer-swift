package com.selesse.tailerswift.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.selesse.tailerswift.gui.highlighting.FileSetting;
import com.selesse.tailerswift.gui.view.MainFrameView;
import com.selesse.tailerswift.settings.Program;
import com.selesse.tailerswift.settings.Settings;

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
public class MainFrame implements Runnable {
    private MainFrameView mainFrameView;
    private Map<String, Thread> fileThreadMap;
    private List<File> watchedFiles;

    public MainFrame() {
        mainFrameView = new MainFrameView(this);
        fileThreadMap = Maps.newHashMap();
        watchedFiles = Lists.newArrayList();
    }

    @Override
    public void run() {
        mainFrameView.initializeGui();

        loadSettings();
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

    private void loadSettings() {
        Settings settings = Program.getInstance().getSettings();
        mainFrameView.setAlwaysOnTop(settings.isAlwaysOnTop());
        for (String filePath : settings.getAbsoluteFilePaths()) {
            File file = new File(filePath);
            startWatching(file);
        }
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
        if (focusedTabName != null) {
            File file = new File(focusedTabName);
            Thread associatedThread = fileThreadMap.get(file.getAbsolutePath());
            associatedThread.interrupt();

            watchedFiles.remove(file);

            updateSettings();

            mainFrameView.closeCurrentTab();
        }
    }

    public void startWatching(File chosenFile) {
        if (watchedFiles.contains(chosenFile)) {
            mainFrameView.focusTabToAlreadyOpen(chosenFile);
            return;
        }
        // create a new tab in the view for this file
        mainFrameView.addTab(chosenFile);

        // initialize and start a thread to watch the file, add it to our thread map
        Thread fileWatcherThread = new Thread(mainFrameView.createFileWatcherFor(chosenFile));
        fileWatcherThread.start();
        fileThreadMap.put(chosenFile.getAbsolutePath(), fileWatcherThread);

        // master list of watched files
        watchedFiles.add(chosenFile);

        // add this new file to our settings
        updateSettings();
    }

    public void setFont(Font font) {
        Program.getInstance().getSettings().setFont(font);
        mainFrameView.setFont(font);
    }

    public void addHighlight(FileSetting fileSetting) {
        mainFrameView.addHighlight(fileSetting);
    }
}
