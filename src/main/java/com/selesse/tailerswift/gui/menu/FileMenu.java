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

public class FileMenu extends AbstractMenu {
    private FileDialog fileDialog;
    private MainFrame mainFrame;

    public FileMenu(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        menu = new JMenu("File");

        fileDialog = new FileDialog(mainFrame.getJFrame());

        menu.add(createAddWatchedFileMenuItem());
        menu.add(createCloseCurrentFileMenuItem());

        // OS X handles CMD + Q automatically
        if (Program.getInstance().getOperatingSystem() != OperatingSystem.MAC) {
            JMenuItem exitMenuItem = createExitMenuItem();
            menu.add(exitMenuItem);
        }
    }

    private JMenuItem createAddWatchedFileMenuItem() {
        JMenuItem addWatchedFileMenuItem = new JMenuItem("Open/watch file...");
        addWatchedFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        addWatchedFileMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileDialog.setVisible(true);
                String chosenFileName = fileDialog.getFile();
                if (chosenFileName != null) {
                    File chosenFile = new File(fileDialog.getDirectory() + File.separator + chosenFileName);
                    mainFrame.startWatching(chosenFile);
                }
            }
        });

        return addWatchedFileMenuItem;
    }

    private JMenuItem createCloseCurrentFileMenuItem() {
        JMenuItem closeCurrentFileMenuItem = new JMenuItem("Close current file");
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
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });

        return exitMenuItem;
    }
}
