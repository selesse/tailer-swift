package com.selesse.tailerswift.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.selesse.tailerswift.UserInterface;
import com.selesse.tailerswift.filewatcher.FileWatcher;
import com.selesse.tailerswift.gui.highlighting.HighlightingThread;
import com.selesse.tailerswift.gui.view.MainFrameView;
import com.selesse.tailerswift.settings.Program;
import com.selesse.tailerswift.settings.Settings;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
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
        mainFrameView.addTabbedPaneChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                int selectedIndex = mainFrameView.getTabbedPane().getSelectedIndex();
                try {
                    mainFrameView.getFilePathLabel().setText(watchedFiles.get(selectedIndex).getAbsolutePath());
                }
                // this will only be an issue on startup
                catch (IndexOutOfBoundsException e) {
                    mainFrameView.getFilePathLabel().setText("");
                }
            }
        });

        loadSettings();
    }

    public DropTarget createFileDropTarget() {
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

    public void startWatching(File chosenFile) {
        if (watchedFiles.contains(chosenFile)) {
            mainFrameView.focusTabToAlreadyOpen(chosenFile);
            return;
        }
        // add it to the view
        mainFrameView.addTab(chosenFile);

        // create a thread to watch the file, add it to our map
        Thread fileWatcherThread = new Thread(createFileWatcherFor(chosenFile));
        fileWatcherThread.start();
        fileThreadMap.put(chosenFile.getAbsolutePath(), fileWatcherThread);

        // master list of watched files
        watchedFiles.add(chosenFile);

        // add this new file to our settings
        updateSettings();
    }

    private FileWatcher createFileWatcherFor(final File chosenFile) {
        return new FileWatcher(new UserInterface() {
            private StringBuilder stringBuilder = new StringBuilder();

            @Override
            public void updateFile(Path observedPath, String modificationString) {
                String absolutePath = observedPath.toFile().getAbsolutePath();

                JTextComponent textComponent = mainFrameView.getTextComponentFor(absolutePath);
                stringBuilder.append(modificationString);
                textComponent.setText(stringBuilder.toString());

                Thread highlightingThread = new Thread(new HighlightingThread(stringBuilder, textComponent));
                highlightingThread.start();
            }

            @Override
            public void newFile(Path observedPath, String modificationString) {
                String absolutePath = observedPath.toFile().getAbsolutePath();

                JTextComponent textComponent = mainFrameView.getTextComponentFor(absolutePath);
                stringBuilder = new StringBuilder();
                stringBuilder.append(modificationString);
                textComponent.setText(stringBuilder.toString());
            }

            @Override
            public void deleteFile(Path observedPath) {
                String absolutePath = observedPath.toFile().getAbsolutePath();

                JTextComponent textComponent = mainFrameView.getTextComponentFor(absolutePath);
                stringBuilder = new StringBuilder();
                textComponent.setText(stringBuilder.toString());
            }
        }, chosenFile.getAbsolutePath());
    }

    public Collection<Thread> getAllThreads() {
        return fileThreadMap.values();
    }

    private void updateSettings() {
        Program.getInstance().setWatchedFiles(watchedFiles);
    }

    public MainFrameView getView() {
        return mainFrameView;
    }

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
}
