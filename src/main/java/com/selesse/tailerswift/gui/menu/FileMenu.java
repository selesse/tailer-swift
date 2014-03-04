package com.selesse.tailerswift.gui.menu;

import com.selesse.tailerswift.gui.MainFrame;
import com.selesse.tailerswift.settings.OperatingSystem;
import com.selesse.tailerswift.settings.Program;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings({"DM_EXIT"})
public class FileMenu extends AbstractMenu {
    private CrossPlatformFileChooser fileChooser;
    private MainFrame mainFrame;

    public FileMenu(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        menu = new JMenu("File");
        menu.setName("Menu");

        fileChooser = new CrossPlatformFileChooser(mainFrame.getFrame());
        fileChooser.setName("File chooser");
        fileChooser.setMultiSelectionEnabled(true);

        menu.add(createAddWatchedFileMenuItem());
        menu.add(createCloseCurrentFileMenuItem());

        // OS X already handles CMD + Q
        if (Program.getInstance().getOperatingSystem() != OperatingSystem.MAC) {
            JMenuItem exitMenuItem = createExitMenuItem();
            menu.add(exitMenuItem);
        }
    }

    private JMenuItem createAddWatchedFileMenuItem() {
        JMenuItem addWatchedFileMenuItem = new JMenuItem("Open/watch file...");
        addWatchedFileMenuItem.setName("Open/watch file...");
        addWatchedFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        addWatchedFileMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setVisible(true);
                if (fileChooser.hasSelectedFile()) {
                    for (File file : fileChooser.getSelectedFiles()) {
                        mainFrame.startWatching(file);
                    }
                }
            }
        });

        return addWatchedFileMenuItem;
    }

    private JMenuItem createCloseCurrentFileMenuItem() {
        JMenuItem closeCurrentFileMenuItem = new JMenuItem("Close current file");
        closeCurrentFileMenuItem.setName("Close current file");
        closeCurrentFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        closeCurrentFileMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.closeCurrentTab();
            }
        });
        return closeCurrentFileMenuItem;
    }

    private JMenuItem createExitMenuItem() {
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setName("Exit");
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        return exitMenuItem;
    }
}
